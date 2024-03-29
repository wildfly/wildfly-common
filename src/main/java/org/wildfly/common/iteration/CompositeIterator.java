/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2019 Red Hat, Inc., and individual contributors
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

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * Iterator that iterates over a series of iterators.
 * @author Paul Ferraro
 */
public class CompositeIterator<E> implements Iterator<E> {

    private final Iterable<? extends Iterator<? extends E>> iterators;
    private Iterator<? extends E> lastIterator;

    /**
     * Constructs a new composite iterator.
     * @param iterators a series of iterators
     */
    @SafeVarargs
    public CompositeIterator(Iterator<? extends E>... iterators) {
        this(Arrays.asList(iterators));
    }

    /**
     * Constructs a new composite iterator.
     * @param iterators a series of iterators
     */
    public CompositeIterator(Iterable<? extends Iterator<? extends E>> iterators) {
        this.iterators = iterators;
    }

    @Override
    public boolean hasNext() {
        for (Iterator<? extends E> iterator : this.iterators) {
            if (iterator.hasNext()) return true;
        }
        return false;
    }

    @Override
    public E next() {
        for (Iterator<? extends E> iterator : this.iterators) {
            if (iterator.hasNext()) {
                this.lastIterator = iterator;
                return iterator.next();
            }
        }
        throw new NoSuchElementException();
    }

    @Override
    public void remove() {
        Iterator<? extends E> iterator = this.lastIterator;
        if (iterator == null) {
            throw new IllegalStateException();
        }
        iterator.remove();
    }

    @Override
    public void forEachRemaining(Consumer<? super E> action) {
        for (Iterator<? extends E> iterator : this.iterators) {
            while (iterator.hasNext()) {
                action.accept(iterator.next());
            }
        }
    }
}
