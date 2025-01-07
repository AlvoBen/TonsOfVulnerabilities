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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.AbstractPropInterface;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;

/**
 * @author D042774
 *
 */
public class AbstractPropTypeTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public AbstractPropTypeTest(String pContextId, Feature pFeature) {
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
    public void testGetType() {
        Type type = _helperContext.getTypeHelper().getType(AbstractPropInterface.class);
        assertNotNull(type);

        Type containedType = type.getProperty("contained").getType();
        assertNotNull(containedType);
        assertFalse(containedType.isDataType());
        assertTrue(containedType.isAbstract());

        Type referencedType = type.getProperty("referenced").getType();
        assertNotNull(referencedType);
        assertFalse(referencedType.isDataType());
        assertTrue(referencedType.isAbstract());

        final Property listProp = type.getProperty("list");
        Type listType = listProp.getType();
        assertNotNull(listType);
        assertFalse(listType.isDataType());
        assertTrue(listType.isAbstract());
        assertTrue(listProp.isMany());

        System.out.println(_helperContext.getXSDHelper().generate(Collections.singletonList(type)));
    }

    @Test
    public void testAbtractPropType() {
        AbstractPropInterface data =
            (AbstractPropInterface)_helperContext.getDataFactory().create(AbstractPropInterface.class);
        assertNotNull(data);

        final DataObject contained = _helperContext.getDataFactory().create(AbstractPropInterface.class);
        data.setContained(contained);
        data.setReferenced(contained);

        assertSame(contained, data.getContained());
        assertSame(contained, data.getReferenced());

        final DataObject listElement = _helperContext.getDataFactory().create(AbstractPropInterface.class);
        data.getList().add(listElement);

        assertEquals(1, data.getList().size());
        assertSame(listElement, data.getList().get(0));

        System.out.println(_helperContext.getXMLHelper().save((DataObject)data, null, "data"));
    }

    @Test
    public void testCreateProperty() {
        DataObject data = _helperContext.getDataFactory().create(AbstractPropInterface.class);
        assertNotNull(data);

        try {
            data.createDataObject("contained");
            fail("IllegalArgumentException expected!");
        } catch (IllegalArgumentException ex) {
            assertEquals("cannot create the abstract data type commonj.sdo:DataObject", ex.getMessage());
        }

        try {
            data.createDataObject("referenced");
            fail("IllegalArgumentException expected!");
        } catch (IllegalArgumentException ex) {
            assertEquals("cannot create the abstract data type commonj.sdo:DataObject", ex.getMessage());
        }

        try {
            data.createDataObject("list");
            fail("IllegalArgumentException expected!");
        } catch (IllegalArgumentException ex) {
            assertEquals("cannot create the abstract data type commonj.sdo:DataObject", ex.getMessage());
        }
}
}
