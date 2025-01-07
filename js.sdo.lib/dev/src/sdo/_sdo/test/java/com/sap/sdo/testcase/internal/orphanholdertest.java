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

import static com.sap.sdo.api.util.URINamePair.DATAOBJECT;
import static com.sap.sdo.api.util.URINamePair.PROP_XML_ORPHAN_HOLDER;
import static com.sap.sdo.api.util.URINamePair.TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.types.PropertyConstants;
import com.sap.sdo.api.types.TypeConstants;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

/**
 * @author D042774
 *
 */
public class OrphanHolderTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public OrphanHolderTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testOrphanHolderProperty() throws Exception {
        Property orphanHolderProp = _helperContext.getTypeHelper().getOpenContentProperty(PROP_XML_ORPHAN_HOLDER.getURI(), PROP_XML_ORPHAN_HOLDER.getName());
        assertNotNull(orphanHolderProp);

        DataObject typeObj = _helperContext.getDataFactory().create(TYPE.getURI(), TYPE.getName());
        typeObj.set(TypeConstants.NAME, "OrphanType");
        typeObj.set(TypeConstants.URI, "test.sap.com.sdo");
        DataObject propObj = typeObj.createDataObject(TypeConstants.PROPERTY);
        propObj.set(PropertyConstants.NAME, "orphans");
        propObj.set(PropertyConstants.TYPE, _helperContext.getTypeHelper().getType(DATAOBJECT.getURI(), DATAOBJECT.getName()));
        propObj.set(PropertyConstants.CONTAINMENT, true);
        propObj.set(PropertyConstants.MANY, true);
        propObj.set(orphanHolderProp, true);

        assertEquals(true, propObj.getBoolean(orphanHolderProp));
        assertEquals(true, ((SdoProperty)propObj).isOrphanHolder());

        Type type = _helperContext.getTypeHelper().define(typeObj);
        String xsd = _helperContext.getXSDHelper().generate(Collections.singletonList(type));
        System.out.println(xsd);

        List<Type> types = _helperContext.getXSDHelper().define(xsd);
        assertNotNull(types);
        assertEquals(1, types.size());
        List<Property> props = types.get(0).getProperties();
        assertEquals(1, props.size());
        assertEquals(true, props.get(0).get(orphanHolderProp));
        assertEquals(true, ((SdoProperty)props.get(0)).isOrphanHolder());
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

        String xml = _helperContext.getXMLHelper().save(data, "test.sap.com.sdo", "data");
        System.out.println(xml);

        XMLDocument doc = _helperContext.getXMLHelper().load(xml);
        assertNotNull(doc);
        DataObject root = doc.getRootObject();
        assertNotNull(root);

        List<DataObject> emplList = root.getList("employee");
        assertEquals(3, emplList.size());
        for (DataObject employee : emplList) {
            DataObject department = employee.getDataObject("department");
            assertNotNull(department);
            assertTrue("department number was " + department.getInt("number"), department.getInt("number") > 0);
            assertEquals(null, department.getContainer());
        }

        assertEquals(
            xml,
            _helperContext.getXMLHelper().save(root, doc.getRootElementURI(), doc.getRootElementName()));
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

        String xml = _helperContext.getXMLHelper().save(data, "test.sap.com.sdo", "data2");
        System.out.println(xml);

        XMLDocument doc = _helperContext.getXMLHelper().load(xml);
        assertNotNull(doc);
        DataObject root = doc.getRootObject();
        assertNotNull(root);

        List<DataObject> emplList = root.getList("employee");
        assertEquals(3, emplList.size());
        for (DataObject employee : emplList) {
            DataObject department = employee.getDataObject("department");
            assertNotNull(department);
            assertTrue("department number was " + department.getInt("number"), department.getInt("number") > 0);
            assertEquals(null, department.getContainer());
        }

        assertEquals(
            xml,
            _helperContext.getXMLHelper().save(root, doc.getRootElementURI(), doc.getRootElementName()));
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

        String xml = _helperContext.getXMLHelper().save(data, "test.sap.com.sdo", "data2");
        System.out.println(xml);

        XMLDocument doc = _helperContext.getXMLHelper().load(xml);
        assertNotNull(doc);
        DataObject root = doc.getRootObject();
        assertNotNull(root);

