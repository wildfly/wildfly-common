package org.wildfly.common;

/**
 * @deprecated Please use {@link org.wildfly.common.net.HostName} instead.
 */
public final class HostName {

    /**
     * @deprecated Please use {@link org.wildfly.common.net.HostName#getHostName()} instead.
     */
    public static String getHostName() {
        return org.wildfly.common.net.HostName.getHostName();
    }

    /**
     * @deprecated Please use {@link org.wildfly.common.net.HostName#getQualifiedHostName()} instead.
     */
    public static String getQualifiedHostName() {
        return org.wildfly.common.net.HostName.getQualifiedHostName();
    }
}
