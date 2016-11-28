/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2016 Red Hat, Inc., and individual contributors
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

package org.wildfly.common.context;

import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.LongFunction;
import java.util.function.Predicate;

import org.wildfly.common.Assert;
import org.wildfly.common._private.CommonMessages;
import org.wildfly.common.annotation.NotNull;
import org.wildfly.common.function.ExceptionBiConsumer;
import org.wildfly.common.function.ExceptionBiFunction;
import org.wildfly.common.function.ExceptionBiPredicate;
import org.wildfly.common.function.ExceptionConsumer;
import org.wildfly.common.function.ExceptionFunction;
import org.wildfly.common.function.ExceptionIntFunction;
import org.wildfly.common.function.ExceptionLongFunction;
import org.wildfly.common.function.ExceptionPredicate;

/**
 * A base class for contexts which are activated in a thread-local context.
 *
 * @param <C> the public type of the contextual object
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public interface Contextual<C extends Contextual<C>> {
    /**
     * Get the context manager for this object.  The implementation of this method normally should return a constant
     * instance.
     *
     * @return the context manager (must not be {@code null})
     */
    @NotNull
    ContextManager<C> getInstanceContextManager();

    /**
     * Run the given task with this contextual object selected.
     *
     * @param runnable the task to run (must not be {@code null})
     */
    default void run(Runnable runnable) {
        Assert.checkNotNullParam("runnable", runnable);
        final ContextManager<C> contextManager = getInstanceContextManager();
        final C old = contextManager.getAndSetCurrent(this);
        try {
            runnable.run();
        } finally {
            contextManager.restoreCurrent(old);
        }
    }

    /**
     * Run the given task with this contextual object selected.
     *
     * @param action the task to run (must not be {@code null})
     * @param <R> the return value type
     * @return the action return value
     */
    default <R> R runAction(PrivilegedAction<R> action) {
        final ContextManager<C> contextManager = getInstanceContextManager();
        final C old = contextManager.getAndSetCurrent(this);
        try {
            return action.run();
        } finally {
            contextManager.restoreCurrent(old);
        }
    }

    /**
     * Run the given task with this contextual object selected.
     *
     * @param action the task to run (must not be {@code null})
     * @param <R> the return value type
     * @return the action return value
     * @throws PrivilegedActionException if the action fails with an exception
     */
    default <R> R runExceptionAction(PrivilegedExceptionAction<R> action) throws PrivilegedActionException {
        final ContextManager<C> contextManager = getInstanceContextManager();
        final C old = contextManager.getAndSetCurrent(this);
        try {
            return action.run();
        } catch (Exception e) {
            throw CommonMessages.msg.privilegedActionFailed(e);
        } finally {
            contextManager.restoreCurrent(old);
        }
    }

    /**
     * Run the given task with this contextual object selected.
     *
     * @param callable the task to run (must not be {@code null})
     * @param <V> the return value type
     * @return the action return value
     */
    default <V> V runCallable(Callable<V> callable) throws Exception {
        final ContextManager<C> contextManager = getInstanceContextManager();
        final C old = contextManager.getAndSetCurrent(this);
        try {
            return callable.call();
        } finally {
            contextManager.restoreCurrent(old);
        }
    }

    /**
     * Run the given task with this contextual object selected.
     *
     * @param consumer the task to run (must not be {@code null})
     * @param param1 the first parameter to pass to the task
     * @param param2 the second parameter to pass to the task
     * @param <T> the first parameter type
     * @param <U> the second parameter type
     */
    default <T, U> void runBiConsumer(BiConsumer<T, U> consumer, T param1, U param2) {
        final ContextManager<C> contextManager = getInstanceContextManager();
        final C old = contextManager.getAndSetCurrent(this);
        try {
            consumer.accept(param1, param2);
        } finally {
            contextManager.restoreCurrent(old);
        }
    }

    /**
     * Run the given task with this contextual object selected.
     *
     * @param consumer the task to run (must not be {@code null})
     * @param param1 the first parameter to pass to the task
     * @param param2 the second parameter to pass to the task
     * @param <T> the first parameter type
     * @param <U> the second parameter type
     * @param <E> the exception type
     * @throws E if an exception occurs in the task
     */
    default <T, U, E extends Exception> void runExBiConsumer(ExceptionBiConsumer<T, U, E> consumer, T param1, U param2) throws E {
        final ContextManager<C> contextManager = getInstanceContextManager();
        final C old = contextManager.getAndSetCurrent(this);
        try {
            consumer.accept(param1, param2);
        } finally {
            contextManager.restoreCurrent(old);
        }
    }

    /**
     * Run the given task with this contextual object selected.
     *
     * @param consumer the task to run (must not be {@code null})
     * @param param the parameter to pass to the task
     * @param <T> the parameter type
     */
    default <T> void runConsumer(Consumer<T> consumer, T param) {
        final ContextManager<C> contextManager = getInstanceContextManager();
        final C old = contextManager.getAndSetCurrent(this);
        try {
            consumer.accept(param);
        } finally {
            contextManager.restoreCurrent(old);
        }
    }

    /**
     * Run the given task with this contextual object selected.
     *
     * @param consumer the task to run (must not be {@code null})
     * @param param the parameter to pass to the task
     * @param <T> the parameter type
     * @param <E> the exception type
     * @throws E if an exception occurs in the task
     */
    default <T, E extends Exception> void runExConsumer(ExceptionConsumer<T, E> consumer, T param) throws E {
        final ContextManager<C> contextManager = getInstanceContextManager();
        final C old = contextManager.getAndSetCurrent(this);
        try {
            consumer.accept(param);
        } finally {
            contextManager.restoreCurrent(old);
        }
    }

    /**
     * Run the given task with this contextual object selected.
     *
     * @param function the task to run (must not be {@code null})
     * @param param1 the first parameter to pass to the task
     * @param param2 the second parameter to pass to the task
     * @param <T> the first parameter type
     * @param <U> the second parameter type
     * @param <R> the return value type
     * @return the action return value
     */
    default <T, U, R> R runBiFunction(BiFunction<T, U, R> function, T param1, U param2) {
        final ContextManager<C> contextManager = getInstanceContextManager();
        final C old = contextManager.getAndSetCurrent(this);
        try {
            return function.apply(param1, param2);
        } finally {
            contextManager.restoreCurrent(old);
        }
    }

    /**
     * Run the given task with this contextual object selected.
     *
     * @param function the task to run (must not be {@code null})
     * @param param1 the first parameter to pass to the task
     * @param param2 the second parameter to pass to the task
     * @param <T> the first parameter type
     * @param <U> the second parameter type
     * @param <R> the return value type
     * @param <E> the exception type
     * @return the action return value
     * @throws E if an exception occurs in the task
     */
    default <T, U, R, E extends Exception> R runExBiFunction(ExceptionBiFunction<T, U, R, E> function, T param1, U param2) throws E {
        final ContextManager<C> contextManager = getInstanceContextManager();
        final C old = contextManager.getAndSetCurrent(this);
        try {
            return function.apply(param1, param2);
        } finally {
            contextManager.restoreCurrent(old);
        }
    }

    /**
     * Run the given task with this contextual object selected.
     *
     * @param function the task to run (must not be {@code null})
     * @param param the parameter to pass to the task
     * @param <T> the parameter type
     * @param <R> the return value type
     * @return the action return value
     */
    default <T, R> R runFunction(Function<T, R> function, T param) {
        final ContextManager<C> contextManager = getInstanceContextManager();
        final C old = contextManager.getAndSetCurrent(this);
        try {
            return function.apply(param);
        } finally {
            contextManager.restoreCurrent(old);
        }
    }

    /**
     * Run the given task with this contextual object selected.
     *
     * @param function the task to run (must not be {@code null})
     * @param param the parameter to pass to the task
     * @param <T> the parameter type
     * @param <R> the return value type
     * @param <E> the exception type
     * @return the action return value
     * @throws E if an exception occurs in the task
     */
    default <T, R, E extends Exception> R runExFunction(ExceptionFunction<T, R, E> function, T param) throws E {
        final ContextManager<C> contextManager = getInstanceContextManager();
        final C old = contextManager.getAndSetCurrent(this);
        try {
            return function.apply(param);
        } finally {
            contextManager.restoreCurrent(old);
        }
    }

    /**
     * Run the given task with this contextual object selected.
     *
     * @param predicate the task to run (must not be {@code null})
     * @param param1 the first parameter to pass to the task
     * @param param2 the second parameter to pass to the task
     * @param <T> the first parameter type
     * @param <U> the second parameter type
     * @return the action return value
     */
    default <T, U> boolean runBiPredicate(BiPredicate<T, U> predicate, T param1, U param2) {
        final ContextManager<C> contextManager = getInstanceContextManager();
        final C old = contextManager.getAndSetCurrent(this);
        try {
            return predicate.test(param1, param2);
        } finally {
            contextManager.restoreCurrent(old);
        }
    }

    /**
     * Run the given task with this contextual object selected.
     *
     * @param predicate the task to run (must not be {@code null})
     * @param param1 the first parameter to pass to the task
     * @param param2 the second parameter to pass to the task
     * @param <T> the first parameter type
     * @param <U> the second parameter type
     * @param <E> the exception type
     * @return the action return value
     * @throws E if an exception occurs in the task
     */
    default <T, U, E extends Exception> boolean runExBiPredicate(ExceptionBiPredicate<T, U, E> predicate, T param1, U param2) throws E {
        final ContextManager<C> contextManager = getInstanceContextManager();
        final C old = contextManager.getAndSetCurrent(this);
        try {
            return predicate.test(param1, param2);
        } finally {
            contextManager.restoreCurrent(old);
        }
    }

    /**
     * Run the given task with this contextual object selected.
     *
     * @param predicate the task to run (must not be {@code null})
     * @param param the parameter to pass to the task
     * @param <T> the first parameter type
     * @return the action return value
     */
    default <T> boolean runPredicate(Predicate<T> predicate, T param) {
        final ContextManager<C> contextManager = getInstanceContextManager();
        final C old = contextManager.getAndSetCurrent(this);
        try {
            return predicate.test(param);
        } finally {
            contextManager.restoreCurrent(old);
        }
    }

    /**
     * Run the given task with this contextual object selected.
     *
     * @param predicate the task to run (must not be {@code null})
     * @param param the parameter to pass to the task
     * @param <T> the first parameter type
     * @param <E> the exception type
     * @return the action return value
     * @throws E if an exception occurs in the task
     */
    default <T, E extends Exception> boolean runExPredicate(ExceptionPredicate<T, E> predicate, T param) throws E {
        final ContextManager<C> contextManager = getInstanceContextManager();
        final C old = contextManager.getAndSetCurrent(this);
        try {
            return predicate.test(param);
        } finally {
            contextManager.restoreCurrent(old);
        }
    }

    /**
     * Run the given task with this contextual object selected.
     *
     * @param function the task to run (must not be {@code null})
     * @param value the parameter to pass to the task
     * @param <T> the return value type
     * @return the action return value
     */
    default <T> T runIntFunction(IntFunction<T> function, int value) {
        final ContextManager<C> contextManager = getInstanceContextManager();
        final C old = contextManager.getAndSetCurrent(this);
        try {
            return function.apply(value);
        } finally {
            contextManager.restoreCurrent(old);
        }
    }

    /**
     * Run the given task with this contextual object selected.
     *
     * @param function the task to run (must not be {@code null})
     * @param value the parameter to pass to the task
     * @param <T> the return value type
     * @param <E> the exception type
     * @return the action return value
     * @throws E if an exception occurs in the task
     */
    default <T, E extends Exception> T runExIntFunction(ExceptionIntFunction<T, E> function, int value) throws E {
        final ContextManager<C> contextManager = getInstanceContextManager();
        final C old = contextManager.getAndSetCurrent(this);
        try {
            return function.apply(value);
        } finally {
            contextManager.restoreCurrent(old);
        }
    }

    /**
     * Run the given task with this contextual object selected.
     *
     * @param function the task to run (must not be {@code null})
     * @param value the parameter to pass to the task
     * @param <T> the return value type
     * @return the action return value
     */
    default <T> T runLongFunction(LongFunction<T> function, long value) {
        final ContextManager<C> contextManager = getInstanceContextManager();
        final C old = contextManager.getAndSetCurrent(this);
        try {
            return function.apply(value);
        } finally {
            contextManager.restoreCurrent(old);
        }
    }

    /**
     * Run the given task with this contextual object selected.
     *
     * @param function the task to run (must not be {@code null})
     * @param value the parameter to pass to the task
     * @param <T> the return value type
     * @param <E> the exception type
     * @return the action return value
     * @throws E if an exception occurs in the task
     */
    default <T, E extends Exception> T runExLongFunction(ExceptionLongFunction<T, E> function, long value) throws E {
        final ContextManager<C> contextManager = getInstanceContextManager();
        final C old = contextManager.getAndSetCurrent(this);
        try {
            return function.apply(value);
        } finally {
            contextManager.restoreCurrent(old);
        }
    }
}
