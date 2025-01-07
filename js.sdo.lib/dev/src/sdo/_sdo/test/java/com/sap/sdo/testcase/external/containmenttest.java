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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.Arrays;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.ContainerInf;
import com.sap.sdo.testcase.typefac.SimpleAttrIntf;
import com.sap.sdo.testcase.typefac.SimpleChain;
import com.sap.sdo.testcase.typefac.SimpleChild;
import com.sap.sdo.testcase.typefac.SimpleParent;

import commonj.sdo.DataObject;

public class ContainmentTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public ContainmentTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testContainment() throws Exception {
        SimpleParent parent = (SimpleParent)_helperContext.getDataFactory().create(SimpleParent.class);
        SimpleChild child = (SimpleChild)_helperContext.getDataFactory().create(SimpleChild.class);

        parent.setChild(child);

        String xml = _helperContext.getXMLHelper().save((DataObject)parent, null, "parent");

        assertNotNull(xml);
        System.out.println(xml);
    }

    @Test
    public void testChain() throws Exception {
        SimpleChain chainLink1 = (SimpleChain)_helperContext.getDataFactory().create(SimpleChain.class);
        SimpleChain chainLink2 = (SimpleChain)_helperContext.getDataFactory().create(SimpleChain.class);
        SimpleChain chainLink3 = (SimpleChain)_helperContext.getDataFactory().create(SimpleChain.class);
        SimpleChain chainLink4 = (SimpleChain)_helperContext.getDataFactory().create(SimpleChain.class);
        SimpleChain chainLink5 = (SimpleChain)_helperContext.getDataFactory().create(SimpleChain.class);

        chainLink1.setNext(chainLink2);
        chainLink2.setNext(chainLink3);
        chainLink3.setNext(chainLink4);
        chainLink4.setNext(chainLink5);
        chainLink5.setNext(chainLink1);

        String xml = _helperContext.getXMLHelper().save((DataObject)chainLink1, null, "chain");

        assertNotNull(xml);
        System.out.println(xml);
    }

    @Test
    public void testDepartment() throws Exception {
        SimpleAttrIntf empl1 = (SimpleAttrIntf)_helperContext.getDataFactory().create(SimpleAttrIntf.class);
        empl1.setName("empl1");
        SimpleAttrIntf empl2 = (SimpleAttrIntf)_helperContext.getDataFactory().create(SimpleAttrIntf.class);
        empl2.setName("empl2");
        SimpleAttrIntf empl3 = (SimpleAttrIntf)_helperContext.getDataFactory().create(SimpleAttrIntf.class);
        empl3.setName("empl3");

        ContainerInf department = (ContainerInf)_helperContext.getDataFactory().create(ContainerInf.class);
        department.setEmployees(Arrays.asList(new SimpleAttrIntf[]{empl1, empl2, empl3}));
        department.setEmployeeOfTheMonth(empl3);

        assertSame(empl3, department.getEmployeeOfTheMonth());
        assertEquals(3, department.getEmployees().size());
        assertEquals(empl1, department.getEmployees().get(0));
        assertEquals(empl2, department.getEmployees().get(1));
        assertEquals(empl3, department.getEmployees().get(2));

        String xml = _helperContext.getXMLHelper().save((DataObject)department, null, "department");

        assertNotNull(xml);
        System.out.println(xml);
}
}
