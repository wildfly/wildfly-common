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
final class DelimitedCodePointIterator extends CodePointIterator {
    private final CodePointIterator iter;
    private final int[] delims;
    long offset;

    DelimitedCodePointIterator(final CodePointIterator iter, final int... delims) {
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
