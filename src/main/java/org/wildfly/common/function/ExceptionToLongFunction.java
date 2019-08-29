package org.wildfly.common.function;

import org.wildfly.common.Assert;

/**
 * A two-argument function which can throw an exception.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
@FunctionalInterface
public interface ExceptionToLongFunction<T, E extends Exception> {
    /**
     * Apply this function to the given arguments.
     *
     * @param t the first argument
     * @return the function result
     * @throws E if an exception occurs
     */
    long apply(T t) throws E;

    default <R> ExceptionFunction<T, R, E> andThen(ExceptionLongFunction<R, E> after) {
        Assert.checkNotNullParam("after", after);
        return t -> after.apply(apply(t));
    }

    default <T2> ExceptionToLongFunction<T2, E> compose(ExceptionFunction<? super T2, ? extends T, ? extends E> before) {
        Assert.checkNotNullParam("before", before);
        return t -> apply(before.apply(t));
    }
}
