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
 * A unary operator which can throw an exception.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
@FunctionalInterface
public interface ExceptionUnaryOperator<T, E extends Exception> extends ExceptionFunction<T, T, E> {
    default ExceptionUnaryOperator<T, E> andThen(ExceptionUnaryOperator<T, ? extends E> after) {
        Assert.checkNotNullParam("after", after);
        return t -> after.apply(apply(t));
    }

    default ExceptionUnaryOperator<T, E> compose(ExceptionUnaryOperator<T, ? extends E> before) {
        Assert.checkNotNullParam("before", before);
        return t -> apply(before.apply(t));
    }

    static <T, E extends Exception> ExceptionUnaryOperator<T, E> of(ExceptionFunction<T, T, E> func) {
        return func instanceof ExceptionUnaryOperator ? (ExceptionUnaryOperator<T, E>) func : func::apply;
    }
}
