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

import static com.sap.sdo.api.util.URINamePair.PROPERTY;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapXmlHelper;
import com.sap.sdo.api.types.PropertyConstants;
import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.NonSequencedInheritedIntf;
import com.sap.sdo.testcase.typefac.OppositePropsContainA;
import com.sap.sdo.testcase.typefac.OppositePropsContainB;
import com.sap.sdo.testcase.typefac.SequencedOppositeIntf;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

/**
 * @author D042774
 *
 */
public class StreamReaderCompareTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public StreamReaderCompareTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private SapXmlHelper _helper;

    /* (non-Javadoc)
     * @see com.sap.sdo.testcase.SdoTestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        _helper  = (SapXmlHelper)_helperContext.getXMLHelper();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        _helper = null;
    }

    @Test
    public void testReaderMessage1K() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "message_1K.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        URL xmlURL = getClass().getClassLoader().getResource(PACKAGE + "message_1K.xml");
        XMLDocument doc = _helper.load(xmlURL.openStream(), xmlURL.toString(), null);
        assertNotNull(doc);
        compareReaders(doc);
    }

    @Test
    public void testReaderMassData() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "schema4.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        xsdURL = getClass().getClassLoader().getResource(PACKAGE + "schema3.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        URL xmlURL = getClass().getClassLoader().getResource(PACKAGE + "1.xml");
        XMLDocument doc = _helper.load(xmlURL.openStream(), xmlURL.toString(), null);
        assertNotNull(doc);
        compareReaders(doc);
    }

    @Test
    public void testReaderContactList() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "ContactList.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        URL xmlURL = getClass().getClassLoader().getResource(PACKAGE + "ContactList.xml");
        XMLDocument doc = _helper.load(xmlURL.openStream(), xmlURL.toString(), null);
        assertNotNull(doc);
        compareReaders(doc);
    }

    @Test
    public void testReaderSameNames() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "SameNames.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        URL xmlURL = getClass().getClassLoader().getResource(PACKAGE + "SameNames.xml");
        XMLDocument doc = _helper.load(xmlURL.openStream(), xmlURL.toString(), null);
        assertNotNull(doc);
        compareReaders(doc);
    }

    @Test
    public void testReaderElementQualAttributeQual() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "ElementQualAttributeQual.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        URL xmlURL = getClass().getClassLoader().getResource(PACKAGE + "ElementQualAttributeQual.xml");
        XMLDocument doc = _helper.load(xmlURL.openStream(), xmlURL.toString(), null);
        assertNotNull(doc);
        compareReaders(doc);
    }

    @Test
    public void testReaderElementQualAttributeQualExtended() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "ElementQualAttributeQualExtended.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        URL xmlURL = getClass().getClassLoader().getResource(PACKAGE + "ElementQualAttributeQualExtended.xml");
        XMLDocument doc = _helper.load(xmlURL.openStream(), xmlURL.toString(), null);
        assertNotNull(doc);
        compareReaders(doc);
    }

    @Test
    public void testReaderElementQualAttributeQualRestricted() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "ElementQualAttributeQualRestricted.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        URL xmlURL = getClass().getClassLoader().getResource(PACKAGE + "ElementQualAttributeQualRestricted.xml");
        XMLDocument doc = _helper.load(xmlURL.openStream(), xmlURL.toString(), null);
        assertNotNull(doc);
        compareReaders(doc);
    }

    @Test
    public void testReaderElementQualAttributeUnqual() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "ElementQualAttributeUnqual.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        URL xmlURL = getClass().getClassLoader().getResource(PACKAGE + "ElementQualAttributeUnqual.xml");
        XMLDocument doc = _helper.load(xmlURL.openStream(), xmlURL.toString(), null);
        assertNotNull(doc);
        compareReaders(doc);
    }

    @Test
    public void testReaderElementUnqualAttributeQual() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "ElementUnqualAttributeQual.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        URL xmlURL = getClass().getClassLoader().getResource(PACKAGE + "ElementUnqualAttributeQual.xml");
        XMLDocument doc = _helper.load(xmlURL.openStream(), xmlURL.toString(), null);
        assertNotNull(doc);
        compareReaders(doc);
    }

    @Test
    public void testReaderElementUnqualAttributeUnqual() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "ElementUnqualAttributeUnqual.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        URL xmlURL = getClass().getClassLoader().getResource(PACKAGE + "ElementUnqualAttributeUnqual.xml");
        XMLDocument doc = _helper.load(xmlURL.openStream(), xmlURL.toString(), null);
        assertNotNull(doc);
        compareReaders(doc);
    }

    @Test
    public void testReaderDataGraph() throws Exception {
        URL xmlURL = getClass().getClassLoader().getResource(PACKAGE + "DataGraph.xml");
        XMLDocument doc = _helper.load(xmlURL.openStream(), xmlURL.toString(), null);
        assertNotNull(doc);
        compareReaders(doc);
    }

    @Test
    public void testReaderDataGraphNonSequencedWithFewTypeInfos() throws Exception {
        _helperContext.getTypeHelper().getType(NonSequencedInheritedIntf.class);
        URL xmlURL = getClass().getClassLoader().getResource(PACKAGE + "DataGraphNonSequencedWithFewTypeInfos.xml");
        XMLDocument doc = _helper.load(xmlURL.openStream(), xmlURL.toString(), null);
        assertNotNull(doc);
        compareReaders(doc);
    }

    @Test
    public void testReaderDataGraphOpen() throws Exception {
        URL xmlURL = getClass().getClassLoader().getResource(PACKAGE + "DataGraphOpen.xml");
        XMLDocument doc = _helper.load(xmlURL.openStream(), xmlURL.toString(), null);
        assertNotNull(doc);
        compareReaders(doc);
    }

    @Test
    public void testReaderDataGraphSeq() throws Exception {
        URL xmlURL = getClass().getClassLoader().getResource(PACKAGE + "DataGraphSeq.xml");
        XMLDocument doc = _helper.load(xmlURL.openStream(), xmlURL.toString(), null);
        assertNotNull(doc);
        compareReaders(doc);
    }

    @Test
    public void testReaderDataGraphSequencedWithFewTypeInfos() throws Exception {
        URL xmlURL = getClass().getClassLoader().getResource(PACKAGE + "DataGraphSequencedWithFewTypeInfos.xml");
        XMLDocument doc = _helper.load(xmlURL.openStream(), xmlURL.toString(), null);
        assertNotNull(doc);
        compareReaders(doc);
    }

    @Test
    public void testReaderDataGraphWithAlternativeIndex() throws Exception {
        URL xmlURL = getClass().getClassLoader().getResource(PACKAGE + "DataGraphWithAlternativeIndex.xml");
        XMLDocument doc = _helper.load(xmlURL.openStream(), xmlURL.toString(), null);
        assertNotNull(doc);
        compareReaders(doc);
    }

    @Test
    public void testReaderDataGraphWithIdElements() throws Exception {
        URL xmlURL = getClass().getClassLoader().getResource(PACKAGE + "DataGraphWithIdElements.xml");
        XMLDocument doc = _helper.load(xmlURL.openStream(), xmlURL.toString(), null);
        assertNotNull(doc);
        compareReaders(doc);
    }

    @Test
    public void testReaderDataGraphWithLocalChangeSummary() throws Exception {
        URL xmlURL = getClass().getClassLoader().getResource(PACKAGE + "DataGraphWithLocalChangeSummary.xml");
        XMLDocument doc = _helper.load(xmlURL.openStream(), xmlURL.toString(), null);
        assertNotNull(doc);
        compareReaders(doc);
    }

    @Test
    public void testReaderDataGraphWithLocalChangeSummaryQualified() throws Exception {
        URL xmlURL = getClass().getClassLoader().getResource(PACKAGE + "DataGraphWithLocalChangeSummaryQualified.xml");
        XMLDocument doc = _helper.load(xmlURL.openStream(), xmlURL.toString(), null);
        assertNotNull(doc);
        compareReaders(doc);
    }

    @Test
    public void testReaderDataGraphWithRelativePath() throws Exception {
        URL xmlURL = getClass().getClassLoader().getResource(PACKAGE + "DataGraphWithRelativPath.xml");
        XMLDocument doc = _helper.load(xmlURL.openStream(), xmlURL.toString(), null);
        assertNotNull(doc);
        compareReaders(doc);
    }

    @Test
    public void testOppositeContainment() throws Exception {
        OppositePropsContainA a =
            (OppositePropsContainA)_helperContext.getDataFactory().create(OppositePropsContainA.class);
        OppositePropsContainB b =
            (OppositePropsContainB)_helperContext.getDataFactory().create(OppositePropsContainB.class);
        assertNotNull(a);
        assertNotNull(b);
        ((DataObjectDecorator)a).getInstance().setReadOnlyMode(false);
        a.getBs().add(b);
        ((DataObjectDecorator)a).getInstance().setReadOnlyMode(true);
        assertSame(a, b.getA());

        Type aType = ((DataObject)a).getType();
        DataObject propObj = _helperContext.getDataFactory().create(PROPERTY.getURI(), PROPERTY.getName());
        propObj.set(PropertyConstants.NAME, "a");
        propObj.set(PropertyConstants.TYPE, aType);
        propObj.set(PropertyConstants.CONTAINMENT, true);
        Property prop = _helperContext.getTypeHelper().defineOpenContentProperty(aType.getURI(), propObj);

        compareReaders((DataObject)a, aType.getURI(), prop.getName());
    }

    @Test
    public void testOppositeContainmentSequenced() throws Exception {
        SequencedOppositeIntf outer = (SequencedOppositeIntf)_helperContext.getDataFactory().create(SequencedOppositeIntf.class);
        SequencedOppositeIntf seqIntf = (SequencedOppositeIntf)_helperContext.getDataFactory().create(SequencedOppositeIntf.class);
        seqIntf.setSv(outer);
        seqIntf.setName("name");

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<ns1:outer xmlns:ns1=\"com.sap.sdo.testcase.typefac\""
            + " xsi:type=\"ns1:SequencedOppositeIntf\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"
            + "  <mv>\n"
            + "    <name>name</name>\n"
            + "  </mv>\n"
            + "</ns1:outer>\n";

        compareReaders((DataObject)outer, "com.sap.sdo.testcase.typefac", "outer");
    }

    @Test
    public void testOrphanRendering() throws Exception {
        final String schemaFileName = PACKAGE + "OrphanType.xsd";
        InputStream is = getClass().getClassLoader().getResourceAsStream(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(is,null);
        assertNotNull(types);

        Type orphanType = _helperContext.getTypeHelper().getType("test.sap.com.sdo", "OrphanType");
        assertNotNull(orphanType);
        Type departmentType = _helperContext.getTypeHelper().getType("test.sap.com.sdo", "DepartmentType");
        assertNotNull(departmentType);
        Type employeeType = _helperContext.getTypeHelper().getType("test.sap.com.sdo", "EmployeeType");
        assertNotNull(employeeType);

        DataObject deptA = _helperContext.getDataFactory().create(departmentType);
        deptA.setString("name", "department A");
        deptA.setString("location", "location A");
        deptA.setInt("number", 1);

        DataObject deptB = _helperContext.getDataFactory().create(departmentType);
        deptB.setString("name", "department B");
        deptB.setString("location", "location B");
        deptB.setString("description", "bbbbb");
        deptB.setInt("number", 2);

        DataObject e1 = _helperContext.getDataFactory().create(employeeType);
        e1.setString("name", "employee 1");
        e1.setString("SN", "E0001");
        e1.setBoolean("manager", false);
        e1.setDataObject("department", deptA);

        DataObject e2 = _helperContext.getDataFactory().create(employeeType);
        e2.setString("name", "employee 2");
        e2.setString("SN", "E0002");
        e2.setBoolean("manager", false);
        e2.setDataObject("department", deptB);

        DataObject e3 = _helperContext.getDataFactory().create(employeeType);
        e3.setString("name", "employee 3");
        e3.setString("SN", "E0003");
        e3.setBoolean("manager", false);
        e3.setDataObject("department", deptA);

        DataObject data = _helperContext.getDataFactory().create(orphanType);
        List<DataObject> employees = data.getList("employee");
        employees.add(e1);
        employees.add(e2);
        employees.add(e3);

        compareReaders(data, "test.sap.com.sdo", "data");
    }

    @Test
    public void testOrphanRendering2() throws Exception {
        final String schemaFileName = PACKAGE + "OrphanType.xsd";
        InputStream is = getClass().getClassLoader().getResourceAsStream(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(is,null);
        assertNotNull(types);

        Type orphanType = _helperContext.getTypeHelper().getType("test.sap.com.sdo", "OrphanType2");
        assertNotNull(orphanType);
        Type departmentType = _helperContext.getTypeHelper().getType("test.sap.com.sdo", "DepartmentType");
        assertNotNull(departmentType);
        Type employeeType = _helperContext.getTypeHelper().getType("test.sap.com.sdo", "EmployeeType");
        assertNotNull(employeeType);

        DataObject deptA = _helperContext.getDataFactory().create(departmentType);
        deptA.setString("name", "department A");
        deptA.setString("location", "location A");
        deptA.setInt("number", 1);

        DataObject deptB = _helperContext.getDataFactory().create(departmentType);
        deptB.setString("name", "department B");
        deptB.setString("location", "location B");
        deptB.setString("description", "bbbbb");
        deptB.setInt("number", 2);

        DataObject e1 = _helperContext.getDataFactory().create(employeeType);
        e1.setString("name", "employee 1");
        e1.setString("SN", "E0001");
        e1.setBoolean("manager", false);
        e1.setDataObject("department", deptA);

        DataObject e2 = _helperContext.getDataFactory().create(employeeType);
        e2.setString("name", "employee 2");
        e2.setString("SN", "E0002");
        e2.setBoolean("manager", false);
        e2.setDataObject("department", deptB);

        DataObject e3 = _helperContext.getDataFactory().create(employeeType);
        e3.setString("name", "employee 3");
        e3.setString("SN", "E0003");
        e3.setBoolean("manager", false);
        e3.setDataObject("department", deptA);

        DataObject data = _helperContext.getDataFactory().create(orphanType);
        List<DataObject> employees = data.getList("employee");
        employees.add(e1);
        employees.add(e2);
        employees.add(e3);

        compareReaders(data, "test.sap.com.sdo", "data");
    }

    @Test
    public void testDifferentOrphanRendering() throws Exception {
        final String schemaFileName = PACKAGE + "OrphanType.xsd";
        InputStream is = getClass().getClassLoader().getResourceAsStream(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(is,null);
        assertNotNull(types);

        Type orphanType = _helperContext.getTypeHelper().getType("test.sap.com.sdo", "OrphanType2");
        assertNotNull(orphanType);
        Type companyType = _helperContext.getTypeHelper().getType("test.sap.com.sdo", "CompanyType");
        assertNotNull(companyType);
        Type departmentType = _helperContext.getTypeHelper().getType("test.sap.com.sdo", "DepartmentType");
        assertNotNull(departmentType);
        Type employeeType = _helperContext.getTypeHelper().getType("test.sap.com.sdo", "EmployeeType");
        assertNotNull(employeeType);

        DataObject company = _helperContext.getDataFactory().create(companyType);
        company.setString("name", "company");

        DataObject deptA = _helperContext.getDataFactory().create(departmentType);
        deptA.setString("name", "department A");
        deptA.setString("location", "location A");
        deptA.setInt("number", 1);
        deptA.setDataObject("company", company);

        DataObject deptB = _helperContext.getDataFactory().create(departmentType);
        deptB.setString("name", "department B");
        deptB.setString("location", "location B");
        deptB.setString("description", "bbbbb");
        deptB.setInt("number", 2);
        deptB.setDataObject("company", company);

        DataObject e1 = _helperContext.getDataFactory().create(employeeType);
        e1.setString("name", "employee 1");
        e1.setString("SN", "E0001");
        e1.setBoolean("manager", false);
        e1.setDataObject("department", deptA);

        DataObject e2 = _helperContext.getDataFactory().create(employeeType);
        e2.setString("name", "employee 2");
        e2.setString("SN", "E0002");
        e2.setBoolean("manager", false);
        e2.setDataObject("department", deptB);

        DataObject e3 = _helperContext.getDataFactory().create(employeeType);
        e3.setString("name", "employee 3");
        e3.setString("SN", "E0003");
        e3.setBoolean("manager", false);
        e3.setDataObject("department", deptA);

        DataObject data = _helperContext.getDataFactory().create(orphanType);
        List<DataObject> employees = data.getList("employee");
        employees.add(e1);
        employees.add(e2);
        employees.add(e3);

        compareReaders(data, "test.sap.com.sdo", "data");
    }
}
