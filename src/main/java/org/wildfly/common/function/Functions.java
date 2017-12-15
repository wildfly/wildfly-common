/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2017 Red Hat, Inc., and individual contributors
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

package org.wildfly.common.function;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.wildfly.common.Assert;

/**
 * A set of utility methods which return common functions.
 */
public final class Functions {
    private Functions() {}

    /**
     * Get the singleton consumer which accepts and runs runnable instances.
     *
     * @return the runnable consumer
     */
    public static Consumer<Runnable> runnableConsumer() {
        return RunnableConsumer.INSTANCE;
    }

    /**
     * Get the singleton exception consumer which accepts and runs exception runnable instances.
     *
     * @param <E> the exception type
     * @return the runnable consumer
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <E extends Exception> ExceptionConsumer<ExceptionRunnable<E>, E> exceptionRunnableConsumer() {
        return ExceptionRunnableConsumer.INSTANCE;
    }

    /**
     * Get the singleton consumer which accepts a consumer and an argument to hand to it.
     *
     * @param <T> the argument type
     * @return the consumer
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> BiConsumer<Consumer<T>, T> consumerBiConsumer() {
        return ConsumerBiConsumer.INSTANCE;
    }

    /**
     * Get the singleton consumer which accepts a consumer and an argument to hand to it.
     *
     * @param <T> the argument type
     * @param <E> the exception type
     * @return the consumer
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T, E extends Exception> ExceptionBiConsumer<ExceptionConsumer<T, E>, T, E> exceptionConsumerBiConsumer() {
        return ExceptionConsumerBiConsumer.INSTANCE;
    }

    /**
     * Get the singleton function which accepts a supplier and returns the result of the supplier.
     *
     * @param <R> the result type
     * @return the function
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <R> Function<Supplier<R>, R> supplierFunction() {
        return SupplierFunction.INSTANCE;
    }

    /**
     * Get the singleton function which accepts a supplier and returns the result of the supplier.
     *
     * @param <R> the result type
     * @param <E> the exception type
     * @return the function
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <R, E extends Exception> ExceptionFunction<ExceptionSupplier<R, E>, R, E> exceptionSupplierFunction() {
        return ExceptionSupplierFunction.INSTANCE;
    }

    /**
     * Get the singleton function which accepts a function which accepts a supplier, all of which return the result
     * of the supplier.
     *
     * @param <R> the result type
     * @return the function
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <R> BiFunction<Function<Supplier<R>, R>, Supplier<R>, R> supplierFunctionBiFunction() {
        return FunctionSupplierBiFunction.INSTANCE;
    }

    /**
     * Get the singleton function which accepts a function which accepts a supplier, all of which return the result
     * of the supplier.
     *
     * @param <R> the result type
     * @param <E> the exception type
     * @return the function
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <R, E extends Exception> ExceptionBiFunction<ExceptionFunction<ExceptionSupplier<R, E>, R, E>, ExceptionSupplier<R, E>, R, E> exceptionSupplierFunctionBiFunction() {
        return ExceptionFunctionSupplierBiFunction.INSTANCE;
    }

    /**
     * Get the singleton function which accepts a function and a parameter to pass to the function, and returns the
     * result of the function.
     *
     * @param <T> the argument type
     * @param <R> the result type
     * @return the function
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T, R> BiFunction<Function<T, R>, T, R> functionBiFunction() {
        return FunctionBiFunction.INSTANCE;
    }

    /**
     * Get the singleton function which accepts a function and a parameter to pass to the function, and returns the
     * result of the function.
     *
     * @param <T> the argument type
     * @param <R> the result type
     * @param <E> the exception type
     * @return the function
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T, R, E extends Exception> ExceptionBiFunction<ExceptionFunction<T, R, E>, T, R, E> exceptionFunctionBiFunction() {
        return ExceptionFunctionBiFunction.INSTANCE;
    }

    /**
     * Get a supplier which always returns the same value.
     *
     * @param value the value to return
     * @param <T> the value type
     * @return the value supplier
     */
    @SuppressWarnings("unchecked")
    public static <T> Supplier<T> constantSupplier(T value) {
        return value == null ? ConstantSupplier.NULL : new ConstantSupplier<>(value);
    }

