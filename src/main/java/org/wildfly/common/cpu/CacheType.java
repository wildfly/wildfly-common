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

import java.util.EnumSet;

/**
 * The type of cache.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public enum CacheType {
    /**
     * Unknown cache type.
     */
    UNKNOWN(false, false),
    /**
     * Data cache.
     */
    DATA(false, true),
    /**
     * Instruction cache.
     */
    INSTRUCTION(true, false),
    /**
     * Unified instruction/data cache.
     */
    UNIFIED(true, true),
    ;

    private static final int fullSize = values().length;
    private final boolean instruction;
    private final boolean data;

    CacheType(final boolean instruction, final boolean data) {
        this.instruction = instruction;
        this.data = data;
    }

    /**
     * Determine if this cache line type holds instructions.
     *
     * @return {@code true} if the cache line holds instructions, {@code false} if it does not or it cannot be determined
     */
    public boolean isInstruction() {
        return instruction;
    }

    /**
     * Determine if this cache line type holds data.
     *
     * @return {@code true} if the cache line holds data, {@code false} if it does not or it cannot be determined
     */
    public boolean isData() {
        return data;
    }

    /**
     * Determine whether the given set is fully populated (or "full"), meaning it contains all possible values.
     *
     * @param set the set
     *
     * @return {@code true} if the set is full, {@code false} otherwise
     */
    public static boolean isFull(final EnumSet<CacheType> set) {
        return set != null && set.size() == fullSize;
    }

    /**
     * Determine whether this instance is equal to one of the given instances.
     *
     * @param v1 the first instance
     *
     * @return {@code true} if one of the instances matches this one, {@code false} otherwise
     */
    public boolean in(final CacheType v1) {
        return this == v1;
    }

    /**
     * Determine whether this instance is equal to one of the given instances.
     *
     * @param v1 the first instance
     * @param v2 the second instance
     *
     * @return {@code true} if one of the instances matches this one, {@code false} otherwise
     */
    public boolean in(final CacheType v1, final CacheType v2) {
        return this == v1 || this == v2;
    }

    /**
     * Determine whether this instance is equal to one of the given instances.
     *
     * @param v1 the first instance
     * @param v2 the second instance
     * @param v3 the third instance
     *
     * @return {@code true} if one of the instances matches this one, {@code false} otherwise
     */
    public boolean in(final CacheType v1, final CacheType v2, final CacheType v3) {
        return this == v1 || this == v2 || this == v3;
    }

    /**
     * Determine whether this instance is equal to one of the given instances.
     *
     * @param values the possible values
     *
     * @return {@code true} if one of the instances matches this one, {@code false} otherwise
     */
    public boolean in(final CacheType... values) {
        if (values != null) for (CacheType value : values) {
            if (this == value) return true;
        }
        return false;
    }
}
