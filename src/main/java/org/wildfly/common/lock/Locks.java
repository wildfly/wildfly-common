package org.wildfly.common.lock;

import org.wildfly.common.annotation.NotNull;

/**
 * A utility class to create {@code ExtendedLock} objects.
 */
public final class Locks {
    private Locks() {}

    /**
     * Create a standard reentrant {@code ExtendedLock} with the default fairness policy.
     *
     * @return a reentrant {@code ExtendedLock}
     */
    public static @NotNull ExtendedLock reentrantLock() {
        return new ExtendedReentrantLock();
    }

    /**
     * Create a standard reentrant {@code ExtendedLock} with the given fairness policy.
     *
     * @param fair the fairness policy
     * @return a reentrant {@code ExtendedLock}
     */
    public static @NotNull ExtendedLock reentrantLock(boolean fair) {
        return new ExtendedReentrantLock(fair);
    }

    /**
     * Create a spin lock.
     *
     * @return the spin lock
     * @see SpinLock
     */
    public static @NotNull ExtendedLock spinLock() {
        return new SpinLock();
    }
}
