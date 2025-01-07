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
@SdoTypeMetaData(sequenced=true)
public interface SequencedOppositeIntf {

    @SdoPropertyMetaData(opposite="sv", containment=true)
    List<SequencedOppositeIntf> getMv();
    void setMv(List<SequencedOppositeIntf> pValue);
    
    @SdoPropertyMetaData(containment=false)
    SequencedOppositeIntf getSv();
    void setSv(SequencedOppositeIntf pValue);
    
    @SdoPropertyMetaData(xmlInfo=@XmlPropertyMetaData)
    String getName();
    void setName(String pName);
    
}
