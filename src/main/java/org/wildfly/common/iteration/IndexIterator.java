package org.wildfly.common.iteration;

/**
 * An iterator which can report the current iterator index.
 */
public interface IndexIterator {
    /**
     * Get the current iterator index.
     *
     * @return the index
     */
    long getIndex();
}
