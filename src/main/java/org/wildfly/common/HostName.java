/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2015 Red Hat, Inc., and individual contributors
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

package org.wildfly.common;

import static java.security.AccessController.doPrivileged;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.PrivilegedAction;
import java.util.regex.Pattern;

/**
 * Methods for getting the system host name.  The host name is detected from the environment, but may be overridden by
 * use of the {@code jboss.host.name} and/or {@code jboss.qualified.host.name} system properties.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class HostName {

    private static final String hostName;
    private static final String qualifiedHostName;

    static {
        String[] names = doPrivileged(new PrivilegedAction<String[]>() {
            public String[] run() {
                // allow host name to be overridden
                String qualifiedHostName = System.getProperty("jboss.qualified.host.name");
                String providedHostName = System.getProperty("jboss.host.name");
                if (qualifiedHostName == null) {
                    // if host name is specified, don't pick a qualified host name that isn't related to it
                    qualifiedHostName = providedHostName;
                    if (qualifiedHostName == null) {
                        // POSIX-like OSes including Mac should have this set
                        qualifiedHostName = System.getenv("HOSTNAME");
                    }
                    if (qualifiedHostName == null) {
                        // Certain versions of Windows
                        qualifiedHostName = System.getenv("COMPUTERNAME");
                    }
                    if (qualifiedHostName == null) {
                        try {
                            qualifiedHostName = getLocalHost().getHostName();
                        } catch (UnknownHostException e) {
                            qualifiedHostName = null;
                        }
                    }
                    if (qualifiedHostName != null && Pattern.compile("^\\d+\\.\\d+\\.\\d+\\.\\d+$|:").matcher(qualifiedHostName).find()) {
                        // IP address is not acceptable
                        qualifiedHostName = null;
                    }
                    if (qualifiedHostName == null) {
                        // Give up
                        qualifiedHostName = "unknown-host.unknown-domain";
                    } else {
                        qualifiedHostName = qualifiedHostName.trim().toLowerCase();
                    }
                }
                if (providedHostName == null) {
                    // Use the host part of the qualified host name
                    final int idx = qualifiedHostName.indexOf('.');
                    providedHostName  = idx == -1 ? qualifiedHostName : qualifiedHostName.substring(0, idx);
                }
                return new String[] {
                    providedHostName,
                    qualifiedHostName
                };
            }
        });
        hostName = names[0];
        qualifiedHostName = names[1];
    }

    static InetAddress getLocalHost() throws UnknownHostException {
        InetAddress addr;
        try {
            addr = InetAddress.getLocalHost();
        } catch (ArrayIndexOutOfBoundsException e) {  //this is workaround for mac osx bug see AS7-3223 and JGRP-1404
            addr = InetAddress.getByName(null);
        }
        return addr;
    }

    /**
     * Get the detected host name.
     *
     * @return the detected host name
     */
    public static String getHostName() {
        return hostName;
    }

    /**
     * Get the detected qualified host name.
     *
     * @return the detected qualified host name
     */
    public static String getQualifiedHostName() {
        return qualifiedHostName;
    }
}
