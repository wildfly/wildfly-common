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

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 *
 * @deprecated Use {@link io.smallrye.common.cpu.CacheLevelInfo} instead.
 */
@Deprecated(forRemoval = true)
public final class CacheLevelInfo {
    private final io.smallrye.common.cpu.CacheLevelInfo info;

    CacheLevelInfo(final io.smallrye.common.cpu.CacheLevelInfo info) {
        this.info = info;
    }

    /**
     * Get the level index.  For example, the level of L1 cache will be "1", L2 will be "2", etc.  If the level is
     * not known, 0 is returned.
     *
     * @return the level index, or 0 if unknown
     */
    public int getCacheLevel() {
        return info.getCacheLevel();
    }

    /**
     * Get the type of cache.  If the type is unknown, {@link CacheType#UNKNOWN} is returned.
     *
     * @return the type of cache (not {@code null})
     */
    public CacheType getCacheType() {
        return CacheType.of(info.getCacheType());
    }

    /**
     * Get the size of this cache level in kilobytes.  If the size is unknown, 0 is returned.
     *
     * @return the size of this cache level in kilobytes, or 0 if unknown
     */
    public int getCacheLevelSizeKB() {
        return info.getCacheLevelSizeKB();
    }

    /**
     * Get the cache line size in bytes.  If the size is unknown, 0 is returned.
     *
     * @return the cache line size in bytes, or 0 if unknown
     */
    public int getCacheLineSize() {
        return info.getCacheLineSize();
    }
}
