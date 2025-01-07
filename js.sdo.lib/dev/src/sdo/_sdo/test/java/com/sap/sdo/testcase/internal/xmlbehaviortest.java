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

import static com.sap.sdo.api.util.URINamePair.DATATYPE_XML_URI;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sdo.tck.staticTest.CT2;
import javax.sdo.tck.staticTest.ComplexType1;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.glx.paradigmInterface.postprocessor.ws.wssx.NestedPolicyType;
import com.sap.sdo.api.helper.ErrorHandler;
import com.sap.sdo.api.helper.InterfaceGenerator;
import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.helper.SapTypeHelper;
import com.sap.sdo.api.helper.SapXmlDocument;
import com.sap.sdo.api.helper.SapXmlHelper;
import com.sap.sdo.api.helper.SapXsdHelper;
import com.sap.sdo.api.impl.SapHelperProvider;
import com.sap.sdo.api.types.PropertyConstants;
import com.sap.sdo.api.types.TypeConstants;
import com.sap.sdo.api.types.schema.Annotation;
import com.sap.sdo.api.types.schema.Appinfo;
import com.sap.sdo.api.types.schema.ComplexType;
import com.sap.sdo.api.types.schema.Documentation;
import com.sap.sdo.api.types.schema.Element;
import com.sap.sdo.api.types.schema.Schema;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.context.HelperContextImpl;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.builtin.DataGraphType;
import com.sap.sdo.impl.types.builtin.PropertyType;
import com.sap.sdo.impl.types.builtin.TypeType;
import com.sap.sdo.impl.xml.DefaultSchemaResolver;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.InvalidSchemaInfo;
import com.sap.sdo.testcase.typefac.OpenInterface;
import com.sap.sdo.testcase.typefac.SimpleSdoNameIntf;
import com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TDefinitions;
import com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TPortType;
import com.sap.tc.esmp.tools.wsdlexport.model.wsdl.http.UrlEncoded;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.helper.XSDHelper;
import commonj.sdo.impl.HelperProvider;

/**
 * @author D042774
 *
 */
