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

import java.util.EnumSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.BiConsumer;

import org.wildfly.common.function.ExceptionBiConsumer;

/**
 * A compiled property-expansion expression string.  An expression string is a mix of plain strings and expression
 * segments, which are wrapped by the sequence "{@code ${ ... }}".
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 *
 * @deprecated Use {@link io.smallrye.common.expression.Expression} instead.
 */
@Deprecated(forRemoval = true)
public final class Expression {
    private final io.smallrye.common.expression.Expression expr;

    Expression(final io.smallrye.common.expression.Expression expr) {
        this.expr = expr;
    }

    /**
     * Get the immutable set of string keys that are referenced by expressions in this compiled expression.  If there
     * are no expansions in this expression, the set is empty.  Note that this will <em>not</em> include any string keys
     * that themselves contain expressions, in the case that {@link Flag#NO_RECURSE_KEY} was not specified.
     *
     * @return the immutable set of strings (not {@code null})
     */
    public Set<String> getReferencedStrings() {
        return expr.getReferencedStrings();
    }

    /**
     * Evaluate the expression with the given expansion function, which may throw a checked exception.  The given "function"
     * is a predicate which returns {@code true} if the expansion succeeded or {@code false} if it failed (in which case
     * a default value may be used).  If expansion succeeds, the expansion function should append the result to the
     * given {@link StringBuilder}.
     *
     * @param expandFunction the expansion function to apply (must not be {@code null})
     * @param <E> the exception type thrown by the expansion function
     * @return the expanded string
     * @throws E if the expansion function throws an exception
     */
    public <E extends Exception> String evaluateException(final ExceptionBiConsumer<ResolveContext<E>, StringBuilder, E> expandFunction) throws E {
        return expr.<E>evaluateException((resolveContext, stringBuilder) -> expandFunction.accept(new ResolveContext<E>(resolveContext), stringBuilder));
    }

    /**
     * Evaluate the expression with the given expansion function.  The given "function"
     * is a predicate which returns {@code true} if the expansion succeeded or {@code false} if it failed (in which case
     * a default value may be used).  If expansion succeeds, the expansion function should append the result to the
     * given {@link StringBuilder}.
     *
     * @param expandFunction the expansion function to apply (must not be {@code null})
     * @return the expanded string
     */
    public String evaluate(BiConsumer<ResolveContext<RuntimeException>, StringBuilder> expandFunction) {
        return evaluateException(expandFunction::accept);
    }

    /**
     * Evaluate the expression using a default expansion function that evaluates system and environment properties
     * in the JBoss style (i.e. using the prefix {@code "env."} to designate an environment property).
     * The caller must have all required security manager permissions.
     *
     * @param failOnNoDefault {@code true} to throw an {@link IllegalArgumentException} if an unresolvable key has no
     *      default value; {@code false} to expand such keys to an empty string
     * @return the expanded string
     */
    public String evaluateWithPropertiesAndEnvironment(boolean failOnNoDefault) {
        return expr.evaluateWithPropertiesAndEnvironment(failOnNoDefault);
    }

    /**
     * Evaluate the expression using a default expansion function that evaluates system properties.
     * The caller must have all required security manager permissions.
     *
     * @param failOnNoDefault {@code true} to throw an {@link IllegalArgumentException} if an unresolvable key has no
     *      default value; {@code false} to expand such keys to an empty string
     * @return the expanded string
     */
    public String evaluateWithProperties(boolean failOnNoDefault) {
        return expr.evaluateWithProperties(failOnNoDefault);
    }

    /**
     * Evaluate the expression using a default expansion function that evaluates environment properties.
     * The caller must have all required security manager permissions.
     *
     * @param failOnNoDefault {@code true} to throw an {@link IllegalArgumentException} if an unresolvable key has no
     *      default value; {@code false} to expand such keys to an empty string
     * @return the expanded string
     */
    public String evaluateWithEnvironment(boolean failOnNoDefault) {
        return expr.evaluateWithEnvironment(failOnNoDefault);
    }

    /**
     * Compile an expression string.
     *
     * @param string the expression string (must not be {@code null})
     * @param flags optional flags to apply which affect the compilation
     * @return the compiled expression (not {@code null})
     */
    public static Expression compile(String string, Flag... flags) {
        return new Expression(io.smallrye.common.expression.Expression.compile(string, Flag.mapFlagsArray(flags)));
    }

