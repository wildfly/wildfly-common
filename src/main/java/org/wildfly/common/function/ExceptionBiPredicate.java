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

/**
 * A two-argument predicate which can throw an exception.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
@FunctionalInterface
public interface ExceptionBiPredicate<T, U, E extends Exception> {
    /**
     * Evaluate this predicate on the given arguments.
     *
     * @param t the first argument
     * @param u the second argument
     * @return {@code true} if the predicate passes, {@code false} otherwise
     * @throws E if an exception occurs
     */
    boolean test(T t, U u) throws E;

    default ExceptionBiPredicate<T, U, E> and(ExceptionBiPredicate<T, U, E> other) {
        return (t, u) -> test(t, u) && other.test(t, u);
    }

    default ExceptionBiPredicate<T, U, E> or(ExceptionBiPredicate<T, U, E> other) {
        return (t, u) -> test(t, u) || other.test(t, u);
    }

    default ExceptionBiPredicate<T, U, E> xor(ExceptionBiPredicate<T, U, E> other) {
        return (t, u) -> test(t, u) != other.test(t, u);
    }

    default ExceptionBiPredicate<T, U, E> not() {
        return (t, u) -> !test(t, u);
    }
}
