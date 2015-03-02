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

package org.wildfly.common.ref;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A special version of {@link PhantomReference} that is strongly retained until it is reaped by the collection thread.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public class CleanerReference<T, A> extends PhantomReference<T, A> {
    private static final Set<CleanerReference<?, ?>> set = Collections.newSetFromMap(new ConcurrentHashMap<CleanerReference<?, ?>, Boolean>());

    /**
     * Construct a new instance with a reaper.
     *
     * @param referent the referent
     * @param attachment the attachment
     * @param reaper the reaper to use
     */
    public CleanerReference(final T referent, final A attachment, final Reaper<T, A> reaper) {
        super(referent, attachment, reaper);
        set.add(this);
    }

    void clean() {
        set.remove(this);
    }

    public final int hashCode() {
        return super.hashCode();
    }

    public final boolean equals(final Object obj) {
        return super.equals(obj);
    }
}
