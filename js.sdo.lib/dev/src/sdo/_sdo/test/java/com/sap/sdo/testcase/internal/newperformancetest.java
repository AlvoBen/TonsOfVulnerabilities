/*
 * Copyright (c) 2007 by SAP AG, Walldorf.,
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
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.sax.SAXSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.sap.sdo.api.helper.SapXmlHelper;
import com.sap.sdo.api.impl.SapHelperProvider;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.context.HelperContextImpl;
import com.sap.sdo.impl.types.Namespace;
import com.sap.sdo.impl.types.TypeHelperImpl;
import com.sap.sdo.impl.types.builtin.PropertyLogic;
import com.sap.sdo.impl.types.builtin.PropertyLogicFacade;
import com.sap.sdo.impl.types.builtin.TypeLogic;
import com.sap.sdo.impl.types.builtin.TypeLogicFacade;
import com.sap.sdo.testcase.MemoryAnalyzer;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.impl.HelperProvider;

/**
 * @author D042774
 *
 */
public class NewPerformanceTest {
    public static int RUNS = 10;
    public static int OUTPUT_INTERVAL = 10000;
    private static XMLReader xmlReader;

    static {
        try {
            xmlReader = XMLReaderFactory.createXMLReader();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }
    
    public static final String[][] ACCESSORS =
        { { "channel", "title" }, { "channel", "description" }, { "channel", "language" }, { "channel", "item", "title" }, { "channel", "item", "link" },
            { "channel", "item", "description" }, { "channel", "item", "guid" , "isPermaLink"}, { "channel", "item", "pubDate" }, { "channel", "item", "BuyItNowPrice" },
            { "channel", "item", "CurrentPrice" }, { "channel", "item", "EndTime" }, { "channel", "item", "BidCount" }, { "channel", "item", "Category" },
            { "channel", "item", "AuctionType" } };

    private static final String PACKAGE = "com/sap/sdo/testcase/schemas/";
    private long diff = 0;
    private HelperContext _helperContext;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        diff = 0;
        _helperContext = HelperProvider.getDefaultContext();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        if (diff > 0) {
            long millis = diff / 1000000;
            System.out.printf("Mapping %d data objects took %d ms (=%f mappings/sec)\n", RUNS, millis, (double) (1000 * RUNS) / millis);
        }
        System.out.println();
        SapHelperProvider.removeContext(_helperContext);
        _helperContext = null;
    }

    private static long _MemoryFoodprint;
    @Test
    public void testMemoryFoodprint() throws IOException {
        System.out.println("*** Memory footprint without types and properties! ***");
        loadSchema("message_many_1K.xsd");
        XMLDocument document = loadXML("message_1K.xml", getOptions(false));
        MemoryAnalyzer memoryAnalyzer = new MemoryAnalyzer();
        addFilters(memoryAnalyzer);
        int limit64bit = 2440;
        int limit32bit = 1808;
        try {
            analyzeDocument(document, memoryAnalyzer, limit64bit, limit32bit);
        } finally {
            _MemoryFoodprint = memoryAnalyzer.getBytes64bit();
        }
    }

    private static long _MemoryFoodprintSequenced;
    @Test
    public void testMemoryFoodprintSequenced() throws IOException {
        System.out.println("*** Memory footprint without types and properties Sequenced! ***");
        loadSchema("message_any_1K.xsd");
        XMLDocument document = loadXML("message_1K.xml", getOptions(false));
        MemoryAnalyzer memoryAnalyzer = new MemoryAnalyzer();
        addFilters(memoryAnalyzer);
        int limit64bit = 3288;
        int limit32bit = 2312;
        try {
            analyzeDocument(document, memoryAnalyzer, limit64bit, limit32bit);
        } finally {
            _MemoryFoodprintSequenced = memoryAnalyzer.getBytes64bit();
        }
    }

    private void addFilters(MemoryAnalyzer memoryAnalyzer) {
        memoryAnalyzer.addClassFilter(TypeLogicFacade.class);
        memoryAnalyzer.addClassFilter(TypeLogic.class);
        memoryAnalyzer.addClassFilter(PropertyLogicFacade.class);
        memoryAnalyzer.addClassFilter(PropertyLogic.class);
        memoryAnalyzer.addClassFilter(Namespace.TypeAndContextPair.class);
    }

    private static long _MemoryFoodprintWOSchemaSimple;
    @Test
    public void testMemoryFoodprintWOSchemaSimple() throws IOException {
        System.out.println("*** Memory footprint without types and properties without Schema simplified! ***");
        XMLDocument document = loadXML("message_1K.xml", getOptions(true));
        MemoryAnalyzer memoryAnalyzer = new MemoryAnalyzer();
        addFilters(memoryAnalyzer);
        int limit64bit = 3792;
        int limit32bit = 2480;
        try {
            analyzeDocument(document, memoryAnalyzer, limit64bit, limit32bit);
        } finally {
            _MemoryFoodprintWOSchemaSimple = memoryAnalyzer.getBytes64bit();
            System.out.println("*** Memory footprint without types with properties without Schema simplified! ***");
            memoryAnalyzer = new MemoryAnalyzer();
            addFiltersWithoutOpenProps(memoryAnalyzer);
            memoryAnalyzer.scanObject(document.getRootObject());
            System.out.println("64 bit: " + memoryAnalyzer.getBytes64bit()+ "\t32 bit: " + memoryAnalyzer.getBytes32bit());
        }
    }

