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
package com.sap.sdo.testcase.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.builtin.ChangeSummaryType;
import com.sap.sdo.impl.xml.XMLDocumentImpl;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.DataGraphRootIntf;
import com.sap.sdo.testcase.typefac.SequencedOppositeIntf;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataGraph;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;

public class DataGraphSerializationTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public DataGraphSerializationTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private XMLHelper _helper = null;

    @Before
    public void setUp() throws Exception {
        _helper = _helperContext.getXMLHelper();
    }

    @After
    public void tearDown() throws Exception {
        _helper = null;
    }

    @Test
    public void testDataGraphSerialization() throws Exception {
        DataGraph dataGraph = (DataGraph)_helperContext.getDataFactory().create("commonj.sdo","DataGraphType");

        Type type = _helperContext.getTypeHelper().getType(SequencedOppositeIntf.class);
        dataGraph.createRootObject(type);

        DataObject rootObject = dataGraph.getRootObject();
        assertEquals(type, rootObject.getType());
        assertSame(dataGraph, rootObject.getDataGraph());

        ChangeSummary changeSummary = dataGraph.getChangeSummary();

        assertSame(rootObject, changeSummary.getRootObject());
        assertSame(changeSummary, rootObject.getChangeSummary());

        SequencedOppositeIntf level1Object =
            (SequencedOppositeIntf)rootObject;
        changeSummary.beginLogging();

        DataObject level2Object =
            _helperContext.getDataFactory().create(
                _helperContext.getTypeHelper().getType(SequencedOppositeIntf.class));

        level1Object.getMv().add((SequencedOppositeIntf)level2Object);

        changeSummary.endLogging();
        changeSummary.beginLogging();

        level1Object.setName("name");
        level2Object.set("name", "a name");

        assertSame(rootObject, level2Object.getRootObject());
        assertSame(dataGraph, level2Object.getDataGraph());
        assertSame(changeSummary, level2Object.getChangeSummary());

        Sequence oldSeq = changeSummary.getOldSequence((DataObject)level1Object);
        assertEquals(1, oldSeq.size());
        assertEquals("mv", oldSeq.getProperty(0).getName());

        ((DataObject)dataGraph).createDataObject(0);
        XMLDocument doc = new XMLDocumentImpl((DataObject)dataGraph,"commonj.sdo","datagraph");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        _helper.save(doc, out, null);
        //visitor.generate(doc,null,null);
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
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
            + "<xsd:schema targetNamespace=\"com.sap.sdo.testcase.typefac\""
            + " sdoj:package=\"com.sap.sdo.testcase.typefac\""
            + ">\n"
            + "<xsd:complexType name=\"SequencedOppositeIntf\" sdox:sequence=\"true\">\n"
            + "    <xsd:choice maxOccurs=\"unbounded\">\n"
            + "        <xsd:element name=\"mv\" type=\"data:SequencedOppositeIntf\" sdox:oppositeProperty=\"sv\" nillable=\"true\"/>\n"
            + "        <xsd:element name=\"name\" type=\"xsd:string\" sdox:many=\"false\" nillable=\"true\"/>\n"
            + "    </xsd:choice>\n"
            + "</xsd:complexType>\n"
            + "</xsd:schema>\n"
            + "  </xsd>\n"
            + "  <changeSummary>\n"
            + "    <sdo:dataObject xsi:type=\"data:SequencedOppositeIntf\" sdo:ref=\"#/sdo:dataObject\" sdo:unset=\"name\">\n"
            + "      <mv sdo:ref=\"#/sdo:dataObject/mv.0\"></mv>\n"
            + "    </sdo:dataObject>\n"
            + "    <mv xsi:type=\"data:SequencedOppositeIntf\" sdo:ref=\"#/sdo:dataObject/mv.0\" sdo:unset=\"name\">\n"
            + "    </mv>\n"
            + "  </changeSummary>\n"
            + "  <sdo:dataObject xsi:type=\"data:SequencedOppositeIntf\">\n"
            + "    <mv>\n"
            + "      <name>a name</name>\n"
            + "    </mv>\n"
            + "    <name>name</name>\n"
            + "  </sdo:dataObject>\n"
            + "</sdo:datagraph>\n";

        assertLineEquality(xml, out.toString("UTF-8"));
        XMLDocument doc2 = _helper.load(xml);
        StringWriter xml2 = new StringWriter();
        _helper.save(doc2, xml2, null);
        assertLineEquality(xml, xml2.toString());
    }

    @Test
    public void testDataGraphSerializationWithDeleteNew() throws Exception {
        DataGraph dataGraph = (DataGraph)_helperContext.getDataFactory().create("commonj.sdo","DataGraphType");
        Type type = _helperContext.getTypeHelper().getType(DataGraphRootIntf.class);
        dataGraph.createRootObject(type);

        DataObject rootObject = dataGraph.getRootObject();
        assertEquals(type, rootObject.getType());
        assertSame(dataGraph, rootObject.getDataGraph());

        ChangeSummary changeSummary = dataGraph.getChangeSummary();

        assertSame(rootObject, changeSummary.getRootObject());
        assertSame(changeSummary, rootObject.getChangeSummary());

        SequencedOppositeIntf level1Object =
            (SequencedOppositeIntf)_helperContext.getDataFactory().create(
                _helperContext.getTypeHelper().getType(SequencedOppositeIntf.class));
        ((DataGraphRootIntf)rootObject).setRoot(level1Object);

        changeSummary.beginLogging();

        DataObject level2Object =
            _helperContext.getDataFactory().create(
                _helperContext.getTypeHelper().getType(SequencedOppositeIntf.class));

        level1Object.getMv().add((SequencedOppositeIntf)level2Object);

        level1Object.setName("name");
        level2Object.set("name", "a name");

        assertSame(rootObject, level2Object.getRootObject());
        assertSame(dataGraph, level2Object.getDataGraph());
        assertSame(changeSummary, level2Object.getChangeSummary());

        DataObject level3Object =
            _helperContext.getDataFactory().create(
                _helperContext.getTypeHelper().getType(SequencedOppositeIntf.class));

        level1Object.getMv().add((SequencedOppositeIntf)level3Object);

        level3Object.set("name", "third name");

        level2Object.delete();

        Sequence oldSeq = changeSummary.getOldSequence((DataObject)level1Object);
        assertEquals(0, oldSeq.size());

        ((DataObject)dataGraph).createDataObject(0);
        XMLDocument doc = new XMLDocumentImpl((DataObject)dataGraph,"commonj.sdo","datagraph");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        _helper.save(doc, out, null);

        String xmlStart = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<sdo:datagraph xmlns:sdo=\"commonj.sdo\""
            + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
            + " xmlns:data=\"com.sap.sdo.testcase.typefac\""
            + ">\n"
            + "  <xsd"
            + " xmlns:sdox=\"commonj.sdo/xml\""
            + " xmlns:sdoj=\"commonj.sdo/java\""
            + ">\n"
            + "    <xsd:schema targetNamespace=\"com.sap.sdo.testcase.typefac\""
            + " sdoj:package=\"com.sap.sdo.testcase.typefac\""
            + ">\n";

        String element = "<xsd:element name=\"defaultRootName\" type=\"data:DataGraphRootIntf\" nillable=\"true\"/>\n";

        String complexType1 = "<xsd:complexType name=\"SequencedOppositeIntf\" sdox:sequence=\"true\">\n"
            + "    <xsd:choice maxOccurs=\"unbounded\">\n"
            + "        <xsd:element name=\"mv\" type=\"data:SequencedOppositeIntf\" sdox:oppositeProperty=\"sv\" nillable=\"true\"/>\n"
            + "        <xsd:element name=\"name\" type=\"xsd:string\" sdox:many=\"false\" nillable=\"true\"/>\n"
            + "    </xsd:choice>\n"
            + "</xsd:complexType>\n";

        String complexType2 = "<xsd:complexType name=\"DataGraphRootIntf\">\n"
            + "    <xsd:sequence>\n"
            + "        <xsd:element name=\"root\" type=\"data:SequencedOppositeIntf\" minOccurs=\"0\" nillable=\"true\"/>\n"
            + "    </xsd:sequence>\n"
            + "</xsd:complexType>\n";

        String xmlTail = "</xsd:schema>\n"
            + "  </xsd>\n"
            + "  <changeSummary create=\"#/data:defaultRootName/root/mv.0\">\n"
            + "    <root xsi:type=\"data:SequencedOppositeIntf\" sdo:ref=\"#/data:defaultRootName/root\" sdo:unset=\"mv name\">\n"
            + "    </root>\n"
            + "  </changeSummary>\n"
            + "  <data:defaultRootName>\n"
            + "    <root>\n"
            + "      <name>name</name>\n"
            + "      <mv>\n"
            + "        <name>third name</name>\n"
            + "      </mv>\n"
            + "    </root>\n"
            + "  </data:defaultRootName>\n"
            + "</sdo:datagraph>\n";

        String xml = out.toString("UTF-8");
        assertNotNull(xml);
        assertLineEquality(xmlStart, xml.substring(0, xmlStart.length()));
        assertLineInclusion(element, xml);
        assertLineInclusion(complexType1, xml);
        assertLineInclusion(complexType2, xml);
        assertLineEquality(xmlTail, xml.substring(xml.length()-xmlTail.length()));
//        assertEquals(
//            xmlStart.length()+element.length()+complexType1.length()+complexType2.length()+xmlTail.length(),
//            xml.length());
    }

    @Test
    public void testDataGraphSerializationWithDelete() throws Exception {
        DataGraph dataGraph = (DataGraph)_helperContext.getDataFactory().create("commonj.sdo","DataGraphType");

        Type type = _helperContext.getTypeHelper().getType(DataGraphRootIntf.class);
        dataGraph.createRootObject(type);

        DataObject rootObject = dataGraph.getRootObject();
        assertEquals(type, rootObject.getType());
        assertSame(dataGraph, rootObject.getDataGraph());

        ChangeSummary changeSummary = dataGraph.getChangeSummary();

        assertSame(rootObject, changeSummary.getRootObject());
        assertSame(changeSummary, rootObject.getChangeSummary());

        SequencedOppositeIntf level1Object =
            (SequencedOppositeIntf)_helperContext.getDataFactory().create(
                _helperContext.getTypeHelper().getType(SequencedOppositeIntf.class));
        ((DataGraphRootIntf)rootObject).setRoot(level1Object);

        DataObject level2Object =
            _helperContext.getDataFactory().create(
                _helperContext.getTypeHelper().getType(SequencedOppositeIntf.class));

        level1Object.getMv().add((SequencedOppositeIntf)level2Object);

        changeSummary.beginLogging();

        // level1Object.setName("name");
        level2Object.set("name", "a name");

        assertSame(rootObject, level2Object.getRootObject());
        assertSame(dataGraph, level2Object.getDataGraph());
        assertSame(changeSummary, level2Object.getChangeSummary());

        DataObject level3Object =
            _helperContext.getDataFactory().create(
                _helperContext.getTypeHelper().getType(SequencedOppositeIntf.class));

        level1Object.getMv().add((SequencedOppositeIntf)level3Object);

        level3Object.set("name", "third name");

        level2Object.delete();

        Sequence oldSeq = changeSummary.getOldSequence((DataObject)level1Object);
        assertEquals(1, oldSeq.size());
        assertEquals("mv", oldSeq.getProperty(0).getName());

        ((DataObject)dataGraph).createDataObject(0);
        XMLDocument doc = new XMLDocumentImpl((DataObject)dataGraph,"commonj.sdo","datagraph");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        _helper.save(doc, out, null);

        String xmlStart = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<sdo:datagraph xmlns:sdo=\"commonj.sdo\""
            + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
            + " xmlns:data=\"com.sap.sdo.testcase.typefac\""
            + ">\n"
            + "  <xsd"
            + " xmlns:sdox=\"commonj.sdo/xml\""
            + " xmlns:sdoj=\"commonj.sdo/java\""
            + ">\n"
            + "    <xsd:schema targetNamespace=\"com.sap.sdo.testcase.typefac\""
            + " sdoj:package=\"com.sap.sdo.testcase.typefac\""
            + ">\n";

        String element = "<xsd:element name=\"defaultRootName\" type=\"data:DataGraphRootIntf\" nillable=\"true\"/>\n";

        String complexType1 = "<xsd:complexType name=\"SequencedOppositeIntf\" sdox:sequence=\"true\">\n"
            + "    <xsd:choice maxOccurs=\"unbounded\">\n"
            + "        <xsd:element name=\"mv\" type=\"data:SequencedOppositeIntf\" sdox:oppositeProperty=\"sv\" nillable=\"true\"/>\n"
            + "        <xsd:element name=\"name\" type=\"xsd:string\" sdox:many=\"false\" nillable=\"true\"/>\n"
            + "    </xsd:choice>\n"
            + "</xsd:complexType>\n";

        String complexType2 = "<xsd:complexType name=\"DataGraphRootIntf\">\n"
            + "    <xsd:sequence>\n"
            + "        <xsd:element name=\"root\" type=\"data:SequencedOppositeIntf\" minOccurs=\"0\" nillable=\"true\"/>\n"
            + "    </xsd:sequence>\n"
            + "</xsd:complexType>\n";

        String xmlTail = "</xsd:schema>\n"
            + "  </xsd>\n"
            + "  <changeSummary create=\"#/data:defaultRootName/root/mv.0\" delete=\"#/changeSummary/root.0/mv.0\">\n"
            + "    <root xsi:type=\"data:SequencedOppositeIntf\" sdo:ref=\"#/data:defaultRootName/root\">\n"
            + "      <mv>\n"
            + "      </mv>\n"
            + "    </root>\n"
            + "  </changeSummary>\n"
            + "  <data:defaultRootName>\n"
            + "    <root>\n"
            + "      <mv>\n"
            + "        <name>third name</name>\n"
            + "      </mv>\n"
            + "    </root>\n"
            + "  </data:defaultRootName>\n"
            + "</sdo:datagraph>\n";

        String xml = out.toString("UTF-8");
        assertNotNull(xml);
        assertLineEquality(xmlStart, xml.substring(0, xmlStart.length()));
        assertLineInclusion(element, xml);
        assertLineInclusion(complexType1, xml);
        assertLineInclusion(complexType2, xml);
        assertLineEquality(xmlTail, xml.substring(xml.length()-xmlTail.length()));
//        assertEquals(
//            xmlStart.length()+element.length()+complexType1.length()+complexType2.length()+xmlTail.length(),
//            xml.length());
    }