public class XmlBehaviorTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public XmlBehaviorTest(String pContextId, Feature pFeature) {
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
    public void testSdoNames() {
        final String schemaFileName = PACKAGE + "order.xsd";
        InputStream is = getClass().getClassLoader().getResourceAsStream(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(is,null);
        assertNotNull(types);
        assertEquals(1, types.size());

        String xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<root xmlns=\"http://www.test2.com\" xmlns:sdo=\"commonj.sdo\">\n"
                + "  <key>key</key>\n"
                + "  <fullname>fullname</fullname>\n"
                + "</root>\n";
        DataObject order = _helperContext.getXMLHelper().load(xml).getRootObject();
        assertNotNull(order);
        assertSame(types.get(0), order.getType());
        assertEquals("key", order.get("test_key"));
        assertEquals("fullname", order.get("test_fullname"));
        // assertEquals(xml, _context.getXMLHelper().save(order, null, "order"));
    }

    @Test
    public void testUnqualifiedXsdElement() {
        final String schemaFileName = PACKAGE + "unqualifiedOrder.xsd";
        InputStream is = getClass().getClassLoader().getResourceAsStream(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(is,null);
        assertNotNull(types);
        assertEquals(1, types.size());

        DataObject order = _helperContext.getDataFactory().create(types.get(0));
        order.set("key", "key");
        order.set("fullname", "fullname");

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                    + "<ns1:order" +
                            " xmlns:ns1=\"http://www.test1.com\"" +
                            " xsi:type=\"ns1:order\"" +
                            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
                            " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"" +
                            ">\n"
                    + "  <key>key</key>\n"
                    + "  <fullname>fullname</fullname>\n"
                    + "</ns1:order>\n";
        assertLineEquality(xml, _helperContext.getXMLHelper().save(order, "http://www.test1.com", "order"));
    }

    @Test
    public void testMultipleNamespaces() throws Exception {
        final String schemaFileName = PACKAGE + "temp10.xsd";
        URL resource = getClass().getClassLoader().getResource(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(resource.openStream(), resource.toString());
        assertNotNull(types);
        assertEquals(3, types.size());

        DataObject user2 = _helperContext.getDataFactory().create("http://www.fourthtest.com", "userType2");
        user2.set("id", "Id");
        user2.set("name", "Name");
        DataObject user3 = _helperContext.getDataFactory().create("http://www.fourthtest.com", "userType3");
        user3.set("firstname", "First Name");
        user3.set("lastname", "Last Name");
        DataObject root = _helperContext.getDataFactory().create("http://www.test.com", "order");
        List<Property> properties = root.getInstanceProperties();
        assertNotNull(properties);
        assertEquals(2, properties.size());
        root.set("user2", user2);
        root.set("user3", user3);
        System.out.println(_helperContext.getXMLHelper().save(root, null, "root"));

    }

    @Test
    public void testAbsoluteSchemaLocation() throws Exception {
        final String schemaFileName = PACKAGE + "temp10absolute.xsd";
        URL resource = getClass().getClassLoader().getResource(schemaFileName);
        String resourceStr = resource.toExternalForm();
        String schemaDir = resourceStr.substring(0, resourceStr.lastIndexOf('/'));
        String xsd = readFile(resource);
        xsd = xsd.replace("%%SCHEMA_DIR%%", schemaDir);

        List<Type> types = _helperContext.getXSDHelper().define(xsd);
        assertNotNull(types);
        assertEquals(3, types.size());

        DataObject user2 = _helperContext.getDataFactory().create("http://www.fourthtest.com", "userType2");
        user2.set("id", "Id");
        user2.set("name", "Name");
        DataObject user3 = _helperContext.getDataFactory().create("http://www.fourthtest.com", "userType3");
        user3.set("firstname", "First Name");
        user3.set("lastname", "Last Name");
        DataObject root = _helperContext.getDataFactory().create("http://www.test.absolute.com", "order");
        List<Property> properties = root.getInstanceProperties();
        assertNotNull(properties);
        assertEquals(2, properties.size());
        root.set("user", user2);
        root.set("user3", user3);
        System.out.println(_helperContext.getXMLHelper().save(root, null, "root"));
    }

    @Test
    public void testValueProp() throws Exception {
        final String schemaFileName = PACKAGE + "request.xsd";
        URL resource = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(resource.openStream(), resource.toString());
        DataObject l_dataobjectRoot =
            _helperContext.getDataFactory().create("sap.com/bpem/glx/built-in/finite-integer", "request");
        DataObject l_dataobjectContent = _helperContext.getDataFactory().create(createType());
        l_dataobjectRoot.setDataObject("content", l_dataobjectContent);
        DataObject l_dataobjectFirst = _helperContext.getDataFactory().create(createType());
        l_dataobjectFirst.set(l_dataobjectFirst.getType().getProperty("value"), "some value");
        l_dataobjectContent.set(createProperty("first"), l_dataobjectFirst);
        String xml =
            _helperContext.getXMLHelper().save(
                l_dataobjectRoot,
                l_dataobjectRoot.getType().getURI(),
                l_dataobjectRoot.getType().getName());

        XMLDocument xmlDoc = _helperContext.getXMLHelper().load(xml);
        assertEquals("some value", xmlDoc.getRootObject().getString("content/first/value"));
        assertEquals(
            xml,
            _helperContext.getXMLHelper().save(
                xmlDoc.getRootObject(),
                xmlDoc.getRootElementURI(),
                xmlDoc.getRootElementName()));
    }

    private final Property createProperty(String name) {
        DataObject l_dataobjectProperty = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        l_dataobjectProperty.set("type", createType());
        l_dataobjectProperty.set("name", name);
        l_dataobjectProperty.set("containment", true);
        return _helperContext.getTypeHelper().defineOpenContentProperty(null, l_dataobjectProperty);
    }

    private final Type createType() {
        DataObject l_dataobjectType = _helperContext.getDataFactory().create("commonj.sdo",    "Type");
        l_dataobjectType.setString("uri", "some.uri");
        l_dataobjectType.setString("name", "GenericType");
        l_dataobjectType.setBoolean("open", true);
        DataObject l_dataobjectPropertyValue = l_dataobjectType.createDataObject("property");
        l_dataobjectPropertyValue.setString("name", "value");
        l_dataobjectPropertyValue.set(
            "type", _helperContext.getTypeHelper().getType("commonj.sdo", "String"));
        return _helperContext.getTypeHelper().define(l_dataobjectType);
    }

    @Test
    public void testUndefinedAttribute() throws Exception {
        final String schemaFileName = PACKAGE + "order.xsd";
        InputStream is = getClass().getClassLoader().getResourceAsStream(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(is,null);
        assertNotNull(types);
        assertEquals(1, types.size());

        String xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<root orderDate=\"2007-11-20\" xmlns=\"http://www.test2.com\" xmlns:sdo=\"commonj.sdo\">\n"
                + "  <key>key</key>\n"
                + "  <fullname>fullname</fullname>\n"
                + "</root>\n";
        try {
            _helperContext.getXMLHelper().load(xml).getRootObject();
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals(
                "Type http://www.test2.com#order is not open. Property 'orderDate' is not defined.",
                ex.getMessage());
        }

        DataObject order = _helperContext.getXMLHelper().load(
                                new ByteArrayInputStream(xml.getBytes()),
                                "http://www.test2.com",
                                Collections.singletonMap(
                                    SapXmlHelper.OPTION_KEY_ERROR_HANDLER,
                                    new MyErrorHandler())).getRootObject();
        assertNotNull(order);
        assertSame(types.get(0), order.getType());
        assertEquals("key", order.get("test_key"));
        assertEquals("fullname", order.get("test_fullname"));
        assertEquals(null, order.get("orderDate"));
    }

    @Test
    public void testUndefinedElement() throws Exception {
        final String schemaFileName = PACKAGE + "order.xsd";
        InputStream is = getClass().getClassLoader().getResourceAsStream(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(is,null);
        assertNotNull(types);
        assertEquals(1, types.size());

        String xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<root xmlns=\"http://www.test2.com\" xmlns:sdo=\"commonj.sdo\">\n"
                + "  <key>key</key>\n"
                + "  <fullname>fullname</fullname>\n"
                + "  <orderDate>2007-11-20</orderDate>\n"
                + "</root>\n";
        try {
            _helperContext.getXMLHelper().load(xml).getRootObject();
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals(
                "Type http://www.test2.com#order is not open. Property 'orderDate' is not defined.",
                ex.getMessage());
        }

        DataObject order = _helperContext.getXMLHelper().load(
                                new ByteArrayInputStream(xml.getBytes()),
                                "http://www.test2.com",
                                Collections.singletonMap(
                                    SapXmlHelper.OPTION_KEY_ERROR_HANDLER,
                                    new MyErrorHandler())).getRootObject();
        assertNotNull(order);
        assertSame(types.get(0), order.getType());
        assertEquals("key", order.get("test_key"));
        assertEquals("fullname", order.get("test_fullname"));
        assertEquals(null, order.get("orderDate"));
    }

    @Test
    public void testInvalidProperties() throws Exception {
        final String schemaFileName = PACKAGE + "FacetType.xsd";
        InputStream is = getClass().getClassLoader().getResourceAsStream(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(is,null);
        assertNotNull(types);
        assertEquals(8, types.size());

        System.out.println(_helperContext.getXSDHelper().generate(types));

        String xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<data facetAttribute=\"foo\">\n"
            + "  <facetElement>key</facetElement>\n"
            + "</data>\n";

        DataObject data = _helperContext.getXMLHelper().load(xml).getRootObject();
        assertNotNull(data);
        assertEquals("foo", data.get("facetAttribute"));
        assertEquals("key", data.get("facetElement"));

        xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<data facetAttribute=\"foo1\">\n"
            + "  <facetElement>key</facetElement>\n"
            + "</data>\n";

        try {
            _helperContext.getXMLHelper().load(xml).getRootObject();
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals(
                "Value 'foo1' of type #facetType@facetAttribute fails validation check: Enumeration=[foo]",
                ex.getMessage());
        }

        data = _helperContext.getXMLHelper().load(
                                new ByteArrayInputStream(xml.getBytes()),
                                "",
                                Collections.singletonMap(
                                    SapXmlHelper.OPTION_KEY_ERROR_HANDLER,
                                    new MyErrorHandler())).getRootObject();
        assertNotNull(data);
        assertEquals(null, data.get("facetAttribute"));
        assertFalse(data.isSet("facetAttribute"));
        assertEquals("key", data.get("facetElement"));

        xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<data facetAttribute=\"foo\">\n"
            + "  <facetElement>key1</facetElement>\n"
            + "</data>\n";

        try {
            _helperContext.getXMLHelper().load(xml).getRootObject();
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals(
                "Value 'key1' of type #facetType+facetElement fails validation check: Length=3",
                ex.getMessage());
        }

        data = _helperContext.getXMLHelper().load(
                                new ByteArrayInputStream(xml.getBytes()),
                                "",
                                Collections.singletonMap(
                                    SapXmlHelper.OPTION_KEY_ERROR_HANDLER,
                                    new MyErrorHandler())).getRootObject();
        assertNotNull(data);
        assertEquals("foo", data.get("facetAttribute"));
        assertEquals(null, data.get("facetElement"));
        assertFalse(data.isSet("facetElement"));
    }

    @Test
    public void testBytesMaxLength() throws Exception {
        final String schemaFileName = PACKAGE + "FacetType.xsd";
        InputStream is = getClass().getClassLoader().getResourceAsStream(schemaFileName);
        _helperContext.getXSDHelper().define(is,null);

        DataObject data = _helperContext.getDataFactory().create(null, "RestrictedLengthType");
        assertNotNull(data);

        data.unset("bytesMaxLength");
        data.set("bytesMaxLength", null);
        data.set("bytesMaxLength", "0000000000000000");
        data.set("bytesMaxLength", new byte[8]);
        try {
            data.set("bytesMaxLength", "000000000000000000");
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            assertEquals(
                "Value '000000000000000000' of type #RestrictedLengthType+bytesMaxLength fails validation check: MaxLength=8",
                ex.getMessage());
        }
        try {
            data.set("bytesMaxLength", new byte[16]);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            assertEquals(
                "Value '00000000000000000000000000000000' of type #RestrictedLengthType+bytesMaxLength fails validation check: MaxLength=8",
                ex.getMessage());
        }
    }

    @Test
    public void testStringListLength() throws Exception {
        final String schemaFileName = PACKAGE + "FacetType.xsd";
        InputStream is = getClass().getClassLoader().getResourceAsStream(schemaFileName);
        _helperContext.getXSDHelper().define(is,null);

        DataObject data = _helperContext.getDataFactory().create(null, "RestrictedLengthType");
        assertNotNull(data);

        data.unset("listLength");
        data.set("listLength", null);
        data.set("listLength", Arrays.asList("a", "b", "c"));
        try {
            data.set("listLength", new ArrayList<String>(3));
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            assertEquals(
                "Value '' of type #RestrictedLengthType+listLength fails validation check: Length=3",
                ex.getMessage());
        }
        try {
            data.set("listLength", Arrays.asList("a", "b", "c", "d"));
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            assertEquals(
                "Value 'a b c d' of type #RestrictedLengthType+listLength fails validation check: Length=3",
                ex.getMessage());
        }
    }

    @Test
    public void testLoadDocumentPart() throws Exception {
        final String schemaFileName = PACKAGE + "order.xsd";
        InputStream is = getClass().getClassLoader().getResourceAsStream(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(is,null);
        assertNotNull(types);
        assertEquals(1, types.size());

        String xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<order xmlns=\"http://www.test2.com\" xmlns:sdo=\"commonj.sdo\">\n"
                + "  <key>key</key>\n"
                + "  <fullname>fullname</fullname>\n"
                + "  <orderDate>2007-11-20</orderDate>\n"
                + "</order>\n";

        XMLStreamReader reader =
            XMLInputFactory.newInstance().createXMLStreamReader(
                new ByteArrayInputStream(xml.getBytes()));
        while (reader.next() != XMLStreamReader.START_ELEMENT);
        try {
            ((DataObject)((SapXmlHelper)_helperContext.getXMLHelper()).load(reader, null)).getRootObject();
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals(
                "Type http://www.test2.com#order is not open. Property 'orderDate' is not defined.",
                ex.getMessage());
        }

        reader = XMLInputFactory.newInstance().createXMLStreamReader(
                    new ByteArrayInputStream(xml.getBytes()));
        while (reader.next() != XMLStreamReader.START_ELEMENT);
        DataObject order = ((DataObject)((SapXmlHelper)_helperContext.getXMLHelper()).load(
                                reader,
                                Collections.singletonMap(
                                    SapXmlHelper.OPTION_KEY_ERROR_HANDLER,
                                    new MyErrorHandler()))).getRootObject();
        assertNotNull(order);
        assertSame(types.get(0), order.getType());
        assertEquals("key", order.get("test_key"));
        assertEquals("fullname", order.get("test_fullname"));
        assertEquals(null, order.get("orderDate"));
    }

    @Test
    public void testLoadDocumentPart2() throws Exception {
        final String schemaFileName = PACKAGE + "order.xsd";
        InputStream is = getClass().getClassLoader().getResourceAsStream(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(is,null);
        assertNotNull(types);
        assertEquals(1, types.size());

        String xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<order xmlns=\"http://www.test2.com\" xmlns:sdo=\"commonj.sdo\">\n"
                + "  <key>key</key>\n"
                + "  <fullname>fullname</fullname>\n"
                + "  <orderDate>2007-11-20</orderDate>\n"
                + "</order>\n";

        XMLStreamReader reader =
            XMLInputFactory.newInstance().createXMLStreamReader(
                new ByteArrayInputStream(xml.getBytes()));
        while (reader.next() != XMLStreamReader.START_ELEMENT);
        try {
            ((DataObject)((SapXmlHelper)_helperContext.getXMLHelper()).load(
                reader, "http://www.test2.com", "order", null)).getRootObject();
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals(
                "Type http://www.test2.com#order is not open. Property 'orderDate' is not defined.",
                ex.getMessage());
        }

        reader = XMLInputFactory.newInstance().createXMLStreamReader(
                    new ByteArrayInputStream(xml.getBytes()));
        while (reader.next() != XMLStreamReader.START_ELEMENT);
        DataObject order = ((DataObject)((SapXmlHelper)_helperContext.getXMLHelper()).load(
                                reader,
                                "http://www.test2.com", "order",
                                Collections.singletonMap(
                                    SapXmlHelper.OPTION_KEY_ERROR_HANDLER,
                                    new MyErrorHandler()))).getRootObject();
        assertNotNull(order);
        assertSame(types.get(0), order.getType());
        assertEquals("key", order.get("test_key"));
        assertEquals("fullname", order.get("test_fullname"));
        assertEquals(null, order.get("orderDate"));
    }

    @Test
    public void testSoapResponse() throws Exception {
        SapXsdHelper xsdHelper = (SapXsdHelper)_helperContext.getXSDHelper();

        final String schemaFileName = PACKAGE + "schema2.sdoxsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        xsdHelper.define(url.openStream(), null, null);
        final String xmlFileName = PACKAGE + "SoapResponse.xml";
        URL xmlUrl = getClass().getClassLoader().getResource(xmlFileName);
        InputStream systemResourceAsStream = xmlUrl.openStream();
        SapXmlHelper helper = (SapXmlHelper)_helperContext.getXMLHelper();
        helper.load(systemResourceAsStream);
    }

    @Test
    public void testSoapResponseAlias() throws Exception {
        SapXsdHelper xsdHelper = (SapXsdHelper)_helperContext.getXSDHelper();

        final String schemaFileName = PACKAGE + "schema2Alias.sdoxsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        xsdHelper.define(url.openStream(), null, null);
        final String xmlFileName = PACKAGE + "SoapResponse.xml";
        URL xmlUrl = getClass().getClassLoader().getResource(xmlFileName);
        InputStream systemResourceAsStream = xmlUrl.openStream();
        SapXmlHelper helper = (SapXmlHelper)_helperContext.getXMLHelper();
        helper.load(systemResourceAsStream);
    }

    @Test
    public void testCustomer() throws Exception {
        SapXsdHelper xsdHelper = (SapXsdHelper)_helperContext.getXSDHelper();

        final String schemaFileName = PACKAGE + "customer.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        xsdHelper.define(url.openStream(), null, null);
        final String xmlFileName = PACKAGE + "customer2.xml";
        URL xmlUrl = getClass().getClassLoader().getResource(xmlFileName);
        InputStream systemResourceAsStream = xmlUrl.openStream();
        SapXmlHelper helper = (SapXmlHelper)_helperContext.getXMLHelper();

        SapXmlDocument xmlDoc = helper.load(systemResourceAsStream);
        assertNotNull(xmlDoc);

        DataObject customer = xmlDoc.getRootObject();
        assertNotNull(customer);
        assertEquals("+customer", customer.getType().getName());

        assertEquals("", customer.get("support-level"));
        assertEquals(true, customer.isSet("support-level"));
    }

    @Test
    public void testDynamic() throws Exception {
        String xsd = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<xsd:schema targetNamespace=\"sap.com/test\"" +
                " xmlns:tns=\"sap.com/test\"" +
                " xmlns:sdo=\"commonj.sdo\"" +
                " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
                " <xsd:complexType name=\"request\">" +
                "  <xsd:sequence>" +
                "   <xsd:element name=\"content\" type=\"xsd:anyType\"/>" +
                "  </xsd:sequence>" +
                " </xsd:complexType>" +
                "</xsd:schema>";
        List<Type> types = _helperContext.getXSDHelper().define(xsd);

        DataObject propObj =
            _helperContext.getDataFactory().create(
                URINamePair.PROPERTY.getURI(), URINamePair.PROPERTY.getName());
        propObj.set(PropertyConstants.NAME, "value");
        propObj.set(
            PropertyConstants.TYPE,
            _helperContext.getTypeHelper().getType(
                URINamePair.OBJECT.getURI(), URINamePair.OBJECT.getName()));
        propObj.setBoolean(
            _helperContext.getXSDHelper().getGlobalProperty(
                URINamePair.DATATYPE_XML_URI, PropertyConstants.XML_ELEMENT, false),
            false);
        Property value =
            _helperContext.getTypeHelper().defineOpenContentProperty("sap.com/test", propObj);

        DataObject foo =
            _helperContext.getDataFactory().create(
                URINamePair.OPEN.getURI(), URINamePair.OPEN.getName());

        DataObject one =
            _helperContext.getDataFactory().create(
                URINamePair.OPEN.getURI(), URINamePair.OPEN.getName());
        one.setInt(value, 1);

        DataObject two1 =
            _helperContext.getDataFactory().create(
                URINamePair.OPEN.getURI(), URINamePair.OPEN.getName());
        two1.setInt(value, 2);

        DataObject two2 =
            _helperContext.getDataFactory().create(
                URINamePair.OPEN.getURI(), URINamePair.OPEN.getName());

        DataObject data =
            _helperContext.getDataFactory().create(
                URINamePair.OPEN.getURI(), URINamePair.OPEN.getName());
        data.setInt(value, 3);

        two2.setDataObject("data", data);

        foo.setDataObject("one", one);

        DataObject propObj2 =
            _helperContext.getDataFactory().create(
                URINamePair.PROPERTY.getURI(), URINamePair.PROPERTY.getName());
        propObj2.set(PropertyConstants.NAME, "two");
        propObj2.set(
            PropertyConstants.TYPE,
            _helperContext.getTypeHelper().getType(
                URINamePair.DATAOBJECT.getURI(), URINamePair.DATAOBJECT.getName()));
        propObj2.set(PropertyConstants.MANY, true);
        propObj2.set(PropertyConstants.CONTAINMENT, true);
        Property prop =
            _helperContext.getTypeHelper().defineOpenContentProperty("sap.com/test", propObj2);

        foo.getList(prop).add(two1);
        foo.getList(prop).add(two2);

        DataObject bar =
            _helperContext.getDataFactory().create(
                URINamePair.OPEN.getURI(), URINamePair.OPEN.getName());
        bar.setInt(value, 42);

        DataObject content =
            _helperContext.getDataFactory().create(
                URINamePair.OPEN.getURI(), URINamePair.OPEN.getName());
        content.setDataObject("foo", foo);
        content.setDataObject("bar", bar);

        DataObject request = _helperContext.getDataFactory().create(types.get(0));
        request.setDataObject("content", content);

        System.out.println(_helperContext.getXMLHelper().save(request, null, "request"));
        System.out.println(_helperContext.getXSDHelper().generate(types));
    }

    @Test
    public void testRootChildSchemas() throws Exception {
        final String rootFileName = PACKAGE + "root.xml";
        URL rootUrl = getClass().getClassLoader().getResource(rootFileName);
        final String childFileName = PACKAGE + "child.xml";
        URL childUrl = getClass().getClassLoader().getResource(childFileName);

        final List<Schema> schemas = new ArrayList<Schema>();
        schemas.add(
            (Schema)_helperContext.getXMLHelper().load(
                rootUrl.openStream(), rootUrl.toString(), null).getRootObject());
        assertEquals(1, schemas.size());
        schemas.add(
            (Schema)_helperContext.getXMLHelper().load(
                childUrl.openStream(), childUrl.toString(), null).getRootObject());
        assertEquals(2, schemas.size());

        List<Type> types = ((SapXsdHelper)_helperContext.getXSDHelper()).define(schemas, null);
        assertEquals(8, types.size());
    }

    @Test
    public void testRootChildAliasSchemas() throws Exception {
        final String rootFileName = PACKAGE + "rootAlias.xml";
        URL rootUrl = getClass().getClassLoader().getResource(rootFileName);
        final String childFileName = PACKAGE + "childAlias.xml";
        URL childUrl = getClass().getClassLoader().getResource(childFileName);

        final List<Schema> schemas = new ArrayList<Schema>();
        schemas.add(
            (Schema)_helperContext.getXMLHelper().load(
                rootUrl.openStream(), rootUrl.toString(), null).getRootObject());
        assertEquals(1, schemas.size());
        schemas.add(
            (Schema)_helperContext.getXMLHelper().load(
                childUrl.openStream(), childUrl.toString(), null).getRootObject());
        assertEquals(2, schemas.size());

        List<Type> types = ((SapXsdHelper)_helperContext.getXSDHelper()).define(schemas, null);
        assertEquals(8, types.size());
    }

    @Test
    public void testAnnotatedType() throws Exception {
        final String fileName = PACKAGE + "annotatedType.xsd";
        URL url = getClass().getClassLoader().getResource(fileName);

        List<Type> types = _helperContext.getXSDHelper().define(url.openStream(), url.toString());
        assertNotNull(types);
        assertEquals(1, types.size());

        Type annotated = types.get(0);
        assertEquals("my_type", annotated.getName());

        // get a reference of type in parsed schema
        // the result is a DataObject which represents the type in the schema as xml
        // ComplexType is a static SDO super interface of all possible complex type declarations
        ComplexType schemaRef = (ComplexType)((DataObject)annotated).getDataObject(TypeConstants.SCHEMA_REFERENCE);
        Annotation annotation = schemaRef.getAnnotation();

        // get appinfo element thru static SDO
        List<Appinfo> appInfos = annotation.getAppinfo();
        assertNotNull(appInfos);
        assertEquals(1, appInfos.size());
        // appinfo is mixed and we're getting access to its content by accessing the sequence
        assertEquals("appinfo", ((DataObject)appInfos.get(0)).getSequence().getValue(0));

        // get documentation element thru static SDO
        List<Documentation> documentation = annotation.getDocumentation();
        assertNotNull(documentation);
        assertEquals(1, documentation.size());
        // documentation is mixed and we're getting access to its content by accessing the sequence
        assertEquals("documentation", ((DataObject)documentation.get(0)).getSequence().getValue(0));

        // own annotations are accessed by their property names
        assertEquals("true", schemaRef.getString("annotated"));

        Property item = annotated.getProperty("item");
        Element elementRef = (Element)((DataObject)item).getDataObject(TypeConstants.SCHEMA_REFERENCE);
        assertEquals("item", elementRef.getName());

    }

    @Test
    public void testHotelManagement() throws Exception {
        XSDHelper XSD_HELPER = _helperContext.getXSDHelper();
        XMLHelper XML_HELPER = _helperContext.getXMLHelper();

        // loading the XSD to default context using the xsd file
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "hotel_management.xsd");
        XSD_HELPER.define(xsdURL.openStream(), xsdURL.toString());
        // getting the root data object
        URL xmlURL = getClass().getClassLoader().getResource(PACKAGE + "hotelsManagement.xml");
        DataObject root = XML_HELPER.load(xmlURL.openStream(),
                xmlURL.toString(), null).getRootObject();

        List<DataObject> cities = root.getList("city");
        assertEquals(6, cities.size());
        for (DataObject city : cities) {
            // printing out the city name
            System.out.printf("%s :", city.getString("name"));
            // getting the list of hotels
            List<DataObject> hotels = city.getList("hotel");
            assertFalse(hotels.isEmpty());
            for (DataObject hotel : hotels) {
                // printing out the hotel name
                System.out.printf(" [%s]", hotel.getString("name"));
            }
            System.out.println();
        }

        // getting Hamburg city object
        DataObject specificCity = root.getDataObject("city.3");
        assertNotNull(specificCity);
        assertEquals("Hamburg", specificCity.getString("name"));

        // getting Hamburg city object
        specificCity = root.getDataObject("city[name='Hamburg']");
        assertNotNull(specificCity);
        assertEquals("Hamburg", specificCity.getString("name"));
    }

    @Test
    public void testDataObject0() throws Exception {
        final String fileName = PACKAGE + "DataObject0.xml";
        URL url = getClass().getClassLoader().getResource(fileName);

        XMLDocument doc = _helperContext.getXMLHelper().load(url.openStream(), url.toString(), null);
        assertNotNull(doc);

        DataObject root = doc.getRootObject();
        assertNotNull(root);

        assertEquals("Galaxy", root.getString("value"));

        DataObject data = _helperContext.getDataFactory().create(String.class);
        data.setString("value", "Galaxy");

        System.out.println(_helperContext.getXMLHelper().save(data, null, "data"));

        DataObject copy = _helperContext.getDataFactory().create(data.getType());
        copy.set(data.getInstanceProperty("value"), "foo");

        System.out.println(_helperContext.getXMLHelper().save(copy, null, "copy"));
    }

    @Test
    public void testGMonthSerialization() throws Exception {
        XSDHelper XSD_HELPER = _helperContext.getXSDHelper();
        XMLHelper XML_HELPER = _helperContext.getXMLHelper();

        // loading the XSD to default context using the xsd file
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "schema1.sdoxsd");
        XSD_HELPER.define(xsdURL.openStream(), xsdURL.toString());

        // getting the root data object
        URL xmlURL = getClass().getClassLoader().getResource(PACKAGE + "schema1.xml");
        XMLDocument doc = XML_HELPER.load(xmlURL.openStream(), xmlURL.toString(), null);
        assertNotNull(doc);

        assertEquals("--10", doc.getRootObject().get("$inputParameter1"));
        List<Property> instanceProperties = doc.getRootObject().getInstanceProperties();
        assertEquals(1, instanceProperties.size());
        assertEquals("$inputParameter1", instanceProperties.get(0).getName());
        assertEquals("inputParameter1", XSD_HELPER.getLocalName(instanceProperties.get(0)));

        String xml = XML_HELPER.save(
                        doc.getRootObject(),
                        doc.getRootElementURI(),
                        doc.getRootElementName());
        System.out.println(xml);

        doc = XML_HELPER.load(xml);
        assertNotNull(doc);

        assertEquals("--10", doc.getRootObject().get("$inputParameter1"));
        instanceProperties = doc.getRootObject().getInstanceProperties();
        assertEquals(1, instanceProperties.size());
        assertEquals("$inputParameter1", instanceProperties.get(0).getName());
        assertEquals("inputParameter1", XSD_HELPER.getLocalName(instanceProperties.get(0)));
    }

    @Test
    public void testGMonthAliasSerialization() throws Exception {
        XSDHelper XSD_HELPER = _helperContext.getXSDHelper();
        XMLHelper XML_HELPER = _helperContext.getXMLHelper();

        // loading the XSD to default context using the xsd file
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "schema1Alias.sdoxsd");
        XSD_HELPER.define(xsdURL.openStream(), xsdURL.toString());

        // getting the root data object
        URL xmlURL = getClass().getClassLoader().getResource(PACKAGE + "schema1.xml");
        XMLDocument doc = XML_HELPER.load(xmlURL.openStream(), xmlURL.toString(), null);
        assertNotNull(doc);

        assertEquals("--10", doc.getRootObject().get("$inputParameter1"));
        List<Property> instanceProperties = doc.getRootObject().getInstanceProperties();
        assertEquals(1, instanceProperties.size());
        List<String> aliasNames = instanceProperties.get(0).getAliasNames();
        assertNotNull(aliasNames);
        assertEquals(1, aliasNames.size());
        assertEquals("$inputParameter1", aliasNames.get(0));
        assertEquals("inputParameter1", instanceProperties.get(0).getName());
        assertEquals("inputParameter1", XSD_HELPER.getLocalName(instanceProperties.get(0)));

        String xml = XML_HELPER.save(
                        doc.getRootObject(),
                        doc.getRootElementURI(),
                        doc.getRootElementName());
        System.out.println(xml);

        doc = XML_HELPER.load(xml);
        assertNotNull(doc);

        assertEquals("--10", doc.getRootObject().get("$inputParameter1"));
        instanceProperties = doc.getRootObject().getInstanceProperties();
        assertEquals(1, instanceProperties.size());
        aliasNames = instanceProperties.get(0).getAliasNames();
        assertNotNull(aliasNames);
        assertEquals(1, aliasNames.size());
        assertEquals("$inputParameter1", aliasNames.get(0));
        assertEquals("inputParameter1", instanceProperties.get(0).getName());
        assertEquals("inputParameter1", XSD_HELPER.getLocalName(instanceProperties.get(0)));
    }

    @Test
    public void testAllGroup() throws Exception {
        XSDHelper xsdHelper = _helperContext.getXSDHelper();

        // loading the XSD to default context using the xsd file
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "all.xsd");
        List<Type> types = xsdHelper.define(xsdURL.openStream(), xsdURL.toString());

        final String xsd1 = xsdHelper.generate(types);
        System.out.println(xsd1);

        assertEquals(1, types.size());
        assertEquals(true, types.get(0).isSequenced());
        List<Property> props = types.get(0).getProperties();
        assertEquals(2, props.size());
        for (Property property : props) {
            assertEquals(false, property.isMany());
        }

        XSDHelper xsdHelper2 = SapHelperProvider.getNewContext().getXSDHelper();
        List<Type> types2 = xsdHelper2.define(xsd1);
        assertEquals(1, types2.size());
        assertEquals(true, types2.get(0).isSequenced());
        List<Property> props2 = types2.get(0).getProperties();
        assertEquals(2, props2.size());
        for (Property property : props2) {
            assertEquals(false, property.isMany());
        }


        final String xsd2 = xsdHelper2.generate(types2);
        assertEquals(xsd1, xsd2);
    }

    @Test
    public void testBinaryWrapper() throws Exception {
        HelperContext helperContext = _helperContext;
        DataFactory dataFactory = helperContext.getDataFactory();
        TypeHelper typeHelper = helperContext.getTypeHelper();
        XMLHelper xmlHelper = helperContext.getXMLHelper();
        XSDHelper xsdHelper = helperContext.getXSDHelper();

        DataObject hexBin = dataFactory.create("commonj.sdo", "Bytes");
        hexBin.set("value", new byte[]{27,33});

        System.out.println(xmlHelper.save(hexBin, null, "hex"));

        DataObject base64Bin = dataFactory.create(
            URINamePair.OPEN.getURI(),
            URINamePair.OPEN.getName());
        DataObject propObj = dataFactory.create("commonj.sdo", "Property");
        propObj.setString(PropertyConstants.NAME, TypeConstants.VALUE);
        propObj.set(PropertyConstants.TYPE, typeHelper.getType("commonj.sdo", "Bytes"));
        propObj.setBoolean(PropertyConstants.MANY, false);
        propObj.setBoolean(
            xsdHelper.getGlobalProperty(
                URINamePair.DATATYPE_XML_URI, PropertyConstants.XML_ELEMENT, false),
            true);
        propObj.setBoolean(
            xsdHelper.getGlobalProperty(
                URINamePair.DATATYPE_XML_URI, PropertyConstants.XML_SIMPLE_CONTENT, false),
            true);
        propObj.set(
            xsdHelper.getGlobalProperty(
                URINamePair.DATATYPE_XML_URI, PropertyConstants.XSD_TYPE, false),
            URINamePair.SCHEMA_BASE64BINARY.toStandardSdoFormat());
        Property property = typeHelper.defineOpenContentProperty(null, propObj);
        base64Bin.set(property, new byte[]{27,33});

        System.out.println(xmlHelper.save(base64Bin, null, "base64"));
    }

    @Test
    public void testWsdlParsing() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "ECC_MAINTENANCEREQUESTRELRC.wsdl");
        SapXmlHelper xmlHelper = (SapXmlHelper)_helperContext.getXMLHelper();

        Map options = new HashMap();
        options.put(SapXmlHelper.OPTION_KEY_DEFINE_SCHEMAS, SapXmlHelper.OPTION_VALUE_TRUE);
        SapXmlDocument doc = xmlHelper.load(xsdURL.openStream(), xsdURL.toString(), options);
        List types = doc.getDefinedTypes();
        assertNotNull(types);
        assertEquals(28, types.size());

        checkGlobalElement(
            "MaintenanceRequestReleaseConfirmation_sync",
            "MaintenanceRequestReleaseConfirmationMessage_sync",
            true);
        checkGlobalElement(
            "MaintenanceRequestReleaseRequest_sync",
            "MaintenanceRequestReleaseRequestMessage_sync",
            true);
        checkGlobalElement("StandardMessageFault", "+StandardMessageFault", false);
    }

    @Test
    public void testWsdlTypeGeneration() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "MBUS_TEST_FM_03.wsdl");
        SapXmlHelper xmlHelper = (SapXmlHelper)_helperContext.getXMLHelper();

        Map options = new HashMap();
        options.put(SapXmlHelper.OPTION_KEY_DEFINE_SCHEMAS, SapXmlHelper.OPTION_VALUE_TRUE);
        SapXmlDocument doc = xmlHelper.load(xsdURL.openStream(), xsdURL.toString(), options);
        List types = doc.getDefinedTypes();

        InterfaceGenerator generator =
            ((SapTypeHelper)_helperContext.getTypeHelper()).createInterfaceGenerator("c:\\test");
        generator.addPackage("urn:sap-com:document:sap:rfc:functions", "com.sap.rfc.functions");
        generator.addSchemaLocation("urn:sap-com:document:sap:rfc:functions", "MBUS_TEST_FM_03.wsdl");
        generator.setGenerateAnnotations(false);
        generator.generate(types);
    }

