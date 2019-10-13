package org.wildfly.common.iteration;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An enumeration which is also an iterator.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public interface EnumerationIterator<E> extends Enumeration<E>, Iterator<E> {
    /**
     * Determine if there are more elements to iterate over in the direction of this iterator.
     *
     * @return {@code true} if there are more elements, {@code false} otherwise
     */
    default boolean hasMoreElements() {
        return hasNext();
    }

    /**
     * Get the next element in the direction of this iterator.
     *
     * @return the next element
     */
    default E nextElement() {
        return next();
    }

    /**
     * Get an enumeration iterator over one element.
     *
     * @param item the element
     * @param <E> the element type
     * @return the enumeration iterator
     */
    static <E> EnumerationIterator<E> over(E item) {
        return new EnumerationIterator<E>() {
            boolean done;
            public boolean hasNext() {
                return ! done;
            }

            public E next() {
                if (! hasNext()) throw new NoSuchElementException();
                done = true;
                return item;
            }
        };
    }
}
