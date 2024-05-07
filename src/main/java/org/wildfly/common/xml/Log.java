/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2022 Red Hat, Inc., and individual contributors
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

package org.wildfly.common.xml;

import static java.lang.invoke.MethodHandles.lookup;
import static org.jboss.logging.Logger.Level.WARN;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

@MessageLogger(projectCode = "COM", length = 5)
interface Log extends BasicLogger {

    Log XML_FACTORY_LOGGER = Logger.getMessageLogger(lookup(), Log.class, "org.wildfly.common.xml");

    // 3100-3199 reserved for xml factory logging
    @LogMessage(level = WARN)
    @Message(id = 3100, value = "Property or feature %s not supported by %s")
    void xmlFactoryPropertyNotSupported(@Cause Throwable cause, String property, String factoryName);

}
