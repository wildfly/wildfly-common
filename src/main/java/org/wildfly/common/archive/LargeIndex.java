package org.wildfly.common.archive;

import java.util.Arrays;

/**
 * An index for archives between 64KB and 4GB in size.
 */
final class LargeIndex extends Index {
    private final int[] table;

    LargeIndex(final int entries) {
        super(entries);
        final int[] array = new int[size()];
        Arrays.fill(array, -1);
        this.table = array;
    }

    long get(final int index) {
        final int val = table[index];
        return val == -1 ? -1 : val & 0xffffffff;
    }

    void put(int index, final long offset) {
        final int[] table = this.table;
        int val = table[index];
        while (val != -1L) {
            index = index + 1 & getMask();
            val = table[index];
        }
        table[index] = Math.toIntExact(offset);
    }
}
