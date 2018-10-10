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

package org.wildfly.common.format;

import org.wildfly.common.flags.Flags;

/**
 */
abstract class FormatFlags<E extends Enum<E>, This extends FormatFlags<E, This>> extends Flags<E, This> {
    FormatFlags(final int bits) {
        super(bits);
    }

    public final void forbidAll() {
        if (! isEmpty()) {
            throw notAllowed(this);
        }
    }

    public final void forbidAllBut(final E flag) {
        without(flag).forbidAll();
    }

    private static IllegalArgumentException notAllowed(final Flags<?, ?> flags) {
        return new IllegalArgumentException("Flags " + flags + " not allowed here");
    }

    public void forbid(final E flag) {
        if (contains(flag)) {
            throw notAllowed(value(0).with(flag));
        }
    }
}
