package org.wildfly.common.ref;

/**
 * A reference which is reapable (can be automatically collected).
 *
 * @param <T> the reference type
 * @param <A> the reference attachment type
 *
 * @apiviz.exclude
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
interface Reapable<T, A> {

    /**
     * Get the associated reaper.
     *
     * @return the reaper
     */
    Reaper<T, A> getReaper();
}
