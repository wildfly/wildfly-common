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

package org.wildfly.common.function;

import org.wildfly.common.Assert;

/**
 * A one-argument function which can throw an exception.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
@FunctionalInterface
public interface ExceptionFunction<T, R, E extends Exception> {
    /**
     * Applies this function to the given arguments.
     *
     * @param t the argument
     * @return the function result
     * @throws E if an exception occurs
     */
    R apply(T t) throws E;

    default <R2> ExceptionFunction<T, R2, E> andThen(ExceptionFunction<? super R, ? extends R2, ? extends E> after) {
        Assert.checkNotNullParam("after", after);
        return t -> after.apply(apply(t));
    }

    default <R2> ExceptionFunction<T, R2, E> andThen(ExceptionBiFunction<? super T, ? super R, ? extends R2, ? extends E> after) {
        Assert.checkNotNullParam("after", after);
        return t -> after.apply(t, apply(t));
    }

    default <T2> ExceptionFunction<T2, R, E> compose(ExceptionFunction<? super T2, ? extends T, ? extends E> before) {
        Assert.checkNotNullParam("before", before);
        return t -> apply(before.apply(t));
    }

    default ExceptionConsumer<T, E> andThen(ExceptionConsumer<? super R, ? extends E> after) {
        Assert.checkNotNullParam("after", after);
        return t -> after.accept(apply(t));
    }

    default ExceptionConsumer<T, E> andThen(ExceptionBiConsumer<? super T, ? super R, ? extends E> after) {
        Assert.checkNotNullParam("after", after);
        return t -> after.accept(t, apply(t));
    }

    default ExceptionPredicate<T, E> andThen(ExceptionPredicate<? super R, ? extends E> after) {
        Assert.checkNotNullParam("after", after);
        return t -> after.test(apply(t));
    }

    default ExceptionPredicate<T, E> andThen(ExceptionBiPredicate<? super T, ? super R, ? extends E> after) {
        Assert.checkNotNullParam("after", after);
        return t -> after.test(t, apply(t));
    }

    default ExceptionSupplier<R, E> compose(ExceptionSupplier<? extends T, ? extends E> before) {
        Assert.checkNotNullParam("before", before);
        return () -> apply(before.get());
    }
}
