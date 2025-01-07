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

import com.sap.sdo.api.SdoPropertyMetaData;
import com.sap.sdo.api.SdoTypeMetaData;
import com.sap.sdo.api.XmlPropertyMetaData;
@SdoTypeMetaData(sequenced=true)
public interface SequencedInheritedIntf extends SequencedParentIntf {

    String getMoreInfo();
    void setMoreInfo(String pMoreInfo);
    
    @SdoPropertyMetaData(xmlInfo=@XmlPropertyMetaData(xmlElement=false))
    String getExtraAttribute();
    void setExtraAttribute(String pExtraAttribute);
}