    /**
     * Get a supplier which always returns the same value.
     *
     * @param value the value to return
     * @param <T> the value type
     * @param <E> the exception type
     * @return the value supplier
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T, E extends Exception> ExceptionSupplier<T, E> constantExceptionSupplier(T value) {
        return value == null ? ConstantSupplier.NULL : new ConstantSupplier(value);
    }

    /**
     * Get a runnable which executes the given consumer with captured values.
     *
     * @param consumer the consumer to run (must not be {@code null})
     * @param param1 the first parameter to pass
     * @param param2 the second parameter to pass
     * @param <T> the first parameter type
     * @param <U> the second parameter type
     * @return the capturing runnable
     */
    public static <T, U> Runnable capturingRunnable(BiConsumer<T, U> consumer, T param1, U param2) {
        Assert.checkNotNullParam("consumer", consumer);
        return new BiConsumerRunnable<T, U>(consumer, param1, param2);
    }

    /**
     * Get a runnable which executes the given consumer with captured values.
     *
     * @param consumer the consumer to run (must not be {@code null})
     * @param param the parameter to pass
     * @param <T> the parameter type
     * @return the capturing runnable
     */
    public static <T> Runnable capturingRunnable(Consumer<T> consumer, T param) {
        Assert.checkNotNullParam("consumer", consumer);
        return new ConsumerRunnable<T>(consumer, param);
    }

    /**
     * Get a runnable which executes the given consumer with captured values.
     *
     * @param consumer the consumer to run (must not be {@code null})
     * @param param1 the first parameter to pass
     * @param param2 the second parameter to pass
     * @param <T> the first parameter type
     * @param <U> the second parameter type
     * @param <E> the exception type
     * @return the capturing runnable
     */
    public static <T, U, E extends Exception> ExceptionRunnable<E> exceptionCapturingRunnable(ExceptionBiConsumer<T, U, E> consumer, T param1, U param2) {
        Assert.checkNotNullParam("consumer", consumer);
        return new ExceptionBiConsumerRunnable<T, U, E>(consumer, param1, param2);
    }

    /**
     * Get a runnable which executes the given consumer with captured values.
     *
     * @param consumer the consumer to run (must not be {@code null})
     * @param param the parameter to pass
     * @param <T> the parameter type
     * @param <E> the exception type
     * @return the capturing runnable
     */
    public static <T, E extends Exception> ExceptionRunnable<E> exceptionCapturingRunnable(ExceptionConsumer<T, E> consumer, T param) {
        Assert.checkNotNullParam("consumer", consumer);
        return new ExceptionConsumerRunnable<T, E>(consumer, param);
    }

    static class RunnableConsumer implements Consumer<Runnable> {
        static final Consumer<Runnable> INSTANCE = new RunnableConsumer();

        private RunnableConsumer() {}

        public void accept(final Runnable runnable) {
            runnable.run();
        }
    }

    static class ExceptionRunnableConsumer<E extends Exception> implements ExceptionConsumer<ExceptionRunnable<E>, E> {
        static final ExceptionConsumer INSTANCE = new ExceptionRunnableConsumer<>();

        private ExceptionRunnableConsumer() {}

        public void accept(final ExceptionRunnable<E> ExceptionRunnable) throws E {
            ExceptionRunnable.run();
        }
    }

    static class ConsumerBiConsumer implements BiConsumer<Consumer<Object>, Object> {
        static final BiConsumer INSTANCE = new ConsumerBiConsumer();

        private ConsumerBiConsumer() {}

        public void accept(final Consumer<Object> consumer, final Object o) {
            consumer.accept(o);
        }
    }

    static class ExceptionConsumerBiConsumer<E extends Exception> implements ExceptionBiConsumer<ExceptionConsumer<Object, E>, Object, E> {
        static final ExceptionBiConsumer INSTANCE = new ExceptionConsumerBiConsumer<>();

        private ExceptionConsumerBiConsumer() {}

        public void accept(final ExceptionConsumer<Object, E> consumer, final Object o) throws E {
            consumer.accept(o);
        }
    }

    static class SupplierFunction implements Function<Supplier<Object>, Object> {
        static final Function INSTANCE = new SupplierFunction();

        private SupplierFunction() {}

        public Object apply(final Supplier<Object> supplier) {
            return supplier.get();
        }
    }

    static class ExceptionSupplierFunction<E extends Exception> implements ExceptionFunction<ExceptionSupplier<Object, E>, Object, E> {
        static final ExceptionFunction INSTANCE = new ExceptionSupplierFunction<>();

