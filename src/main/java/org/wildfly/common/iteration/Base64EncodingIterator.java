/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2017 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wildfly.common.iteration;

import java.util.NoSuchElementException;

import org.wildfly.common.Assert;

/**
 */
abstract class Base64EncodingIterator extends CodePointIterator {

    private final ByteIterator iter;
    private final boolean addPadding;
    private int c0, c1, c2, c3;
    private int state;
    private int offset;

    Base64EncodingIterator(final ByteIterator iter, final boolean addPadding) {
        this.iter = iter;
        this.addPadding = addPadding;
    }

    // states:
    // 0 - need another three data bytes
    // 1 - 4 characters to read
    // 2 - 3 characters to read
    // 3 - 2 characters to read
    // 4 - 1 character to read
    // 5 - 2 characters + == to read
    // 6 - 1 character (c1) + == to read
    // 7 - == to read
    // 8 - second = to read
    // 9 - 3 characters + = to read
    // a - 2 characters (c1, c2) + = to read
    // b - 1 character (c2) + = to read
    // c - = to read
    // d - after ==
    // e - after =
    // f - clean end

    public boolean hasNext() {
        return state == 0 && iter.hasNext() || state > 0 && state < 0xd;
    }

    public boolean hasPrevious() {
        return offset > 0;
    }

    abstract int calc0(int b0);

    abstract int calc1(int b0, int b1);

    abstract int calc2(int b1, int b2);

    abstract int calc3(int b2);

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
                    state = 6;
                    return c0;
                }
                int b1 = iter.next();
                c1 = calc1(b0, b1);
                if (! iter.hasNext()) {
                    c2 = calc2(b1, 0);
                    state = 0xa;
                    return c0;
                }
                int b2 = iter.next();
                c2 = calc2(b1, b2);
                c3 = calc3(b2);
                state = 2;
                return c0;
            }
            case 1: {
                state = 2;
                return c0;
            }
            case 2: {
                state = 3;
                return c1;
            }
            case 3: {
                state = 4;
                return c2;
            }
            case 4: {
                state = 0;
                return c3;
            }
            case 5: {
                state = 6;
                return c0;
            }
            case 6: {
                state = addPadding ? 7 : 0xd;
                return c1;
            }
            case 7: {
                state = 8;
                return '=';
            }
            case 8: {
                state = 0xd;
                return '=';
            }
            case 9: {
                state = 0xa;
                return c0;
            }
            case 0xa: {
                state = 0xb;
                return c1;
            }
            case 0xb: {
                state = addPadding ? 0xc : 0xe;
                return c2;
            }
            case 0xc: {
                state = 0xe;
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
                    state = 5;
                    return c0;
                }
                int b1 = iter.next();
                c1 = calc1(b0, b1);
                if (! iter.hasNext()) {
                    c2 = calc2(b1, 0);
                    state = 9;
                    return c0;
                }
                int b2 = iter.next();
                c2 = calc2(b1, b2);
                c3 = calc3(b2);
                state = 1;
                return c0;
            }
            case 1: {
                return c0;
            }
            case 2: {
                return c1;
            }
            case 3: {
                return c2;
            }
            case 4: {
                return c3;
            }
            case 5: {
                return c0;
            }
            case 6: {
                return c1;
            }
            case 7: {
                return '=';
            }
            case 8: {
                return '=';
            }
            case 9: {
                return c0;
            }
            case 0xa: {
                return c1;
            }
            case 0xb: {
                return c2;
            }
            case 0xc: {
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
            case 0:
            case 1:
            case 5:
            case 9:
            case 0xf: {
                int b2 = iter.previous();
                int b1 = iter.previous();
                int b0 = iter.previous();
                c0 = calc0(b0);
                c1 = calc1(b0, b1);
                c2 = calc2(b1, b2);
                c3 = calc3(b2);
                state = 4;
                return c3;
            }
            case 2: {
                state = 1;
                return c0;
            }
            case 3: {
                state = 2;
                return c1;
            }
            case 4: {
                state = 3;
                return c2;
            }
            case 6: {
                state = 5;
                return c0;
            }
            case 7: {
                state = 6;
                return c1;
            }
            case 8: {
                state = 7;
                return '=';
            }
            case 0xa: {
                state = 9;
                return c0;
            }
            case 0xb: {
                state = 0xa;
                return c1;
            }
            case 0xc: {
                state = 0xb;
                return c2;
            }
            case 0xd: {
                state = 8;
                return '=';
            }
            case 0xe: {
                state = 0xc;
                return '=';
            }
            default: {
                throw Assert.impossibleSwitchCase(state);
            }
        }
    }

    public int peekPrevious() throws NoSuchElementException {
        if (! hasPrevious()) throw new NoSuchElementException();
        switch (state) {
            case 0:
            case 1:
            case 5:
            case 9:
            case 0xf: {
                return calc3(iter.peekPrevious());
            }
            case 2: {
                return c0;
            }
            case 3: {
                return c1;
            }
            case 4: {
                return c2;
            }
            case 6: {
                return c0;
            }
            case 7: {
                return c1;
            }
            case 8: {
                return '=';
            }
            case 0xa: {
                return c0;
            }
            case 0xb: {
                return c1;
            }
            case 0xc: {
                return c2;
            }
            case 0xd: {
                return '=';
            }
            case 0xe: {
                return '=';
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
