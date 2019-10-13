package org.wildfly.common.iteration;

import java.util.NoSuchElementException;

/**
 */
final class LimitedCodePointIterator extends CodePointIterator {
    private final CodePointIterator iter;
    private final long size;
    private long offset;

    LimitedCodePointIterator(final CodePointIterator iter, final long size) {
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
        if (! hasNext()) {
            throw new NoSuchElementException();
        }
        offset++;
        return iter.next();
    }

    public int peekNext() throws NoSuchElementException {
        if (! hasNext()) {
            throw new NoSuchElementException();
        }
        return iter.peekNext();
    }

    public int previous() {
        if (! hasPrevious()) {
            throw new NoSuchElementException();
        }
        offset--;
        return iter.previous();
    }

    public int peekPrevious() throws NoSuchElementException {
        if (! hasPrevious()) {
            throw new NoSuchElementException();
        }
        return iter.peekPrevious();
    }

    public long getIndex() {
        return offset;
    }
}
