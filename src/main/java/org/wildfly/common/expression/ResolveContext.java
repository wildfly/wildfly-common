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

package org.wildfly.common.expression;

/**
 * The expression resolve context, which can be used to query the current expression key, write out expansions or
 * default values, or perform validation.
 * <p>
 * The expression context is not thread-safe and is not valid outside of the property expansion function body.
 *
 * @param <E> the exception type that can be thrown by the expansion function
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 *
 * @deprecated Use {@link io.smallrye.common.expression.ResolveContext} instead.
 */
@Deprecated(forRemoval = true)
public final class ResolveContext<E extends Exception> {
    private final io.smallrye.common.expression.ResolveContext<E> delegate;

    ResolveContext(final io.smallrye.common.expression.ResolveContext<E> delegate) {
        this.delegate = delegate;
    }

    /**
     * Get the expression resolution key, as a string.  If the key contains an expression, it will have been expanded
     * unless {@link Expression.Flag#NO_RECURSE_KEY} was given.
     * The result is not cached and will be re-expanded every time this method is called.
     *
     * @return the expanded key (not {@code null})
     * @throws E if the recursive expansion threw an exception
     */
    public String getKey() throws E {
        return delegate.getKey();
    }

    /**
     * Expand the default value to the given string builder.  If the default value contains an expression, it will
     * have been expanded unless {@link Expression.Flag#NO_RECURSE_DEFAULT} was given.
     * The result is not cached and will be re-expanded every time this method is called.
     *
     * @param target the string builder target
     * @throws E if the recursive expansion threw an exception
     */
    public void expandDefault(StringBuilder target) throws E {
        delegate.expandDefault(target);
    }

    /**
     * Expand the default value to the current target string builder.  If the default value contains an expression, it will
     * have been expanded unless {@link Expression.Flag#NO_RECURSE_DEFAULT} was given.
     * The result is not cached and will be re-expanded every time this method is called.
     *
     * @throws E if the recursive expansion threw an exception
     */
    public void expandDefault() throws E {
        delegate.expandDefault();
    }

    /**
     * Expand the default value to a string.  If the default value contains an expression, it will
     * have been expanded unless {@link Expression.Flag#NO_RECURSE_DEFAULT} was given.
     * The result is not cached and will be re-expanded every time this method is called.
     *
     * @return the expanded string (not {@code null})
     * @throws E if the recursive expansion threw an exception
     */
    public String getExpandedDefault() throws E {
        return delegate.getExpandedDefault();
    }

    /**
     * Determine if the current expression has a default value.
     *
     * @return {@code true} if there is a default value, {@code false} otherwise
     */
    public boolean hasDefault() {
        return delegate.hasDefault();
    }
}
