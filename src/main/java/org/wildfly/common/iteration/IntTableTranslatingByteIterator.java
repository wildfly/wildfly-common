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
final class IntTableTranslatingByteIterator extends ByteIterator {
    private final ByteIterator iter;
    private final int[] table;

    IntTableTranslatingByteIterator(final ByteIterator iter, final int[] table) {
        this.iter = iter;
        this.table = table;
    }

    public boolean hasNext() {
        return iter.hasNext();
    }

    public boolean hasPrevious() {
        return iter.hasPrevious();
    }

    public int next() throws NoSuchElementException {
        return table[iter.next()] & 0xff;
    }

    public int peekNext() throws NoSuchElementException {
        return table[iter.peekNext()] & 0xff;
    }

    public int previous() throws NoSuchElementException {
        return table[iter.previous()] & 0xff;
    }

    public int peekPrevious() throws NoSuchElementException {
        return table[iter.peekPrevious()] & 0xff;
    }

    public long getIndex() {
        return iter.getIndex();
    }
}
