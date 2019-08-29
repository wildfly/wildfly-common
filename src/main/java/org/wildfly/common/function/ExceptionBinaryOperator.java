package org.wildfly.common.function;

import org.wildfly.common.Assert;

/**
 * A binary operator which can throw an exception.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
@FunctionalInterface
public interface ExceptionBinaryOperator<T, E extends Exception> extends ExceptionBiFunction<T, T, T, E> {

    default ExceptionBinaryOperator<T, E> andThen(ExceptionUnaryOperator<T, ? extends E> after) {
        Assert.checkNotNullParam("after", after);
        return (t, u) -> after.apply(apply(t, u));
    }
}
