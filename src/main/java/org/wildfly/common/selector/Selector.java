/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
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

package org.wildfly.common.selector;

import java.lang.reflect.InvocationTargetException;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicReference;

import org.wildfly.common.Assert;
import org.wildfly.common.context.Contextual;

/**
 * A selector for an object which is obtainable via static context.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 *
 * @deprecated Use {@link Contextual} instead.
 */
@Deprecated
public abstract class Selector<T> {

    private static final Selector<?> NULL = new Selector<Object>() {
        public Object get() {
            return null;
        }
    };

    private static final ClassValue<Holder<?>> selVal = new ClassValue<Holder<?>>() {
        protected Holder<?> computeValue(final Class<?> type) {
            return doCompute(type);
        }

        private <S> Holder<S> doCompute(final Class<S> type) {
            Selector<S> selector = null;
            try {
                final DefaultSelector defaultSelector = type.getAnnotation(DefaultSelector.class);
                if (defaultSelector != null) {
                    final Class<? extends Selector<?>> selectorType = defaultSelector.value();
                    selector = (Selector<S>) selectorType.getConstructor().newInstance();
                }
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException ignored) {
            }
            final Holder<S> holder = new Holder<>(type);
            holder.set(selector);
            return holder;
        }
    };

    protected Selector() {
    }

    /**
     * Get the currently relevant object, or {@code null} if there is none.
     *
     * @return the currently relevant object
     */
    public abstract T get();

    /**
     * Get the {@code null} selector.  This selector always returns {@code null}.
     *
     * @param <T> the selectable class' type
     * @return the {@code null} selector
     */
    @SuppressWarnings("unchecked")
    public static <T> Selector<T> nullSelector() {
        return (Selector<T>) NULL;
    }

