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

    long get(final int index) {
        final int val = table[index];
        return val == -1 ? -1 : val & 0xffff;
    }

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
