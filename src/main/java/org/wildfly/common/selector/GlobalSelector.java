package org.wildfly.common.selector;

import org.wildfly.common.Assert;
import org.wildfly.common.context.Contextual;

/**
 * A selector which always returns one global instance.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 *
 * @deprecated Use {@link Contextual} instead.
 */
@Deprecated
public final class GlobalSelector<T> extends Selector<T> {
    private final T instance;

    /**
     * Construct a new instance.
     *
     * @param instance the constant instance to always return from this selector
     */
    public GlobalSelector(final T instance) {
        Assert.checkNotNullParam("instance", instance);
        this.instance = instance;
    }

    public T get() {
        return instance;
    }
}