    private static long _MemoryFoodprintWOSchemaGeneric;
    @Test
    public void testMemoryFoodprintWOSchemaGeneric() throws IOException {
        System.out.println("*** Memory footprint without types and properties without Schema generic! ***");
        XMLDocument document = loadXML("message_1K.xml", getOptions(false));
        MemoryAnalyzer memoryAnalyzer = new MemoryAnalyzer();
        addFilters(memoryAnalyzer);
        int limit64bit = 7360;
        int limit32bit = 4472;
        try {
            analyzeDocument(document, memoryAnalyzer, limit64bit, limit32bit);
        } finally {
            _MemoryFoodprintWOSchemaGeneric = memoryAnalyzer.getBytes64bit();
            System.out.println("*** Memory footprint without types with properties without Schema generic! ***");
            memoryAnalyzer = new MemoryAnalyzer();
            addFiltersWithoutOpenProps(memoryAnalyzer);
            memoryAnalyzer.scanObject(document.getRootObject());
            System.out.println("64 bit: " + memoryAnalyzer.getBytes64bit()+ "\t32 bit: " + memoryAnalyzer.getBytes32bit());
        }
    }

    private void addFiltersWithoutOpenProps(MemoryAnalyzer memoryAnalyzer) {
        HelperContext coreContext = SapHelperProvider.getContext(SapHelperProvider.CORE_CONTEXT_ID);
        memoryAnalyzer.addObjectFilter(coreContext);
        TypeHelperImpl coreTypeHelper = (TypeHelperImpl)coreContext.getTypeHelper();
        for (Type type: coreTypeHelper.getTypesForNamespace(URINamePair.DATATYPE_URI)) {
            memoryAnalyzer.addObjectFilter(type);
        }
        for (Type type: coreTypeHelper.getTypesForNamespace(URINamePair.DATATYPE_XML_URI)) {
            memoryAnalyzer.addObjectFilter(type);
        }
        for (Type type: coreTypeHelper.getTypesForNamespace(URINamePair.DATATYPE_JAVA_URI)) {
            memoryAnalyzer.addObjectFilter(type);
        }
        for (Type type: coreTypeHelper.getTypesForNamespace(URINamePair.CTX_URI)) {
            memoryAnalyzer.addObjectFilter(type);
        }
        for (Type type: coreTypeHelper.getTypesForNamespace(URINamePair.SCHEMA_URI)) {
            memoryAnalyzer.addObjectFilter(type);
        }
        for (Property prop: coreTypeHelper.getPropertiesForNamespace(URINamePair.DATATYPE_URI)) {
            memoryAnalyzer.addObjectFilter(prop);
        }
        for (Property prop: coreTypeHelper.getPropertiesForNamespace(URINamePair.DATATYPE_XML_URI)) {
            memoryAnalyzer.addObjectFilter(prop);
        }
        for (Property prop: coreTypeHelper.getPropertiesForNamespace(URINamePair.DATATYPE_JAVA_URI)) {
            memoryAnalyzer.addObjectFilter(prop);
        }
        for (Property prop: coreTypeHelper.getPropertiesForNamespace(URINamePair.CTX_URI)) {
            memoryAnalyzer.addObjectFilter(prop);
        }
        for (Property prop: coreTypeHelper.getPropertiesForNamespace(URINamePair.SCHEMA_URI)) {
            memoryAnalyzer.addObjectFilter(prop);
        }
        memoryAnalyzer.addClassFilter(TypeLogicFacade.class);
        memoryAnalyzer.addClassFilter(TypeLogic.class);
        memoryAnalyzer.addClassFilter(PropertyLogic.class);
        memoryAnalyzer.addClassFilter(Namespace.TypeAndContextPair.class);
        memoryAnalyzer.addClassFilter(HelperContextImpl.class);
    }

    private void analyzeDocument(XMLDocument document, MemoryAnalyzer memoryAnalyzer, int limit64bit, int limit32bit) {
        memoryAnalyzer.scanObject(document.getRootObject());
        System.out.println("64 bit: " + memoryAnalyzer.getBytes64bit()+ "\t32 bit: " + memoryAnalyzer.getBytes32bit());
        long dif64bit = memoryAnalyzer.getBytes64bit() - limit64bit;
        long dif32bit = memoryAnalyzer.getBytes32bit() - limit32bit;
        assertTrue(dif64bit + " bytes over limit", dif64bit <= 0);
        assertTrue(dif32bit + " bytes over limit", dif32bit <= 0);
        if (dif64bit < 0) {
            System.out.println((-dif64bit) + " bytes below old 64 bit limit :-)");
        }
        if (dif32bit < 0) {
            System.out.println((-dif32bit) + " bytes below old 32 bit limit :-)");
        }
    }

