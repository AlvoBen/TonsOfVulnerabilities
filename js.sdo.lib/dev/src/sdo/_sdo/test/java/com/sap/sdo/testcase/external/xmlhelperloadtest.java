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

import static com.sap.sdo.api.util.URINamePair.OPEN;
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
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;

import com.example.myPackage.Address;
import com.example.myPackage.Item;
import com.example.myPackage.Items;
import com.example.myPackage.PurchaseOrderType;
import com.sap.sdo.api.helper.SapXmlHelper;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.IPolymorphicProperty;
import com.sap.sdo.testcase.typefac.IntExtension;
import com.sap.sdo.testcase.typefac.OpenInterface;
import com.sap.sdo.testcase.typefac.OppositePropsA;
import com.sap.sdo.testcase.typefac.OppositePropsB;
import com.sap.sdo.testcase.typefac.OppositePropsContainA;
import com.sap.sdo.testcase.typefac.OppositePropsContainB;
import com.sap.sdo.testcase.typefac.Root;
import com.sap.sdo.testcase.typefac.SimpleAttrIntf;
import com.sap.sdo.testcase.typefac.SimpleContainedIntf;
import com.sap.sdo.testcase.typefac.SimpleContainingIntf;
import com.sap.sdo.testcase.typefac.SimpleExtension;
import com.sap.sdo.testcase.typefac.cc.EContainer;
import com.sap.sdo.testcase.typefac.cc.ExtendedSimpleType;
import com.sap.sdo.testcase.typefac.cc.RestrictedSimpleType;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.helper.XSDHelper;

public class XMLHelperLoadTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public XMLHelperLoadTest(String pContextId, Feature pFeature) {
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

    /*
     * Test method for 'com.sap.sdo.impl.xml.XMLHelperImpl.load(DataObject, String, String)'
     */
    @Test
    public void testLoadDataObjectStringString() throws IOException {
        Type t = _helperContext.getTypeHelper().getType(SimpleAttrIntf.class);
        buildRootProp(t);
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<simple:"+t.getName()+" name=\"Stefan\" "+
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "+
            "xmlns:simple=\""+t.getURI()+"\" " +
            "xmlns:sdo=\"commonj.sdo\">\n"
            + "  <data>a data</data>\n"
            + "  <green>true</green>\n"
            + "</simple:"+t.getName()+">\n";
        InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
        XMLDocument doc = _helper.load(is);
        SimpleAttrIntf attr = (SimpleAttrIntf)doc.getRootObject();
        assertEquals(attr.getName(),"Stefan");
        assertEquals(attr.getData(),"a data");
        assertEquals(attr.isGreen(),true);
        t = _helperContext.getTypeHelper().getType(SimpleContainingIntf.class);
        buildRootProp(t);
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<o:"+t.getName()+
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "+
            "xmlns:o=\""+t.getURI()+"\" " +
            "xmlns:sdo=\"commonj.sdo\">\n" +
            "  <x>x</x>\n" +
            "  <inner>\n" +
            "    <name>name</name>\n" +
            "  </inner>\n" +
            "</o:"+t.getName()+">\n";
        is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
        doc = _helper.load(is);
        SimpleContainingIntf outer = (SimpleContainingIntf)doc.getRootObject();
        assertEquals("x",outer.getX());
        SimpleContainedIntf inner = outer.getInner();
        assertNotNull(inner);
        assertEquals(((DataObject)inner).getContainer(),outer);
        assertEquals("name",inner.getName());

        t = _helperContext.getTypeHelper().getType(OppositePropsContainA.class);
        buildRootProp(t);
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<o:"+t.getName()+
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "+
            "xmlns:o=\""+t.getURI()+"\" " +
            "xmlns:sdo=\"commonj.sdo\">\n"
            + "  <bs>\n"
            + "  </bs>\n"
            + "  <bs>\n"
            + "  </bs>\n"
            + "</o:"+t.getName()+">\n";
        is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
        doc = _helper.load(is);
        assertEquals(doc.getXMLVersion(),"1.0");
        OppositePropsContainA oppA = (OppositePropsContainA)doc.getRootObject();
        List bs = oppA.getBs();
        assertEquals(2,bs.size());
        OppositePropsContainB b = (OppositePropsContainB)bs.get(0);
        assertEquals(b.getA(), oppA);
        b = (OppositePropsContainB)bs.get(1);
        assertEquals(b.getA(), oppA);

        t = _helperContext.getTypeHelper().getType(OpenInterface.class);
        _helperContext.getTypeHelper().getType(OppositePropsA.class);
        _helperContext.getTypeHelper().getType(OppositePropsB.class);
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<ns1:"+t.getName()
            + " xmlns:ns1=\""+t.getURI()+"\""
            + " xsi:type=\"ns1:OpenInterface\""
            + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"
            + "  <a xsi:type=\"ns1:OppositePropsA\">\n"
            + "    <bs>#/ns1:" + t.getName() + "/b.0</bs>\n"
            + "    <bs>#/ns1:" + t.getName() + "/b.1</bs>\n"
            + "  </a>\n"
            + "  <b xsi:type=\"ns1:OppositePropsB\">\n"
            + "    <a>#/ns1:" + t.getName() + "/a</a>\n"
            + "  </b>\n"
            + "  <b xsi:type=\"ns1:OppositePropsB\">\n"
            + "    <a>#/ns1:" + t.getName() + "/a</a>\n"
            + "  </b>\n"
            + "</ns1:"+t.getName()+">\n";
        is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
        doc = _helper.load(is);
        StringWriter xml2 = new StringWriter();
        _helper.save(doc, xml2, null);
        System.out.println(xml2);
        assertLineEquality(xml, xml2.toString());

        assertEquals(doc.getXMLVersion(),"1.0");
        DataObject root = doc.getRootObject();
        DataObject aObj = root.getDataObject("a");
        assertNotNull(aObj);
        OppositePropsA a = (OppositePropsA)aObj;
        bs = root.getList("b");
        assertNotNull(bs);
        assertEquals(2,bs.size());
        OppositePropsB b1 = (OppositePropsB)bs.get(0);
        assertTrue(a.getBs().contains(b1));
        assertEquals(b1.getA(), a);
        OppositePropsB b2 = (OppositePropsB)bs.get(0);
        assertTrue(a.getBs().contains(b2));
        assertEquals(b2.getA(), a);
    }

    /*
     * Test method for 'com.sap.sdo.impl.xml.XMLHelperImpl.load(Reader, String, Object)'
     */
    @Test
    public void testLoadReaderStringObject() throws IOException {
        Type t = _helperContext.getTypeHelper().getType(SimpleAttrIntf.class);
        buildRootProp(t);
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<simple:"+t.getName()+" name=\"Stefan\" "+
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "+
            "xmlns:simple=\""+t.getURI()+"\">\n"
            + "  <data>a data</data>\n"
            + "  <green>true</green>\n"
            + "</simple:"+t.getName()+">\n";
        Reader reader = new StringReader(xml);
        XMLDocument doc = _helper.load(reader, null, null);
        SimpleAttrIntf attr = (SimpleAttrIntf)doc.getRootObject();
        assertEquals(attr.getName(),"Stefan");
        assertEquals(attr.getData(),"a data");
        assertEquals(attr.isGreen(),true);
        t = _helperContext.getTypeHelper().getType(SimpleContainingIntf.class);
        buildRootProp(t);
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<o:"+t.getName()+
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "+
            "xmlns:o=\""+t.getURI()+"\" " +
            "xmlns:sdo=\"commonj.sdo\">\n" +
            "  <x>x</x>\n" +
            "  <inner>\n" +
            "    <name>name</name>\n" +
            "  </inner>\n" +
            "</o:"+t.getName()+">\n";
        reader = new StringReader(xml);
        doc = _helper.load(reader, null, null);
        SimpleContainingIntf outer = (SimpleContainingIntf)doc.getRootObject();
        assertEquals("x",outer.getX());
        SimpleContainedIntf inner = outer.getInner();
        assertNotNull(inner);
        assertEquals(((DataObject)inner).getContainer(),outer);
        assertEquals("name",inner.getName());

        t = _helperContext.getTypeHelper().getType(OppositePropsContainA.class);
        buildRootProp(t);
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<o:"+t.getName()+
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "+
            "xmlns:o=\""+t.getURI()+"\" " +
            "xmlns:sdo=\"commonj.sdo\">\n"
            + "  <bs>\n"
            + "  </bs>\n"
            + "  <bs>\n"
            + "  </bs>\n"
            + "</o:"+t.getName()+">\n";
        reader = new StringReader(xml);
        doc = _helper.load(reader, null, null);
        assertEquals(doc.getXMLVersion(),"1.0");
        OppositePropsContainA oppA = (OppositePropsContainA)doc.getRootObject();
        List bs = oppA.getBs();
        assertEquals(2,bs.size());
        OppositePropsContainB b = (OppositePropsContainB)bs.get(0);
        assertEquals(b.getA(), oppA);
        b = (OppositePropsContainB)bs.get(1);
        assertEquals(b.getA(), oppA);

        t = _helperContext.getTypeHelper().getType(OpenInterface.class);
        _helperContext.getTypeHelper().getType(OppositePropsA.class);
        _helperContext.getTypeHelper().getType(OppositePropsB.class);
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<o:"+t.getName()+
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "+
            "xmlns:o=\""+t.getURI()+"\" " +
            "xmlns:sdo=\"commonj.sdo\">\n"
            + "  <a xsi:type=\"o:OppositePropsA\">\n"
            + "    <bs>#/o:" + t.getName() + "/b.0</bs>\n"
            + "    <bs>#/o:" + t.getName() + "/b.1</bs>\n"
            + "  </a>\n"
            + "  <b xsi:type=\"o:OppositePropsB\">\n"
            + "    <a>#/o:" + t.getName() + "/a</a>\n"
            + "  </b>\n"
            + "  <b xsi:type=\"o:OppositePropsB\">\n"
            + "    <a>#/o:" + t.getName() + "/a</a>\n"
            + "  </b>\n"
            + "</o:"+t.getName()+">\n";
        reader = new StringReader(xml);
        doc = _helper.load(reader, null, null);
        assertEquals(doc.getXMLVersion(),"1.0");
        DataObject root = doc.getRootObject();
        DataObject aObj = root.getDataObject("a");
        assertNotNull(aObj);
        OppositePropsA a = (OppositePropsA)aObj;
        bs = root.getList("b");
        assertNotNull(bs);
        assertEquals(2,bs.size());
        OppositePropsB b1 = (OppositePropsB)bs.get(0);
        assertTrue(a.getBs().contains(b1));
        assertEquals(b1.getA(), a);
        OppositePropsB b2 = (OppositePropsB)bs.get(0);
        assertTrue(a.getBs().contains(b2));
        assertEquals(b2.getA(), a);
    }

