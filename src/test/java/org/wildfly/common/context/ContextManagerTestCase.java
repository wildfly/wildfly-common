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

package org.wildfly.common.context;

import org.junit.Assert;
import org.junit.Test;

/**
 * Validates default context priorities.
 * @author Paul Ferraro
 */
public class ContextManagerTestCase {

    @Test
    public void test() {
        ContextManager<MockContext> manager = new ContextManager<>(MockContext.class);
        MockContext defaultGlobalContext = new MockContext(manager);
        MockContext defaultGenericClassLoaderContext = new MockContext(manager);
        MockContext defaultSpecificClassLoaderContext = new MockContext(manager);
        MockContext defaultThreadContext = new MockContext(manager);
        MockContext runContext = new MockContext(manager);

        // Validate active context priorities as we apply defaults
        Assert.assertNull(manager.get());

        manager.setClassLoaderDefault(classLoader -> defaultGenericClassLoaderContext);

        Assert.assertSame(defaultGenericClassLoaderContext, manager.get());

        manager.setGlobalDefault(defaultGlobalContext);

        Assert.assertSame(defaultGlobalContext, manager.get());

        manager.setClassLoaderDefault(this.getClass().getClassLoader(), defaultSpecificClassLoaderContext);

        Assert.assertSame(defaultSpecificClassLoaderContext, manager.get());

        manager.setThreadDefault(defaultThreadContext);

        Assert.assertSame(defaultThreadContext, manager.get());

        runContext.run(() -> Assert.assertSame(runContext, manager.get()));

        // Validate active context priorities as we remove defaults
        Assert.assertSame(defaultThreadContext, manager.get());

        manager.setThreadDefault(null);

        Assert.assertSame(defaultSpecificClassLoaderContext, manager.get());

        manager.setClassLoaderDefault(this.getClass().getClassLoader(), null);

        Assert.assertSame(defaultGlobalContext, manager.get());

        manager.setGlobalDefault(null);

        Assert.assertSame(defaultGenericClassLoaderContext, manager.get());

        manager.setClassLoaderDefault(null);

        Assert.assertNull(manager.get());
    }

    private static class MockContext implements Contextual<MockContext> {
        private final ContextManager<MockContext> manager;

        MockContext(ContextManager<MockContext> manager) {
            this.manager = manager;
        }

        @Override
        public ContextManager<MockContext> getInstanceContextManager() {
            return this.manager;
        }
    }
}