        List<DataObject> emplList = root.getList("employee");
        assertEquals(3, emplList.size());
        for (DataObject employee : emplList) {
            DataObject department = employee.getDataObject("department");
            assertNotNull(department);
            assertTrue("department number was " + department.getInt("number"), department.getInt("number") > 0);
            assertEquals(null, department.getContainer());

            DataObject c = department.getDataObject("company");
            assertNotNull(c);
            assertEquals("company", c.getString("name"));
            assertEquals(null, c.getContainer());
        }

        assertEquals(
            xml,
            _helperContext.getXMLHelper().save(root, doc.getRootElementURI(), doc.getRootElementName()));
    }

    @Test
    public void testOrphanWithChangeSummary() throws Exception {
        final String schemaFileName = PACKAGE + "CompanyWithOrphans.xsd";
        InputStream is = getClass().getClassLoader().getResourceAsStream(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(is,null);
        assertNotNull(types);

        final String xmlFileName = PACKAGE + "CompanyWithOrphans.xml";
        URL xmlUrl = getClass().getClassLoader().getResource(xmlFileName);
        XMLDocument doc = _helperContext.getXMLHelper().load(xmlUrl.openStream(), xmlUrl.toString(), null);
        assertNotNull(doc);
        checkDataGraph(doc);

        StringWriter writer = new StringWriter();
        _helperContext.getXMLHelper().save(doc, writer, null);
        String original = writer.toString();
        doc = _helperContext.getXMLHelper().load(original);
        assertNotNull(doc);
        checkDataGraph(doc);

        DataObject datagraph = doc.getRootObject();
        DataObject root = datagraph.getDataObject(datagraph.getType().getProperties().size());
        List<DataObject> departments = root.getList("department");
        for (Iterator<DataObject> it = departments.iterator();it.hasNext();) {
            DataObject department = it.next();
            if ("Sales".equals(department.getString("name"))) {
                it.remove();
            } else if ("Manufacturing".equals(department.getString("name"))) {
                department.set("employee[name='Stefan']/salary", 3);
                DataObject frank = department.createDataObject("employee");
                frank.setString("name", "Frank");
                frank.set("salary", 2);
            }
        }
        writer = new StringWriter();
        _helperContext.getXMLHelper().save(doc, writer, null);
        String graphWithCs = writer.toString();
        System.out.println(graphWithCs);
        doc = _helperContext.getXMLHelper().load(graphWithCs);
        writer = new StringWriter();
        _helperContext.getXMLHelper().save(doc, writer, null);
        assertEquals(graphWithCs, writer.toString());

        doc.getRootObject().getChangeSummary().undoChanges();

        writer = new StringWriter();
        _helperContext.getXMLHelper().save(doc, writer, null);
        assertEquals(original, writer.toString());
    }

    @Test
    public void testDeletingOrphans() throws Exception {
        final String schemaFileName = PACKAGE + "CompanyWithOrphans.xsd";
        InputStream is = getClass().getClassLoader().getResourceAsStream(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(is,null);
        assertNotNull(types);

        final String xmlFileName = PACKAGE + "CompanyWithOrphans.xml";
        URL xmlUrl = getClass().getClassLoader().getResource(xmlFileName);
        XMLDocument doc = _helperContext.getXMLHelper().load(xmlUrl.openStream(), xmlUrl.toString(), null);
        assertNotNull(doc);
        checkDataGraph(doc);

        StringWriter writer = new StringWriter();
        _helperContext.getXMLHelper().save(doc, writer, null);
        String original = writer.toString();
        doc = _helperContext.getXMLHelper().load(original);
        assertNotNull(doc);
        checkDataGraph(doc);

        DataObject datagraph = doc.getRootObject();
        DataObject root = datagraph.getDataObject(datagraph.getType().getProperties().size());
        List<DataObject> departments = root.getList("department");
        for (Iterator<DataObject> it = departments.iterator();it.hasNext();) {
            DataObject department = it.next();
            if ("Sales".equals(department.getString("name"))) {
                department.detach();
            } else if ("Manufacturing".equals(department.getString("name"))) {
                DataObject employee = department.getDataObject("employee[name='Ulf']");
                assertNotNull(employee);
                employee.delete();
            }
        }

        writer = new StringWriter();
        _helperContext.getXMLHelper().save(doc, writer, null);
        String graphWithCs = writer.toString();
        System.out.println(graphWithCs);
        doc = _helperContext.getXMLHelper().load(graphWithCs);
        writer = new StringWriter();
        _helperContext.getXMLHelper().save(doc, writer, null);
        assertEquals(graphWithCs, writer.toString());

        doc.getRootObject().getChangeSummary().undoChanges();

        writer = new StringWriter();
        _helperContext.getXMLHelper().save(doc, writer, null);
        assertEquals(original, writer.toString());
    }

    @Test
    public void testOrphansTree() throws Exception {
        final String schemaFileName = PACKAGE + "CompanyWithOrphans.xsd";
        InputStream is = getClass().getClassLoader().getResourceAsStream(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(is,null);
        assertNotNull(types);

        Type emplType = null;
        for (Type type : types) {
            if ("Employee".equals(type.getName())) {
                emplType = type;
                break;
            }
        }
        assertNotNull(emplType);

        DataObject typeObj = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        typeObj.set(TypeConstants.NAME, "EmployeeContainer");
        typeObj.set(TypeConstants.URI, emplType.getURI());
        DataObject property = typeObj.createDataObject(TypeConstants.PROPERTY);
        property.set(PropertyConstants.NAME, "e");
        property.set(PropertyConstants.TYPE, emplType);
        property.set(PropertyConstants.CONTAINMENT, true);
        property.set(PropertyConstants.MANY, true);
        Type containerType = _helperContext.getTypeHelper().define(typeObj);
        assertNotNull(containerType);
        DataObject container = _helperContext.getDataFactory().create(containerType);
        assertNotNull(container);
        List<DataObject> emplList = container.getList("e");

        final String xmlFileName = PACKAGE + "CompanyWithOrphans.xml";
        URL xmlUrl = getClass().getClassLoader().getResource(xmlFileName);
        XMLDocument doc = _helperContext.getXMLHelper().load(xmlUrl.openStream(), xmlUrl.toString(), null);
        assertNotNull(doc);
        checkDataGraph(doc);

        StringWriter writer = new StringWriter();
        _helperContext.getXMLHelper().save(doc, writer, null);
        String original = writer.toString();
        doc = _helperContext.getXMLHelper().load(original);
        assertNotNull(doc);
        checkDataGraph(doc);

        DataObject datagraph = doc.getRootObject();
        DataObject root = datagraph.getDataObject(datagraph.getType().getProperties().size());
        List<DataObject> departments = root.getList("department");
        for (DataObject department : departments) {
            List<DataObject> employees = department.getList("employee");
            for (DataObject employee : employees) {
                if (employee.getContainer() == null) {
                    emplList.add(employee);
                    if ("Stefan".equals(employee.get("name"))) {
                        employee.set("salary", 3);
                    }
                }
            }
        }

        writer = new StringWriter();
        _helperContext.getXMLHelper().save(doc, writer, null);
        String newOrphan = writer.toString();
        System.out.println(newOrphan);

        doc.getRootObject().getChangeSummary().undoChanges();

        writer = new StringWriter();
        _helperContext.getXMLHelper().save(doc, writer, null);
        assertEquals(original, writer.toString());

        doc = _helperContext.getXMLHelper().load(newOrphan);
        writer = new StringWriter();
        _helperContext.getXMLHelper().save(doc, writer, null);
        assertEquals(newOrphan, writer.toString());

        doc.getRootObject().getChangeSummary().undoChanges();

        writer = new StringWriter();
        _helperContext.getXMLHelper().save(doc, writer, null);
        assertEquals(original, writer.toString());
    }

    private void checkDataGraph(XMLDocument doc) {
        DataObject datagraph = doc.getRootObject();
        DataObject root = datagraph.getDataObject(datagraph.getType().getProperties().size());
        assertNotNull(root);
        assertEquals("Company", root.getType().getName());
        assertEquals("Acme", root.getString("name"));
        List<DataObject> departments = root.getList("department");
        assertEquals(2, departments.size());
        for (DataObject department : departments) {
            assertEquals(null, department.getContainer());
            List<DataObject> employees = department.getList("employee");
            assertEquals(2, employees.size());
            for (DataObject employee : employees) {
                assertEquals(null, employee.getContainer());
            }
        }
        assertEquals(true, datagraph.getChangeSummary().isLogging());
    }
}