    @Test
    public void testTypeDefinitionFromWSDL() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "MBUS_TEST_FM_03.wsdl");
        SapXmlHelper xmlHelper = (SapXmlHelper)_helperContext.getXMLHelper();

        Map<String,String> options = new HashMap<String,String>();
        options.put(SapXmlHelper.OPTION_KEY_DEFINE_SCHEMAS, SapXmlHelper.OPTION_VALUE_TRUE);
        SapXmlDocument doc = xmlHelper.load(xsdURL.openStream(), xsdURL.toString(), options);

        List<Type> types = (List)doc.getDefinedTypes();
        assertEquals(40, types.size());

        boolean found = false;
        for (Type type : types) {
            if (type.getName().equals("SCOL_BO_COMPLEX_PROPERTIES_CPY")) {
                DataObject data = _helperContext.getDataFactory().create(type);
                data.setInt("PROPERTY_ID", 1234);
                found = true;
            }
        }
        assertEquals("type SCOL_BO_COMPLEX_PROPERTIES_CPY not found", true, found);

        // second run
        xsdURL = getClass().getClassLoader().getResource(PACKAGE + "MBUS_TEST_FM_03.wsdl");
        doc = xmlHelper.load(xsdURL.openStream(), xsdURL.toString(), options);

        types = (List)doc.getDefinedTypes();
        assertEquals(40, types.size());

        found = false;
        for (Type type : types) {
            if (type.getName().equals("SCOL_BO_COMPLEX_PROPERTIES_CPY")) {
                DataObject data = _helperContext.getDataFactory().create(type);
                data.setInt("PROPERTY_ID", 1234);
                found = true;
            }
        }
        assertEquals("type SCOL_BO_COMPLEX_PROPERTIES_CPY not found", true, found);
    }

    @Test
    public void testDateValidity() throws Exception {
        // loading the XSD to default context using the xsd file
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "DatePeriod.xsd");
        List<Type> types = _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        assertNotNull(types);
        assertEquals(3, types.size());

        DataObject date  = _helperContext.getDataFactory().create("com.sap.sdo.test", "DatePeriod");
        assertNotNull(date);
        try {
            date.set("EndDate", "0000-00-00");
            fail("processed invalid date: 0000-00-00");
        } catch (IllegalArgumentException ex) {
            assertEquals("invalid date string: 0000-00-00", ex.getMessage());
        }

        date  = _helperContext.getDataFactory().create("com.sap.sdo.test", "CLOSED_DatePeriod");
        assertNotNull(date);
        try {
            date.set("EndDate", "0000-00-00");
            fail("processed invalid date: 0000-00-00");
        } catch (IllegalArgumentException ex) {
            assertEquals("invalid date string: 0000-00-00", ex.getMessage());
        }
    }

    @Test
    public void testJavaNameProperty() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "SimpleSdoNameIntf.xsd");
        List<Type> types = _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        assertNotNull(types);
        assertEquals(1, types.size());
        Type schemaType = types.get(0);
        List<Property> props = schemaType.getProperties();
        assertNotNull(props);
        assertEquals(1, props.size());
        Property foo = props.get(0);
        assertEquals("foo", foo.getName());
        assertEquals(false, ((DataObject)foo).isSet(PropertyType.getJavaNameProperty()));

        Type type = _helperContext.getTypeHelper().getType(SimpleSdoNameIntf.class);
        assertSame(schemaType, type);
        System.out.println(type);
        assertEquals(true, ((DataObject)foo).isSet(PropertyType.getJavaNameProperty()));
        assertEquals("bar", ((DataObject)foo).get(PropertyType.getJavaNameProperty()));

        SimpleSdoNameIntf data = (SimpleSdoNameIntf)_helperContext.getDataFactory().create(type);
        data.setBar("foobar");
        assertEquals("foobar", data.getBar());
        assertEquals("foobar", ((DataObject)data).get("foo"));
    }

    @Test
    public void testSequenced() throws Exception {
        String xsd =
            "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sdox=\"commonj.sdo/xml\"" +
            " elementFormDefault=\"unqualified\" attributeFormDefault=\"unqualified\">\n" +
            "  <xs:element name=\"address\" type=\"address\"/>\n" +
            "  <xs:complexType name=\"address\" sdox:sequence=\"true\">\n" +
    		"    <xs:sequence>\n" +
    		"       <xs:element name=\"street\" type=\"xs:string\"/>\n" +
    		"       <xs:element name=\"city\" type=\"xs:string\"/>\n" +
    		"    </xs:sequence>\n" +
    		"  </xs:complexType>\n" +
    		"</xs:schema>";
        List<Type> types = _helperContext.getXSDHelper().define(xsd);
        assertNotNull(types);
        assertEquals(1, types.size());

        String xml = "<address><street>123 Any Street</street><city>Ottawa</city></address>";
        XMLDocument doc = _helperContext.getXMLHelper().load(xml);
        DataObject address = doc.getRootObject();
        assertSame(types.get(0), address.getType());

        Sequence sequence = address.getSequence();
        assertNotNull(sequence);
        assertEquals(2, sequence.size());

        String xml2 =
            "<address>" +
            "    <street>123 Any Street</street>" +
            "    <city>Ottawa</city>" +
            "</address>";
        XMLDocument doc2 = _helperContext.getXMLHelper().load(xml2);
        DataObject address2 = doc2.getRootObject();
        assertSame(types.get(0), address2.getType());

        Sequence sequence2 = address2.getSequence();
        assertNotNull(sequence2);
        assertEquals(2, sequence2.size());
        //sequence2.addText("bla");
        StringWriter writer = new StringWriter();
        _helperContext.getXMLHelper().save(doc2, writer, null);
        System.out.println(writer.toString());
    }

    @Test
    public void testMixed() throws Exception {
        String xsd =
            "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"" +
            " elementFormDefault=\"unqualified\" attributeFormDefault=\"unqualified\">\n" +
            "  <xs:element name=\"address\" type=\"address\"/>\n" +
            "  <xs:complexType name=\"address\" mixed=\"true\">\n" +
            "    <xs:sequence>\n" +
            "       <xs:element name=\"street\" type=\"xs:string\"/>\n" +
            "       <xs:element name=\"city\" type=\"xs:string\"/>\n" +
            "    </xs:sequence>\n" +
            "  </xs:complexType>\n" +
            "</xs:schema>";
        List<Type> types = _helperContext.getXSDHelper().define(xsd);
        assertNotNull(types);
        assertEquals(1, types.size());

        String xml = "<address><street>123 Any Street</street><city>Ottawa</city></address>";
        XMLDocument doc = _helperContext.getXMLHelper().load(xml);
        DataObject address = doc.getRootObject();
        assertSame(types.get(0), address.getType());

        Sequence sequence = address.getSequence();
        assertNotNull(sequence);
        assertEquals(2, sequence.size());

        String xml2 =
            "<address>" +
            "    <street>123 Any Street</street>" +
            "    <city>Ottawa</city>" +
            "</address>";
        XMLDocument doc2 = _helperContext.getXMLHelper().load(xml2);
        DataObject address2 = doc2.getRootObject();
        assertSame(types.get(0), address2.getType());

        StringWriter writer = new StringWriter();
        _helperContext.getXMLHelper().save(doc2, writer, null);
        System.out.println(writer.toString());

        Sequence sequence2 = address2.getSequence();
        assertNotNull(sequence2);
        for (int i=0; i<sequence2.size(); ++i) {
            System.out.println(i + " -->" + sequence2.getValue(i) + "<--");
        }
        assertEquals(4, sequence2.size());
    }

    @Test
    public void testSdoPath() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "bookstore.xsd");
        List<Type> types = _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        assertNotNull(types);
        assertEquals(3, types.size());

        Type type = _helperContext.getTypeHelper().getType("", "BOOK");
        assertNotNull(type);
        assertNotNull(type.getProperty("subject"));
        assertNotNull(type.getProperty("subject.2"));

        URL url = getClass().getClassLoader().getResource(PACKAGE + "bookstore.xml");
        XMLDocument doc = _helperContext.getXMLHelper().load(url.openStream(), url.toString(), null);
        assertNotNull(doc);

        DataObject root = doc.getRootObject();
        assertNotNull(root);

        DataObject book = root.getDataObject("book[category='COOKING']");
        assertNotNull(book);
        assertEquals("Cooking related book", book.getString("subject"));
        assertEquals("International Cooking", book.getString("subject.2"));

        DataObject graph = _helperContext.getDataFactory().create(DataGraphType.getInstance());
        graph.setDataObject("root", root);

        String original = _helperContext.getXMLHelper().save(graph, null, "graph");
        System.out.println(original);

        ChangeSummary cs = graph.getChangeSummary();
        assertNotNull(cs);
        cs.beginLogging();

        book.setString("subject.2", "Intl. Cooking");
        String changed = _helperContext.getXMLHelper().save(graph, null, "graph");
        System.out.println(changed);
    }

    @Test
    public void testAtSign() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "bookstore.xsd");
        List<Type> types = _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        assertNotNull(types);
        assertEquals(3, types.size());

        URL url = getClass().getClassLoader().getResource(PACKAGE + "bookstore.xml");
        XMLDocument doc = _helperContext.getXMLHelper().load(url.openStream(), url.toString(), null);
        assertNotNull(doc);

        DataObject root = doc.getRootObject();
        assertNotNull(root);

        DataObject book = root.getDataObject("book[@category='COOKING']");
        assertNotNull(book);
        assertEquals("COOKING", book.getString("category"));
        assertEquals("COOKING", book.getString("@category"));
        assertEquals("Cooking related book", book.getString("subject"));
        assertEquals("International Cooking", book.getString("subject.2"));
        assertEquals(null, book.getString("@subject"));
        assertEquals(null, book.getString("@subject.2"));

        book = root.getDataObject("book.2");
        assertEquals("WEB", book.getString("category"));
        assertEquals("WEB", book.getString("@category"));
        assertEquals("Web related book", book.getString("subject"));
        assertEquals(null, book.getString("@subject"));
        assertEquals("en", book.get("title/lang"));
        assertEquals("en", book.get("title/@lang"));

        DataObject data = _helperContext.getDataFactory().create(OpenInterface.class);
        data.setString("prop", "foo");
        assertEquals("foo", data.get("prop"));
        assertEquals(null, data.get("@prop"));
        DataObject propObj = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        propObj.set(PropertyConstants.NAME, "aProp");
        propObj.set(PropertyConstants.TYPE, _helperContext.getTypeHelper().getType("commonj.sdo", "String"));
        propObj.set(
            _helperContext.getTypeHelper().getOpenContentProperty(DATATYPE_XML_URI, PropertyConstants.XML_ELEMENT),
            false);
        Property prop = _helperContext.getTypeHelper().defineOpenContentProperty(null, propObj);
        data.setString(prop, "a string");
        assertEquals("a string", data.get(prop));
        assertEquals("a string", data.get("aProp"));
        assertEquals("a string", data.get("@aProp"));
        data.setString("@attr", "bar");
        //assertEquals("bar", data.get("attr"));
        assertEquals("bar", data.get("@attr"));
    }

    @Test
    public void testNonNameConversion() throws Exception {
        _helperContext.setContextOption(
            SapHelperContext.OPTION_KEY_MIXED_CASE_JAVA_NAMES,
            SapHelperContext.OPTION_VALUE_FALSE);
        assertEquals(
            SapHelperContext.OPTION_VALUE_FALSE,
            _helperContext.getContextOption(SapHelperContext.OPTION_KEY_MIXED_CASE_JAVA_NAMES));

        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "simple.xsd");
        List<SdoType<?>> types = _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        assertNotNull(types);
        assertEquals(4, types.size());

        for (SdoType<?> type : types) {
            if ("CT2".equals(type.getName())) {
                assertEquals("ComplexType2", type.getXmlName());
                assertEquals(
                    "javax.sdo.tck.staticTest.CT2",
                    ((DataObject)type).getString(TypeType.getJavaClassProperty()));
                Property prop = type.getProperty("ct1_list");
                assertNotNull(prop);
                assertEquals("ct1_list", prop.getName());
                assertNull(((DataObject)prop).getString(PropertyType.getJavaNameProperty()));
                assertEquals(
                    CT2.class.getName(), ((DataObject)type).get(TypeType.getJavaClassProperty()));

                DataObject data = _helperContext.getDataFactory().create(CT2.class);
                assertSame(type, data.getType());
                ComplexType1 ct1 = (ComplexType1)_helperContext.getDataFactory().create(ComplexType1.class);
                List<ComplexType1> list = new ArrayList<ComplexType1>();
                list.add(ct1);
                ((CT2)data).setCt1_list(list);
                List<ComplexType1> result = ((CT2)data).getCt1_list();
                assertNotNull(result);
                assertEquals(1, result.size());
                assertSame(ct1, result.get(0));
                result = data.getList("ct1_list");
                assertNotNull(result);
                assertEquals(1, result.size());
                assertSame(ct1, result.get(0));
            }
        }

        try {
            _helperContext.setContextOption(
                SapHelperContext.OPTION_KEY_MIXED_CASE_JAVA_NAMES,
                SapHelperContext.OPTION_VALUE_TRUE);
            fail("Second attempt to test option doesn't fail.");
        } catch (IllegalStateException ex) {
            assertEquals(
                "Option 'com.sap.sdo.api.helper.SapHelperContext.MixedCaseJavaNames' was already set!",
                ex.getMessage());
        }
    }

    @Test
    public void testAnyType() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "anyType.xsd");
        List<?> types = _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        assertNotNull(types);
        assertEquals(1, types.size());

        DataObject dataRoot = _helperContext.getDataFactory().create("sap.com/test", "root");
        Property item2Prop = dataRoot.getType().getProperty("item2");
        dataRoot.setString(item2Prop, "foobar");

        assertEquals(true, item2Prop.getType().isAbstract());
        assertEquals(false, item2Prop.getType().isDataType());

        DataObject dataItem2 = dataRoot.getDataObject(dataRoot.getType().getProperty("item2"));

        assertEquals(false, dataItem2.getType().isAbstract());
        assertEquals(true, dataItem2.getType().isOpen());
        assertEquals(false, dataItem2.getType().isSequenced());
        assertEquals(false, _helperContext.getXSDHelper().isMixed(dataItem2.getType()));

        dataItem2.setString("some_name", "some_value");

