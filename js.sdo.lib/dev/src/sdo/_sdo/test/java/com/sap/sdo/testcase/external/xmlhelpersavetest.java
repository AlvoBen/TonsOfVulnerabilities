/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.sdo.testcase.external;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.stream.StreamResult;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapXmlHelper;
import com.sap.sdo.api.helper.SapXsdHelper;
import com.sap.sdo.api.helper.util.SDOContentHandler;
import com.sap.sdo.api.helper.util.SDOResult;
import com.sap.sdo.api.types.schema.Schema;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.builtin.DataGraphType;
import com.sap.sdo.impl.types.builtin.OpenType;
import com.sap.sdo.impl.types.builtin.PropertyType;
import com.sap.sdo.impl.types.builtin.TypeType;
import com.sap.sdo.impl.types.builtin.DataGraphType.XsdType;
import com.sap.sdo.impl.types.simple.ListSimpleType;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.IPolymorphicProperty;
import com.sap.sdo.testcase.typefac.LoggingRootIntf;
import com.sap.sdo.testcase.typefac.LoggingRootSequencedIntf;
import com.sap.sdo.testcase.typefac.OpenInterface;
import com.sap.sdo.testcase.typefac.OpenSequencedInterface;
import com.sap.sdo.testcase.typefac.OppositePropsA;
import com.sap.sdo.testcase.typefac.OppositePropsB;
import com.sap.sdo.testcase.typefac.SequencedOppositeIntf;
import com.sap.sdo.testcase.typefac.SimpleAttrIntf;
import com.sap.sdo.testcase.typefac.SimpleContainedIntf;
import com.sap.sdo.testcase.typefac.SimpleContainingIntf;
import com.sap.sdo.testcase.typefac.SimpleExtension;
import com.sap.sdo.testcase.typefac.SimpleIdIntf;
import com.sap.sdo.testcase.typefac.SimpleIntf1;
import com.sap.sdo.testcase.typefac.SimpleMappedIntf;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataGraph;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.ChangeSummary.Setting;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;

