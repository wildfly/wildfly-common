package org.wildfly.common.iteration;

import java.util.NoSuchElementException;

import org.wildfly.common.Assert;

/**
 */
abstract class Base32EncodingCodePointIterator extends CodePointIterator {

    private final ByteIterator iter;
    private final boolean addPadding;
    private int c0, c1, c2, c3, c4, c5, c6, c7;
    private int state;
    private int offset;

    Base32EncodingCodePointIterator(final ByteIterator iter, final boolean addPadding) {
        this.iter = iter;
        this.addPadding = addPadding;
    }

    // states:
    // 0x00 - need another five data bytes
    // 0x01 - 8 characters to read
    // 0x02 - 7 characters to read
    // 0x03 - 6 characters to read
    // 0x04 - 5 characters to read
    // 0x05 - 4 characters to read
    // 0x06 - 3 characters to read
    // 0x07 - 2 characters to read
    // 0x08 - 1 character to read
    // 0x09 - 2 characters + ====== to read
    // 0x0a - 1 character (c1) + ====== to read
    // 0x0b - ====== to read
    // 0x0c - ===== to read
    // 0x0d - ==== to read
    // 0x0e - === to read
    // 0x0f - == to read
    // 0x10 - = to read
    // 0x11 - 4 characters + ==== to read
    // 0x12 - 3 characters (c1, c2, c3) + ==== to read
    // 0x13 - 2 characters (c2, c3) + ==== to read
    // 0x14 - 1 character (c3) + ==== to read
    // 0x15 - ==== to read
    // 0x16 - === to read
    // 0x17 - == to read
    // 0x18 - = to read
    // 0x19 - 5 characters + === to read
    // 0x1a - 4 characters (c1, c2, c3, c4) + === to read
    // 0x1b - 3 characters (c2, c3, c4) + === to read
    // 0x1c - 2 characters (c3, c4) + === to read
    // 0x1d - 1 character (c4) + === to read
    // 0x1e - === to read
    // 0x1f - == to read
    // 0x20 - = to read
    // 0x21 - 7 characters + = to read
    // 0x22 - 6 characters (c1, c2, c3, c4, c5, c6) + = to read
    // 0x23 - 5 characters (c2, c3, c4, c5, c6) + = to read
    // 0x24 - 4 characters (c3, c4, c5, c6) + = to read
    // 0x25 - 3 characters (c4, c5, c6) + = to read
    // 0x26 - 2 characters (c5, c6) + = to read
    // 0x27 - 1 characters (c6) + = to read
    // 0x28 - = to read
    // 0x29 - after ======
    // 0x2a - after ====
    // 0x2b - after ===
    // 0x2c - after =
    // 0x2d - end

    public boolean hasNext() {
        return state == 0 && iter.hasNext() || state > 0 && state < 0x29;
    }

    public boolean hasPrevious() {
        return offset > 0;
    }

    abstract int calc0(int b0);

    abstract int calc1(int b0, int b1);

    abstract int calc2(final int b1);

    abstract int calc3(final int b1, final int b2);

    abstract int calc4(final int b2, final int b3);

    abstract int calc5(final int b3);

    abstract int calc6(final int b3, final int b4);

    abstract int calc7(final int b4);

