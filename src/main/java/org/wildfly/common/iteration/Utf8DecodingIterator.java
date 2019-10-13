package org.wildfly.common.iteration;

import java.util.NoSuchElementException;

/**
 */
class Utf8DecodingIterator extends CodePointIterator {
    private final ByteIterator iter;
    private long offset = 0;

    Utf8DecodingIterator(final ByteIterator iter) {
        this.iter = iter;
    }

    public boolean hasNext() {
        return iter.hasNext();
    }

    public boolean hasPrevious() {
        return offset > 0;
    }

    private void seekToNext() {
        int b;
        while (iter.hasNext()) {
            b = iter.next();
            if ((b & 0b11_000000) != 0b10_000000) {
                // back up one spot
                iter.previous();
                return;
            }
        }
    }

    private void seekToPrev() {
        int b;
        while (iter.hasPrevious()) {
            b = iter.previous();
            if ((b & 0b11_000000) != 0b10_000000) {
                return;
            }
        }
    }

    public int next() {
        if (! iter.hasNext()) throw new NoSuchElementException();
        offset++;
        // >= 1 byte
        int a = iter.next();
        if ((a & 0b1_0000000) == 0b0_0000000) {
            // one byte
            return a;
        }
        if ((a & 0b11_000000) == 0b10_000000) {
            // first byte is invalid; return � instead
            seekToNext();
            return '�';
        }
        // >= 2 bytes
        if (! iter.hasNext()) {
            // truncated
            return '�';
        }
        int b = iter.next();
        if ((b & 0b11_000000) != 0b10_000000) {
            // second byte is invalid; return � instead
            seekToNext();
            return '�';
        }
        if ((a & 0b111_00000) == 0b110_00000) {
            // two bytes
            return (a & 0b000_11111) << 6 | b & 0b00_111111;
        }
        // >= 3 bytes
        if (! iter.hasNext()) {
            // truncated
            return '�';
        }
        int c = iter.next();
        if ((c & 0b11_000000) != 0b10_000000) {
            // third byte is invalid; return � instead
            seekToNext();
            return '�';
        }
        if ((a & 0b1111_0000) == 0b1110_0000) {
            // three bytes
            return (a & 0b0000_1111) << 12 | (b & 0b00_111111) << 6 | c & 0b00_111111;
        }
        // >= 4 bytes
        if (! iter.hasNext()) {
            // truncated
            return '�';
        }
        int d = iter.next();
        if ((d & 0b11_000000) != 0b10_000000) {
            // fourth byte is invalid; return � instead
            seekToNext();
            return '�';
        }
        if ((a & 0b11111_000) == 0b11110_000) {
            // four bytes
            return (a & 0b00000_111) << 18 | (b & 0b00_111111) << 12 | (c & 0b00_111111) << 6 | d & 0b00_111111;
        }
        // only invalid possibilities are left; return � instead
        seekToNext();
        return '�';
    }

    public int peekNext() throws NoSuchElementException {
        if (! iter.hasNext()) throw new NoSuchElementException();
        int a = iter.peekNext();
        if ((a & 0b1_0000000) == 0b0_0000000) {
            // one byte
            return a;
        }
        if ((a & 0b11_000000) == 0b10_000000) {
            // first byte is invalid; return � instead
            return '�';
        }
        // >= 2 bytes
        iter.next();
        if (! iter.hasNext()) {
            iter.previous();
            // truncated
            return '�';
        }
        int b = iter.peekNext();
        if ((b & 0b11_000000) != 0b10_000000) {
            // second byte is invalid; return � instead
            iter.previous();
            return '�';
        }
        if ((a & 0b111_00000) == 0b110_00000) {
            // two bytes
            iter.previous();
            return (a & 0b000_11111) << 6 | b & 0b00_111111;
        }
        // >= 3 bytes
        iter.next();
        if (! iter.hasNext()) {
            // truncated
            iter.previous();
            iter.previous();
            return '�';
        }
        int c = iter.peekNext();
        if ((c & 0b11_000000) != 0b10_000000) {
            // third byte is invalid; return � instead
            iter.previous();
            iter.previous();
            return '�';
        }
        if ((a & 0b1111_0000) == 0b1110_0000) {
            // three bytes
            iter.previous();
            iter.previous();
            return (a & 0b0000_1111) << 12 | (b & 0b00_111111) << 6 | c & 0b00_111111;
        }
        // >= 4 bytes
        iter.next();
        if (! iter.hasNext()) {
            // truncated
            iter.previous();
            iter.previous();
            iter.previous();
            return '�';
        }
        int d = iter.peekNext();
        if ((d & 0b11_000000) != 0b10_000000) {
            // fourth byte is invalid; return � instead
            iter.previous();
            iter.previous();
            iter.previous();
            return '�';
        }
        if ((a & 0b11111_000) == 0b11110_000) {
            // four bytes
            iter.previous();
            iter.previous();
            iter.previous();
            return (a & 0b00000_111) << 18 | (b & 0b00_111111) << 12 | (c & 0b00_111111) << 6 | d & 0b00_111111;
        }
        // only invalid possibilities are left; return � instead
        iter.previous();
        iter.previous();
        iter.previous();
        return '�';
    }

