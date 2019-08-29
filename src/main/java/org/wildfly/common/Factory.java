package org.wildfly.common;

import java.util.function.Supplier;

/**
 * An object which produces another object.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 * @see Supplier
 */
public interface Factory<T> {

    /**
     * Create the object.
     *
     * @return the object
     */
    T create();
}
