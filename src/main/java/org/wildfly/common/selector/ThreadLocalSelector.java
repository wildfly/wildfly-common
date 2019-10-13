package org.wildfly.common.selector;

import org.wildfly.common.Assert;
import org.wildfly.common.context.Contextual;

/**
 * A thread local selector implementation.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 *
 * @deprecated Use {@link Contextual} instead.
 */
@Deprecated
public final class ThreadLocalSelector<T> extends Selector<T> {
    private final ThreadLocal<? extends T> threadLocal;

    /**
     * Construct a new instance.
     *
     * @param threadLocal the thread-local to use to store the selector's value
     */
    public ThreadLocalSelector(final ThreadLocal<? extends T> threadLocal) {
        Assert.checkNotNullParam("threadLocal", threadLocal);
        this.threadLocal = threadLocal;
    }

    public T get() {
        return threadLocal.get();
    }
}
