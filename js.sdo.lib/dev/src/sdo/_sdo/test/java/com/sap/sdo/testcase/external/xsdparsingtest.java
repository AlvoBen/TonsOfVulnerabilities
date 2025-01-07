package com.sap.sdo.testcase.external;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.sap.sdo.api.helper.SapXmlDocument;
import com.sap.sdo.api.helper.SapXmlHelper;
import com.sap.sdo.api.helper.SapXsdHelper;
import com.sap.sdo.api.types.schema.Element;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.types.simple.JavaSimpleType;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.helper.XSDHelper;

public class XsdParsingTest extends SdoTestCase {
	/**
     * @param pHelperContext
     */
    public XsdParsingTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }
    @Test
    public void testCompanyExample() throws UnsupportedEncodingException {
        final String schemaFileName = PACKAGE + "companyExample.xsd";
        InputStream is = getClass().getClassLoader().getResourceAsStream(schemaFileName);
    	_helperContext.getXSDHelper().define(is,null);
    	Type t = _helperContext.getTypeHelper().getType("company.xsd","CompanyType");
    	assertNotNull("could not find type",t);
    	String appInfo = _helperContext.getXSDHelper().getAppinfo(t, "MySource");
    	assertNotNull(appInfo);
    	assertLineEquality(
            "<xsd:appinfo" +
            " source=\"MySource\"" +
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
            " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"" +
            ">\n" +
            "\t\taaaaa\n" +
            "\t\t<blah app=\"1\"></blah>\n" +
            "\t\t<blah2> sfejkjfes </blah2>\n" +
            "\t</xsd:appinfo>\n",
            appInfo);
    	Property p;
    	p = t.getProperty("name");
    	appInfo = _helperContext.getXSDHelper().getAppinfo(p,null);
    	assertNotNull(appInfo);
    	assertLineEquality(
            "<xsd:appinfo" +
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
            " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"" +
            ">\n" +
            "\t\taaaaa\n" +
            "\t\t<blah app=\"1\"></blah>\n" +
            "\t\t<blah2> sfejkjfes </blah2>\n" +
            "\t</xsd:appinfo>\n",
            appInfo);
    	assertNotNull(p);
    	assertFalse("name should be single valued",p.isMany());
    	assertFalse("name should not be containment",p.isContainment());
    	p = t.getProperty("departments");
    	assertNotNull(p);
    	assertTrue("departments should have isMany",p.isMany());
    	assertTrue("departments should be containment",p.isContainment());
    	assertEquals(p.getType(),_helperContext.getTypeHelper().getType("company.xsd","DepartmentType"));
    	Type departmentType = p.getType();
    	assertNull(_helperContext.getXSDHelper().getAppinfo(departmentType,null));
    	p = departmentType.getProperty("name");
    	assertNotNull(p);
    	assertFalse("name should be single valued",p.isMany());
    	assertFalse("name should not be containment",p.isContainment());
    	p = departmentType.getProperty("employees");
    	assertTrue("employees should have isMany",p.isMany());
    	assertTrue("employees should be containment",p.isContainment());
    	assertEquals(p.getType(),_helperContext.getTypeHelper().getType("company.xsd","EmployeeType"));

    	String expected =
    		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    		"<xsd:schema targetNamespace=\"company.xsd\"" +
            " xmlns:tns=\"company.xsd\" xmlns:sdox=\"commonj.sdo/xml\"" +
    		" xmlns:sdoj=\"commonj.sdo/java\"" +
    		" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"" +
            ">\n" +
    		"<xsd:element name=\"company\" type=\"tns:CompanyType\"/>\n" +
    		"<xsd:complexType name=\"CompanyType\">\n" +
    		"    <xsd:sequence>\n" +
    		"        <xsd:element name=\"departments\" type=\"tns:DepartmentType\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
    		"    </xsd:sequence>\n" +
    		"    <xsd:attribute name=\"name\" type=\"xsd:string\"/>\n" +
    		"    <xsd:attribute name=\"employeeOfTheMonth\" type=\"xsd:string\"/>\n" +
    		"</xsd:complexType>\n" +
    		"<xsd:complexType name=\"DepartmentType\">\n" +
    		"    <xsd:sequence>\n" +
    		"        <xsd:element name=\"employees\" type=\"tns:EmployeeType\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
    		"    </xsd:sequence>\n" +
    		"    <xsd:attribute name=\"name\" type=\"xsd:string\"/>\n" +
    		"    <xsd:attribute name=\"location\" type=\"xsd:string\"/>\n" +
    		"    <xsd:attribute name=\"number\" type=\"xsd:int\"/>\n" +
    		"</xsd:complexType>\n" +
    		"<xsd:complexType name=\"EmployeeType\">\n" +
    		"    <xsd:attribute name=\"name\" type=\"xsd:string\"/>\n" +
    		"    <xsd:attribute name=\"SN\" type=\"xsd:ID\"/>\n" +
    		"    <xsd:attribute name=\"manager\" type=\"xsd:boolean\"/>\n" +
    		"</xsd:complexType>\n" +
    		"</xsd:schema>\n";

    	List<Type> types = new ArrayList<Type>(1);
    	types.add(t);
    	Map<String,String> map = new HashMap<String,String>();
    	String xsd = _helperContext.getXSDHelper().generate(types, map);
    	assertLineEquality(expected,xsd);

        is = new ByteArrayInputStream(xsd.getBytes("UTF-8"));
    	_helperContext.getXSDHelper().define(is,null);
	}
	@Test
    public void testCompanyIncludeExample() throws Exception {
        final String schemaFileName = PACKAGE + "companyIncludeExample.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
    	_helperContext.getXSDHelper().define(url.openStream(),url.toString());
    	Type t = _helperContext.getTypeHelper().getType("companyIncludeExample.xsd","CompanyType");
    	assertNotNull("could not find type",t);
    	Property p;
    	p = t.getProperty("name");
    	assertNotNull(p);
    	assertFalse("name should be single valued",p.isMany());
    	assertFalse("name should not be containment",p.isContainment());
    	p = t.getProperty("departments");
    	assertNotNull(p);
    	assertTrue("departments should have isMany",p.isMany());
    	assertTrue("departments should be containment",p.isContainment());
    	assertEquals(p.getType(),_helperContext.getTypeHelper().getType("companyIncludeExample.xsd","DepartmentType"));
    	Type departmentType = p.getType();
    	p = departmentType.getProperty("name");
    	assertNotNull(p);
    	assertFalse("name should be single valued",p.isMany());
    	assertFalse("name should not be containment",p.isContainment());
    	p = departmentType.getProperty("employees");
    	assertTrue("employees should have isMany",p.isMany());
    	assertTrue("employees should be containment",p.isContainment());
    	assertEquals(p.getType(),_helperContext.getTypeHelper().getType("companyIncludeExample.xsd","EmployeeType"));

	}
	@Test
    public void testCompanyImportExample() throws Exception {
        final String schemaFileName = PACKAGE + "companyImportExample.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(),url.toString());
    	Type t = _helperContext.getTypeHelper().getType("companyImportExample.xsd","CompanyType");
    	assertNotNull("could not find type",t);
    	Property p;
    	p = t.getProperty("name");
    	assertNotNull(p);
    	assertFalse("name should be single valued",p.isMany());
    	assertFalse("name should not be containment",p.isContainment());
    	p = t.getProperty("departments");
    	assertNotNull(p);
    	assertTrue("departments should have isMany",p.isMany());
    	assertTrue("departments should be containment",p.isContainment());
    	assertEquals(p.getType(),_helperContext.getTypeHelper().getType("companyImportExample.xsd","DepartmentType"));
    	Type departmentType = p.getType();
    	p = departmentType.getProperty("name");
    	assertNotNull(p);
    	assertFalse("name should be single valued",p.isMany());
    	assertFalse("name should not be containment",p.isContainment());
    	p = departmentType.getProperty("employees");
    	assertTrue("employees should have isMany",p.isMany());
    	assertTrue("employees should be containment",p.isContainment());
    	assertEquals(p.getType(),_helperContext.getTypeHelper().getType("employeeImportedExample.xsd","EmployeeType"));

    	String expected =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    		"<xsd:schema targetNamespace=\"companyImportExample.xsd\"" +
            " xmlns:tns=\"companyImportExample.xsd\"" +
            " xmlns:ns1=\"employeeImportedExample.xsd\"" +
            " xmlns:sdox=\"commonj.sdo/xml\"" +
            " xmlns:sdoj=\"commonj.sdo/java\"" +
            " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"" +
            ">\n" +
    		"<xsd:import schemaLocation=\"employeeImportedExample.xsd\" namespace=\"employeeImportedExample.xsd\"/>\n" +
    		"    <xsd:element name=\"company\" type=\"tns:CompanyType\"/>\n" +
    		"    <xsd:complexType name=\"CompanyType\">\n" +
    		"        <xsd:sequence>\n" +
    		"            <xsd:element name=\"departments\" type=\"tns:DepartmentType\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
    		"        </xsd:sequence>\n" +
    		"        <xsd:attribute name=\"name\" type=\"xsd:string\"/>\n" +
    		"        <xsd:attribute name=\"employeeOfTheMonth\" type=\"xsd:string\"/>\n" +
    		"    </xsd:complexType>\n" +
    		"    <xsd:complexType name=\"DepartmentType\">\n" +
    		"        <xsd:sequence>\n" +
    		"            <xsd:element name=\"employees\" type=\"ns1:EmployeeType\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
    		"        </xsd:sequence>\n" +
    		"        <xsd:attribute name=\"name\" type=\"xsd:string\"/>\n" +
    		"        <xsd:attribute name=\"location\" type=\"xsd:string\"/>\n" +
    		"        <xsd:attribute name=\"number\" type=\"xsd:int\"/>\n" +
    		"    </xsd:complexType>\n" +
    		"</xsd:schema>\n";
    	List<Type> types = new ArrayList<Type>(1);
    	types.add(t);
    	Map<String,String> map = new HashMap<String,String>();
    	map.put("employeeImportedExample.xsd", "employeeImportedExample.xsd");
    	String xsd = _helperContext.getXSDHelper().generate(types, map);
        assertLineEquality(expected,xsd);
        InputStream is = new ByteArrayInputStream(xsd.getBytes("UTF-8"));
    	_helperContext.getXSDHelper().define(is,url.toString());
	}
	@Test
    public void testLetterExample() {
        final String schemaFileName = PACKAGE + "letterExample.xsd";
        InputStream is = getClass().getClassLoader().getResourceAsStream(schemaFileName);
    	_helperContext.getXSDHelper().define(is,null);
    	Type t = _helperContext.getTypeHelper().getType("letter.xsd","FormLetter");
    	assertNotNull("could not find type",t);
    	assertTrue("should be sequenced",t.isSequenced());
    	Property p;
    	p = t.getProperty("date");
    	assertNotNull(p);
    	assertFalse("date should single-valued",p.isMany());
    	assertFalse("date should not be containment",p.isContainment());
	}
	@Test
    public void testPOExample() {
        final String schemaFileName = PACKAGE + "PurchaseOrder.xsd";
        InputStream is = getClass().getClassLoader().getResourceAsStream(schemaFileName);
    	_helperContext.getXSDHelper().define(is,null);
    	Type t = _helperContext.getTypeHelper().getType("","PurchaseOrder");
    	assertNotNull(t);
    	Property p = _helperContext.getXSDHelper().getGlobalProperty("","PurchaseOrder",true);
    	assertNotNull(p);
    	assertSame(t,p.getType());
    	assertEquals(t.getProperties().size(), 9);
    	p = t.getProperty("Actions");
    	assertNotNull(p);
    	Type actionsType = p.getType();
    	assertEquals(actionsType.getName(), "ActionsType");
    	assertEquals(actionsType.getProperties().size(),1);
    	Property actionProp = actionsType.getProperty("Action");
    	assertTrue(actionProp.isMany());
    	Type actionType = actionProp.getType();
    	assertEquals("ActionsType+Action", actionType.getName());
    }
	@Test
    public void testListsExample() throws Exception {
        final String schemaFileName = PACKAGE + "SimpleTypeList.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(),url.toString());
    	Type t = _helperContext.getTypeHelper().getType("lists.xsd","Container");
    	assertNotNull("could not find type",t);
    	Property p = t.getProperty("x");
    	assertNotNull(p);
    	assertEquals(List.class, p.getType().getInstanceClass());
	}
	@Test
    public void testUnionExample() throws Exception {
        final String schemaFileName = PACKAGE + "SimpleTypeUnion.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(),url.toString());
    	Type t = _helperContext.getTypeHelper().getType("union.xsd","Container");
    	assertNotNull("could not find type",t);
    	Property p = t.getProperty("x");
    	assertEquals(Object.class, p.getType().getInstanceClass());
	}
	@Test
    public void testNillable() throws Exception {
        final String schemaFileName = PACKAGE + "NillableType.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(),url.toString());
    	Type t = _helperContext.getTypeHelper().getType("nillable.xsd","Container");
    	assertNotNull("could not find type",t);
    	Property p = t.getProperty("byte");
    	assertEquals(byte.class,p.getType().getInstanceClass());
    	p = t.getProperty("byteObj");
    	assertEquals(Byte.class,p.getType().getInstanceClass());
    	p = t.getProperty("bool");
    	assertEquals(boolean.class,p.getType().getInstanceClass());
    	p = t.getProperty("boolObj");
    	assertEquals(Boolean.class,p.getType().getInstanceClass());
    	p = t.getProperty("int");
    	assertEquals(int.class,p.getType().getInstanceClass());
    	p = t.getProperty("intObj");
    	assertEquals(Integer.class,p.getType().getInstanceClass());
    	p = t.getProperty("long");
    	assertEquals(long.class,p.getType().getInstanceClass());
    	p = t.getProperty("longObj");
    	assertEquals(Long.class,p.getType().getInstanceClass());
    	p = t.getProperty("float");
    	assertEquals(float.class,p.getType().getInstanceClass());
    	p = t.getProperty("floatObj");
    	assertEquals(Float.class,p.getType().getInstanceClass());
    	p = t.getProperty("double");
    	assertEquals(double.class,p.getType().getInstanceClass());
    	p = t.getProperty("doubleObj");
    	assertEquals(Double.class,p.getType().getInstanceClass());
	}
    @Test
    public void testNillableRestricted() throws Exception {
        final String schemaFileName = PACKAGE + "NillableRestrictedType.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(),url.toString());
        Type t = _helperContext.getTypeHelper().getType("nillableRestricted.xsd","Container");
        assertNotNull("could not find type",t);
        Property p = t.getProperty("int");
        assertEquals(int.class,p.getType().getInstanceClass());
        p = t.getProperty("intObj");
        assertEquals(Integer.class,p.getType().getInstanceClass());
        p = t.getProperty("long");
        assertEquals(long.class,p.getType().getInstanceClass());
        p = t.getProperty("longObj");
        assertEquals(Long.class,p.getType().getInstanceClass());
    }
	@Test
    public void testSimpleContentExample() throws Exception {
        final String schemaFileName = PACKAGE + "SimpleContentType.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(),url.toString());
    	Type t = _helperContext.getTypeHelper().getType("simpleContent.xsd","SimpleContentType");
    	assertNotNull("could not find type",t);
    	Property p = t.getProperty("value");
    	assertNotNull(p);
	}
	@Test
    public void testInheritExample() throws Exception {
        final String schemaFileName = PACKAGE + "companyInheritenceExample.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(),url.toString());
    	Type t = _helperContext.getTypeHelper().getType("inherit.xsd","SalariedEmployeeType");
    	assertNotNull("could not find type",t);
    	Type t2 = _helperContext.getTypeHelper().getType("inherit.xsd","EmployeeType");
    	assertNotNull("could not find type",t2);
    	assertEquals(t.getBaseTypes().get(0),t2);
	}
	@Test
    public void testComplexRestrictsComplex() throws Exception {
        final String schemaFileName = PACKAGE + "ComplexRestrictsComplex.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(),url.toString());
    	Type t = _helperContext.getTypeHelper().getType("complex-restricts-complex.xsd","DareObasanjo");
    	assertNotNull("could not find type",t);
    	Type bt = (Type)t.getBaseTypes().get(0);
    	assertEquals("XML-Deviant",bt.getName());
    	assertEquals(t.getProperties().size(), bt.getProperties().size());
    }
	@Test
    public void testChangeSummaryType() throws Exception {
        final String schemaFileName = PACKAGE + "ChangeSummaryType.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(),url.toString());
    	Type t = _helperContext.getTypeHelper().getType("ipoMessage.xsd","PurchaseOrderMessageType");
    	Property p = t.getProperty("changeSummary");
    	assertEquals(_helperContext.getTypeHelper().getType("commonj.sdo","ChangeSummaryType"),p.getType());
    	DataObject o = _helperContext.getDataFactory().create(t);
    	System.out.println(o.get("changeSummary"));
    }
	@Test
    public void testGroups() throws Exception {
        final String schemaFileName = PACKAGE + "Groups.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(),url.toString());
    	Type t = _helperContext.getTypeHelper().getType("groups.xsd","UsesGroup");
    	assertEquals(5,t.getProperties().size());
	}
	@Test
    public void testAttributeGroups() throws Exception {
        final String schemaFileName = PACKAGE + "AttributeGroups.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(),url.toString());
    	Type t = _helperContext.getTypeHelper().getType("attrgroups.xsd","UsesGroup");
    	assertEquals(5,t.getProperties().size());
	}
	@Test
    public void testCompositors() throws Exception {
        final String schemaFileName = PACKAGE + "Compositor.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(),url.toString());
    	Type t = _helperContext.getTypeHelper().getType("compositors.xsd","SimpleSequence");
    	assertFalse(t.isSequenced());
    	assertFalse(t.getProperty("x").isMany());
    	assertTrue(t.getProperty("y").isMany());
    	assertFalse(t.getProperty("z").isMany());
    	t = _helperContext.getTypeHelper().getType("compositors.xsd","RepeatingSequence");
    	assertTrue(t.isSequenced());
    	assertTrue(t.getProperty("x").isMany());
    	assertTrue(t.getProperty("y").isMany());
    	assertTrue(t.getProperty("z").isMany());
    	t = _helperContext.getTypeHelper().getType("compositors.xsd","RepeatingSequenceWithOneElement");
    	assertFalse(t.isSequenced());
    	assertTrue(t.getProperty("y").isMany());
    	t = _helperContext.getTypeHelper().getType("compositors.xsd","SimpleAll");
    	assertTrue(t.isSequenced());
    	assertFalse(t.getProperty("x").isMany());
    	assertFalse(t.getProperty("y").isMany());
    	assertFalse(t.getProperty("z").isMany());
    	t = _helperContext.getTypeHelper().getType("compositors.xsd","SequenceContainsChoice");
    	assertFalse(t.isSequenced());
    	assertFalse(t.getProperty("x").isMany());
    	assertTrue(t.getProperty("y").isMany());
    	assertFalse(t.getProperty("z").isMany());
    	t = _helperContext.getTypeHelper().getType("compositors.xsd","RepeatingSequenceContainsChoice");
    	assertTrue(t.isSequenced());
    	assertTrue(t.getProperty("x").isMany());
    	assertTrue(t.getProperty("y").isMany());
    	assertTrue(t.getProperty("z").isMany());
    	t = _helperContext.getTypeHelper().getType("compositors.xsd","ChoiceContainsRepeatingSequence");
    	assertTrue(t.isSequenced());
    	assertTrue(t.getProperty("x").isMany());
    	assertTrue(t.getProperty("y").isMany());
    	assertFalse(t.getProperty("z").isMany());
    }

    @Test
    public void testExtendedSimpleType() throws Exception {
        final String schemaFileName = PACKAGE + "extendedSimpleType.xsd";
        InputStream is = getClass().getClassLoader().getResourceAsStream(schemaFileName);
        final XSDHelper xsdHelper = _helperContext.getXSDHelper();
        xsdHelper.define(is,null);
        final TypeHelper typeHelper = _helperContext.getTypeHelper();
        Type extendedType = typeHelper.getType("ext.xsd","ExtendedSimpleType");
        assertNotNull("could not find type",extendedType);
        assertNotNull(extendedType.getProperties());
        assertEquals(3, extendedType.getProperties().size());
        Property prop = extendedType.getProperty("value");
        assertNotNull(prop);
        assertSame(JavaSimpleType.INTEGER, prop.getType());

        assertEquals(true, xsdHelper.isAttribute(extendedType.getProperty("meta1")));
        assertEquals(true, xsdHelper.isAttribute(extendedType.getProperty("meta2")));

        Type restrictedType = typeHelper.getType("ext.xsd","RestrictedSimpleType");
        assertNotNull("could not find type",restrictedType);
        assertNotNull(restrictedType.getProperties());
        assertEquals(3, restrictedType.getProperties().size());
        Property prop2 = restrictedType.getProperty("value");
        assertNotNull(prop2);
        assertSame(JavaSimpleType.INTEGER, prop2.getType());

        assertEquals(true, xsdHelper.isAttribute(extendedType.getProperty("meta1")));
        assertEquals(true, xsdHelper.isAttribute(extendedType.getProperty("meta2")));

        Property containerProp = xsdHelper.getGlobalProperty("ext.xsd", "container", true);
        Type containerType = containerProp.getType();
        Property extensionProp = containerType.getProperty("extended");
        Property restrictionProp = containerType.getProperty("restricted");
        assertSame(extendedType, extensionProp.getType());
        assertSame(restrictedType, restrictionProp.getType());
    }

    @Test
    public void testTreeXsd() throws Exception {
        final String schemaFileName = PACKAGE + "tree.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        final XSDHelper xsdHelper = _helperContext.getXSDHelper();
        xsdHelper.define(url.openStream(), url.toString());

        Property globalNodeProp = xsdHelper.getGlobalProperty("com.sap.test.tree", "node", true);
        assertEquals(true, xsdHelper.isElement(globalNodeProp));
        assertEquals("node", globalNodeProp.getName());

        Type nodeType = globalNodeProp.getType();
        Property nodeProp = nodeType.getProperty("node");
        assertEquals(true, xsdHelper.isElement(nodeProp));
        assertEquals(true, nodeProp.isMany());
        assertEquals("node", nodeProp.getName());

        assertSame(nodeType, nodeProp.getType());
        Property nameProp = nodeType.getProperty("name");
        assertEquals(true, xsdHelper.isAttribute(nameProp));
        assertEquals(false, nameProp.isMany());
        assertEquals("name", nameProp.getName());
    }

    @Test
    public void testMergeProperties() throws Exception {
        final String schemaFileName = PACKAGE + "mergeProperties.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        final XSDHelper xsdHelper = _helperContext.getXSDHelper();
        xsdHelper.define(url.openStream(), url.toString());

        final TypeHelper typeHelper = _helperContext.getTypeHelper();
        Type baseType = typeHelper.getType("com.sap.test.merge","base");
        assertEquals(true, baseType.isSequenced());
        Property merge1Prop = baseType.getProperty("merge1");
        Property merge2Prop = baseType.getProperty("merge2");
        Property merge3Prop = baseType.getProperty("merge3");
        Property merge4Prop = baseType.getProperty("merge4");
        Property merge5Prop = baseType.getProperty("merge5");
        assertEquals(true, merge1Prop.isMany());
        assertEquals(true, merge2Prop.isMany());
        assertEquals(true, merge3Prop.isMany());
        assertEquals(true, merge4Prop.isMany());
        assertEquals(false, merge5Prop.isMany());

        Type extensionType = typeHelper.getType("com.sap.test.merge","extension");
        assertEquals(true, extensionType.isSequenced());
        Property extensionMerge1Prop = extensionType.getProperty("merge1");
        Property extensionMerge2Prop = extensionType.getProperty("merge2");
        Property extensionMerge3Prop = extensionType.getProperty("merge3");
        Property extensionMerge4Prop = extensionType.getProperty("merge4");
        Property extensionMerge5Prop = extensionType.getProperty("merge5");
        Property extensionMerge6Prop = extensionType.getProperty("merge6");

        assertEquals(merge1Prop, extensionMerge1Prop);
        assertEquals(merge2Prop, extensionMerge2Prop);
        assertEquals(merge3Prop, extensionMerge3Prop);
        assertEquals(merge4Prop, extensionMerge4Prop);
        assertEquals(merge5Prop, extensionMerge5Prop);
        assertEquals(false, extensionMerge6Prop.isMany());
    }

    @Test
    public void testMergePropertiesNonSequenced() throws Exception {
        final String schemaFileName = PACKAGE + "mergeProperties.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        final XSDHelper xsdHelper = _helperContext.getXSDHelper();
        xsdHelper.define(url.openStream(), url.toString());

        final TypeHelper typeHelper = _helperContext.getTypeHelper();
        Type baseType = typeHelper.getType("com.sap.test.merge","base2");
        assertEquals(false, baseType.isSequenced());
        Property merge1Prop = baseType.getProperty("merge1");
        Property merge2Prop = baseType.getProperty("merge2");
        Property merge3Prop = baseType.getProperty("merge3");
        assertEquals(true, merge1Prop.isMany());
        assertEquals(false, merge2Prop.isMany());
        assertEquals(true, merge3Prop.isMany());

        Type extensionType = typeHelper.getType("com.sap.test.merge","extension2");
        assertEquals(false, extensionType.isSequenced());
        Property extensionMerge1Prop = extensionType.getProperty("merge1");
        Property extensionMerge2Prop = extensionType.getProperty("merge2");
        Property extensionMerge3Prop = extensionType.getProperty("merge3");
        Property extensionMerge4Prop = extensionType.getProperty("merge4");

        assertEquals(merge1Prop, extensionMerge1Prop);
        assertEquals(merge2Prop, extensionMerge2Prop);
        assertEquals(merge3Prop, extensionMerge3Prop);
        assertEquals(false, extensionMerge4Prop.isMany());
    }

    @Test
    public void testIllegalComplexContent() throws Exception {
        final String schemaFileName = PACKAGE + "illegalComplexContent.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        final XSDHelper xsdHelper = _helperContext.getXSDHelper();
        try {
            xsdHelper.define(url.openStream(), url.toString());
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid complexContent com.sap.test.error#+container", e.getMessage());
        }
    }

    @Test
    public void testElementFormDefault() {
        String schema = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                      + "<xsd:schema targetNamespace=\"com.sap.sdo.testcase\""
                      + " xmlns:tns=\"com.sap.sdo.testcase\""
                      + " xmlns:sdox=\"commonj.sdo/xml\""
                      + " xmlns:sdoj=\"commonj.sdo/java\""
                      + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
                      + " elementFormDefault=\"qualified\""
                      + ">\n"
                      + "<xsd:complexType name=\"ElementFormDefaultType\"></xsd:complexType>\n"
                      + "</xsd:schema>\n";
        List<Type> types = _helperContext.getXSDHelper().define(new StringReader(schema), null);
        assertNotNull("Couldn't define type.", types);
        assertEquals(1, types.size());
        assertLineEquality(schema, _helperContext.getXSDHelper().generate(types));
    }

    @Test
    public void testAttributeFormDefault() {
        String schema = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                      + "<xsd:schema targetNamespace=\"com.sap.sdo.testcase\""
                      + " xmlns:tns=\"com.sap.sdo.testcase\""
                      + " xmlns:sdox=\"commonj.sdo/xml\""
                      + " xmlns:sdoj=\"commonj.sdo/java\""
                      + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
                      + " attributeFormDefault=\"qualified\""
                      + ">\n"
                      + "<xsd:complexType name=\"AttributeFormDefaultType\"></xsd:complexType>\n"
                      + "</xsd:schema>\n";
        List<Type> types = _helperContext.getXSDHelper().define(new StringReader(schema), null);
        assertNotNull("Couldn't define type.", types);
        assertEquals(1, types.size());
        assertLineEquality(schema, _helperContext.getXSDHelper().generate(types));
    }

    @Test
    public void testSpecificElementFormDefault() {
        String schema = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                      + "<xsd:schema targetNamespace=\"http://www.test.com\""
                      + " xmlns:tns=\"http://www.test.com\" xmlns:sdox=\"commonj.sdo/xml\""
                      + " xmlns:sdoj=\"commonj.sdo/java\""
                      + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
                      + " elementFormDefault=\"qualified\""
                      + ">\n"
                      + "<xsd:element name=\"root\" type=\"tns:order\"/>\n"
                      + "<xsd:complexType name=\"order\">\n"
                      + "    <xsd:sequence>\n"
                      + "        <xsd:element name=\"key\" type=\"xsd:string\" minOccurs=\"0\"/>\n"
                      + "        <xsd:element name=\"fullname\" type=\"xsd:string\" minOccurs=\"0\"/>\n"
                      + "    </xsd:sequence>\n"
                      + "</xsd:complexType>\n"
                      + "</xsd:schema>\n";
        List<Type> types = _helperContext.getXSDHelper().define(new StringReader(schema), null);
        assertNotNull("Couldn't define type.", types);
        assertEquals(1, types.size());
        assertLineEquality(schema, _helperContext.getXSDHelper().generate(types));

        DataObject data = _helperContext.getDataFactory().create(types.get(0));
        data.setString("key", "key");
        data.setString("fullname", "fullname");

        System.out.println(_helperContext.getXMLHelper().save(data, null, "order"));

        schema = schema.replace("elementFormDefault=\"qualified\"", "elementFormDefault=\"unqualified\"");
        schema = schema.replace("http://www.test.com", "http://www.test.com2");

        types = _helperContext.getXSDHelper().define(new StringReader(schema), null);
        assertNotNull("Couldn't define type.", types);
        assertEquals(1, types.size());
//        assertEquals(schema, _helperContext.getXSDHelper().generate(types));

        data = _helperContext.getDataFactory().create(types.get(0));
        data.setString("key", "key");
        data.setString("fullname", "fullname");

        System.out.println(_helperContext.getXMLHelper().save(data, null, "order"));
    }

    @Test
    public void testSdoModelXsd() throws IOException {
        URL url = getClass().getClassLoader().getResource("xsd/sdoModel.xsd");
        _helperContext.getXSDHelper().define(url.openStream(), url.toString());
    }

    @Test
    public void testFindSchemaLocationInXml() throws IOException {
        URL url = getClass().getClassLoader().getResource(PACKAGE + "message_1K.xml");
        SapXmlHelper xmlHelper = (SapXmlHelper)_helperContext.getXMLHelper();
        SapXmlDocument xmlDocument = xmlHelper.load(url.openStream(), url.toString(), null);
        assertEquals("rss", xmlDocument.getRootElementName());
        assertEquals("http://www.sap.com", xmlDocument.getRootElementURI());
        DataObject rss = xmlDocument.getRootObject();
        assertEquals("+rss", rss.getType().getName());
        assertEquals("http://www.sap.com", rss.getType().getURI());

        List<DataObject> definedTypes = xmlDocument.getDefinedTypes();
        assertEquals(4, definedTypes.size());
        for (DataObject type: definedTypes) {
            String typesXml = _helperContext.getXMLHelper().save(type, "commonj.sdo", "type");
            System.out.println(typesXml);
        }
    }

    @Test
    public void testSchemaParsingWithValidation() throws Exception {
        final String schemaFileName = PACKAGE + "change_request.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        Reader reader = new InputStreamReader(url.openStream());
        List<? extends Type> types = ((SapXsdHelper)_helperContext.getXSDHelper()).define(reader,"",null);

    }

    @Test
    public void testDefaultValues() throws Exception {
        final String schemaFileName = PACKAGE + "DefaultValues.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        final XSDHelper xsdHelper = _helperContext.getXSDHelper();
        xsdHelper.define(url.openStream(), url.toString());

        final TypeHelper typeHelper = _helperContext.getTypeHelper();
        Type rootType = typeHelper.getType("com.sap.test","rootType");
        Property attrDefault = rootType.getProperty("attrDefault");
        assertEquals("defaultValue", attrDefault.getDefault());
        Property attrFixed = rootType.getProperty("attrFixed");
        assertEquals("fixedValue", attrFixed.getDefault());
        Property elementDefault = rootType.getProperty("elementDefault");
        assertEquals("defaultValue", elementDefault.getDefault());
        Property elementFixed = rootType.getProperty("elementFixed");
        assertEquals("fixedValue", elementFixed.getDefault());
    }

    @Test
    public void testNoNS() throws Exception {
        final String schemaFileName = PACKAGE + "NoNS.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        final XSDHelper xsdHelper = _helperContext.getXSDHelper();
        xsdHelper.define(url.openStream(), url.toString());

        final TypeHelper typeHelper = _helperContext.getTypeHelper();
        Property root = typeHelper.getOpenContentProperty(null, "RootNoNS");
        Type type = root.getType();
        assertEquals(null, type.getURI());
        assertSame(root, typeHelper.getOpenContentProperty("", "RootNoNS"));
        Type baseType = (Type)type.getBaseTypes().get(0);
        assertEquals(null, baseType.getURI());
        assertEquals("NoNSBase", baseType.getName());
    }

    @Test
    public void testNoNSImport() throws Exception {
        final String schemaFileName = PACKAGE + "NoNSImport.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        final XSDHelper xsdHelper = _helperContext.getXSDHelper();
        xsdHelper.define(url.openStream(), url.toString());

        final TypeHelper typeHelper = _helperContext.getTypeHelper();
        Property rootNoNS = typeHelper.getOpenContentProperty(null, "RootNoNS");
        Type rootNoNSType = rootNoNS.getType();
        assertEquals(null, rootNoNSType.getURI());
        assertSame(rootNoNS, typeHelper.getOpenContentProperty("", "RootNoNS"));
        Type baseType = (Type)rootNoNSType.getBaseTypes().get(0);
        assertEquals(null, baseType.getURI());
        assertEquals("NoNSBase", baseType.getName());

        Property rootElement = typeHelper.getOpenContentProperty("http://www.sap.com/schema/test/NoNSImport", "rootElement");
        Type rootElementType = rootElement.getType();
        Property imported = rootElementType.getProperty("imported");
        assertSame(rootNoNSType, imported.getType());

        Property included = rootElementType.getProperty("included");
        Type includedType = included.getType();
        assertNotSame(rootNoNSType, includedType);
        assertEquals("http://www.sap.com/schema/test/NoNSImport", includedType.getURI());

        Type includedBaseType = (Type)includedType.getBaseTypes().get(0);
        assertNotSame(baseType, includedBaseType);
        assertEquals("http://www.sap.com/schema/test/NoNSImport", includedBaseType.getURI());
        assertEquals("NoNSBase", includedBaseType.getName());

        Property importedRootNoNS = rootElementType.getProperty("importedRootNoNS");
        assertSame(rootNoNSType, importedRootNoNS.getType());
        assertEquals("", xsdHelper.getNamespaceURI(importedRootNoNS));

        Property includedRootNoNS = rootElementType.getProperty("RootNoNS");
        assertSame(includedType, includedRootNoNS.getType());
        assertEquals("http://www.sap.com/schema/test/NoNSImport", xsdHelper.getNamespaceURI(includedRootNoNS));
    }

    @Test
    public void testPropsWithSameNames() throws Exception {
        URL url = getClass().getClassLoader().getResource(PACKAGE + "SameNames.xsd");
        XSDHelper xsdHelper = _helperContext.getXSDHelper();
        TypeHelper typeHelper = _helperContext.getTypeHelper();
        List<Type> types = xsdHelper.define(url.openStream(), url.toString());
        Type sameNames = typeHelper.getType("com.sap.sdo.testcase.anonymous", "sameNames");
        List<Property> properties = sameNames.getProperties();
        assertEquals(4, properties.size());
        Property prop0 = properties.get(0);
        Property prop1 = properties.get(1);
        Property prop2 = properties.get(2);
        Property prop3 = properties.get(3);
        assertEquals("prop", prop0.getName());
        assertEquals("prop", prop1.getName());
        assertEquals("prop", prop2.getName());
        assertEquals("prop", prop3.getName());
        assertEquals("com.sap.sdo.testcase.anonymous", xsdHelper.getNamespaceURI(prop0));
        assertEquals("", xsdHelper.getNamespaceURI(prop1));
        assertEquals("com.sap.sdo.testcase.anonymous", xsdHelper.getNamespaceURI(prop2));
        assertEquals("", xsdHelper.getNamespaceURI(prop3));
        assertEquals(true, xsdHelper.isElement(prop0));
        assertEquals(true, xsdHelper.isElement(prop1));
        assertEquals(true, xsdHelper.isAttribute(prop2));
        assertEquals(true, xsdHelper.isAttribute(prop3));
        assertEquals(prop0, sameNames.getProperty("prop"));
        assertEquals(prop2, sameNames.getProperty("@prop"));
        Type type0 = prop0.getType();
        Type type1 = prop1.getType();
        Type type2 = prop2.getType();
        Type type3 = prop3.getType();
        assertEquals("com.sap.sdo.testcase.anonymous", type0.getURI());
        assertEquals("com.sap.sdo.testcase.anonymous", type1.getURI());
        assertEquals("com.sap.sdo.testcase.anonymous", type2.getURI());
        assertEquals("com.sap.sdo.testcase.anonymous", type3.getURI());
        assertEquals("sameNames+prop~", type0.getName());
        assertEquals("sameNames+prop", type1.getName());
        assertEquals("sameNames@prop~", type2.getName());
        assertEquals("sameNames@prop", type3.getName());
        Property globalElement = xsdHelper.getGlobalProperty("com.sap.sdo.testcase.anonymous", "prop", true);
        Property globalAttribute = xsdHelper.getGlobalProperty("com.sap.sdo.testcase.anonymous", "prop", false);
        assertEquals("prop", globalElement.getName());
        assertEquals("prop", globalAttribute.getName());
        assertEquals("com.sap.sdo.testcase.anonymous", xsdHelper.getNamespaceURI(globalElement));
        assertEquals("com.sap.sdo.testcase.anonymous", xsdHelper.getNamespaceURI(globalAttribute));
        assertEquals(true, xsdHelper.isElement(globalElement));
        assertEquals(true, xsdHelper.isAttribute(globalAttribute));
        assertSame(globalElement, typeHelper.getOpenContentProperty("com.sap.sdo.testcase.anonymous", "prop"));
        assertSame(globalAttribute, typeHelper.getOpenContentProperty("com.sap.sdo.testcase.anonymous", "@prop"));
        Type globalElementType = globalElement.getType();
        Type globalAttributeType = globalAttribute.getType();
        assertEquals("com.sap.sdo.testcase.anonymous", globalElementType.getURI());
        assertEquals("com.sap.sdo.testcase.anonymous", globalAttributeType.getURI());
        assertEquals("+prop", globalElementType.getName());
        assertEquals("@prop", globalAttributeType.getName());
    }

    @Test
    public void testGroupsImport() throws IOException {
        URL xsdUrl = getClass().getClassLoader().getResource(PACKAGE + "GroupsImport.xsd");
        XSDHelper xsdHelper = _helperContext.getXSDHelper();
        xsdHelper.define(xsdUrl.openStream(), xsdUrl.toString());

        TypeHelper typeHelper = _helperContext.getTypeHelper();
        Property iRoot = typeHelper.getOpenContentProperty("com.sap.sdo.testcase.import", "root");
        Type iRootType = iRoot.getType();
        Property ia1 = iRootType.getProperty("a1");
        Property ia2 = iRootType.getProperty("a2");
        Property ie1 = iRootType.getProperty("e1");
        Property ie2 = iRootType.getProperty("e2");

        assertEquals("", xsdHelper.getNamespaceURI(ia1));
        assertEquals("com.sap.sdo.testcase.groups", xsdHelper.getNamespaceURI(ia2));
        assertEquals("com.sap.sdo.testcase.groups", xsdHelper.getNamespaceURI(ie1));
        assertEquals("", xsdHelper.getNamespaceURI(ie2));

        Property gRoot = typeHelper.getOpenContentProperty("com.sap.sdo.testcase.groups", "root");
        Type gRootType = gRoot.getType();
        Property ga1 = gRootType.getProperty("a1");
        Property ga2 = gRootType.getProperty("a2");
        Property ge1 = gRootType.getProperty("e1");
        Property ge2 = gRootType.getProperty("e2");

        assertEquals("", xsdHelper.getNamespaceURI(ga1));
        assertEquals("com.sap.sdo.testcase.groups", xsdHelper.getNamespaceURI(ga2));
        assertEquals("com.sap.sdo.testcase.groups", xsdHelper.getNamespaceURI(ge1));
        assertEquals("", xsdHelper.getNamespaceURI(ge2));

        assertSame(ia1.getType(), ga1.getType());
        assertSame(ia2.getType(), ga2.getType());
        assertSame(ie1.getType(), ge1.getType());
        assertSame(ie2.getType(), ge2.getType());

        assertEquals("com.sap.sdo.testcase.groups", ia1.getType().getURI());
        assertEquals("com.sap.sdo.testcase.groups", ia2.getType().getURI());
        assertEquals("com.sap.sdo.testcase.groups", ie1.getType().getURI());
        assertEquals("com.sap.sdo.testcase.groups", ie2.getType().getURI());
    }

    @Test
    public void testPropertyMergeT2() throws IOException {
        URL xsdUrl = getClass().getClassLoader().getResource(PACKAGE + "PropertyMerge.xsd");
        XSDHelper xsdHelper = _helperContext.getXSDHelper();
        xsdHelper.define(xsdUrl.openStream(), xsdUrl.toString());

        TypeHelper typeHelper = _helperContext.getTypeHelper();

        Type t2 = typeHelper.getType("com.sap.sdo.test.propmerge", "T2");
        assertEquals(true, t2.isSequenced());
        Property a = t2.getProperty("A");
        assertEquals(true, a.isMany());
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        assertEquals(stringType, a.getType());
        Property b = t2.getProperty("B");
        assertEquals(false, b.isMany());
        assertEquals(stringType, b.getType());
    }

    @Test
    public void testPropertyMergeT4() throws IOException {
        URL xsdUrl = getClass().getClassLoader().getResource(PACKAGE + "PropertyMerge.xsd");
        XSDHelper xsdHelper = _helperContext.getXSDHelper();
        xsdHelper.define(xsdUrl.openStream(), xsdUrl.toString());

        TypeHelper typeHelper = _helperContext.getTypeHelper();

        Type t4 = typeHelper.getType("com.sap.sdo.test.propmerge", "T4");
        assertEquals(true, t4.isSequenced());
        Property a = t4.getProperty("A");
        assertEquals(false, a.isMany());
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        assertEquals(stringType, a.getType());
        Property b = t4.getProperty("B");
        assertEquals(false, b.isMany());
        assertEquals(stringType, b.getType());
    }

    @Test
    public void testRestrictionLongToInt() throws IOException {
        URL xsdUrl = getClass().getClassLoader().getResource(PACKAGE + "RestrictionLongToInt.xsd");
        XSDHelper xsdHelper = _helperContext.getXSDHelper();
        xsdHelper.define(xsdUrl.openStream(), xsdUrl.toString());

        TypeHelper typeHelper = _helperContext.getTypeHelper();

        Type t1 = typeHelper.getType("http://test.org", "T1");
        Property a1 = t1.getProperty("a");
        assertSame(typeHelper.getType("commonj.sdo", "Long"), a1.getType());
        Type t2 = typeHelper.getType("http://test.org", "T2");
        Property a2 = t2.getProperty("a");
        assertSame(typeHelper.getType("commonj.sdo", "Long"), a2.getType());
        Property b = t2.getProperty("b");
        assertSame(typeHelper.getType("commonj.sdo", "Int"), b.getType());
    }

    @Test
    public void testRestrictionIntegerToInt() throws IOException {
        URL xsdUrl = getClass().getClassLoader().getResource(PACKAGE + "RestrictionIntegerToInt.xsd");
        XSDHelper xsdHelper = _helperContext.getXSDHelper();
        xsdHelper.define(xsdUrl.openStream(), xsdUrl.toString());

        TypeHelper typeHelper = _helperContext.getTypeHelper();

        Type t1 = typeHelper.getType("http://test.org", "T1");
        Property a1 = t1.getProperty("a");
        assertSame(typeHelper.getType("commonj.sdo", "Integer"), a1.getType());
        Type t2 = typeHelper.getType("http://test.org", "T2");
        Property a2 = t2.getProperty("a");
        assertSame(typeHelper.getType("commonj.sdo", "Integer"), a2.getType());
        Property b = t2.getProperty("b");
        assertSame(int.class, b.getType().getInstanceClass());
    }
    @Test
    public void testSchemaRef() throws IOException {
        URL url = getClass().getClassLoader().getResource(PACKAGE + "btmGlxIntegration.wsdl");

        XMLHelper xmlHelper = _helperContext.getXMLHelper();
        Map options = new HashMap();
        options.put(SapXmlHelper.OPTION_KEY_DEFINE_SCHEMAS, SapXmlHelper.OPTION_VALUE_TRUE);
        xmlHelper.load(url.openStream(), url.toString(), options);

        Type type = _helperContext.getTypeHelper().getType("http://sap.com/BtmGlxIntgn", "TaskDataType");
        Property amountProp = type.getProperty("Amount");
        Property schemaRefProp = _helperContext.getTypeHelper().getOpenContentProperty(URINamePair.PROP_CTX_SCHEMA_REFERENCE.getURI(), URINamePair.PROP_CTX_SCHEMA_REFERENCE.getName());

        DataObject schemaRef = (DataObject)amountProp.get(schemaRefProp);

        Element element = (Element)schemaRef;

        assertEquals("1", element.getMaxOccurs());

    }

    @Test
    public void testWsdl() throws IOException {
        loadFromWSDL (_helperContext, PACKAGE + "OrderFacade.wsdl" );
        loadFromWSDL (_helperContext, PACKAGE + "ComplaintManagementDataService.wsdl" );
        loadFromWSDL (_helperContext, PACKAGE + "CustomerFacade.wsdl" );
    }

    private void  loadFromWSDL (commonj.sdo.helper.HelperContext hCtx, String location ) throws IOException {
        // creating the options settings
        Map<String,String> options = new HashMap<String,String>();
        // setting the value of OPTION_KEY_DEFINE_SCHEMAS as true
        // thus the types will be loaded to context
        options.put(SapXmlHelper.OPTION_KEY_DEFINE_SCHEMAS,
        SapXmlHelper.OPTION_VALUE_TRUE);

        InputStream in = getClass().getClassLoader().getResourceAsStream(location);
        ((SapXmlHelper) hCtx.getXMLHelper()).load(in, location, options);
      }

}
