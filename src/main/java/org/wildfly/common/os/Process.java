package org.wildfly.common.os;

import static java.security.AccessController.doPrivileged;

/**
 * Utilities for getting information about the current process.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class Process {
    private static final long processId;
    private static final String processName;

    static {
        Object[] array = doPrivileged(new GetProcessInfoAction());
        processId = ((Long) array[0]).longValue();
        processName = (String) array[1];
    }

    private Process() {
    }

    /**
     * Get the name of this process.  If the process name is not known, then "&lt;unknown&gt;" is returned.
     *
     * @return the process name (not {@code null})
     */
    public static String getProcessName() {
        return processName;
    }

    /**
     * Get the ID of this process.  This is the operating system specific PID.  If the PID cannot be determined,
     * -1 is returned.
     *
     * @return the ID of this process, or -1 if it cannot be determined
     */
    public static long getProcessId() {
        return processId;
    }
}
