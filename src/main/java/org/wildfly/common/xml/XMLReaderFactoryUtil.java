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

import static org.wildfly.common.xml.FactoryConstants.APACHE_DISALLOW_DOCTYPE_DECL;
import static org.wildfly.common.xml.FactoryConstants.APACHE_LOAD_EXTERNAL_DTD;
import static org.wildfly.common.xml.FactoryConstants.XML_EXTERNAL_GENERAL_ENTITIES;
import static org.wildfly.common.xml.FactoryConstants.XML_EXTERNAL_PARAMETER_ENTITIES;
import static org.wildfly.common.xml.Log.XML_FACTORY_LOGGER;

import org.wildfly.common.annotation.NotNull;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Factory provides {@link XMLReaderFactory} with secure defaults set. Properties not supported generate a warning, but the
 * factory process creation will continue and return a result.
 * Settings based on recommendations of
 * <a href="https://rules.sonarsource.com/java/RSPEC-2755">Sonarcloud RSPEC-2755</a> and
 * <a href="https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html#java">OWASP XML
 * External Entity Prevention Cheatsheet</a>.
 * <p/>
 *
 * <ul>
 * <li>{@link org.wildfly.common.xml.FactoryConstants#APACHE_DISALLOW_DOCTYPE_DECL} is set to true.</li>
 * <li>{@link org.wildfly.common.xml.FactoryConstants#APACHE_LOAD_EXTERNAL_DTD} is set to false.</li>
 * <li>{@link org.wildfly.common.xml.FactoryConstants#XML_EXTERNAL_GENERAL_ENTITIES} is set to false.</li>
 * <li>{@link org.wildfly.common.xml.FactoryConstants#XML_EXTERNAL_PARAMETER_ENTITIES} is set to false.</li>
 * </ul>
 *
 * @author <a href="mailto:boris@unckel.net">Boris Unckel</a>
 * @since 1.6.0.Final
 */
public final class XMLReaderFactoryUtil {

    /*
     * Prevent recurring log messages (per classloader).
     */
    private static final AtomicBoolean TO_BE_LOGGED = new AtomicBoolean(true);

    /**
     * Factory generated with secure defaults.
     * @return an instance of the XMLInputFactory.
     */
    @NotNull
    public static XMLReader create() throws SAXException {
        final XMLReader instance = XMLReaderFactory.createXMLReader();
        final boolean toBeLogged = TO_BE_LOGGED.compareAndSet(true, false);

        try {
            instance.setFeature(APACHE_DISALLOW_DOCTYPE_DECL, true);
        } catch (SAXNotRecognizedException | SAXNotSupportedException e) {
            if (toBeLogged) {
                XML_FACTORY_LOGGER.xmlFactoryPropertyNotSupported(e, APACHE_DISALLOW_DOCTYPE_DECL,
                        instance.getClass().getCanonicalName());
            }
        }

        try {
            instance.setFeature(APACHE_LOAD_EXTERNAL_DTD, false);
        } catch (SAXNotRecognizedException | SAXNotSupportedException e) {
            if (toBeLogged) {
                XML_FACTORY_LOGGER.xmlFactoryPropertyNotSupported(e, APACHE_LOAD_EXTERNAL_DTD,
                        instance.getClass().getCanonicalName());
            }
        }

        try {
            instance.setFeature(XML_EXTERNAL_GENERAL_ENTITIES, false);
        } catch (SAXNotRecognizedException | SAXNotSupportedException e) {
            if (toBeLogged) {
                XML_FACTORY_LOGGER.xmlFactoryPropertyNotSupported(e, XML_EXTERNAL_GENERAL_ENTITIES,
                        instance.getClass().getCanonicalName());
            }
        }

        try {
            instance.setFeature(XML_EXTERNAL_PARAMETER_ENTITIES, false);
        } catch (SAXNotRecognizedException | SAXNotSupportedException e) {
            if (toBeLogged) {
                XML_FACTORY_LOGGER.xmlFactoryPropertyNotSupported(e, XML_EXTERNAL_PARAMETER_ENTITIES,
                        instance.getClass().getCanonicalName());
            }
        }

        return instance;
    }

    /**
     * No instance.
     */
    private XMLReaderFactoryUtil() {
        throw new IllegalStateException("No instance");
    }

}
