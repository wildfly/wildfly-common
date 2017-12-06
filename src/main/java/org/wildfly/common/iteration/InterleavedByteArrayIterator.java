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
final class InterleavedByteArrayIterator extends ByteIterator {
    private final int len;
    private final byte[] bytes;
    private final int offs;
    private final int[] interleave;
    private int idx;

    InterleavedByteArrayIterator(final int len, final byte[] bytes, final int offs, final int[] interleave) {
        this.len = len;
        this.bytes = bytes;
        this.offs = offs;
        this.interleave = interleave;
        idx = 0;
    }

    public boolean hasNext() {
        return idx < len;
    }

    public boolean hasPrevious() {
        return idx > 0;
    }

    public int next() {
        if (! hasNext()) throw new NoSuchElementException();
        return bytes[offs + interleave[idx++]] & 0xff;
    }

    public int previous() {
        if (! hasPrevious()) throw new NoSuchElementException();
        return bytes[offs + interleave[-- idx]] & 0xff;
    }

    public int peekNext() throws NoSuchElementException {
        if (! hasNext()) throw new NoSuchElementException();
        return bytes[offs + interleave[idx]] & 0xff;
    }

    public int peekPrevious() throws NoSuchElementException {
        if (! hasPrevious()) throw new NoSuchElementException();
        return bytes[offs + interleave[idx - 1]] & 0xff;
    }

    public long getIndex() {
        return idx;
    }
}
