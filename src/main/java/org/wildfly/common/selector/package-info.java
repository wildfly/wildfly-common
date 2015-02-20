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

/**
 * Classes which implement a selector pattern, which enables a global provider of some service to be selected
 * depending on context.  Several selector implementations are provided, such as {@link org.wildfly.common.selector.GlobalSelector GlobalSelector}
 * and {@link org.wildfly.common.selector.ThreadLocalSelector ThreadLocalSelector}.  Custom implementations can be created by extending
 * the {@link org.wildfly.common.selector.Selector Selector} base class.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
package org.wildfly.common.selector;