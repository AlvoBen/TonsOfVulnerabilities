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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.InterfaceGenerator;
import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.helper.SapTypeHelper;
import com.sap.sdo.api.helper.SapXsdHelper;
import com.sap.sdo.api.helper.SchemaResolver;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.TypeHelperImpl;
import com.sap.sdo.impl.xml.DefaultSchemaResolver;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;

/**
 * @author D042774
 *
 */
public class ScaRelatedTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public ScaRelatedTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }
    private XMLHelper _helper = null;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        _helper = _helperContext.getXMLHelper();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        _helper = null;
    }

    @Test
    public void testScaComponentType() throws Exception {
        String schemaFileName = PACKAGE + "sca/sca.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        System.out.println("URI " + url);
        _helperContext.getXSDHelper().define(url.openStream(),url.toString());
        schemaFileName = PACKAGE + "sca/sca-binding-sca.xsd";
        url = getClass().getClassLoader().getResource(schemaFileName);
        System.out.println("URI " + url);
        _helperContext.getXSDHelper().define(url.openStream(),url.toString());
        schemaFileName = PACKAGE + "sca/sca-binding-webservice.xsd";
        url = getClass().getClassLoader().getResource(schemaFileName);
        System.out.println("URI " + url);
        _helperContext.getXSDHelper().define(url.openStream(),url.toString());
        //schemaFileName = PACKAGE + "sca/sca-implementation-composite.xsd";
        //is = getClass().getClassLoader().getResourceAsStream(schemaFileName);
        //_helperContext.getXSDHelper().define(is,url.toString());
        schemaFileName = PACKAGE + "sca/sca-implementation-java.xsd";
        url = getClass().getClassLoader().getResource(schemaFileName);
        System.out.println("URI " + url);
        _helperContext.getXSDHelper().define(url.openStream(),url.toString());
        schemaFileName = PACKAGE + "sca/sca-interface-java.xsd";
        url = getClass().getClassLoader().getResource(schemaFileName);
        System.out.println("URI " + url);
        _helperContext.getXSDHelper().define(url.openStream(),url.toString());
        schemaFileName = PACKAGE + "sca/sca-interface-wsdl.xsd";
        url = getClass().getClassLoader().getResource(schemaFileName);
        System.out.println("URI " + url);
        _helperContext.getXSDHelper().define(url.openStream(),url.toString());

        InterfaceGenerator generator =
            ((SapTypeHelper)_helperContext.getTypeHelper()).createInterfaceGenerator("c:\\test");
        generator.addPackage("http://www.osoa.org/xmlns/sca/0.9", "org.osoa.sca.sdo");
        generator.addSchemaLocation("http://www.osoa.org/xmlns/sca/0.9", "sca/sca.xsd");
        List<Type> types = ((TypeHelperImpl)_helperContext.getTypeHelper()).getTypesForNamespace("http://www.osoa.org/xmlns/sca/1.0");
        generator.generate(types);

        URL uri = getClass().getClassLoader().getResource(PACKAGE+"sca/Accounts.componentType");
        XMLDocument doc = _helper.load(uri.openStream(),null,null);
        DataObject component = doc.getRootObject();
        DataObject service = (DataObject)component.getList("service").get(0);
        assertEquals("AccountService",service.get("name"));
        DataObject intf = service.getDataObject("interface");
        assertEquals("JavaInterface",intf.getType().getName());

        String componentId = ((SdoType)component.getType()).getId(component);
        String serviceId = ((SdoType)service.getType()).getId(service);
        String serviceInterfaceId = ((SdoType)intf.getType()).getId(intf);
        List refs = component.getList("reference");
        String ref0Id = ((SdoType)((DataObject)refs.get(0)).getType()).getId((DataObject)refs.get(0));
        String ref1Id = ((SdoType)((DataObject)refs.get(1)).getType()).getId((DataObject)refs.get(1));
        intf = ((DataObject)refs.get(0)).getDataObject("interface");
        String ref0InterfaceId = ((SdoType)intf.getType()).getId(intf);
        intf = ((DataObject)refs.get(1)).getDataObject("interface");
        String ref1InterfaceId = ((SdoType)intf.getType()).getId(intf);
        DataObject prop = (DataObject)component.getList("property").get(0);
        String propertyId = ((SdoType)prop.getType()).getId(prop);
        String expectedXml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
            "<v1:componentType"+
            " xmlns:v1=\"http://www.osoa.org/xmlns/sca/0.9\""+
            " xmlns:sdo=\"commonj.sdo\""+
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
            " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""+
            ">\n"+
            "  <v1:service name=\"AccountService\">\n"+
            "    <v1:interface.java interface=\"services.account.AccountService\"></v1:interface.java>\n"+
            "  </v1:service>\n"+
            "  <v1:reference name=\"accountDataService\">\n"+
            "    <v1:interface.java interface=\"services.accountdata.AccountDataService\"></v1:interface.java>\n"+
            "  </v1:reference>\n"+
            "  <v1:reference name=\"stockQuoteService\">\n"+
            "    <v1:interface.java interface=\"services.stockquote.StockQuoteService\"></v1:interface.java>\n"+
            "  </v1:reference>\n"+
            "  <v1:property name=\"currency\" type=\"xsd:string\"></v1:property>\n"+
            "</v1:componentType>\n";
        String myXml = _helperContext.getXMLHelper().save(component, component.getType().getURI(), "componentType");
        assertEquals(expectedXml,myXml);
    }
    @Test
    public void testScaModule() throws Exception {
        final String schemaFileName = PACKAGE + "sca/sca.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(),url.toString());
        URL uri = getClass().getClassLoader().getResource(PACKAGE+"sca/sca.module");
        XMLDocument doc = _helper.load(uri.openStream(),null,null);
        DataObject module = doc.getRootObject();
        DataObject component = (DataObject)module.getList("component").get(0);
        DataObject reference = (DataObject)component.get("references");
        Property prop = reference.getInstanceProperty("profileService");
        String myXml = _helperContext.getXMLHelper().save(module, module.getType().getURI(), "module");
        XMLDocument doc2 = _helper.load(new ByteArrayInputStream(myXml.getBytes("UTF-8")),null,null);
        assertTrue(_helperContext.getEqualityHelper().equal(doc.getRootObject(),doc2.getRootObject()));
    }
    @Test
    public void testWsdlLoad() throws Exception {

        DefaultSchemaResolver resolver = new DefaultSchemaResolver(_helperContext);
        String schemaFileName = PACKAGE + "wsdl.xsd";
        URL url =getClass().getClassLoader().getResource(schemaFileName);
        resolver.defineSchemaLocationMapping("http://schemas.xmlsoap.org/wsdl/", url.toString());
//        _helperContext.getXSDHelper().define(url.openStream(),url.toString());

        schemaFileName = PACKAGE + "soap-wsdl.xsd";
        url = getClass().getClassLoader().getResource(schemaFileName);
        ((SapXsdHelper)_helperContext.getXSDHelper()).define(
            url.openStream(),
            url.toString(),
            Collections.singletonMap(SchemaResolver.class.getName(),resolver));

        URL uri = getClass().getClassLoader().getResource(PACKAGE+"wsdl.xml");
        XMLDocument doc = _helper.load(uri.openStream(),null,null);
        DataObject wsdl = doc.getRootObject();
        String myXml = _helperContext.getXMLHelper().save(wsdl, wsdl.getType().getURI(), "wsdl");

        XMLDocument doc2 = _helper.load(new ByteArrayInputStream(myXml.getBytes("UTF-8")),null,null);
        _helperContext.getEqualityHelper().equal(doc.getRootObject(), doc2.getRootObject());

        List<Type> types = ((TypeHelperImpl)_helperContext.getTypeHelper()).getTypesForNamespace("http://schemas.xmlsoap.org/wsdl/soap/");
        String xsd = _helperContext.getXSDHelper().generate(types);
        System.out.println(xsd);

        int index = xsd.indexOf("<xsd:simpleType name=\"encodingStyle\">") - 4;
        assertTrue(index > 0);

        String xsdPart = "    <xsd:simpleType name=\"encodingStyle\">\n"
            + "        <xsd:list itemType=\"xsd:anyURI\"/>\n"
            + "    </xsd:simpleType>";

        assertEquals(xsdPart, xsd.substring(index, index + xsdPart.length()));

        index = xsd.indexOf("<xsd:attribute name=\"message\"");
        assertTrue(index > 0);

        xsdPart = "<xsd:attribute name=\"message\" type=\"xsd:QName\"";

        assertEquals(xsdPart, xsd.substring(index, index + xsdPart.length()));

        index = xsd.indexOf("<xsd:attribute name=\"part\"");
        assertTrue(index > 0);

        xsdPart = "<xsd:attribute name=\"part\" type=\"xsd:NMTOKEN\"";

        assertEquals(xsdPart, xsd.substring(index, index + xsdPart.length()));

    }

    @Test
    public void testSCA() throws Exception {
        final String schemaFileName = PACKAGE + "sca/sca.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(),url.toString());
        Type componentType = _helperContext.getTypeHelper().getType("http://www.osoa.org/xmlns/sca/0.9","ComponentType");
        assertNotNull(componentType);
        assertEquals(3,componentType.getProperties().size());
        assertTrue(componentType.isOpen());
        Property p = componentType.getProperty("service");
        assertEquals("Service", p.getType().getName());
        Type serviceType = p.getType();
        assertEquals(2,serviceType.getProperties().size());
        p = serviceType.getProperty("name");
        assertEquals(_helperContext.getTypeHelper().getType(String.class),p.getType());
        p = serviceType.getProperty("interface");
        Type interfaceType = p.getType();
        assertEquals("Interface",interfaceType.getName());
        String ref = ((DataObject)p).getString("ref");
        int i = ref.lastIndexOf('#');
        assertTrue(i>0);
        String uri = ref.substring(0,i);
        String name = ref.substring(i+1);
        Property head = _helperContext.getXSDHelper().getGlobalProperty(uri,name,true);
        assertEquals(2, ((DataObject)head).getList("substitutes").size());
    }
    @Test
    public void testSCA1() throws Exception {
        final String schemaFileName = PACKAGE + "sca10/sca.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        List<Type> ts = _helperContext.getXSDHelper().define(url.openStream(),url.toString());
        InterfaceGenerator generator =
            ((SapTypeHelper)_helperContext.getTypeHelper()).createInterfaceGenerator("c:\\test");
        generator.addPackage("http://www.osoa.org/xmlns/sca/0.9", "org.osoa.sca.sdo");
        generator.addSchemaLocation("http://www.osoa.org/xmlns/sca/0.9", "sca/sca.xsd");
        generator.generate(ts);
    }
    @Test
    public void testWsdl() throws Exception {
        final String schemaFileName = PACKAGE + "wsdl.xsd";
        InputStream is = getClass().getClassLoader().getResourceAsStream(schemaFileName);
        List ts = _helperContext.getXSDHelper().define(is,null);
        Type tOperationType = _helperContext.getTypeHelper().getType("http://schemas.xmlsoap.org/wsdl/", "tOperation");

        Property nameProperty = tOperationType.getProperty("name");
        assertEquals(tOperationType.getProperties().get(4), nameProperty);

        InterfaceGenerator generator =
            ((SapTypeHelper)_helperContext.getTypeHelper()).createInterfaceGenerator("c:\\test");
        generator.addPackage("http://schemas.xmlsoap.org/wsdl/", "org.xmlsoap.schemas.wsdl");
        generator.addSchemaLocation("http://schemas.xmlsoap.org/wsdl/", "wsdl.xsd");
        generator.generate(ts);
    }
    @Test
    public void testSoap() throws Exception {
        final String schemaFileName = PACKAGE + "SOAP.xsd";
        InputStream is = getClass().getClassLoader().getResourceAsStream(schemaFileName);
        List ts = _helperContext.getXSDHelper().define(is,null);
        InterfaceGenerator generator =
            ((SapTypeHelper)_helperContext.getTypeHelper()).createInterfaceGenerator("c:\\test");
        generator.addPackage(
            "http://schemas.xmlsoap.org/soap/envelope/",
            "org.sap.sca.prototype.bindings.ws.soap.sdo");
        generator.addSchemaLocation(
            "http://schemas.xmlsoap.org/soap/envelope/", "SOAP.xsd");
        generator.generate(ts);
    }
    @Test
    public void testAnnotationsExample() throws Exception {
        final String schemaFileName = PACKAGE + "sdoAnnotationsExample.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(),url.toString());
        Type t = _helperContext.getTypeHelper().getType("http://www.example.com/IPO","PurchaseOrderType");
        assertTrue("type was not provided", t != null);
        Type root = t;
        // TODO:  How is packagename handled?
        //assertEquals("com.example.myPackage",((XSDHelperImpl)_helperContext.getXSDHelper()).getPackageName(t));
        assertEquals(5, t.getProperties().size());
        Property p = t.getProperty("orderDate");
        Type simpleType = p.getType();
        assertTrue(simpleType.isDataType());
        assertEquals("http://www.example.com/IPO",simpleType.getURI());
        assertEquals("MyGregorianDate",simpleType.getName());
        assertEquals(com.sap.xml.datatype.GregorianCalendar.class,simpleType.getInstanceClass());
        p = t.getProperty("items");
        Type itemsType = p.getType();
        assertEquals("http://www.example.com/IPO",itemsType.getURI());
        assertEquals("Items",itemsType.getName());
        p = itemsType.getProperty("item");
        Type itemType = p.getType();
        assertEquals("http://www.example.com/IPO",itemType.getURI());
        assertEquals("Item",itemType.getName());
        assertEquals(itemType.getProperty("comment"), itemType.getProperty("itemComment"));
        p = itemType.getProperty("shipDate");
        assertEquals("String", p.getType().getName());
        assertEquals("commonj.sdo",p.getType().getURI());
        t = _helperContext.getTypeHelper().getType("http://www.example.com/IPO","SKU");
        assertNotNull("could not find type",t);
        assertEquals(com.example.SKU.class, t.getInstanceClass());
        InterfaceGenerator generator =
            ((SapTypeHelper)_helperContext.getTypeHelper()).createInterfaceGenerator("c:\\test");
        generator.addPackage("http://www.example.com/IPO", "com.example.myPackage");
        generator.generate(root);
    }
    @Test
    public void testSoapEncoding() throws Exception {
        final String schemaFileName = PACKAGE + "soap11encoding.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        List<Type> x = _helperContext.getXSDHelper().define(url.openStream(),url.toString());
        InterfaceGenerator generator =
            ((SapTypeHelper)_helperContext.getTypeHelper()).createInterfaceGenerator("c:\\test");
        generator.addSchemaLocation("http://schemas.xmlsoap.org/soap/encoding/", "soap11encoding.xsd");
        generator.generate(x);
    }
}