    public int next() throws NoSuchElementException {
        if (! hasNext()) throw new NoSuchElementException();
        offset++;
        switch (state) {
            case 0: {
                assert iter.hasNext();
                int b0 = iter.next();
                c0 = calc0(b0);
                if (! iter.hasNext()) {
                    c1 = calc1(b0, 0);
                    state = 0x0a;
                    return c0;
                }
                int b1 = iter.next();
                c1 = calc1(b0, b1);
                c2 = calc2(b1);
                if (! iter.hasNext()) {
                    c3 = calc3(b1, 0);
                    state = 0x12;
                    return c0;
                }
                int b2 = iter.next();
                c3 = calc3(b1, b2);
                if (! iter.hasNext()) {
                    c4 = calc4(b2, 0);
                    state = 0x1a;
                    return c0;
                }
                int b3 = iter.next();
                c4 = calc4(b2, b3);
                c5 = calc5(b3);
                if (! iter.hasNext()) {
                    c6 = calc6(b3, 0);
                    state = 0x22;
                    return c0;
                }
                int b4 = iter.next();
                c6 = calc6(b3, b4);
                c7 = calc7(b4);
                state = 2;
                return c0;
            }
            case 1:
            case 9:
            case 0x11:
            case 0x19:
            case 0x21: {
                state++;
                return c0;
            }
            case 2:
            case 0x12:
            case 0x1a:
            case 0x22: {
                state++;
                return c1;
            }
            case 3:
            case 0x13:
            case 0x1b:
            case 0x23: {
                state++;
                return c2;
            }
            case 4:
            case 0x1c:
            case 0x24: {
                state++;
                return c3;
            }
            case 5:
            case 0x25: {
                state++;
                return c4;
            }
            case 6:
            case 0x26: {
                state++;
                return c5;
            }
            case 7: {
                state = 8;
                return c6;
            }
            case 8: {
                state = 0;
                return c7;
            }
            case 0x0a: {
                state = addPadding ? 0x0b : 0x29;
                return c1;
            }
            case 0x14: {
                state = addPadding ? 0x15 : 0x2a;
                return c3;
            }
            case 0x1d: {
                state = addPadding ? 0x1e : 0x2b;
                return c4;
            }
            case 0x27: {
                state = addPadding ? 0x28 : 0x2c;
                return c6;
            }
            case 0x0b:
            case 0x0c:
            case 0x0d:
            case 0x0e:
            case 0x0f:
            case 0x15:
            case 0x16:
            case 0x17:
            case 0x1e:
            case 0x1f: {
                state++;
                return '=';
            }
            case 0x10: {
                state = 0x29;
                return '=';
            }
            case 0x18: {
                state = 0x2a;
                return '=';
            }
            case 0x20: {
                state = 0x2b;
                return '=';
            }
            case 0x28: {
                state = 0x2c;
                return '=';
            }
            default: {
                throw Assert.impossibleSwitchCase(state);
            }
        }
    }

    public int peekNext() throws NoSuchElementException {
        if (! hasNext()) throw new NoSuchElementException();
        switch (state) {
            case 0: {
                assert iter.hasNext();
                int b0 = iter.next();
                c0 = calc0(b0);
                if (! iter.hasNext()) {
                    c1 = calc1(b0, 0);
                    state = 9;
                    return c0;
                }
                int b1 = iter.next();
                c1 = calc1(b0, b1);
                c2 = calc2(b1);
                if (! iter.hasNext()) {
                    c3 = calc3(b1, 0);
                    state = 0x11;
                    return c0;
                }
                int b2 = iter.next();
                c3 = calc3(b1, b2);
                if (! iter.hasNext()) {
                    c4 = calc4(b2, 0);
                    state = 0x19;
                    return c0;
                }
                int b3 = iter.next();
                c4 = calc4(b2, b3);
                c5 = calc5(b3);
                if (! iter.hasNext()) {
                    c6 = calc6(b3, 0);
                    state = 0x21;
                    return c0;
                }
                int b4 = iter.next();
                c6 = calc6(b3, b4);
                c7 = calc7(b4);
                state = 1;
                return c0;
            }
            case 1:
            case 9:
            case 0x11:
            case 0x19:
            case 0x21: {
                return c0;
            }
            case 2:
            case 0x0a:
            case 0x12:
            case 0x1a:
            case 0x22: {
                return c1;
            }
            case 3:
            case 0x13:
            case 0x1b:
            case 0x23: {
                return c2;
            }
            case 4:
            case 0x14:
            case 0x1c:
            case 0x24: {
                return c3;
            }
            case 5:
            case 0x1d:
            case 0x25: {
                return c4;
            }
            case 6:
            case 0x26: {
                return c5;
            }
            case 7:
            case 0x27: {
                return c6;
            }
            case 8: {
                return c7;
            }
            case 0x0b:
            case 0x0c:
            case 0x0d:
            case 0x0e:
            case 0x0f:
            case 0x10:
            case 0x15:
            case 0x16:
            case 0x17:
            case 0x18:
            case 0x1e:
            case 0x1f:
            case 0x20:
            case 0x28: {
                return '=';
            }
            default: {
                throw Assert.impossibleSwitchCase(state);
            }
        }
    }

