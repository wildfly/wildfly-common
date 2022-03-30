/*
 * Copyright 2021 Red Hat, Inc.
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

import static org.junit.Assert.assertNotNull;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPathFactory;

import org.junit.Test;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;


/**
 * @author <a href="mailto:boris@unckel.net">Boris Unckel</a>
 *
 */
public class XmlFactoriesTest {

    @Test
    public void testDocumentBuilderFactoryUtil() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactoryUtil.create();
        assertNotNull(documentBuilderFactory);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        assertNotNull(documentBuilder);
    }

    @Test
    public void testSAXParserFactoryUtil() throws ParserConfigurationException, SAXException {
        SAXParserFactory saxParserFactory = SAXParserFactoryUtil.create();
        assertNotNull(saxParserFactory);
        SAXParser saxParser = saxParserFactory.newSAXParser();
        assertNotNull(saxParser);
    }

    @Test
    public void testTransformerFactoryUtil() throws TransformerConfigurationException {
        TransformerFactory transformerFactory = TransformerFactoryUtil.create();
        assertNotNull(transformerFactory);
        Transformer transformer = transformerFactory.newTransformer();
        assertNotNull(transformer);
    }

    @Test
    public void testXMLInputFactoryUtil() {
        XMLInputFactory xmlInputFactory = XMLInputFactoryUtil.create();
        assertNotNull(xmlInputFactory);
    }

    @Test
    public void testXMLReaderFactoryUtil() throws SAXException {
        XMLReader xmlReader = XMLReaderFactoryUtil.create();
        assertNotNull(xmlReader);
    }

    @Test
    public void testSchemaFactoryUtil() {
        SchemaFactory schemaFactory = SchemaFactoryUtil.create();
        assertNotNull(schemaFactory);
    }

    @Test
    public void testXPathFactoryUtil() {
        XPathFactory schemaFactory = XPathFactoryUtil.create();
        assertNotNull(schemaFactory);
    }

    @Test
    public void testValidatorUtil() throws SAXException {
        Schema schema = SchemaFactoryUtil.create().newSchema();
        Validator validator = ValidatorUtil.create(schema);
        assertNotNull(validator);
    }
}
