package org.wildfly.common.os;

import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;

/**
 * Process redirections that work on all JDK versions.
 */
public final class ProcessRedirect {
    private ProcessRedirect() {}

    /**
     * Get the "discard" process redirection target.
     *
     * @return the discarding redirection target
     */
    public static ProcessBuilder.Redirect discard() {
        return ProcessBuilder.Redirect.to(new File(isWindows() ? "NUL" : "/dev/null"));
    }

    private static boolean isWindows() {
        final SecurityManager sm = System.getSecurityManager();
        return (sm == null ? getOsName() : getOsNamePrivileged()).toLowerCase(Locale.ROOT).contains("windows");
    }

    private static String getOsNamePrivileged() {
        return AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() {
                return getOsName();
            }
        });
    }

    private static String getOsName() {
        return System.getProperty("os.name", "unknown");
    }
}
