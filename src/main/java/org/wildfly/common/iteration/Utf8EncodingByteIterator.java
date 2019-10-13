package org.wildfly.common.iteration;

import java.util.NoSuchElementException;

import org.wildfly.common.Assert;
import org.wildfly.common.bytes.ByteStringBuilder;

/**
 */
final class Utf8EncodingByteIterator extends ByteIterator {
    private final CodePointIterator iter;
    private final boolean escapeNul;
    // state 0 = between code points
    // state 1 = after byte 1 of 2
    // state 2 = after byte 1 of 3
    // state 3 = after byte 2 of 3
    // state 4 = after byte 1 of 4
    // state 5 = after byte 2 of 4
    // state 6 = after byte 3 of 4

    private int st;
    private int cp;
    private long offset;

    Utf8EncodingByteIterator(final CodePointIterator iter, final boolean escapeNul) {
        this.iter = iter;
        this.escapeNul = escapeNul;
        cp = - 1;
    }

    public boolean hasNext() {
        return st != 0 || iter.hasNext();
    }

    public boolean hasPrevious() {
        return st != 0 || iter.hasPrevious();
    }

    public int next() throws NoSuchElementException {
        if (! hasNext()) throw new NoSuchElementException();
        offset++;
        switch (st) {
            case 0: {
                int cp = iter.next();
                if (cp == 0 && ! escapeNul || cp < 0x80) {
                    return cp;
                } else if (cp < 0x800) {
                    this.cp = cp;
                    st = 1;
                    return 0b110_00000 | cp >> 6;
                } else if (cp < 0x10000) {
                    this.cp = cp;
                    st = 2;
                    return 0b1110_0000 | cp >> 12;
                } else if (cp < 0x110000) {
                    this.cp = cp;
                    st = 4;
                    return 0b11110_000 | cp >> 18;
                } else {
                    this.cp = '�';
                    st = 2;
                    return 0b1110_0000 | '�' >> 12;
                }
            }
            case 1:
            case 3:
            case 6: {
                st = 0;
                return 0b10_000000 | cp & 0x3f;
            }
            case 2: {
                st = 3;
                return 0b10_000000 | cp >> 6 & 0x3f;
            }
            case 4: {
                st = 5;
                return 0b10_000000 | cp >> 12 & 0x3f;
            }
            case 5: {
                st = 6;
                return 0b10_000000 | cp >> 6 & 0x3f;
            }
            default: {
                throw Assert.impossibleSwitchCase(st);
            }
        }
    }

    public int peekNext() throws NoSuchElementException {
        if (! hasNext()) throw new NoSuchElementException();
        switch (st) {
            case 0: {
                int cp = iter.peekNext();
                if (cp < 0x80) {
                    return cp;
                } else if (cp < 0x800) {
                    return 0b110_00000 | cp >> 6;
                } else if (cp < 0x10000) {
                    return 0b1110_0000 | cp >> 12;
                } else if (cp < 0x110000) {
                    return 0b11110_000 | cp >> 18;
                } else {
                    return 0b1110_0000 | '�' >> 12;
                }
            }
            case 1:
            case 3:
            case 6: {
                return 0b10_000000 | cp & 0x3f;
            }
            case 2:
            case 5: {
                return 0b10_000000 | cp >> 6 & 0x3f;
            }
            case 4: {
                return 0b10_000000 | cp >> 12 & 0x3f;
            }
            default: {
                throw Assert.impossibleSwitchCase(st);
            }
        }
    }

    public int previous() throws NoSuchElementException {
        if (! hasPrevious()) throw new NoSuchElementException();
        offset--;
        switch (st) {
            case 0: {
                int cp = iter.previous();
                if (cp == 0 && ! escapeNul || cp < 0x80) {
                    return cp;
                } else if (cp < 0x800) {
                    this.cp = cp;
                    st = 1;
                    return 0b10_000000 | cp & 0x3f;
                } else if (cp < 0x10000) {
                    this.cp = cp;
                    st = 3;
                    return 0b10_000000 | cp & 0x3f;
                } else if (cp < 0x110000) {
                    this.cp = cp;
                    st = 6;
                    return 0b10_000000 | cp & 0x3f;
                } else {
                    this.cp = '�';
                    st = 3;
                    return 0b10_000000 | '�' & 0x3f;
                }
            }
            case 1: {
                st = 0;
                return 0b110_00000 | cp >> 6;
            }
            case 2: {
                st = 0;
                return 0b1110_0000 | cp >> 12;
            }
            case 3: {
                st = 2;
                return 0b10_000000 | cp >> 6 & 0x3f;
            }
            case 4: {
                st = 0;
                return 0b11110_000 | cp >> 18;
            }
            case 5: {
                st = 4;
                return 0b10_000000 | cp >> 12 & 0x3f;
            }
            case 6: {
                st = 5;
                return 0b10_000000 | cp >> 6 & 0x3f;
            }
            default: {
                throw Assert.impossibleSwitchCase(st);
            }
        }
    }

    public int peekPrevious() throws NoSuchElementException {
        if (! hasPrevious()) throw new NoSuchElementException();
        switch (st) {
            case 0: {
                int cp = iter.peekPrevious();
                if (cp == 0 && ! escapeNul || cp < 0x80) {
                    return cp;
                } else if (cp < 0x800) {
                    return 0b10_000000 | cp & 0x3f;
                } else if (cp < 0x10000) {
                    return 0b10_000000 | cp & 0x3f;
                } else if (cp < 0x110000) {
                    return 0b10_000000 | cp & 0x3f;
                } else {
                    return 0b10_000000 | '�' & 0x3f;
                }
            }
            case 1: {
                return 0b110_00000 | cp >> 6;
            }
            case 2: {
                return 0b1110_0000 | cp >> 12;
            }
            case 3:
            case 6: {
                return 0b10_000000 | cp >> 6 & 0x3f;
            }
            case 4: {
                return 0b11110_000 | cp >> 18;
            }
            case 5: {
                return 0b10_000000 | cp >> 12 & 0x3f;
            }
            default: {
                throw Assert.impossibleSwitchCase(st);
            }
        }
    }

    public ByteStringBuilder appendTo(final ByteStringBuilder builder) {
        if (st == 0) {
            // this is faster
            final int oldLen = builder.length();
            builder.appendUtf8(iter);
            offset += builder.length() - oldLen;
        } else {
            super.appendTo(builder);
        }
        return builder;
    }

    public long getIndex() {
        return offset;
    }
}
