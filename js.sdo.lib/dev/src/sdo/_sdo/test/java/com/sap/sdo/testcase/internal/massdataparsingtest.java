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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBResult;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stax.StAXSource;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapXmlHelper;
import com.sap.sdo.impl.types.Namespace;
import com.sap.sdo.impl.types.builtin.PropertyLogic;
import com.sap.sdo.impl.types.builtin.PropertyLogicFacade;
import com.sap.sdo.impl.types.builtin.TypeLogic;
import com.sap.sdo.impl.types.builtin.TypeLogicFacade;
import com.sap.sdo.impl.util.OutputToInputStream;
import com.sap.sdo.impl.xml.stream.SdoStreamReader;
import com.sap.sdo.testcase.MemoryAnalyzer;
import com.sap.xi.appl.se.global.OutboundDeliveryByBatchIDQueryResponseInManyResponse;

import commonj.sdo.DataObject;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.impl.HelperProvider;

/**
 * @author D042774
 *
 */
public class MassDataParsingTest extends TestCase {

    private static final String PACKAGE = "com/sap/sdo/testcase/schemas/";
    private HelperContext _helperContext;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        _helperContext = HelperProvider.getDefaultContext();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();
        _helperContext = null;
    }

    @Test
    public void testSAPerformanceWith40000() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "schema4.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        xsdURL = getClass().getClassLoader().getResource(PACKAGE + "schema3.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());

        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "40000.xml");
        final XMLDocument doc = _helperContext.getXMLHelper().load(xmlUrl.openStream(), xmlUrl.toString(), null);
        assertNotNull(doc);
        DataObject root = doc.getRootObject();
        assertNotNull(root);
        System.out.println(root.getType().getName());
        long start1 = System.nanoTime();
        MemoryAnalyzer memoryAnalyzer = new MemoryAnalyzer();
        memoryAnalyzer.scanObject(root);
        System.out.println((System.nanoTime() - start1)/1000000000d);
        System.out.println("Memory footprint including types and properties");
        System.out.println("32 bit: " + memoryAnalyzer.getBytes32bit()+ " (" + memoryAnalyzer.getOptimal32bit() + ')');
        System.out.println("64 bit: " + memoryAnalyzer.getBytes64bit()+ " (" + memoryAnalyzer.getOptimal64bit() + ')');
        long start2 = System.nanoTime();
        memoryAnalyzer = new MemoryAnalyzer();
        memoryAnalyzer.addClassFilter(TypeLogicFacade.class);
        memoryAnalyzer.addClassFilter(TypeLogic.class);
        memoryAnalyzer.addClassFilter(PropertyLogicFacade.class);
        memoryAnalyzer.addClassFilter(PropertyLogic.class);
        memoryAnalyzer.addClassFilter(Namespace.TypeAndContextPair.class);
        memoryAnalyzer.scanObject(root);
        System.out.println((System.nanoTime() - start2)/1000000000d);
        System.out.println("Memory footprint without types and properties");
        System.out.println("32 bit: " + memoryAnalyzer.getBytes32bit()+ " (" + memoryAnalyzer.getOptimal32bit() + ')');
        System.out.println("64 bit: " + memoryAnalyzer.getBytes64bit()+ " (" + memoryAnalyzer.getOptimal64bit() + ')');
        long dif32bit = memoryAnalyzer.getBytes32bit() - 57122520;
        long dif64bit = memoryAnalyzer.getBytes64bit() - 102724048;
        memoryAnalyzer = null;
        assertTrue(dif32bit + " bytes over limit", dif32bit <= 0);
        assertTrue(dif64bit + " bytes over limit", dif64bit <= 0);

        long start = System.nanoTime();
        OutputToInputStream out = new OutputToInputStream();
        _helperContext.getXMLHelper().save(doc, out, null);
        InputStream in = out.getInputStream();
        XMLDocument doc2 = _helperContext.getXMLHelper().load(in);

//        PipedInputStream in = new PipedInputStream();
//        final PipedOutputStream out = new PipedOutputStream(in);
//        new Thread(
//          new Runnable(){
//            public void run(){
//                try {
//                    _helperContext.getXMLHelper().save(doc, out, null);
//                } catch (IOException e) {
//                    throw new IllegalArgumentException(e);
//                }
//            }
//          }
//        ).start();
//        final XMLDocument doc2 = _helperContext.getXMLHelper().load(in, xmlUrl.toString(), null);

