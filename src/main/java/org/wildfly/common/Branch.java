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

package org.wildfly.common;

/**
 * Branch probability directives.  On JVMs which support these constructs,
 */
public final class Branch {
    private Branch() {}

    /**
     * Assert that the given expression is on the "fast path", which is to say, it will be {@code true} more than 99.9%
     * of the time.
     *
     * @param expr the expression value
     * @return the expression value
     */
    public static boolean veryLikely(boolean expr) {
        return expr;
    }

    /**
     * Assert that the given expression is on the "slow path", which is to say, it will be {@code false} more than 99.9%
     * of the time.
     *
     * @param expr the expression value
     * @return the expression value
     */
    public static boolean veryUnlikely(boolean expr) {
        return expr;
    }

    /**
     * Assert that the given expression is likely, which is to say, it will be {@code true} more than 75%
     * of the time.
     *
     * @param expr the expression value
     * @return the expression value
     */
    public static boolean likely(boolean expr) {
        return expr;
    }

    /**
     * Assert that the given expression is unlikely, which is to say, it will be {@code false} more than 75%
     * of the time.
     *
     * @param expr the expression value
     * @return the expression value
     */
    public static boolean unlikely(boolean expr) {
        return expr;
    }

    /**
     * Assert that the given expression has the given probability of being {@code true}.
     *
     * @param prob the probability where {@code 0.0f ≤ prob ≤ 1.0f}
     * @param expr the expression value
     * @return the expression value
     */
    public static boolean probability(float prob, boolean expr) {
        assert 0.0f <= prob && prob <= 1.0f;
        return expr;
    }
}