    public int previous() {
        // read backwards
        if (! iter.hasPrevious()) throw new NoSuchElementException();
        offset--;
        // >= 1 byte
        int a = iter.previous();
        if ((a & 0b1_0000000) == 0b0_0000000) {
            // one byte
            return a;
        }
        if ((a & 0b11_000000) != 0b10_000000) {
            // last byte is invalid; return � instead
            seekToPrev();
            return '�';
        }
        int cp = a & 0b00_111111;
        // >= 2 bytes
        a = iter.previous();
        if ((a & 0b111_00000) == 0b110_00000) {
            // two bytes
            return (a & 0b000_11111) << 6 | cp;
        }
        if ((a & 0b11_000000) != 0b10_000000) {
            // second-to-last byte is invalid; return � instead
            seekToPrev();
            return '�';
        }
        cp |= (a & 0b00_111111) << 6;
        // >= 3 bytes
        a = iter.previous();
        if ((a & 0b1111_0000) == 0b1110_0000) {
            // three bytes
            return (a & 0b0000_1111) << 12 | cp;
        }
        if ((a & 0b11_000000) != 0b10_000000) {
            // third-to-last byte is invalid; return � instead
            seekToPrev();
            return '�';
        }
        cp |= (a & 0b00_111111) << 12;
        // >= 4 bytes
        a = iter.previous();
        if ((a & 0b11111_000) == 0b11110_000) {
            // four bytes
            return (a & 0b00000_111) << 18 | cp;
        }
        // only invalid possibilities are left; return � instead
        seekToPrev();
        return '�';
    }

    public int peekPrevious() throws NoSuchElementException {
        // read backwards
        if (! iter.hasPrevious()) throw new NoSuchElementException();
        // >= 1 byte
        int a = iter.peekPrevious();
        if ((a & 0b1_0000000) == 0b0_0000000) {
            // one byte
            return a;
        }
        if ((a & 0b11_000000) != 0b10_000000) {
            // last byte is invalid; return � instead
            return '�';
        }
        int cp = a & 0b00_111111;
        // >= 2 bytes
        iter.previous();
        a = iter.peekPrevious();
        if ((a & 0b111_00000) == 0b110_00000) {
            // two bytes
            iter.next();
            return (a & 0b000_11111) << 6 | cp;
        }
        if ((a & 0b11_000000) != 0b10_000000) {
            // second-to-last byte is invalid; return � instead
            iter.next();
            return '�';
        }
        cp |= (a & 0b00_111111) << 6;
        // >= 3 bytes
        iter.previous();
        a = iter.peekPrevious();
        if ((a & 0b1111_0000) == 0b1110_0000) {
            // three bytes
            iter.next();
            iter.next();
            return (a & 0b0000_1111) << 12 | cp;
        }
        if ((a & 0b11_000000) != 0b10_000000) {
            // third-to-last byte is invalid; return � instead
            iter.next();
            iter.next();
            return '�';
        }
        cp |= (a & 0b00_111111) << 12;
        // >= 4 bytes
        iter.previous();
        a = iter.peekPrevious();
        if ((a & 0b11111_000) == 0b11110_000) {
            // four bytes
            iter.next();
            iter.next();
            iter.next();
            return (a & 0b00000_111) << 18 | cp;
        }
        // only invalid possibilities are left; return � instead
        iter.next();
        iter.next();
        iter.next();
        return '�';
    }

    public long getIndex() {
        return offset;
    }
}
