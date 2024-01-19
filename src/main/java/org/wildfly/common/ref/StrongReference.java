/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
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

/**
 * A strong reference with an attachment.  Since strong references are always reachable, a reaper may not be used.
 *
 * @param <T> the reference value type
 * @param <A> the attachment type
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 *
 * @deprecated Use {@link io.smallrye.common.ref.StrongReference} instead.
 */
@Deprecated(forRemoval = true)
public class StrongReference<T, A> implements Reference<T, A> {

    private volatile T referent;
    private final A attachment;

    /**
     * Construct a new instance.
     *
     * @param referent the referent
     * @param attachment the attachment
     */
    public StrongReference(final T referent, final A attachment) {
        this.referent = referent;
        this.attachment = attachment;
    }

    /**
     * Construct a new instance.
     *
     * @param referent the referent
     */
    public StrongReference(final T referent) {
        this(referent, null);
    }

    public T get() {
        return referent;
    }

    public void clear() {
        referent = null;
    }

    public A getAttachment() {
        return attachment;
    }

    public Type getType() {
        return Type.STRONG;
    }

    public String toString() {
        return "strong reference to " + String.valueOf(get());
    }
}