//        DataObject propObj = _helperContext.getDataFactory().create("commonj.sdo", "Property");
//        propObj.set(PropertyConstants.NAME, "some_attr");
//        propObj.set(PropertyConstants.TYPE, _helperContext.getTypeHelper().getType("commonj.sdo", "String"));
//        propObj.set(
//            _helperContext.getTypeHelper().getOpenContentProperty(DATATYPE_XML_URI, PropertyConstants.XML_ELEMENT),
//            false);
//        Property prop = _helperContext.getTypeHelper().defineOpenContentProperty(null, propObj);
//        dataItem2.setString(prop, "other_value");

        System.out.println(_helperContext.getXMLHelper().save(dataRoot, "sap.com/test", "root"));

    }

    @Test
    public void testTypedParsing() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "hotel_management.xsd");
        List<?> types = _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        assertNotNull(types);
        assertEquals(6, types.size());

        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "hotelsManagement.xml");
        XMLDocument doc = _helperContext.getXMLHelper().load(xmlUrl.openStream(), xmlUrl.toString(), null);
        assertNotNull(doc);
        DataObject root = doc.getRootObject();
        assertNotNull(root);
        assertEquals("IHotelManagement", root.getType().getName());
        assertEquals("com.sap.sdo.tutorial", root.getType().getURI());
    }

    @Test
    public void testNestedTextNodes() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "render.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());

        DataObject l_dataRoot = _helperContext.getDataFactory().create("sap.com/test", "root");
        DataObject l_dataSomething = _helperContext.getDataFactory().create(URINamePair.MIXEDTEXT_TYPE.getURI(), URINamePair.MIXEDTEXT_TYPE.getName());
        l_dataRoot.getList(l_dataRoot.getInstanceProperty("something")).add(l_dataSomething);
        DataObject l_dataFoo = _helperContext.getDataFactory().create(URINamePair.MIXEDTEXT_TYPE.getURI(), URINamePair.MIXEDTEXT_TYPE.getName());
        DataObject l_dataPropertyFoo = _helperContext.getDataFactory().create(URINamePair.PROPERTY.getURI(), URINamePair.PROPERTY.getName());
        l_dataPropertyFoo.set(PropertyConstants.TYPE_STR, _helperContext.getTypeHelper().getType(URINamePair.MIXEDTEXT_TYPE.getURI(), URINamePair.MIXEDTEXT_TYPE.getName()));
        l_dataPropertyFoo.set(PropertyConstants.NAME_STR, "foo");
        l_dataPropertyFoo.set(PropertyConstants.CONTAINMENT_STR, Boolean.TRUE);
        l_dataPropertyFoo.set(PropertyConstants.MANY_STR, Boolean.TRUE);
        l_dataPropertyFoo.set(PropertyConstants.NULLABLE_STR, Boolean.TRUE);
        l_dataPropertyFoo.set(_helperContext.getTypeHelper().getOpenContentProperty(URINamePair.PROP_XML_XML_ELEMENT.getURI(), URINamePair.PROP_XML_XML_ELEMENT.getName()), Boolean.TRUE);
        Property l_propertyFoo = _helperContext.getTypeHelper().defineOpenContentProperty(null, l_dataPropertyFoo);
        l_dataSomething.getList(l_propertyFoo).add(l_dataFoo);
        l_dataFoo.getSequence().addText("bar");

        String dataFoo = _helperContext.getXMLHelper().save(l_dataFoo, "sap.com/test", "foo");
        assertTrue("Didn't find 'bar' in " + dataFoo, dataFoo.indexOf(">bar<") != -1);

        String dataRoot = _helperContext.getXMLHelper().save(l_dataRoot, "sap.com/test", "root");
        assertTrue("Didn't find 'bar' in " + dataRoot, dataRoot.indexOf(">bar<") != -1);

    }

    @Test
    public void testXsdDefine() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "MainComponentInterface.xsd");
        //List types = XSDHelper.INSTANCE.define(xsdURL.openStream(), xsdURL.toString());
        List types = _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        assertNotNull(types);
        assertEquals(28, types.size());
        types = _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        assertNotNull(types);
        assertEquals(0, types.size());
    }

    @Test
    public void testDateElement() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "DateElement.xsd");
        List<?> types = _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        assertNotNull(types);
        assertEquals(2, types.size());

        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "DateElement.xml");
        XMLDocument doc = _helperContext.getXMLHelper().load(xmlUrl.openStream(), xmlUrl.toString(), null);
        assertNotNull(doc);

        assertLineEquality(
            readFile(xmlUrl),
            _helperContext.getXMLHelper().save(doc.getRootObject(), doc.getRootElementURI(), doc.getRootElementName()));
    }

    @Test
    public void testWrapperElement() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "DateElement.xsd");
        List<Type> types = _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        assertNotNull(types);
        assertEquals(2, types.size());
        assertEquals("DateWrapper", types.get(0).getName());

        DataObject data = _helperContext.getDataFactory().create(types.get(0));
        DataObject element = data.createDataObject("Date");
        element.set("name", "current");

        String xml = _helperContext.getXMLHelper().save(data, null, "wrapper");
        System.out.println(xml);

        XMLDocument doc = _helperContext.getXMLHelper().load(xml);
        assertNotNull(doc);

        assertEquals(
            xml,
            _helperContext.getXMLHelper().save(doc.getRootObject(), doc.getRootElementURI(), doc.getRootElementName()));
    }

    @Test
    public void testEmptySave() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "DateElement.xsd");
        List<Type> types = _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        assertNotNull(types);
        assertEquals(2, types.size());

        try {
            _helperContext.getXMLHelper().save((DataObject)null, "sap.com/glx/", "Date");
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            assertEquals("the DataObject to be rendered must not be null", ex.getMessage());
        }

        String xml = _helperContext.getXMLHelper().save((DataObject)null, "sap.com/glx/", "DateNullable");
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        		"<ns1:DateNullable" +
        		" xmlns:ns1=\"sap.com/glx/\"" +
        		" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"" +
        		" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
        		" xsi:nil=\"true\"></ns1:DateNullable>\n";
        assertLineEquality(expected, xml);
    }

    @Test
    public void testReferenceIncludingPrefix() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "globalChild.xsd");
        List<Type> types = _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        assertNotNull(types);
        assertEquals(1, types.size());

        xsdURL = getClass().getClassLoader().getResource(PACKAGE + "globalChildReferenced.xsd");
        types = _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        assertNotNull(types);
        assertEquals(1, types.size());

        DataObject root = _helperContext.getDataFactory().create(types.get(0));
        DataObject child = root.createDataObject("child");
        child.setString("name", "ein Name");

        root.setDataObject("ref1", child);
        root.setDataObject("ref2", child);

        String xml = _helperContext.getXMLHelper().save(root, "test.sap.com", "root");
        System.out.println(xml);

        XMLDocument doc = _helperContext.getXMLHelper().load(xml);
        assertNotNull(doc);
        DataObject data = doc.getRootObject();
        assertNotNull(data);

        Object child1 = data.get("ref1");
        Object child2 = data.get("child");
        Object child3 = data.get("ref2");

        assertEquals(child2, child1);
        assertEquals(child2, child3);
    }

    @Test
    public void testImplementationBpm() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "sca10/sca-implementation-bpm.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());

        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "a_demo_glx_dc.composite");
        XMLDocument doc = _helperContext.getXMLHelper().load(xmlUrl.openStream(), xmlUrl.toString(), null);
        assertNotNull(doc);

        StringWriter writer = new StringWriter();
        _helperContext.getXMLHelper().save(doc, writer, null);

        assertLineEquality(readFile(xmlUrl), writer.toString());
    }

    @Test
    public void testRPCEncodedWsdlStyle() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "xi/soapencoding.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());

        _helperContext.getTypeHelper().getType(TDefinitions.class);

        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "DocumentCreationWSService.wsdl");
        Map<String, String> options = new HashMap<String,String>();
        options.put(SapXmlHelper.OPTION_KEY_DEFINE_SCHEMAS, SapXmlHelper.OPTION_VALUE_FALSE);
        SapXmlDocument doc = (SapXmlDocument)_helperContext.getXMLHelper().load(xmlUrl.openStream(), xmlUrl.toString(), options);
        assertNotNull(doc);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        _helperContext.getXMLHelper().save(doc, out, null);
        //System.out.println(writer.toString());
        doc = (SapXmlDocument)_helperContext.getXMLHelper().load(new ByteArrayInputStream(out.toByteArray()), null, options);
        List<DataObject> types = doc.getDefinedTypes();
        assertNotNull(types);
        assertEquals(false, types.isEmpty());
    }

    @Test
    public void testSchemaImport() throws Exception {
        Type type = _helperContext.getTypeHelper().getType("http://www.w3.org/2005/08/addressing", "ReferenceParametersType");
        assertNull(type);
        DataObject data = _helperContext.getDataFactory().create(NestedPolicyType.class);
        assertNotNull(data);
        type = _helperContext.getTypeHelper().getType("http://www.w3.org/2005/08/addressing", "ReferenceParametersType");
        assertNotNull(type);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSchemaLocationInStaticSDO() throws Exception {
        _helperContext.getDataFactory().create(InvalidSchemaInfo.class);
    }

    @Test
    public void testIndexedNames() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "indexed_names.xsd");
        _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());

        Property nameProp = _helperContext.getXSDHelper().getGlobalProperty("com.sap.sdo.test", "name", true);
        assertNotNull(nameProp);

        DataObject data = _helperContext.getDataFactory().create(nameProp.getType());
        data.setString("nameA.0", "a name");
        List<String> nameB = data.getList("nameB.1");
        nameB.add("name1");
        nameB.add("name2");
        nameB.add("name3");
        data.setString("nameC.2", "name");
        List<String> nameC = data.getList("nameC");
        nameC.add("nameA");
        nameC.add("nameB");
        nameC.add("nameC");
        data.setString("nameD.1", "another name");

        assertEquals("a name", data.getString("nameA.0"));
        assertEquals("name1", data.getString("nameB.1.0"));
        assertEquals("name2", data.getString("nameB.1.1"));
        assertEquals("name3", data.getString("nameB.1.2"));
        assertEquals("nameA", data.getString("nameC.0"));
        assertEquals("nameB", data.getString("nameC.1"));
        assertEquals("name", data.getString("nameC.2"));
        assertEquals("another name", data.getString("nameD.1"));

        System.out.println(_helperContext.getXMLHelper().save(data, nameProp.getType().getURI(), nameProp.getName()));
    }

    @Test
    public void testEmptySequence() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "EmptySequence.xsd");
        List<Type> types = _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        assertNotNull(types);
        assertEquals(1, types.size());

        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "EmptySequence.xml");
        XMLDocument doc = _helperContext.getXMLHelper().load(xmlUrl.openStream(), xmlUrl.toString(), null);
        assertNotNull(doc);
        assertSame(types.get(0), doc.getRootObject().getType());
    }

    @Test
    public void testQueryFilter() throws Exception {
        URL xsdURL = getClass().getClassLoader().getResource(PACKAGE + "QueryFilter.xsd");
        List<Type> types = _helperContext.getXSDHelper().define(xsdURL.openStream(), xsdURL.toString());
        assertNotNull(types);
        assertEquals(1, types.size());

        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "QueryFilter.xml");
        XMLDocument doc = _helperContext.getXMLHelper().load(xmlUrl.openStream(), xmlUrl.toString(), null);
        assertNotNull(doc);
        assertSame(types.get(0), doc.getRootObject().getType());

        System.out.println(doc.getRootObject().getInstanceProperty("valueLow").getType().getName());
        System.out.println(doc.getRootObject().get("valueLow"));

        StringWriter writer = new StringWriter();
        _helperContext.getXMLHelper().save(doc, writer, null);

        System.out.println(writer);
    }

    @Test
    public void testCreateTPortType() throws Exception {
//        URL url = getClass().getClassLoader().getResource("com/sap/tc/esmp/tools/wsdlexport/model/wsdl/wsdl.xsd");
//        _helperContext.getXSDHelper().define(url .openStream(), url.toString());

        TypeHelper typeHelper = _helperContext.getTypeHelper();
        checkInterface(Schema.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.CanonicalizationMethodType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.CryptoBinary.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.DigestMethodType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.DigestValueType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.DsaKeyValueType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.HmacOutputLengthType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.KeyInfoType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.KeyValueType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.ManifestType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.ObjectType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.PgpDataType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.ReferenceType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.RetrievalMethodType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.RsaKeyValueType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.SignatureMethodType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.SignaturePropertiesType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.SignaturePropertyType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.SignatureType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.SignatureValueType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.SignedInfoType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.SpkiDataType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.TransformsType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.TransformType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.X509DataType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.X509IssuerSerialType.class);
        /*
        checkInterface(org.w3.www.xml.p1998.namespace.Lang.class);
        checkInterface(org.w3.www.xml.p1998.namespace.LangBaseType.class);
        checkInterface(org.w3.www.xml.p1998.namespace.Space.class);
        */

        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wssecurity.utility.AttributedDateTime.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wssecurity.utility.AttributedUri.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wssecurity.utility.TimestampType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wssecurity.utility.TTimestampFault.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wssecurity.secext.AttributedString.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wssecurity.secext.BinarySecurityTokenType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wssecurity.secext.EmbeddedType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wssecurity.secext.EncodedString.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wssecurity.secext.FaultcodeEnum.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wssecurity.secext.KeyIdentifierType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wssecurity.secext.PasswordString.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wssecurity.secext.ReferenceType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wssecurity.secext.SecurityHeaderType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wssecurity.secext.SecurityTokenReferenceType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wssecurity.secext.TransformationParametersType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wssecurity.secext.TUsage.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wssecurity.secext.UsernameTokenType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.policy.AppliesTo.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.policy.OperatorContentType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.policy.Policy.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.policy.PolicyAttachment.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.policy.PolicyReference.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.policy.PolicyUrIs.class);

        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TBinding.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TBindingOperation.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TBindingOperationFault.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TBindingOperationMessage.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TDefinitions.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TDocumentation.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TDocumented.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TExtensibilityElement.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TExtensibleAttributesDocumented.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TExtensibleDocumented.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TFault.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TImport.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TMessage.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TOperation.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TParam.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TPart.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TPort.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TPortType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TService.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TTypes.class);

        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.http.AddressType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.http.BindingType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.http.OperationType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.http.UrlEncoded.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.http.UrlReplacement.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.mime.ContentType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.mime.MultipartRelatedType.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.mime.TMimeXml.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.mime.TPart.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap.EncodingStyle.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap.TAddress.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap.TBinding.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap.TBody.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap.TFault.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap.TFaultRes.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap.THeader.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap.THeaderFault.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap.TOperation.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap.TStyleChoice.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap.UseChoice.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap12.TAddress.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap12.TBinding.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap12.TBody.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap12.TExtensibilityElementOpenAttrs.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap12.TFault.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap12.TFaultRes.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap12.THeader.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap12.THeaderFault.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap12.TOperation.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap12.TParts.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap12.TStyleChoice.class);
        checkInterface(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap12.UseChoice.class);

        TPortType tPortType = (TPortType)_helperContext.getDataFactory().create(TPortType.class);

        UrlEncoded urlEncoded = (UrlEncoded)_helperContext.getDataFactory().create(UrlEncoded.class);
        Property urlEncodedElement = _helperContext.getXSDHelper().getGlobalProperty("http://schemas.xmlsoap.org/wsdl/http/", "urlEncoded", true);
        assertSame(((DataObject)urlEncoded).getType(), urlEncodedElement.getType());
    }

    private void checkInterface(Class<?> pInterface) {
        Type type = _helperContext.getTypeHelper().getType(pInterface);
        assertNotNull(type);
        if (!type.isDataType()) {
            assertEquals(pInterface, type.getInstanceClass());
        }
    }

    /**
     *
     */
    private void checkGlobalElement(String pElementName, String pTypeName, boolean pCheckTypeUri) {
        String elNs = "http://sap.com/xi/SAPGlobal20/Global";
        String tNs = "http://sap.com/xi/EA-APPL/SE/Global";

        Property element = _helperContext.getTypeHelper().getOpenContentProperty(elNs, pElementName);
        assertNotNull(element);
        assertEquals(pElementName, element.getName());
        assertEquals(elNs, _helperContext.getXSDHelper().getNamespaceURI(element));
        Type type = element.getType();
        assertNotNull(type);
        assertEquals(pTypeName, type.getName());
        if (pCheckTypeUri) {
            assertEquals(tNs, type.getURI());
        }
    }

    private static class MyErrorHandler implements ErrorHandler {
        /* (non-Javadoc)
         * @see com.sap.sdo.api.helper.ErrorHandler#handleInvalidValue(java.lang.RuntimeException)
         */
        public void handleInvalidValue(RuntimeException pException) {
        }

        /* (non-Javadoc)
         * @see com.sap.sdo.api.helper.ErrorHandler#handleUnknownProperty(java.lang.RuntimeException)
         */
        public void handleUnknownProperty(RuntimeException pException) {
        }
    }

    /**
     * @return
     */
    private DefaultSchemaResolver getSchemaResolver() {
        if (_helperContext instanceof HelperContextImpl) {
            return new DefaultSchemaResolver(_helperContext);
        } else {
            return new DefaultSchemaResolver(HelperProvider.getDefaultContext());
        }
    }
}
