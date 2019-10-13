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