    /**
     * Get the selector for a given class.  Never returns {@code null}.  If there is a selector set, the caller must
     * have the {@code get} {@link SelectorPermission} for the class.
     *
     * @param clazz the class
     * @param <T> the class type
     * @return the selector for the given type (not {@code null})
     */
    @SuppressWarnings("unchecked")
    public static <T> Selector<T> selectorFor(Class<T> clazz) {
        Assert.checkNotNullParam("clazz", clazz);
        final Holder<T> holder = (Holder<T>) selVal.get(clazz);
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(holder.getGetPermission());
        }
        final Selector<T> sel = holder.get();
        return sel == null ? Selector.<T>nullSelector() : sel;
    }

    /**
     * Set the selector for a given class.  If there is no selector set, the caller must have the {@code set} {@link SelectorPermission} for that class.
     * If there is one set, the caller must have the {@code change} {@link SelectorPermission}.  If there is a selector
     * set, and it is identical to the proposed selector, this method returns without taking any action.
     *
     * @param clazz the class
     * @param selector the selector to set for the class
     * @param <T> the class type
     */
    @SuppressWarnings("unchecked")
    public static <T> void setSelectorFor(Class<T> clazz, Selector<T> selector) {
        Assert.checkNotNullParam("clazz", clazz);
        final Holder<T> holder = (Holder<T>) selVal.get(clazz);
        Selector<T> oldValue;
        boolean set = false, change = false;
        for (;;) {
            oldValue = holder.get();
            if (oldValue == selector) {
                return;
            }
            if (oldValue == null) {
                if (! set) {
                    final SecurityManager sm = System.getSecurityManager();
                    if (sm != null) {
                        sm.checkPermission(holder.getSetPermission());
                    }
                    set = true;
                }
                if (holder.compareAndSet(null, selector)) {
                    return;
                }
            } else {
                if (! change) {
                    final SecurityManager sm = System.getSecurityManager();
                    if (sm != null) {
                        sm.checkPermission(holder.getChangePermission());
                    }
                    change = true;
                }
                if (holder.compareAndSet(oldValue, selector)) {
                    return;
                }
            }
        }
    }

    /**
     * Get an efficient, unchecked selector getter for a given class.  The caller must have the {@code get}
     * {@link SelectorPermission} for the class.
     *
     * @param clazz the class
     * @param <T> the class type
     * @return the unchecked selector getter
     */
    @SuppressWarnings("unchecked")
    public static <T> Getter<T> selectorGetterFor(Class<T> clazz) {
        Assert.checkNotNullParam("clazz", clazz);
        final Holder<T> holder = (Holder<T>) selVal.get(clazz);
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(holder.getGetPermission());
        }
        return new Getter<>(holder);
    }

    /**
     * Get a privileged action which returns the getter for a selector.
     *
     * @param clazz the class
     * @param <T> the class type
     * @return the getter privileged action
     */
    public static <T> PrivilegedAction<Getter<T>> selectorGetterActionFor(final Class<T> clazz) {
        Assert.checkNotNullParam("clazz", clazz);
        return () -> selectorGetterFor(clazz);
    }

    /**
     * Get the {@code get} permission for the given class.  The permission is cached.
     *
     * @param clazz the class to get the permission for
     * @return the selector permission for the class
     */
    public static SelectorPermission getGetPermissionFor(Class<?> clazz) {
        Assert.checkNotNullParam("clazz", clazz);
        return selVal.get(clazz).getGetPermission();
    }

    /**
     * Get the {@code set} permission for the given class.  The permission is cached.
     *
     * @param clazz the class to get the permission for
     * @return the selector permission for the class
     */
    public static SelectorPermission getSetPermissionFor(Class<?> clazz) {
        Assert.checkNotNullParam("clazz", clazz);
        return selVal.get(clazz).getSetPermission();
    }

    /**
     * Get the {@code change} permission for the given class.  The permission is cached.
     *
     * @param clazz the class to get the permission for
     * @return the selector permission for the class
     */
    public static SelectorPermission getChangePermissionFor(Class<?> clazz) {
        Assert.checkNotNullParam("clazz", clazz);
        return selVal.get(clazz).getChangePermission();
    }

    /**
     * An efficient, unchecked getter for a selector for a given class.
     *
     * @param <T> the selectable class' type
     */
    public static final class Getter<T> {
        private final Holder<T> holder;

        Getter(final Holder<T> holder) {
            this.holder = holder;
        }

        /**
         * Get the selector for this getter.  No permission checks are performed.
         *
         * @return the selector
         */
        public Selector<T> getSelector() {
            final Selector<T> sel = holder.get();
            return sel == null ? Selector.<T>nullSelector() : sel;
        }
    }

    @SuppressWarnings("serial")
    static final class Holder<T> extends AtomicReference<Selector<T>> {
        private final Class<T> clazz;
        private final SelectorPermission getPermission;
        private final SelectorPermission setPermission;
        private final SelectorPermission changePermission;
        private final AtomicReference<Object> lockRef = new AtomicReference<>();

        Holder(final Class<T> clazz) {
            Assert.assertNotNull(clazz);
            this.clazz = clazz;
            getPermission = new SelectorPermission(clazz.getName(), "get");
            setPermission = new SelectorPermission(clazz.getName(), "set");
            changePermission = new SelectorPermission(clazz.getName(), "change");
        }

        Class<T> getClazz() {
            return clazz;
        }

        SelectorPermission getGetPermission() {
            return getPermission;
        }

        SelectorPermission getSetPermission() {
            return setPermission;
        }

        SelectorPermission getChangePermission() {
            return changePermission;
        }

        void lock(Object key) {
            Assert.assertNotNull(key);
            if (! lockRef.compareAndSet(null, key)) {
                throw new SecurityException("Selector is locked");
            }
        }

        void unlock(Object key) {
            Assert.assertNotNull(key);
            if (! lockRef.compareAndSet(key, null)) {
                throw new SecurityException("Selector could not be unlocked");
            }
        }
    }
}
