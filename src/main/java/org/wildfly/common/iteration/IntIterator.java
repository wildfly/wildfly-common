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
 * A primitive iterator, which can be used as the basis for string parsing, tokenizing, and other purposes.
 */
public interface IntIterator {
    /**
     * Determine if there is another element in this sequence.
     *
     * @return {@code true} if there is another element, {@code false} otherwise
     */
    boolean hasNext();

    /**
     * Get the next element in the sequence.
     *
     * @return the next element
     * @throws NoSuchElementException if there are no more elements
     */
    int next() throws NoSuchElementException;

    /**
     * Observe the next element in the sequence without moving the iterator.
     *
     * @return the next element
     * @throws NoSuchElementException if there are no more elements
     */
    int peekNext() throws NoSuchElementException;
}
