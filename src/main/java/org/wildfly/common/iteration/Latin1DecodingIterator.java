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
final class Latin1DecodingIterator extends CodePointIterator {
    private final ByteIterator iter;
    private final long start;

    Latin1DecodingIterator(final ByteIterator iter, final long start) {
        this.iter = iter;
        this.start = start;
    }

    public boolean hasNext() {
        return iter.hasNext();
    }

    public boolean hasPrevious() {
        return start > 0 && iter.hasPrevious();
    }

    public int next() {
        return iter.next();
    }

    public int peekNext() throws NoSuchElementException {
        return iter.peekNext();
    }

    public int previous() {
        if (start == 0) throw new NoSuchElementException();
        return iter.previous();
    }

    public int peekPrevious() throws NoSuchElementException {
        return iter.peekPrevious();
    }

    public long getIndex() {
        return iter.getIndex() - start;
    }
}
