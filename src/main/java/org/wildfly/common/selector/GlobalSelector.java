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

package org.wildfly.common.selector;

import org.wildfly.common.Assert;
import org.wildfly.common.context.Contextual;

/**
 * A selector which always returns one global instance.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 *
 * @deprecated Use {@link Contextual} instead.
 */
@Deprecated
public final class GlobalSelector<T> extends Selector<T> {
    private final T instance;

    /**
     * Construct a new instance.
     *
     * @param instance the constant instance to always return from this selector
     */
    public GlobalSelector(final T instance) {
        Assert.checkNotNullParam("instance", instance);
        this.instance = instance;
    }

    public T get() {
        return instance;
    }
}
