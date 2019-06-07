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
 * An index for archives greater than 4GB in size.
 */
final class HugeIndex extends Index {
    private final long[] table;

    HugeIndex(final int entries) {
        super(entries);
        final long[] array = new long[size()];
        Arrays.fill(array, -1);
        this.table = array;
    }

    long get(final int index) {
        return table[index];
    }

    void put(int index, final long offset) {
        final long[] table = this.table;
        long val = table[index];
        while (val != -1L) {
            index = index + 1 & getMask();
            val = table[index];
        }
        table[index] = offset;
    }
}