    private static double _SimpleMappingPath = Double.MAX_VALUE;
    @Test
    public void testSimpleMappingPath() throws IOException {
        System.out.println("*** Mapping using pathes! ***");
        loadSchema("message_many_1K.xsd");
        XMLDocument document = loadXML("message_1K.xml", getOptions(false));
        loopSimpleMappingPath(document);
        _SimpleMappingPath = Math.min(_SimpleMappingPath, getMillisPerMapping());
    }

    private static double _SimpleMappingPathSequenced = Double.MAX_VALUE;
    @Test
    public void testSimpleMappingPathSequenced() throws IOException {
        System.out.println("*** Mapping using pathes Sequenced! ***");
        loadSchema("message_any_1K.xsd");
        XMLDocument document = loadXML("message_1K.xml", getOptions(false));
        loopSimpleMappingPath(document);
        _SimpleMappingPathSequenced = Math.min(_SimpleMappingPathSequenced, getMillisPerMapping());
    }

    private static double _SimpleMappingPathWOSchemaSimple = Double.MAX_VALUE;
    @Test
    public void testSimpleMappingPathWOSchemaSimple() throws IOException {
        System.out.println("*** Mapping using pathes without Schema simplified! ***");
        XMLDocument document = loadXML("message_1K.xml", getOptions(true));
        loadSchema("message_any_1K.xsd");
        loopSimpleMappingPath(document);
        _SimpleMappingPathWOSchemaSimple = Math.min(_SimpleMappingPathWOSchemaSimple, getMillisPerMapping());
    }

    private static double _SimpleMappingPathWOSchemaGeneric = Double.MAX_VALUE;
    @Test
    public void testSimpleMappingPathWOSchemaGeneric() throws IOException {
        System.out.println("*** Mapping using pathes without Schema generic! ***");
        XMLDocument document = loadXML("message_1K.xml", getOptions(false));
        loadSchema("message_any_1K.xsd");
        loopSimpleMappingPath(document);
        _SimpleMappingPathWOSchemaGeneric = Math.min(_SimpleMappingPathWOSchemaGeneric, getMillisPerMapping());
    }

    private void loopSimpleMappingPath(XMLDocument document) {
        for (int i = 0; i < RUNS; i++) {
            measureSimpleMappingPath(document.getRootObject());
            if ((i + 1) % OUTPUT_INTERVAL == 0) {
                System.out.printf("%d data objects mapped, took %d ns\n", i + 1, diff);
                System.out.flush();
            }
        }
    }

    private static double _SimpleMappingIndex = Double.MAX_VALUE;
    @Test
    public void testSimpleMappingIndex() throws IOException {
        System.out.println("*** Mapping using indexes! ***");
        loadSchema("message_many_1K.xsd");
        XMLDocument document = loadXML("message_1K.xml", getOptions(false));
        loopSimpleMappingIndex(document);
        _SimpleMappingIndex = Math.min(_SimpleMappingIndex, getMillisPerMapping());
    }

    private static double _SimpleMappingIndexSequenced = Double.MAX_VALUE;
    @Test
    public void testSimpleMappingIndexSequenced() throws IOException {
        System.out.println("*** Mapping using indexes Sequenced! ***");
        loadSchema("message_any_1K.xsd");
        XMLDocument document = loadXML("message_1K.xml", getOptions(false));
        loopSimpleMappingIndex(document);
        _SimpleMappingIndexSequenced = Math.min(_SimpleMappingIndexSequenced, getMillisPerMapping());
    }

    private void loopSimpleMappingIndex(XMLDocument document) {
        DataObject origin = document.getRootObject();
        int[][] indexes = computePropertyIndexes(origin);
        for (int i = 0; i < RUNS; i++) {
            measureSimpleMappingIndex(origin, indexes);
            if ((i + 1) % OUTPUT_INTERVAL == 0) {
                System.out.printf("%d data objects mapped, took %d ns\n", i + 1, diff);
                System.out.flush();
            }
        }
    }

    private static double _SimpleMappingProperty = Double.MAX_VALUE;
    @Test
    public void testSimpleMappingProperty() throws IOException {
        System.out.println("*** Mapping using Property! ***");
        loadSchema("message_many_1K.xsd");
        XMLDocument document = loadXML("message_1K.xml", getOptions(false));
        loopSimpleMappingProperty(document);
        _SimpleMappingProperty = Math.min(_SimpleMappingProperty, getMillisPerMapping());
    }

    private static double _SimpleMappingPropertySequenced = Double.MAX_VALUE;
    @Test
    public void testSimpleMappingPropertySequenced() throws IOException {
        System.out.println("*** Mapping using Property Sequenced! ***");
        loadSchema("message_any_1K.xsd");
        XMLDocument document = loadXML("message_1K.xml", getOptions(false));
        loopSimpleMappingProperty(document);
        _SimpleMappingPropertySequenced = Math.min(_SimpleMappingPropertySequenced, getMillisPerMapping());
    }

    private void loopSimpleMappingProperty(XMLDocument document) {
        DataObject origin = document.getRootObject();
        Property[][] indexes = computeProperties(origin);
        for (int i = 0; i < RUNS; i++) {
            measureSimpleMappingProperty(origin, indexes);
            if ((i + 1) % OUTPUT_INTERVAL == 0) {
                System.out.printf("%d data objects mapped, took %d ns\n", i + 1, diff);
                System.out.flush();
            }
        }
    }

