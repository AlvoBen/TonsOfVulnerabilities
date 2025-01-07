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

import java.net.URL;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapDataHelper;
import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.ReadOnlyInterface;

import commonj.sdo.DataObject;
import commonj.sdo.Sequence;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

/**
 * @author D042774
 *
 */
public class SapDataHelperTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public SapDataHelperTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.testcase.SdoTestCase#setUp()
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

    /**
     * Test method for {@link com.sap.sdo.impl.data.DataHelperImpl#setReadOnlyMode(commonj.sdo.DataObject, boolean)}.
     */
    @Test
    public void testSetReadOnlyMode() {
        DataObject dO = _helperContext.getDataFactory().create(ReadOnlyInterface.class);
        Sequence sequence = dO.getSequence();
        try {
            sequence.add("multi", "1");
        } catch (UnsupportedOperationException ex) {
            assertEquals("Property multi is read-only", ex.getMessage());
        }

        ((SapDataHelper)_helperContext.getDataHelper()).setReadOnlyMode(dO, false);
        sequence.add("multi", "1");
        sequence.add("single", "2");
        sequence.add("multi", "3");
        ((SapDataHelper)_helperContext.getDataHelper()).setReadOnlyMode(dO, true);

        assertEquals("2", dO.getString("single"));
        List<String> multi = dO.getList("multi");
        assertEquals(2, multi.size());
        assertEquals("1", multi.get(0));
        assertEquals("3", multi.get(1));

        try {
            sequence.add("multi", "1");
        } catch (UnsupportedOperationException ex) {
            assertEquals("Property multi is read-only", ex.getMessage());
        }
    }

    /**
     * Test method for {@link com.sap.sdo.impl.data.DataHelperImpl#getHelperContext(commonj.sdo.DataObject)}.
     * @throws Exception
     */
    @Test
    public void testGetHelperContext() throws Exception {
        final String schemaFileName = PACKAGE + "ContactList.xsd";
        URL schemalUrl = getClass().getClassLoader().getResource(schemaFileName);
        List<Type> types = _helperContext.getXSDHelper().define(schemalUrl.openStream(), schemalUrl.toString());

        final String xmlFileName = PACKAGE + "ContactList.xml";
        URL xmlUrl = getClass().getClassLoader().getResource(xmlFileName);

        XMLDocument doc = _helperContext.getXMLHelper().load(xmlUrl.openStream(), xmlUrl.toString(), null);
        DataObject contactList = doc.getRootObject();

        assertEquals(
            ((SdoType)_helperContext.getTypeHelper().getType("http://www.example.com/Customer", "ContactList")).getHelperContext(),
            ((SapDataHelper)_helperContext.getDataHelper()).getHelperContext(contactList));

        SdoType contactType = (SdoType)_helperContext.getTypeHelper().getType("http://www.example.com/Customer", "Contact");
        for (DataObject contact : (List<DataObject>)contactList.getList("Contact")) {
            assertEquals(
                contactType.getHelperContext(),
                ((SapDataHelper)_helperContext.getDataHelper()).getHelperContext(contact));
        }
    }

}
