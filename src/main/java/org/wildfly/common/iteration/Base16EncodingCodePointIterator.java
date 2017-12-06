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

/**
 */
final class Base16EncodingCodePointIterator extends CodePointIterator {
    private ByteIterator iter;
    private final boolean toUpperCase;
    int b;
    boolean lo;

    Base16EncodingCodePointIterator(final ByteIterator iter, final boolean toUpperCase) {
        this.iter = iter;
        this.toUpperCase = toUpperCase;
    }

    public boolean hasNext() {
        return lo || iter.hasNext();
    }

    public boolean hasPrevious() {
        return lo || iter.hasPrevious();
    }

    private int hex(final int i) {
        if (i < 10) {
            return '0' + i;
        } else {
            assert i < 16;
            return (toUpperCase ? 'A' : 'a') + i - 10;
        }
    }

    public int next() throws NoSuchElementException {
        if (! hasNext()) throw new NoSuchElementException();
        if (lo) {
            lo = false;
            return hex(b & 0xf);
        } else {
            b = iter.next();
            lo = true;
            return hex(b >> 4);
        }
    }

    public int peekNext() throws NoSuchElementException {
        if (! hasNext()) throw new NoSuchElementException();
        if (lo) {
            return hex(b & 0xf);
        } else {
            return hex(iter.peekNext() >> 4);
        }
    }

    public int previous() throws NoSuchElementException {
        if (! hasPrevious()) throw new NoSuchElementException();
        if (lo) {
            lo = false;
            iter.previous();
            return hex(b >> 4);
        } else {
            b = iter.peekPrevious();
            lo = true;
            return hex(b & 0xf);
        }
    }

    public int peekPrevious() throws NoSuchElementException {
        if (! hasPrevious()) throw new NoSuchElementException();
        if (lo) {
            return hex(b >> 4);
        } else {
            return hex(iter.peekPrevious() & 0xf);
        }
    }

    public long getIndex() {
        return iter.getIndex() * 2 + (lo ? 1 : 0);
    }
}
