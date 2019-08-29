package org.wildfly.common.ref;

/**
 * A cleaner for a dead object.
 *
 * @param <T> the reference type
 * @param <A> the reference attachment type
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public interface Reaper<T, A> {

    /**
     * Perform the cleanup action for a reference.
     *
     * @param reference the reference
     */
    void reap(Reference<T, A> reference);
}
