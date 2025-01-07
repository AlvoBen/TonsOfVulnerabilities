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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.impl.types.builtin.PropertyType;
import com.sap.sdo.impl.types.builtin.TypeType;
import com.sap.sdo.impl.types.simple.JavaSimpleType;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.SimpleFacetIntf;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;

public class SimpleTypeTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public SimpleTypeTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testFacets() throws Exception {
    	Type t = _helperContext.getTypeHelper().getType(SimpleFacetIntf.class);
        SimpleFacetIntf c = (SimpleFacetIntf)_helperContext.getDataFactory().create(t);
    	c.setRestrictedString("abc");
    	c.setRestrictedString("abcde");
    	try {
    		c.setRestrictedString("a");
    		fail("facet check did not work");
    	} catch (IllegalArgumentException e) { //$JL-EXC$

    	}
    	try {
    		c.setRestrictedString("abcdef");
    		fail("facet check did not work");
    	} catch (IllegalArgumentException e) { //$JL-EXC$

    	}
    	c.setRestrictedString(null);

        DataObject d = (DataObject)c;
        Property p = d.getProperty("restrictedString");
        DataObject type = (DataObject)p.getType();

        if (type.isSet(TypeType.FACETS)) {
            DataObject facets = type.getDataObject(TypeType.FACETS);
            assertEquals("minlength", 3, facets.getInt(TypeType.FACET_MINLENGTH));
            assertEquals("maxlength", 5, facets.getInt(TypeType.FACET_MAXLENGTH));
        } else {
            fail("defined facets not found");
        }
    }

    @Test
    public void testDynamicFacet() throws Exception {
        DataObject facetObject = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        facetObject.set(TypeType.NAME, "SapFacetType");
        facetObject.set(TypeType.URI, "com.sap.sdo");
        facetObject.set(TypeType.DATA_TYPE, true);
        facetObject.set(TypeType.BASE_TYPE, Arrays.asList(new Type[]{JavaSimpleType.STRING}));
        facetObject.set("fieldlabel","my label");
        facetObject.set("description","maybe");

        Type facetType = _helperContext.getTypeHelper().define(facetObject);

        DataObject typeObject = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        typeObject.set(TypeType.NAME, "SapType");
        typeObject.set(TypeType.URI, "com.sap.sdo");

        DataObject prop = typeObject.createDataObject("property");
        prop.set(PropertyType.NAME, "field");
        prop.set(PropertyType.TYPE, facetType);
        prop.set("isCustomerExtension", true);

        Type t = _helperContext.getTypeHelper().define(typeObject);

        DataObject d = _helperContext.getDataFactory().create(t);

        Property p = d.getProperty("field");
        DataObject type = (DataObject)p.getType();

        d.set(p, "field value");

        assertEquals("field value", d.get("field"));
        assertTrue(((DataObject)p).getBoolean("isCustomerExtension"));
        assertEquals("my label", type.get("fieldlabel"));
        assertEquals("maybe", type.get("description"));
    }
}
