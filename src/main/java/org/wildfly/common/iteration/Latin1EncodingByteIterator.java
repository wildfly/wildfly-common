package org.wildfly.common.iteration;

import java.util.NoSuchElementException;

/**
 */
final class Latin1EncodingByteIterator extends ByteIterator {
    private final CodePointIterator iter;

    Latin1EncodingByteIterator(final CodePointIterator iter) {
        this.iter = iter;
    }

    public boolean hasNext() {
        return iter.hasNext();
    }

    public boolean hasPrevious() {
        return iter.hasPrevious();
    }

    public int next() throws NoSuchElementException {
        final int v = iter.next();
        return v > 255 ? '?' : v;
    }

    public int peekNext() throws NoSuchElementException {
        final int v = iter.peekNext();
        return v > 255 ? '?' : v;
    }

    public int previous() throws NoSuchElementException {
        final int v = iter.previous();
        return v > 255 ? '?' : v;
    }

    public int peekPrevious() throws NoSuchElementException {
        final int v = iter.peekPrevious();
        return v > 255 ? '?' : v;
    }

    public long getIndex() {
        return iter.getIndex();
    }
}
