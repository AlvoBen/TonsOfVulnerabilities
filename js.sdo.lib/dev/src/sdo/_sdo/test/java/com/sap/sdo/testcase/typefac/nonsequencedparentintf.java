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
import com.sap.sdo.api.SdoTypeMetaData;
import com.sap.sdo.api.XmlPropertyMetaData;

@SdoTypeMetaData(open=true)
public interface NonSequencedParentIntf {

    @SdoPropertyMetaData(containment=true, propertyIndex=1)
    List<NonSequencedParentIntf> getContained();
    void setContained(List<NonSequencedParentIntf> pValue);
    
    @SdoPropertyMetaData(containment=false, propertyIndex=0)
    List<NonSequencedParentIntf> getReferenced();
    void setReferenced(List<NonSequencedParentIntf> pValue);
    
    @SdoPropertyMetaData(xmlInfo=@XmlPropertyMetaData, propertyIndex=2)
    String getName();
    void setName(String pName);
    
    @SdoPropertyMetaData(xmlInfo=@XmlPropertyMetaData(xmlElement=false), propertyIndex=3)
    String getAttribute();
    void setAttribute(String pAttribute);
}