    private static double _XMLParsing = Double.MAX_VALUE;
    @Test
    public void testXMLParsing() throws IOException {
        System.out.println("*** XML Parsing! ***");
        loadSchema("message_many_1K.xsd");
        URL url = getClass().getClassLoader().getResource(PACKAGE + "message_1K.xml");
        String xml = this.readFile(url);
        loopXMLParsing(xml, false);
        _XMLParsing = Math.min(_XMLParsing, getMillisPerMapping());
    }

    private static double _XMLParsingSequenced = Double.MAX_VALUE;
    @Test
    public void testXMLParsingSequenced() throws IOException {
        System.out.println("*** XML Parsing Sequenced! ***");
        loadSchema("message_any_1K.xsd");
        URL url = getClass().getClassLoader().getResource(PACKAGE + "message_1K.xml");
        String xml = this.readFile(url);
        loopXMLParsing(xml, false);
        _XMLParsingSequenced = Math.min(_XMLParsingSequenced, getMillisPerMapping());
    }

    private static double _XMLParsingWOSchemaSimple = Double.MAX_VALUE;
    @Test
    public void testXMLParsingWOSchemaSimple() throws IOException {
        System.out.println("*** XML Parsing without Schema simplified! ***");
        URL url = getClass().getClassLoader().getResource(PACKAGE + "message_1K.xml");
        String xml = this.readFile(url);
        loopXMLParsing(xml, true);
        _XMLParsingWOSchemaSimple = Math.min(_XMLParsingWOSchemaSimple, getMillisPerMapping());
    }

    private static double _XMLParsingWOSchemaGeneric = Double.MAX_VALUE;
    @Test
    public void testXMLParsingWOSchemaGeneric() throws IOException {
        System.out.println("*** XML Parsing without Schema generic! ***");
        URL url = getClass().getClassLoader().getResource(PACKAGE + "message_1K.xml");
        String xml = this.readFile(url);
        loopXMLParsing(xml, false);
        _XMLParsingWOSchemaGeneric = Math.min(_XMLParsingWOSchemaGeneric, getMillisPerMapping());
    }

    private void loopXMLParsing(String xml, boolean simplifyOpenContent) throws IOException {
        for (int i = 0; i < RUNS; i++) {
            measureXMLParsing(xml, simplifyOpenContent);
            if ((i + 1) % OUTPUT_INTERVAL == 0) {
                System.out.printf("%d data objects mapped, took %d ns\n", i + 1, diff);
                System.out.flush();
            }
        }
    }

    private static double _XMLSaxParsing = Double.MAX_VALUE;
    @Test
    public void testXMLSaxParsing() throws IOException, SAXException {
        System.out.println("*** XML Sax Parsing! ***");
        loadSchema("message_many_1K.xsd");
        URL url = getClass().getClassLoader().getResource(PACKAGE + "message_1K.xml");
        String xml = this.readFile(url);
        loopXMLSaxParsing(xml, false);
        _XMLSaxParsing = Math.min(_XMLSaxParsing, getMillisPerMapping());
    }

    private static double _XMLSaxParsingSequenced = Double.MAX_VALUE;
    @Test
    public void testXMLSaxParsingSequenced() throws IOException, SAXException {
        System.out.println("*** XML Sax Parsing Sequenced! ***");
        loadSchema("message_any_1K.xsd");
        URL url = getClass().getClassLoader().getResource(PACKAGE + "message_1K.xml");
        String xml = this.readFile(url);
        loopXMLSaxParsing(xml, false);
        _XMLSaxParsingSequenced = Math.min(_XMLSaxParsingSequenced, getMillisPerMapping());
    }

    private static double _XMLSaxParsingWOSchemaSimple = Double.MAX_VALUE;
    @Test
    public void testXMLSaxParsingWOSchemaSimple() throws IOException, SAXException {
        System.out.println("*** XML Sax Parsing without Schema simplified! ***");
        URL url = getClass().getClassLoader().getResource(PACKAGE + "message_1K.xml");
        String xml = this.readFile(url);
        loopXMLSaxParsing(xml, true);
        _XMLSaxParsingWOSchemaSimple = Math.min(_XMLSaxParsingWOSchemaSimple, getMillisPerMapping());
    }

    private static double _XMLSaxParsingWOSchemaGeneric = Double.MAX_VALUE;
    @Test
    public void testXMLSaxParsingWOSchemaGeneric() throws IOException, SAXException {
        System.out.println("*** XML Sax Parsing without Schema generic! ***");
        URL url = getClass().getClassLoader().getResource(PACKAGE + "message_1K.xml");
        String xml = this.readFile(url);
        loopXMLSaxParsing(xml, false);
        _XMLSaxParsingWOSchemaGeneric = Math.min(_XMLSaxParsingWOSchemaGeneric, getMillisPerMapping());
    }

