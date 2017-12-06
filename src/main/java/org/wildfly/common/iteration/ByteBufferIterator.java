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

import java.nio.ByteBuffer;
import java.util.NoSuchElementException;

/**
 */
final class ByteBufferIterator extends ByteIterator {
    private final ByteBuffer buffer;
    private final int initialPosition;

    ByteBufferIterator(final ByteBuffer buffer) {
        this.buffer = buffer;
        initialPosition = buffer.position();
    }

    public boolean hasNext() {
        return buffer.hasRemaining();
    }

    public boolean hasPrevious() {
        return buffer.position() > initialPosition;
    }

    public int next() throws NoSuchElementException {
        if (! hasNext()) throw new NoSuchElementException();
        return buffer.get() & 0xff;
    }

    public int peekNext() throws NoSuchElementException {
        if (! hasNext()) throw new NoSuchElementException();
        return buffer.get(buffer.position()) & 0xff;
    }

    public int previous() throws NoSuchElementException {
        if (! hasPrevious()) throw new NoSuchElementException();
        buffer.position(buffer.position() - 1);
        return peekNext();
    }

    public int peekPrevious() throws NoSuchElementException {
        if (! hasPrevious()) throw new NoSuchElementException();
        return buffer.get(buffer.position() - 1) & 0xff;
    }

    public long getIndex() {
        return buffer.position() - initialPosition;
    }
}
