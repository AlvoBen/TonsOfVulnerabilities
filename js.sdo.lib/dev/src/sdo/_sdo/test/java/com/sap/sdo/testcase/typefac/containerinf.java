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
package com.sap.sdo.testcase.typefac;

import java.util.List;

import com.sap.sdo.api.SdoPropertyMetaData;

/**
 * @author D042774
 *
 */
public interface ContainerInf {
    @SdoPropertyMetaData(
        containment = false
    )
    SimpleAttrIntf getEmployeeOfTheMonth();

    void setEmployeeOfTheMonth(SimpleAttrIntf pEmployee);
    
    @SdoPropertyMetaData(
        containment = true
    )
    List<SimpleAttrIntf> getEmployees();
    
    void setEmployees(List<SimpleAttrIntf> pEmployees);
}
