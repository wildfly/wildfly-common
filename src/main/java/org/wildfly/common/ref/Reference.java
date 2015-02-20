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

import java.util.EnumSet;

/**
 * An enhanced reference type with a type-safe attachment.
 *
 * @param <T> the reference value type
 * @param <A> the attachment type
 *
 * @see java.lang.ref.Reference
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public interface Reference<T, A> {

    /**
     * Get the value, or {@code null} if the reference has been cleared.
     *
     * @return the value
     */
    T get();

    /**
     * Get the attachment, if any.
     *
     * @return the attachment
     */
    A getAttachment();

    /**
     * Clear the reference.
     */
    void clear();

    /**
     * Get the type of the reference.
     *
     * @return the type
     */
    Type getType();

    /**
     * A reference type.
     *
     * @apiviz.exclude
     */
    enum Type {

        /**
         * A strong reference.
         */
        STRONG,
        /**
         * A weak reference.
         */
        WEAK,
        /**
         * A phantom reference.
         */
        PHANTOM,
        /**
         * A soft reference.
         */
        SOFT,
        /**
         * A {@code null} reference.
         */
        NULL,
        ;

        private static final int fullSize = values().length;

        /**
         * Determine whether the given set is fully populated (or "full"), meaning it contains all possible values.
         *
         * @param set the set
         *
         * @return {@code true} if the set is full, {@code false} otherwise
         */
        public static boolean isFull(final EnumSet<Type> set) {
            return set != null && set.size() == fullSize;
        }

        /**
         * Determine whether this instance is equal to one of the given instances.
         *
         * @param v1 the first instance
         *
         * @return {@code true} if one of the instances matches this one, {@code false} otherwise
         */
        public boolean in(final Type v1) {
            return this == v1;
        }

        /**
         * Determine whether this instance is equal to one of the given instances.
         *
         * @param v1 the first instance
         * @param v2 the second instance
         *
         * @return {@code true} if one of the instances matches this one, {@code false} otherwise
         */
        public boolean in(final Type v1, final Type v2) {
            return this == v1 || this == v2;
        }

        /**
         * Determine whether this instance is equal to one of the given instances.
         *
         * @param v1 the first instance
         * @param v2 the second instance
         * @param v3 the third instance
         *
         * @return {@code true} if one of the instances matches this one, {@code false} otherwise
         */
        public boolean in(final Type v1, final Type v2, final Type v3) {
            return this == v1 || this == v2 || this == v3;
        }

        /**
         * Determine whether this instance is equal to one of the given instances.
         *
         * @param values the possible values
         *
         * @return {@code true} if one of the instances matches this one, {@code false} otherwise
         */
        public boolean in(final Type... values) {
            if (values != null) for (Type value : values) {
                if (this == value) return true;
            }
            return false;
        }
    }
}
