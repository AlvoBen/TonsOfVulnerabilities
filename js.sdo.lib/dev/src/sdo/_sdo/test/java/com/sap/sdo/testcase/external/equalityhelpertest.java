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

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.SimpleContainedIntf;
import com.sap.sdo.testcase.typefac.SimpleContainingIntf;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;

public class EqualityHelperTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public EqualityHelperTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testSimpleShallowSequencedEqual() {
        DataObject typeDO = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        typeDO.set("open",true);
        typeDO.set("name","OpenType");
        typeDO.set("uri","commonj.sdo");
        Type t = _helperContext.getTypeHelper().define(typeDO);

        DataObject d1 = _helperContext.getDataFactory().create(t);
        DataObject d2 = _helperContext.getDataFactory().create(t);
        DataObject prop = _helperContext.getDataFactory().create("commonj.sdo","Property");
        prop.set("name","p1");
        prop.set("type",_helperContext.getTypeHelper().getType(String.class));
        DataObject prop2 = _helperContext.getDataFactory().create("commonj.sdo","Property");
        prop2.set("name","openPropertyOfThisTypeObject");
        prop2.set("type",_helperContext.getTypeHelper().getType(String.class));
        Property p1 = _helperContext.getTypeHelper().defineOpenContentProperty(null, prop);
        Property p2 = _helperContext.getTypeHelper().defineOpenContentProperty(null, prop2);
        d1.set(p1, "s1");
        d1.setFloat(p2, 12f);

        d2.set(p1, "s1");
        d2.setFloat(p2, 11f);
        assertTrue("shallow sequenced !equal on opentype failed",
                !_helperContext.getEqualityHelper().equalShallow(d1, d2));
        d2.setFloat(p2, 12f);
        assertTrue("shallow sequenced equal on opentype failed",
                _helperContext.getEqualityHelper().equalShallow(d1, d2));

        DataObject p = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        p.set("name", "co");
        p.set("type", t);
        p.setBoolean("containment", true);
        DataObject c = _helperContext.getDataFactory().create(t);
        Property pr = _helperContext.getTypeHelper().defineOpenContentProperty(null, p);
        d1.setDataObject(pr, c);
        assertTrue("shallow sequenced !equal on opentype failed",
                !_helperContext.getEqualityHelper().equalShallow(d1, d2));
    }

    @Test
    public void testSimpleDeepSequencedEqual() {
        DataObject typeDO = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        typeDO.set("open",true);
        typeDO.set("name","OpenType");
        typeDO.set("uri","commonj.sdo");
        Type t = _helperContext.getTypeHelper().define(typeDO);

        DataObject d1 = _helperContext.getDataFactory().create(t);
        DataObject d2 = _helperContext.getDataFactory().create(t);
        DataObject prop = _helperContext.getDataFactory().create("commonj.sdo","Property");
        prop.set("name","p1");
        prop.set("type",_helperContext.getTypeHelper().getType(String.class));
        Property property = _helperContext.getTypeHelper().defineOpenContentProperty(null, prop);
        d2.set(property, "s1");
        d1.set(property, "s1");
        assertTrue("sequenced equal on opentype failed",
                _helperContext.getEqualityHelper().equal(d1, d2));

        DataObject p = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        p.set("name", "co");
        p.set("type", t);
        p.setBoolean("containment", true);
        DataObject c = _helperContext.getDataFactory().create(t);
        Property pr = _helperContext.getTypeHelper().defineOpenContentProperty(null, p);
        d1.setDataObject(pr, c);
        DataObject pc = _helperContext.getDataFactory().create("commonj.sdo","Property");
        pc.set("name","pc");
        pc.set("type",_helperContext.getTypeHelper().getType(String.class));
        Property pcProperty = _helperContext.getTypeHelper().defineOpenContentProperty(null, pc);
        c.set(pcProperty, "test");
        assertTrue(_helperContext.getEqualityHelper().equal(d1, d1));
        assertTrue("sequenced !equal on opentype failed",
                !_helperContext.getEqualityHelper().equal(d1, d2));
        assertTrue("sequenced !equal on opentype failed", !d1.equals(d2));
        DataObject c2 = _helperContext.getDataFactory().create(t);
        d2.setDataObject(pr, c2);
        c2.set(pcProperty, "test");
        assertTrue("sequenced equal on opentype failed",
                _helperContext.getEqualityHelper().equal(d1, d2));
        c2.set(pcProperty, "test3");
        assertTrue("sequenced !equal on opentype failed",
                !_helperContext.getEqualityHelper().equal(d1, d2));
        assertTrue("sequenced !equal on opentype failed", !d1.equals(d2));
    }

    @Test
    public void testTypedSDOEquality() {
        SimpleContainingIntf s1 = (SimpleContainingIntf)_helperContext.getDataFactory()
                .create(SimpleContainingIntf.class);
        SimpleContainingIntf s2 = (SimpleContainingIntf)_helperContext.getDataFactory()
                .create(SimpleContainingIntf.class);
        assertTrue("equal on typed SDO",
            _helperContext.getEqualityHelper().equal((DataObject)s1, (DataObject)s2));
        s1.setX("x");
        s2.setX("x");
        assertTrue("equal on typed SDO",
            _helperContext.getEqualityHelper().equal((DataObject)s1, (DataObject)s2));
        s2.setX("y");
        assertTrue("!equal on typed SDO",
            !_helperContext.getEqualityHelper().equal((DataObject)s1, (DataObject)s2));
        s2.setX("x");
        assertTrue("equal on typed SDO",
            _helperContext.getEqualityHelper().equal((DataObject)s1, (DataObject)s2));
        s1.setInner((SimpleContainedIntf)_helperContext.getDataFactory()
                .create(SimpleContainedIntf.class));
        assertTrue("!equal on typed SDO",
            !_helperContext.getEqualityHelper().equal((DataObject)s1, (DataObject)s2));
        s2.setInner((SimpleContainedIntf)_helperContext.getDataFactory()
                .create(SimpleContainedIntf.class));
        assertTrue("equal on typed SDO",
            _helperContext.getEqualityHelper().equal((DataObject)s1, (DataObject)s2));
        s1.getInner().setName("name");
        assertTrue("!equal on typed SDO",
            !_helperContext.getEqualityHelper().equal((DataObject)s1, (DataObject)s2));
        s2.getInner().setName("name");
        assertTrue("equal on typed SDO",
            _helperContext.getEqualityHelper().equal((DataObject)s1, (DataObject)s2));
        s2.getInner().setName("nix");
        assertTrue("!equal on typed SDO",
            !_helperContext.getEqualityHelper().equal((DataObject)s1, (DataObject)s2));
    }

    @Test
    public void testMixedContentEquality() throws IOException {
        XMLHelper xmlHelper = _helperContext.getXMLHelper();

        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "1.xml");
        final XMLDocument doc1 = xmlHelper.load(xmlUrl.openStream(), xmlUrl.toString(), null);
        final XMLDocument doc2 = xmlHelper.load(xmlUrl.openStream(), xmlUrl.toString(), null);
        assertTrue(_helperContext.getEqualityHelper().equal(doc1.getRootObject(), doc2.getRootObject()));

    }
}
