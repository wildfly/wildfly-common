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
 * A two-argument consumer which can throw an exception.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
@FunctionalInterface
public interface ExceptionBiConsumer<T, U, E extends Exception> {
    /**
     * Performs this operation on the given arguments.
     *
     * @param t the first argument
     * @param u the second argument
     * @throws E if an exception occurs
     */
    void accept(T t, U u) throws E;

    default ExceptionBiConsumer<T, U, E> andThen(ExceptionBiConsumer<? super T, ? super U, ? extends E> after) {
        Assert.checkNotNullParam("after", after);
        return (t, u) -> {
            accept(t, u);
            after.accept(t, u);
        };
    }

    default ExceptionRunnable<E> compose(ExceptionSupplier<? extends T, ? extends E> before1, ExceptionSupplier<? extends U, ? extends E> before2) {
        Assert.checkNotNullParam("before1", before1);
        Assert.checkNotNullParam("before2", before2);
        return () -> accept(before1.get(), before2.get());
    }
}