    private void loopXMLSaxParsing(String xml, boolean simplifyOpenContent) throws IOException, SAXException {
        for (int i = 0; i < RUNS; i++) {
            measureXMLSaxParsing(xml, simplifyOpenContent);
            if ((i + 1) % OUTPUT_INTERVAL == 0) {
                System.out.printf("%d data objects mapped, took %d ns\n", i + 1, diff);
                System.out.flush();
            }
        }
    }

    private static double _XMLRendering = Double.MAX_VALUE;
    @Test
    public void testXMLRendering() throws Exception {
        System.out.println("*** XML Rendering! ***");
        loadSchema("message_many_1K.xsd");
        Map options = getOptions(false);
        XMLDocument document = loadXML("message_1K.xml", options);
        loopXMLRendering(document, options);
        _XMLRendering = Math.min(_XMLRendering, getMillisPerMapping());
    }

    private static double _XMLRenderingSequenced = Double.MAX_VALUE;
    @Test
    public void testXMLRenderingSequenced() throws Exception {
        System.out.println("*** XML Rendering Sequenced! ***");
        loadSchema("message_any_1K.xsd");
        Map options = getOptions(false);
        XMLDocument document = loadXML("message_1K.xml", options);
        loopXMLRendering(document, options);
        _XMLRenderingSequenced = Math.min(_XMLRenderingSequenced, getMillisPerMapping());
    }

    private static double _XMLRenderingWOSchemaSimple = Double.MAX_VALUE;
    @Test
    public void testXMLRenderingWOSchemaSimple() throws Exception {
        System.out.println("*** XML Rendering without Schema simplified! ***");
        Map options = getOptions(true);
        XMLDocument document = loadXML("message_1K.xml", options);
        loopXMLRendering(document, options);
        _XMLRenderingWOSchemaSimple = Math.min(_XMLRenderingWOSchemaSimple, getMillisPerMapping());
    }

    private static double _XMLRenderingWOSchemaGeneric = Double.MAX_VALUE;
    @Test
    public void testXMLRenderingWOSchemaGeneric() throws Exception {
        System.out.println("*** XML Rendering without Schema generic! ***");
        Map options = getOptions(false);
        XMLDocument document = loadXML("message_1K.xml", options);
        loopXMLRendering(document, options);
        _XMLRenderingWOSchemaGeneric = Math.min(_XMLRenderingWOSchemaGeneric, getMillisPerMapping());
    }

    private void loopXMLRendering(XMLDocument document, Map options) throws IOException {
        for (int i = 0; i < RUNS; i++) {
            measureXMLRendering(document, options);
            if ((i + 1) % OUTPUT_INTERVAL == 0) {
                System.out.printf("%d data objects mapped, took %d ns\n", i + 1, diff);
                System.out.flush();
            }
        }
    }

    private static double _XMLStreaming = Double.MAX_VALUE;
    @Test
    public void testXMLStreaming() throws Exception {
        System.out.println("*** XML Streaming! ***");
        loadSchema("message_many_1K.xsd");
        Map options = getOptions(false);
        XMLDocument document = loadXML("message_1K.xml", options);
        loopXMLStreaming(document, options);
        _XMLStreaming = Math.min(_XMLStreaming, getMillisPerMapping());
    }

    private static double _XMLStreamingSequenced = Double.MAX_VALUE;
    @Test
    public void testXMLStreamingSequenced() throws Exception {
        System.out.println("*** XML Streaming Sequenced! ***");
        loadSchema("message_any_1K.xsd");
        Map options = getOptions(false);
        XMLDocument document = loadXML("message_1K.xml", options);
        loopXMLStreaming(document, options);
        _XMLStreamingSequenced = Math.min(_XMLStreamingSequenced, getMillisPerMapping());
    }

    private static double _XMLStreamingWOSchemaSimple = Double.MAX_VALUE;
    @Test
    public void testXMLStreamingWOSchemaSimple() throws Exception {
        System.out.println("*** XML Streaming without Schema simplified! ***");
        Map options = getOptions(true);
        XMLDocument document = loadXML("message_1K.xml", options);
        loopXMLStreaming(document, options);
        _XMLStreamingWOSchemaSimple = Math.min(_XMLStreamingWOSchemaSimple, getMillisPerMapping());
    }

    private static double _XMLStreamingWOSchemaGeneric = Double.MAX_VALUE;
    @Test
    public void testXMLStreamingWOSchemaGeneric() throws Exception {
        System.out.println("*** XML Streaming without Schema generic! ***");
        Map options = getOptions(false);
        XMLDocument document = loadXML("message_1K.xml", options);
        loopXMLStreaming(document, options);
        _XMLStreamingWOSchemaGeneric = Math.min(_XMLStreamingWOSchemaGeneric, getMillisPerMapping());
    }

    private void loopXMLStreaming(XMLDocument document, Map options) throws IOException, XMLStreamException {
        for (int i = 0; i < RUNS; i++) {
            measureXMLStreaming(document, options);
            if ((i + 1) % OUTPUT_INTERVAL == 0) {
                System.out.printf("%d data objects mapped, took %d ns\n", i + 1, diff);
                System.out.flush();
            }
        }
    }

