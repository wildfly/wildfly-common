package org.wildfly.common.iteration;

import java.util.NoSuchElementException;

/**
 */
final class DelimitedByteIterator extends ByteIterator {
    private final ByteIterator iter;
    private final int[] delims;
    long offset;

    DelimitedByteIterator(final ByteIterator iter, final int... delims) {
        this.iter = iter;
        this.delims = delims;
        offset = 0;
    }

    public boolean hasNext() {
        return iter.hasNext() && ! isDelim(iter.peekNext());
    }

    public boolean hasPrevious() {
        return offset > 0;
    }

    public int next() {
        if (! hasNext()) throw new NoSuchElementException();
        offset++;
        return iter.next();
    }

    public int peekNext() throws NoSuchElementException {
        if (! hasNext()) throw new NoSuchElementException();
        return iter.peekNext();
    }

    public int previous() {
        if (! hasPrevious()) throw new NoSuchElementException();
        offset--;
        return iter.previous();
    }

    public int peekPrevious() throws NoSuchElementException {
        if (! hasPrevious()) throw new NoSuchElementException();
        return iter.peekPrevious();
    }

    public long getIndex() {
        return offset;
    }

    private boolean isDelim(int b) {
        for (int delim : delims) {
            if (delim == b) {
                return true;
            }
        }
        return false;
    }
}
