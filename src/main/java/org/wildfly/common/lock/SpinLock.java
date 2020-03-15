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

import static org.wildfly.common.lock.JDKSpecific.unsafe;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

import org.wildfly.common.Assert;
import org.wildfly.common.cpu.ProcessorInfo;

/**
 * A spin lock.  Such locks are designed to only be held for a <em>very</em> short time - for example, long enough to compare and
 * swap two fields.  The lock may degrade to yielding the thread after a certain number of spins if it is held for too long.
 * <p>
 * Spin locks do not support conditions, and they do not support timed waiting.  Normally only the uninterruptible {@code lock},
 * {@code tryLock}, and {@code unlock} methods should be used to control the lock.
 */
public class SpinLock implements ExtendedLock {
    private static final long ownerOffset;
    private static final int defaultSpinLimit;

    static {
        try {
            ownerOffset = unsafe.objectFieldOffset(SpinLock.class.getDeclaredField("owner"));
        } catch (NoSuchFieldException e) {
            throw new NoSuchFieldError(e.getMessage());
        }
        defaultSpinLimit = AccessController.doPrivileged(
            (PrivilegedAction<Integer>) () -> Integer.valueOf(
                System.getProperty("jboss.spin-lock.limit", ProcessorInfo.availableProcessors() == 1 ? "0" : "5000")
            )
        ).intValue();
    }

    @SuppressWarnings("unused")
    private volatile Thread owner;

    private int level;

    private final int spinLimit;

    /**
     * Construct a new instance.
     */
    public SpinLock() {
        this(defaultSpinLimit);
    }

    /**
     * Construct a new instance with the given spin limit.
     *
     * @param spinLimit the spin limit to use for this instance
     */
    public SpinLock(final int spinLimit) {
        Assert.checkMinimumParameter("spinLimit", 0, spinLimit);
        this.spinLimit = spinLimit;
    }

    /**
     * Determine if this spin lock is held.  Useful for assertions.
     *
     * @return {@code true} if the lock is held by any thread, {@code false} otherwise
     */
    public boolean isLocked() {
        return owner != null;
    }

    /**
     * Determine if this spin lock is held by the calling thread.  Useful for assertions.
     *
     * @return {@code true} if the lock is held by the calling thread, {@code false} otherwise
     */
    public boolean isHeldByCurrentThread() {
        return owner == Thread.currentThread();
    }

    /**
     * Determine if this lock is fair.
     *
     * @return {@code true}; the lock is fair
     */
    public boolean isFair() {
        return true;
    }

    /**
     * Acquire the lock by spinning until it is held.
     */
    public void lock() {
        Thread current = Thread.currentThread();
        if (this.owner == current) {
            level++;
            return;
        }
        int spins = spinLimit;
        while (!unsafe.compareAndSwapObject(this, ownerOffset, null, current)) {
            if (spins == 0) {
                Thread.yield();
            } else {
                JDKSpecific.onSpinWait();
                spins--;
            }
        }
        level = 1;
    }

    /**
     * Acquire the lock by spinning until it is held or the thread is interrupted.
     *
     * @throws InterruptedException if the thread is interrupted before the lock can be acquired
     */
    public void lockInterruptibly() throws InterruptedException {
        if (Thread.interrupted()) throw new InterruptedException();
        Thread current = Thread.currentThread();
        if (this.owner == current) {
            level++;
            return;
        }
        int spins = spinLimit;
        while (!unsafe.compareAndSwapObject(this, ownerOffset, null, current)) {
            if (Thread.interrupted()) throw new InterruptedException();
            if (spins == 0) {
                Thread.yield();
            } else {
                JDKSpecific.onSpinWait();
                spins--;
            }
        }
        level = 1;
    }

    /**
     * Try to acquire the lock, returning immediately whether or not the lock was acquired.
     *
     * @return {@code true} if the lock was acquired, {@code false} otherwise
     */
    public boolean tryLock() {
        if (owner == Thread.currentThread()) {
            level++;
            return true;
        } else if (unsafe.compareAndSwapObject(this, ownerOffset, null, Thread.currentThread())) {
            level = 1;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Release the lock.
     *
     * @throws IllegalMonitorStateException if the lock is not held by the current thread
     */
    public void unlock() {
        if (owner == Thread.currentThread()) {
            if (--level == 0) this.owner = null;
        } else {
            throw new IllegalMonitorStateException();
        }
    }

    /**
     * Unsupported.
     *
     * @param time ignored
     * @param unit ignored
     * @return nothing
     * @throws UnsupportedOperationException always
     */
    public boolean tryLock(final long time, final TimeUnit unit) throws UnsupportedOperationException {
        throw Assert.unsupported();
    }

    /**
     * Unsupported.
     *
     * @return nothing
     * @throws UnsupportedOperationException always
     */
    public Condition newCondition() throws UnsupportedOperationException {
        throw Assert.unsupported();
    }
}
