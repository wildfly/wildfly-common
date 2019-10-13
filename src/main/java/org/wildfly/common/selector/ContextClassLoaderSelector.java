package org.wildfly.common.selector;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
@Deprecated
class ContextClassLoaderSelector extends Selector<ClassLoader> {

    public ClassLoader get() {
        return Thread.currentThread().getContextClassLoader();
    }
}
