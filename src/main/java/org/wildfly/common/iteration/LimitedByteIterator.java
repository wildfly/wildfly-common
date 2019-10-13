package org.wildfly.common.iteration;

import java.util.NoSuchElementException;

/**
 */
final class LimitedByteIterator extends ByteIterator {
    private final ByteIterator iter;
    private final long size;
    long offset;

    LimitedByteIterator(final ByteIterator iter, final long size) {
        this.iter = iter;
        this.size = size;
        offset = 0;
    }

    public boolean hasNext() {
        return offset < size && iter.hasNext();
    }

    public boolean hasPrevious() {
        return offset > 0;
    }

    public int next() {
        if (offset == size) {
            throw new NoSuchElementException();
        }
        offset++;
        return iter.next();
    }

    public int peekNext() throws NoSuchElementException {
        if (offset == size) {
            throw new NoSuchElementException();
        }
        return iter.peekNext();
    }

    public int previous() {
        if (offset == 0) {
            throw new NoSuchElementException();
        }
        offset--;
        return iter.previous();
    }

    public int peekPrevious() throws NoSuchElementException {
        if (offset == 0) {
            throw new NoSuchElementException();
        }
        return iter.peekPrevious();
    }

    public int drain(final byte[] dst, final int offs, final int len) {
        return super.drain(dst, offs, (int) Math.min(len, size - offset));
    }

    public long getIndex() {
        return offset;
    }
}
