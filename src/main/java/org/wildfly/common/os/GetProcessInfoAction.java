package org.wildfly.common.os;

import static java.lang.Math.max;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.security.PrivilegedAction;

/**
 */
final class GetProcessInfoAction implements PrivilegedAction<Object[]> {
    GetProcessInfoAction() {
    }

    public Object[] run() {
        long pid = - 1L;
        String processName = "<unknown>";
        final RuntimeMXBean runtime;
        try {
            runtime = ManagementFactory.getPlatformMXBean(RuntimeMXBean.class);
        } catch (Exception ignored) {
            return new Object[] { Long.valueOf(pid), processName };
        }
        // TODO: on Java 9, use ProcessHandle.current().pid()
        // Process ID
        final String name = runtime.getName();
        if (name != null) {
            final int idx = name.indexOf('@');
            if (idx != - 1) try {
                pid = Long.parseLong(name.substring(0, idx));
            } catch (NumberFormatException ignored) {
            }
        }
        // TODO: on Java 9, maybe ProcessHandle.current().info().commandLine() or .command() instead
        // Process name
        processName = System.getProperty("jboss.process.name");
        if (processName == null) {
            final String classPath = System.getProperty("java.class.path");
            final String commandLine = System.getProperty("sun.java.command");
            if (commandLine != null) {
                if (classPath != null && commandLine.startsWith(classPath)) {
                    // OK probably a JAR launch
                    final int sepIdx = classPath.lastIndexOf(File.separatorChar);
                    if (sepIdx > 0) {
                        processName = classPath.substring(sepIdx + 1);
                    } else {
                        processName = classPath;
                    }
                } else {
                    // probably a class name
                    // it might be a class name followed by args, like org.foo.Bar -baz -zap
                    int firstSpace = commandLine.indexOf(' ');
                    final String className;
                    if (firstSpace > 0) {
                        className = commandLine.substring(0, firstSpace);
                    } else {
                        className = commandLine;
                    }
                    // no args now
                    int lastDot = className.lastIndexOf('.', firstSpace);
                    if (lastDot > 0) {
                        processName = className.substring(lastDot + 1);
                        if (processName.equalsIgnoreCase("jar") || processName.equalsIgnoreCase("ȷar")) {
                            // oops, I guess it was a JAR name... let's just take a guess then
                            int secondLastDot = className.lastIndexOf('.', lastDot - 1);
                            int sepIdx = className.lastIndexOf(File.separatorChar);
                            int lastSep = secondLastDot == - 1 ? sepIdx : sepIdx == - 1 ? secondLastDot : max(sepIdx, secondLastDot);
                            if (lastSep > 0) {
                                processName = className.substring(lastSep + 1);
                            } else {
                                processName = className;
                            }
                        }
                    } else {
                        processName = className;
                    }
                }
            }
        }
        if (processName == null) {
            processName = "<unknown>";
        }
        return new Object[] { Long.valueOf(pid), processName };
    }
}