//      PipedReader in = new PipedReader();
//      final PipedWriter out = new PipedWriter(in);
//      new Thread(
//        new Runnable(){
//          public void run(){
//              try {
//                  _helperContext.getXMLHelper().save(doc, out, null);
//              } catch (IOException e) {
//                  throw new IllegalArgumentException(e);
//              }
//          }
//        }
//      ).start();
//      final XMLDocument doc2 = _helperContext.getXMLHelper().load(in, xmlUrl.toString(), null);

        System.out.println((System.nanoTime() - start)/1000000000d);
        assertNotNull(doc2);
        DataObject root2 = doc2.getRootObject();
        assertNotNull(root2);
        System.out.println(root2.getType().getName());

    }

    public static void main(String[] args) throws Exception {
        MassDataParsingTest test = new MassDataParsingTest();
        test.setUp();
        test.testSAPerformanceWith40000();
        //test.testJaxbByXmlStreamReader();
        test.tearDown();
    }

    @Test
    public void testByteArrayTransformation() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "schema4.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        xsdURL = getClass().getClassLoader().getResource(PACKAGE + "schema3.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());

        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "40000.xml");
        XMLDocument doc = _helperContext.getXMLHelper().load(xmlUrl.openStream(), xmlUrl.toString(), null);
        assertNotNull(doc);
        DataObject root = doc.getRootObject();
        assertNotNull(root);

        long start = System.nanoTime();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        _helperContext.getXMLHelper().save(doc, out, null);
        InputStream in = new ByteArrayInputStream(out.toByteArray());

        long interim = System.nanoTime();

        doc = _helperContext.getXMLHelper().load(in);

        assertNotNull(doc);
        root = doc.getRootObject();
        assertNotNull(root);

        long end = System.nanoTime();
        System.out.println("full: " + (end - start)/1000000000d);
        System.out.println("writing: " + (interim - start)/1000000000d);
        System.out.println("loading: " + (end - interim)/1000000000d);
    }

    @Test
    public void testXmlStreamTransformation() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "schema4.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        xsdURL = getClass().getClassLoader().getResource(PACKAGE + "schema3.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());

        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "40000.xml");
        XMLHelper helper = _helperContext.getXMLHelper();
        XMLDocument doc = helper.load(xmlUrl.openStream(), xmlUrl.toString(), null);
        assertNotNull(doc);
        DataObject root = doc.getRootObject();
        assertNotNull(root);

        long start = System.nanoTime();

        SdoStreamReader reader = new SdoStreamReader(doc, null, _helperContext);
        doc = helper.load(new StAXSource(reader), null, null);

        assertNotNull(doc);
        root = doc.getRootObject();
        assertNotNull(root);

        System.out.println((System.nanoTime() - start)/1000000000d);
    }

    @Test
    public void testJaxbByXmlStreamReader() throws Exception {
        System.out.println("JAXB with XMLStreamReader");
        System.gc();
        try { Thread.sleep(5000); }
        catch(InterruptedException e) {}

        // sdo setup
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "schema4.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        xsdURL = getClass().getClassLoader().getResource(PACKAGE + "schema3.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());

        // jaxb setup
        JAXBContext ctx =
            JAXBContext.newInstance("com.sap.xi.appl.se.global");
        Unmarshaller unmarshaller = ctx.createUnmarshaller();

        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "40000.xml");
        XMLHelper helper = _helperContext.getXMLHelper();
        XMLDocument doc = helper.load(xmlUrl.openStream(), xmlUrl.toString(), null);
        assertNotNull(doc);

        //MemoryAnalyzer analyzer2 = new MemoryAnalyzer();
        //analyzer2.scanObject(doc);
        //System.out.println("32: " + analyzer2.getBytes32bit());
        //System.out.println("64: " + analyzer2.getBytes64bit());

        long start = System.nanoTime();

        Object data =
            unmarshaller.unmarshal(
                ((SapXmlHelper)_helperContext.getXMLHelper()).createXMLStreamReader(doc, null));

        System.out.println((System.nanoTime() - start)/1000000000d);

        assertEquals(
            OutboundDeliveryByBatchIDQueryResponseInManyResponse.class,
            ((JAXBElement)data).getDeclaredType());

        //ctx.createMarshaller().marshal(data, System.out);

