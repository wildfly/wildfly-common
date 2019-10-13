package org.wildfly.common.selector;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.security.BasicPermission;
import java.security.Permission;

import org.wildfly.common.Assert;

/**
 * Permission to access a specific selector.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class SelectorPermission extends BasicPermission {

    private static final long serialVersionUID = -7156787601824624014L;

    private static final int ACTION_GET = 1;
    private static final int ACTION_SET = 2;
    private static final int ACTION_CHANGE = 4;

    private final int actions;

    public SelectorPermission(final String name, final String actions) {
        super(name);
        Assert.checkNotNullParam("name", name);
        Assert.checkNotNullParam("actions", actions);
        final String[] actionArray = actions.split("\\s*,\\s*");
        int q = 0;
        for (String action : actionArray) {
            if (action.equalsIgnoreCase("get")) {
                q |= ACTION_GET;
            } else if (action.equalsIgnoreCase("set")) {
                q |= ACTION_SET;
            } else if (action.equalsIgnoreCase("change")) {
                q |= ACTION_CHANGE;
            } else if (action.equals("*")) {
                q |= ACTION_GET | ACTION_SET | ACTION_CHANGE;
                break;
            }
        }
        this.actions = q;
    }

    public String getActions() {
        // few enough
        final int maskedActions = actions & 0b111;
        switch (maskedActions) {
            case 0b000: return "";
            case 0b001: return "get";
            case 0b010: return "set";
            case 0b011: return "get,set";
            case 0b100: return "change";
            case 0b101: return "get,change";
            case 0b110: return "set,change";
            case 0b111: return "get,set,change";
            default: throw Assert.impossibleSwitchCase(maskedActions);
        }
    }

    public boolean implies(final Permission p) {
        return p instanceof SelectorPermission && implies((SelectorPermission) p);
    }

    public boolean implies(final SelectorPermission p) {
        return p != null && (p.actions & actions) == p.actions && super.implies(p);
    }

    public boolean equals(final Object p) {
        return p instanceof SelectorPermission && equals((SelectorPermission) p);
    }

    public boolean equals(final SelectorPermission p) {
        return p != null && (p.actions == actions) && super.equals(p);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        final int actions = this.actions;
        if ((actions & 0b111) != actions) {
            throw new InvalidObjectException("Invalid permission actions");
        }
    }
}
