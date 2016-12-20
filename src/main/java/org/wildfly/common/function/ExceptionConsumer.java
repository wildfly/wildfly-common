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
 * A one-argument consumer which can throw an exception.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
@FunctionalInterface
public interface ExceptionConsumer<T, E extends Exception> {
    /**
     * Performs this operation on the given argument.
     *
     * @param t the argument
     * @throws E if an exception occurs
     */
    void accept(T t) throws E;

    default ExceptionConsumer<T, E> andThen(ExceptionConsumer<? super T, ? extends E> after) {
        Assert.checkNotNullParam("after", after);
        return t -> {
            accept(t);
            after.accept(t);
        };
    }

    default ExceptionConsumer<T, E> compose(ExceptionConsumer<? super T, ? extends E> before) {
        Assert.checkNotNullParam("before", before);
        return t -> {
            accept(t);
            before.accept(t);
        };
    }

    default ExceptionRunnable<E> compose(ExceptionSupplier<? extends T, ? extends E> before) {
        Assert.checkNotNullParam("before", before);
        return () -> accept(before.get());
    }
}
