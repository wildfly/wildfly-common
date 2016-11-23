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

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.function.Supplier;

import org.wildfly.common.Assert;
import org.wildfly.common._private.CommonMessages;

/**
 * A permission object for operations on {@link ContextManager} instances.
 * <p>
 * This type of permission requires a {@code name} and an {@code action}.  The {@code name} may be the name
 * of a context manager, or the special {@code *} name which means the permission applies to all context managers.
 * <p>
 * The {@code action} may be one or more of the following (each action name being separated by a comma):
 * <ul>
 *     <li>{@code get} - allow {@linkplain ContextManager#get() getting} the current context</li>
 *     <li>{@code getPrivilegedSupplier} - allow access to the {@link ContextManager#getPrivilegedSupplier()} method</li>
 *     <li>{@code getGlobalDefault} - allow access to the {@linkplain ContextManager#getGlobalDefault() global default context}</li>
 *     <li>{@code setGlobalDefault} - allow {@linkplain ContextManager#setGlobalDefault(Contextual) setting the global default instance}</li>
 *     <li>{@code setGlobalDefaultSupplier} - allow {@linkplain ContextManager#setGlobalDefaultSupplier(Supplier) setting the global default instance supplier}</li>
 *     <li>{@code getThreadDefault} - allow access to the {@linkplain ContextManager#getThreadDefault() per-thread default context}</li>
 *     <li>{@code setThreadDefault} - allow {@linkplain ContextManager#setThreadDefault(Contextual) setting the per-thread default instance}</li>
 *     <li>{@code setThreadDefaultSupplier} - allow {@linkplain ContextManager#setThreadDefaultSupplier(Supplier) setting the per-thread default instance supplier}</li>
 * </ul>
 * Additionally, the special {@code *} action name is allowed which implies all of the above actions.
 * <p>
 * The {@link #newPermissionCollection()} method returns an optimized container for context permissions.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class ContextPermission extends Permission {

    private static final long serialVersionUID = 2149744699461086708L;

    private static final int ACTION_GET                     = 0b00000000001;
    private static final int ACTION_GET_PRIV_SUP            = 0b00000000010;
    private static final int ACTION_GET_GLOBAL_DEF          = 0b00000000100;
    private static final int ACTION_SET_GLOBAL_DEF          = 0b00000001000;
    private static final int ACTION_SET_GLOBAL_DEF_SUP      = 0b00000010000;
    private static final int ACTION_GET_THREAD_DEF          = 0b00000100000;
    private static final int ACTION_SET_THREAD_DEF          = 0b00001000000;
    private static final int ACTION_SET_THREAD_DEF_SUP      = 0b00010000000;
    private static final int ACTION_GET_CLASSLOADER_DEF     = 0b00100000000;
    private static final int ACTION_SET_CLASSLOADER_DEF     = 0b01000000000;
    private static final int ACTION_SET_CLASSLOADER_DEF_SUP = 0b10000000000;

    private static final int ACTION_ALL                     = 0b11111111111;

    static final String STR_GET = "get";
    static final String STR_GET_PRIV_SUP = "getPrivilegedSupplier";
    static final String STR_GET_GLOBAL_DEF = "getGlobalDefault";
    static final String STR_SET_GLOBAL_DEF = "setGlobalDefault";
    static final String STR_SET_GLOBAL_DEF_SUP = "setGlobalDefaultSupplier";
    static final String STR_GET_THREAD_DEF = "getThreadDefault";
    static final String STR_SET_THREAD_DEF = "setThreadDefault";
    static final String STR_SET_THREAD_DEF_SUP = "setThreadDefaultSupplier";
    static final String STR_GET_CLASSLOADER_DEF = "getClassLoaderDefault";
    static final String STR_SET_CLASSLOADER_DEF = "setClassLoaderDefault";
    static final String STR_SET_CLASSLOADER_DEF_SUP = "setClassLoaderDefaultSupplier";

    private final transient int actionBits;

    private transient String actionString;

    /**
     * Constructs a permission with the specified name.
     *
     * @param name name of the Permission object being created (must not be {@code null})
     * @param actions the actions string (must not be {@code null})
     */
    public ContextPermission(final String name, final String actions) {
        super(name);
        Assert.checkNotNullParam("name", name);
        Assert.checkNotNullParam("actions", actions);
        actionBits = parseActions(actions);
    }

    ContextPermission(final String name, final int actionBits) {
        super(name);
        Assert.checkNotNullParam("name", name);
        this.actionBits = actionBits & ACTION_ALL;
    }

    private static int parseActions(final String actions) throws IllegalArgumentException {
        int bits = 0;
        int start = 0;
        int idx = actions.indexOf(',');
        if (idx == -1) {
            return parseAction(actions);
        } else do {
            bits |= parseAction(actions.substring(start, idx));
            start = idx + 1;
            idx = actions.indexOf(',', start);
        } while (idx != -1);
        bits |= parseAction(actions.substring(start));
        return bits;
    }

    private static int parseAction(final String action) {
        switch (action.trim()) {
            case STR_GET: return ACTION_GET;
            case STR_GET_PRIV_SUP: return ACTION_GET_PRIV_SUP;
            case STR_GET_GLOBAL_DEF: return ACTION_GET_GLOBAL_DEF;
            case STR_SET_GLOBAL_DEF: return ACTION_SET_GLOBAL_DEF;
            case STR_SET_GLOBAL_DEF_SUP: return ACTION_SET_GLOBAL_DEF_SUP;
            case STR_GET_THREAD_DEF: return ACTION_GET_THREAD_DEF;
            case STR_SET_THREAD_DEF: return ACTION_SET_THREAD_DEF;
            case STR_SET_THREAD_DEF_SUP: return ACTION_SET_THREAD_DEF_SUP;
            case STR_GET_CLASSLOADER_DEF: return ACTION_GET_CLASSLOADER_DEF;
            case STR_SET_CLASSLOADER_DEF: return ACTION_SET_CLASSLOADER_DEF;
            case STR_SET_CLASSLOADER_DEF_SUP: return ACTION_SET_CLASSLOADER_DEF_SUP;
            case "*": return ACTION_ALL;
            case "": return 0;
            default: {
                throw CommonMessages.msg.invalidPermissionAction(action);
            }
        }
    }

    /**
     * Determine if the given permission is implied by this permission.
     *
     * @param permission the other permission
     * @return {@code true} if the other permission is not {@code null} and is a context permission which is implied by
     *  this permission instance; {@code false} otherwise
     */
    public boolean implies(final Permission permission) {
        return permission instanceof ContextPermission && implies((ContextPermission) permission);
    }

    /**
     * Determine if the given permission is implied by this permission.
     *
     * @param permission the other permission
     * @return {@code true} if the other permission is not {@code null} and is a context permission which is implied by
     *  this permission instance; {@code false} otherwise
     */
    public boolean implies(final ContextPermission permission) {
        return this == permission || permission != null && isSet(this.actionBits, permission.actionBits) && impliesName(permission.getName());
    }

    private boolean impliesName(String otherName) {
        final String myName = getName();
        return myName.equals("*") || myName.equals(otherName);
    }

    static boolean isSet(int mask, int test) {
        return (mask & test) == test;
    }

    /**
     * Determine if this permission is equal to the given object.
     *
     * @param obj the other object
     * @return {@code true} if the object is a context permission that is exactly equal to this one; {@code false} otherwise
     */
    public boolean equals(final Object obj) {
        return obj instanceof ContextPermission && equals((ContextPermission) obj);
    }

    /**
     * Determine if this permission is equal to the given permission.
     *
     * @param permission the other permission
     * @return {@code true} if the permission is a context permission that is exactly equal to this one; {@code false} otherwise
     */
    public boolean equals(final ContextPermission permission) {
        return this == permission || permission != null && actionBits == permission.actionBits && getName().equals(permission.getName());
    }

    /**
     * Get the hash code of this permission.
     *
     * @return the hash code of this permission
     */
    public int hashCode() {
        return getName().hashCode() * 17 + actionBits;
    }

    /**
     * Get the actions string.  This string will be returned in a canonical format.
     *
     * @return the actions string
     */
    public String getActions() {
        String actionString = this.actionString;
        if (actionString == null) {
            final int actionBits = this.actionBits;
            if (isSet(actionBits, ACTION_ALL)) {
                return this.actionString = "*";
            } else if (actionBits == 0) {
                return this.actionString = "";
            }
            final StringBuilder b = new StringBuilder();
            if (isSet(actionBits, ACTION_GET)) b.append(STR_GET).append(',');
            if (isSet(actionBits, ACTION_GET_PRIV_SUP)) b.append(STR_GET_PRIV_SUP).append(',');
            if (isSet(actionBits, ACTION_GET_GLOBAL_DEF)) b.append(STR_GET_GLOBAL_DEF).append(',');
            if (isSet(actionBits, ACTION_SET_GLOBAL_DEF)) b.append(STR_SET_GLOBAL_DEF).append(',');
            if (isSet(actionBits, ACTION_SET_GLOBAL_DEF_SUP)) b.append(STR_SET_GLOBAL_DEF_SUP).append(',');
            if (isSet(actionBits, ACTION_GET_THREAD_DEF)) b.append(STR_GET_THREAD_DEF).append(',');
            if (isSet(actionBits, ACTION_SET_THREAD_DEF)) b.append(STR_SET_THREAD_DEF).append(',');
            if (isSet(actionBits, ACTION_SET_THREAD_DEF_SUP)) b.append(STR_SET_THREAD_DEF_SUP).append(',');
            if (isSet(actionBits, ACTION_GET_CLASSLOADER_DEF)) b.append(STR_GET_CLASSLOADER_DEF).append(',');
            if (isSet(actionBits, ACTION_SET_CLASSLOADER_DEF)) b.append(STR_SET_CLASSLOADER_DEF).append(',');
            if (isSet(actionBits, ACTION_SET_CLASSLOADER_DEF_SUP)) b.append(STR_SET_CLASSLOADER_DEF_SUP).append(',');
            assert b.length() > 0;
            b.setLength(b.length() - 1);
            return this.actionString = b.toString();
        }
        return actionString;
    }

    /**
     * Create a copy of this permission with the additional given actions.
     *
     * @param actions the additional actions (must not be {@code null})
     * @return the new permission (not {@code null})
     */
    @org.jetbrains.annotations.NotNull
    public ContextPermission withActions(String actions) {
        return withActionBits(parseActions(actions));
    }

    ContextPermission withActionBits(int actionBits) {
        if (isSet(this.actionBits, actionBits)) {
            return this;
        } else {
            return new ContextPermission(getName(), this.actionBits | actionBits);
        }
    }

    /**
     * Create a copy of this permission without any of the given actions.
     *
     * @param actions the actions to subtract (must not be {@code null})
     * @return the new permission (not {@code null})
     */
    @org.jetbrains.annotations.NotNull
    public ContextPermission withoutActions(String actions) {
        return withoutActionBits(parseActions(actions));
    }

    ContextPermission withoutActionBits(final int actionBits) {
        if ((actionBits & this.actionBits) == 0) {
            return this;
        } else {
            return new ContextPermission(getName(), this.actionBits & ~actionBits);
        }
    }

    /**
     * Get a new permission collection instance which can hold this type of permissions.
     *
     * @return a new permission collection instance (not {@code null})
     */
    public PermissionCollection newPermissionCollection() {
        return new ContextPermissionCollection();
    }

    int getActionBits() {
        return actionBits;
    }
}
