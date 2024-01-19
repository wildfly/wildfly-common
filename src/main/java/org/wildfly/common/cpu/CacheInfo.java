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

import java.util.stream.IntStream;

/**
 * A class which exposes any available cache line information for the current CPU.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 * @deprecated Use {@link io.smallrye.common.cpu.CacheInfo} instead.
 */
@Deprecated(forRemoval = true)
public final class CacheInfo {
    private static final CacheLevelInfo[] cacheLevels;

    /**
     * Get the number of CPU cache level entries.  If no cache information could be gathered, 0 is returned.
     *
     * @return the number of CPU cache levels, or 0 if unknown
     * @deprecated Use {@link io.smallrye.common.cpu.CacheInfo#getLevelEntryCount()} instead.
     */
    @Deprecated(forRemoval = true)
    public static int getLevelEntryCount() {
        return io.smallrye.common.cpu.CacheInfo.getLevelEntryCount();
    }

    /**
     * Get the CPU cache level information for a cache level.  The {@code index} argument must be greater than zero and
     * less than the number of levels returned by {@link #getLevelEntryCount()}.
     *
     * @param index the cache level index
     * @return the CPU cache level information
     * @deprecated Use {@link io.smallrye.common.cpu.CacheInfo#getCacheLevelInfo(int)} instead.
     */
    @Deprecated(forRemoval = true)
    public static CacheLevelInfo getCacheLevelInfo(int index) {
        return cacheLevels[index];
    }

    /**
     * Get the smallest known data cache line size.  If no cache line sizes are known, 0 is returned.  Note that smaller
     * cache lines may exist if one or more cache line sizes are unknown.
     *
     * @return the smallest cache line size, or 0 if unknown
     * @deprecated Use {@link io.smallrye.common.cpu.CacheInfo#getSmallestDataCacheLineSize()} instead.
     */
    @Deprecated(forRemoval = true)
    public static int getSmallestDataCacheLineSize() {
        return io.smallrye.common.cpu.CacheInfo.getSmallestDataCacheLineSize();
    }

    /**
     * Get the smallest known instruction cache line size.  If no cache line sizes are known, 0 is returned.  Note that smaller
     * cache lines may exist if one or more cache line sizes are unknown.
     *
     * @return the smallest cache line size, or 0 if unknown
     * @deprecated Use {@link io.smallrye.common.cpu.CacheInfo#getSmallestInstructionCacheLineSize()} instead.
     */
    @Deprecated(forRemoval = true)
    public static int getSmallestInstructionCacheLineSize() {
        return io.smallrye.common.cpu.CacheInfo.getSmallestInstructionCacheLineSize();
    }

    static {
        cacheLevels = IntStream.range(0, io.smallrye.common.cpu.CacheInfo.getLevelEntryCount()).mapToObj(io.smallrye.common.cpu.CacheInfo::getCacheLevelInfo).map(CacheLevelInfo::new).toArray(CacheLevelInfo[]::new);
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