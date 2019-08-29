package org.wildfly.common.function;

import org.wildfly.common.Assert;

/**
 * A supplier which can throw an exception.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
@FunctionalInterface
public interface ExceptionSupplier<T, E extends Exception> {
    /**
     * Gets a result.
     *
     * @return the result
     * @throws E if an exception occurs
     */
    T get() throws E;

    default ExceptionRunnable<E> andThen(ExceptionConsumer<? super T, ? extends E> after) {
        Assert.checkNotNullParam("after", after);
        return () -> after.accept(get());
    }

    default <R> ExceptionSupplier<R, E> andThen(ExceptionFunction<? super T, ? extends R, ? extends E> after) {
        Assert.checkNotNullParam("after", after);
        return () -> after.apply(get());
    }
}
