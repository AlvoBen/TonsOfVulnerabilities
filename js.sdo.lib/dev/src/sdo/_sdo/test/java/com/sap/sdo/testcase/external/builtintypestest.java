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

import java.util.List;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;

public class BuiltInTypesTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public BuiltInTypesTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testTypeType() {
        Type t = _helperContext.getTypeHelper().getType("commonj.sdo", "Type");
        assertTrue("failed to retrieve {commonj.sdo}Type", t != null);
        assertTrue("incorrect URI " + t.getURI(), "commonj.sdo".equals(t
                .getURI()));
        assertTrue("incorrect name " + t.getName(), "Type".equals(t
                .getName()));
        String[] names = new String[] { "baseType", "property", "aliasName",
                "name", "uri", "dataType", "open", "sequenced", "abstract" };
        List ps = t.getProperties();

        assertTrue("incorrect number of properties",
                ps.size() >= names.length);
        for (int i = 0; i < names.length; i++) {
            Property p = (Property)ps.get(i);
            assertTrue("property mismatch " + names[i] + "!="
                    + p.getName(), names[i].equals(p.getName()));
        }
        assertTrue("Type is not open", t.isOpen());
        assertTrue("Type is abstract", !t.isAbstract());
    }

    @Test
    public void testTypeProperty() {
        Type t = _helperContext.getTypeHelper().getType("commonj.sdo", "Property");
        assertTrue("failed to retrieve {commonj.sdo}Property", t != null);
        assertTrue("incorrect URI " + t.getURI(), "commonj.sdo".equals(t
                .getURI()));
        assertTrue("incorrect name " + t.getName(), "Property".equals(t
                .getName()));
        String[] names = new String[] { "aliasName", "name", "many",
                "containment", "default", "readOnly", "type", "opposite", "nullable", "containingType" };
        List ps = t.getProperties();
        assertTrue("incorrect number of properties",
                ps.size() >= names.length);
        for (int i = 0; i < names.length; i++) {
            Property p = (Property)ps.get(i);
            assertTrue("property mismatch " + names[i] + "!="
                    + p.getName(), names[i].equals(p.getName()));
        }
        assertTrue("Property is not open", t.isOpen());
        assertTrue("Property is abstract", !t.isAbstract());
    }

    @Test
    public void testTypeOpenType() {
        DataObject typeDO =
            _helperContext.getDataFactory().create("commonj.sdo", "Type");
        typeDO.set("open",true);
        typeDO.set("name","OpenType");
        typeDO.set("uri","commonj.sdo");
        Type t = _helperContext.getTypeHelper().define(typeDO);
        assertTrue("failed to retrieve {commonj.sdo}OpenType", t != null);
        assertTrue("incorrect URI " + t.getURI(), "commonj.sdo".equals(t
                .getURI()));
        assertTrue("incorrect name " + t.getName(), "OpenType".equals(t
                .getName()));
        List ps = t.getProperties();
        assertTrue("incorrect number of properties", ps.size() == 0);
        assertTrue("OpenType is not open", t.isOpen());
        assertTrue("OpenType is abstract", !t.isAbstract());
        DataObject d = _helperContext.getDataFactory().create(t);
        d.set("testprop","testval");
        Object rv = d.get("testprop");
        assertTrue("bad value returned from value lookup " + rv,"testval".equals(rv));
        d.unset("testprop");
        DataObject p =
            _helperContext.getDataFactory().create("commonj.sdo", "Property");
        p.set("type", _helperContext.getTypeHelper().getType("commonj.sdo", "String"));
        p.set("name", "aprop");
        p.setBoolean("containment", true);
        d.set(_helperContext.getTypeHelper().defineOpenContentProperty(null, p), "testvalue");
        Object o = d.get("aprop");
        assertTrue("bad value returned from value lookup " + o,
                "testvalue".equals(o));
        ps = d.getInstanceProperties();
        assertTrue("wrong length of instance properties " + ps.size(), ps
                .size() == 1);
        Property p0 = (Property)ps.get(0);
        assertTrue("bad property name " + p0.getName(), "aprop".equals(p0
                .getName()));
        assertTrue("bad type " + p0.getType(), _helperContext.getTypeHelper()
                .getType("commonj.sdo", "String").equals(p0.getType()));
        assertTrue("not flagged as set ", d.isSet("aprop"));
        d.unset("aprop");
        ps = d.getInstanceProperties();
        assertTrue("lookup should return null", d.get("aprop") == null);
        assertTrue("wrong length of instance properties after unset "
                + ps.size(), ps.size() == 0);
        assertTrue("not flagged as unset ", !d.isSet("aprop"));
    }
}
