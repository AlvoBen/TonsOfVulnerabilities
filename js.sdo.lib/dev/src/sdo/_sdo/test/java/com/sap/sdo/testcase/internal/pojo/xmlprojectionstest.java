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
package com.sap.sdo.testcase.internal.pojo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;

import org.junit.Test;

import com.sap.sdo.api.helper.SapDataFactory;
import com.sap.sdo.api.helper.SapDataHelper;
import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.impl.SapHelperProvider;
import com.sap.sdo.api.types.SapType;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.internal.pojo.ex.Course;
import com.sap.sdo.testcase.internal.pojo.ex.PojoX;
import com.sap.sdo.testcase.internal.pojo.ex.PojoY;
import com.sap.sdo.testcase.internal.pojo.ex.PojoY2;
import com.sap.sdo.testcase.internal.pojo.ex.PojoZ;
import com.sap.sdo.testcase.internal.pojo.ex.School;
import com.sap.sdo.testcase.internal.pojo.ex.Student;

import commonj.sdo.DataObject;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.DataHelper;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLHelper;

public class XmlProjectionsTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public XmlProjectionsTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }
    @Test
    public void testMockRdbDas() {
    	DataObject cal = DataFactory.INSTANCE.create(School.class);
    	cal.set("name","Berkeley");
    	DataObject stu = cal.createDataObject("students");
    	stu.set("name", "Billy Bear");
    	DataObject cor = cal.createDataObject("courses");
    	cor.set("name", "Basket Weaving");
    	((Student)stu).getCourses().add((Course)cor);

    	HelperContext hc2 = SapHelperProvider.getNewContext();
    	hc2.getXSDHelper().define(getClass().getClassLoader().getResourceAsStream("com/sap/sdo/testcase/internal/pojo/ex/projection.xsd"), null);
    	DataObject projection = ((SapDataFactory)hc2.getDataFactory()).project(cal);
    	assertEquals(1,projection.getList("students").size());
    	DataObject stu2 = (DataObject)projection.getList("students").get(0);
    	assertNull(stu.getContainer());
    	assertEquals(projection, stu2.getContainer());
    	assertEquals(1,stu2.getList("courses").size());

    	DataObject cor2 = stu2.createDataObject("courses");
    	cor2.set("name", "FORTRAN72 and You");
    	assertEquals(2,stu2.getList("courses").size());
    	assertEquals(2,stu.getList("courses").size());
    	assertEquals("FORTRAN72 and You",((DataObject)stu.getList("courses").get(1)).get("name"));

    	assertEquals(1,projection.getList("students").size());
    	DataObject stu3 = cal.createDataObject("students");
    	projection.getList("students").size();
    	assertEquals(2, projection.getList("students").size());
    	DataObject projStu = (DataObject)projection.getList("students").get(1);
    	assertNull(stu3.getContainer());
    	assertEquals(projection, projStu.getContainer());
    	System.out.println("----------");
    	System.out.println(XMLHelper.INSTANCE.save(projection, "x", "y"));
    	System.out.println("----------");
    }
	@Test
    public void testPojoEnhancer() throws Exception {
		PojoX pojo = (PojoX)DataFactory.INSTANCE.create(PojoX.class);
		pojo.setX(4);
		assertEquals(4, pojo.getX());
		assertEquals(4,((DataObject)pojo).get("x"));

		Field f = PojoX.class.getField("_x");
		f.set(pojo, 5);
		assertEquals(5, pojo.getX());
		assertEquals(5,((DataObject)pojo).get("x"));

		PojoY pojoY = (PojoY)DataFactory.INSTANCE.create(PojoY.class);
		pojo.setY(pojoY);
		assertSame(pojoY,pojo.getY());
		assertSame(pojoY,((DataObject)pojo).get("y"));
		pojoY.setY(6);
		assertEquals(6, pojoY.getY());
		assertEquals(6,((DataObject)pojo).get("y/y"));

	}
	@Test
    public void testProjection() throws Exception {
		DataObject y = DataFactory.INSTANCE.create(PojoY.class);
    	HelperContext hc2 = SapHelperProvider.getNewContext();
    	hc2.getTypeHelper().getType(PojoY2.class);
		PojoY2 y2 = (PojoY2)((SapDataFactory)hc2.getDataFactory()).project(y);
		y.set("y", 1);
		assertEquals(1,((PojoY)y).getY());
		assertEquals(1,y.get(0));
		assertEquals(1,((DataObject)y2).get(0));
		assertEquals(1,y2.getFoo());
		y2.setFoo(2);
		assertEquals(2,((PojoY)y).getY());
		assertEquals(2,y.get(0));
		assertEquals(2,((DataObject)y2).get(0));
		assertEquals(2,y2.getFoo());
		PojoY o = (PojoY)((SapDataHelper)DataHelper.INSTANCE).project(y);
		assertEquals(PojoY.class, o.getClass());
		assertEquals(2,o.getY());
		DataObject x = DataFactory.INSTANCE.create(PojoX.class);
		((PojoX)x).setX(4);
		((PojoX)x).setY((PojoY)y);
		PojoX ox = (PojoX)((SapDataHelper)DataHelper.INSTANCE).project(x);
		assertEquals(PojoX.class, ox.getClass());
		assertEquals(PojoY.class, ox.getY().getClass());
	}
	@Test
    public void testCast() throws Exception {
		PojoX pojo = new PojoX();
		pojo.setX(5);
		PojoY pojoy = new PojoY();
		pojoy.setY(1);
		pojo.setY(pojoy);
		DataObject o = ((SapDataFactory)DataFactory.INSTANCE).cast(pojo);
		assertEquals(5, o.get("x"));
	}
	@Test
    public void testJ1Example() throws Exception {
    	HelperContext hc1 = SapHelperProvider.getNewContext();
    	hc1.getTypeHelper().getType(POOrder.class);
    	HelperContext hc2 = SapHelperProvider.getNewContext();
        final String schemaFileName = PACKAGE + "j1.xsd";
        InputStream is = getClass().getClassLoader().getResourceAsStream(schemaFileName);
    	hc2.getXSDHelper().define(is,schemaFileName);
    	String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ns2:submitOrder id=\"0\" xmlns:ns2=\"http://www.sample.org/shop/\">\n"
        +"<lineItems>\n"
        +"  <description>Ferrari Testosteron</description>\n"
        +"  <matnr>V0002</matnr>\n"
        +"  <price>150000.00</price>\n"
        +"  <quantity>1</quantity>\n"
        +"</lineItems>\n"
        +"<lineItems>\n"
        +"  <description>Cloaking and Spying</description>\n"
        +"  <matnr>AL002</matnr>\n"
        +"  <price>5.99</price>\n"
        +"  <quantity>1</quantity>\n"
        +"</lineItems>\n"
        +"<total>150005.99</total>\n"
        +"<user>ccc</user>\n"
        +"</ns2:submitOrder>\n";
    	DataObject xmlObj = hc2.getXMLHelper().load(xml).getRootObject();
    	assertEquals("Order",xmlObj.getType().getName());


        assertEquals(POOrder.class, hc1.getTypeHelper().getType("http://www.sample.org/shop/", "Order").getInstanceClass());
        assertSame(xmlObj,((DataObject)xmlObj.getList("lineItems").get(0)).getDataObject("order"));
    	DataObject jObj = ((SapDataFactory)hc1.getDataFactory()).project(xmlObj);
        System.out.println(jObj.getType());
        assertSame(hc1, ((SapType)jObj.getType()).getHelperContext());
        final List lineItems = jObj.getList("lineItems");
        assertEquals(2, lineItems.size());
    	assertSame(jObj,((POOrderItem)lineItems.get(0)).getOrder());
        assertSame(jObj,((POOrderItem)lineItems.get(1)).getOrder());

        POOrderItem lineItem0 = (POOrderItem)lineItems.get(0);
        POOrderItem removed = ((POOrderItem)lineItems.remove(0));
        assertSame(lineItem0, removed);

    	IOrder order = (IOrder)((SapDataHelper)hc1.getDataHelper()).project(jObj);

       	assertSame(order,order.getLineItems().get(0).getOrder());

       	// TODO
       	//assertEquals("",hc1.getXMLHelper().save(xmlObj, "x", "y"));
	}
	@Test
    public void testRecogniseGettersWithIs() throws Exception {
		DataObject z = DataFactory.INSTANCE.create(PojoZ.class);
		assertEquals(1,z.getType().getDeclaredProperties().size());
	}
}
