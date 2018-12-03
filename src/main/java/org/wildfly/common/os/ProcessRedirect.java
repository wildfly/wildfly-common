/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2018 Red Hat, Inc., and individual contributors
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
