/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2016 Red Hat, Inc., and individual contributors
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

package org.wildfly.common.context;

import static java.security.AccessController.doPrivileged;

import java.security.PrivilegedAction;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import org.wildfly.common.Assert;

/**
 * A context manager for a {@link Contextual} type.
 *
 * @param <C> the public type of the contextual object
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class ContextManager<C extends Contextual<C>> implements Supplier<C> {
    private final AtomicReference<Supplier<C>> globalDefaultSupplierRef = new AtomicReference<>();
    private final ConcurrentHashMap<ClassLoader, Supplier<C>> perClassLoaderDefault = new ConcurrentHashMap<>();
    private final Class<C> type;
    private final String name;
    private final ThreadLocal<State<C>> stateRef = ThreadLocal.withInitial(State::new);
    private final ContextPermission getPermission;

    /**
     * Construct a new instance, with a name matching the class name of the given {@code type}.
     *
     * @param type the type class of the context object (must not be {@code null})
     */
    public ContextManager(final Class<C> type) {
        this(type, type.getName());
    }

    /**
     * Construct a new instance.
     *
     * @param type the type class of the context object (must not be {@code null})
     * @param name the name to use for permission checks (must not be {@code null} or empty)
     */
    public ContextManager(final Class<C> type, final String name) {
        Assert.checkNotNullParam("type", type);
        Assert.checkNotNullParam("name", name);
        Assert.checkNotEmptyParam("name", name);
        this.type = type;
        this.name = name;
        // construct commonly-used permission object
        getPermission = new ContextPermission(name, ContextPermission.STR_GET);
    }

    /**
     * Get the global default context instance.  Note that the global default is determined by way of a {@link Supplier} so
     * the returned value may vary from call to call, depending on the policy of that {@code Supplier}.
     *
     * @return the global default, or {@code null} if none is installed or available
     */
    public C getGlobalDefault() {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new ContextPermission(name, ContextPermission.STR_GET_GLOBAL_DEF));
        }
        final Supplier<C> globalDefault = globalDefaultSupplierRef.get();
        return globalDefault == null ? null : globalDefault.get();
    }

    /**
     * Get the global default supplier instance.
     *
     * @return the global default supplier, or {@code null} if none is installed or available
     */
    public Supplier<C> getGlobalDefaultSupplier() {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new ContextPermission(name, ContextPermission.STR_GET_GLOBAL_DEF));
        }
        return globalDefaultSupplierRef.get();
    }

    /**
     * Set the global default instance supplier.  The supplier, if one is given, should have a reasonable policy such
     * that callers of {@link #getGlobalDefault()} will obtain results consistent with a general expectation of stability.
     *
     * @param supplier the supplier, or {@code null} to remove the global default
     */
    public void setGlobalDefaultSupplier(final Supplier<C> supplier) {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new ContextPermission(name, ContextPermission.STR_SET_GLOBAL_DEF_SUP));
        }
        globalDefaultSupplierRef.set(supplier);
    }

    /**
     * Set the global default instance supplier, but only if it was not already set.  If no supplier is set, the given
     * supplier supplier is queried to get the new value to set.
     *
     * @param supplierSupplier the supplier supplier (must not be {@code null})
     * @return {@code true} if the supplier was set, {@code false} if it was already set to something else
     * @see #setGlobalDefaultSupplier(Supplier)
     */
    public boolean setGlobalDefaultSupplierIfNotSet(final Supplier<Supplier<C>> supplierSupplier) {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new ContextPermission(name, ContextPermission.STR_SET_GLOBAL_DEF_SUP));
        }
        final AtomicReference<Supplier<C>> ref = this.globalDefaultSupplierRef;
        // try not to compute the value if not needed
        return ref.get() == null && ref.compareAndSet(null, supplierSupplier.get());
    }

    /**
     * Set the global default instance.  This instance will be returned from all subsequent calls to {@link #getGlobalDefault()},
     * replacing any previous instance or {@linkplain #setGlobalDefaultSupplier(Supplier) supplier} that was set.
     *
     * @param globalDefault the global default value, or {@code null} to remove the global default
     */
    public void setGlobalDefault(final C globalDefault) {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new ContextPermission(name, ContextPermission.STR_GET_GLOBAL_DEF));
        }
        globalDefaultSupplierRef.set(globalDefault == null ? null : () -> globalDefault);
    }

    /**
     * Get the class loader default instance.  Note that the class loader default is determined by way of a {@link Supplier} so
     * the returned value may vary from call to call, depending on the policy of that {@code Supplier}.
     *
     * @param classLoader the class loader
     * @return the global default, or {@code null} if none is installed or available
     */
    public C getClassLoaderDefault(final ClassLoader classLoader) {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new ContextPermission(name, ContextPermission.STR_GET_CLASSLOADER_DEF));
        }
        final Supplier<C> supplier;
        if (classLoader == null) {
            return null;
        }
        supplier = perClassLoaderDefault.get(classLoader);
        return supplier == null ? null : supplier.get();
    }

    /**
     * Get the class loader default supplier.\
     *
     * @param classLoader the class loader
     * @return the global default, or {@code null} if none is installed or available
     */
    public Supplier<C> getClassLoaderDefaultSupplier(final ClassLoader classLoader) {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new ContextPermission(name, ContextPermission.STR_GET_CLASSLOADER_DEF));
        }
        if (classLoader == null) {
            return null;
        }
        return perClassLoaderDefault.get(classLoader);
    }

    /**
     * Set the per-class loader default instance supplier.  The supplier, if one is given, should have a reasonable policy such
     * that callers of {@link #getClassLoaderDefault(ClassLoader)} will obtain results consistent with a general expectation of stability.
     *
     * @param classLoader the class loader (must not be {@code null})
     * @param supplier the supplier, or {@code null} to remove the default for this class loader
     */
    public void setClassLoaderDefaultSupplier(final ClassLoader classLoader, final Supplier<C> supplier) {
        Assert.checkNotNullParam("classLoader", classLoader);
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new ContextPermission(name, ContextPermission.STR_SET_CLASSLOADER_DEF_SUP));
        }
        if (supplier == null) {
            perClassLoaderDefault.remove(classLoader);
        } else {
            perClassLoaderDefault.put(classLoader, supplier);
        }
    }

    /**
     * Set the per-class loader default instance supplier.  The supplier, if one is given, should have a reasonable policy such
     * that callers of {@link #getClassLoaderDefault(ClassLoader)} will obtain results consistent with a general expectation of stability.
     *
     * @param classLoader the class loader (must not be {@code null})
     * @param classLoaderDefault the class loader default value, or {@code null} to remove the default
     */
    public void setClassLoaderDefault(final ClassLoader classLoader, final C classLoaderDefault) {
        Assert.checkNotNullParam("classLoader", classLoader);
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new ContextPermission(name, ContextPermission.STR_SET_CLASSLOADER_DEF));
        }
        if (classLoaderDefault == null) {
            perClassLoaderDefault.remove(classLoader);
        } else {
            perClassLoaderDefault.put(classLoader, () -> classLoaderDefault);
        }
    }

    /**
     * Get the per-thread default context instance.  Note that the per-thread default is determined by way of a {@link Supplier} so
     * the returned value may vary from call to call, depending on the policy of that {@code Supplier}.
     *
     * @return the per-thread default, or {@code null} if none is installed or available
     */
    public C getThreadDefault() {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new ContextPermission(name, ContextPermission.STR_GET_THREAD_DEF));
        }
        final Supplier<C> defaultSupplier = stateRef.get().defaultSupplier;
        return defaultSupplier == null ? null : defaultSupplier.get();
    }

    /**
     * Get the per-thread default context instance.
     *
     * @return the per-thread default supplier, or {@code null} if none is installed or available
     */
    public Supplier<C> getThreadDefaultSupplier() {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new ContextPermission(name, ContextPermission.STR_GET_THREAD_DEF));
        }
        return stateRef.get().defaultSupplier;
    }

    /**
     * Set the per-thread default instance supplier.  The supplier, if one is given, should have a reasonable policy such
     * that callers of {@link #getThreadDefault()} will obtain results consistent with a general expectation of stability.
     *
     * @param supplier the supplier, or {@code null} to remove the per-thread default
     */
    public void setThreadDefaultSupplier(final Supplier<C> supplier) {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new ContextPermission(name, ContextPermission.STR_SET_THREAD_DEF_SUP));
        }
        stateRef.get().defaultSupplier = supplier;
    }

    /**
     * Set the per-thread default instance.  This instance will be returned from all subsequent calls to {@link #getThreadDefault()},
     * replacing any previous instance or {@linkplain #setThreadDefaultSupplier(Supplier) supplier} that was set.
     *
     * @param threadDefault the per-thread default value, or {@code null} to remove the per-thread default
     */
    public void setThreadDefault(final C threadDefault) {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new ContextPermission(name, ContextPermission.STR_SET_THREAD_DEF));
        }
        stateRef.get().defaultSupplier = threadDefault == null ? null : () -> threadDefault;
    }

    /**
     * Get the currently active context, possibly examining the per-thread or global defaults.
     *
     * @return the current context, or {@code null} if none is active
     */
    public C get() {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(getPermission);
        }
        return getPrivileged();
    }

    /**
     * Get a privileged supplier for this context manager which returns the currently active context without a permission
     * check.
     *
     * @return the privileged supplier
     */
    public Supplier<C> getPrivilegedSupplier() {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new ContextPermission(name, ContextPermission.STR_GET_PRIV_SUP));
        }
        return this::getPrivileged;
    }

    private C getPrivileged() {
        final State<C> state = stateRef.get();
        C c = state.current;
        if (c != null) return c;
        final Thread currentThread = Thread.currentThread();
        final SecurityManager sm = System.getSecurityManager();
        ClassLoader classLoader;
        if (sm != null) {
            classLoader = doPrivileged((PrivilegedAction<ClassLoader>) currentThread::getContextClassLoader);
        } else {
            classLoader = currentThread.getContextClassLoader();
        }
        Supplier<C> supplier;
        if (classLoader != null) {
            supplier = perClassLoaderDefault.get(classLoader);
            if (supplier != null) {
                c = supplier.get();
                if (c != null) return c;
            }
        }
        supplier = state.defaultSupplier;
        if (supplier != null) {
            c = supplier.get();
            if (c != null) return c;
        }
        supplier = globalDefaultSupplierRef.get();
        return supplier != null ? supplier.get() : null;
    }

    C getAndSetCurrent(Contextual<C> newVal) {
        final C cast = type.cast(newVal);
        final State<C> state = stateRef.get();
        try {
            return state.current;
        } finally {
            state.current = cast;
        }
    }

    void restoreCurrent(C oldVal) {
        stateRef.get().current = oldVal;
    }

    static class State<T> {
        T current;
        Supplier<T> defaultSupplier;

        State() {
        }
    }
}