    public int previous() throws NoSuchElementException {
        if (! hasPrevious()) throw new NoSuchElementException();
        offset--;
        switch (state) {
            case 0x21: {
                iter.previous(); // skip and fall through
            }
            case 0x19: {
                iter.previous(); // skip and fall through
            }
            case 0x11: {
                iter.previous(); // skip and fall through
            }
            case 9: {
                iter.previous(); // skip and fall through
            }
            case 0:
            case 1:
            case 0x2d: {
                int b4 = iter.previous();
                int b3 = iter.previous();
                int b2 = iter.previous();
                int b1 = iter.previous();
                int b0 = iter.previous();
                c0 = calc0(b0);
                c1 = calc1(b0, b1);
                c2 = calc2(b1);
                c3 = calc3(b1, b2);
                c4 = calc4(b2, b3);
                c5 = calc5(b3);
                c6 = calc6(b3, b4);
                c7 = calc7(b4);
                state = 8;
                return c7;
            }
            case 2:
            case 0x0a:
            case 0x1a:
            case 0x12:
            case 0x22: {
                state--;
                return c0;
            }
            case 3:
            case 0x0b:
            case 0x13:
            case 0x1b:
            case 0x23: {
                state--;
                return c1;
            }
            case 4:
            case 0x14:
            case 0x1c:
            case 0x24: {
                state--;
                return c2;
            }
            case 5:
            case 0x15:
            case 0x1d:
            case 0x25: {
                state--;
                return c3;
            }
            case 6:
            case 0x1e:
            case 0x26: {
                state--;
                return c4;
            }
            case 7:
            case 0x27: {
                state--;
                return c5;
            }
            case 8:
            case 0x28: {
                state--;
                return c6;
            }
            case 0x0c:
            case 0x0d:
            case 0x0e:
            case 0x0f:
            case 0x10:
            case 0x16:
            case 0x17:
            case 0x18:
            case 0x1f:
            case 0x20: {
                state--;
                return '=';
            }
            case 0x29: {
                if (addPadding) {
                    state = 0x10;
                    return '=';
                } else {
                    state = 0x0a;
                    return c1;
                }
            }
            case 0x2a: {
                if (addPadding) {
                    state = 0x18;
                    return '=';
                } else {
                    state = 0x14;
                    return c3;
                }
            }
            case 0x2b: {
                if (addPadding) {
                    state = 0x20;
                    return '=';
                } else {
                    state = 0x1d;
                    return c4;
                }
            }
            case 0x2c: {
                if (addPadding) {
                    state = 0x28;
                    return '=';
                } else {
                    state = 0x27;
                    return c6;
                }
            }
            default: {
                throw Assert.impossibleSwitchCase(state);
            }
        }
    }

    public int peekPrevious() throws NoSuchElementException {
        if (! hasPrevious()) throw new NoSuchElementException();
        switch (state) {
            case 0x21:
                iter.previous(); // skip and fall through
            case 0x19:
                iter.previous(); // skip and fall through
            case 0x11:
                iter.previous(); // skip and fall through
            case 9:
                iter.previous(); // skip and fall through
            case 0:
            case 1:
            case 0x2d: {
                int result = calc7(iter.peekPrevious());
                if (state == 9) {
                    iter.next();
                } else if (state == 0x11) {
                    iter.next();
                    iter.next();
                } else if (state == 0x19) {
                    iter.next();
                    iter.next();
                    iter.next();
                } else if (state == 0x21) {
                    iter.next();
                    iter.next();
                    iter.next();
                    iter.next();
                }
                return result;
            }
            case 2:
            case 0x0a:
            case 0x1a:
            case 0x12:
            case 0x22: {
                return c0;
            }
            case 3:
            case 0x0b:
            case 0x13:
            case 0x1b:
            case 0x23: {
                return c1;
            }
            case 4:
            case 0x14:
            case 0x1c:
            case 0x24: {
                return c2;
            }
            case 5:
            case 0x15:
            case 0x1d:
            case 0x25: {
                return c3;
            }
            case 6:
            case 0x1e:
            case 0x26: {
                return c4;
            }
            case 7:
            case 0x27: {
                return c5;
            }
            case 8:
            case 0x28: {
                return c6;
            }
            case 0x0c:
            case 0x0d:
            case 0x0e:
            case 0x0f:
            case 0x10:
            case 0x16:
            case 0x17:
            case 0x18:
            case 0x1f:
            case 0x20: {
                return '=';
            }
            case 0x29: {
                return addPadding ? '=' : c1;
            }
            case 0x2a: {
                return addPadding ? '=' : c3;
            }
            case 0x2b: {
                return addPadding ? '=' : c4;
            }
            case 0x2c: {
                return addPadding ? '=' : c6;
            }
            default: {
                throw Assert.impossibleSwitchCase(state);
            }
        }
    }

    public long getIndex() {
        return offset;
    }
}
