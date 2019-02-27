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
