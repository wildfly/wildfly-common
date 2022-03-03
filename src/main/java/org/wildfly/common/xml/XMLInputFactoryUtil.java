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

import static org.wildfly.common.xml.Log.XML_FACTORY_LOGGER;

import javax.xml.stream.XMLInputFactory;

import org.wildfly.common.annotation.NotNull;

/**
 * Factory provides {@link XMLInputFactory} with secure defaults set. Properties not supported generate a warning, but the
 * factory process creation will continue and return a result.
 * Settings based on recommendations of
 * <a href="https://rules.sonarsource.com/java/RSPEC-2755">Sonarcloud RSPEC-2755</a> and
 * <a href="https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html#java">OWASP XML
 * External Entity Prevention Cheatsheet</a>.
 * <p/>
 *
 * <ul>
 * <li>{@link javax.xml.stream.XMLInputFactory#SUPPORT_DTD} is set to false.</li>
 * <li>{@link javax.xml.stream.XMLInputFactory#IS_SUPPORTING_EXTERNAL_ENTITIES} is set to false.</li>
 * </ul>
 *
 * @author <a href="mailto:boris@unckel.net">Boris Unckel</a>
 * @since 1.6.0.Final
 */
public final class XMLInputFactoryUtil {

    /*
     * Prevent recurring log messages (per classloader).
     */
    private static volatile boolean TO_BE_LOGGED = true;

    /**
     * Factory generated with secure defaults.
     * @return an instance of the XMLInputFactory.
     */
    @NotNull
    public static XMLInputFactory create() {
        final XMLInputFactory instance = XMLInputFactory.newInstance();

        try {
            instance.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
        } catch (IllegalArgumentException e) {
            if (TO_BE_LOGGED) {
                XML_FACTORY_LOGGER.xmlFactoryPropertyNotSupported(e, XMLInputFactory.SUPPORT_DTD,
                        instance.getClass().getCanonicalName());
            }
        }

        try {
            instance.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
        } catch (IllegalArgumentException e) {
            if (TO_BE_LOGGED) {
                XML_FACTORY_LOGGER.xmlFactoryPropertyNotSupported(e, XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES,
                        instance.getClass().getCanonicalName());
            }
        }

        TO_BE_LOGGED = false;

        return instance;
    }

    /**
     * No instance.
     */
    private XMLInputFactoryUtil() {
        throw new IllegalStateException("No instance");
    }

}
