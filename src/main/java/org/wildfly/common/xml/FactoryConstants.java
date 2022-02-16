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

/**
 * Constants to configure the factories.
 * <a href="https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html#java">OWASP XML
 * External Entity Prevention Cheatsheet</a>
 * @author <a href="mailto:boris@unckel.net">Boris Unckel</a>
 * @since 1.6.0.Final
 */
final class FactoryConstants {

    /**
     * This is the PRIMARY defense. If DTDs (doctypes) are disallowed, almost all XML entity attacks are prevented
     * Xerces 2 only:
     * @see{https://xerces.apache.org/xerces2-j/features.html#disallow-doctype-decl}
     */
    public static final String APACHE_DISALLOW_DOCTYPE_DECL = "http://apache.org/xml/features/disallow-doctype-decl";

    /**
     * If you can't completely disable DTDs, then at least do the following. This feature has to be used together with the
     * following one (external-parameter-entities), otherwise it will not protect you from XXE for sure Do not include external
     * general entities.
     * Xerces 1:
     * @see{https://xerces.apache.org/xerces-j/features.html#external-general-entities}
     *
     * Xerces 2:
     * @see{https://xerces.apache.org/xerces2-j/features.html#external-general-entities}
     */
    public static final String XML_EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";

    /**
     * This feature has to be used together with the previous one(external-general-entities), otherwise it will not protect you from XXE for sure
     *  Do not include external parameter entities or the external DTD subset.
     * Xerces:
     * @see{https://xerces.apache.org/xerces-j/features.html#external-parameter-entities}
     *
     * Xerces 2:
     * @see{https://xerces.apache.org/xerces2-j/features.html#external-parameter-entities}
     */
    public static final String XML_EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";

    /**
     * Disable external DTDs as well.
     */
    public static final String APACHE_LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";

    /**
     * No instance.
     */
    private FactoryConstants() {
        throw new IllegalStateException("No instance");
    }

}