//    @Test
//    public void testDataGraphSerializationWithRootDelete() throws Exception {
//        DataGraph dataGraph = (DataGraph)_helperContext.getDataFactory().create("commonj.sdo","DataGraphType");
//
//        Type type = _helperContext.getTypeHelper().getType(DataGraphRootIntf.class);
//        dataGraph.createRootObject(type);
//
//        DataObject rootObject = dataGraph.getRootObject();
//        assertEquals(type, rootObject.getType());
//        assertSame(dataGraph, rootObject.getDataGraph());
//
//        ChangeSummary changeSummary = dataGraph.getChangeSummary();
//
//        assertSame(rootObject, changeSummary.getRootObject());
//        assertSame(changeSummary, rootObject.getChangeSummary());
//
//        SequencedOppositeIntf level1Object =
//            (SequencedOppositeIntf)_helperContext.getDataFactory().create(
//                _helperContext.getTypeHelper().getType(SequencedOppositeIntf.class));
//        ((DataGraphRootIntf)rootObject).setRoot(level1Object);
//
//        DataObject level2Object =
//            _helperContext.getDataFactory().create(
//                _helperContext.getTypeHelper().getType(SequencedOppositeIntf.class));
//
//        level1Object.getMv().add((SequencedOppositeIntf)level2Object);
//
//        changeSummary.beginLogging();
//
//        rootObject.detach();
//
//        System.out.println(_helperContext.getXMLHelper().save((DataObject)dataGraph,"commonj.sdo","datagraph"));
//    }

    @Test
    public void testDataGraphSerializationWithDeepDelete() throws Exception {
        DataGraph dataGraph = (DataGraph)_helperContext.getDataFactory().create("commonj.sdo","DataGraphType");

        Type type = _helperContext.getTypeHelper().getType(DataGraphRootIntf.class);
        dataGraph.createRootObject(type);

        DataObject rootObject = dataGraph.getRootObject();
        assertEquals(type, rootObject.getType());
        assertSame(dataGraph, rootObject.getDataGraph());

        ChangeSummary changeSummary = dataGraph.getChangeSummary();

        assertSame(rootObject, changeSummary.getRootObject());
        assertSame(changeSummary, rootObject.getChangeSummary());

        SequencedOppositeIntf level1Object =
            (SequencedOppositeIntf)_helperContext.getDataFactory().create(
                _helperContext.getTypeHelper().getType(SequencedOppositeIntf.class));
        ((DataGraphRootIntf)rootObject).setRoot(level1Object);

        DataObject level2Object =
            _helperContext.getDataFactory().create(
                _helperContext.getTypeHelper().getType(SequencedOppositeIntf.class));

        level1Object.getMv().add((SequencedOppositeIntf)level2Object);

        level2Object.set("name", "level 2");

        assertSame(rootObject, level2Object.getRootObject());
        assertSame(dataGraph, level2Object.getDataGraph());
        assertSame(changeSummary, level2Object.getChangeSummary());

        DataObject level3Object =
            _helperContext.getDataFactory().create(
                _helperContext.getTypeHelper().getType(SequencedOppositeIntf.class));

        level2Object.getList("mv").add(level3Object);

        level3Object.set("name", "level 3");
        level1Object.setName("root - level 1");

        DataObject level4Object =
            _helperContext.getDataFactory().create(
                _helperContext.getTypeHelper().getType(SequencedOppositeIntf.class));

        level3Object.getList("mv").add(level4Object);

        level4Object.set("name", "level 4");

        changeSummary.beginLogging();

        // level3Object.delete();
        level2Object.delete();

        level1Object.getMv().add((SequencedOppositeIntf)level2Object);

        Sequence oldSeq = changeSummary.getOldSequence((DataObject)level1Object);
        assertEquals(2, oldSeq.size());
        assertEquals("mv", oldSeq.getProperty(0).getName());
        assertEquals("name", oldSeq.getProperty(1).getName());

        Sequence old2Seq = changeSummary.getOldSequence(level2Object);
        assertEquals(2, old2Seq.size());
        assertEquals("name", old2Seq.getProperty(0).getName());
        assertEquals("mv", old2Seq.getProperty(1).getName());

        ((DataObject)dataGraph).createDataObject(0);
        XMLDocument doc = new XMLDocumentImpl((DataObject)dataGraph,"commonj.sdo","datagraph");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        _helper.save(doc, out, null);

        String xmlStart = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<sdo:datagraph xmlns:sdo=\"commonj.sdo\""
            + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
            + " xmlns:data=\"com.sap.sdo.testcase.typefac\""
            + ">\n"
            + "  <xsd"
            + " xmlns:sdox=\"commonj.sdo/xml\""
            + " xmlns:sdoj=\"commonj.sdo/java\""
            + ">\n"
            + "    <xsd:schema targetNamespace=\"com.sap.sdo.testcase.typefac\""
            + " sdoj:package=\"com.sap.sdo.testcase.typefac\""
            + ">\n";

        String element = "<xsd:element name=\"defaultRootName\" type=\"data:DataGraphRootIntf\" nillable=\"true\"/>\n";

        String complexType1 = "<xsd:complexType name=\"SequencedOppositeIntf\" sdox:sequence=\"true\">\n"
            + "    <xsd:choice maxOccurs=\"unbounded\">\n"
            + "        <xsd:element name=\"mv\" type=\"data:SequencedOppositeIntf\" sdox:oppositeProperty=\"sv\" nillable=\"true\"/>\n"
            + "        <xsd:element name=\"name\" type=\"xsd:string\" sdox:many=\"false\" nillable=\"true\"/>\n"
            + "    </xsd:choice>\n"
            + "</xsd:complexType>\n";

        String complexType2 = "<xsd:complexType name=\"DataGraphRootIntf\">\n"
            + "    <xsd:sequence>\n"
            + "        <xsd:element name=\"root\" type=\"data:SequencedOppositeIntf\" minOccurs=\"0\" nillable=\"true\"/>\n"
            + "    </xsd:sequence>\n"
            + "</xsd:complexType>\n";

        String xmlTail = "</xsd:schema>\n"
            + "  </xsd>\n"
            + "  <changeSummary delete=\"#/changeSummary/mv.0/mv.0\">\n"
            + "    <root xsi:type=\"data:SequencedOppositeIntf\" sdo:ref=\"#/data:defaultRootName/root\">\n"
            + "      <mv sdo:ref=\"#/data:defaultRootName/root/mv.0\"></mv>\n"
            + "      <name>root - level 1</name>\n"
            + "    </root>\n"
            + "    <mv xsi:type=\"data:SequencedOppositeIntf\" sdo:ref=\"#/data:defaultRootName/root/mv.0\">\n"
            + "      <name>level 2</name>\n"
            + "      <mv>\n"
            + "        <name>level 3</name>\n"
            + "        <mv>\n"
            + "          <name>level 4</name>\n"
            + "        </mv>\n"
            + "      </mv>\n"
            + "    </mv>\n"
            + "  </changeSummary>\n"
            + "  <data:defaultRootName>\n"
            + "    <root>\n"
            + "      <name>root - level 1</name>\n"
            + "      <mv xsi:nil=\"true\"></mv>\n"
            + "    </root>\n"
            + "  </data:defaultRootName>\n"
            + "</sdo:datagraph>\n";

        String xml = out.toString("UTF-8");
        assertNotNull(xml);
        assertLineEquality(xmlStart, xml.substring(0, xmlStart.length()));
        assertLineInclusion(element, xml);
        assertLineInclusion(complexType1, xml);
        assertLineInclusion(complexType2, xml);
        assertLineEquality(xmlTail, xml.substring(xml.length()-xmlTail.length()));
//        assertEquals(
//            xmlStart.length()+element.length()+complexType1.length()+complexType2.length()+xmlTail.length(),
//            xml.length());
    }

    @Test
    public void testDataGraphSerializationWithDeep2Delete() throws Exception {
        DataGraph dataGraph = (DataGraph)_helperContext.getDataFactory().create("commonj.sdo","DataGraphType");

        Type type = _helperContext.getTypeHelper().getType(DataGraphRootIntf.class);
        dataGraph.createRootObject(type);

        DataObject rootObject = dataGraph.getRootObject();
        assertEquals(type, rootObject.getType());
        assertSame(dataGraph, rootObject.getDataGraph());

        ChangeSummary changeSummary = dataGraph.getChangeSummary();

        assertSame(rootObject, changeSummary.getRootObject());
        assertSame(changeSummary, rootObject.getChangeSummary());

        SequencedOppositeIntf level1Object =
            (SequencedOppositeIntf)_helperContext.getDataFactory().create(
                _helperContext.getTypeHelper().getType(SequencedOppositeIntf.class));
        ((DataGraphRootIntf)rootObject).setRoot(level1Object);

        DataObject level2Object =
            _helperContext.getDataFactory().create(
                _helperContext.getTypeHelper().getType(SequencedOppositeIntf.class));

        level1Object.getMv().add((SequencedOppositeIntf)level2Object);

        level2Object.set("name", "level 2");

        assertSame(rootObject, level2Object.getRootObject());
        assertSame(dataGraph, level2Object.getDataGraph());
        assertSame(changeSummary, level2Object.getChangeSummary());

        DataObject level3Object =
            _helperContext.getDataFactory().create(
                _helperContext.getTypeHelper().getType(SequencedOppositeIntf.class));

        level2Object.getList("mv").add(level3Object);

        level3Object.set("name", "level 3");
        level1Object.setName("root - level 1");

        DataObject level4Object =
            _helperContext.getDataFactory().create(
                _helperContext.getTypeHelper().getType(SequencedOppositeIntf.class));

        level3Object.getList("mv").add(level4Object);

        level4Object.set("name", "level 4");

        changeSummary.beginLogging();

        level3Object.delete();
        level3Object.set("name", "level 3");

        level1Object.getMv().add((SequencedOppositeIntf)level3Object);

        Sequence oldSeq = changeSummary.getOldSequence((DataObject)level1Object);
        assertEquals(2, oldSeq.size());
        assertEquals("mv", oldSeq.getProperty(0).getName());
        assertEquals("name", oldSeq.getProperty(1).getName());

        Sequence old2Seq = changeSummary.getOldSequence(level2Object);
        assertEquals(2, old2Seq.size());
        assertEquals("name", old2Seq.getProperty(0).getName());
        assertEquals("mv", old2Seq.getProperty(1).getName());

        ((DataObject)dataGraph).createDataObject(0);
        XMLDocument doc = new XMLDocumentImpl((DataObject)dataGraph,"commonj.sdo","datagraph");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        _helper.save(doc, out, null);

        String xmlStart = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<sdo:datagraph xmlns:sdo=\"commonj.sdo\""
            + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
            + " xmlns:data=\"com.sap.sdo.testcase.typefac\""
            + ">\n"
            + "  <xsd"
            + " xmlns:sdox=\"commonj.sdo/xml\""
            + " xmlns:sdoj=\"commonj.sdo/java\""
            + ">\n"
            + "    <xsd:schema targetNamespace=\"com.sap.sdo.testcase.typefac\""
            + " sdoj:package=\"com.sap.sdo.testcase.typefac\""
            + ">\n";

        String element = "<xsd:element name=\"defaultRootName\" type=\"data:DataGraphRootIntf\" nillable=\"true\"/>\n";

        String complexType1 = "<xsd:complexType name=\"SequencedOppositeIntf\" sdox:sequence=\"true\">\n"
            + "    <xsd:choice maxOccurs=\"unbounded\">\n"
            + "        <xsd:element name=\"mv\" type=\"data:SequencedOppositeIntf\" sdox:oppositeProperty=\"sv\" nillable=\"true\"/>\n"
            + "        <xsd:element name=\"name\" type=\"xsd:string\" sdox:many=\"false\" nillable=\"true\"/>\n"
            + "    </xsd:choice>\n"
            + "</xsd:complexType>\n";

        String complexType2 = "<xsd:complexType name=\"DataGraphRootIntf\">\n"
            + "    <xsd:sequence>\n"
            + "        <xsd:element name=\"root\" type=\"data:SequencedOppositeIntf\" minOccurs=\"0\" nillable=\"true\"/>\n"
            + "    </xsd:sequence>\n"
            + "</xsd:complexType>\n";

        String xmlTail = "</xsd:schema>\n"
            + "  </xsd>\n"
            + "  <changeSummary delete=\"#/changeSummary/mv.1/mv.0\">\n"
            + "    <root xsi:type=\"data:SequencedOppositeIntf\" sdo:ref=\"#/data:defaultRootName/root\">\n"
            + "      <mv sdo:ref=\"#/data:defaultRootName/root/mv.0\"></mv>\n"
            + "      <name>root - level 1</name>\n"
            + "    </root>\n"
            + "    <mv xsi:type=\"data:SequencedOppositeIntf\" sdo:ref=\"#/data:defaultRootName/root/mv.0\">\n"
            + "      <name>level 2</name>\n"
            + "      <mv sdo:ref=\"#/data:defaultRootName/root/mv.1\"></mv>\n"
            + "    </mv>\n"
            + "    <mv xsi:type=\"data:SequencedOppositeIntf\" sdo:ref=\"#/data:defaultRootName/root/mv.1\">\n"
            + "      <name>level 3</name>\n"
            + "      <mv>\n"
            + "        <name>level 4</name>\n"
            + "      </mv>\n"
            + "    </mv>\n"
            + "  </changeSummary>\n"
            + "  <data:defaultRootName>\n"
            + "    <root>\n"
            + "      <mv>\n"
            + "        <name>level 2</name>\n"
            + "      </mv>\n"
            + "      <name>root - level 1</name>\n"
            + "      <mv>\n"
            + "        <name>level 3</name>\n"
            + "      </mv>\n"
            + "    </root>\n"
            + "  </data:defaultRootName>\n"
            + "</sdo:datagraph>\n";

        String xml = out.toString("UTF-8");
        assertNotNull(xml);
        assertLineEquality(xmlStart, xml.substring(0, xmlStart.length()));
        assertLineInclusion(element, xml);
        assertLineInclusion(complexType1, xml);
        assertLineInclusion(complexType2, xml);
        assertLineEquality(xmlTail, xml.substring(xml.length()-xmlTail.length()));
        System.out.println(xml);
//        assertEquals(
//            xmlStart.length()+element.length()+complexType1.length()+complexType2.length()+xmlTail.length(),
//            xml.length());
    }

    @Test
    public void testDataGraphWithLocalChangeSummaryParsing()
    throws Exception {
	        final String fileName = PACKAGE + "DataGraphWithLocalChangeSummary.xml";
	        InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);

			DataGraph ret = (DataGraph)_helperContext.getXMLHelper().load(is).getRootObject();
			DataObject root = ret.getRootObject();
			assertNotNull(root);
			assertEquals("CompanyType",root.getType().getName());
			assertEquals(ChangeSummaryType.getInstance(),root.getInstanceProperty("changeSummary").getType());
			assertNotNull(root.get("changeSummary"));
			assertNotNull(root.getChangeSummary());
			Property p = root.getInstanceProperty("name");
			assertEquals("MegaCorp",root.getString(p));
			p = root.getInstanceProperty("employeeOfTheMonth");
			DataObject eom = root.getDataObject(p);
			assertEquals("Al Smith",eom.getString("name"));
			List depts = root.getList("departments");
			DataObject dept = (DataObject)depts.get(0);
			p = dept.getInstanceProperty("employees");
			List emps = dept.getList(p);
			assertEquals(3, emps.size());
			assertNotNull(root.get("changeSummary"));
			List oldEmps = (List)ret.getChangeSummary().getOldValue(dept,p).getValue();
			assertEquals(3, oldEmps.size());
			assertEquals(emps.get(0), oldEmps.get(0));
			assertEquals(dept,((DataObject)oldEmps.get(0)).getContainer());
			p = ((DataObject)emps.get(0)).getInstanceProperty("SN");
			assertEquals("E0001",((DataObject)emps.get(0)).get(p));
			assertEquals("E0003",((DataObject)emps.get(1)).get(p));
			assertEquals("E0004",((DataObject)emps.get(2)).get(p));
			assertEquals("E0003",((DataObject)oldEmps.get(2)).get(p));
			assertEquals("E0002",ret.getChangeSummary().getOldValue((DataObject)oldEmps.get(1),p).getValue());
			assertTrue(ret.getChangeSummary().isDeleted((DataObject)oldEmps.get(1)));
			assertTrue(ret.getChangeSummary().isCreated((DataObject)emps.get(2)));

            System.out.println(_helperContext.getXMLHelper().save((DataObject)ret, null, "datagraph"));

	        ByteArrayOutputStream os = new ByteArrayOutputStream();
	        _helper.save(ret.getRootObject(), root.getType().getURI(), "company", os);
	        String expected =
	        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<ns1:company" +
            " xmlns:ns1=\"companydatagraphLCS.xsd\"" +
            " name=\"MegaCorp\" employeeOfTheMonth=\"E0004\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
            " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n" +
	        "  <departments name=\"Advanced Technologies\" location=\"NY\" number=\"123\">\n" +
	        "    <employees name=\"John Jones\" SN=\"E0001\"></employees>\n" +
	        "    <employees name=\"Jane Doe\" SN=\"E0003\"></employees>\n" +
	        "    <employees name=\"Al Smith\" SN=\"E0004\" manager=\"true\"></employees>\n" +
	        "  </departments>\n" +
	        "  <changeSummary create=\"E0004\" delete=\"E0002\" xmlns:sdo=\"commonj.sdo\">\n" +
	        "    <ns1:company xsi:type=\"ns1:CompanyType\" sdo:ref=\"#/ns1:company\" name=\"AcmeCorp\" sdo:unset=\"employeeOfTheMonth\"></ns1:company>\n" +
	        "    <departments xsi:type=\"ns1:DepartmentType\" sdo:ref=\"#/ns1:company/departments.0\">\n" +
	        "      <employees sdo:ref=\"E0001\"></employees>\n" +
	        "      <employees name=\"Mary Smith\" SN=\"E0002\" manager=\"true\"></employees>\n" +
	        "      <employees sdo:ref=\"E0003\"></employees>\n" +
	        "    </departments>\n" +
	        "  </changeSummary>\n" +
	        "</ns1:company>\n";
	        assertLineEquality(expected, os.toString());
            DataObject expectedRoot = _helperContext.getXMLHelper().load(expected).getRootObject();
	        assertTrue("\nexpected:\n" + expectedRoot + "\nactual:\n" + root,
                _helperContext.getEqualityHelper().equal(expectedRoot, root));
    }

    @Test
    public void testDataGraphWithLocalChangeSummaryQualifiedParsing()
    throws Exception {
            final String fileName = PACKAGE + "DataGraphWithLocalChangeSummaryQualified.xml";
            InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);

            DataGraph ret = (DataGraph)_helperContext.getXMLHelper().load(is).getRootObject();
            DataObject root = ret.getRootObject();
            assertNotNull(root);
            assertEquals("CompanyType",root.getType().getName());
            assertEquals(ChangeSummaryType.getInstance(),root.getInstanceProperty("changeSummary").getType());
            assertNotNull(root.get("changeSummary"));
            assertNotNull(root.getChangeSummary());
            Property p = root.getInstanceProperty("name");
            assertEquals("MegaCorp",root.getString(p));
            p = root.getInstanceProperty("employeeOfTheMonth");
            DataObject eom = root.getDataObject(p);
            assertEquals("Al Smith",eom.getString("name"));
            List depts = root.getList("departments");
            DataObject dept = (DataObject)depts.get(0);
            p = dept.getInstanceProperty("employees");
            List emps = dept.getList(p);
            assertEquals(3, emps.size());
            assertNotNull(root.get("changeSummary"));
            List oldEmps = (List)ret.getChangeSummary().getOldValue(dept,p).getValue();
            assertEquals(3, oldEmps.size());
            assertEquals(emps.get(0), oldEmps.get(0));
            assertEquals(dept,((DataObject)oldEmps.get(0)).getContainer());
            p = ((DataObject)emps.get(0)).getInstanceProperty("SN");
            assertEquals("E0001",((DataObject)emps.get(0)).get(p));
            assertEquals("E0003",((DataObject)emps.get(1)).get(p));
            assertEquals("E0004",((DataObject)emps.get(2)).get(p));
            assertEquals("E0003",((DataObject)oldEmps.get(2)).get(p));
            assertEquals("E0002",ret.getChangeSummary().getOldValue((DataObject)oldEmps.get(1),p).getValue());
            assertTrue(ret.getChangeSummary().isDeleted((DataObject)oldEmps.get(1)));
            assertTrue(ret.getChangeSummary().isCreated((DataObject)emps.get(2)));

            System.out.println(_helperContext.getXMLHelper().save((DataObject)ret, null, "datagraph"));

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            _helper.save(ret.getRootObject(), root.getType().getURI(), "company", os);
            String expected =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<ns1:company" +
            " xmlns:ns1=\"companydatagraphLCS.xsd\"" +
            " name=\"MegaCorp\" employeeOfTheMonth=\"E0004\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
            " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n" +
            "  <ns1:departments name=\"Advanced Technologies\" location=\"NY\" number=\"123\">\n" +
            "    <ns1:employees name=\"John Jones\" SN=\"E0001\"></ns1:employees>\n" +
            "    <ns1:employees name=\"Jane Doe\" SN=\"E0003\"></ns1:employees>\n" +
            "    <ns1:employees name=\"Al Smith\" SN=\"E0004\" manager=\"true\"></ns1:employees>\n" +
            "  </ns1:departments>\n" +
            "  <ns1:changeSummary create=\"E0004\" delete=\"E0002\" xmlns:sdo=\"commonj.sdo\">\n" +
            "    <ns1:company xsi:type=\"ns1:CompanyType\" sdo:ref=\"#/ns1:company\" name=\"AcmeCorp\"></ns1:company>\n" +
            "    <ns1:departments xsi:type=\"ns1:DepartmentType\" sdo:ref=\"#/ns1:company/ns1:departments.0\">\n" +
            "      <ns1:employees sdo:ref=\"E0001\"></ns1:employees>\n" +
            "      <ns1:employees name=\"Mary Smith\" SN=\"E0002\" manager=\"true\"></ns1:employees>\n" +
            "      <ns1:employees sdo:ref=\"E0003\"></ns1:employees>\n" +
            "    </ns1:departments>\n" +
            "  </ns1:changeSummary>\n" +
            "</ns1:company>\n";
            assertLineEquality(expected, os.toString());
            DataObject expectedRoot = _helperContext.getXMLHelper().load(expected).getRootObject();
            assertTrue("\nexpected:\n" + expectedRoot + "\nactual:\n" + root,
                _helperContext.getEqualityHelper().equal(expectedRoot, root));
    }

    @Test
    public void testDataGraphWithIdElementParsing()
    throws Exception {
	        final String fileName = PACKAGE + "DataGraphWithIdElements.xml";
	        InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);

            DataGraph ret = (DataGraph)_helperContext.getXMLHelper().load(is).getRootObject();
			DataObject root = ret.getRootObject();
			assertNotNull(root);
			assertEquals("CompanyType",root.getType().getName());
			assertNotNull(ret.getChangeSummary());
			Property p = root.getInstanceProperty("name");
			assertEquals("MegaCorp",root.getString(p));
			assertEquals("ACME",ret.getChangeSummary().getOldValue(root,p).getValue());
			p = root.getInstanceProperty("employeeOfTheMonth");
			DataObject eom = root.getDataObject(p);
			assertEquals("Al Smith",eom.getString("name"));
			DataObject oldEOM = (DataObject)ret.getChangeSummary().getOldValue(root,p).getValue();
			p = eom.getInstanceProperty("SN");
			assertEquals("E0002",ret.getChangeSummary().getOldValue(oldEOM,p).getValue());
			p = eom.getInstanceProperty("name");
			assertEquals("Mary Smith",ret.getChangeSummary().getOldValue(oldEOM,p).getValue());
			List depts = root.getList("departments");
			DataObject dept = (DataObject)depts.get(0);
			p = dept.getInstanceProperty("employees");
			List emps = dept.getList(p);
			assertEquals(3, emps.size());
			List oldEmps = (List)ret.getChangeSummary().getOldValue(dept,p).getValue();
			assertEquals(3, oldEmps.size());
			assertEquals(emps.get(0), oldEmps.get(0));
			assertEquals(dept,((DataObject)oldEmps.get(0)).getContainer());
			p = ((DataObject)emps.get(0)).getInstanceProperty("SN");
			assertEquals("E0001",((DataObject)emps.get(0)).get(p));
			assertEquals("E0003",((DataObject)emps.get(1)).get(p));
			assertEquals("E0004",((DataObject)emps.get(2)).get(p));
			assertEquals("E0003",((DataObject)oldEmps.get(2)).get(p));
			assertEquals("E0002",ret.getChangeSummary().getOldValue((DataObject)oldEmps.get(1),p).getValue());
			assertTrue(ret.getChangeSummary().isDeleted((DataObject)oldEmps.get(1)));
			assertTrue(ret.getChangeSummary().isCreated((DataObject)emps.get(2)));
    }

    @Test
    public void testDataGraphWithAlternatvIndex()
    throws Exception {
            final String fileName = PACKAGE + "DataGraphWithAlternativeIndex.xml";
            InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);

            DataGraph ret = (DataGraph)_helperContext.getXMLHelper().load(is).getRootObject();
            DataObject root = ret.getRootObject();
            assertNotNull(root);
            assertEquals("CompanyType",root.getType().getName());
            assertNotNull(ret.getChangeSummary());
            Property p = root.getInstanceProperty("name");
            assertEquals("MegaCorp",root.getString(p));
            assertEquals("ACME",ret.getChangeSummary().getOldValue(root,p).getValue());
            p = root.getInstanceProperty("employeeOfTheMonth");
            DataObject eom = root.getDataObject(p);
            assertEquals("Al Smith",eom.getString("name"));
            DataObject oldEOM = (DataObject)ret.getChangeSummary().getOldValue(root,p).getValue();
            p = eom.getInstanceProperty("SN");
            assertEquals("E0002",ret.getChangeSummary().getOldValue(oldEOM,p).getValue());
            p = eom.getInstanceProperty("name");
            assertEquals("Mary Smith",ret.getChangeSummary().getOldValue(oldEOM,p).getValue());
            List depts = root.getList("departments");
            DataObject dept = (DataObject)depts.get(0);
            p = dept.getInstanceProperty("employees");
            List emps = dept.getList(p);
            assertEquals(3, emps.size());
            List oldEmps = (List)ret.getChangeSummary().getOldValue(dept,p).getValue();
            assertEquals(3, oldEmps.size());
            assertEquals(emps.get(0), oldEmps.get(0));
            assertEquals(dept,((DataObject)oldEmps.get(0)).getContainer());
            p = ((DataObject)emps.get(0)).getInstanceProperty("SN");
            assertEquals("E0001",((DataObject)emps.get(0)).get(p));
            assertEquals("E0003",((DataObject)emps.get(1)).get(p));
            assertEquals("E0004",((DataObject)emps.get(2)).get(p));
            assertEquals("E0003",((DataObject)oldEmps.get(2)).get(p));
            assertEquals("E0002",ret.getChangeSummary().getOldValue((DataObject)oldEmps.get(1),p).getValue());
            assertTrue(ret.getChangeSummary().isDeleted((DataObject)oldEmps.get(1)));
            assertTrue(ret.getChangeSummary().isCreated((DataObject)emps.get(2)));
    }

    @Test
    public void testDataGraphSeqParsing()
    throws Exception {
	        final String fileName = PACKAGE + "DataGraphSeq.xml";
	        InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);

            DataGraph ret = (DataGraph)_helperContext.getXMLHelper().load(is).getRootObject();
			DataObject root = ret.getRootObject();
			assertNotNull(root);
			assertEquals("CompanyType",root.getType().getName());
			assertNotNull(ret.getChangeSummary());
			Property p = root.getInstanceProperty("name");
			assertEquals("MegaCorp",root.getString(p));
			assertEquals("ACME",ret.getChangeSummary().getOldValue(root,p).getValue());
			p = root.getInstanceProperty("employeeOfTheMonth");
			DataObject eom = root.getDataObject(p);
			assertEquals("Al Smith",eom.getString("name"));
			DataObject oldEOM = (DataObject)ret.getChangeSummary().getOldValue(root,p).getValue();
			p = eom.getInstanceProperty("name");
			assertEquals("Mary Smith",ret.getChangeSummary().getOldValue(oldEOM,p).getValue());
			List depts = root.getList("departments");
			DataObject dept = (DataObject)depts.get(0);
			p = dept.getInstanceProperty("employees");
			assertTrue(((SdoProperty)p).isXmlElement());
			List emps = dept.getList(p);
			assertEquals(3, emps.size());
			Sequence newSeq = dept.getSequence();
			assertEquals(3, newSeq.size());
			Sequence oldSeq = ret.getChangeSummary().getOldSequence(dept);
			assertEquals(3, oldSeq.size());
			Property seqProp = oldSeq.getProperty(0);
			assertEquals(p.getName(),seqProp.getName());
			assertSame(p,seqProp);
			List oldEmps = (List)ret.getChangeSummary().getOldValue(dept,p).getValue();
			assertEquals(3, oldEmps.size());
			assertEquals(emps.get(0), oldEmps.get(0));
			assertEquals(dept,((DataObject)oldEmps.get(0)).getContainer());
			p = ((DataObject)emps.get(0)).getInstanceProperty("SN");
			assertEquals("E0001",((DataObject)emps.get(0)).get(p));
			assertEquals("E0003",((DataObject)emps.get(1)).get(p));
			assertEquals("E0004",((DataObject)emps.get(2)).get(p));
			assertEquals("E0003",((DataObject)oldEmps.get(2)).get(p));
			assertEquals("E0002",ret.getChangeSummary().getOldValue((DataObject)oldEmps.get(1),p).getValue());
			assertTrue(ret.getChangeSummary().isDeleted((DataObject)oldEmps.get(1)));
			assertTrue(ret.getChangeSummary().isCreated((DataObject)emps.get(2)));
    }

	@Test
    public void testWsdlParsing()
	throws Exception {
        final String schemaFileName = PACKAGE + "wsdl.xsd";
        InputStream is = getClass().getClassLoader().getResourceAsStream(schemaFileName);
    	List ts = _helperContext.getXSDHelper().define(is,null);
    	Type t = _helperContext.getTypeHelper().getType("http://schemas.xmlsoap.org/wsdl/","tDefinitions");
    	assertTrue(t.getProperty("message").isMany());
	    final String wsdlFileName = PACKAGE + "wsdl.xml";
	    is = getClass().getClassLoader().getResourceAsStream(wsdlFileName);

		XMLDocument doc = _helperContext.getXMLHelper().load(is);
		DataObject obj = doc.getRootObject();
		List messages = obj.getList("message");
		assertEquals(2,messages.size());
	}
    @Test
    public void testDataGraphParsing()
	throws Exception {
	        final String fileName = PACKAGE + "DataGraph.xml";
	        URL url = getClass().getClassLoader().getResource(fileName);

			DataGraph ret = (DataGraph)_helperContext.getXMLHelper().load(url.openStream()).getRootObject();

			DataObject root = ret.getRootObject();

			assertNotNull(root);
			assertEquals("CompanyType",root.getType().getName());
			assertNotNull(ret.getChangeSummary());
			Property p = root.getInstanceProperty("name");
			assertEquals("MegaCorp",root.getString(p));
			assertEquals("ACME",ret.getChangeSummary().getOldValue(root,p).getValue());
			p = root.getInstanceProperty("employeeOfTheMonth");
			DataObject eom = root.getDataObject(p);
			assertEquals("Al Smith",eom.getString("name"));
			DataObject oldEOM = (DataObject)ret.getChangeSummary().getOldValue(root,p).getValue();
			p = eom.getInstanceProperty("name");
			assertEquals("Mary Smith",ret.getChangeSummary().getOldValue(oldEOM,p).getValue());
			List depts = root.getList("departments");
			DataObject dept = (DataObject)depts.get(0);
			p = dept.getInstanceProperty("employees");
			List emps = dept.getList(p);
			assertEquals(3, emps.size());
			List oldEmps = (List)ret.getChangeSummary().getOldValue(dept,p).getValue();
			assertEquals(3, oldEmps.size());
			assertEquals(emps.get(0), oldEmps.get(0));
			assertEquals(dept,((DataObject)oldEmps.get(0)).getContainer());
			p = ((DataObject)emps.get(0)).getInstanceProperty("SN");
			assertEquals("E0001",((DataObject)emps.get(0)).get(p));
			assertEquals("E0003",((DataObject)emps.get(1)).get(p));
			assertEquals("E0004",((DataObject)emps.get(2)).get(p));
			assertEquals("E0003",((DataObject)oldEmps.get(2)).get(p));
			assertEquals("E0002",ret.getChangeSummary().getOldValue((DataObject)oldEmps.get(1),p).getValue());
			assertTrue(ret.getChangeSummary().isDeleted((DataObject)oldEmps.get(1)));
			assertTrue(ret.getChangeSummary().isCreated((DataObject)emps.get(2)));
            assertTrue(ret.getChangeSummary().isLogging());
            assertFalse(ret.getChangeSummary().isModified((DataObject)ret));

	        XMLDocument doc = new XMLDocumentImpl((DataObject)ret,"commonj.sdo","datagraph");
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            _helper.save(doc, out, null);

			InputStream input = new ByteArrayInputStream(out.toByteArray());
            DataGraph ret2 = (DataGraph)_helperContext.getXMLHelper().load(input).getRootObject();

            String retXml = _helperContext.getXMLHelper().save((DataObject)ret, null, "data");
            String ret2Xml = _helperContext.getXMLHelper().save((DataObject)ret2, null, "data");

//            assertEquals(readFile(url), retXml);
            assertEquals(retXml, ret2Xml);

			assertTrue(_helperContext.getEqualityHelper().equal(ret.getRootObject(),ret2.getRootObject()));
            ret.getRootObject().getChangeSummary().undoChanges();
            ret2.getRootObject().getChangeSummary().undoChanges();
            assertTrue(_helperContext.getEqualityHelper().equal(ret.getRootObject(),ret2.getRootObject()));
	}

    @Test
    public void testDataGraphWithRelativPathParsing()
    throws Exception {
            final String fileName = PACKAGE + "DataGraphWithRelativPath.xml";
            URL url = getClass().getClassLoader().getResource(fileName);

            DataGraph ret =
                (DataGraph)_helperContext.getXMLHelper().load(url.openStream()).getRootObject();
            System.out.println(_helperContext.getXMLHelper().save((DataObject)ret, null, "graph"));
            DataObject root = ret.getRootObject();
            assertNotNull(root);
            assertEquals("CompanyType",root.getType().getName());
            assertNotNull(ret.getChangeSummary());
            Property p = root.getInstanceProperty("name");
            assertEquals("MegaCorp",root.getString(p));
            assertEquals("ACME",ret.getChangeSummary().getOldValue(root,p).getValue());
            p = root.getInstanceProperty("employeeOfTheMonth");
            DataObject eom = root.getDataObject(p);
            assertEquals("Al Smith",eom.getString("name"));
            DataObject oldEOM = (DataObject)ret.getChangeSummary().getOldValue(root,p).getValue();
            p = eom.getInstanceProperty("name");
            assertEquals("Mary Smith",ret.getChangeSummary().getOldValue(oldEOM,p).getValue());
            List depts = root.getList("departments");
            DataObject dept = (DataObject)depts.get(0);
            p = dept.getInstanceProperty("employees");
            List emps = dept.getList(p);
            assertEquals(3, emps.size());
            List oldEmps = (List)ret.getChangeSummary().getOldValue(dept,p).getValue();
            assertEquals(3, oldEmps.size());
            assertEquals(emps.get(0), oldEmps.get(0));
            assertEquals(dept,((DataObject)oldEmps.get(0)).getContainer());
            p = ((DataObject)emps.get(0)).getInstanceProperty("SN");
            assertEquals("E0001",((DataObject)emps.get(0)).get(p));
            assertEquals("E0003",((DataObject)emps.get(1)).get(p));
            assertEquals("E0004",((DataObject)emps.get(2)).get(p));
            assertEquals("E0003",((DataObject)oldEmps.get(2)).get(p));
            assertEquals("E0002",ret.getChangeSummary().getOldValue((DataObject)oldEmps.get(1),p).getValue());
            assertTrue(ret.getChangeSummary().isDeleted((DataObject)oldEmps.get(1)));
            assertTrue(ret.getChangeSummary().isCreated((DataObject)emps.get(2)));

            XMLDocument doc = new XMLDocumentImpl((DataObject)ret,"commonj.sdo","datagraph");
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            _helper.save(doc, out, null);

            InputStream is = new ByteArrayInputStream(out.toByteArray());
            DataGraph ret2 = (DataGraph)_helperContext.getXMLHelper().load(is).getRootObject();

            String retXml = _helperContext.getXMLHelper().save((DataObject)ret, null, "data");
            String ret2Xml = _helperContext.getXMLHelper().save((DataObject)ret2, null, "data");

            assertEquals(retXml, ret2Xml);

            assertTrue(_helperContext.getEqualityHelper().equal(ret.getRootObject(),ret2.getRootObject()));
            ret.getRootObject().getChangeSummary().undoChanges();
            ret2.getRootObject().getChangeSummary().undoChanges();
            assertTrue(_helperContext.getEqualityHelper().equal(ret.getRootObject(),ret2.getRootObject()));
    }
	@Test
    public void testLetterDataGraphParsing()
	throws Exception {
	        final String fileName = PACKAGE + "LetterDataGraph.xml";
	        InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);

            DataGraph ret = (DataGraph)_helperContext.getXMLHelper().load(is).getRootObject();
			DataObject root = ret.getRootObject();
			String xml = _helperContext.getXMLHelper().save(root,root.getType().getURI(),"letters");
			String id = ((SdoType)root.getType()).getId(root);
			String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
            "<ns1:letters" +
            " xmlns:ns1=\"letter.xsd\"" +
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
            " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n" +
			"<date>August 1, 2003</date>\n"+
			"Mutual of Omaha\n"+
			"Wild Kingdom, USA\n"+
			"Dear\n"+
            "<firstName>Casy</firstName>\n"+
			"<lastName>Crocodile</lastName>\n"+
			"Please buy more shark repellent.\n"+
			"Your premium is past due.\n"+
            "</ns1:letters>\n";
			assertLineEquality(expected,xml);
	}

	@Test
    public void testOpenDataGraphParsing()
	throws Exception {
	        final String fileName = PACKAGE + "DataGraphOpen.xml";
	        InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);

            DataGraph ret = (DataGraph)_helperContext.getXMLHelper().load(is).getRootObject();

            System.out.println(_helperContext.getXMLHelper().save((DataObject)ret, null, "graph"));

			DataObject root = ret.getRootObject();
			assertNotNull(root);
			assertEquals("CompanyType",root.getType().getName());
			assertNotNull(ret.getChangeSummary());
			Property p = root.getInstanceProperty("name");
			assertEquals("MegaCorp",root.getString(p));
			assertEquals("ACME",ret.getChangeSummary().getOldValue(root,p).getValue());
			p = root.getInstanceProperty("employeeOfTheMonth");
			DataObject eom = root.getDataObject(p);
			assertEquals("Al Smith",eom.getString("name"));
			DataObject oldEOM = (DataObject)ret.getChangeSummary().getOldValue(root,p).getValue();
			p = eom.getInstanceProperty("name");
			assertEquals("Mary Smith",ret.getChangeSummary().getOldValue(oldEOM,p).getValue());
			List depts = root.getList("departments");
			DataObject dept = (DataObject)depts.get(0);
			p = dept.getInstanceProperty("employees");
			List emps = dept.getList(p);
			assertEquals(3, emps.size());
			List oldEmps = (List)ret.getChangeSummary().getOldValue(dept,p).getValue();
			assertEquals(3, oldEmps.size());
			assertEquals(emps.get(0), oldEmps.get(0));
			assertEquals(dept,((DataObject)oldEmps.get(0)).getContainer());
			p = ((DataObject)emps.get(0)).getInstanceProperty("SN");
			assertEquals("E0001",((DataObject)emps.get(0)).get(p));
			assertEquals("E0003",((DataObject)emps.get(1)).get(p));
			assertEquals("E0004",((DataObject)emps.get(2)).get(p));
			assertEquals("E0003",((DataObject)oldEmps.get(2)).get(p));
			assertEquals("E0002",ret.getChangeSummary().getOldValue((DataObject)oldEmps.get(1),p).getValue());
			assertTrue(ret.getChangeSummary().isDeleted((DataObject)oldEmps.get(1)));
			assertTrue(ret.getChangeSummary().isCreated((DataObject)emps.get(2)));
			assertEquals("xxx",dept.get("openElement"));
			assertEquals("openVal",root.getString("openAttr"));
	}

    @Test
    public void testEmptyDatagraph() throws Exception {
        DataGraph dataGraph = (DataGraph)_helperContext.getDataFactory().create("commonj.sdo","DataGraphType");

        String xml = _helper.save((DataObject)dataGraph, null, "datagraph");
        assertNotNull(xml);

        System.out.println(xml);
    }

    @Test
    public void testCreateDataGraph() {
        DataGraph dataGraph = (DataGraph)_helperContext.getDataFactory().create("commonj.sdo","DataGraphType");

        Type type = _helperContext.getTypeHelper().getType(SequencedOppositeIntf.class);
        dataGraph.createRootObject(type.getURI(), type.getName());

    }

    @Test
    public void testCreateType() {
        DataGraph dataGraph = (DataGraph)_helperContext.getDataFactory().create("commonj.sdo","DataGraphType");

        Type type1 = _helperContext.getTypeHelper().getType(SequencedOppositeIntf.class);
        Type type2 = dataGraph.getType(type1.getURI(), type1.getName());

        assertSame(type1, type2);

    }

}
