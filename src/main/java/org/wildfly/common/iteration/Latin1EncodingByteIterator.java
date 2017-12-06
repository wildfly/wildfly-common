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
final class Latin1EncodingByteIterator extends ByteIterator {
    private final CodePointIterator iter;

    Latin1EncodingByteIterator(final CodePointIterator iter) {
        this.iter = iter;
    }

    public boolean hasNext() {
        return iter.hasNext();
    }

    public boolean hasPrevious() {
        return iter.hasPrevious();
    }

    public int next() throws NoSuchElementException {
        final int v = iter.next();
        return v > 255 ? '?' : v;
    }

    public int peekNext() throws NoSuchElementException {
        final int v = iter.peekNext();
        return v > 255 ? '?' : v;
    }

    public int previous() throws NoSuchElementException {
        final int v = iter.previous();
        return v > 255 ? '?' : v;
    }

    public int peekPrevious() throws NoSuchElementException {
        final int v = iter.peekPrevious();
        return v > 255 ? '?' : v;
    }

    public long getIndex() {
        return iter.getIndex();
    }
}
