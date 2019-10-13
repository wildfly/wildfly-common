package org.wildfly.common.iteration;

import static org.wildfly.common._private.CommonMessages.msg;

import java.util.NoSuchElementException;

/**
 */
abstract class Base32DecodingByteIterator extends ByteIterator {
    private final CodePointIterator iter;
    private final boolean requirePadding;

    // states:
    // 0x00: nothing read
    // 0x01: five bytes to return o0..o4
    // 0x02: four bytes to return o1..o4 (o0 still populated)
    // 0x03: three byte to return o2..o4 (o0..o1 still populated)
    // 0x04: two bytes to return o3..o4 (o0..o2 still populated)
    // 0x05: one byte to return o4 (o0..o3 still populated)
    // 0x06: four bytes then eof o0..o3 =
    // 0x07: three bytes then eof o1..o3 = (o0 still populated)
    // 0x08: two bytes then eof o2..o3 = (o0..o1 still populated)
    // 0x09: one byte then eof o3 = (o0..o2 still populated)
    // 0x0a: three bytes then eof o0..o2 ===
    // 0x0b: two bytes then eof o1..o2 === (o0 still populated)
    // 0x0c: one byte then eof o2 === (o0..o1 still populated)
    // 0x0d: two bytes then eof o0..o1 ====
    // 0x0e: one byte then eof o1 ==== (o0 still populated)
    // 0x0f: one byte then eof o0 ======
    // 0x10: four bytes then eof o0..o3 no pad
    // 0x11: three bytes then eof o1..o3 no pad (o0 still populated)
    // 0x12: two bytes then eof o2..o3 no pad (o0..o1 still populated)
    // 0x13: one byte then eof o3 no pad (o0..o2 still populated)
    // 0x14: three bytes then eof o0..o2 no pad
    // 0x15: two bytes then eof o1..o2 no pad (o0 still populated)
    // 0x16: one byte then eof o2 no pad (o0..o1 still populated)
    // 0x17: two bytes then eof o0..o1 no pad
    // 0x18: one byte then eof o1 no pad (o0 still populated)
    // 0x19: one byte then eof o0 no pad
    // 0x1a: end (=) (o0..o3 still populated)
    // 0x1b: end (===) (o0..o2 still populated)
    // 0x1c: end (====) (o0..o1 still populated)
    // 0x1d: end (======) (o0 still populated)
    // 0x1e: end (= but no pad) (o0..o3 still populated)
    // 0x1f: end (=== but no pad) (o0..o2 still populated)
    // 0x20: end (==== but no pad) (o0..o1 still populated)
    // 0x21: end (====== but no pad) (o0 still populated)

    private int state = 0;
    private int o0, o1, o2, o3, o4;
    private int offset;

    Base32DecodingByteIterator(final CodePointIterator iter, final boolean requirePadding) {
        this.iter = iter;
        this.requirePadding = requirePadding;
    }