//        MemoryAnalyzer analyzer = new MemoryAnalyzer();
//        analyzer.scanObject(data);
//        System.out.println("32: " + analyzer.getBytes32bit());
//        System.out.println("64: " + analyzer.getBytes64bit());

//        analyzer2.scanObject(data);
//        System.out.println("32: " + analyzer2.getBytes32bit());
//        System.out.println("64: " + analyzer2.getBytes64bit());
//
    }

    @Test
    public void testJaxbByJAXBResult() throws Exception {
        System.out.println("JAXB with JAXBResult");
        System.gc();
        try { Thread.sleep(5000); }
        catch(InterruptedException e) {}

        // sdo setup
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "schema4.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        xsdURL = getClass().getClassLoader().getResource(PACKAGE + "schema3.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());

        // jaxb setup
        JAXBContext ctx =
            JAXBContext.newInstance("com.sap.xi.appl.se.global");

        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "40000.xml");
        XMLHelper helper = _helperContext.getXMLHelper();
        XMLDocument doc = helper.load(xmlUrl.openStream(), xmlUrl.toString(), null);
        assertNotNull(doc);

        //MemoryAnalyzer analyzer2 = new MemoryAnalyzer();
        //analyzer2.scanObject(doc);
        //System.out.println("32: " + analyzer2.getBytes32bit());
        //System.out.println("64: " + analyzer2.getBytes64bit());

        long start = System.nanoTime();

        JAXBResult result = new JAXBResult(ctx);
        _helperContext.getXMLHelper().save(doc, result, null);
        Object data = result.getResult();

        System.out.println((System.nanoTime() - start)/1000000000d);

        assertEquals(
            OutboundDeliveryByBatchIDQueryResponseInManyResponse.class,
            ((JAXBElement)data).getDeclaredType());

        //ctx.createMarshaller().marshal(data, System.out);

//        MemoryAnalyzer analyzer = new MemoryAnalyzer();
//        analyzer.scanObject(data);
//        System.out.println("32: " + analyzer.getBytes32bit());
//        System.out.println("64: " + analyzer.getBytes64bit());

//        analyzer2.scanObject(data);
//        System.out.println("32: " + analyzer2.getBytes32bit());
//        System.out.println("64: " + analyzer2.getBytes64bit());
//
    }

    @Test
    public void testJaxbByByteArray() throws Exception {
        System.out.println("JAXB with ByteArray");
        System.gc();
        try { Thread.sleep(5000); }
        catch(InterruptedException e) {}

        // sdo setup
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "schema4.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        xsdURL = getClass().getClassLoader().getResource(PACKAGE + "schema3.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());

        // jaxb setup
        JAXBContext ctx =
            JAXBContext.newInstance("com.sap.xi.appl.se.global");
        Unmarshaller unmarshaller = ctx.createUnmarshaller();

        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "40000.xml");
        XMLHelper helper = _helperContext.getXMLHelper();
        XMLDocument doc = helper.load(xmlUrl.openStream(), xmlUrl.toString(), null);
        assertNotNull(doc);

        Map<String, Object> options = Collections.singletonMap(SapXmlHelper.OPTION_KEY_INDENT, null);

//        MemoryAnalyzer analyzer2 = new MemoryAnalyzer();
//        analyzer2.scanObject(doc);
//        System.out.println("32: " + analyzer2.getBytes32bit());
//        System.out.println("64: " + analyzer2.getBytes64bit());

        long start = System.nanoTime();

        OutputToInputStream out = new OutputToInputStream();

        long a1 = System.nanoTime();

        _helperContext.getXMLHelper().save(doc, out, options);
        doc = null;

        long a2 = System.nanoTime();

        Object data = unmarshaller.unmarshal(out.getInputStream());

        long end = System.nanoTime();
        System.out.println((end - start)/1000000000d);
        System.out.println("SDO rendering: " + (a2 - a1)/1000000000d);
        System.out.println("JAXB unmarshalling: " + (end - a2)/1000000000d);

        assertEquals(
            OutboundDeliveryByBatchIDQueryResponseInManyResponse.class,
            ((JAXBElement)data).getDeclaredType());

        //ctx.createMarshaller().marshal(data, System.out);

