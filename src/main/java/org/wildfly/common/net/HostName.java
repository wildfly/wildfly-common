/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2017 Red Hat, Inc., and individual contributors
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

package org.wildfly.common.net;

/**
 * Methods for getting the system host name.  The host name is detected from the environment, but may be overridden by
 * use of the {@code jboss.host.name} and/or {@code jboss.qualified.host.name} system properties.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 * @deprecated Use {@link io.smallrye.common.net.HostName} instead.
 */
@Deprecated(forRemoval = true)
public final class HostName {

    /**
     * Get the detected host name.
     *
     * @return the detected host name
     * @deprecated Use {@link io.smallrye.common.net.HostName#getHostName()} instead.
     */
    @Deprecated(forRemoval = true)
    public static String getHostName() {
        return io.smallrye.common.net.HostName.getHostName();
    }

    /**
     * Get the detected qualified host name.
     *
     * @return the detected qualified host name
     * @deprecated Use {@link io.smallrye.common.net.HostName#getQualifiedHostName()} instead.
     */
    @Deprecated(forRemoval = true)
    public static String getQualifiedHostName() {
        return io.smallrye.common.net.HostName.getQualifiedHostName();
    }

    /**
     * Get the node name.
     *
     * @return the node name
     * @deprecated Use {@link io.smallrye.common.net.HostName#getNodeName()} instead.
     */
    @Deprecated(forRemoval = true)
    public static String getNodeName() {
        return io.smallrye.common.net.HostName.getNodeName();
    }

    /**
     * Set the host name.  The qualified host name is set directly from the given value; the unqualified host name
     * is then re-derived from that value.  The node name is not changed by this method.
     *
     * @param qualifiedHostName the host name
     * @deprecated Use {@link io.smallrye.common.net.HostName#setQualifiedHostName(String)} instead.
     */
    @Deprecated(forRemoval = true)
    public static void setQualifiedHostName(final String qualifiedHostName) {
        io.smallrye.common.net.HostName.setQualifiedHostName(qualifiedHostName);
    }

    /**
     * Set the node name.
     *
     * @param nodeName the node name
     * @deprecated Use {@link io.smallrye.common.net.HostName#setNodeName(String)} instead.
     */
    @Deprecated(forRemoval = true)
    public static void setNodeName(final String nodeName) {
        io.smallrye.common.net.HostName.setNodeName(nodeName);
    }
}