    @Test
    public void testPOExample() throws Exception {
        final String schemaFileName = PACKAGE + "sdoAnnotationsExample.xsd";
        URL url  = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(), url.toString());

        URL uri = getClass().getClassLoader().getResource(PACKAGE+"poExample.xml");
        XMLDocument doc = _helper.load(uri.openStream(),null,null);
        assertEquals(doc.getEncoding(),"UTF-8");
        assertEquals(doc.getXMLVersion(),"1.0");
        PurchaseOrderType purchaseOrder = (PurchaseOrderType)doc.getRootObject();

        assertEquals(purchaseOrder.getOrderDate().get(GregorianCalendar.YEAR), 1999); // "1999-10-20");
        assertEquals(purchaseOrder.getOrderDate().get(GregorianCalendar.MONTH), 10);
        assertEquals(purchaseOrder.getOrderDate().get(GregorianCalendar.DAY_OF_MONTH), 20);
        Address addr = purchaseOrder.getShipTo();
        assertNotNull(addr);
        assertEquals(((DataObject)addr).getContainer(),purchaseOrder);
        assertEquals("US", addr.getCountry());
        assertEquals("Alice Smith", addr.getName());
        assertEquals("123 Maple Street", addr.getStreet());
        assertEquals("Mill Valley", addr.getCity());
        assertEquals("PA", addr.getState());
        assertEquals("90952", addr.getZip().toString());
        addr = purchaseOrder.getBillTo();
        assertNotNull(addr);
        assertEquals("US", addr.getCountry());
        assertEquals("Robert Smith", addr.getName());
        assertEquals("8 Oak Avenue", addr.getStreet());
        assertEquals("Mill Valley", addr.getCity());
        //assertEquals("PA", addr.getState());
        assertEquals("95819", addr.getZip().toString());

        assertEquals(((DataObject)purchaseOrder).get("comment"), "Hurry, my lawn is going wild!");

        Items items = purchaseOrder.getItems();

        final Item item0 = items.getItem().get(0);
        assertEquals(item0.getPartNum().toString(), "872-AA");
        assertEquals(item0.getProductName(), "Lawnmower");
        Property quantityProp = ((DataObject)item0).getInstanceProperty("quantity");
        assertEquals(_helperContext.getTypeHelper().getType("commonj.sdo", "Int"), quantityProp.getType());
        assertEquals(item0.getQuantity().intValue(), 1);
        assertEquals(item0.getUsPrice().toString(), "148.95");
        assertEquals(item0.getComment(), "Confirm this is electric");