    private Map getOptions(boolean simplifyOpenContent) {
        Map options = new HashMap();
        if (!simplifyOpenContent) {
            options.put("com.sap.sdo.api.helper.SapXmlHelper.SimplifyOpenContent", Boolean.FALSE.toString());
        }
        options.put("com.sap.sdo.api.helper.SapXmlHelper.DefineSchemas", Boolean.FALSE.toString());
        return options;
    }

    private void measureSimpleMappingPath(DataObject origin) {
        DataObject target;
        DataObject os, ot;
        int i, j;
        long start, end;
        start = System.nanoTime();

        target = _helperContext.getDataFactory().create("http://www.sap.com", "rss");
        for (i = 0; i < ACCESSORS.length; i++) {
            StringBuilder path = new StringBuilder();


            os = origin;
            ot = target;

            for (j = 0; j < ACCESSORS[i].length - 1; j++) {
                path.append('/');
                Property ps = os.getInstanceProperty(ACCESSORS[i][j]);
                String pathGet;
                if (ps.isMany() || ps.isOpenContent()) {
                    pathGet = ACCESSORS[i][j]+"[1]";
                } else {
                    pathGet = ACCESSORS[i][j];
                }
                os = os.getDataObject(pathGet);
                path.append(pathGet);

                Property pt = ot.getInstanceProperty(ACCESSORS[i][j]);
                String pathSet;
                if (pt.isMany()) {
                    pathSet = ACCESSORS[i][j]+"[1]";
                } else {
                    pathSet = ACCESSORS[i][j];
                }
                if (ot.isSet(ACCESSORS[i][j])) {
                    ot = ot.getDataObject(pathSet);
                } else {
                    ot = ot.createDataObject(ACCESSORS[i][j]);
                }

            }
            Property pt = ot.getInstanceProperty(ACCESSORS[i][ACCESSORS[i].length - 1]);
            String pathStep;
            if (pt.isMany()) {
                pathStep = ACCESSORS[i][ACCESSORS[i].length - 1]+"[1]";
            } else {
                pathStep = ACCESSORS[i][ACCESSORS[i].length - 1];
            }
            path.append('/');
            path.append(ACCESSORS[i][ACCESSORS[i].length - 1]);
            Property ps = os.getInstanceProperty(ACCESSORS[i][ACCESSORS[i].length - 1]);
            if (ps.isMany() || ps.isOpenContent()) {
                path.append("[1]");
            }
            Object value = origin.get(path.toString());
            if (value instanceof DataObject && pt.getType().isDataType()) {
                ot.setString(pathStep, (String)((DataObject)value).getSequence().getValue(0));
            } else {
                ot.set(pathStep, value);
            }
        }

        end = System.nanoTime();
        diff += end - start;
    }

    private void measureSimpleMappingIndex(DataObject origin, int[][] indexes) {

            DataObject target;

            DataObject os, ot;
            int i, j;
            long start, end;
            start = System.nanoTime();

            target = _helperContext.getDataFactory().create("http://www.sap.com", "rss");
            for (i = 0; i < indexes.length; i++) {
                os = origin;
                ot = target;

                for (j = 0; j < indexes[i].length - 1; j++) {
                    Property ps = (Property)os.getInstanceProperties().get(indexes[i][j]);
                    if (ps.isMany()) {
                        os = (DataObject)os.getList(indexes[i][j]).get(0);
                    } else {
                        os = os.getDataObject(indexes[i][j]);
                    }
                    if (ot.isSet(indexes[i][j])) {
                        Property pt = (Property)ot.getInstanceProperties().get(indexes[i][j]);
                        if (pt.isMany()) {
                            ot = (DataObject)ot.getList(indexes[i][j]).get(0);
                        } else {
                            ot = ot.getDataObject(indexes[i][j]);
                        }
                    } else {
                        ot = ot.createDataObject(indexes[i][j]);
                    }
                }
                ot.set(indexes[i][indexes[i].length - 1], os.get(indexes[i][indexes[i].length - 1]));
            }

            end = System.nanoTime();
            diff += end - start;
    }

    private void measureSimpleMappingProperty(DataObject origin, Property[][] properties) {

        DataObject target;

        DataObject os, ot;
        int i, j;
        long start, end;
        start = System.nanoTime();

        target = _helperContext.getDataFactory().create("http://www.sap.com", "rss");
        //for (i = 0; i < ACCESSORS.length; i++) {
        for (i = 0; i < properties.length; i++) {
            os = origin;
            ot = target;

            for (j = 0; j < properties[i].length - 1; j++) {
                if (properties[i][j].isMany()) {
                    os = (DataObject)os.getList(properties[i][j]).get(0);
                } else {
                    os = os.getDataObject(properties[i][j]);
                }
                if (ot.isSet(properties[i][j])) {
                    if (properties[i][j].isMany()) {
                        ot = (DataObject)ot.getList(properties[i][j]).get(0);
                    } else {
                        ot = ot.getDataObject(properties[i][j]);
                    }
                } else {
                    ot = ot.createDataObject(properties[i][j]);
                }
            }
            ot.set(properties[i][properties[i].length - 1], os.get(properties[i][properties[i].length - 1]));
        }

        end = System.nanoTime();
        diff += end - start;
    }

