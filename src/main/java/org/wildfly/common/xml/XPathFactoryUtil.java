/*
 * Copyright 2022 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wildfly.common.xml;

import static javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING;
import static org.wildfly.common.xml.Log.XML_FACTORY_LOGGER;

import java.util.concurrent.atomic.AtomicBoolean;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.wildfly.common.annotation.NotNull;



/**
 * Factory provides {@link XPathFactory} with secure defaults set. Properties not supported generate a warning, but
 * the factory process creation will continue and return a result.
 * Settings based on recommendations of
 * <a href="https://rules.sonarsource.com/java/RSPEC-2755">Sonarcloud RSPEC-2755</a> and
 * <a href="https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html#java">OWASP XML
 * External Entity Prevention Cheatsheet</a>.
 * <p/>
 *
 * <ul>
 * <li>{@link javax.xml.XMLConstants#FEATURE_SECURE_PROCESSING} is set to true.</li>
 * </ul>
 *
 * @author <a href="mailto:boris@unckel.net">Boris Unckel</a>
 * @since 1.7.0.Final
 */
public class XPathFactoryUtil {
    /*
     * Prevent recurring log messages (per classloader).
     */
    private static final AtomicBoolean TO_BE_LOGGED = new AtomicBoolean(true);

    /**
     * Factory generated with secure defaults.
     * @return an instance of the XPathFactory.
     */
    @NotNull
    public static XPathFactory create() {
        final XPathFactory instance = XPathFactory.newInstance();
        final boolean toBeLogged = TO_BE_LOGGED.compareAndSet(true, false);

        try {
            instance.setFeature(FEATURE_SECURE_PROCESSING, true);
        } catch (XPathFactoryConfigurationException e) {
            XML_FACTORY_LOGGER.xmlFactoryPropertyNotSupported(e, FEATURE_SECURE_PROCESSING,
                    instance.getClass().getCanonicalName());
        }

        return instance;
    }

    /**
     * No instance.
     */
    private XPathFactoryUtil() {
        throw new IllegalStateException("No instance");
    }
}