        assertEquals(items.getItem().get(1).getPartNum().toString(), "926-AA");
        assertEquals(items.getItem().get(1).getProductName(), "Baby Monitor");
        assertEquals(items.getItem().get(1).getUsPrice().toString(), "39.98");
        assertEquals(items.getItem().get(1).getShipDate(), "1999-05-21");
    }
    @Test
    public void testSimpleListsExample() throws Exception {
        final String schemaFileName = PACKAGE + "SimpleTypeList.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(),url.toString());

        URL uri = getClass().getClassLoader().getResource(PACKAGE+"simpleTypeLists.xml");
        XMLDocument doc = _helper.load(uri.openStream(),null,null);
        assertEquals(doc.getEncoding(),"UTF-8");
        assertEquals(doc.getXMLVersion(),"1.0");
        DataObject container = doc.getRootObject();
        List x = container.getList("x");
        assertEquals(4, x.size());
        assertEquals("aa",x.get(0));
        assertEquals("bbb",x.get(1));
        assertEquals("111",x.get(2));
        assertEquals("222",x.get(3));
    }
    @Test
    public void testQNameExample() throws Exception {
        final String schemaFileName = PACKAGE + "SimpleTypeURI.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(),url.toString());

        URL uri = getClass().getClassLoader().getResource(PACKAGE+"SimpleTypeURI.xml");
        XMLDocument doc = _helper.load(uri.openStream(),null,null);
        assertEquals(doc.getEncoding(),"UTF-8");
        assertEquals(doc.getXMLVersion(),"1.0");
        DataObject container = doc.getRootObject();
        Property p = container.getInstanceProperty("y");
        assertEquals("URI",p.getType().getName());
        assertEquals("another.xsd#blah",container.get(p));
        List xs = container.getList("x");
        assertEquals("qname.xsd#same",xs.get(0));
        assertEquals("element#blahblah",xs.get(1));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        + "<ns1:container xmlns:ns1=\"qname.xsd\" xmlns:ns2=\"another.xsd\" y=\"ns2:blah\""
        + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
        + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"
        + "  <ns1:x>ns1:same</ns1:x>\n"
        + "  <ns1:x xmlns:ns3=\"element\">ns3:blahblah</ns1:x>\n"
        + "</ns1:container>\n";
        String xml = _helperContext.getXMLHelper().save(container,"qname.xsd","container");
        assertLineEquality(expected, xml);
    }
    @Test
    public void testSimpleContentExample() throws Exception {
        final String schemaFileName = PACKAGE + "SimpleContentType.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(),url.toString());
        URL uri = getClass().getClassLoader().getResource(PACKAGE+"SimpleContentType.xml");
        XMLDocument doc = _helper.load(uri.openStream(),null,null);
        assertEquals(doc.getEncoding(),"UTF-8");
        assertEquals(doc.getXMLVersion(),"1.0");
        DataObject container = doc.getRootObject();
        DataObject intValue = container.getDataObject(0);
        String value = intValue.getString("value");
        assertEquals("2345", value);
        DataObject qnameValue = container.getDataObject(1);
        value = qnameValue.getString("value");
        assertEquals("http://test.com/xxxx#test", value);

        String s = _helper.save(container, "simpleContent.xsd", "simpleContent");
        System.out.println(s);

        InputStream is = new ByteArrayInputStream(s.getBytes("UTF-8"));
        doc = _helper.load(is,null,null);

        container = doc.getRootObject();
        intValue = container.getDataObject(0);
        value = intValue.getString("value");
        assertEquals("2345", value);

        qnameValue = container.getDataObject(1);
        value = qnameValue.getString("value");
        assertEquals("http://test.com/xxxx#test", value);
    }
    @Test
    public void testSimpleInherit() throws Exception {
        _helperContext.getTypeHelper().getType(IPolymorphicProperty.class);
        _helperContext.getTypeHelper().getType(SimpleExtension.class);
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        +"<co:container xsi:type=\"co:IPolymorphicProperty\" xmlns:co=\"com.sap.sdo.testcase.typefac\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
        +"  <a xsi:type=\"co:SimpleExtension\">\n"
        +"  </a>\n"
        +"</co:container>\n";
        InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
        XMLDocument doc = _helper.load(is);
        IPolymorphicProperty o = (IPolymorphicProperty)doc.getRootObject();
        assertEquals("container",doc.getRootElementName());
        assertNotNull(o.getA());
        assertTrue(SimpleExtension.class.isInstance(o.getA()));
    }
    @Test
    public void testInheritenceExample() throws Exception {
        final String schemaFileName = PACKAGE + "companyInheritenceExample.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(), url.toString());

        URL uri = getClass().getClassLoader().getResource(PACKAGE+"inheritExample.xml");
        XMLDocument doc = _helper.load(uri.openStream(),null,null);
        DataObject company = doc.getRootObject();
        assertEquals("CompanyType",company.getType().getName());
        DataObject x = company.getDataObject("employeeOfTheMonth");
        assertNotNull(x);
        assertEquals("Jane Doe", x.getString("name"));
        assertEquals("SalariedEmployeeType",x.getType().getName());
    }
    @Test
    public void testAnyTypeExample() throws Exception {
        String schemaFileName = PACKAGE + "companyExample.xsd";
        InputStream is = getClass().getClassLoader().getResourceAsStream(schemaFileName);
        _helperContext.getXSDHelper().define(is,null);

        schemaFileName = PACKAGE + "anyTypeExample.xsd";
        is = getClass().getClassLoader().getResourceAsStream(schemaFileName);
        _helperContext.getXSDHelper().define(is,null);

        URL url = getClass().getClassLoader().getResource(PACKAGE+"AnyTypeExample.xml");
        is = url.openStream();
        XMLDocument doc = _helper.load(is,null,null);
        assertNotNull(doc);
        assertNotNull(doc.getRootObject());
        assertEquals(1, doc.getRootObject().getType().getProperties().size());
        List objs = doc.getRootObject().getList("any");
        assertEquals(2, objs.size());
        DataObject company = (DataObject)objs.get(0);
        assertEquals("CompanyType",company.getType().getName());
        assertEquals("MegaCorp", company.getString("name"));
        /**  with wrapper object... */
        DataObject value = (DataObject)objs.get(1);
        Property p = value.getInstanceProperty("value");
        assertNotNull(p);
        assertSame(_helperContext.getTypeHelper().getType(String.class), p.getType());
        assertEquals("hello", value.getString(p));
        /**   without wrapper object        **
        assertEquals("hello", objs.get(1));
        **                                  **/
    }

    @Test
    public void testAnyTypeCornerCase() throws Exception {
        String schemaFileName = PACKAGE + "anyTypeExample.xsd";
        InputStream is = getClass().getClassLoader().getResourceAsStream(schemaFileName);
        _helperContext.getXSDHelper().define(is,null);

        URL url = getClass().getClassLoader().getResource(PACKAGE+"AnyTypeCornerCase.xml");
        is = url.openStream();
        XMLDocument doc = _helper.load(is,null,null);
        assertNotNull(doc);
        assertNotNull(doc.getRootObject());
        assertEquals(1, doc.getRootObject().getType().getProperties().size());
        List objs = doc.getRootObject().getList("any");
        assertEquals(15, objs.size());

        DataObject value = (DataObject)objs.get(0);
        Property p = value.getInstanceProperty("value");
        assertNotNull(p);
        assertSame(_helperContext.getTypeHelper().getType(Integer.TYPE), p.getType());
        assertEquals(5, value.get(p));

        value = (DataObject)objs.get(1);
        p = value.getInstanceProperty("value");
        assertNotNull(p);
        assertSame(_helperContext.getTypeHelper().getType(String.class), p.getType());
        assertEquals("hello", value.get(p));

        value = (DataObject)objs.get(2);
        p = value.getInstanceProperty("value");
        assertNotNull(p);
        assertSame(_helperContext.getTypeHelper().getType(String.class), p.getType());
        assertEquals("", value.get(p));

        value = (DataObject)objs.get(3);
        p = value.getInstanceProperty("value");
        assertNotNull(p);
        assertSame(_helperContext.getTypeHelper().getType(Boolean.TYPE), p.getType());
        assertEquals(true, value.get(p));

        value = (DataObject)objs.get(4);
        p = value.getInstanceProperty("value");
        assertNotNull(p);
        assertSame(_helperContext.getTypeHelper().getType(BigDecimal.class), p.getType());
        assertEquals(new BigDecimal("3.14"), value.get(p));

        value = (DataObject)objs.get(5);
        p = value.getInstanceProperty("value");
        assertNotNull(p);
        assertSame(_helperContext.getTypeHelper().getType(Double.TYPE), p.getType());
        assertEquals(5d, value.get(p));

        value = (DataObject)objs.get(6);
        p = value.getInstanceProperty("value");
        assertNotNull(p);
        assertSame(_helperContext.getTypeHelper().getType(Float.TYPE), p.getType());
        assertEquals(3.14f, value.get(p));

        value = (DataObject)objs.get(7);
        p = value.getInstanceProperty("value");
        assertNotNull(p);
        assertSame(_helperContext.getTypeHelper().getType(BigInteger.class), p.getType());
        assertEquals(new BigInteger("5"), value.get(p));

        value = (DataObject)objs.get(8);
        p = value.getInstanceProperty("value");
        assertNotNull(p);
        assertSame(_helperContext.getTypeHelper().getType(Byte.TYPE), p.getType());
        assertEquals((byte)5, value.get(p));

        value = (DataObject)objs.get(9);
        p = value.getInstanceProperty("value");
        assertNotNull(p);
        assertSame(_helperContext.getTypeHelper().getType(Long.TYPE), p.getType());
        assertEquals(5L, value.get(p));

        value = (DataObject)objs.get(10);
        p = value.getInstanceProperty("value");
        assertNotNull(p);
        assertSame(_helperContext.getTypeHelper().getType(Short.TYPE), p.getType());
        assertEquals((short)5, value.get(p));

        value = (DataObject)objs.get(11);
        p = value.getInstanceProperty("value");
        assertNotNull(p);
        assertSame(_helperContext.getTypeHelper().getType("commonj.sdo", "DateTime"), p.getType());
        assertEquals("2007-03-08T09:17:00", value.getString(p));

        value = (DataObject)objs.get(12);
        p = value.getInstanceProperty("value");
        assertNotNull(p);
        assertSame(_helperContext.getTypeHelper().getType("commonj.sdo", "Bytes"), p.getType());
        byte[] bytes = value.getBytes(p);
        byte[] expected = new byte[]{0x5a};
        assertEquals(expected.length, bytes.length);
        for (int i=0; i<bytes.length; ++i) {
            assertEquals(expected[i], bytes[i]);
        }

        value = (DataObject)objs.get(13);
        p = value.getInstanceProperty("value");
        assertNotNull(p);
        assertSame(_helperContext.getTypeHelper().getType("commonj.sdo", "Bytes"), p.getType());
        bytes = value.getBytes(p);
        final String utf8 = new String(bytes, "UTF-8");
        String str = "Hätten Hüte ein ß im Namen, wären sie möglicherweise keine Hüte mehr,\r\n"
            + "sondern Hüße.\r\n";
        assertEquals(str, utf8);

        value = (DataObject)objs.get(14);
        p = value.getInstanceProperty("value");
        assertNotNull(p);
        assertSame(_helperContext.getTypeHelper().getType("commonj.sdo", "URI"), p.getType());
        assertEquals("commonj.sdo#String", value.get(p));

    }

    @Test
    public void testAnyTypeComplexType() throws Exception {
        String schemaFileName = PACKAGE + "anyTypeExample.xsd";
        InputStream is = getClass().getClassLoader().getResourceAsStream(schemaFileName);
        _helperContext.getXSDHelper().define(is,null);

        URL url = getClass().getClassLoader().getResource(PACKAGE+"AnyTypeComplexType.xml");
        is = url.openStream();
        XMLDocument doc = _helper.load(is,null,null);
        StringWriter xml = new StringWriter();
        _helper.save(doc, xml, null);
        System.out.println(xml.toString());
        assertNotNull(doc);
        assertNotNull(doc.getRootObject());
        assertEquals(1, doc.getRootObject().getType().getProperties().size());
        List objs = doc.getRootObject().getList("any");
        assertEquals(1, objs.size());
        DataObject value = (DataObject)objs.get(0);
        Property p = value.getInstanceProperty("test");
        assertNotNull(p);
        assertSame(_helperContext.getTypeHelper().getType(String.class), p.getType());
        assertEquals("hello", value.get(p));

    }
    @Test
    public void testUnsetAttribute() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                    + "<ns1:message"
                    + " xmlns:ns1=\"ipoMessage.xsd\""
                    + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                    + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"
                    + "  <purchaseOrder orderDate=\"2006-11-28\">\n"
                    + "    <shipTo name=\"Alice Smith\" street=\"123 Maple Street\" city=\"Mill Valley\" state=\"PA\" zip=\"90952\" country=\"US\"></shipTo>\n"
                    + "    <billTo name=\"Robert Smith\" street=\"8 Oak Avenue\" city=\"Mill Valley\" zip=\"95819\" country=\"US\"></billTo>\n"
                    + "    <ns2:comment xmlns:ns2=\"http://www.example.com/IPO\">hi</ns2:comment>\n"
                    + "    <items>\n"
                    + "      <item partNum=\"872-AA\">\n"
                    + "        <productName>Lawnmower</productName>\n"
                    + "        <quantity>1</quantity>\n"
                    + "        <USPrice>148.95</USPrice>\n"
                    + "        <ns3:comment xmlns:ns3=\"http://www.example.com/IPO\">Confirm this is electric</ns3:comment>\n"
                    + "      </item>\n"
                    + "      <item partNum=\"926-AA\">\n"
                    + "        <productName>Baby Monitor</productName>\n"
                    + "        <USPrice>39.98</USPrice>\n"
                    + "        <shipDate>1999-05-21</shipDate>\n"
                    + "      </item>\n"
                    + "    </items>\n"
                    + "  </purchaseOrder>\n"
                    + "  <changeSummary create=\"#/ns1:message/purchaseOrder\" xmlns:sdo=\"commonj.sdo\">\n"
                    + "    <ns1:message xsi:type=\"ns1:PurchaseOrderMessageType\" sdo:ref=\"#/ns1:message\" sdo:unset=\"purchaseOrder\"></ns1:message>\n"
                    + "  </changeSummary>\n"
                    + "</ns1:message>\n";
        String schemaFileName = PACKAGE + "ChangeSummaryType.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(),url.toString());
        Type messageType = _helperContext.getTypeHelper().getType("ipoMessage.xsd","PurchaseOrderMessageType");
        DataObject message = _helperContext.getDataFactory().create(messageType);
        message.getChangeSummary().beginLogging();
        URL uri = getClass().getClassLoader().getResource(PACKAGE+"poExample.xml");
        DataObject po = _helper.load(uri.openStream(),null,null).getRootObject();
        message.set("purchaseOrder",po);
        po.set("orderDate","2006-11-28");
        assertFalse(((SdoProperty)po.getType().getProperty("comment")).getIndex()<0);
        po.set(po.getType().getProperty("comment"), "hi");

        String myXml = _helperContext.getXMLHelper().save(message, messageType.getURI(), "message");
        assertLineEquality(xml,myXml);
        DataObject x = _helperContext.getXMLHelper().load(myXml).getRootObject();
        assertTrue(x.isSet("purchaseOrder"));
        String myXml2 = _helperContext.getXMLHelper().save(x, messageType.getURI(), "message");
        assertEquals(myXml,myXml2);
        x.getChangeSummary().undoChanges();
        assertFalse(x.isSet("purchaseOrder"));
        assertLineEquality(xml,myXml);
    }

    @Test
    public void testStreamSource() throws Exception {
        Type t = _helperContext.getTypeHelper().getType(SimpleAttrIntf.class);
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<simple:"+t.getName()+" name=\"Stefan\" "+
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "+
            "xmlns:simple=\""+t.getURI()+"\" " +
            "xmlns:sdo=\"commonj.sdo\">\n"
            + "  <data>a data</data>\n"
            + "  <green>true</green>\n"
            + "</simple:"+t.getName()+">\n";
        Source source = new StreamSource(new ByteArrayInputStream(xml.getBytes("UTF-8")));
        XMLDocument doc = _helper.load(source, null, null);
        SimpleAttrIntf attr = (SimpleAttrIntf)doc.getRootObject();
        assertEquals(attr.getName(),"Stefan");
        assertEquals(attr.getData(),"a data");
        assertEquals(attr.isGreen(),true);

        t = _helperContext.getTypeHelper().getType(SimpleContainingIntf.class);
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<o:"+t.getName()+
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "+
            "xmlns:o=\""+t.getURI()+"\" " +
            "xmlns:sdo=\"commonj.sdo\">\n" +
            "  <x>x</x>\n" +
            "  <inner>\n" +
            "    <name>name</name>\n" +
            "  </inner>\n" +
            "</o:"+t.getName()+">\n";
        source = new StreamSource(new ByteArrayInputStream(xml.getBytes("UTF-8")));
        doc = _helper.load(source, null, null);
        SimpleContainingIntf outer = (SimpleContainingIntf)doc.getRootObject();
        assertEquals("x",outer.getX());
        SimpleContainedIntf inner = outer.getInner();
        assertNotNull(inner);
        assertEquals(((DataObject)inner).getContainer(),outer);
        assertEquals("name",inner.getName());

        t = _helperContext.getTypeHelper().getType(OppositePropsContainA.class);
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<o:"+t.getName()+
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "+
            "xmlns:o=\""+t.getURI()+"\" " +
            "xmlns:sdo=\"commonj.sdo\">\n"
            + "  <bs>\n"
            + "  </bs>\n"
            + "  <bs>\n"
            + "  </bs>\n"
            + "</o:"+t.getName()+">\n";
        source = new StreamSource(new ByteArrayInputStream(xml.getBytes("UTF-8")));
        doc = _helper.load(source, null, null);
        assertEquals(doc.getXMLVersion(),"1.0");
        OppositePropsContainA oppA = (OppositePropsContainA)doc.getRootObject();
        List bs = oppA.getBs();
        assertEquals(2,bs.size());
        OppositePropsContainB b = (OppositePropsContainB)bs.get(0);
        assertEquals(b.getA(), oppA);
        b = (OppositePropsContainB)bs.get(1);
        assertEquals(b.getA(), oppA);

        t = _helperContext.getTypeHelper().getType(OpenInterface.class);
        _helperContext.getTypeHelper().getType(OppositePropsA.class);
        _helperContext.getTypeHelper().getType(OppositePropsB.class);
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<o:"+t.getName()+
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "+
            "xmlns:o=\""+t.getURI()+"\" " +
            "xmlns:sdo=\"commonj.sdo\">\n"
            + "  <a xsi:type=\"o:OppositePropsA\">\n"
            + "    <bs>#/o:" + t.getName() + "/b.0</bs>\n"
            + "    <bs>#/o:" + t.getName() + "/b.1</bs>\n"
            + "  </a>\n"
            + "  <b xsi:type=\"o:OppositePropsB\">\n"
            + "    <a>#/o:" + t.getName() + "/a</a>\n"
            + "  </b>\n"
            + "  <b xsi:type=\"o:OppositePropsB\">\n"
            + "    <a>#/o:" + t.getName() + "/a</a>\n"
            + "  </b>\n"
            + "</o:"+t.getName()+">\n";
        source = new StreamSource(new ByteArrayInputStream(xml.getBytes("UTF-8")));
        doc = _helper.load(source, null, null);
        assertEquals(doc.getXMLVersion(),"1.0");
        DataObject root = doc.getRootObject();
        DataObject aObj = root.getDataObject("a");
        assertNotNull(aObj);
        OppositePropsA a = (OppositePropsA)aObj;
        bs = root.getList("b");
        assertNotNull(bs);
        assertEquals(2,bs.size());
        OppositePropsB b1 = (OppositePropsB)bs.get(0);
        assertTrue(a.getBs().contains(b1));
        assertEquals(b1.getA(), a);
        OppositePropsB b2 = (OppositePropsB)bs.get(0);
        assertTrue(a.getBs().contains(b2));
        assertEquals(b2.getA(), a);
    }

    @Test
    public void testStAXSourceXMLStreamReader() throws Exception {
        Type t = _helperContext.getTypeHelper().getType(SimpleAttrIntf.class);
        buildRootProp(t);
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<simple:"+t.getName()+" name=\"Stefan\" "+
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "+
            "xmlns:simple=\""+t.getURI()+"\" " +
            "xmlns:sdo=\"commonj.sdo\">\n"
            + "  <data>a data</data>\n"
            + "  <green>true</green>\n"
            + "</simple:"+t.getName()+">\n";
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLStreamReader parser = xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(xml.getBytes("UTF-8")));
        Source source = new StAXSource(parser);
        XMLDocument doc = _helper.load(source, null, null);
        SimpleAttrIntf attr = (SimpleAttrIntf)doc.getRootObject();
        assertEquals(attr.getName(),"Stefan");
        assertEquals(attr.getData(),"a data");
        assertEquals(attr.isGreen(),true);

        t = _helperContext.getTypeHelper().getType(SimpleContainingIntf.class);
        buildRootProp(t);
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<o:"+t.getName()+
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "+
            "xmlns:o=\""+t.getURI()+"\" " +
            "xmlns:sdo=\"commonj.sdo\">\n" +
            "  <x>x</x>\n" +
            "  <inner>\n" +
            "    <name>name</name>\n" +
            "  </inner>\n" +
            "</o:"+t.getName()+">\n";
        parser = xmlInputFactory.createXMLStreamReader(new StringReader(xml));
        source = new StAXSource(parser);
        doc = _helper.load(source, null, null);
        SimpleContainingIntf outer = (SimpleContainingIntf)doc.getRootObject();
        assertEquals("x",outer.getX());
        SimpleContainedIntf inner = outer.getInner();
        assertNotNull(inner);
        assertEquals(((DataObject)inner).getContainer(),outer);
        assertEquals("name",inner.getName());

        t = _helperContext.getTypeHelper().getType(OppositePropsContainA.class);
        buildRootProp(t);
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<o:"+t.getName()+
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "+
            "xmlns:o=\""+t.getURI()+"\" " +
            "xmlns:sdo=\"commonj.sdo\">\n"
            + "  <bs>\n"
            + "  </bs>\n"
            + "  <bs>\n"
            + "  </bs>\n"
            + "</o:"+t.getName()+">\n";
        parser = xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(xml.getBytes("UTF-8")));
        source = new StAXSource(parser);
        doc = _helper.load(source, null, null);
        assertEquals(doc.getXMLVersion(),"1.0");
        OppositePropsContainA oppA = (OppositePropsContainA)doc.getRootObject();
        List bs = oppA.getBs();
        assertEquals(2,bs.size());
        OppositePropsContainB b = (OppositePropsContainB)bs.get(0);
        assertEquals(b.getA(), oppA);
        b = (OppositePropsContainB)bs.get(1);
        assertEquals(b.getA(), oppA);

        t = _helperContext.getTypeHelper().getType(OpenInterface.class);
        buildRootProp(t);
        _helperContext.getTypeHelper().getType(OppositePropsA.class);
        _helperContext.getTypeHelper().getType(OppositePropsB.class);
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<o:"+t.getName()+
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "+
            "xmlns:o=\""+t.getURI()+"\" " +
            "xmlns:sdo=\"commonj.sdo\">\n"
            + "  <a xsi:type=\"o:OppositePropsA\">\n"
            + "    <bs>#/o:" + t.getName() + "/b.0</bs>\n"
            + "    <bs>#/o:" + t.getName() + "/b.1</bs>\n"
            + "  </a>\n"
            + "  <b xsi:type=\"o:OppositePropsB\">\n"
            + "    <a>#/o:" + t.getName() + "/a</a>\n"
            + "  </b>\n"
            + "  <b xsi:type=\"o:OppositePropsB\">\n"
            + "    <a>#/o:" + t.getName() + "/a</a>\n"
            + "  </b>\n"
            + "</o:"+t.getName()+">\n";
        parser = xmlInputFactory.createXMLStreamReader(new StringReader(xml));
        source = new StAXSource(parser);
        doc = _helper.load(source, null, null);
        assertEquals(doc.getXMLVersion(),"1.0");
        DataObject root = doc.getRootObject();
        DataObject aObj = root.getDataObject("a");
        assertNotNull(aObj);
        OppositePropsA a = (OppositePropsA)aObj;
        bs = root.getList("b");
        assertNotNull(bs);
        assertEquals(2,bs.size());
        OppositePropsB b1 = (OppositePropsB)bs.get(0);
        assertTrue(a.getBs().contains(b1));
        assertEquals(b1.getA(), a);
        OppositePropsB b2 = (OppositePropsB)bs.get(0);
        assertTrue(a.getBs().contains(b2));
        assertEquals(b2.getA(), a);
    }

    @Test
    public void testStAXSourceXMLEventReader() throws Exception {
        Type t = _helperContext.getTypeHelper().getType(SimpleAttrIntf.class);
        buildRootProp(t);
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<simple:"+t.getName()+" name=\"Stefan\" "+
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "+
            "xmlns:simple=\""+t.getURI()+"\" " +
            "xmlns:sdo=\"commonj.sdo\">\n"
            + "  <data>a data</data>\n"
            + "  <green>true</green>\n"
            + "</simple:"+t.getName()+">\n";
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLEventReader parser = xmlInputFactory.createXMLEventReader(new ByteArrayInputStream(xml.getBytes("UTF-8")));
        Source source = new StAXSource(parser);
        XMLDocument doc = _helper.load(source, null, null);
        SimpleAttrIntf attr = (SimpleAttrIntf)doc.getRootObject();
        assertEquals(attr.getName(),"Stefan");
        assertEquals(attr.getData(),"a data");
        assertEquals(attr.isGreen(),true);

        t = _helperContext.getTypeHelper().getType(SimpleContainingIntf.class);
        buildRootProp(t);
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<o:"+t.getName()+
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "+
            "xmlns:o=\""+t.getURI()+"\" " +
            "xmlns:sdo=\"commonj.sdo\">\n" +
            "  <x>x</x>\n" +
            "  <inner>\n" +
            "    <name>name</name>\n" +
            "  </inner>\n" +
            "</o:"+t.getName()+">\n";
        parser = xmlInputFactory.createXMLEventReader(new StringReader(xml));
        source = new StAXSource(parser);
        doc = _helper.load(source, null, null);
        SimpleContainingIntf outer = (SimpleContainingIntf)doc.getRootObject();
        assertEquals("x",outer.getX());
        SimpleContainedIntf inner = outer.getInner();
        assertNotNull(inner);
        assertEquals(((DataObject)inner).getContainer(),outer);
        assertEquals("name",inner.getName());

        t = _helperContext.getTypeHelper().getType(OppositePropsContainA.class);
        buildRootProp(t);
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<o:"+t.getName()+
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "+
            "xmlns:o=\""+t.getURI()+"\" " +
            "xmlns:sdo=\"commonj.sdo\">\n"
            + "  <bs>\n"
            + "  </bs>\n"
            + "  <bs>\n"
            + "  </bs>\n"
            + "</o:"+t.getName()+">\n";
        parser = xmlInputFactory.createXMLEventReader(new ByteArrayInputStream(xml.getBytes("UTF-8")));
        source = new StAXSource(parser);
        doc = _helper.load(source, null, null);
        assertEquals(doc.getXMLVersion(),"1.0");
        OppositePropsContainA oppA = (OppositePropsContainA)doc.getRootObject();
        List bs = oppA.getBs();
        assertEquals(2,bs.size());
        OppositePropsContainB b = (OppositePropsContainB)bs.get(0);
        assertEquals(b.getA(), oppA);
        b = (OppositePropsContainB)bs.get(1);
        assertEquals(b.getA(), oppA);

        t = _helperContext.getTypeHelper().getType(OpenInterface.class);
        buildRootProp(t);
        _helperContext.getTypeHelper().getType(OppositePropsA.class);
        _helperContext.getTypeHelper().getType(OppositePropsB.class);
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<o:"+t.getName()+
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "+
            "xmlns:o=\""+t.getURI()+"\" " +
            "xmlns:sdo=\"commonj.sdo\">\n"
            + "  <a xsi:type=\"o:OppositePropsA\">\n"
            + "    <bs>#/o:" + t.getName() + "/b.0</bs>\n"
            + "    <bs>#/o:" + t.getName() + "/b.1</bs>\n"
            + "  </a>\n"
            + "  <b xsi:type=\"o:OppositePropsB\">\n"
            + "    <a>#/o:" + t.getName() + "/a</a>\n"
            + "  </b>\n"
            + "  <b xsi:type=\"o:OppositePropsB\">\n"
            + "    <a>#/o:" + t.getName() + "/a</a>\n"
            + "  </b>\n"
            + "</o:"+t.getName()+">\n";
        parser = xmlInputFactory.createXMLEventReader(new StringReader(xml));
        source = new StAXSource(parser);
        doc = _helper.load(source, null, null);
        assertEquals(doc.getXMLVersion(),"1.0");
        DataObject root = doc.getRootObject();
        DataObject aObj = root.getDataObject("a");
        assertNotNull(aObj);
        OppositePropsA a = (OppositePropsA)aObj;
        bs = root.getList("b");
        assertNotNull(bs);
        assertEquals(2,bs.size());
        OppositePropsB b1 = (OppositePropsB)bs.get(0);
        assertTrue(a.getBs().contains(b1));
        assertEquals(b1.getA(), a);
        OppositePropsB b2 = (OppositePropsB)bs.get(0);
        assertTrue(a.getBs().contains(b2));
        assertEquals(b2.getA(), a);
    }

    @Test
    public void testSAXSource() throws Exception {
        Type t = _helperContext.getTypeHelper().getType(SimpleAttrIntf.class);
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<simple:"+t.getName()+" name=\"Stefan\" "+
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "+
            "xmlns:simple=\""+t.getURI()+"\" " +
            "xmlns:sdo=\"commonj.sdo\">\n"
            + "  <data>a data</data>\n"
            + "  <green>true</green>\n"
            + "</simple:"+t.getName()+">\n";
        Source source = new SAXSource(new InputSource(new ByteArrayInputStream(xml.getBytes("UTF-8"))));
        XMLDocument doc = _helper.load(source, null, null);
        SimpleAttrIntf attr = (SimpleAttrIntf)doc.getRootObject();
        assertEquals(attr.getName(),"Stefan");
        assertEquals(attr.getData(),"a data");
        assertEquals(attr.isGreen(),true);

        t = _helperContext.getTypeHelper().getType(SimpleContainingIntf.class);
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<o:"+t.getName()+
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "+
            "xmlns:o=\""+t.getURI()+"\" " +
            "xmlns:sdo=\"commonj.sdo\">\n" +
            "  <x>x</x>\n" +
            "  <inner>\n" +
            "    <name>name</name>\n" +
            "  </inner>\n" +
            "</o:"+t.getName()+">\n";
        source = new SAXSource(new InputSource(new ByteArrayInputStream(xml.getBytes("UTF-8"))));
        doc = _helper.load(source, null, null);
        SimpleContainingIntf outer = (SimpleContainingIntf)doc.getRootObject();
        assertEquals("x",outer.getX());
        SimpleContainedIntf inner = outer.getInner();
        assertNotNull(inner);
        assertEquals(((DataObject)inner).getContainer(),outer);
        assertEquals("name",inner.getName());

        t = _helperContext.getTypeHelper().getType(OppositePropsContainA.class);
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<o:"+t.getName()+
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "+
            "xmlns:o=\""+t.getURI()+"\" " +
            "xmlns:sdo=\"commonj.sdo\">\n"
            + "  <bs>\n"
            + "  </bs>\n"
            + "  <bs>\n"
            + "  </bs>\n"
            + "</o:"+t.getName()+">\n";
        source = new SAXSource(new InputSource(new ByteArrayInputStream(xml.getBytes("UTF-8"))));
        doc = _helper.load(source, null, null);
        assertEquals(doc.getXMLVersion(),"1.0");
        OppositePropsContainA oppA = (OppositePropsContainA)doc.getRootObject();
        List bs = oppA.getBs();
        assertEquals(2,bs.size());
        OppositePropsContainB b = (OppositePropsContainB)bs.get(0);
        assertEquals(b.getA(), oppA);
        b = (OppositePropsContainB)bs.get(1);
        assertEquals(b.getA(), oppA);

        t = _helperContext.getTypeHelper().getType(OpenInterface.class);
        _helperContext.getTypeHelper().getType(OppositePropsA.class);
        _helperContext.getTypeHelper().getType(OppositePropsB.class);
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<o:"+t.getName()+
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "+
            "xmlns:o=\""+t.getURI()+"\" " +
            "xmlns:sdo=\"commonj.sdo\">\n"
            + "  <a xsi:type=\"o:OppositePropsA\">\n"
            + "    <bs>#/o:" + t.getName() + "/b.0</bs>\n"
            + "    <bs>#/o:" + t.getName() + "/b.1</bs>\n"
            + "  </a>\n"
            + "  <b xsi:type=\"o:OppositePropsB\">\n"
            + "    <a>#/o:" + t.getName() + "/a</a>\n"
            + "  </b>\n"
            + "  <b xsi:type=\"o:OppositePropsB\">\n"
            + "    <a>#/o:" + t.getName() + "/a</a>\n"
            + "  </b>\n"
            + "</o:"+t.getName()+">\n";
        source = new SAXSource();
        ((SAXSource)source).setInputSource(new InputSource(new ByteArrayInputStream(xml.getBytes("UTF-8"))));
        doc = _helper.load(source, null, null);
        assertEquals(doc.getXMLVersion(),"1.0");
        DataObject root = doc.getRootObject();
        DataObject aObj = root.getDataObject("a");
        assertNotNull(aObj);
        OppositePropsA a = (OppositePropsA)aObj;
        bs = root.getList("b");
        assertNotNull(bs);
        assertEquals(2,bs.size());
        OppositePropsB b1 = (OppositePropsB)bs.get(0);
        assertTrue(a.getBs().contains(b1));
        assertEquals(b1.getA(), a);
        OppositePropsB b2 = (OppositePropsB)bs.get(0);
        assertTrue(a.getBs().contains(b2));
        assertEquals(b2.getA(), a);
    }

    @Test
    public void testDOMSource() throws Exception {
        Type t = _helperContext.getTypeHelper().getType(SimpleAttrIntf.class);
        buildRootProp(t);
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<simple:"+t.getName()+" name=\"Stefan\" "+
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "+
            "xmlns:simple=\""+t.getURI()+"\" " +
            "xmlns:sdo=\"commonj.sdo\">\n"
            + "  <data>a data</data>\n"
            + "  <green>true</green>\n"
            + "</simple:"+t.getName()+">\n";
        Source source = new StreamSource(new ByteArrayInputStream(xml.getBytes("UTF-8")));
        DOMResult result = new DOMResult();
        TransformerFactory.newInstance().newTransformer().transform(source, result);
        DOMSource domSource = new DOMSource(result.getNode(), result.getSystemId());
        XMLDocument doc = _helper.load(domSource, null, null);
        SimpleAttrIntf attr = (SimpleAttrIntf)doc.getRootObject();
        assertEquals(attr.getName(),"Stefan");
        assertEquals(attr.getData(),"a data");
        assertEquals(attr.isGreen(),true);

        t = _helperContext.getTypeHelper().getType(SimpleContainingIntf.class);
        buildRootProp(t);
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<o:"+t.getName()+
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "+
            "xmlns:o=\""+t.getURI()+"\" " +
            "xmlns:sdo=\"commonj.sdo\">\n" +
            "  <x>x</x>\n" +
            "  <inner>\n" +
            "    <name>name</name>\n" +
            "  </inner>\n" +
            "</o:"+t.getName()+">\n";
        source = new StreamSource(new ByteArrayInputStream(xml.getBytes("UTF-8")));
        result = new DOMResult();
        TransformerFactory.newInstance().newTransformer().transform(source, result);
        domSource = new DOMSource(result.getNode(), result.getSystemId());
        doc = _helper.load(domSource, null, null);
        SimpleContainingIntf outer = (SimpleContainingIntf)doc.getRootObject();
        assertEquals("x",outer.getX());
        SimpleContainedIntf inner = outer.getInner();
        assertNotNull(inner);
        assertEquals(((DataObject)inner).getContainer(),outer);
        assertEquals("name",inner.getName());

        t = _helperContext.getTypeHelper().getType(OppositePropsContainA.class);
        buildRootProp(t);
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<o:"+t.getName()+
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "+
            "xmlns:o=\""+t.getURI()+"\" " +
            "xmlns:sdo=\"commonj.sdo\">\n"
            + "  <bs>\n"
            + "  </bs>\n"
            + "  <bs>\n"
            + "  </bs>\n"
            + "</o:"+t.getName()+">\n";
        source = new StreamSource(new ByteArrayInputStream(xml.getBytes("UTF-8")));
        result = new DOMResult();
        TransformerFactory.newInstance().newTransformer().transform(source, result);
        domSource = new DOMSource(result.getNode(), result.getSystemId());
        doc = _helper.load(domSource, null, null);
        assertEquals(doc.getXMLVersion(),"1.0");
        OppositePropsContainA oppA = (OppositePropsContainA)doc.getRootObject();
        List bs = oppA.getBs();
        assertEquals(2,bs.size());
        OppositePropsContainB b = (OppositePropsContainB)bs.get(0);
        assertEquals(b.getA(), oppA);
        b = (OppositePropsContainB)bs.get(1);
        assertEquals(b.getA(), oppA);

        t = _helperContext.getTypeHelper().getType(OpenInterface.class);
        buildRootProp(t);
        _helperContext.getTypeHelper().getType(OppositePropsA.class);
        _helperContext.getTypeHelper().getType(OppositePropsB.class);
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<o:"+t.getName()+
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "+
            "xmlns:o=\""+t.getURI()+"\" " +
            "xmlns:sdo=\"commonj.sdo\">\n"
            + "  <a xsi:type=\"o:OppositePropsA\">\n"
            + "    <bs>#/o:" + t.getName() + "/b.0</bs>\n"
            + "    <bs>#/o:" + t.getName() + "/b.1</bs>\n"
            + "  </a>\n"
            + "  <b xsi:type=\"o:OppositePropsB\">\n"
            + "    <a>#/o:" + t.getName() + "/a</a>\n"
            + "  </b>\n"
            + "  <b xsi:type=\"o:OppositePropsB\">\n"
            + "    <a>#/o:" + t.getName() + "/a</a>\n"
            + "  </b>\n"
            + "</o:"+t.getName()+">\n";
        source = new StreamSource(new ByteArrayInputStream(xml.getBytes("UTF-8")));
        result = new DOMResult();
        TransformerFactory.newInstance().newTransformer().transform(source, result);
        domSource = new DOMSource(result.getNode(), result.getSystemId());
        doc = _helper.load(domSource, null, null);
        assertEquals(doc.getXMLVersion(),"1.0");
        DataObject root = doc.getRootObject();
        DataObject aObj = root.getDataObject("a");
        assertNotNull(aObj);
        OppositePropsA a = (OppositePropsA)aObj;
        bs = root.getList("b");
        assertNotNull(bs);
        assertEquals(2,bs.size());
        OppositePropsB b1 = (OppositePropsB)bs.get(0);
        assertTrue(a.getBs().contains(b1));
        assertEquals(b1.getA(), a);
        OppositePropsB b2 = (OppositePropsB)bs.get(0);
        assertTrue(a.getBs().contains(b2));
        assertEquals(b2.getA(), a);
    }

    @Test
    public void testLoadString() {
        assertEquals(null, _helper.load((String)null));
    }

    @Test
    public void testLoadReaderMap() throws Exception {
        String fileName = PACKAGE + "message_1K.xml";
        URL url = getClass().getClassLoader().getResource(fileName);

        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(url.openStream());
        while(reader.next()!= XMLStreamConstants.START_ELEMENT) {}
        reader.nextTag();

        Object obj =
            ((SapXmlHelper)_helper).load(reader, null);

        assertNotNull(obj);
        assertTrue(obj instanceof DataObject);
        DataObject data = (DataObject)obj;
        _helperContext.getXSDHelper().isMixed(data.getType());
        assertNotNull(data.getInstanceProperty("title"));
        assertEquals("4711", data.getString("title"));
        assertNotNull(data.getInstanceProperty("description"));
        assertEquals("4711", data.getString("description"));
        assertNotNull(data.getInstanceProperty("language"));
        assertEquals("de-DE", data.getString("language"));
        assertNotNull(data.getInstanceProperty("item"));
    }

    @Test
    public void testLoadReaderStringStringMap() throws Exception {
        try {
            ((SapXmlHelper)_helper).load(null, null, null, null);
            fail("expected XMLStreamException");
        } catch (XMLStreamException ex) {
            assertEquals("Type null#null is unknown in HelperContext", ex.getMessage());
        }

        String fileName = PACKAGE + "message_1K.xml";
        URL url = getClass().getClassLoader().getResource(fileName);

        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(url.openStream());
        while(reader.next()!= XMLStreamConstants.START_ELEMENT) {}
        reader.nextTag();

        Object obj =
            ((SapXmlHelper)_helper).load(reader, "http://www.w3.org/2001/XMLSchema", "any", null);

        assertNotNull(obj);
        assertTrue(obj instanceof DataObject);
        DataObject data = (DataObject)obj;
        _helperContext.getXSDHelper().isMixed(data.getType());
        assertNotNull(data.getInstanceProperty("title"));
        assertEquals("4711", data.getString("title"));
        assertNotNull(data.getInstanceProperty("description"));
        assertEquals("4711", data.getString("description"));
        assertNotNull(data.getInstanceProperty("language"));
        assertEquals("de-DE", data.getString("language"));
        assertNotNull(data.getInstanceProperty("item"));
    }

    @Test
    public void testExtendedSimpleContent() throws IOException {
        final String schemaFileName = PACKAGE + "extendedSimpleType.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(), url.toString());
        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "extendedSimpleType.xml");
        XMLDocument document = _helper.load(xmlUrl.openStream(), xmlUrl.toString(), null);

        DataObject container = document.getRootObject();
        DataObject extended = container.getDataObject("extended");
        DataObject restricted = container.getDataObject("restricted");

        assertEquals(200, extended.getInt("value"));
        assertEquals("meta", extended.getString("meta1"));
        assertEquals(1, extended.getInt("meta2"));

        assertEquals(2, restricted.getInt("value"));
        assertEquals("metameta", restricted.getString("meta1"));
        assertEquals(0, restricted.getInt("meta2"));

        String xml = _helper.save(container, document.getRootElementURI(), document.getRootElementName());

        XMLDocument document2 = _helper.load(xml);
        DataObject container2 = document2.getRootObject();

        assertTrue(_helperContext.getEqualityHelper().equal(container, container2));

        String xml2 = _helper.save(container2, document2.getRootElementURI(), document2.getRootElementName());
        assertEquals(xml, xml2);
    }

    @Test
    public void testExtendedSimpleContentInCS() throws IOException {
        final String schemaFileName = PACKAGE + "extendedSimpleType.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(), url.toString());
        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "extendedSimpleType.xml");
        XMLDocument document = _helper.load(xmlUrl.openStream(), xmlUrl.toString(), null);

        DataObject dataGraph = _helperContext.getDataFactory().create(
            URINamePair.DATAGRAPH_TYPE.getURI(), URINamePair.DATAGRAPH_TYPE.getName());
        Property rootProp = _helperContext.getXSDHelper().getGlobalProperty(
            document.getRootElementURI(), document.getRootElementName(), true);

        DataObject container = document.getRootObject();
        dataGraph.getList(rootProp).add(container);
        dataGraph.getChangeSummary().beginLogging();

        container.set("extended/@meta1", "new");
        container.set("restricted/value", "22");

        String xml = _helper.save(dataGraph, URINamePair.PROP_SDO_DATAGRAPH.getURI(), URINamePair.PROP_SDO_DATAGRAPH.getName());
        System.out.println(xml);

        XMLDocument document2 = _helper.load(xml);
        DataObject dataGraph2 = document2.getRootObject();

        assertTrue(_helperContext.getEqualityHelper().equal(dataGraph, dataGraph2));

        String xml2 = _helper.save(dataGraph2, document2.getRootElementURI(), document2.getRootElementName());
        assertEquals(xml, xml2);

    }
