package org.wildfly.common.iteration;

import java.util.NoSuchElementException;

/**
 */
final class Latin1DecodingIterator extends CodePointIterator {
    private final ByteIterator iter;
    private final long start;

    Latin1DecodingIterator(final ByteIterator iter, final long start) {
        this.iter = iter;
        this.start = start;
    }

    public boolean hasNext() {
        return iter.hasNext();
    }

    public boolean hasPrevious() {
        return start > 0 && iter.hasPrevious();
    }

    public int next() {
        return iter.next();
    }

    public int peekNext() throws NoSuchElementException {
        return iter.peekNext();
    }

    public int previous() {
        if (start == 0) throw new NoSuchElementException();
        return iter.previous();
    }

    public int peekPrevious() throws NoSuchElementException {
        return iter.peekPrevious();
    }

    public long getIndex() {
        return iter.getIndex() - start;
    }
}
