/*
 * Copyright (c) 2008 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.sdo.testcase.internal;

import static org.junit.Assert.assertNotNull;

import java.io.StringReader;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;

import com.sap.sdo.api.helper.SapXmlHelper;
import com.sap.sdo.api.impl.SapHelperProvider;
import com.sap.sdo.impl.util.OutputToInputStream;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.helper.XMLDocument;

/**
 * @author D042774
 *
 */
public class SourceTransformationTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public SourceTransformationTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private SapXmlHelper _helper;
    private XMLDocument _doc;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        System.gc();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        // sdo setup
        URL xsdURL = getClass().getClassLoader().getResource(
            PACKAGE + "schema4.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(),
            xsdURL.toString());
        xsdURL = getClass().getClassLoader().getResource(
            PACKAGE + "schema3.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(),
            xsdURL.toString());

        URL xmlUrl = getClass().getClassLoader().getResource(
            PACKAGE + "40000.xml");
        _helper = (SapXmlHelper)_helperContext.getXMLHelper();
        _doc = _helper.load(xmlUrl.openStream(), xmlUrl.toString(),
            null);
        assertNotNull(_doc);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        _helper = null;
        _doc = null;
    }

    @Test
    public void testStreamSourceTransform() throws Exception {
        double sum = 0;
        for (int i=0; i<10; ++i) {
            long start = System.nanoTime();

            OutputToInputStream stream = new OutputToInputStream();
            _helper.save(_doc, stream, Collections.singletonMap(SapXmlHelper.OPTION_KEY_INDENT, null));
            Source streamSource = new StreamSource(stream.getInputStream());

            sum += xsltTransform(streamSource, start);
        }
        System.out.println(sum / 10);
    }

    @Test
    public void testSaxSourceTransform() throws Exception {
        double sum = 0;
        for (int i=0; i<10; ++i) {
            long start = System.nanoTime();
            Map<String, Object> options = new HashMap<String, Object>(2);
            options.put("http://xml.org/sax/features/namespace-prefixes", true);
            options.put(SapXmlHelper.OPTION_KEY_INDENT, null);
            Source saxSource = new SAXSource(_helper.createXMLReader(_doc, options), null);

            sum += xsltTransform(saxSource, start);
        }
        System.out.println(sum / 10);
    }

    @Test
    public void testSdoSourceTransform() throws Exception {
        double sum = 0;
        for (int i=0; i<10; ++i) {
            long start = System.nanoTime();
            Map<String, Object> options = new HashMap<String, Object>(2);
            options.put("http://xml.org/sax/features/namespace-prefixes", true);
            options.put(SapXmlHelper.OPTION_KEY_INDENT, null);
            Source saxSource = _helper.createSDOSource(_doc, options);

            sum += xsltTransform(saxSource, start);
        }
        System.out.println(sum / 10);
    }

    @Test
    public void testDomSourceTransform() throws Exception {
        double sum = 0;
        for (int i=0; i<10; ++i) {
            long start = System.nanoTime();

            DOMResult result = new DOMResult();
            _helper.save(_doc, result, Collections.singletonMap(SapXmlHelper.OPTION_KEY_INDENT, null));
            Node data = result.getNode();

            Source domSource = new DOMSource(data);

            sum += xsltTransform(domSource, start);
        }
        System.out.println(sum / 10);
    }

    @Test
    public void testStaxSourceTransform() throws Exception {
        double sum = 0;
        for (int i=0; i<10; ++i) {
            long start = System.nanoTime();

            Source staxSource = new StAXSource(_helper.createXMLStreamReader(_doc, null));

            sum += xsltTransform(staxSource, start);
        }
        System.out.println(sum / 10);
    }

    private double xsltTransform(Source source, long start) throws Exception {
        Source xsltSource = new StreamSource(
            new StringReader(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
                    + "    <xsl:stylesheet version=\"1.0\""
                    + "      xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">"
                    + "    <xsl:output omit-xml-declaration=\"yes\" indent=\"yes\"/>"
                    + "        <xsl:template match=\"*\">"
                    + "            <xsl:copy-of select = \".\" />"
                    + "        </xsl:template>" + "    </xsl:stylesheet>"));

        // das Factory-Pattern unterstützt verschiedene XSLT-Prozessoren
        TransformerFactory transFact = TransformerFactory.newInstance();
        Transformer trans = transFact.newTransformer(xsltSource);

        OutputToInputStream out = new OutputToInputStream();
        StreamResult result = new StreamResult(out);
        // DOMResult result = new DOMResult();

        trans.transform(source, result);

        XMLDocument doc = _helper.load(out.getInputStream());
        // XMLDocument doc = _helper.load(new DOMSource(result.getNode()), null, null);

        assertNotNull(doc);
        assertNotNull(doc.getRootObject());

        return (System.nanoTime() - start) / 1000000000d;
    }

    public static void main(String[] args) throws Exception {
        SourceTransformationTest test =
            new SourceTransformationTest(SapHelperProvider.DEFAULT_CONTEXT_ID, null);
        test.setUp();
        //test.testStaxSourceTransform();
        test.testSaxSourceTransform();
        test.tearDown();
    }
}
