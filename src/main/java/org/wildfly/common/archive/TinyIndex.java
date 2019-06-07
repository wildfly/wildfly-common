/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2019 Red Hat, Inc., and individual contributors
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

package org.wildfly.common.archive;

import java.util.Arrays;

/**
 * An index for archives less than 64KB in size.
 */
final class TinyIndex extends Index {
    private final short[] table;

    TinyIndex(final int entries) {
        super(entries);
        final short[] array = new short[size()];
        Arrays.fill(array, (short) -1);
        this.table = array;
    }

    @Override
    long get(final int index) {
        final int val = table[index];
        return val == -1 ? -1 : val & 0xffff;
    }

    @Override
    void put(int index, final long offset) {
        final short[] table = this.table;
        int val = table[index];
        while (val != -1L) {
            index = index + 1 & getMask();
            val = table[index];
        }
        table[index] = (short) offset;
    }
}
