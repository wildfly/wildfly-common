package org.wildfly.common.archive;

/**
 */
abstract class Index {
    final int tableSize;

    Index(final int entries) {
        if (entries >= 1 << 30) {
            throw new IllegalStateException("Index is too large");
        }
        this.tableSize = Integer.highestOneBit(entries << 2);
    }

    final int size() {
        return tableSize;
    }

    abstract long get(int index);

    abstract void put(int index, long offset);

    int getMask() {
        return tableSize - 1;
    }
}