        private ExceptionSupplierFunction() {}

        public Object apply(final ExceptionSupplier<Object, E> supplier) throws E {
            return supplier.get();
        }
    }

    static class FunctionSupplierBiFunction implements BiFunction<Function<Supplier<Object>, Object>, Supplier<Object>, Object> {
        static final BiFunction INSTANCE = new FunctionSupplierBiFunction();

        private FunctionSupplierBiFunction() {}

        public Object apply(final Function<Supplier<Object>, Object> function, final Supplier<Object> supplier) {
            return function.apply(supplier);
        }
    }

    static class ExceptionFunctionSupplierBiFunction<E extends Exception> implements ExceptionBiFunction<ExceptionFunction<ExceptionSupplier<Object, E>, Object, E>, ExceptionSupplier<Object, E>, Object, E> {
        static final ExceptionBiFunction INSTANCE = new ExceptionFunctionSupplierBiFunction();

        private ExceptionFunctionSupplierBiFunction() {}

        public Object apply(final ExceptionFunction<ExceptionSupplier<Object, E>, Object, E> function, final ExceptionSupplier<Object, E> supplier) throws E {
            return function.apply(supplier);
        }
    }

    static class FunctionBiFunction<T, R> implements BiFunction<Function<T, R>, T, R> {
        static final BiFunction INSTANCE = new FunctionBiFunction();

        private FunctionBiFunction() {
        }

        public R apply(final Function<T, R> function, final T t) {
            return function.apply(t);
        }
    }

    static class ExceptionFunctionBiFunction<T, R, E extends Exception> implements ExceptionBiFunction<ExceptionFunction<T, R, E>, T, R, E> {
        static final ExceptionBiFunction INSTANCE = new ExceptionFunctionBiFunction();

        private ExceptionFunctionBiFunction() {
        }

        public R apply(final ExceptionFunction<T, R, E> function, final T t) throws E {
            return function.apply(t);
        }
    }

    static class ConstantSupplier<T> implements Supplier<T>, ExceptionSupplier<T, RuntimeException> {
        static final ConstantSupplier NULL = new ConstantSupplier<>(null);

        private final T arg1;

        ConstantSupplier(final T arg1) {
            this.arg1 = arg1;
        }

        public T get() {
            return arg1;
        }

        public String toString() {
            return String.format("supplier(%s)", arg1);
        }
    }

    static class BiConsumerRunnable<T, U> implements Runnable {
        private final BiConsumer<T, U> consumer;
        private final T param1;
        private final U param2;

        BiConsumerRunnable(final BiConsumer<T, U> consumer, final T param1, final U param2) {
            this.consumer = consumer;
            this.param1 = param1;
            this.param2 = param2;
        }

        public void run() {
            consumer.accept(param1, param2);
        }

        public String toString() {
            return String.format("%s(%s,%s)", consumer, param1, param2);
        }
    }

    static class ConsumerRunnable<T> implements Runnable {
        private final Consumer<T> consumer;
        private final T param;

        ConsumerRunnable(final Consumer<T> consumer, final T param) {
            this.consumer = consumer;
            this.param = param;
        }

        public void run() {
            consumer.accept(param);
        }

        public String toString() {
            return String.format("%s(%s)", consumer, param);
        }
    }

    static class ExceptionBiConsumerRunnable<T, U, E extends Exception> implements ExceptionRunnable<E> {
        private final ExceptionBiConsumer<T, U, E> consumer;
        private final T param1;
        private final U param2;

        ExceptionBiConsumerRunnable(final ExceptionBiConsumer<T, U, E> consumer, final T param1, final U param2) {
            this.consumer = consumer;
            this.param1 = param1;
            this.param2 = param2;
        }

        public void run() throws E {
            consumer.accept(param1, param2);
        }

        public String toString() {
            return String.format("%s(%s,%s)", consumer, param1, param2);
        }
    }

    static class ExceptionConsumerRunnable<T, E extends Exception> implements ExceptionRunnable<E> {
        private final ExceptionConsumer<T, E> consumer;
        private final T param;

        ExceptionConsumerRunnable(final ExceptionConsumer<T, E> consumer, final T param) {
            this.consumer = consumer;
            this.param = param;
        }

        public void run() throws E {
            consumer.accept(param);
        }

        public String toString() {
            return String.format("%s(%s)", consumer, param);
        }
    }
}
