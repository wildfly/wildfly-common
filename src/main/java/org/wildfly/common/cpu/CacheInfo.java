/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2015 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wildfly.common.cpu;

import static java.security.AccessController.doPrivileged;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Locale;

/**
 * A class which exposes any available cache line information for the current CPU.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class CacheInfo {
    private static final CacheLevelInfo[] cacheLevels;

    /**
     * Get the number of CPU cache level entries.  If no cache information could be gathered, 0 is returned.
     *
     * @return the number of CPU cache levels, or 0 if unknown
     */
    public static int getLevelEntryCount() {
        return cacheLevels.length;
    }

    /**
     * Get the CPU cache level information for a cache level.  The {@code index} argument must be greater than zero and
     * less than the number of levels returned by {@link #getLevelEntryCount()}.
     *
     * @param index the cache level index
     * @return the CPU cache level information
     */
    public static CacheLevelInfo getCacheLevelInfo(int index) {
        return cacheLevels[index];
    }

    /**
     * Get the smallest known data cache line size.  If no cache line sizes are known, 0 is returned.  Note that smaller
     * cache lines may exist if one or more cache line sizes are unknown.
     *
     * @return the smallest cache line size, or 0 if unknown
     */
    public static int getSmallestDataCacheLineSize() {
        int minSize = Integer.MAX_VALUE;
        for (CacheLevelInfo cacheLevel : cacheLevels) {
            if (cacheLevel.getCacheType().isData()) {
                final int cacheLineSize = cacheLevel.getCacheLineSize();
                if (cacheLineSize != 0 && cacheLineSize < minSize) {
                    minSize = cacheLineSize;
                }
            }
        }
        return minSize == Integer.MAX_VALUE ? 0 : minSize;
    }

    /**
     * Get the smallest known instruction cache line size.  If no cache line sizes are known, 0 is returned.  Note that smaller
     * cache lines may exist if one or more cache line sizes are unknown.
     *
     * @return the smallest cache line size, or 0 if unknown
     */
    public static int getSmallestInstructionCacheLineSize() {
        int minSize = Integer.MAX_VALUE;
        for (CacheLevelInfo cacheLevel : cacheLevels) {
            if (cacheLevel.getCacheType().isInstruction()) {
                final int cacheLineSize = cacheLevel.getCacheLineSize();
                if (cacheLineSize != 0 && cacheLineSize < minSize) {
                    minSize = cacheLineSize;
                }
            }
        }
        return minSize == Integer.MAX_VALUE ? 0 : minSize;
    }

    static {
        cacheLevels = doPrivileged(new PrivilegedAction<CacheLevelInfo[]>() {
            public CacheLevelInfo[] run() {
                try {
                    String osArch = System.getProperty("os.name", "unknown").toLowerCase(Locale.US);
                    if (osArch.contains("linux")) {
                        // try to read /sys fs
                        final File cpu0 = new File("/sys/devices/system/cpu/cpu0/cache");
                        if (cpu0.exists()) {
                            // great!
                            final File[] files = cpu0.listFiles();
                            if (files != null) {
                                ArrayList<File> indexes = new ArrayList<File>();
                                for (File file : files) {
                                    if (file.getName().startsWith("index")) {
                                        indexes.add(file);
                                    }
                                }
                                final CacheLevelInfo[] levelInfoArray = new CacheLevelInfo[indexes.size()];
                                for (int i = 0; i < indexes.size(); i++) {
                                    File file = indexes.get(i);
                                    int index = parseIntFile(new File(file, "level"));
                                    final CacheType type;
                                    switch (parseStringFile(new File(file, "type"))) {
                                        case "Data": type = CacheType.DATA; break;
                                        case "Instruction": type = CacheType.INSTRUCTION; break;
                                        case "Unified": type = CacheType.UNIFIED; break;
                                        default: type = CacheType.UNKNOWN; break;
                                    }
                                    int size = parseIntKBFile(new File(file, "size"));
                                    int lineSize = parseIntFile(new File(file, "coherency_line_size"));
                                    levelInfoArray[i] = new CacheLevelInfo(index, type, size, lineSize);
                                }
                                return levelInfoArray;
                            }
                        }
                    } else if (osArch.contains("macosx")) {
                        // TODO: use the following sysctls:
                        //  sysctl hw.cachelinesize
                        //  sysctl hw.l1dcachesize
                        //  sysctl hw.l1icachesize
                        //  sysctl hw.l2cachesize
                        //  sysctl hw.l3cachesize
                    } else if (osArch.contains("windows")) {
                        // TODO: use the wmic utility to get cache line info
                    }
                } catch (Throwable ignored) {}
                // all has failed
                return new CacheLevelInfo[0];
            }
        });
    }

    static int parseIntFile(final File file) {
        try {
            return Integer.parseInt(parseStringFile(file));
        } catch (Throwable ignored) {
            return 0;
        }
    }

    static int parseIntKBFile(final File file) {
        try {
            final String s = parseStringFile(file);
            if (s.endsWith("K")) {
                return Integer.parseInt(s.substring(0, s.length() - 1));
            } else if (s.endsWith("M")) {
                return Integer.parseInt(s.substring(0, s.length() - 1)) * 1024;
            } else if (s.endsWith("G")) {
                return Integer.parseInt(s.substring(0, s.length() - 1)) * 1024 * 1024;
            } else {
                return Integer.parseInt(s);
            }
        } catch (Throwable ignored) {
            return 0;
        }
    }

    static String parseStringFile(final File file) {
        try (FileInputStream is = new FileInputStream(file)) {
            try (Reader r = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                StringBuilder b = new StringBuilder();
                char[] cb = new char[64];
                int res;
                while ((res = r.read(cb)) != -1) {
                    b.append(cb, 0, res);
                }
                return b.toString().trim();
            }
        } catch (Throwable ignored) {
            return "";
        }
    }

    public static void main(String[] args) {
        System.out.println("Detected cache info:");
        for (CacheLevelInfo levelInfo : cacheLevels) {
            System.out.printf("Level %d cache: type %s, size %d KiB, cache line is %d bytes%n",
                Integer.valueOf(levelInfo.getCacheLevel()),
                levelInfo.getCacheType(),
                Integer.valueOf(levelInfo.getCacheLevelSizeKB()),
                Integer.valueOf(levelInfo.getCacheLineSize())
            );
        }
    }
}