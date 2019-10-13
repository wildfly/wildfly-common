package org.wildfly.common.context;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import org.wildfly.common.Assert;
import org.wildfly.common._private.CommonMessages;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
final class ContextPermissionCollection extends PermissionCollection {
    private static final long serialVersionUID = - 3651721703337368351L;

    private volatile State state = emptyState;

    private static final AtomicReferenceFieldUpdater<ContextPermissionCollection, State> stateUpdater = AtomicReferenceFieldUpdater.newUpdater(ContextPermissionCollection.class, State.class, "state");

    public void add(final Permission permission) throws SecurityException, IllegalArgumentException {
        Assert.checkNotNullParam("permission", permission);
        if (permission instanceof ContextPermission) {
            add((ContextPermission) permission);
        } else {
            throw CommonMessages.msg.invalidPermissionType(ContextPermission.class, permission.getClass());
        }
    }

    public void add(final ContextPermission contextPermission) throws SecurityException {
        Assert.checkNotNullParam("contextPermission", contextPermission);
        if (isReadOnly()) {
            throw CommonMessages.msg.readOnlyPermissionCollection();
        }
        final int actionBits = contextPermission.getActionBits();
        if (actionBits == 0) {
            // no operation
            return;
        }
        final String name = contextPermission.getName();
        State oldState, newState;
        do {
            oldState = this.state;
            final ContextPermission oldGlobalPermission = oldState.globalPermission;
            final int globalActions = oldGlobalPermission == null ? 0 : oldGlobalPermission.getActionBits();
            if (oldGlobalPermission != null && oldGlobalPermission.implies(contextPermission)) {
                // already fully implied by global permission
                return;
            } else {
                final Map<String, ContextPermission> oldPermissions = oldState.permissions;
                final Map<String, ContextPermission> newPermissions;
                final ContextPermission newGlobalPermission;
                if (name.equals("*")) {
                    // it's global but with some bits we don't have; calculate the new global actions and subtract from the map
                    if (oldGlobalPermission == null) {
                        newGlobalPermission = contextPermission;
                    } else {
                        newGlobalPermission = oldGlobalPermission.withActionBits(contextPermission.getActionBits());
                    }

                    // now subtract
                    newPermissions = cloneWithout(oldPermissions, newGlobalPermission);
                } else {
                    newGlobalPermission = oldGlobalPermission;
                    // it's not global; check & add actions to our map permission
                    final ContextPermission mapPermission = oldPermissions.get(name);
                    if (mapPermission == null) {
                        // no map entry; just create one (but without any global actions we have defined)
                        if (oldPermissions.isEmpty()) {
                            // change empty map to singleton map
                            newPermissions = Collections.singletonMap(name, contextPermission.withoutActionBits(globalActions));
                        } else {
                            // make a copy of the map plus the new entry
                            newPermissions = new HashMap<>(oldPermissions);
                            newPermissions.put(name, contextPermission.withoutActionBits(globalActions));
                        }
                    } else if (((mapPermission.getActionBits() | globalActions) & actionBits) == actionBits) {
                        // already fully implied by a map entry
                        return;
                    } else {
                        // replace the map entry
                        if (oldPermissions.size() == 1) {
                            // it was a singleton map, just replace it
                            newPermissions = Collections.singletonMap(name, mapPermission.withActionBits(actionBits & ~globalActions));
                        } else {
                            // copy the map and replace the entry
                            newPermissions = new HashMap<>(oldPermissions);
                            newPermissions.put(name, mapPermission.withActionBits(actionBits & ~globalActions));
                        }
                    }
                }
                newState = new State(newGlobalPermission, newPermissions);
            }
        } while (! stateUpdater.compareAndSet(this, oldState, newState));
    }

    private static Map<String, ContextPermission> cloneWithout(final Map<String, ContextPermission> oldPermissions, final ContextPermission newGlobalPermission) {
        final Iterator<ContextPermission> iterator = oldPermissions.values().iterator();
        ContextPermission first;
        for (;;) {
            if (! iterator.hasNext()) {
                return Collections.emptyMap();
            }
            first = iterator.next();
            if (! newGlobalPermission.implies(first)) {
                // break into next phase
                break;
            }
        }
        final int globalActionBits = newGlobalPermission.getActionBits();
        ContextPermission second;
        for (;;) {
            if (! iterator.hasNext()) {
                return Collections.singletonMap(first.getName(), first.withoutActionBits(globalActionBits));
            }
            second = iterator.next();
            if (! newGlobalPermission.implies(second)) {
                // break into next phase
                break;
            }
        }
        HashMap<String, ContextPermission> newMap = new HashMap<>();
        newMap.put(first.getName(), first.withoutActionBits(globalActionBits));
        newMap.put(second.getName(), second.withoutActionBits(globalActionBits));
        ContextPermission subsequent;
        while (iterator.hasNext()) {
            subsequent = iterator.next();
            if (! newGlobalPermission.implies(subsequent)) {
                newMap.put(subsequent.getName(), subsequent.withoutActionBits(globalActionBits));
            }
        }
        return newMap;
    }

    public boolean implies(final Permission permission) {
        return permission instanceof ContextPermission && implies((ContextPermission) permission);
    }

    public boolean implies(final ContextPermission permission) {
        if (permission == null) return false;
        final State state = this.state;
        final ContextPermission globalPermission = state.globalPermission;
        final int globalBits;
        if (globalPermission != null) {
            if (globalPermission.implies(permission)) {
                return true;
            }
            globalBits = globalPermission.getActionBits();
        } else {
            globalBits = 0;
        }
        final int bits = permission.getActionBits();
        final String name = permission.getName();
        if (name.equals("*")) {
            return false;
        }
        final ContextPermission ourPermission = state.permissions.get(name);
        if (ourPermission == null) {
            return false;
        }
        final int ourBits = ourPermission.getActionBits() | globalBits;
        return (bits & ourBits) == bits;
    }

    public Enumeration<Permission> elements() {
        final State state = this.state;
        final Iterator<ContextPermission> iterator = state.permissions.values().iterator();
        return new Enumeration<Permission>() {
            Permission next = state.globalPermission;

            public boolean hasMoreElements() {
                if (next != null) {
                    return true;
                }
                if (iterator.hasNext()) {
                    next = iterator.next();
                    return true;
                }
                return false;
            }

            public Permission nextElement() {
                if (! hasMoreElements()) throw new NoSuchElementException();
                try {
                    return next;
                } finally {
                    next = null;
                }
            }
        };
    }

    static class State {
        private final ContextPermission globalPermission;
        private final Map<String, ContextPermission> permissions;

        State(final ContextPermission globalPermission, final Map<String, ContextPermission> permissions) {
            this.globalPermission = globalPermission;
            this.permissions = permissions;
        }
    }

    private static final State emptyState = new State(null, Collections.emptyMap());
}
