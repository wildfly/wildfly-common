package org.wildfly.common.selector;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.wildfly.common.context.Contextual;

/**
 * An annotation indicating the default selector implementation class to use for a class.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 *
 * @deprecated Use {@link Contextual} instead.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Deprecated
public @interface DefaultSelector {

    /**
     * The selector implementation class to use.
     *
     * @return the selector implementation class
     */
    Class<? extends Selector<?>> value();
}
