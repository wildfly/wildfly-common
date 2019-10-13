package org.wildfly.common.iteration;

import java.util.NoSuchElementException;

/**
 * A bi-directional primitive iterator.
 */
public interface BiDirIntIterator extends IntIterator {
    boolean hasNext();

    int next() throws NoSuchElementException;

    int peekNext() throws NoSuchElementException;

    /**
     * Determine if there is a previous element in this sequence.
     *
     * @return {@code true} if there is a previous element, {@code false} otherwise
     */
    boolean hasPrevious();

    /**
     * Get the previous element in the sequence.
     *
     * @return the previous element
     * @throws NoSuchElementException if there are no more elements
     */
    int previous() throws NoSuchElementException;

    /**
     * Observe the previous element in the sequence without moving the iterator.
     *
     * @return the previous element
     * @throws NoSuchElementException if there are no more elements
     */
    int peekPrevious() throws NoSuchElementException;
}