    /**
     * Compile an expression string.
     *
     * @param string the expression string (must not be {@code null})
     * @param flags optional flags to apply which affect the compilation (must not be {@code null})
     * @return the compiled expression (not {@code null})
     */
    public static Expression compile(String string, EnumSet<Flag> flags) {
        return new Expression(io.smallrye.common.expression.Expression.compile(string, Flag.mapFlags(flags)));
    }

    /**
     * Flags that can apply to a property expression compilation
     */
    public enum Flag {
        /**
         * Do not trim leading and trailing whitespace off of the expression string before parsing it.
         */
        NO_TRIM(io.smallrye.common.expression.Expression.Flag.NO_TRIM),
        /**
         * Ignore syntax problems instead of throwing an exception.
         */
        LENIENT_SYNTAX(io.smallrye.common.expression.Expression.Flag.LENIENT_SYNTAX),
        /**
         * Support single-character expressions that can be interpreted without wrapping in curly braces.
         */
        MINI_EXPRS(io.smallrye.common.expression.Expression.Flag.MINI_EXPRS),
        /**
         * Do not support recursive expression expansion in the key part of the expression.
         */
        NO_RECURSE_KEY(io.smallrye.common.expression.Expression.Flag.NO_RECURSE_KEY),
        /**
         * Do not support recursion in default values.
         */
        NO_RECURSE_DEFAULT(io.smallrye.common.expression.Expression.Flag.NO_RECURSE_DEFAULT),
        /**
         * Do not support smart braces.
         */
        NO_SMART_BRACES(io.smallrye.common.expression.Expression.Flag.NO_SMART_BRACES),
        /**
         * Support {@code Policy} file style "general" expansion alternate expression syntax.  "Smart" braces
         * will only work if the opening brace is not the first character in the expression key.
         */
        GENERAL_EXPANSION(io.smallrye.common.expression.Expression.Flag.GENERAL_EXPANSION),
        /**
         * Support standard escape sequences in plain text and default value fields, which begin with a backslash ("{@code \}") character.
         */
        ESCAPES(io.smallrye.common.expression.Expression.Flag.ESCAPES),
        /**
         * Treat expressions containing a double-colon delimiter as special, encoding the entire content into the key.
         */
        DOUBLE_COLON(io.smallrye.common.expression.Expression.Flag.DOUBLE_COLON),
        ;

        private final io.smallrye.common.expression.Expression.Flag flag;

        Flag(final io.smallrye.common.expression.Expression.Flag flag) {
            this.flag = flag;
        }

        static Flag of(io.smallrye.common.expression.Expression.Flag flag) {
            switch (flag) {
                case NO_TRIM: return NO_TRIM;
                case LENIENT_SYNTAX: return LENIENT_SYNTAX;
                case MINI_EXPRS: return MINI_EXPRS;
                case NO_RECURSE_KEY: return NO_RECURSE_KEY;
                case NO_RECURSE_DEFAULT: return NO_RECURSE_DEFAULT;
                case NO_SMART_BRACES: return NO_SMART_BRACES;
                case GENERAL_EXPANSION: return GENERAL_EXPANSION;
                case ESCAPES: return ESCAPES;
                case DOUBLE_COLON: return DOUBLE_COLON;
                default: throw new NoSuchElementException(flag.toString());
            }
        }

        io.smallrye.common.expression.Expression.Flag flag() {
            return flag;
        }

        static EnumSet<io.smallrye.common.expression.Expression.Flag> mapFlagsArray(Flag[] array) {
            EnumSet<io.smallrye.common.expression.Expression.Flag> set = EnumSet.noneOf(io.smallrye.common.expression.Expression.Flag.class);
            for (Flag f : array) {
                set.add(f.flag());
            }
            return set;
        }

        static EnumSet<io.smallrye.common.expression.Expression.Flag> mapFlags(final EnumSet<Flag> flags) {
            EnumSet<io.smallrye.common.expression.Expression.Flag> set = EnumSet.noneOf(io.smallrye.common.expression.Expression.Flag.class);
            for (Flag f : flags) {
                set.add(f.flag());
            }
            return set;
        }
    }
}