//    @Test
//    public void testExtendedSimpleContentByIntfGeneration() throws IOException {
//        final String schemaFileName = PACKAGE + "extendedSimpleType.xsd";
//        URL url = getClass().getClassLoader().getResource(schemaFileName);
//        List<Type> types = _helperContext.getXSDHelper().define(url.openStream(), url.toString());
//        SapTypeHelper typeHelper = (SapTypeHelper)_helperContext.getTypeHelper();
//        InterfaceGenerator generator = typeHelper.createInterfaceGenerator("C:/test");
//        generator.addPackage("ext.xsd", "com.sap.sdo.testcase.typefac.cc");
//        generator.addSchemaLocation("ext.xsd", "extendedSimpleType.xsd");
//
//        generator.generate(types);
//    }

    @Test
    public void testExtendedSimpleContentWithNilValues() throws Exception {
        final String schemaFileName = PACKAGE + "extendedSimpleType.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(), url.toString());
        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "extendedSimpleTypeNil.xml");
        XMLDocument document = _helper.load(xmlUrl.openStream(), xmlUrl.toString(), null);

        DataObject container = document.getRootObject();
        DataObject extended = container.getDataObject("extended");
        DataObject restricted = container.getDataObject("restricted");

        assertEquals(0, extended.getInt("value"));
        assertEquals("meta", extended.getString("meta1"));
        assertEquals(1, extended.getInt("meta2"));

        assertEquals(0, restricted.getInt("value"));
        assertEquals("metameta", restricted.getString("meta1"));
        assertEquals(0, restricted.getInt("meta2"));

        // System.out.println(_helper.save(document.getRootObject(), document.getRootElementURI(), document.getRootElementName()));
    }

    @Test
    public void testExtendedSimpleContentWithoutSchema() throws Exception {
      URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "extendedSimpleTypeNil.xml");
      XMLDocument document = _helper.load(xmlUrl.openStream(), xmlUrl.toString(), null);

      DataObject container = document.getRootObject();
      DataObject extended = container.getDataObject("extended");
      DataObject restricted = container.getDataObject("restricted");

      assertEquals("meta", extended.getString("meta1"));
      assertEquals(1, extended.getInt("meta2"));

      assertEquals("metameta", restricted.getString("meta1"));
      assertEquals(0, restricted.getInt("meta2"));

      System.out.println(_helper.save(document.getRootObject(), document.getRootElementURI(), document.getRootElementName()));
   }

    @Test
    public void testExtendedSimpleContentThroughApi() throws Exception {
        DataObject l_root = _helperContext.getDataFactory().create(OPEN.getURI(), OPEN.getName());
        DataObject l_data = _helperContext.getDataFactory().create("commonj.sdo", "String");
        assertEquals(true, l_data.getInstanceProperty("value").isNullable());
        l_data.set("value", "foo");
        l_data.set("attribute", 42);
        l_root.setDataObject("data", l_data);

        String xml = _helper.save(l_root, "sap.com/test", "root");
        System.out.println(xml);

        DataObject root = _helper.load(xml).getRootObject();
        assertNotNull(root);

        DataObject data = root.getDataObject("data");
        assertNotNull(data);
        assertEquals("foo", root.getString("data"));
        assertEquals("42", data.get("attribute"));
        Property attrProp = data.getInstanceProperty("attribute");
        assertNotNull(attrProp);
        assertEquals(true, _helperContext.getXSDHelper().isAttribute(attrProp));
    }

    @Test
    public void testExtendedSimpleContentByIntf() throws IOException {
        DataFactory dataFactory = _helperContext.getDataFactory();
        EContainer container = (EContainer)dataFactory.create(EContainer.class);
        ExtendedSimpleType extended = (ExtendedSimpleType)dataFactory.create(ExtendedSimpleType.class);
        RestrictedSimpleType restricted = (RestrictedSimpleType)dataFactory.create(RestrictedSimpleType.class);
        extended.setMeta1("extended1");
        extended.setMeta2(12);
        extended.setValue(new BigInteger("10000000000000000"));
        restricted.setMeta1("restricted1");
        restricted.setMeta2(22);
        restricted.setValue(new BigInteger("20000000000000000"));
        container.setExtended(extended);
        container.setRestricted(restricted);

        String xml = _helper.save((DataObject)container, "ext.xsd", "container");
        assertFalse(xml, xml.contains("value"));
    }

    @Test
    public void testExtendedSimpleContentPolymorphRendering() throws IOException {
        final String schemaFileName = PACKAGE + "extendedSimpleType.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(), url.toString());

        DataFactory dataFactory = _helperContext.getDataFactory();

        DataObject container = dataFactory.create("ext.xsd", "container");
        DataObject extended = dataFactory.create("ext.xsd", "RestrictedSimpleType");
        DataObject restricted = dataFactory.create("ext.xsd", "RestrictedSimpleType");

        extended.setString("meta1", "restricted-extended-simple-1");
        extended.setInt("value", 1);

        restricted.setString("meta1", "restricted-extended-simple-2");
        restricted.setInt("value", 2);

        container.setDataObject("extended", extended);
        container.setDataObject("restricted", restricted);

        String xml = _helper.save(container, "ext.xsd", "container");

        XMLDocument document2 = _helper.load(xml);
        DataObject container2 = document2.getRootObject();

        assertTrue(_helperContext.getEqualityHelper().equal(container, container2));

        String xml2 = _helper.save(container2, document2.getRootElementURI(), document2.getRootElementName());
        assertEquals(xml, xml2);

        assertSame(extended.getType(), container2.getDataObject("extended").getType());
    }

    @Test
    public void testExtendedSimpleContentWithXsdType() throws IOException {
        final String schemaFileName = PACKAGE + "extendedSimpleType.xsd";
        URL url = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(), url.toString());

        DataFactory dataFactory = _helperContext.getDataFactory();

        DataObject container = dataFactory.create(OPEN.getURI(), OPEN.getName());
        DataObject extended = dataFactory.create("ext.xsd", "RestrictedSimpleType");

        extended.setString("meta1", "restricted-extended-simple-1");
        extended.setInt("value", 1);


        container.setDataObject("extended", extended);

        String xml = _helper.save(container, "sap.com/test", "root");
        System.out.println(xml);

