/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2018 Red Hat, Inc., and individual contributors
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
