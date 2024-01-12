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

package org.wildfly.common.net;

import java.net.URI;

/**
 * URI-specific utilities.
 *
 * @deprecated Use {@link io.smallrye.common.net.URIs} instead.
 */
@Deprecated(forRemoval = true)
public final class URIs {
    private URIs() {}

    /**
     * Get the user name information from a URI, if any.
     *
     * @param uri the URI
     * @return the user name, or {@code null} if the URI did not contain a recoverable user name
     * @deprecated Use {@link io.smallrye.common.net.URIs#getUserFromURI(URI)} instead.
     */
    @Deprecated(forRemoval = true)
    public static String getUserFromURI(URI uri) {
        return io.smallrye.common.net.URIs.getUserFromURI(uri);
    }
}