//        XMLDocument document2 = _helper.load(xml);
//        DataObject container2 = document2.getRootObject();
//
//        assertTrue(_helperContext.getEqualityHelper().equal(container, container2));
//
//        String xml2 = _helper.save(container2, document2.getRootElementURI(), document2.getRootElementName());
//        assertEquals(xml, xml2);
//
//        assertSame(extended.getType(), container2.getDataObject("extended").getType());
    }

    @Test
    public void testRenamedAttributeRefs() throws IOException {
        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "renamedAttributeRefs.xml");
        XMLDocument document = _helper.load(xmlUrl.openStream(), xmlUrl.toString(), null);

        DataObject container = document.getRootObject();
        assertEquals("String1", container.getString("sdo1"));
        assertEquals("String2", container.getString("sdo2"));
        assertEquals("String3", container.getString("sdo3a"));

        XSDHelper xsdHelper = _helperContext.getXSDHelper();
        Property global1 = xsdHelper.getGlobalProperty("com.sap.test.rar", "xml1", false);
        Property global2 = xsdHelper.getGlobalProperty("com.sap.test.rar", "xml2", false);
        Property global3 = xsdHelper.getGlobalProperty("com.sap.test.rar", "xml3", false);

        assertEquals("xml1", global1.getName());
        assertEquals("sdo2", global2.getName());
        assertEquals("sdo3", global3.getName());

        String xml = _helperContext.getXMLHelper().save(container, document.getRootElementURI(), document.getRootElementName());
        System.out.println(xml);
    }

    @Test
    public void testToString() {
        assertNotNull(_helper.toString());
    }

    @Test
    public void testDataTypeElement() throws Exception {
        final String schemaFileName = PACKAGE + "dataTypeElement.xsd";
        URL url  = getClass().getClassLoader().getResource(schemaFileName);
        _helperContext.getXSDHelper().define(url.openStream(), url.toString());

        Property dataTypeElement =
            _helperContext.getTypeHelper().getOpenContentProperty("", "dataTypeElement");
        assertNotNull(dataTypeElement);

        URL uri = getClass().getClassLoader().getResource(PACKAGE+"dataTypeElement.xml");
        XMLDocument doc = _helper.load(uri.openStream(),uri.toString(),null);
        assertNotNull(doc);
        DataObject root = doc.getRootObject();
        assertNotNull(root);
        assertEquals(42, root.get("value"));

        String xml =
            _helperContext.getXMLHelper().save(
                root,
                doc.getRootElementURI(),
                doc.getRootElementName());

        assertEquals(42, _helper.load(xml).getRootObject().get("value"));
    }

    @Test
    public void testEqualNames() throws IOException {
        XSDHelper xsdHelper = _helperContext.getXSDHelper();
        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "nsClash.xml");
            XMLDocument document = _helper.load(xmlUrl.openStream(), xmlUrl.toString(), null);
        DataObject e11 = document.getRootObject().getDataObject("e11");
        List<Property> props = e11.getInstanceProperties();
        assertEquals(2, props.size());
        Property property0 = props.get(0);
        Property property1 = props.get(1);
        assertEquals("e2", property0.getName());
        assertEquals("e2", property1.getName());
        assertEquals("http://www.example.org/s2", xsdHelper.getNamespaceURI(property0));
        assertEquals("http://www.example.org/s4", xsdHelper.getNamespaceURI(property1));
        assertSame(property0, e11.getInstanceProperty("e2"));

        DataObject s2e2 = e11.getDataObject(property0);
        DataObject s4e2 = e11.getDataObject(property1);
        assertNotSame(s2e2, s4e2);

        }

    @Test
    public void testEqualNamesXSD() throws IOException {
        URL xsdUrl = getClass().getClassLoader().getResource(PACKAGE + "s1.xsd");
        XSDHelper xsdHelper = _helperContext.getXSDHelper();
        xsdHelper.define(xsdUrl.openStream(), xsdUrl.toString());
        Type e11 = _helperContext.getTypeHelper().getType("http://www.example.org/s1", "e11");
        List<Property> props = e11.getProperties();
        assertEquals(2, props.size());
        Property property0 = props.get(0);
        Property property1 = props.get(1);
        assertEquals("e2", property0.getName());
        assertEquals("e2", property1.getName());
        assertEquals("http://www.example.org/s2", xsdHelper.getNamespaceURI(property0));
        assertEquals("http://www.example.org/s4", xsdHelper.getNamespaceURI(property1));
        assertSame(property0, e11.getProperty("e2"));
    }

    @Test
    public void testNoNamespace() {
        String xml = "<a><b name=\"b1\"/><b name=\"b2\"/></a>";
        XMLDocument document = _helper.load(xml);
        String rootElementURI = document.getRootElementURI();
        if (rootElementURI != null) {
            assertEquals("", rootElementURI);
        }
        assertEquals("a", document.getRootElementName());
        //see spec 3.12.1 and 3.12.2
        assertEquals(null, document.getEncoding());
        // bug in JDK 1.6.0_07-b06
        //assertEquals(null, document.getXMLVersion());
        String rendered = _helper.save(document.getRootObject(), rootElementURI, document.getRootElementName());
        System.out.println(rendered);
    }

    @Test
    public void testNoNS() throws IOException {
        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "NoNS.xml");
        XMLDocument document = _helper.load(xmlUrl.openStream(), xmlUrl.toString(), null);

        DataObject root = document.getRootObject();
        Type type = root.getType();
        assertEquals(null, type.getURI());
        assertEquals("NoNSType", type.getName());

        Type baseType = (Type)type.getBaseTypes().get(0);
        assertEquals(null, baseType.getURI());
        assertEquals("NoNSBase", baseType.getName());

        assertEquals("baseString", root.get("base"));
        assertEquals("element1String", root.get("element1"));
    }

    @Test
    public void testNoNSImport() throws IOException {
        final XSDHelper xsdHelper = _helperContext.getXSDHelper();
        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "NoNSImport.xml");
        XMLDocument document = _helper.load(xmlUrl.openStream(), xmlUrl.toString(), null);

        DataObject rootElement = document.getRootObject();
        DataObject imported = rootElement.getDataObject("imported");
        assertEquals("element imported", imported.getString("element1"));
        assertEquals("base imported", imported.getString("base"));

        DataObject included = rootElement.getDataObject("included");
        assertEquals("element included", included.getString("element1"));
        assertEquals("base included", included.getString("base"));

        DataObject importedRootNoNS = rootElement.getDataObject("importedRootNoNS");
        assertEquals("element ref imported", importedRootNoNS.getString("element1"));
        assertEquals("base ref imported", importedRootNoNS.getString("base"));

        DataObject rootNoNS = rootElement.getDataObject("RootNoNS");
        assertEquals("element ref included", rootNoNS.getString("element1"));
        assertEquals("base ref included", rootNoNS.getString("base"));

        Property importedProp = rootElement.getInstanceProperty("imported");
        Type importedType = importedProp.getType();
        Type importedBaseType = (Type)importedType.getBaseTypes().get(0);

        Property includedProp = rootElement.getInstanceProperty("included");
        Type includedType = includedProp.getType();
        assertNotSame(importedType, includedType);
        assertEquals("http://www.sap.com/schema/test/NoNSImport", includedType.getURI());

        Type includedBaseType = (Type)includedType.getBaseTypes().get(0);
        assertNotSame(importedBaseType, includedBaseType);
        assertEquals("http://www.sap.com/schema/test/NoNSImport", includedBaseType.getURI());
        assertEquals("NoNSBase", includedBaseType.getName());

        Property importedRootNoNSProp = rootElement.getInstanceProperty("importedRootNoNS");
        assertSame(importedType, importedRootNoNSProp.getType());
        assertEquals("", xsdHelper.getNamespaceURI(importedRootNoNSProp));

        Property includedRootNoNSProp = rootElement.getInstanceProperty("RootNoNS");
        assertSame(includedType, includedRootNoNSProp.getType());
        assertEquals("http://www.sap.com/schema/test/NoNSImport", xsdHelper.getNamespaceURI(includedRootNoNSProp));

        StringWriter rendered = new StringWriter();
        _helper.save(document, rendered, null);
        System.out.println(rendered.toString());
    }

    @Test
    public void testNilElement() throws Exception {
        String xml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n" +
            "  <openInterface>\n" +
            "    <a xsi:type=\"xsd:string\" xsi:nil=\"true\"></a>\n" +
            "  </openInterface>\n" +
            "</root>\n";
        XMLDocument doc = _helper.load(xml);
        assertLineEquality(xml, _helper.save(doc.getRootObject(), doc.getRootElementURI(), doc.getRootElementName()));

        xml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n" +
            "  <openInterface>\n" +
            "    <a xsi:nil=\"true\"></a>\n" +
            "  </openInterface>\n" +
            "</root>\n";
        Map options = Collections.singletonMap(SapXmlHelper.OPTION_KEY_SIMPLIFY_OPEN_CONTENT, SapXmlHelper.OPTION_VALUE_FALSE);
        doc = _helper.load(new StringReader(xml), null, options);
        assertLineEquality(xml, _helper.save(doc.getRootObject(), doc.getRootElementURI(), doc.getRootElementName()));

        _helperContext.getTypeHelper().getType(OpenInterface.class);
        xml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n" +
            "  <openInterface>\n" +
            "    <a" +
            " xmlns:ns1=\"com.sap.sdo.testcase.typefac\"" +
            " xsi:type=\"ns1:OpenInterface\" xsi:nil=\"true\"" +
            "></a>\n" +
            "  </openInterface>\n" +
            "</root>\n";
        doc = _helper.load(xml);
        assertLineEquality(xml, _helper.save(doc.getRootObject(), doc.getRootElementURI(), doc.getRootElementName()));

        xml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n" +
            "  <openInterface>\n" +
            "    <a xsi:type=\"xsd:integer\" xsi:nil=\"true\"></a>\n" +
            "  </openInterface>\n" +
            "</root>\n";
        doc = _helper.load(xml);
        assertLineEquality(xml, _helper.save(doc.getRootObject(), doc.getRootElementURI(), doc.getRootElementName()));

        xml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n" +
            "  <openInterface>\n" +
            "    <a xsi:type=\"xsd:int\" xsi:nil=\"true\"></a>\n" +
            "  </openInterface>\n" +
            "</root>\n";
        doc = _helper.load(xml);
        assertLineEquality(xml, _helper.save(doc.getRootObject(), doc.getRootElementURI(), doc.getRootElementName()));
    }

    @Test
    public void testComplexSimplePolymorphism() throws IOException {
        URL xsdUrl = getClass().getClassLoader().getResource(PACKAGE + "ComplexSimplePolymorphism.xsd");
        _helperContext.getXSDHelper().define(xsdUrl.openStream(), xsdUrl.toString());

        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "ComplexSimplePolymorphism.xml");
        XMLDocument document = _helper.load(xmlUrl.openStream(), xmlUrl.toString(), null);

        DataObject root = document.getRootObject();
        Object simpleInt = root.get("simpleInt");
        assertEquals(Integer.class, simpleInt.getClass());
        assertEquals(Integer.valueOf(99), simpleInt);

        Object value = root.get("simpleInt");
        assertEquals(Integer.valueOf(99), value);

        StringWriter genXml = new StringWriter();
        _helper.save(document, genXml, null);
        System.out.println(genXml);

        XMLDocument doc = _helper.load(genXml.toString());
        DataObject loaded = doc.getRootObject();

        assertTrue(_helperContext.getEqualityHelper().equal(root, loaded));

        simpleInt = loaded.get("simpleInt");
        assertEquals(Integer.class, simpleInt.getClass());
        assertEquals(Integer.valueOf(99), simpleInt);

        StringWriter result = new StringWriter();
        _helper.save(doc, result, null);
        assertEquals(genXml.toString(), result.toString());
    }

    @Test
    public void testComplexSimplePolymorphismStatic() throws Exception {
        //TODO check parsing and rendering
        Type rootType = _helperContext.getTypeHelper().getType(Root.class);
        _helperContext.getTypeHelper().getType(IntExtension.class);

        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "ComplexSimplePolymorphism.xml");
        XMLDocument document = _helper.load(xmlUrl.openStream(), xmlUrl.toString(), null);

        DataObject root = document.getRootObject();
        assertTrue(root instanceof Root);
        DataObject simpleInt = root.getDataObject("simpleInt");
        assertEquals("hello", simpleInt.get("attr"));
        assertEquals(Integer.valueOf(99), simpleInt.get("value"));
        assertTrue(simpleInt instanceof IntExtension);

        IntExtension intExtension = ((Root)root).getSimpleInt();
        assertTrue(intExtension instanceof IntExtension);
        assertEquals(99, intExtension.getValue());
        assertEquals("hello", intExtension.getAttr());
    }

    @Test
    public void testGroupsImport() throws IOException {
        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "GroupsImport.xml");
        XMLDocument document = _helper.load(xmlUrl.openStream(), xmlUrl.toString(), null);

        DataObject root = document.getRootObject();
        Type type = root.getType();
    }

    @Test
    public void testOpenContentConflict() throws IOException {
        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "OpenContentConflict.xml");
        XMLDocument document = _helper.load(xmlUrl.openStream(), xmlUrl.toString(), null);

        DataObject root = document.getRootObject();
        assertEquals(2, root.getInstanceProperties().size());
        assertEquals("defined", root.get(0));
        assertEquals(Collections.singletonList("open"), root.get(1));
        Property prop0 = (Property)root.getInstanceProperties().get(0);
        Property prop1 = (Property)root.getInstanceProperties().get(1);
        assertEquals("defined", root.get(prop0));
        assertEquals(Collections.singletonList("open"), root.get(prop1));

        assertEquals(false, prop0.isMany());
        assertEquals(false, prop0.isOpenContent());
        assertEquals(true, prop1.isMany());
        assertEquals(true, prop1.isOpenContent());

        Sequence sequence = root.getSequence();
        assertEquals(2, sequence.size());
        assertSame(prop0, sequence.getProperty(0));
        assertSame(prop1, sequence.getProperty(1));
        assertEquals("defined", sequence.getValue(0));
        assertEquals("open", sequence.getValue(1));

        StringWriter genXml = new StringWriter();
        _helper.save(document, genXml, null);
        String originalXml = readFile(xmlUrl);
        assertLineEquality(originalXml, genXml.toString());


    }

    @Test
    public void testSimpleStringXml() throws IOException {
        URL wsdlUrl = getClass().getClassLoader().getResource(PACKAGE + "strings.wsdl");
        Map options = Collections.singletonMap(SapXmlHelper.OPTION_KEY_DEFINE_SCHEMAS, SapXmlHelper.OPTION_VALUE_TRUE);
        _helper.load(wsdlUrl.openStream(), wsdlUrl.toString(), options);

        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "strings.xml");
        XMLDocument document =
            _helper.load(
                xmlUrl.openStream(), xmlUrl.toString(),
                Collections.singletonMap(IGNORE_FOR_FEATURE_TEST, true));

        DataObject root = document.getRootObject();
        assertEquals(null, root.getString("value"));
        Property valueProp = root.getInstanceProperty("value");
        assertNull(valueProp);
    }

    @Test
    public void testReferences() throws IOException {
        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "References.xml");
        XMLDocument document = _helper.load(xmlUrl.openStream(), xmlUrl.toString(), null);

        DataObject root = document.getRootObject();
        StringWriter genXml = new StringWriter();
        _helper.save(document, genXml, null);
        String originalXml = readFile(xmlUrl);
        assertLineEquality(originalXml, genXml.toString());
    }

}
