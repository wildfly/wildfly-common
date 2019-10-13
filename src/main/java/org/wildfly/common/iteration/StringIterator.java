package org.wildfly.common.iteration;

import java.util.NoSuchElementException;

/**
 */
final class StringIterator extends CodePointIterator {
    private final int len;
    private final String string;
    private final int offs;
    private int idx;
    private long offset;

    StringIterator(final int len, final String string, final int offs) {
        this.len = len;
        this.string = string;
        this.offs = offs;
        idx = 0;
        offset = 0;
    }

    public boolean hasNext() {
        return idx < len;
    }

    public boolean hasPrevious() {
        return offset > 0;
    }

    public int next() {
        if (! hasNext()) throw new NoSuchElementException();
        try {
            offset++;
            return string.codePointAt(idx + offs);
        } finally {
            idx = string.offsetByCodePoints(idx + offs, 1) - offs;
        }
    }

    public int peekNext() throws NoSuchElementException {
        if (! hasNext()) throw new NoSuchElementException();
        return string.codePointAt(idx + offs);
    }

    public int previous() {
        if (! hasPrevious()) throw new NoSuchElementException();
        idx = string.offsetByCodePoints(idx + offs, - 1) - offs;
        offset--;
        return string.codePointAt(idx + offs);
    }

    public int peekPrevious() throws NoSuchElementException {
        if (! hasPrevious()) throw new NoSuchElementException();
        return string.codePointBefore(idx + offs);
    }

    public long getIndex() {
        return offset;
    }

    public StringBuilder drainTo(final StringBuilder b) {
        try {
            return b.append(string, idx + offs, offs + len);
        } finally {
            offset += string.codePointCount(idx + offs, offs + len);
            idx = len;
        }
    }

    public String drainToString() {
        try {
            return string.substring(idx + offs, offs + len);
        } finally {
            offset += string.codePointCount(idx + offs, offs + len);
            idx = len;
        }
    }
}
