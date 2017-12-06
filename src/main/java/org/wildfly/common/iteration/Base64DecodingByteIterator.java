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

import static org.wildfly.common._private.CommonMessages.msg;

import java.util.NoSuchElementException;

/**
 */
abstract class Base64DecodingByteIterator extends ByteIterator {
    private final CodePointIterator iter;
    private final boolean requirePadding;
    // states:
    // 0: nothing read
    // 1: three bytes to return o0..2
    // 2: two bytes to return o1..2 (o0 still populated)
    // 3: one byte to return o2 (o0..o1 still populated)
    // 4: two bytes then eof o0..1 =
    // 5: one bytes then eof o1 = (o0 still populated)
    // 6: one byte then eof o0 ==
    // 7: two bytes then eof o0..1 no pad
    // 8: one byte then eof o1 no pad (o0 still populated)
    // 9: one byte then eof o0 no pad
    // a: end (==) (o0 still populated)
    // b: end (=) (o0..o1 still populated)
    // c: end (== but no pad) (o0 still populated)
    // d: end (= but no pad) (o0..o1 still populated)
    private int state = 0;
    private int o0, o1, o2;
    private int offset;

    Base64DecodingByteIterator(final CodePointIterator iter, final boolean requirePadding) {
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
                state = 9;
                return true;
            }
            int b2 = iter.next();
            if (b2 == '=') {
                if (! iter.hasNext()) {
                    throw msg.expectedTwoPaddingCharacters();
                }
                if (iter.next() != '=') {
                    throw msg.expectedTwoPaddingCharacters();
                }
                state = 6;
                return true;
            }
            o1 = calc1(b1, b2);
            if (! iter.hasNext()) {
                if (requirePadding) {
                    throw msg.expectedPadding();
                }
                state = 7;
                return true;
            }
            int b3 = iter.next();
            if (b3 == '=') {
                state = 4;
                return true;
            }
            o2 = calc2(b2, b3);
            state = 1;
            return true;
        } else {
            return state < 0xa;
        }
    }

    public boolean hasPrevious() {
        return state != 0 || offset > 0;
    }

    abstract int calc0(int b0, int b1);

    abstract int calc1(int b1, int b2);

    abstract int calc2(int b2, int b3);

    public int next() {
        if (! hasNext()) {
            throw new NoSuchElementException();
        }
        switch (state) {
            case 1: {
                state = 2;
                offset++;
                return o0;
            }
            case 2: {
                state = 3;
                offset++;
                return o1;
            }
            case 3: {
                state = 0;
                offset++;
                return o2;
            }
            case 4: {
                state = 5;
                offset++;
                return o0;
            }
            case 5: {
                state = 0xb;
                offset++;
                return o1;
            }
            case 6: {
                state = 0xa;
                offset++;
                return o0;
            }
            case 7: {
                state = 8;
                offset++;
                return o0;
            }
            case 8: {
                state = 0xd;
                offset++;
                return o1;
            }
            case 9: {
                state = 0xc;
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
            case 4:
            case 6:
            case 7:
            case 9: {
                return o0;
            }
            case 2:
            case 5:
            case 8: {
                return o1;
            }
            case 3: {
                return o2;
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
        switch (state) {
            case 6: {
                iter.previous(); // skip =
                // fall thru
            }
            case 4: {
                iter.previous(); // skip =
                // fall thru
            }
            case 0:
            case 1:
            case 7:
            case 9: {
                int b3 = iter.previous();
                int b2 = iter.previous();
                int b1 = iter.previous();
                int b0 = iter.previous();
                o0 = calc0(b0, b1);
                o1 = calc1(b1, b2);
                state = 3;
                offset--;
                return o2 = calc2(b2, b3);
            }
            case 2: {
                state = 1;
                offset--;
                return o0;
            }
            case 3: {
                state = 2;
                offset--;
                return o1;
            }
            case 5: {
                state = 4;
                offset--;
                return o0;
            }
            case 8: {
                state = 7;
                offset--;
                return o0;
            }
            case 0xa: {
                state = 6;
                offset--;
                return o0;
            }
            case 0xb: {
                state = 5;
                offset--;
                return o1;
            }
            case 0xc: {
                state = 9;
                offset--;
                return o0;
            }
            case 0xd: {
                state = 8;
                offset--;
                return o1;
            }
            default: {
                // padding
                throw new NoSuchElementException();
            }
        }
    }

    public int peekPrevious() throws NoSuchElementException {
        if (! hasPrevious()) {
            throw new NoSuchElementException();
        }
        switch (state) {
            case 6: {
                iter.previous(); // skip =
                // fall thru
            }
            case 4: {
                iter.previous(); // skip =
                // fall thru
            }
            case 0:
            case 1:
            case 7:
            case 9: {
                int b3 = iter.previous();
                int b2 = iter.peekPrevious();
                iter.next();
                if (state == 4) {
                    iter.next();
                } else if (state == 6) {
                    iter.next();
                    iter.next();
                }
                return calc2(b2, b3);
            }
            case 2: {
                return o0;
            }
            case 3: {
                return o1;
            }
            case 5: {
                return o0;
            }
            case 8: {
                return o0;
            }
            case 0xa: {
                return o0;
            }
            case 0xb: {
                return o1;
            }
            case 0xc: {
                return o0;
            }
            case 0xd: {
                return o1;
            }
            default: {
                // padding
                throw new NoSuchElementException();
            }
        }
    }

    public long getIndex() {
        return offset;
    }
}
