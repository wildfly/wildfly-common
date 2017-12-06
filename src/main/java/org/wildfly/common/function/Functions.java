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
        return (ExceptionConsumer) ExceptionRunnableConsumer.INSTANCE;
    }

    /**
     * Get the singleton consumer which accepts a consumer and an argument to hand to it.
     *
     * @param <T> the argument type
     * @return the consumer
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> BiConsumer<Consumer<T>, T> consumerBiConsumer() {
        return (BiConsumer) ConsumerBiConsumer.INSTANCE;
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
        return (ExceptionBiConsumer) ExceptionConsumerBiConsumer.INSTANCE;
    }

    /**
     * Get the singleton function which accepts a supplier and returns the result of the supplier.
     *
     * @param <T> the result type
     * @return the function
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Function<Supplier<T>, T> supplierFunction() {
        return (Function) SupplierFunction.INSTANCE;
    }

    /**
     * Get the singleton function which accepts a supplier and returns the result of the supplier.
     *
     * @param <T> the result type
     * @param <E> the exception type
     * @return the function
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T, E extends Exception> ExceptionFunction<ExceptionSupplier<T, E>, T, E> exceptionSupplierFunction() {
        return (ExceptionFunction) ExceptionSupplierFunction.INSTANCE;
    }

    /**
     * Get the singleton function which accepts a function which accepts a supplier, all of which return the result
     * of the supplier.
     *
     * @param <T> the result type
     * @return the function
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> BiFunction<Function<Supplier<T>, T>, Supplier<T>, T> supplierFunctionBiFunction() {
        return (BiFunction) FunctionSupplierBiFunction.INSTANCE;
    }

    /**
     * Get the singleton function which accepts a function which accepts a supplier, all of which return the result
     * of the supplier.
     *
     * @param <T> the result type
     * @param <E> the exception type
     * @return the function
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T, E extends Exception> ExceptionBiFunction<ExceptionFunction<ExceptionSupplier<T, E>, T, E>, ExceptionSupplier<T, E>, T, E> exceptionSupplierFunctionBiFunction() {
        return (ExceptionBiFunction) ExceptionFunctionSupplierBiFunction.INSTANCE;
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
        return value == null ? (Supplier<T>) ConstantSupplier.NULL : new ConstantSupplier<>(value);
    }

    /**
     * Get a supplier which always returns the same value.
     *
     * @param value the value to return
     * @param <T> the value type
     * @param <E> the exception type
     * @return the value supplier
     */
    @SuppressWarnings("unchecked")
    public static <T, E extends Exception> ExceptionSupplier<T, E> constantExceptionSupplier(T value) {
        return (ExceptionSupplier<T, E>) (value == null ? ConstantSupplier.NULL : new ConstantSupplier<T>(value));
    }

    static class RunnableConsumer implements Consumer<Runnable> {
        static final Consumer<Runnable> INSTANCE = new RunnableConsumer();

        private RunnableConsumer() {}

        public void accept(final Runnable runnable) {
            runnable.run();
        }
    }

    static class ExceptionRunnableConsumer<E extends Exception> implements ExceptionConsumer<ExceptionRunnable<E>, E> {
        static final ExceptionConsumer<ExceptionRunnable<Exception>, Exception> INSTANCE = new ExceptionRunnableConsumer<>();

        private ExceptionRunnableConsumer() {}

        public void accept(final ExceptionRunnable<E> ExceptionRunnable) throws E {
            ExceptionRunnable.run();
        }
    }

    static class ConsumerBiConsumer implements BiConsumer<Consumer<Object>, Object> {
        static final BiConsumer<Consumer<Object>, Object> INSTANCE = new ConsumerBiConsumer();

        private ConsumerBiConsumer() {}

        public void accept(final Consumer<Object> consumer, final Object o) {
            consumer.accept(o);
        }
    }

    static class ExceptionConsumerBiConsumer<E extends Exception> implements ExceptionBiConsumer<ExceptionConsumer<Object, E>, Object, E> {
        static final ExceptionBiConsumer<ExceptionConsumer<Object, Exception>, Object, Exception> INSTANCE = new ExceptionConsumerBiConsumer<>();

        private ExceptionConsumerBiConsumer() {}

        public void accept(final ExceptionConsumer<Object, E> consumer, final Object o) throws E {
            consumer.accept(o);
        }
    }

    static class SupplierFunction implements Function<Supplier<Object>, Object> {
        static final Function<Supplier<Object>, Object> INSTANCE = new SupplierFunction();

        private SupplierFunction() {}

        public Object apply(final Supplier<Object> supplier) {
            return supplier.get();
        }
    }

    static class ExceptionSupplierFunction<E extends Exception> implements ExceptionFunction<ExceptionSupplier<Object, E>, Object, E> {
        static final ExceptionFunction<ExceptionSupplier<Object, Exception>, Object, Exception> INSTANCE = new ExceptionSupplierFunction<>();

        private ExceptionSupplierFunction() {}

        public Object apply(final ExceptionSupplier<Object, E> supplier) throws E {
            return supplier.get();
        }
    }

    static class FunctionSupplierBiFunction implements BiFunction<Function<Supplier<Object>, Object>, Supplier<Object>, Object> {
        static final BiFunction<Function<Supplier<Object>, Object>, Supplier<Object>, Object> INSTANCE = new FunctionSupplierBiFunction();

        private FunctionSupplierBiFunction() {}

        public Object apply(final Function<Supplier<Object>, Object> function, final Supplier<Object> supplier) {
            return function.apply(supplier);
        }
    }

    static class ExceptionFunctionSupplierBiFunction<E extends Exception> implements ExceptionBiFunction<ExceptionFunction<ExceptionSupplier<Object, E>, Object, E>, ExceptionSupplier<Object, E>, Object, E> {
        static final BiFunction<Function<Supplier<Object>, Object>, Supplier<Object>, Object> INSTANCE = new FunctionSupplierBiFunction();

        private ExceptionFunctionSupplierBiFunction() {}

        public Object apply(final ExceptionFunction<ExceptionSupplier<Object, E>, Object, E> function, final ExceptionSupplier<Object, E> supplier) throws E {
            return function.apply(supplier);
        }
    }

    static class ConstantSupplier<T> implements Supplier<T>, ExceptionSupplier<T, RuntimeException> {
        static final Supplier<?> NULL = new ConstantSupplier<>(null);

        private final T arg1;

        ConstantSupplier(final T arg1) {
            this.arg1 = arg1;
        }

        public T get() {
            return arg1;
        }
    }
}
