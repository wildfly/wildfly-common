package org.wildfly.common.iteration;

import java.util.NoSuchElementException;

/**
 */
final class ByteTableTranslatingByteIterator extends ByteIterator {
    private final ByteIterator iter;
    private final byte[] table;

    ByteTableTranslatingByteIterator(final ByteIterator iter, final byte[] table) {
        this.iter = iter;
        this.table = table;
    }

    public boolean hasNext() {
        return iter.hasNext();
    }

    public boolean hasPrevious() {
        return iter.hasPrevious();
    }

    public int next() throws NoSuchElementException {
        return table[iter.next()] & 0xff;
    }

    public int peekNext() throws NoSuchElementException {
        return table[iter.peekNext()] & 0xff;
    }

    public int previous() throws NoSuchElementException {
        return table[iter.previous()] & 0xff;
    }

    public int peekPrevious() throws NoSuchElementException {
        return table[iter.peekPrevious()] & 0xff;
    }

    public long getIndex() {
        return iter.getIndex();
    }
}