    private void measureXMLParsing(String xml, boolean simplifyOpenContent) throws IOException {
        long start, end;
        Map options = new HashMap();
        if (!simplifyOpenContent) {
            options.put("com.sap.sdo.api.helper.SapXmlHelper.SimplifyOpenContent", Boolean.FALSE.toString());
        }
        options.put("com.sap.sdo.api.helper.SapXmlHelper.DefineSchemas", Boolean.FALSE.toString());
        start = System.nanoTime();
        _helperContext.getXMLHelper().load(new StringReader(xml), null, options);
        end = System.nanoTime();
        diff += (end - start);
    }

    private void measureXMLSaxParsing(String xml, boolean simplifyOpenContent) throws IOException, SAXException {
        long start, end;
        Map options = new HashMap();
        if (!simplifyOpenContent) {
            options.put("com.sap.sdo.api.helper.SapXmlHelper.SimplifyOpenContent", Boolean.FALSE.toString());
        }
        options.put("com.sap.sdo.api.helper.SapXmlHelper.DefineSchemas", Boolean.FALSE.toString());
        SAXSource saxSource = new SAXSource(xmlReader, new InputSource(new StringReader(xml)));
        start = System.nanoTime();
        _helperContext.getXMLHelper().load(saxSource, null, options);
        end = System.nanoTime();
        diff += (end - start);
    }

    private void measureXMLRendering(XMLDocument document, Map options) throws IOException {
        long start, end;

        start = System.nanoTime();

        _helperContext.getXMLHelper().save(document, new ByteArrayOutputStream(), options);
        end = System.nanoTime();
        diff += end - start;
    }

    private int[][] computePropertyIndexes(DataObject root) {
        List<Property> list;
        int[][] indexes = new int[ACCESSORS.length][];
        for (int i = 0; i < ACCESSORS.length; i++) {
            DataObject o = root;
            indexes[i] = new int[ACCESSORS[i].length];
            for (int j = 0; j < ACCESSORS[i].length; j++) {
                list = o.getInstanceProperties();
                for (int k = 0; k < list.size(); k++) {
                    Property prop = list.get(k);
                    if (prop.getName().equals(ACCESSORS[i][j])) {
                        indexes[i][j] = k;
                        if (j < ACCESSORS[i].length - 1)
                            if (prop.isMany()) {
                                o = (DataObject)o.getList(k).get(0);
                            } else {
                                o = o.getDataObject(k);
                            }
                        break;
                    }
                }
            }
        }
        return indexes;
    }

    private Property[][] computeProperties(DataObject root) {
        Property[][] properties = new Property[ACCESSORS.length][];
        for (int i = 0; i < ACCESSORS.length; i++) {
            DataObject o = root;
            properties[i] = new Property[ACCESSORS[i].length];
            for (int j = 0; j < ACCESSORS[i].length; j++) {
                 properties[i][j] = o.getInstanceProperty(ACCESSORS[i][j]);
                 assertNotNull(properties[i][j]);
                 if (j < ACCESSORS[i].length - 1)
                     if (properties[i][j].isMany()) {
                         o = (DataObject)o.getList(properties[i][j]).get(0);
                     } else {
                         o = o.getDataObject(properties[i][j]);
                     }
            }
        }
        return properties;
    }

    public void loadSchema(String schemaName) throws IOException {
        URL url = getClass().getClassLoader().getResource(PACKAGE + schemaName);
        _helperContext.getXSDHelper().define(url.openStream(), url.toString());
    }

    public XMLDocument loadXML(String xmlName, Map options) throws IOException {
        URL url = getClass().getClassLoader().getResource(PACKAGE + xmlName);
        XMLDocument document = _helperContext.getXMLHelper().load(url.openStream(), url.toString(), options);
        return document;
    }

    private void measureXMLStreaming(XMLDocument document, Map options) throws IOException, XMLStreamException {
        long start, end;

        start = System.nanoTime();

        XMLStreamReader reader = ((SapXmlHelper)_helperContext.getXMLHelper()).createXMLStreamReader(document, options);
        measureXmlStreamReader(reader);
        end = System.nanoTime();
        diff += end - start;
    }

    private void measureXmlStreamReader(XMLStreamReader sdoReader) throws XMLStreamException {
        if (sdoReader.getEventType() != XMLStreamConstants.END_DOCUMENT) {
            sdoReader.getEncoding();
            sdoReader.getVersion();
        }

        sdoReader.getCharacterEncodingScheme();
        sdoReader.getLocation();

        sdoReader.isCharacters();
        sdoReader.isEndElement();
        sdoReader.isStandalone();
        sdoReader.isStartElement();
        sdoReader.isWhiteSpace();

        sdoReader.standaloneSet();
        //sdoReader.getPITarget();
        //sdoReader.getPIData();

        if (sdoReader.hasName()) {
            sdoReader.getLocalName();
            sdoReader.getNamespaceURI();
            sdoReader.getName();
        }
        sdoReader.getPrefix();

        //attributes
        if (sdoReader.getEventType() == XMLStreamConstants.START_ELEMENT) {
            measureAttribute(sdoReader);
        }

        //namespaces
        if (sdoReader.getEventType() == XMLStreamConstants.START_ELEMENT) {
            measureNamespace(sdoReader);
        }

        if (sdoReader.hasText()) {
            String text = sdoReader.getText();
        }

        if (sdoReader.hasNext()) {
            sdoReader.next();
            measureXmlStreamReader(sdoReader);
        }
    }


