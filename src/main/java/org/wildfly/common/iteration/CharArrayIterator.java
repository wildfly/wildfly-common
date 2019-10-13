package org.wildfly.common.iteration;

import java.util.NoSuchElementException;

/**
 */
final class CharArrayIterator extends CodePointIterator {
    private final int len;
    private final char[] chars;
    private final int offs;
    private int idx;
    private int offset;

    CharArrayIterator(final int len, final char[] chars, final int offs) {
        this.len = len;
        this.chars = chars;
        this.offs = offs;
        idx = 0;
        offset = 0;
    }

    public boolean hasNext() {
        return idx < len;
    }

    public boolean hasPrevious() {
        return idx > 0;
    }

    public int next() {
        if (! hasNext()) throw new NoSuchElementException();
        try {
            offset++;
            return Character.codePointAt(chars, offs + idx);
        } finally {
            idx = Character.offsetByCodePoints(chars, offs, len, offs + idx, 1) - offs;
        }
    }

    public int peekNext() throws NoSuchElementException {
        if (! hasNext()) throw new NoSuchElementException();
        return Character.codePointAt(chars, offs + idx);
    }

    public int previous() {
        if (! hasPrevious()) throw new NoSuchElementException();
        idx = Character.offsetByCodePoints(chars, offs, len, offs + idx, - 1) - offs;
        offset--;
        return Character.codePointAt(chars, offs + idx);
    }

    public int peekPrevious() throws NoSuchElementException {
        if (! hasPrevious()) throw new NoSuchElementException();
        return Character.codePointBefore(chars, offs + idx);
    }

    public long getIndex() {
        return offset;
    }
}
