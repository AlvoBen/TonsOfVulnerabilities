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
@SdoTypeMetaData(sequenced=true,open=true)
public interface SequencedParentIntf {

    @SdoPropertyMetaData(containment=true)
    List<SequencedParentIntf> getContained();
    void setContained(List<SequencedParentIntf> pValue);
    
    @SdoPropertyMetaData(containment=false)
    List<SequencedParentIntf> getReferenced();
    void setReferenced(List<SequencedParentIntf> pValue);
    
    @SdoPropertyMetaData(xmlInfo=@XmlPropertyMetaData)
    String getName();
    void setName(String pName);
    
    @SdoPropertyMetaData(xmlInfo=@XmlPropertyMetaData(xmlElement=false))
    String getAttribute();
    void setAttribute(String pAttribute);
}
