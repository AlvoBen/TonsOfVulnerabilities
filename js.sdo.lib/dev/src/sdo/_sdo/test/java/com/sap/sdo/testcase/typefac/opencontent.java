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

import java.util.Date;

import com.sap.sdo.api.Bool;
import com.sap.sdo.api.OpenContentProperty;
import com.sap.sdo.api.SdoPropertyMetaData;
import com.sap.sdo.api.SdoTypeMetaData;
import com.sap.sdo.api.XmlPropertyMetaData;

import commonj.sdo.ChangeSummary;

/**
 * @author D042774
 *
 */
@SdoTypeMetaData(
    attributeFormDefault=true,
    openContentProperties={@OpenContentProperty(name="attribute", containment=false)}
)
public interface OpenContent {
    @SdoPropertyMetaData(
        aliasNames={"property", "p"},
        xmlInfo=@XmlPropertyMetaData(xsdName="string")
    )
    String getProp();
    void setProp(String pProp);

    @SdoPropertyMetaData(nullable=Bool.TRUE)
    int getNullable();
    void setNullable(int pInt);
    
    ChangeSummary getChangeSummary();
    void setChangeSummary(ChangeSummary pChangeSummary);
    
    @SdoPropertyMetaData(sdoType="commonj.sdo#YearMonthDay")
    Date getDate();
    void setDate(Date pDate);
}