public class XMLHelperSaveTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public XMLHelperSaveTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private XMLHelper _helper = null;
    private static final String NS1 = " xmlns:ns1=\"com.sap.sdo.testcase.typefac\"";

    @Before
    public void setUp() throws Exception {
        _helper = _helperContext.getXMLHelper();
    }

    @After
    public void tearDown() throws Exception {
        _helper = null;
    }

    /*
     * Test method for 'com.sap.sdo.impl.xml.XMLHelperImpl.save(DataObject, String, String)'
     */
    @Test
    public void testSaveDataObjectStringString() {
        SimpleAttrIntf attr = (SimpleAttrIntf)_helperContext.getDataFactory().create(SimpleAttrIntf.class);
        attr.setName("Stefan");
        attr.setData("a data");
        attr.setGreen(true);
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<ns1:simple" + NS1 +
        " xsi:type=\"ns1:SimpleAttrIntf\" name=\"Stefan\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
        " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n" +
        "  <data>a data</data>\n" +
        "  <green>true</green>\n" +
        "</ns1:simple>\n";
        DataObject attrObj = (DataObject)attr;
        String savedXML = _helper.save(attrObj, attrObj.getType().getURI(), "simple");
        System.out.println( savedXML);
        assertLineEquality("XMLHelper doesn't produce expected XML", xml, savedXML);

        SimpleMappedIntf mapped = (SimpleMappedIntf)_helperContext.getDataFactory().create(SimpleMappedIntf.class);
        //((DataObject)mapped).set("xyz", "a data");
        mapped.setData("a data");
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<ns1:simple" + NS1
            + " xsi:type=\"ns1:SimpleMappedIntf\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"
            + "  <xyz>a data</xyz>\n"
            + "</ns1:simple>\n";
        DataObject mappedObj = (DataObject)mapped;
        assertLineEquality("XMLHelper doesn't produce expected XML",
            xml,
            _helper.save(mappedObj, mappedObj.getType().getURI(), "simple"));

        SimpleContainedIntf inner =
            (SimpleContainedIntf)_helperContext.getDataFactory().create(SimpleContainedIntf.class);
        inner.setName("name");
        SimpleContainingIntf containing =
            (SimpleContainingIntf)_helperContext.getDataFactory().create(SimpleContainingIntf.class);
        containing.setInner(inner);
        containing.setX("x");
        String preStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<ns1:outer" + NS1
            + " xsi:type=\"ns1:SimpleContainingIntf\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n";
        String xStr = "  <x>x</x>\n";
        String innerStr = "  <inner>\n"
            + "    <name>name</name>\n"
            + "  </inner>\n";
        String postStr = "</ns1:outer>\n";
        DataObject containingObj = (DataObject)containing;
        String writtenXml = _helper.save(containingObj, containingObj.getType().getURI(), "outer");
        assertLineEquality(
            "XMLHelper doesn't produce expected XML: " + writtenXml,
            preStr, writtenXml.substring(0,preStr.length()));
        assertTrue("XMLHelper doesn't produce expected XML: " + writtenXml, writtenXml.endsWith(postStr));
        assertFalse("XMLHelper doesn't produce expected XML: " + writtenXml, writtenXml.indexOf(innerStr)<0);
        assertFalse("XMLHelper doesn't produce expected XML: " + writtenXml, writtenXml.indexOf(xStr)<0);
        assertEquals(
            "XMLHelper doesn't produce expected XML: " + writtenXml,
            preStr.length()+postStr.length()+innerStr.length()+xStr.length(), writtenXml.length());

        OppositePropsA oppA = (OppositePropsA)_helperContext.getDataFactory().create(OppositePropsA.class);
        OppositePropsB oppB1 = (OppositePropsB)_helperContext.getDataFactory().create(OppositePropsB.class);
        OppositePropsB oppB2 = (OppositePropsB)_helperContext.getDataFactory().create(OppositePropsB.class);
        ((DataObject)oppB1).set("a", oppA);
        ((DataObject)oppB2).set("a", oppA);
        ArrayList<OppositePropsB> opposites = new ArrayList<OppositePropsB>(2);
        opposites.add(oppB1);
        opposites.add(oppB2);
        ((DataObject)oppA).set("bs", opposites);
        assertNotNull(oppA.getBs());
        assertFalse(oppA.getBs().isEmpty());
        DataObject openIntf = _helperContext.getDataFactory().create(OpenInterface.class);
        ((DataObject)oppA).detach();
        openIntf.setDataObject("a", (DataObject)oppA);
        assertEquals("parent container association incorrect ",
            openIntf,
            ((DataObject)oppA).getContainer());
        DataObject pB = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        pB.set("name", "b");
        pB.set("type", ((DataObject)oppB1).getType());
        pB.setBoolean("containment", true);
        pB.setBoolean("many", true);
        Property prB = _helperContext.getTypeHelper().defineOpenContentProperty(null,pB);
        assertTrue("generated property is not containment", prB.isContainment());
        assertTrue("generated property is not many", prB.isMany());
        List<DataObject> l = openIntf.getList(prB);
        l.add((DataObject)oppB1);
        assertEquals("parent container association incorrect ",
            openIntf,
            ((DataObject)oppB1).getContainer());
        l.add((DataObject)oppB2);
        assertEquals("parent container association incorrect ",
            openIntf,
            ((DataObject)oppB2).getContainer());
        // openIntf.setList(prB, openIntf.getList(prB));
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<ns1:opposite" + NS1
            + " xsi:type=\"ns1:OpenInterface\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"
            + "  <a xsi:type=\"ns1:OppositePropsA\">\n"
            + "    <bs>#/ns1:opposite/b.0</bs>\n"
            + "    <bs>#/ns1:opposite/b.1</bs>\n"
            + "  </a>\n"
            + "  <b xsi:type=\"ns1:OppositePropsB\">\n"
            + "    <a>#/ns1:opposite/a</a>\n"
            + "  </b>\n"
            + "  <b xsi:type=\"ns1:OppositePropsB\">\n"
            + "    <a>#/ns1:opposite/a</a>\n"
            + "  </b>\n"
            + "</ns1:opposite>\n";
        assertLineEquality("XMLHelper doesn't produce expected XML",
            xml,
            _helper.save(openIntf, openIntf.getType().getURI(), "opposite"));


    }

    @Test
    public void testSaveWithDetachedElement() throws IOException {
        DataObject a = _helperContext.getDataFactory().create(OpenInterface.class);
        DataObject openIntf = _helperContext.getDataFactory().create(OpenInterface.class);
        LoggingRootIntf root = (LoggingRootIntf)_helperContext.getDataFactory().create(LoggingRootIntf.class);
        openIntf.setList("a", Collections.singletonList(a));
        root.setOpenInterface((OpenInterface)openIntf);
        DataObject copy1 = _helperContext.getCopyHelper().copy((DataObject)root);

        if (!_helperContext.getEqualityHelper().equal((DataObject)root, copy1)) {
            assertEquals(
                _helper.save((DataObject)root, null, "root"),
                _helper.save(copy1, null, "root"));
        }

        String test = _helper.save((DataObject)root, null, "root");
        System.out.println(test);
        final XMLDocument document = _helper.load(test);
        assertEquals(test, _helper.save(document.getRootObject(), null, "root"));

        final ChangeSummary cs = root.getChangeSummary();
        cs.beginLogging();
        a.detach();

        String test2 = _helper.save((DataObject)root, null, "root");
        System.out.println(test2);
        Map<String,String> options = new HashMap<String,String>();
        options.put(SapXmlHelper.OPTION_KEY_SIMPLIFY_OPEN_CONTENT, SapXmlHelper.OPTION_VALUE_FALSE);
        final XMLDocument document2 = _helper.load(new StringReader(test2), null, options);
        assertEquals(test2, _helper.save(document2.getRootObject(), null, "root"));

        document2.getRootObject().getChangeSummary().undoChanges();
        if (!_helperContext.getEqualityHelper().equal(copy1, document2.getRootObject())) {
            assertEquals(
                _helper.save(copy1, null, "root"),
                _helper.save(document2.getRootObject(), null, "root"));
        }

        DataObject b = _helperContext.getDataFactory().create(OpenInterface.class);
        openIntf.setList("b", Collections.singletonList(b));
        b.setList("a", Collections.singletonList(a));
        DataObject copy2 = _helperContext.getCopyHelper().copy((DataObject)root);
        String out = _helper.save((DataObject)root, null, "root");
        System.out.println(out);
        String outC2 = _helper.save(copy2, null, "root");
        assertEquals(out, outC2);
        assertTrue(_helperContext.getEqualityHelper().equal((DataObject)root, copy2));

        for (Setting setting : (List<Setting>)cs.getOldValues(openIntf)) {
            if ("b".equals(setting.getProperty().getName())) {
                assertEquals(setting.getProperty().getName(), false, setting.isSet());
            }
        }

//        out = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
//+ "<root xsi:type=\"LoggingRootIntf\" xmlns:sdo=\"commonj.sdo\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"com.sap.sdo.testcase.typefac\">\n"
//+ "  <changeSummary create=\"#/root/openInterface/b\">\n"
//+ "    <openInterface xsi:type=\"OpenInterface\" sdo:ref=\"#/root/openInterface\" sdo:unset=\"b\">\n"
//+ "      <a xsi:type=\"OpenInterface\" sdo:many=\"false\" sdo:ref=\"#/root/openInterface/b/a\"/>\n"
//+ "    </openInterface>\n"
////+ "    <a xsi:type=\"OpenInterface\" sdo:ref=\"#/root/openInterface/b/a\">\n"
////+ "    </a>\n"
//+ "  </changeSummary>\n"
//+ "  <openInterface>\n"
//+ "    <b xsi:type=\"OpenInterface\" sdo:many=\"false\">\n"
//+ "      <a xsi:type=\"OpenInterface\" sdo:many=\"false\"/>\n"
//+ "    </b>\n"
//+ "  </openInterface>\n"
//+ "</root>";

        XMLDocument doc = _helper.load(new StringReader(out), null, options);

        StringWriter out2 = new StringWriter();
        _helper.save(doc, out2, null);

        assertEquals(out, out2.toString());

        DataObject b1 = doc.getRootObject().getDataObject("openInterface/b");

        if (!_helperContext.getEqualityHelper().equal(b, b1)) {
            assertEquals(
                _helper.save(b, null, "b"),
                _helper.save(b1, null, "b"));
        }

        Property oldB = null;

        DataObject openInterface = doc.getRootObject().getDataObject("openInterface");

        if (!_helperContext.getEqualityHelper().equal(openIntf, openInterface)) {
            assertEquals(
                _helper.save(openIntf, null, "openInterface"),
                _helper.save(openInterface, null, "openInterface"));
        }

        ChangeSummary changeSummary = doc.getRootObject().getChangeSummary();
        List<Setting> settings = changeSummary.getOldValues(openInterface);

        for (Setting setting : settings) {
            if ("b".equals(setting.getProperty().getName())) {
                oldB = setting.getProperty();
                assertEquals(setting.getProperty().getName(), false, setting.isSet());
            }
        }

        assertNotNull(oldB);

        Property newB = doc.getRootObject().getDataObject("openInterface").getInstanceProperty("b");
        assertSame(oldB, newB);

        if (!_helperContext.getEqualityHelper().equal(copy2, doc.getRootObject())) {
            final String saved = _helper.save(doc.getRootObject(), null, "root");
            assertEquals(copy2.getDataObject("openInterface").getInstanceProperties().size(),
                doc.getRootObject().getDataObject("openInterface").getInstanceProperties().size());
            assertEquals(outC2, saved);
            int savedIndex = saved.indexOf("</changeSummary>");
            assertEquals(out.substring(0,savedIndex), saved.substring(0,savedIndex));
            // cut modified a-element
            assertEquals(out.substring(out.indexOf("</changeSummary>")), saved.substring(savedIndex));
            fail("bug in EqualityHelper");
        }

        doc.getRootObject().getChangeSummary().undoChanges();
        if (!_helperContext.getEqualityHelper().equal(copy1, doc.getRootObject())) {
            assertEquals(
                _helper.save(copy1, null, "root"),
                _helper.save(doc.getRootObject(), null, "root"));
        }
    }

    @Test
    public void testSaveWithNewElement() throws Exception {
        DataObject openIntf = _helperContext.getDataFactory().create(OpenInterface.class);
        LoggingRootIntf root = (LoggingRootIntf)_helperContext.getDataFactory().create(LoggingRootIntf.class);
        root.setOpenInterface((OpenInterface)openIntf);
        assertEquals(false, openIntf.isSet("a"));

        String xml = _helper.save((DataObject)root, null, "root");
        assertTrue(xml, xml.contains("<openInterface xsi:nil=\"true\">"));
        XMLDocument doc = _helper.load(xml);
        assertEquals(
            xml,
            _helper.save(doc.getRootObject(), doc.getRootElementURI(), doc.getRootElementName()));

        root.getChangeSummary().beginLogging();
        DataObject a = _helperContext.getDataFactory().create(OpenInterface.class);
        openIntf.setDataObject("a", a);
        assertEquals(true, openIntf.isSet("a"));

        String xml2 = _helper.save((DataObject)root, null, "root");
        XMLDocument doc2 = _helper.load(xml2);
        DataObject root2 = doc2.getRootObject();
        assertEquals(
            xml2,
            _helper.save(root2, doc2.getRootElementURI(), doc2.getRootElementName()));
        assertEquals(true, root2.getDataObject("openInterface").isSet("a"));

        ChangeSummary cs = root2.getChangeSummary();
        assertEquals(true, cs.isLogging());
        cs.endLogging();
        cs.undoChanges();
        assertEquals(false, root2.getDataObject("openInterface").isSet("a"));
        assertEquals(
            xml,
            _helper.save(root2, doc2.getRootElementURI(), doc2.getRootElementName()));
    }

    /*
     * Test method for 'com.sap.sdo.impl.xml.XMLHelperImpl.save(DataObject, String, String, OutputStream)'
     */
    @Test
    public void testSaveDataObjectStringStringOutputStream() throws IOException {
        SimpleAttrIntf attr = (SimpleAttrIntf)_helperContext.getDataFactory().create(SimpleAttrIntf.class);
        attr.setName("Stefan");
        attr.setData("a data");
        attr.setGreen(true);
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<ns1:simple" + NS1
            + " xsi:type=\"ns1:SimpleAttrIntf\" name=\"Stefan\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"
            + "  <data>a data</data>\n"
            + "  <green>true</green>\n"
            + "</ns1:simple>\n";
        PipedOutputStream out = new PipedOutputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new PipedInputStream(out)));
        DataObject attrObj = (DataObject)attr;
        _helper.save(attrObj, attrObj.getType().getURI(), "simple", out);
        StringBuffer buffer = new StringBuffer();
        while (reader.ready()) {
            buffer.append(reader.readLine());
            buffer.append('\n');
        }
        assertLineEquality("XMLHelper doesn't produce expected XML", xml, buffer.toString());
        reader.close();
        attr = (SimpleAttrIntf)_helperContext.getDataFactory().create(SimpleAttrIntf.class);
        attr.setName(null);
        attr.setData(null);
        attr.setGreen(true);
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<ns1:simple" + NS1
            + " xsi:type=\"ns1:SimpleAttrIntf\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"
            + "  <data xsi:nil=\"true\"></data>\n"
            + "  <green>true</green>\n"
            + "</ns1:simple>\n";
        out = new PipedOutputStream();
        reader = new BufferedReader(new InputStreamReader(new PipedInputStream(out)));
        attrObj = (DataObject)attr;
        _helper.save(attrObj, attrObj.getType().getURI(), "simple", out);
        buffer = new StringBuffer();
        while (reader.ready()) {
            buffer.append(reader.readLine());
            buffer.append('\n');
        }
        assertLineEquality("XMLHelper doesn't produce expected XML", xml, buffer.toString());
        reader.close();

        SimpleMappedIntf mapped = (SimpleMappedIntf)_helperContext.getDataFactory().create(SimpleMappedIntf.class);
        //((DataObject)mapped).set("xyz", "a data");
        mapped.setData("a data");
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<ns1:simple" + NS1
            + " xsi:type=\"ns1:SimpleMappedIntf\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"
            + "  <xyz>a data</xyz>\n"
            + "</ns1:simple>\n";
        out = new PipedOutputStream();
        reader = new BufferedReader(new InputStreamReader(new PipedInputStream(out)));
        DataObject mappedObj = (DataObject)mapped;
        _helper.save(mappedObj, mappedObj.getType().getURI(), "simple", out);
        buffer = new StringBuffer();
        while (reader.ready()) {
            buffer.append(reader.readLine());
            buffer.append('\n');
        }
        assertLineEquality("XMLHelper doesn't produce expected XML", xml, buffer.toString());
        reader.close();

        SimpleContainedIntf inner =
            (SimpleContainedIntf)_helperContext.getDataFactory().create(SimpleContainedIntf.class);
        inner.setName("name");
        SimpleContainingIntf containing =
            (SimpleContainingIntf)_helperContext.getDataFactory().create(SimpleContainingIntf.class);
        containing.setInner(inner);
        containing.setX("x");
        String preStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<ns1:outer" + NS1
            + " xsi:type=\"ns1:SimpleContainingIntf\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n";
        String xStr = "  <x>x</x>\n";
        String innerStr = "  <inner>\n"
            + "    <name>name</name>\n"
            + "  </inner>\n";
        String postStr = "</ns1:outer>\n";
        out = new PipedOutputStream();
        reader = new BufferedReader(new InputStreamReader(new PipedInputStream(out)));
        DataObject containingObj = (DataObject)containing;
        _helper.save(containingObj, containingObj.getType().getURI(), "outer", out);
        buffer = new StringBuffer();
        while (reader.ready()) {
            buffer.append(reader.readLine());
            buffer.append('\n');
        }
        reader.close();
        String writtenXml = buffer.toString();
        assertLineEquality(
            "XMLHelper doesn't produce expected XML: " + writtenXml,
            preStr, writtenXml.substring(0,preStr.length()));
        assertTrue("XMLHelper doesn't produce expected XML: " + writtenXml, writtenXml.endsWith(postStr));
        assertFalse("XMLHelper doesn't produce expected XML: " + writtenXml, writtenXml.indexOf(innerStr)<0);
        assertFalse("XMLHelper doesn't produce expected XML: " + writtenXml, writtenXml.indexOf(xStr)<0);
        assertEquals(
            "XMLHelper doesn't produce expected XML: " + writtenXml,
            preStr.length()+postStr.length()+innerStr.length()+xStr.length(), writtenXml.length());

        OppositePropsA oppA = (OppositePropsA)_helperContext.getDataFactory().create(OppositePropsA.class);
        OppositePropsB oppB1 = (OppositePropsB)_helperContext.getDataFactory().create(OppositePropsB.class);
        OppositePropsB oppB2 = (OppositePropsB)_helperContext.getDataFactory().create(OppositePropsB.class);
        ((DataObject)oppB1).set("a", oppA);
        ((DataObject)oppB2).set("a", oppA);
        ArrayList<OppositePropsB> opposites = new ArrayList<OppositePropsB>(2);
        opposites.add(oppB1);
        opposites.add(oppB2);
        ((DataObject)oppA).set("bs", opposites);
        assertNotNull(oppA.getBs());
        assertFalse(oppA.getBs().isEmpty());
        DataObject openIntf = _helperContext.getDataFactory().create(OpenInterface.class);
        DataObject pA = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        pA.set("name", "a");
        pA.set("type", ((DataObject)oppA).getType());
        pA.setBoolean("containment", true);
        Property prA = _helperContext.getTypeHelper().defineOpenContentProperty(null, pA);
        assertTrue("generated property is not containment", prA.isContainment());
        openIntf.setDataObject(prA, (DataObject)oppA);
        assertEquals("parent container association incorrect ",
            openIntf,
            ((DataObject)oppA).getContainer());
        DataObject pB = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        pB.set("name", "b");
        pB.set("type", ((DataObject)oppB1).getType());
        pB.setBoolean("containment", true);
        pB.setBoolean("many", true);
        Property prB = _helperContext.getTypeHelper().defineOpenContentProperty(null, pB);
        assertTrue("generated property is not containment", prB.isContainment());
        assertTrue("generated property is not many", prB.isMany());
        List<DataObject> l = openIntf.getList(prB);
        l.add((DataObject)oppB1);
        assertEquals("parent container association incorrect ",
            openIntf,
            ((DataObject)oppB1).getContainer());
        l.add((DataObject)oppB2);
        assertEquals("parent container association incorrect ",
            openIntf,
            ((DataObject)oppB2).getContainer());
        // openIntf.setList(prB, openIntf.getList(prB));
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<ns1:opposite" + NS1
            + " xsi:type=\"ns1:OpenInterface\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"
            + "  <a xsi:type=\"ns1:OppositePropsA\">\n"
            + "    <bs>#/ns1:opposite/b.0</bs>\n"
            + "    <bs>#/ns1:opposite/b.1</bs>\n"
            + "  </a>\n"
            + "  <b xsi:type=\"ns1:OppositePropsB\">\n"
            + "    <a>#/ns1:opposite/a</a>\n"
            + "  </b>\n"
            + "  <b xsi:type=\"ns1:OppositePropsB\">\n"
            + "    <a>#/ns1:opposite/a</a>\n"
            + "  </b>\n"
            + "</ns1:opposite>\n";
        out = new PipedOutputStream();
        reader = new BufferedReader(new InputStreamReader(new PipedInputStream(out)));
        _helper.save(openIntf, openIntf.getType().getURI(), "opposite", out);
        buffer = new StringBuffer();
        while (reader.ready()) {
            buffer.append(reader.readLine());
            buffer.append('\n');
        }
        assertLineEquality("XMLHelper doesn't produce expected XML", xml, buffer.toString());
        reader.close();
    }

    /*
     * Test method for 'com.sap.sdo.impl.xml.XMLHelperImpl.save(XMLDocument, OutputStream, Object)'
     */
    @Test
    public void testSaveXMLDocumentOutputStreamObject() throws IOException {
        SimpleAttrIntf attr = (SimpleAttrIntf)_helperContext.getDataFactory().create(SimpleAttrIntf.class);
        attr.setName("Stefan");
        attr.setData("a data");
        attr.setGreen(true);
        Map<String, Boolean> options = new HashMap<String, Boolean>(1);
        options.put("XSD", Boolean.TRUE);
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<ns1:simple" + NS1
            + " xsi:type=\"ns1:SimpleAttrIntf\" name=\"Stefan\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"
            + "  <data>a data</data>\n"
            + "  <green>true</green>\n"
            + "</ns1:simple>\n";
        PipedOutputStream out = new PipedOutputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new PipedInputStream(out)));
        DataObject attrObj = (DataObject)attr;
        _helper.save(_helper.createDocument(attrObj, attrObj.getType().getURI(), "simple"), out, options);
        StringBuffer buffer = new StringBuffer();
        while (reader.ready()) {
            buffer.append(reader.readLine());
            buffer.append('\n');
        }
        assertLineEquality("XMLHelper doesn't produce expected XML", xml, buffer.toString());
        reader.close();

        SimpleMappedIntf mapped = (SimpleMappedIntf)_helperContext.getDataFactory().create(SimpleMappedIntf.class);
        //((DataObject)mapped).set("xyz", "a data");
        mapped.setData("a data");
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<ns1:simple" + NS1
            + " xsi:type=\"ns1:SimpleMappedIntf\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"
            + "  <xyz>a data</xyz>\n"
            + "</ns1:simple>\n";
        out = new PipedOutputStream();
        reader = new BufferedReader(new InputStreamReader(new PipedInputStream(out)));
        DataObject mappedObj = (DataObject)mapped;
        _helper.save(_helper.createDocument(mappedObj, mappedObj.getType().getURI(), "simple"), out, options);
        buffer = new StringBuffer();
        while (reader.ready()) {
            buffer.append(reader.readLine());
            buffer.append('\n');
        }
        assertLineEquality("XMLHelper doesn't produce expected XML", xml, buffer.toString());
        reader.close();

        SimpleContainedIntf inner =
            (SimpleContainedIntf)_helperContext.getDataFactory().create(SimpleContainedIntf.class);
        inner.setName("name");
        SimpleContainingIntf containing =
            (SimpleContainingIntf)_helperContext.getDataFactory().create(SimpleContainingIntf.class);
        containing.setInner(inner);
        containing.setX("x");
        String preStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<ns1:outer" + NS1
            + " xsi:type=\"ns1:SimpleContainingIntf\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n";
        String innerStr = "  <inner>\n"
            + "    <name>name</name>\n"
            + "  </inner>\n";
        String xStr = "  <x>x</x>\n";
        String postStr = "</ns1:outer>\n";
        out = new PipedOutputStream();
        reader = new BufferedReader(new InputStreamReader(new PipedInputStream(out)));
        DataObject containingObj = (DataObject)containing;
        _helper.save(_helper.createDocument(containingObj, containingObj.getType().getURI(), "outer"), out, options);
        buffer = new StringBuffer();
        while (reader.ready()) {
            buffer.append(reader.readLine());
            buffer.append('\n');
        }
        reader.close();
        String writtenXml = buffer.toString();
        assertLineEquality(
            "XMLHelper doesn't produce expected XML: " + writtenXml,
            preStr, writtenXml.substring(0, preStr.length()));
        assertTrue("XMLHelper doesn't produce expected XML: " + writtenXml, writtenXml.endsWith(postStr));
        assertFalse("XMLHelper doesn't produce expected XML: " + writtenXml, writtenXml.indexOf(innerStr)<0);
        assertFalse("XMLHelper doesn't produce expected XML: " + writtenXml, writtenXml.indexOf(xStr)<0);
        assertEquals(
            "XMLHelper doesn't produce expected XML: " + writtenXml,
            preStr.length()+postStr.length()+innerStr.length()+xStr.length(), writtenXml.length());
    }

    /*
     * Test method for 'com.sap.sdo.impl.xml.XMLHelperImpl.save(XMLDocument, Writer, Object)'
     */
    @Test
    public void testSaveXMLDocumentWriterObject() throws IOException {
        SimpleAttrIntf attr = (SimpleAttrIntf)_helperContext.getDataFactory().create(SimpleAttrIntf.class);
        attr.setName("Stefan");
        attr.setData("a data");
        attr.setGreen(true);
        Map<String, Boolean> options = new HashMap<String, Boolean>(1);
        options.put("XSD", Boolean.TRUE);
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<ns1:simple" + NS1
            + " xsi:type=\"ns1:SimpleAttrIntf\" name=\"Stefan\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"
            + "  <data>a data</data>\n"
            + "  <green>true</green>\n"
            + "</ns1:simple>\n";
        StringWriter writer = new StringWriter();
        DataObject attrObj = (DataObject)attr;
        _helper.save(_helper.createDocument(attrObj, attrObj.getType().getURI(), "simple"), writer, options);
        assertLineEquality("XMLHelper doesn't produce expected XML",
            xml,
            writer.toString());
        writer.close();

        SimpleMappedIntf mapped = (SimpleMappedIntf)_helperContext.getDataFactory().create(SimpleMappedIntf.class);
        //((DataObject)mapped).set("xyz", "a data");
        mapped.setData("a data");
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<ns1:simple" + NS1
            + " xsi:type=\"ns1:SimpleMappedIntf\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"
            + "  <xyz>a data</xyz>\n"
            + "</ns1:simple>\n";
        writer = new StringWriter();
        DataObject mappedObj = (DataObject)mapped;
        _helper.save(_helper.createDocument(mappedObj, mappedObj.getType().getURI(), "simple"), writer, options);
        assertLineEquality("XMLHelper doesn't produce expected XML",
            xml,
            writer.toString());
        writer.close();

        SimpleContainedIntf inner =
            (SimpleContainedIntf)_helperContext.getDataFactory().create(SimpleContainedIntf.class);
        inner.setName("name");
        SimpleContainingIntf containing =
            (SimpleContainingIntf)_helperContext.getDataFactory().create(SimpleContainingIntf.class);
        containing.setInner(inner);
        containing.setX("x");
        String preStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<ns1:outer" + NS1
            + " xsi:type=\"ns1:SimpleContainingIntf\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n";
        String innerStr = "  <inner>\n"
            + "    <name>name</name>\n"
            + "  </inner>\n";
        String xStr = "  <x>x</x>\n";
        String postStr = "</ns1:outer>\n";
        writer = new StringWriter();
        DataObject containingObj = (DataObject)containing;
        _helper.save(_helper.createDocument(containingObj, containingObj.getType().getURI(), "outer"), writer, options);
        String writtenXml = writer.toString();
        assertLineEquality(
            "XMLHelper doesn't produce expected XML: " + writtenXml,
            preStr, writtenXml.substring(0, preStr.length()));
        assertTrue("XMLHelper doesn't produce expected XML: " + writtenXml, writtenXml.endsWith(postStr));
        assertFalse("XMLHelper doesn't produce expected XML: " + writtenXml, writtenXml.indexOf(innerStr)<0);
        assertFalse("XMLHelper doesn't produce expected XML: " + writtenXml, writtenXml.indexOf(xStr)<0);
        assertEquals(
            "XMLHelper doesn't produce expected XML: " + writtenXml,
            preStr.length()+postStr.length()+innerStr.length()+xStr.length(), writtenXml.length());
        writer.close();
    }

    @Test
    public void testStreamResult() throws Exception {
        SimpleAttrIntf attr = (SimpleAttrIntf)_helperContext.getDataFactory().create(SimpleAttrIntf.class);
        attr.setName("Stefan");
        attr.setData("a data");
        attr.setGreen(true);
        Map<String, Boolean> options = new HashMap<String, Boolean>(1);
        options.put("XSD", Boolean.TRUE);
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<ns1:simple" + NS1
                + " xsi:type=\"ns1:SimpleAttrIntf\" name=\"Stefan\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"
            + "  <data>a data</data>\n"
            + "  <green>true</green>\n"
            + "</ns1:simple>\n";
        StreamResult result = new StreamResult(new StringWriter());
        DataObject attrObj = (DataObject)attr;
        _helper.save(_helper.createDocument(attrObj, attrObj.getType().getURI(), "simple"), result, options);
        assertLineEquality("XMLHelper doesn't produce expected XML",
            xml,
            result.getWriter().toString());
        result.getWriter().close();

        SimpleMappedIntf mapped = (SimpleMappedIntf)_helperContext.getDataFactory().create(SimpleMappedIntf.class);
        //((DataObject)mapped).set("xyz", "a data");
        mapped.setData("a data");
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<ns1:simple" + NS1
            + " xsi:type=\"ns1:SimpleMappedIntf\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"
            + "  <xyz>a data</xyz>\n"
            + "</ns1:simple>\n";
        result = new StreamResult(new ByteArrayOutputStream());
        DataObject mappedObj = (DataObject)mapped;
        _helper.save(_helper.createDocument(mappedObj, mappedObj.getType().getURI(), "simple"), result, options);
        assertLineEquality("XMLHelper doesn't produce expected XML",
            xml,
            ((ByteArrayOutputStream)result.getOutputStream()).toString("UTF-8"));

        SimpleContainedIntf inner =
            (SimpleContainedIntf)_helperContext.getDataFactory().create(SimpleContainedIntf.class);
        inner.setName("name");
        SimpleContainingIntf containing =
            (SimpleContainingIntf)_helperContext.getDataFactory().create(SimpleContainingIntf.class);
        containing.setInner(inner);
        containing.setX("x");
        String preStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<ns1:outer" + NS1
            + " xsi:type=\"ns1:SimpleContainingIntf\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n";
        String innerStr = "  <inner>\n"
            + "    <name>name</name>\n"
            + "  </inner>\n";
        String xStr = "  <x>x</x>\n";
        String postStr = "</ns1:outer>\n";
        result = new StreamResult(new StringWriter());
        DataObject containingObj = (DataObject)containing;
        _helper.save(_helper.createDocument(containingObj, containingObj.getType().getURI(), "outer"), result, options);
        String writtenXml = result.getWriter().toString();
        assertLineEquality(
            "XMLHelper doesn't produce expected XML: " + writtenXml,
            preStr, writtenXml.substring(0, preStr.length()));
        assertTrue("XMLHelper doesn't produce expected XML: " + writtenXml, writtenXml.endsWith(postStr));
        assertFalse("XMLHelper doesn't produce expected XML: " + writtenXml, writtenXml.indexOf(innerStr)<0);
        assertFalse("XMLHelper doesn't produce expected XML: " + writtenXml, writtenXml.indexOf(xStr)<0);
        assertEquals(
            "XMLHelper doesn't produce expected XML: " + writtenXml,
            preStr.length()+postStr.length()+innerStr.length()+xStr.length(), writtenXml.length());
        result.getWriter().close();
    }

    public static final XMLOutputFactory XML_OUTPUT_FACTORY = XMLOutputFactory.newInstance();

    @Test
    public void testStAXResult() throws Exception {
        SimpleAttrIntf attr = (SimpleAttrIntf)_helperContext.getDataFactory().create(SimpleAttrIntf.class);
        attr.setName("Stefan");
        attr.setData("a data");
        attr.setGreen(true);
        Map<String, Boolean> options = new HashMap<String, Boolean>(1);
        options.put("XSD", Boolean.TRUE);
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<ns1:simple" + NS1
                + " xsi:type=\"ns1:SimpleAttrIntf\" name=\"Stefan\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"
            + "  <data>a data</data>\n"
            + "  <green>true</green>\n"
            + "</ns1:simple>\n";
        Writer writer = new StringWriter();
        StAXResult result = new StAXResult(XML_OUTPUT_FACTORY.createXMLStreamWriter(writer));
        DataObject attrObj = (DataObject)attr;
        _helper.save(_helper.createDocument(attrObj, attrObj.getType().getURI(), "simple"), result, options);
        assertLineEquality("XMLHelper doesn't produce expected XML",
            xml,
            writer.toString());
        writer.close();

        SimpleMappedIntf mapped = (SimpleMappedIntf)_helperContext.getDataFactory().create(SimpleMappedIntf.class);
        //((DataObject)mapped).set("xyz", "a data");
        mapped.setData("a data");
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<ns1:simple" + NS1
            + " xsi:type=\"ns1:SimpleMappedIntf\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"
            + "  <xyz>a data</xyz>\n"
            + "</ns1:simple>\n";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        result = new StAXResult(XML_OUTPUT_FACTORY.createXMLStreamWriter(outputStream, "UTF-8"));
        DataObject mappedObj = (DataObject)mapped;
        _helper.save(_helper.createDocument(mappedObj, mappedObj.getType().getURI(), "simple"), result, options);
        assertLineEquality("XMLHelper doesn't produce expected XML",
            xml,
            outputStream.toString("UTF-8"));

        SimpleContainedIntf inner =
            (SimpleContainedIntf)_helperContext.getDataFactory().create(SimpleContainedIntf.class);
        inner.setName("name");
        SimpleContainingIntf containing =
            (SimpleContainingIntf)_helperContext.getDataFactory().create(SimpleContainingIntf.class);
        containing.setInner(inner);
        containing.setX("x");
        String preStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<ns1:outer" + NS1
            + " xsi:type=\"ns1:SimpleContainingIntf\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n";
        String innerStr = "  <inner>\n"
            + "    <name>name</name>\n"
            + "  </inner>\n";
        String xStr = "  <x>x</x>\n";
        String postStr = "</ns1:outer>\n";
        writer = new StringWriter();
        result = new StAXResult(XML_OUTPUT_FACTORY.createXMLStreamWriter(writer));
        DataObject containingObj = (DataObject)containing;
        _helper.save(_helper.createDocument(containingObj, containingObj.getType().getURI(), "outer"), result, options);
        String writtenXml = writer.toString();
        assertLineEquality(
            "XMLHelper doesn't produce expected XML: " + writtenXml,
            preStr, writtenXml.substring(0, preStr.length()));
        assertTrue("XMLHelper doesn't produce expected XML: " + writtenXml, writtenXml.endsWith(postStr));
        assertFalse("XMLHelper doesn't produce expected XML: " + writtenXml, writtenXml.indexOf(innerStr)<0);
        assertFalse("XMLHelper doesn't produce expected XML: " + writtenXml, writtenXml.indexOf(xStr)<0);
        assertEquals(
            "XMLHelper doesn't produce expected XML: " + writtenXml,
            preStr.length()+postStr.length()+innerStr.length()+xStr.length(), writtenXml.length());
        writer.close();
    }

    @Test
    public void testSAXResult() throws Exception {
        SimpleAttrIntf attr = (SimpleAttrIntf)_helperContext.getDataFactory().create(SimpleAttrIntf.class);
        attr.setName("Stefan");
        attr.setData("a data");
        attr.setGreen(true);
        Map<String, Boolean> options = new HashMap<String, Boolean>(1);
        options.put("XSD", Boolean.TRUE);
        SAXResult result = new SAXResult(((SapXmlHelper)_helper).createContentHandler(null));
        _helper.save(_helper.createDocument((DataObject)attr, "com.sap.sdo.testcase.typefac", "simple"), result, options);
        assertNotNull(result.getHandler());
        assertTrue(result.getHandler() instanceof SDOContentHandler);
        SDOContentHandler handler = (SDOContentHandler)result.getHandler();
        DataObject root = handler.getDocument().getRootObject();
        assertNotNull(root);
        assertEquals("simple", handler.getDocument().getRootElementName());
        assertEquals("com.sap.sdo.testcase.typefac", handler.getDocument().getRootElementURI());
        assertEquals("1.0", handler.getDocument().getXMLVersion());
        assertEquals("SimpleAttrIntf", root.getType().getName());
        assertEquals("Stefan", root.getString("name"));
        assertFalse(((SdoProperty)root.getProperty("name")).isXmlElement());
        assertFalse(root.getProperty("name").isMany());
        assertFalse(root.getProperty("name").isContainment());
        assertEquals("a data", root.getString("data"));
        assertTrue(((SdoProperty)root.getProperty("data")).isXmlElement());
        assertFalse(root.getProperty("data").isMany());
        assertFalse(root.getProperty("data").isContainment());
        assertTrue(root.getBoolean("green"));
        assertTrue(((SdoProperty)root.getProperty("green")).isXmlElement());
        assertFalse(root.getProperty("green").isMany());
        assertFalse(root.getProperty("green").isContainment());

        SimpleMappedIntf mapped = (SimpleMappedIntf)_helperContext.getDataFactory().create(SimpleMappedIntf.class);
        //((DataObject)mapped).set("xyz", "a data");
        mapped.setData("a data");
        result = new SAXResult(((SapXmlHelper)_helper).createContentHandler(null));
        _helper.save(_helper.createDocument((DataObject)mapped, "com.sap.sdo.testcase.typefac", "simple"), result, options);
        assertNotNull(result.getHandler());
        assertTrue(result.getHandler() instanceof SDOContentHandler);
        handler = (SDOContentHandler)result.getHandler();
        root = handler.getDocument().getRootObject();
        assertNotNull(root);
        assertEquals("simple", handler.getDocument().getRootElementName());
        assertEquals("com.sap.sdo.testcase.typefac", handler.getDocument().getRootElementURI());
        assertEquals("1.0", handler.getDocument().getXMLVersion());
        assertEquals("SimpleMappedIntf", root.getType().getName());
        assertEquals("a data", root.getString("xyz"));
        assertTrue(((SdoProperty)root.getProperty("xyz")).isXmlElement());
        assertFalse(root.getProperty("xyz").isMany());
        assertFalse(root.getProperty("xyz").isContainment());

        SimpleContainedIntf inner =
            (SimpleContainedIntf)_helperContext.getDataFactory().create(SimpleContainedIntf.class);
        inner.setName("name");
        SimpleContainingIntf containing =
            (SimpleContainingIntf)_helperContext.getDataFactory().create(SimpleContainingIntf.class);
        containing.setInner(inner);
        containing.setX("x");
        result = new SAXResult(((SapXmlHelper)_helper).createContentHandler(null));
        _helper.save(_helper.createDocument((DataObject)containing, "com.sap.sdo.testcase.typefac", "outer"), result, options);
        assertNotNull(result.getHandler());
        assertTrue(result.getHandler() instanceof SDOContentHandler);
        handler = (SDOContentHandler)result.getHandler();
        root = handler.getDocument().getRootObject();
        assertNotNull(root);
        assertTrue(_helperContext.getEqualityHelper().equal((DataObject)containing, root));
        assertEquals("outer", handler.getDocument().getRootElementName());
        assertEquals("com.sap.sdo.testcase.typefac", handler.getDocument().getRootElementURI());
        assertEquals("1.0", handler.getDocument().getXMLVersion());
    }

    @Test
    public void testSDOResult() throws Exception {
        SimpleAttrIntf attr = (SimpleAttrIntf)_helperContext.getDataFactory().create(SimpleAttrIntf.class);
        attr.setName("Stefan");
        attr.setData("a data");
        attr.setGreen(true);
        Map<String, Boolean> options = new HashMap<String, Boolean>(1);
        options.put("XSD", Boolean.TRUE);
        SDOResult result = ((SapXmlHelper)_helper).createSDOResult(null);
        _helper.save(_helper.createDocument((DataObject)attr, "com.sap.sdo.testcase.typefac", "simple"), result, options);
        DataObject root = result.getDocument().getRootObject();
        assertNotNull(root);
        assertEquals("simple", result.getDocument().getRootElementName());
        assertEquals("com.sap.sdo.testcase.typefac", result.getDocument().getRootElementURI());
        assertEquals("1.0", result.getDocument().getXMLVersion());
        assertEquals("SimpleAttrIntf", root.getType().getName());
        assertEquals("Stefan", root.getString("name"));
        assertFalse(((SdoProperty)root.getProperty("name")).isXmlElement());
        assertFalse(root.getProperty("name").isMany());
        assertFalse(root.getProperty("name").isContainment());
        assertEquals("a data", root.getString("data"));
        assertTrue(((SdoProperty)root.getProperty("data")).isXmlElement());
        assertFalse(root.getProperty("data").isMany());
        assertFalse(root.getProperty("data").isContainment());
        assertTrue(root.getBoolean("green"));
        assertTrue(((SdoProperty)root.getProperty("green")).isXmlElement());
        assertFalse(root.getProperty("green").isMany());
        assertFalse(root.getProperty("green").isContainment());

        SimpleMappedIntf mapped = (SimpleMappedIntf)_helperContext.getDataFactory().create(SimpleMappedIntf.class);
        //((DataObject)mapped).set("xyz", "a data");
        mapped.setData("a data");
        result = ((SapXmlHelper)_helper).createSDOResult(null);
        _helper.save(_helper.createDocument((DataObject)mapped, "com.sap.sdo.testcase.typefac", "simple"), result, options);
        root = result.getDocument().getRootObject();
        assertNotNull(root);
        assertEquals("simple", result.getDocument().getRootElementName());
        assertEquals("com.sap.sdo.testcase.typefac", result.getDocument().getRootElementURI());
        assertEquals("1.0", result.getDocument().getXMLVersion());
        assertEquals("SimpleMappedIntf", root.getType().getName());
        assertEquals("a data", root.getString("xyz"));
        assertTrue(((SdoProperty)root.getProperty("xyz")).isXmlElement());
        assertFalse(root.getProperty("xyz").isMany());
        assertFalse(root.getProperty("xyz").isContainment());

        SimpleContainedIntf inner =
            (SimpleContainedIntf)_helperContext.getDataFactory().create(SimpleContainedIntf.class);
        inner.setName("name");
        SimpleContainingIntf containing =
            (SimpleContainingIntf)_helperContext.getDataFactory().create(SimpleContainingIntf.class);
        containing.setInner(inner);
        containing.setX("x");
        result = ((SapXmlHelper)_helper).createSDOResult(null);
        _helper.save(_helper.createDocument((DataObject)containing, "com.sap.sdo.testcase.typefac", "outer"), result, options);
        root = result.getDocument().getRootObject();
        assertNotNull(root);
        assertTrue(_helperContext.getEqualityHelper().equal((DataObject)containing, root));
        assertEquals("outer", result.getDocument().getRootElementName());
        assertEquals("com.sap.sdo.testcase.typefac", result.getDocument().getRootElementURI());
        assertEquals("1.0", result.getDocument().getXMLVersion());
    }

    @Test
    public void testXMLHelperExample() {
        TypeHelper types = _helperContext.getTypeHelper();
        Type intType = types.getType("commonj.sdo", "Int");
        Type stringType = types.getType("commonj.sdo", "String");
        // create a new Type for Customers
        DataObject customerType = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        customerType.set("uri", "http://example.com/customer");
        customerType.set("name", "Customer");
        // create a customer number property
        DataObject custNumProperty = customerType.createDataObject("property");
        custNumProperty.set("name", "custNum");
        custNumProperty.set("type", intType);
        // create a first name property
        DataObject firstNameProperty = customerType.createDataObject("property");
        firstNameProperty.set("name", "firstName");
        firstNameProperty.set("type", stringType);
        // create a last name property
        DataObject lastNameProperty = customerType.createDataObject("property");
        lastNameProperty.set("name", "lastName");
        lastNameProperty.set("type", stringType);
        // now define the Customer type so that customers can be made
        types.define(customerType);

        DataFactory factory = _helperContext.getDataFactory();
        DataObject customer1 = factory.create("http://example.com/customer",
        "Customer");
        customer1.setInt("custNum", 1);
        customer1.set("firstName", "John");
        customer1.set("lastName", "Adams");
        DataObject customer2 = factory.create("http://example.com/customer",
        "Customer");
        customer2.setInt("custNum", 2);
        customer2.set("firstName", "Jeremy");
        customer2.set("lastName", "Pavick");

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<ns1:customer xmlns:ns1=\"http://example.com/customer\""
            + " xsi:type=\"ns1:Customer\" custNum=\"1\" firstName=\"John\" lastName=\"Adams\""
            + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"></ns1:customer>\n";
        assertLineEquality("XMLHelper doesn't produce expected XML",
            xml,
            _helper.save(customer1, "http://example.com/customer", "customer"));
    }

    @Test
    public void testIdRef() throws IOException {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<ns1:outer" + NS1
            + " xsi:type=\"ns1:SimpleIdIntf\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"
            + "  <data>42</data>\n"
            + "  <id>42</id>\n"
            + "  <values>first line</values>\n"
            + "  <values>second line</values>\n"
            + "  <values>third line</values>\n"
            + "</ns1:outer>\n";

        SimpleIdIntf idIntf = (SimpleIdIntf)_helperContext.getDataFactory().create(SimpleIdIntf.class);
        idIntf.setId("42");
        idIntf.setData(idIntf);
        List<String> values = idIntf.getValues();
        values.add("first line");
        values.add("second line");
        values.add("third line");

        Map<String, Boolean> options = new HashMap<String, Boolean>(1);

        StringWriter writer = new StringWriter();
        DataObject idIntfObj = (DataObject)idIntf;
        _helper.save(_helper.createDocument(idIntfObj, idIntfObj.getType().getURI(), "outer"), writer, options);
        assertLineEquality("XMLHelper doesn't produce expected XML", xml, writer.toString());
    }

    @Test
    public void testSequencedWithTypeAndObject() throws IOException {
        SequencedOppositeIntf outer = (SequencedOppositeIntf)_helperContext.getDataFactory().create(SequencedOppositeIntf.class);
        SequencedOppositeIntf seqIntf = (SequencedOppositeIntf)_helperContext.getDataFactory().create(SequencedOppositeIntf.class);
        seqIntf.setSv(outer);
        seqIntf.setName("name");

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<ns1:outer" + NS1
            + " xsi:type=\"ns1:SequencedOppositeIntf\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"
            + "  <mv>\n"
            + "    <name>name</name>\n"
            + "  </mv>\n"
            + "</ns1:outer>\n";

        StringWriter writer = new StringWriter();
        _helper.save(_helper.createDocument((DataObject)outer, "com.sap.sdo.testcase.typefac", "outer"), writer, null);
        assertLineEquality("XMLHelper doesn't produce expected XML", xml, writer.toString());
    }

    @Test
    public void testCreatingXmlFromDataObjects() {
        DataObject purchaseOrder =
            _helperContext.getDataFactory().create(noNamespace.PurchaseOrderType.class);

        purchaseOrder.setString("orderDate", "1999-10-20");

        DataObject shipTo = purchaseOrder.createDataObject("shipTo");
        shipTo.set("country", "US");
        shipTo.set("name", "Alice Smith");
        shipTo.set("street", "123 Maple Street");
        shipTo.set("city", "Mill Valley");
        shipTo.set("state", "CA");
        shipTo.setString("zip", "90952");

        DataObject billTo = purchaseOrder.createDataObject("billTo");
        billTo.set("country", "US");
        billTo.set("name", "Robert Smith");
        billTo.set("street", "8 Oak Avenue");
        billTo.set("city", "Mill Valley");
        shipTo.set("state", "PA");
        billTo.setString("zip", "95819");
        purchaseOrder.set("comment", "Hurry, my lawn is going wild!");
        DataObject items = purchaseOrder.createDataObject("items");

        DataObject item1 = items.createDataObject("item");
        item1.set("partNum", "872-AA");
        item1.set("productName", "Lawnmower");
        item1.setInt("quantity", 1);
        item1.setString("uSPrice", "148.95");
        item1.set("comment", "Confirm this is electric");

        DataObject item2 = items.createDataObject("item");
        item2.set("partNum", "926-AA");
        item2.set("productName", "Baby Monitor");
        item1.setInt("quantity", 1);
        item2.setString("uSPrice", "39.98");
        item2.setString("shipDate", "1999-05-21");

        System.out.println(_helperContext.getXMLHelper().save(purchaseOrder, null,
            "purchaseOrder"));
    }
    @Test
    public void testSaveInheritence() throws Exception {
        IPolymorphicProperty container = (IPolymorphicProperty)_helperContext.getDataFactory().create(IPolymorphicProperty.class);
        SimpleExtension sub = (SimpleExtension)_helperContext.getDataFactory().create(SimpleExtension.class);
        container.setA(sub);
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<ns1:container" + NS1
            + " xsi:type=\"ns1:IPolymorphicProperty\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"
            +"  <a xsi:type=\"ns1:SimpleExtension\" xsi:nil=\"true\"></a>\n"
        +"</ns1:container>\n";
        DataObject containerObj = (DataObject)container;
        assertLineEquality(xml,_helperContext.getXMLHelper().save(containerObj, containerObj.getType().getURI(), "container"));
    }

    @Test
    public void testSetDataObjectToNull() throws IOException {
        SimpleContainingIntf simpleContainingIntf = (SimpleContainingIntf)_helperContext.getDataFactory().create(SimpleContainingIntf.class);
        simpleContainingIntf.setInner(null);
        DataObject simpleContainingObj = (DataObject)simpleContainingIntf;
        String xml = _helperContext.getXMLHelper().save(simpleContainingObj, simpleContainingObj.getType().getURI(), "root");
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<ns1:root" + NS1
            + " xsi:type=\"ns1:SimpleContainingIntf\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"
            + "  <inner xsi:nil=\"true\"></inner>\n"
            + "</ns1:root>\n";
        assertLineEquality(expected,xml);
    }

    @Test
    public void testDOMResult() throws Exception {
        SimpleAttrIntf attr = (SimpleAttrIntf)_helperContext.getDataFactory().create(SimpleAttrIntf.class);
        attr.setName("Stefan");
        attr.setData("a data");
        attr.setGreen(true);
        Map<String, Boolean> options = new HashMap<String, Boolean>(1);
        options.put("XSD", Boolean.TRUE);
        DOMResult result = new DOMResult();
        DataObject attrObj = (DataObject)attr;
        _helper.save(_helper.createDocument(attrObj, attrObj.getType().getURI(), "simple"), result, options);
        DOMSource source = new DOMSource(result.getNode(), result.getSystemId());
        XMLDocument doc = _helper.load(source, null, options);
        DataObject root = doc.getRootObject();
        assertNotNull(root);
        assertEquals("simple", doc.getRootElementName());
        assertEquals("com.sap.sdo.testcase.typefac", doc.getRootElementURI());
        assertEquals("1.0", doc.getXMLVersion());
        assertEquals("SimpleAttrIntf", root.getType().getName());
        assertEquals("Stefan", root.getString("name"));
        assertFalse(((SdoProperty)root.getProperty("name")).isXmlElement());
        assertFalse(root.getProperty("name").isMany());
        assertFalse(root.getProperty("name").isContainment());
        assertEquals("a data", root.getString("data"));
        assertTrue(((SdoProperty)root.getProperty("data")).isXmlElement());
        assertFalse(root.getProperty("data").isMany());
        assertFalse(root.getProperty("data").isContainment());
        assertTrue(root.getBoolean("green"));
        assertTrue(((SdoProperty)root.getProperty("green")).isXmlElement());
        assertFalse(root.getProperty("green").isMany());
        assertFalse(root.getProperty("green").isContainment());

        SimpleMappedIntf mapped = (SimpleMappedIntf)_helperContext.getDataFactory().create(SimpleMappedIntf.class);
        //((DataObject)mapped).set("xyz", "a data");
        mapped.setData("a data");
        result = new DOMResult();
        DataObject mappedObj = (DataObject)mapped;
        _helper.save(_helper.createDocument(mappedObj, mappedObj.getType().getURI(), "simple"), result, options);
        source = new DOMSource(result.getNode(), result.getSystemId());
        doc = _helper.load(source, null, options);
        root = doc.getRootObject();
        assertNotNull(root);
        assertEquals("simple", doc.getRootElementName());
        assertEquals("com.sap.sdo.testcase.typefac", doc.getRootElementURI());
        assertEquals("1.0", doc.getXMLVersion());
        assertEquals("SimpleMappedIntf", root.getType().getName());
        assertEquals("a data", root.getString("xyz"));
        assertTrue(((SdoProperty)root.getProperty("xyz")).isXmlElement());
        assertFalse(root.getProperty("xyz").isMany());
        assertFalse(root.getProperty("xyz").isContainment());

        SimpleContainedIntf inner =
            (SimpleContainedIntf)_helperContext.getDataFactory().create(SimpleContainedIntf.class);
        inner.setName("name");
        SimpleContainingIntf containing =
            (SimpleContainingIntf)_helperContext.getDataFactory().create(SimpleContainingIntf.class);
        containing.setInner(inner);
        containing.setX("x");
        result = new DOMResult();
        DataObject containingObj = (DataObject)containing;
        _helper.save(_helper.createDocument(containingObj, containingObj.getType().getURI(), "outer"), result, options);
        source = new DOMSource(result.getNode(), result.getSystemId());
        doc = _helper.load(source, null, options);
        root = doc.getRootObject();
        assertNotNull(root);
        assertTrue(_helperContext.getEqualityHelper().equal(containingObj, root));
        assertEquals("outer", doc.getRootElementName());
        assertEquals("com.sap.sdo.testcase.typefac", doc.getRootElementURI());
        assertEquals("1.0", doc.getXMLVersion());
    }

    @Test
    public void testEscaping() throws IOException {
        String s = "äöüÄÖÜß&'\"<>" + (char)265;
        DataObject data = _helperContext.getDataFactory().create(OpenInterface.class);
        data.set("prop", s);
        assertEquals(s, data.get("prop"));

        DataObject propObj = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        propObj.setString("name", "attribute");
        propObj.set("type", _helperContext.getTypeHelper().getType(String.class));
        propObj.setBoolean(
            _helperContext.getTypeHelper().getOpenContentProperty("commonj.sdo/xml", "xmlElement"),
            false);

        Property prop = _helperContext.getTypeHelper().defineOpenContentProperty(null, propObj);
        data.set(prop, s);
        assertEquals(s, data.get(prop));

        String xml = _helperContext.getXMLHelper().save(data, null, "data");
        assertNotNull(xml);

        byte[] bytes = xml.getBytes();

        for (byte b : bytes) {
            System.out.print(Integer.toHexString(b & 0xFF) + ' ');
        }
        System.out.println();

        System.out.println(xml);
        XMLDocument doc = _helperContext.getXMLHelper().load(xml);
        assertNotNull(doc);
        StringWriter xml2 = new StringWriter();
        _helperContext.getXMLHelper().save(doc, xml2, null);
        assertEquals(xml, xml2.toString());
        assertEquals(s, doc.getRootObject().getString("prop"));

        assertEquals(prop, doc.getRootObject().getInstanceProperty("attribute"));
        assertEquals(s, doc.getRootObject().getString(prop));
    }

    @Test
    public void testPrefixMap() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<ns1:data" + NS1
            + " xsi:type=\"ns1:OpenInterface\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:test1=\"test1\" xmlns:test=\"test\""
            + " xsi:schemaLocation=\"uri location\" xsi:noNamespaceSchemaLocation=\"nonamespace\""
            + ">\n"
            + "  <prop xsi:type=\"xsd:string\">abc</prop>\n"
            + "</ns1:data>\n";
        DataObject data = _helperContext.getDataFactory().create(OpenInterface.class);
        data.set("prop", "abc");
        Map<String, String> prefixMap = new HashMap<String, String>();
        prefixMap.put("test", "test");
        prefixMap.put("test1", "test1");
        Map<String, Map> map = new HashMap<String, Map>();
        map.put(SapXmlHelper.OPTION_KEY_PREFIX_MAP, prefixMap);
        StringWriter writer = new StringWriter();
        XMLDocument doc = _helperContext.getXMLHelper().createDocument(data, data.getType().getURI(), "data");
        doc.setSchemaLocation("uri location");
        doc.setNoNamespaceSchemaLocation("nonamespace");
        _helperContext.getXMLHelper().save(doc, writer, map);
        assertLineEquality(xml, writer.toString());
    }

    @Test
    public void testSequencedWithSchemaProp() {
        Type open = OpenType.getInstance();
        _helperContext.getTypeHelper().getType(open.getURI(), open.getName());
        String start = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<ns1:data" + NS1
            + " xsi:type=\"ns1:OpenSequencedInterface\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"
            + "  <test xsi:type=\"xsd:string\">test</test>\n"
            + "  <schema xmlns:sdo=\"commonj.sdo\" xsi:type=\"sdo:XSDType\""
            + " xmlns:sdoj=\"commonj.sdo/java\""
            + " xmlns:sdox=\"commonj.sdo/xml\""
            + ">\n"
            + "    <xsd:schema targetNamespace=\"com.sap.sdo.testcase.typefac\""
            + " sdoj:package=\"com.sap.sdo.testcase.typefac\""
            + ">\n";
        String end = "      </xsd:complexType>\n"
            + "    </xsd:schema>\n"
            + "  </schema>\n"
            + "</ns1:data>\n";
        DataObject prop = _helperContext.getDataFactory().create(PropertyType.getInstance());
        prop.set(PropertyType.NAME, "schema");
        prop.set(PropertyType.TYPE, XsdType.getInstance());
        prop.set(PropertyType.MANY, true);
        prop.set(PropertyType.CONTAINMENT, true);
        Property property = _helperContext.getTypeHelper().defineOpenContentProperty(null, prop);

        DataObject data = _helperContext.getDataFactory().create(OpenSequencedInterface.class);
        data.set("test", "test");
        DataObject xsdObject = data.createDataObject(property);
        Property schemaProp = _helperContext.getTypeHelper().getOpenContentProperty(
            URINamePair.PROP_SCHEMA_SCHEMA.getURI(), URINamePair.PROP_SCHEMA_SCHEMA.getName());
        Schema schema = ((SapXsdHelper)_helperContext.getXSDHelper()).generateSchema("com.sap.sdo.testcase.typefac", null, null);
        xsdObject.getList(schemaProp).add(schema);
        String xml = _helperContext.getXMLHelper().save(data, data.getType().getURI(), "data");
        System.out.println(xml);
        assertLineEquality(start, xml.substring(0, start.length()));
        assertTrue(xml, xml.endsWith(end));
    }

    @Test
    public void testNonSequencedWithSchemaProp() {
        String start = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<ns1:data" + NS1
            + " xsi:type=\"ns1:OpenInterface\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"
            + "  <test xsi:type=\"xsd:string\">test</test>\n"
            + "  <schema xmlns:sdo=\"commonj.sdo\" xsi:type=\"sdo:XSDType\""
            + " xmlns:sdoj=\"commonj.sdo/java\""
            + " xmlns:sdox=\"commonj.sdo/xml\""
            + ">\n"
            + "    <xsd:schema targetNamespace=\"com.sap.sdo.testcase.typefac\""
            + " sdoj:package=\"com.sap.sdo.testcase.typefac\""
            + ">\n";
        String end = "      </xsd:complexType>\n"
            + "    </xsd:schema>\n"
            + "  </schema>\n"
            + "</ns1:data>\n";
        DataObject prop = _helperContext.getDataFactory().create(URINamePair.PROPERTY.getURI(), URINamePair.PROPERTY.getName());
        prop.set(PropertyType.NAME, "schema");
        prop.set(PropertyType.TYPE, XsdType.getInstance());
        prop.set(PropertyType.CONTAINMENT, true);
        prop.set(PropertyType.getXmlElementProperty(), true);
        Property property = _helperContext.getTypeHelper().defineOpenContentProperty(null, prop);

        DataObject data = _helperContext.getDataFactory().create(OpenInterface.class);
        data.set("test", "test");
        DataObject xsdObject = data.createDataObject(property);
        Property schemaProp = _helperContext.getTypeHelper().getOpenContentProperty(
            URINamePair.PROP_SCHEMA_SCHEMA.getURI(), URINamePair.PROP_SCHEMA_SCHEMA.getName());
        Schema schema = ((SapXsdHelper)_helperContext.getXSDHelper()).generateSchema("com.sap.sdo.testcase.typefac", null, null);
        xsdObject.getList(schemaProp).add(schema);
        String xml = _helperContext.getXMLHelper().save(data, data.getType().getURI(), "data");
        System.out.println(xml);
        assertLineEquality(start, xml.substring(0, start.length()));
        assertTrue(xml, xml.endsWith(end));

        start = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<sdo:datagraph xmlns:sdo=\"commonj.sdo\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:data=\"commonj.sdo\">\n"
            + "  <changeSummary logging=\"false\">\n"
            + "  </changeSummary>\n";
        end = "</sdo:datagraph>\n";
        data = _helperContext.getDataFactory().create(DataGraphType.getInstance());
        xsdObject = data.createDataObject(property);
        schema = ((SapXsdHelper)_helperContext.getXSDHelper()).generateSchema("com.sap.sdo.testcase.typefac", null, null);
        xsdObject.getList(schemaProp).add(schema);
        xml = _helperContext.getXMLHelper().save(data, data.getType().getURI(), "datagraph");
        assertLineEquality(start, xml.substring(0, start.length()));
        assertLineEquality(end, xml.substring(xml.length() - end.length()));

        start = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<sdo:datagraph" +
                    " xmlns:sdo=\"commonj.sdo\"" +
                    " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
                    " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"" +
                    " xmlns:data=\"com.sap.sdo.testcase.typefac\"" +
                    ">\n"
            + "  <xsd"
            + " xmlns:sdox=\"commonj.sdo/xml\""
            + " xmlns:sdoj=\"commonj.sdo/java\""
            + ">\n"
            + "    <xsd:schema targetNamespace=\"com.sap.sdo.testcase.typefac\""
            + " sdoj:package=\"com.sap.sdo.testcase.typefac\""
            + ">\n";
        String ct1 = "      <xsd:complexType name=\"SimpleContainedIntf\">\n"
            + "        <xsd:sequence>\n"
            + "          <xsd:element name=\"name\" type=\"xsd:string\" minOccurs=\"0\" nillable=\"true\"></xsd:element>\n"
            + "        </xsd:sequence>\n"
            + "      </xsd:complexType>\n";
        String ct2 = "      <xsd:complexType name=\"OpenInterface\" sdox:sequence=\"false\">\n"
            + "        <xsd:sequence>\n"
            + "          <xsd:element name=\"inner\" type=\"xsd:anyURI\" sdox:propertyType=\"data:SimpleContainedIntf\" minOccurs=\"0\" nillable=\"true\"></xsd:element>\n"
            + "          <xsd:element name=\"x\" type=\"xsd:string\" minOccurs=\"0\" nillable=\"true\"></xsd:element>\n"
            + "          <xsd:choice maxOccurs=\"unbounded\">\n"
            + "            <xsd:any minOccurs=\"0\" namespace=\"##other\" processContents=\"lax\"></xsd:any>\n"
            + "            <xsd:any minOccurs=\"0\" namespace=\"##targetNamespace\" processContents=\"lax\"></xsd:any>\n"
            + "          </xsd:choice>\n"
            + "        </xsd:sequence>\n"
            + "        <xsd:anyAttribute namespace=\"##any\" processContents=\"lax\"></xsd:anyAttribute>\n"
            + "      </xsd:complexType>\n";
        String middle = "    </xsd:schema>\n"
            + "  </xsd>\n"
            + "  <changeSummary logging=\"false\">\n"
            + "  </changeSummary>\n";
        end = "  <sdo:dataObject xsi:type=\"data:OpenInterface\">\n"
            + "    <prop xsi:type=\"xsd:string\">abc</prop>\n"
            + "    <openData xsi:type=\"data:OpenInterface\">\n"
            + "      <test xsi:type=\"xsd:int\">1</test>\n"
            + "    </openData>\n"
            + "  </sdo:dataObject>\n"
            + "</sdo:datagraph>\n";
        data = _helperContext.getDataFactory().create(DataGraphType.getInstance());
        data.createDataObject(0);
        DataObject root =
            ((DataGraph)data).createRootObject(_helperContext.getTypeHelper().getType(OpenInterface.class));
        root.set("prop", "abc");
        DataObject openData = _helperContext.getDataFactory().create(OpenInterface.class);
        root.set("openData", openData);
        openData.set("test", 1);

        xml = _helperContext.getXMLHelper().save(data, data.getType().getURI(), "datagraph");
        System.out.println(xml);
        assertLineEquality(start, xml.substring(0, start.length()));
        assertLineEquality(middle,
            xml.substring(
                start.length()+ct1.length()+ct2.length(),
                start.length()+ct1.length()+ct2.length()+middle.length()));
        assertEquals(end, xml.substring(xml.length()-end.length()));
        int index = xml.indexOf(ct1.substring(0, 43));
        assertTrue(ct1.substring(0, 43) + " not found", index > 0);
        assertEquals(ct1, xml.substring(index, index+ct1.length()));
        index = xml.indexOf(ct2.substring(0, 37));
        assertTrue(ct2.substring(0, 37) + " not found", index > 0);
        assertLineEquality(ct2, xml.substring(index, index+ct2.length()));
    }

    @Test
    public void testSequencedWithChangeSummary() {
        LoggingRootSequencedIntf root = (LoggingRootSequencedIntf)_helperContext.getDataFactory().create(LoggingRootSequencedIntf.class);
        List<String> strings = root.getData();
        strings.add("aa");
        strings.add("bb");
        root.getChangeSummary().beginLogging();
        strings.add("cc");
        strings.add("dd");
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<ns1:root" + NS1
            + " xsi:type=\"ns1:LoggingRootSequencedIntf\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"
            + "  <changeSummary xmlns:sdo=\"commonj.sdo\">\n"
            + "    <ns1:root xsi:type=\"ns1:LoggingRootSequencedIntf\" sdo:ref=\"#/ns1:root\">\n"
            + "      <changeSummary></changeSummary>\n"
            + "      <data>aa</data>\n"
            + "      <data>bb</data>\n"
            + "    </ns1:root>\n"
            + "  </changeSummary>\n"
            + "  <data>aa</data>\n"
            + "  <data>bb</data>\n"
            + "  <data>cc</data>\n"
            + "  <data>dd</data>\n"
            + "</ns1:root>\n";
        DataObject rootObj = (DataObject)root;
        assertLineEquality(expected, _helperContext.getXMLHelper().save(rootObj, rootObj.getType().getURI(), "root"));
    }

    @Test
    public void testLetterExample() throws IOException {
        final String schemaFileName = PACKAGE + "letterExample.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(), url.toString());
        DataObject formLetter = _helperContext.getDataFactory().create("letter.xsd","FormLetter");
        assertEquals(true, formLetter.getType().get(TypeType.getMixedProperty()));
        formLetter.getSequence().addText("first line");
        String xml = _helperContext.getXMLHelper().save(formLetter, null, "formLetter");
        System.out.println(xml);

        System.out.println(_helperContext.getTypeHelper().getOpenContentProperty("commonj.sdo","text"));
        assertTrue(xml.indexOf("first line") >= 0);

    }

    @Test
    public void testEncoding() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLStreamWriter w = XMLOutputFactory.newInstance().createXMLStreamWriter(out, "UTF-8");

        w.writeStartDocument("UTF-8", "1.0");
        w.writeCharacters("\n");

        w.writeStartElement("prop");
        w.writeCharacters("äöüÄÖÜß");
        w.writeEndElement();

        w.writeEndDocument();

        System.out.println(out.toString("UTF-8"));

        XMLStreamReader p =
            XMLInputFactory.newInstance().createXMLStreamReader(
                new ByteArrayInputStream(out.toByteArray()));
        while (p.next() != XMLStreamConstants.START_ELEMENT || !"prop".equals(p.getLocalName())) {
        }
        String text1 = p.getElementText();

        p = XMLInputFactory.newInstance().createXMLStreamReader(
            new StringReader(out.toString("UTF-8")));
        while (p.next() != XMLStreamConstants.START_ELEMENT || !"prop".equals(p.getLocalName())) {
        }
        String text2 = p.getElementText();

        assertEquals(text1, text2);
    }

    @Test
    public void testDataObjectSerialization() throws Exception {
        SimpleIntf1 data = (SimpleIntf1)_helperContext.getDataFactory().create(SimpleIntf1.class);
        data.setData("data");
        data.setGreen(true);
        data.setName("name");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out);

        ((SapXmlHelper)_helper).save(data, "elementUri", "elementName", URINamePair.SCHEMA_URI, "anyType", writer, null);

        assertLineEquality(
            "<ns1:elementName" +
            " xmlns:ns1=\"elementUri\" xmlns:ns2=\"com.sap.sdo.testcase.typefac\"" +
            " xsi:type=\"ns2:SimpleIntf1\"" +
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
            " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"" +
            ">\n" +
            "  <data>data</data>\n" +
            "  <green>true</green>\n" +
            "  <name>name</name>\n" +
            "</ns1:elementName>\n",
            out.toString("UTF-8"));

        out = new ByteArrayOutputStream();
        writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out);

        ((SapXmlHelper)_helper).save(data, "elementUri", "elementName", URINamePair.SCHEMA_URI, "anyType", writer, null);

        assertLineEquality(
            "<ns1:elementName" +
            " xmlns:ns1=\"elementUri\" xmlns:ns2=\"com.sap.sdo.testcase.typefac\"" +
            " xsi:type=\"ns2:SimpleIntf1\"" +
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
            " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"" +
            ">\n" +
            "  <data>data</data>\n" +
            "  <green>true</green>\n" +
            "  <name>name</name>\n" +
            "</ns1:elementName>\n",
            out.toString("UTF-8"));
    }

    @Test
    public void testGlobalPropertySerialization() throws Exception {
        DataObject propObj = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        propObj.set(PropertyType.NAME, "globalStringProp");
        propObj.set(PropertyType.TYPE, _helperContext.getTypeHelper().getType("commonj.sdo", "String"));
        propObj.set(PropertyType.getXmlElementProperty(), true);
        Property prop =
            _helperContext.getTypeHelper().defineOpenContentProperty(
                "com.sap.sdo.testcase.typefac", propObj);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out);

        ((SapXmlHelper)_helper).save(
            "value",
            _helperContext.getXSDHelper().getNamespaceURI(prop),
            prop.getName(),
            null, null, writer, null);

        assertEquals(
            "<ns1:globalStringProp xmlns:ns1=\"com.sap.sdo.testcase.typefac\">value</ns1:globalStringProp>\n",
            out.toString("UTF-8"));

        out = new ByteArrayOutputStream();
        writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out);

        try {
            ((SapXmlHelper)_helper).save(
                "value",
                null,
                "prop",
                null, null, writer, null);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals("Can not find type null#null for element null#prop", ex.getMessage());
        }
    }

    @Test
    public void testAttributesWithQName() throws Exception {
        DataObject propObj = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        propObj.set(PropertyType.NAME, "globalQNameProp");
        // propObj.set(PropertyType.TYPE, _helperContext.getTypeHelper().getType("commonj.sdo", "URI"));
        propObj.set(PropertyType.getXmlElementProperty(), false);
        // propObj.set(PropertyType.MANY, true);
        propObj.set(PropertyType.getXsdTypeProperty(), URINamePair.SCHEMA_Q_NAME.toStandardSdoFormat());
        ListSimpleType type = new ListSimpleType(
            (SdoType)_helperContext.getTypeHelper().getType("commonj.sdo", "URI"),
            new URINamePair("com.sap.sdo.testcase.typefac", "URIS"),
            _helperContext);
        type.setXsdType(URINamePair.SCHEMA_Q_NAME);
        propObj.set(PropertyType.TYPE, type);
        Property prop =
            _helperContext.getTypeHelper().defineOpenContentProperty(
                "com.sap.sdo.testcase.typefac", propObj);

        DataObject data = _helperContext.getDataFactory().create(OpenInterface.class);
        List<String> types = new ArrayList<String>();
        types.add(new URINamePair(data.getType().getURI(), data.getType().getName()).toStandardSdoFormat());
        types.add(new URINamePair(prop.getType().getURI(), prop.getType().getName()).toStandardSdoFormat());
        data.setList(prop, types);

        LoggingRootIntf loggingRoot = (LoggingRootIntf)_helperContext.getDataFactory().create(LoggingRootIntf.class);
        loggingRoot.setOpenInterface((OpenInterface)data);

        String xml = _helper.save((DataObject)loggingRoot, "com.sap.sdo.testcase.typefac", "root");

        System.out.println(_helperContext.getXSDHelper().generate(Collections.singletonList(_helperContext.getTypeHelper().getType(OpenInterface.class))));

        XMLDocument doc = _helper.load(xml);
        DataObject root = doc.getRootObject();
        assertNotNull(root);
        assertEquals(xml, _helper.save(root, doc.getRootElementURI(), doc.getRootElementName()));

        List<String> qnames = root.getList("openInterface/"+ prop.getName());
        assertEquals(qnames.toString(), types.size(), qnames.size());
        for (String string : types) {
            assertTrue(qnames.toString(), qnames.contains(string));
        }

        loggingRoot.getChangeSummary().beginLogging();
        data.setList(prop, Collections.emptyList());

        String xml2 = _helper.save((DataObject)loggingRoot, "com.sap.sdo.testcase.typefac", "root");

        XMLDocument doc2 = _helper.load(xml2);
        DataObject root2 = doc2.getRootObject();
        assertNotNull(root2);
        assertEquals(xml2, _helper.save(root2, doc2.getRootElementURI(), doc2.getRootElementName()));

        List<String> qnames2 = root2.getList("openInterface/"+ prop.getName());
        assertEquals(qnames2.toString(), 0, qnames2.size());

        root2.getChangeSummary().endLogging();
        root2.getChangeSummary().undoChanges();
        assertEquals(xml, _helper.save(root2, doc2.getRootElementURI(), doc2.getRootElementName()));
    }

    @Test
    public void testSubstitutionGroup() throws Exception {
        final String schemaFileName = PACKAGE + "ContactList.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(), url.toString());

        final String fileName = PACKAGE + "ContactList.xml";
        URL fileUrl = getClass().getClassLoader().getResource(fileName);

        XMLDocument doc = _helper.load(fileUrl.openStream());
        assertNotNull(doc);
        DataObject contactList = doc.getRootObject();

        String xml = _helper.save(contactList, doc.getRootElementURI(), doc.getRootElementName());
        assertTrue(xml, xml.contains(":Supplier"));
        assertTrue(xml, xml.contains(":Department"));
        assertFalse(xml, xml.contains(":Customer"));

        XMLDocument reloadedDoc = _helper.load(xml);
        assertEquals(
            xml,
            _helper.save(
                reloadedDoc.getRootObject(),
                reloadedDoc.getRootElementURI(),
                reloadedDoc.getRootElementName()));
    }

    @Test
    public void testSubstitutionGroupWithCS() throws Exception {
        final String schemaFileName = PACKAGE + "ContactList.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(), url.toString());

        final String fileName = PACKAGE + "ContactList.xml";
        URL fileUrl = getClass().getClassLoader().getResource(fileName);

        XMLDocument doc = _helper.load(fileUrl.openStream());
        assertNotNull(doc);
        DataObject contactList = doc.getRootObject();
        DataGraph graph =
            (DataGraph)_helperContext.getDataFactory().create(
                URINamePair.DATAGRAPH_TYPE.getURI(),
                URINamePair.DATAGRAPH_TYPE.getName());
        DataObject root = graph.createRootObject(_helperContext.getTypeHelper().getType(OpenInterface.class));
        Property contactListProp = _helperContext.getTypeHelper().getOpenContentProperty("http://www.example.com/Customer", "ContactList");
        root.set(contactListProp, contactList);

        String xml = _helper.save((DataObject)graph, null, "graph");
        assertTrue(xml, xml.contains(":Supplier"));
        assertTrue(xml, xml.contains(":Department"));
        assertFalse(xml, xml.contains(":Customer"));

        root.getChangeSummary().beginLogging();

        List<DataObject> contacts = contactList.getList("Contact");
        assertNotNull(contacts);
        assertEquals(2, contacts.size());

        contacts.get(0).delete();
        DataObject customer = contacts.get(0).getDataObject("Customer");
        customer.getDataObject("Name").set("FirstName", "Otto");
        customer.getDataObject("Name").set("LastName", "Normalverbraucher");

        String xmlWithCS = _helper.save((DataObject)graph, null, "graph");
        System.out.println(xmlWithCS);

        XMLDocument reloadedDoc = _helper.load(xmlWithCS);
        assertNotNull(reloadedDoc);
        assertNotNull(reloadedDoc.getRootObject());
        assertNotNull(reloadedDoc.getRootObject().getChangeSummary());

        assertEquals(
            xmlWithCS,
            _helper.save(
                reloadedDoc.getRootObject(),
                reloadedDoc.getRootElementURI(),
                reloadedDoc.getRootElementName()));

        reloadedDoc.getRootObject().getChangeSummary().endLogging();
        reloadedDoc.getRootObject().getChangeSummary().undoChanges();

        assertEquals(
            xml,
            _helper.save(
                reloadedDoc.getRootObject(),
                reloadedDoc.getRootElementURI(),
                reloadedDoc.getRootElementName()));
    }

    @Test
    public void testInheritedSubstitionGroup() throws Exception {
        final String schemaFileName = PACKAGE + "ContactList.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(), url.toString());

        final String fileName = PACKAGE + "ContactList.xml";
        URL fileUrl = getClass().getClassLoader().getResource(fileName);

        XMLDocument doc = _helper.load(fileUrl.openStream());
        assertNotNull(doc);
        DataObject contactList = doc.getRootObject();

        Property supplier = _helperContext.getTypeHelper().getOpenContentProperty(doc.getRootElementURI(), "Supplier");
        assertNotNull(supplier);
        DataObject inheritedObj = _helperContext.getDataFactory().create(doc.getRootElementURI(), "InheritedType");
        assertNotNull(inheritedObj);
        DataObject name = inheritedObj.createDataObject("Name");
        name.set("FirstName", "Robert");
        name.set("LastName", "Winter");
        DataObject address = inheritedObj.createDataObject("Address");
        address.set("Street", "40 Leighton");
        address.set("Town", "Paddington");
        address.set("City", "Sydney");
        address.set("StateRegionCounty", "New South Wales");
        address.set("ZipPostalCode", "2021");
        address.set("Country", "Australia");

        DataObject contact = contactList.createDataObject("Contact");
        contact.set("Customer", inheritedObj);

        String xml = _helper.save(contactList, doc.getRootElementURI(), doc.getRootElementName());
        assertTrue(xml, xml.contains(":Supplier"));
        assertTrue(xml, xml.contains(":Department"));
        assertFalse(xml, xml.contains(":Customer"));

        XMLDocument reloadedDoc = _helper.load(xml);
        assertEquals(
            xml,
            _helper.save(
                reloadedDoc.getRootObject(),
                reloadedDoc.getRootElementURI(),
                reloadedDoc.getRootElementName()));
    }

    @Test
    public void testSchemaLocation() throws Exception {
        final String fileName = PACKAGE + "ContactList.xml";
        URL fileUrl = getClass().getClassLoader().getResource(fileName);

        DataObject data = _helper.load(fileUrl.openStream()).getRootObject();
        assertNotNull(data);
        Type dataType = data.getType();
        assertEquals("commonj.sdo", dataType.getURI());
        assertEquals("Text", dataType.getName());

        XMLDocument doc = _helper.load(fileUrl.openStream(), fileUrl.toString(), null);
        assertNotNull(doc);
        DataObject contactList = doc.getRootObject();
        Type type = contactList.getType();
        assertEquals("http://www.example.com/Customer", type.getURI());
        assertEquals("+ContactList", type.getName());
    }

    @Test
    public void testTypeProperty() throws Exception {
        final String schemaFileName =
            PACKAGE + "ProcessLeaveRequest.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(url.openStream(), url.toString());
        assertNotNull(types);
        assertFalse(types.isEmpty());

        Type context =
            _helperContext.getTypeHelper().getType(
                "http://sap.com/tc/bpem/wdui/leavereq/ProcessLeaveRequest",
                "Context_466FB40FA8360D30198D11DC83BC003005C5EA9E");
        assertNotNull(context);

        DataObject propObj = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        propObj.set(PropertyType.NAME, "typeProp");
        propObj.set(
            PropertyType.TYPE,
            _helperContext.getTypeHelper().getType(
                URINamePair.TYPE.getURI(),
                URINamePair.TYPE.getName()));
        propObj.set(PropertyType.getXmlElementProperty(), false);
        propObj.set(PropertyType.getXsdTypeProperty(), URINamePair.SCHEMA_Q_NAME.toStandardSdoFormat());
        Property prop =
            _helperContext.getTypeHelper().defineOpenContentProperty(
                "com.sap.sdo.testcase.typefac", propObj);

        DataObject data = _helperContext.getDataFactory().create(OpenInterface.class);
        data.set(prop, null);

        String xml = _helper.save(data, "com.sap.sdo.testcase.typefac", "root");
        XMLDocument doc = _helper.load(xml);
        assertNotNull(doc);
        DataObject reloaded = doc.getRootObject();
        assertNotNull(reloaded);

        assertEquals(null, reloaded.get(prop));
        assertEquals(
            xml,
            _helper.save(reloaded, doc.getRootElementURI(), doc.getRootElementName()));
    }

    @Test
    public void testXsiAttribute() throws Exception {
        final String schemaFileName = PACKAGE + "ProcessLeaveRequest.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(url.openStream(), url.toString());
        assertNotNull(types);
        assertFalse(types.isEmpty());

        Type context =
            _helperContext.getTypeHelper().getType(
                "http://sap.com/tc/bpem/wdui/leavereq/ProcessLeaveRequest",
                "Context_466FB40FA8360D30198D11DC83BC003005C5EA9E");
        assertNotNull(context);

        DataObject data = _helperContext.getDataFactory().create(OpenInterface.class);
        data.set("prop", _helperContext.getDataFactory().create(context));

        String xml = _helper.save(data, "com.sap.sdo.testcase.typefac", "root");
        XMLDocument doc = _helper.load(xml);
        assertNotNull(doc);
        DataObject reloaded = doc.getRootObject();
        assertNotNull(reloaded);

        assertEquals(context, reloaded.getDataObject("prop").getType());
        assertEquals(
            xml,
            _helper.save(reloaded, doc.getRootElementURI(), doc.getRootElementName()));
    }

    @Test
    public void testQNameAttribute() throws Exception {
        final String schemaFileName = PACKAGE + "union.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(url.openStream(), url.toString());
        assertNotNull(types);
        assertFalse(types.isEmpty());

        final String fileName = PACKAGE + "union.xml";
        URL fileUrl = getClass().getClassLoader().getResource(fileName);

        XMLDocument doc = _helper.load(fileUrl.openStream());
        assertNotNull(doc);
        DataObject union = doc.getRootObject();
        assertNotNull(union);

        List<String> memberTypes = union.getList("memberTypes");
        assertNotNull(memberTypes);
        assertEquals(2, memberTypes.size());
        assertTrue(
            memberTypes.toString(),
            memberTypes.contains("http://www.w3.org/2001/XMLSchema#string"));
        assertTrue(
            memberTypes.toString(),
            memberTypes.contains("http://www.w3.org/2001/XMLSchema#integer"));

        String xml = _helper.save(union, doc.getRootElementURI(), doc.getRootElementName());
        doc = _helper.load(fileUrl.openStream());
        assertNotNull(doc);
        union = doc.getRootObject();
        assertNotNull(union);

        memberTypes = union.getList("memberTypes");
        assertNotNull(memberTypes);
        assertEquals(2, memberTypes.size());
        assertTrue(
            memberTypes.toString(),
            memberTypes.contains("http://www.w3.org/2001/XMLSchema#string"));
        assertTrue(
            memberTypes.toString(),
            memberTypes.contains("http://www.w3.org/2001/XMLSchema#integer"));

        assertEquals(xml, _helper.save(union, doc.getRootElementURI(), doc.getRootElementName()));
    }

    @Test
    public void testGlobalElements() throws IOException {
        // data element, could be a sub tree
        DataObject data = _helperContext.getDataFactory().create("com.sap.sdo", "OpenType");
        data.set("name", "myname");

        // the property to be used
        DataObject propObj = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        propObj.set("name", "policy");
        propObj.set("type", _helperContext.getTypeHelper().getType("commonj.sdo", "DataObject"));
        propObj.set("containment", "true");
        Property prop = _helperContext.getTypeHelper().defineOpenContentProperty("http://xmlsec/ws-policy", propObj);

        // root object, that's the object you have in hand
        DataObject root = _helperContext.getDataFactory().create("com.sap.sdo", "OpenType");
        root.set(prop, data);

        XMLDocument xmlDoc = _helperContext.getXMLHelper().createDocument(root, "com.sap.sdo.test", "root");

        StringWriter writer = new StringWriter();
        HashMap<String, Map<String, String>> options = new HashMap<String, Map<String,String>>();
        options.put(SapXmlHelper.OPTION_KEY_PREFIX_MAP, Collections.singletonMap("ws", "http://xmlsec/ws-policy"));

        _helperContext.getXMLHelper().save(xmlDoc, writer, options);
        String xml = writer.toString();

        assertTrue(xml, xml.contains("ws:policy"));
    }

    @Test
    public void testBoolean01() throws Exception {
        final String schemaFileName = PACKAGE + "boolean01.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(url.openStream(), url.toString());
        assertNotNull(types);
        assertFalse(types.isEmpty());

        final String fileName = PACKAGE + "boolean01.xml";
        URL fileUrl = getClass().getClassLoader().getResource(fileName);

        XMLDocument doc = _helper.load(fileUrl.openStream());
        assertNotNull(doc);
        DataObject root = doc.getRootObject();
        assertNotNull(root);

        assertEquals("0", root.getString("bool1"));
        assertEquals("1", root.getString("bool2"));

        root.setBoolean("bool1", true);
        root.setBoolean("bool2", false);

        assertEquals("1", root.getString("bool1"));
        assertEquals("0", root.getString("bool2"));

        root.setBoolean("bool1", false);
        root.setBoolean("bool2", true);

        String xml = _helper.save(root, doc.getRootElementURI(), doc.getRootElementName());

        String originalXml = readFile(fileUrl);
        assertLineEquality(originalXml, xml);
    }

    @Test
    public void testAnySimpleType() throws Exception {
        final String schemaFileName = PACKAGE + "anySimpleTypeExample.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(url.openStream(), url.toString());
        assertNotNull(types);
        assertFalse(types.isEmpty());

        final String fileName = PACKAGE + "anySimpleTypeExample.xml";
        URL fileUrl = getClass().getClassLoader().getResource(fileName);

        XMLDocument doc = _helper.load(fileUrl.openStream());
        assertNotNull(doc);
        DataObject root = doc.getRootObject();
        assertNotNull(root);

        assertEquals("hello", root.getList("any").get(0));
        assertEquals(10, root.getList("any").get(1));

        String xml = _helper.save(root, doc.getRootElementURI(), doc.getRootElementName());

        String originalXml = readFile(fileUrl);
        assertLineEquality(originalXml, xml);
    }
}
