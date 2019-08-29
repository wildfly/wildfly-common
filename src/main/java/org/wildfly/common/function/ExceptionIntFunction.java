package org.wildfly.common.function;

import org.wildfly.common.Assert;

/**
 * A one-argument integer function which can throw an exception.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
@FunctionalInterface
public interface ExceptionIntFunction<R, E extends Exception> {
    /**
     * Applies this function to the given arguments.
     *
     * @param value the argument
     * @return the function result
     * @throws E if an exception occurs
     */
    R apply(int value) throws E;

    default <R2> ExceptionIntFunction<R2, E> andThen(ExceptionFunction<? super R, ? extends R2, ? extends E> after) {
        Assert.checkNotNullParam("after", after);
        return t -> after.apply(apply(t));
    }

    default <T> ExceptionFunction<T, R, E> compose(ExceptionToIntFunction<? super T, ? extends E> before) {
        Assert.checkNotNullParam("before", before);
        return t -> apply(before.apply(t));
    }
}