    public boolean hasNext() {
        if (state == 0) {
            if (! iter.hasNext()) {
                return false;
            }
            int b0 = iter.next();
            if (b0 == '=') {
                throw msg.unexpectedPadding();
            }
            if (! iter.hasNext()) {
                if (requirePadding) {
                    throw msg.expectedPadding();
                } else {
                    throw msg.incompleteDecode();
                }
            }
            int b1 = iter.next();
            if (b1 == '=') {
                throw msg.unexpectedPadding();
            }
            o0 = calc0(b0, b1);
            if (! iter.hasNext()) {
                if (requirePadding) {
                    throw msg.expectedPadding();
                }
                state = 0x19;
                return true;
            }
            int b2 = iter.next();
            if (b2 == '=') {
                for (int i = 0; i < 5; i++) {
                    if (! iter.hasNext()) {
                        throw msg.expectedPaddingCharacters(6);
                    }
                    if (iter.next() != '=') {
                        throw msg.expectedPaddingCharacters(6);
                    }
                }
                state = 0x0f;
                return true;
            }
            if (! iter.hasNext()) {
                if (requirePadding) {
                    throw msg.expectedPadding();
                } else {
                    throw msg.incompleteDecode();
                }
            }
            int b3 = iter.next();
            if (b3 == '=') {
                throw msg.unexpectedPadding();
            }
            o1 = calc1(b1, b2, b3);
            if (! iter.hasNext()) {
                if (requirePadding) {
                    throw msg.expectedPadding();
                }
                state = 0x17;
                return true;
            }
            int b4 = iter.next();
            if (b4 == '=') {
                for (int i = 0; i < 3; i++) {
                    if (! iter.hasNext()) {
                        throw msg.expectedPaddingCharacters(4);
                    }
                    if (iter.next() != '=') {
                        throw msg.expectedPaddingCharacters(4);
                    }
                }
                state = 0x0d;
                return true;
            }
            o2 = calc2(b3, b4);
            if (! iter.hasNext()) {
                if (requirePadding) {
                    throw msg.expectedPadding();
                }
                state = 0x14;
                return true;
            }
            int b5 = iter.next();
            if (b5 == '=') {
                for (int i = 0; i < 2; i++) {
                    if (! iter.hasNext()) {
                        throw msg.expectedPaddingCharacters(3);
                    }
                    if (iter.next() != '=') {
                        throw msg.expectedPaddingCharacters(3);
                    }
                }
                state = 0x0a;
                return true;
            }
            if (! iter.hasNext()) {
                if (requirePadding) {
                    throw msg.expectedPadding();
                } else {
                    throw msg.incompleteDecode();
                }
            }
            int b6 = iter.next();
            if (b6 == '=') {
                throw msg.unexpectedPadding();
            }
            o3 = calc3(b4, b5, b6);
            if (! iter.hasNext()) {
                if (requirePadding) {
                    throw msg.expectedPadding();
                }
                state = 0x10;
                return true;
            }
            int b7 = iter.next();
            if (b7 == '=') {
                state = 0x06;
                return true;
            }
            o4 = calc4(b6, b7);
            state = 1;
            return true;
        } else {
            return state < 0x1a;
        }
    }

    public boolean hasPrevious() {
        return offset > 0;
    }

    abstract int calc0(int b0, int b1);

    abstract int calc1(int b1, int b2, int b3);

    abstract int calc2(int b3, int b4);

    abstract int calc3(int b4, int b5, int b6);

    abstract int calc4(int b6, int b7);

    public int next() {
        if (! hasNext()) {
            throw new NoSuchElementException();
        }
        switch (state) {
            case 1:
            case 6:
            case 0x0a:
            case 0x0d:
            case 0x10:
            case 0x14:
            case 0x17: {
                state++;
                offset++;
                return o0;
            }
            case 2:
            case 7:
            case 0x0b:
            case 0x11:
            case 0x15: {
                state++;
                offset++;
                return o1;
            }
            case 3:
            case 8:
            case 0x12: {
                state++;
                offset++;
                return o2;
            }
            case 4: {
                state = 5;
                offset++;
                return o3;
            }
            case 5: {
                state = 0;
                offset++;
                return o4;
            }
            case 9: {
                state = 0x1a;
                offset++;
                return o3;
            }
            case 0x0c: {
                state = 0x1b;
                offset++;
                return o2;
            }
            case 0x0e: {
                state = 0x1c;
                offset++;
                return o1;
            }
            case 0x0f: {
                state = 0x1d;
                offset++;
                return o0;
            }
            case 0x13: {
                state = 0x1e;
                offset++;
                return o3;
            }
            case 0x16: {
                state = 0x1f;
                offset++;
                return o2;
            }
            case 0x18: {
                state = 0x20;
                offset++;
                return o1;
            }
            case 0x19: {
                state = 0x21;
                offset++;
                return o0;
            }
            default: {
                // padding
                throw new NoSuchElementException();
            }
        }
    }

    public int peekNext() throws NoSuchElementException {
        if (! hasNext()) {
            throw new NoSuchElementException();
        }
        switch (state) {
            case 1:
            case 6:
            case 0x0a:
            case 0x0d:
            case 0x0f:
            case 0x10:
            case 0x14:
            case 0x17:
            case 0x19: {
                return o0;
            }
            case 2:
            case 7:
            case 0x0b:
            case 0x0e:
            case 0x11:
            case 0x15:
            case 0x18: {
                return o1;
            }
            case 3:
            case 8:
            case 0x0c:
            case 0x12:
            case 0x16: {
                return o2;
            }
            case 4:
            case 9:
            case 0x13: {
                return o3;
            }
            case 5: {
                return o4;
            }
            default: {
                // padding
                throw new NoSuchElementException();
            }
        }
    }

