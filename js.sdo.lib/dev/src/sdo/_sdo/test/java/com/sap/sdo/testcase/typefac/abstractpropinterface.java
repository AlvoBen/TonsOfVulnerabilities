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
package com.sap.sdo.testcase.typefac;

import java.util.List;

import com.sap.sdo.api.SdoPropertyMetaData;

import commonj.sdo.DataObject;

public interface AbstractPropInterface {
    @SdoPropertyMetaData(containment=true)
    DataObject getContained();

    void setContained(DataObject data);
    
    @SdoPropertyMetaData(containment=false)
    DataObject getReferenced();
    
    void setReferenced(DataObject data);
    
    List<DataObject> getList();
    
    void setList(List<DataObject> list);
}
