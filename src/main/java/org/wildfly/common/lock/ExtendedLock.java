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
