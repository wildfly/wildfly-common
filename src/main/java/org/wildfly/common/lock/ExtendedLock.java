package org.wildfly.common.lock;

import java.util.concurrent.locks.Lock;

/**
 * A thread-owned lock which exposes additional informational methods.
 */
public interface ExtendedLock extends Lock {

    /**
     * Determine if this lock is held.
     *
     * @return {@code true} if this lock is held, {@code false} otherwise
     */
    boolean isLocked();

    /**
     * Determine if this lock is held by the current thread.
     *
     * @return {@code true} if this lock is held by the current thread, {@code false} otherwise
     */
    boolean isHeldByCurrentThread();

    /**
     * Query if this lock instance tends to be "fair".
     *
     * @return {@code true} if the lock instance tends towards fairness, {@code false} otherwise
     */
    boolean isFair();
}