//        MemoryAnalyzer analyzer = new MemoryAnalyzer();
//        analyzer.scanObject(data);
//        System.out.println("32: " + analyzer.getBytes32bit());
//        System.out.println("64: " + analyzer.getBytes64bit());
//
//        analyzer2.scanObject(data);
//        System.out.println("32: " + analyzer2.getBytes32bit());
//        System.out.println("64: " + analyzer2.getBytes64bit());
    }

    @Test
    public void testJaxbByByteArrayThruXmlStreamReader() throws Exception {
        System.out.println("JAXB with ByteArray thru XMLStreamReader");
        System.gc();
        try { Thread.sleep(5000); }
        catch(InterruptedException e) {}

        // sdo setup
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "schema4.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        xsdURL = getClass().getClassLoader().getResource(PACKAGE + "schema3.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());

        // jaxb setup
        JAXBContext ctx =
            JAXBContext.newInstance("com.sap.xi.appl.se.global");
        Unmarshaller unmarshaller = ctx.createUnmarshaller();

        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "40000.xml");
        XMLHelper helper = _helperContext.getXMLHelper();
        XMLDocument doc = helper.load(xmlUrl.openStream(), xmlUrl.toString(), null);
        assertNotNull(doc);

        Map<String, Object> options = Collections.singletonMap(SapXmlHelper.OPTION_KEY_INDENT, null);

        long start = System.nanoTime();

        OutputToInputStream out = new OutputToInputStream();

        long a1 = System.nanoTime();

        _helperContext.getXMLHelper().save(doc, out, options);
        doc = null;

        long a2 = System.nanoTime();

        XMLStreamReader reader =
            XMLInputFactory.newInstance().createXMLStreamReader(out.getInputStream());

        long a3 = System.nanoTime();

        Object data = unmarshaller.unmarshal(reader);

        long end = System.nanoTime();
        System.out.println((end - start)/1000000000d);
        System.out.println("SDO rendering: " + (a2 - a1)/1000000000d);
        System.out.println("XMLStreamReader creation: " + (a3 - a2)/1000000000d);
        System.out.println("JAXB unmarshalling: " + (end - a3)/1000000000d);

        assertEquals(
            OutboundDeliveryByBatchIDQueryResponseInManyResponse.class,
            ((JAXBElement)data).getDeclaredType());

        //ctx.createMarshaller().marshal(data, System.out);

//        MemoryAnalyzer analyzer = new MemoryAnalyzer();
//        analyzer.scanObject(data);
//        System.out.println("32: " + analyzer.getBytes32bit());
//        System.out.println("64: " + analyzer.getBytes64bit());
    }

    @Test
    public void testDomByDomResult() throws Exception {
        System.out.println("DOM with DOMResult");
        System.gc();
        try { Thread.sleep(5000); }
        catch(InterruptedException e) {}

        // sdo setup
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "schema4.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        xsdURL = getClass().getClassLoader().getResource(PACKAGE + "schema3.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());


        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "40000.xml");
        XMLHelper helper = _helperContext.getXMLHelper();
        XMLDocument doc = helper.load(xmlUrl.openStream(), xmlUrl.toString(), null);
        assertNotNull(doc);

        //MemoryAnalyzer analyzer2 = new MemoryAnalyzer();
        //analyzer2.scanObject(doc);
        //System.out.println("32: " + analyzer2.getBytes32bit());
        //System.out.println("64: " + analyzer2.getBytes64bit());

        long start = System.nanoTime();

        DOMResult result = new DOMResult();
        _helperContext.getXMLHelper().save(doc, result, null);
        Object data = result.getNode();

        System.out.println((System.nanoTime() - start)/1000000000d);

//        assertEquals(
//            OutboundDeliveryByBatchIDQueryResponseInManyResponse.class,
//            ((JAXBElement)data).getDeclaredType());

        //ctx.createMarshaller().marshal(data, System.out);

//        MemoryAnalyzer analyzer = new MemoryAnalyzer();
//        analyzer.scanObject(data);
//        System.out.println("32: " + analyzer.getBytes32bit());
//        System.out.println("64: " + analyzer.getBytes64bit());

//        analyzer2.scanObject(data);
//        System.out.println("32: " + analyzer2.getBytes32bit());
//        System.out.println("64: " + analyzer2.getBytes64bit());
//
    }

}
