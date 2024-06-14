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
import static javax.xml.XMLConstants.ACCESS_EXTERNAL_DTD;
import static javax.xml.XMLConstants.ACCESS_EXTERNAL_SCHEMA;
import static org.wildfly.common.xml.Log.XML_FACTORY_LOGGER;
import static org.wildfly.common.Assert.checkNotNullParam;

import java.util.concurrent.atomic.AtomicBoolean;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

import org.wildfly.common.annotation.NotNull;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * Factory provides {@link Validator} with secure defaults set. Properties not supported generate a warning, but
 * the factory process creation will continue and return a result.
 * Settings based on recommendations of
 * <a href="https://rules.sonarsource.com/java/RSPEC-2755">Sonarcloud RSPEC-2755</a> and
 * <a href="https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html#java">OWASP XML
 * External Entity Prevention Cheatsheet</a>.
 * <p/>
 *
 * <ul>
 * <li>{@link javax.xml.XMLConstants#FEATURE_SECURE_PROCESSING} is set to true.</li>
 * <li>{@link javax.xml.XMLConstants#ACCESS_EXTERNAL_DTD} is set to empty.</li>
 * <li>{@link javax.xml.XMLConstants#ACCESS_EXTERNAL_SCHEMA} is set to empty.</li>
 * </ul>
 *
 * @author <a href="mailto:boris@unckel.net">Boris Unckel</a>
 * @since 1.7.0.Final
 */

public class ValidatorUtil {
    /*
     * Prevent recurring log messages (per classloader).
     */
    private static final AtomicBoolean TO_BE_LOGGED = new AtomicBoolean(true);

    /**
     * Factory generated with secure defaults.
     * @param schemaToUse for creation of the Validator, must be secured on its own.
     * @return an instance of the Validator.
     */
    @NotNull
    public static Validator create(final Schema schemaToUse) {
        checkNotNullParam("schemaToUse", schemaToUse);

        final Validator instance = schemaToUse.newValidator();
        final boolean toBeLogged = TO_BE_LOGGED.compareAndSet(true, false);

        try {
            instance.setFeature(FEATURE_SECURE_PROCESSING, true);
        } catch (SAXNotRecognizedException | SAXNotSupportedException e) {
            if (toBeLogged) {
                XML_FACTORY_LOGGER.xmlFactoryPropertyNotSupported(e, FEATURE_SECURE_PROCESSING,
                        instance.getClass().getCanonicalName());
            }
        }

        try {
            instance.setProperty(ACCESS_EXTERNAL_DTD, "");
        } catch (SAXNotRecognizedException | SAXNotSupportedException e) {
            if (toBeLogged) {
                XML_FACTORY_LOGGER.xmlFactoryPropertyNotSupported(e, ACCESS_EXTERNAL_DTD,
                        instance.getClass().getCanonicalName());
            }
        }

        try {
            instance.setProperty(ACCESS_EXTERNAL_SCHEMA, "");
        } catch (SAXNotRecognizedException | SAXNotSupportedException e) {
            if (toBeLogged) {
                XML_FACTORY_LOGGER.xmlFactoryPropertyNotSupported(e, ACCESS_EXTERNAL_SCHEMA,
                        instance.getClass().getCanonicalName());
            }
        }

        return instance;
    }

    /**
     * No instance.
     */
    private ValidatorUtil() {
        throw new IllegalStateException("No instance");
    }
}
