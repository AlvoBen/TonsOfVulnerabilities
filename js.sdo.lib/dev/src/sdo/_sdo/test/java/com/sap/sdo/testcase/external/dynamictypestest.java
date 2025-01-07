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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.types.PropertyConstants;
import com.sap.sdo.api.types.TypeConstants;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XSDHelper;

public class DynamicTypesTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public DynamicTypesTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private static final String DEF = "def";

    private static final String NAME = "theType";

    private static final String URI = "http://com.sap.sdo.org";

    @Test
    public void testSDO17() {
        DataObject typeObject = _helperContext.getDataFactory().create("commonj.sdo",
        "Type");
        typeObject.set("name", NAME);
        typeObject.set("uri", "sdo17.api");
        DataObject theProp = typeObject.createDataObject("property");
        theProp.set("name", "theProp");
        theProp.set("type", _helperContext.getTypeHelper().getType("commonj.sdo", "String"));
        theProp.set("default", DEF);
        Type t = _helperContext.getTypeHelper().define(typeObject);
        assertEquals(10, t.getInstanceProperties().size());
        assertEquals(NAME, t.get((Property)t.getInstanceProperties().get(3)));
        typeObject = _helperContext.getDataFactory().create("commonj.sdo","Type");
        typeObject.set("name", "typeWithOpenProps");
        typeObject.set("uri", "sdo17.api");
        typeObject.set("additional","hello");
        theProp = typeObject.createDataObject("property");
        theProp.set("name", "theProp");
        theProp.set("type", _helperContext.getTypeHelper().getType("commonj.sdo", "String"));
        theProp.set("default", DEF);
        t = _helperContext.getTypeHelper().define(typeObject);
        assertEquals(11, t.getInstanceProperties().size());
        assertEquals("hello", t.get((Property)t.getInstanceProperties().get(10)));
    }
    @Test
    public void testTypeWithOneProperty() {
        DataObject typeObject = _helperContext.getDataFactory().create("commonj.sdo",
                "Type");
        typeObject.set("name", NAME);
        typeObject.set("uri", URI);
        DataObject theProp = typeObject.createDataObject("property");
        theProp.set("name", "theProp");
        theProp.set("type", _helperContext.getTypeHelper()
                .getType("commonj.sdo", "String"));
        System.out.println(theProp.get("type"));
        theProp.set("default", DEF);
        typeObject.get("property");
        Type t = _helperContext.getTypeHelper().define(typeObject);
        assertTrue("type not returned after define", t != null);
        assertTrue("type has incorrect uri " + t.getURI(), URI.equals(t
                .getURI()));
        assertTrue("type has incorrect name " + t.getName(), NAME
                .equals(t.getName()));
        Property p = t.getProperty("theProp");
        assertTrue("property not returned after define", p != null);
        assertTrue("property of incorrect type " + p.getType(),
                _helperContext.getTypeHelper().getType("commonj.sdo", "String").equals(
                        p.getType()));
        assertTrue("property has incorrect default " + p.getDefault(),
                DEF.equals(p.getDefault()));
        DataObject d = _helperContext.getDataFactory().create(t);
        assertTrue("data object not created ", d != null);
        assertTrue("data object of incorrect type " + d.getType(), t
                .equals(d.getType()));
        Object o = d.get("theProp");
        assertTrue("bad property default from data object " + o, DEF
                .equals(o));
    }
    @Test
    public void testTypeCanHaveOpenProperties() {
        DataObject openTypeDecl = _helperContext.getDataFactory().create("commonj.sdo",
        "Type");
        openTypeDecl.set("uri", "http://example.com/openType");
	    openTypeDecl.set("name", "OpenType");
	    openTypeDecl.setBoolean("open",true);
	    Type openType = _helperContext.getTypeHelper().define(openTypeDecl);
	    DataObject o = _helperContext.getDataFactory().create(openType);
	    DataObject prop = _helperContext.getDataFactory().create("commonj.sdo","Property");
	    prop.set("name","openPropertyOfThisTypeObject");
	    prop.set("type",_helperContext.getTypeHelper().getType(String.class));
	    Property property = _helperContext.getTypeHelper().defineOpenContentProperty(null, prop);
        o.set(property,"xxxx");
    	assertEquals("xxxx",o.get("openPropertyOfThisTypeObject"));

	    prop = _helperContext.getDataFactory().create("commonj.sdo","Property");
	    prop.set("name","neverSeenThisBefore");
	    prop.set("type",_helperContext.getTypeHelper().getType(String.class));
    }
    @Test
    public void testDynamicTypesFromSpec() {
        TypeHelper types = _helperContext.getTypeHelper();
        Type intType = types.getType("commonj.sdo", "Int");
        Type stringType = types.getType("commonj.sdo", "String");
        // create a new Type for Customers
        DataObject customerType = _helperContext.getDataFactory().create("commonj.sdo",
                "Type");
        customerType.set("uri", "http://example.com/customer");
        customerType.set("name", "Customer");
        // create a customer number property
        DataObject custNumProperty = customerType.createDataObject("property");
        custNumProperty.set("name", "custNum");
        custNumProperty.set("type", intType);
        // create a first name property
        DataObject firstNameProperty = customerType
                .createDataObject("property");
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

        assertTrue("bad lookup", customer1.getInt("custNum") == 1);
        assertTrue("bad lookup", customer1.getString("firstName").equals(
                "John"));
        assertTrue("bad lookup", customer1.getString("lastName").equals(
                "Adams"));
        assertTrue("bad lookup", customer2.getInt("custNum") == 2);
        assertTrue("bad lookup", customer2.getString("firstName").equals(
                "Jeremy"));
        assertTrue("bad lookup", customer2.getString("lastName").equals(
                "Pavick"));

        try {
        	customer1.set("undefinedProperty","xxx");
        	fail("access to open property should have thrown an exception");
        } catch(Exception e) { //$JL-EXC$
        	;
        }
    }

    @Test
    public void testEmptyUri() {
        DataObject typeDO = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        typeDO.set("name", "ExtendedIntegerType");
        TypeHelper typeHelper = _helperContext.getTypeHelper();
        Type type = typeHelper.define(typeDO);
        assertNull(type.getURI());
        assertSame(type, typeHelper.getType(null, "ExtendedIntegerType"));
        assertSame(type, typeHelper.getType("", "ExtendedIntegerType"));
    }

    @Test
    public void testReverseRenaming() {
        DataFactory dataFactory = _helperContext.getDataFactory();
        TypeHelper typeHelper = _helperContext.getTypeHelper();

        DataObject typeObject = dataFactory.create(URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
        typeObject.setString(TypeConstants.NAME, "mySdoTypeName");
        typeObject.setString(typeHelper.getOpenContentProperty(
            URINamePair.PROP_XML_XML_NAME.getURI(), URINamePair.PROP_XML_XML_NAME.getName()), "myXmlTypeName");
        typeObject.setString(TypeConstants.URI, "http://sap.com/test/");

        DataObject propObject = typeObject.createDataObject(TypeConstants.PROPERTY);
        propObject.setString(PropertyConstants.NAME, "mySdoPropName");
        propObject.setString(typeHelper.getOpenContentProperty(
            URINamePair.PROP_XML_XML_NAME.getURI(), URINamePair.PROP_XML_XML_NAME.getName()), "myXmlPropName");
        propObject.set(PropertyConstants.TYPE, typeHelper.getType(URINamePair.STRING.getURI(), URINamePair.STRING.getName()));
        propObject.setBoolean(typeHelper.getOpenContentProperty(
            URINamePair.PROP_XML_XML_ELEMENT.getURI(), URINamePair.PROP_XML_XML_ELEMENT.getName()), false);

        Type t = typeHelper.define(typeObject);

        XSDHelper xsdHelper = _helperContext.getXSDHelper();
        assertEquals("mySdoTypeName", t.getName());
        assertEquals("myXmlTypeName", xsdHelper.getLocalName(t));

        assertEquals(1, t.getProperties().size());
        Property p = t.getProperty("mySdoPropName");
        assertEquals("mySdoPropName", p.getName());
        assertEquals("myXmlPropName", xsdHelper.getLocalName(p));
        assertEquals(true, xsdHelper.isAttribute(p));

    }

    @Test
    public void testOnDemandProperty() {
        DataFactory dataFactory = _helperContext.getDataFactory();
        TypeHelper typeHelper = _helperContext.getTypeHelper();

        DataObject propObject = dataFactory.create(URINamePair.PROPERTY.getURI(), URINamePair.PROPERTY.getName());
        propObject.setString(PropertyConstants.NAME, "OnDemandPropName");
        propObject.getList(PropertyConstants.ALIAS_NAME).add("alias1");
        propObject.getList(PropertyConstants.ALIAS_NAME).add("alias2");
        propObject.getList(PropertyConstants.ALIAS_NAME).add("alias3");
        propObject.set(PropertyConstants.TYPE, typeHelper.getType(int.class));
        propObject.setBoolean(PropertyConstants.CONTAINMENT, true);
        
        typeHelper.defineOpenContentProperty(null, propObject);
        
        propObject = dataFactory.create(URINamePair.PROPERTY.getURI(), URINamePair.PROPERTY.getName());
        propObject.setString(PropertyConstants.NAME, "OnDemandPropName2");
        List<String>aliasNames = new ArrayList<String>(3);
        aliasNames.add("alias1");
        aliasNames.add("alias2");
        aliasNames.add("alias3");        
        propObject.setList(PropertyConstants.ALIAS_NAME, aliasNames);
        propObject.set(PropertyConstants.TYPE, typeHelper.getType(int.class));
        propObject.setBoolean(PropertyConstants.CONTAINMENT, true);
        typeHelper.defineOpenContentProperty(null, propObject);
        
        aliasNames = propObject.getList(PropertyConstants.ALIAS_NAME);
        assertEquals(3, aliasNames.size());
    }


}