    private void measurePrefix(String prefix, NamespaceContext sdoNsCtx) {
        String uri = sdoNsCtx.getNamespaceURI(prefix);
        if (uri != null && uri.length() > 0) {
            sdoNsCtx.getPrefix(uri);
            Iterator prefixes = sdoNsCtx.getPrefixes(uri);
            while (prefixes.hasNext()) {
                prefixes.next();
            }
}
    }

    private void measureNamespace(XMLStreamReader sdoReader) {
        int namespaceCount = sdoReader.getNamespaceCount();
        for (int i=0; i<namespaceCount; ++i) {
            String prefix = sdoReader.getNamespacePrefix(i);
            sdoReader.getNamespaceURI(i);
            sdoReader.getNamespaceURI(prefix);
        }
    }

    private void measureAttribute(XMLStreamReader sdoReader) {
        int attributeCount = sdoReader.getAttributeCount();
        for (int i=0; i<attributeCount; ++i) {
            String localName = sdoReader.getAttributeLocalName(i);
            String namespace = sdoReader.getAttributeNamespace(i);

            sdoReader.getAttributeValue(namespace, localName);

            sdoReader.getAttributeName(i);
            sdoReader.getAttributePrefix(i);

            sdoReader.getAttributeType(i);
            sdoReader.getAttributeValue(i);
            sdoReader.isAttributeSpecified(i);
        }
        sdoReader.getAttributeValue(URINamePair.XSI_URI, "nil");
        sdoReader.getAttributeValue(URINamePair.XSI_URI, "type");
    }

    private double getMillisPerMapping() {
        return  diff /(RUNS * 1000000d);
    }

    private String readFile(URL pUrl) throws IOException {
        return readFile(new InputStreamReader(pUrl.openStream()));
    }

    private String readFile(InputStreamReader pReader) throws IOException {
        BufferedReader in = new BufferedReader(pReader);
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        while (true) {
            String line = in.readLine();
            if (line == null) {
                return writer.toString();
            }
            printWriter.print(line);
            printWriter.print('\n');
        }
    }


    public static void main(String[] args) {
        int warmup = 5000;
        int measurments = 10000;
        int bestOf = 3;
        // warm up
        RUNS = warmup;
        JUnitCore.runClasses(NewPerformanceTest.class);
        // measure
        RUNS = measurments;
        for (int i = 0; i < bestOf; i++) {
            JUnitCore.runClasses(NewPerformanceTest.class);
        }

        System.out.println(new Date(System.currentTimeMillis()));
        System.out.println(System.getProperty("java.vm.name") + ' ' + System.getProperty("java.version") + ' ' + System.getProperty("java.vm.version"));
        System.out.println("Warm up:      " + warmup);
        System.out.println("Measurements: " + measurments + " best of " + bestOf);
        System.out.println("\t\tnon-sequenced\tsequenced\tno XSD simple\tno XSD generic");
        System.out.println("64 bit memory\t"+_MemoryFoodprint+"\t\t"+_MemoryFoodprintSequenced+"\t\t"+_MemoryFoodprintWOSchemaSimple+"\t\t"+_MemoryFoodprintWOSchemaGeneric+"\t\t"+"bytes");
        System.out.println("map path\t"+_SimpleMappingPath+'\t'+_SimpleMappingPathSequenced+'\t'+_SimpleMappingPathWOSchemaSimple+'\t'+_SimpleMappingPathWOSchemaGeneric+'\t'+"ms");
        System.out.println("map index\t"+_SimpleMappingIndex+'\t'+_SimpleMappingIndexSequenced+"\t\t\t\t\t"+"ms");
        System.out.println("map property\t"+_SimpleMappingProperty+'\t'+_SimpleMappingPropertySequenced+"\t\t\t\t\t"+"ms");
        System.out.println("parse\t\t"+_XMLParsing+'\t'+_XMLParsingSequenced+'\t'+_XMLParsingWOSchemaSimple+'\t'+_XMLParsingWOSchemaGeneric+'\t'+"ms");
        System.out.println("parse SAX\t"+_XMLSaxParsing+'\t'+_XMLSaxParsingSequenced+'\t'+_XMLSaxParsingWOSchemaSimple+'\t'+_XMLSaxParsingWOSchemaGeneric+'\t'+"ms");
        System.out.println("render\t\t"+_XMLRendering+'\t'+_XMLRenderingSequenced+'\t'+_XMLRenderingWOSchemaSimple+'\t'+_XMLRenderingWOSchemaGeneric+'\t'+"ms");
        System.out.println("StAX reader\t"+_XMLStreaming+'\t'+_XMLStreamingSequenced+'\t'+_XMLStreamingWOSchemaSimple+'\t'+_XMLStreamingWOSchemaGeneric+'\t'+"ms");
    }
}