    public int previous() {
        if (! hasPrevious()) {
            throw new NoSuchElementException();
        }
        int skipChars = 0;
        switch (state) {
            case 0:
            case 1:
            case 6:
            case 0x0a:
            case 0x0d:
            case 0x0f:
            case 0x10:
            case 0x14:
            case 0x17:
            case 0x19: {
                if (state == 6 || state == 0x0a || state == 0x0d || state == 0x0f) {
                    skipChars = 8;
                } else if (state == 0x10) {
                    skipChars = 7;
                } else if (state == 0x14) {
                    skipChars = 5;
                } else if (state == 0x17) {
                    skipChars = 4;
                } else if (state == 0x19) {
                    skipChars = 2;
                }
                for (int i = 0; i < skipChars; i++) {
                    iter.previous(); // consume character
                }
                int b7 = iter.previous();
                int b6 = iter.previous();
                int b5 = iter.previous();
                int b4 = iter.previous();
                int b3 = iter.previous();
                int b2 = iter.previous();
                int b1 = iter.previous();
                int b0 = iter.previous();
                o0 = calc0(b0, b1);
                o1 = calc1(b1, b2, b3);
                o2 = calc2(b3, b4);
                o3 = calc3(b4, b5, b6);
                o4 = calc4(b6, b7);
                state = 5;
                offset--;
                return o4;
            }
            case 2:
            case 7:
            case 0x0b:
            case 0x0e:
            case 0x11:
            case 0x15:
            case 0x18: {
                state--;
                offset--;
                return o0;
            }
            case 3:
            case 8:
            case 0x0c:
            case 0x12:
            case 0x16: {
                state--;
                offset--;
                return o1;
            }
            case 4:
            case 9:
            case 0x13: {
                state--;
                offset--;
                return o2;
            }
            case 5: {
                state = 4;
                offset--;
                return o3;
            }
            case 0x1a: {
                state = 9;
                offset--;
                return o3;
            }
            case 0x1b: {
                state = 0x0c;
                offset--;
                return o2;
            }
            case 0x1c: {
                state = 0x0e;
                offset--;
                return o1;
            }
            case 0x1d: {
                state = 0x0f;
                offset--;
                return o0;
            }
            case 0x1e: {
                state = 0x13;
                offset--;
                return o3;
            }
            case 0x1f: {
                state = 0x16;
                offset--;
                return o2;
            }
            case 0x20: {
                state = 0x18;
                offset--;
                return o1;
            }
            case 0x21: {
                state = 0x19;
                offset--;
                return o0;
            }
            default: {
                throw new NoSuchElementException();
            }
        }
    }

    public int peekPrevious() throws NoSuchElementException {
        if (! hasPrevious()) {
            throw new NoSuchElementException();
        }
        int skipChars = 0;
        switch (state) {
            case 0:
            case 1:
            case 6:
            case 0x0a:
            case 0x0d:
            case 0x0f:
            case 0x10:
            case 0x14:
            case 0x17:
            case 0x19: {
                if (state == 6 || state == 0x0a || state == 0x0d || state == 0x0f) {
                    skipChars = 8;
                } else if (state == 0x10) {
                    skipChars = 7;
                } else if (state == 0x14) {
                    skipChars = 5;
                } else if (state == 0x17) {
                    skipChars = 4;
                } else if (state == 0x19) {
                    skipChars = 2;
                }
                for (int i = 0; i < skipChars; i++) {
                    iter.previous(); // consume character
                }
                int b7 = iter.previous();
                int b6 = iter.peekPrevious();
                iter.next();
                for (int i = 0; i < skipChars; i++) {
                    iter.next();
                }
                return calc4(b6, b7);
            }
            case 2:
            case 7:
            case 0x0b:
            case 0x0e:
            case 0x11:
            case 0x15:
            case 0x18:
            case 0x1d:
            case 0x21: {
                return o0;
            }
            case 3:
            case 8:
            case 0x0c:
            case 0x12:
            case 0x16:
            case 0x1c:
            case 0x20: {
                return o1;
            }
            case 4:
            case 9:
            case 0x13:
            case 0x1b:
            case 0x1f: {
                return o2;
            }
            case 5:
            case 0x1a:
            case 0x1e: {
                return o3;
            }
            default: {
                throw new NoSuchElementException();
            }
        }
    }

    public long getIndex() {
        return offset;
    }
}
