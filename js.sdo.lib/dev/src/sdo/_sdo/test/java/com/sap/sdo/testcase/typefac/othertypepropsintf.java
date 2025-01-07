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

import java.util.Date;

import com.sap.sdo.api.SdoPropertyMetaData;

public interface OtherTypePropsIntf {

    @SdoPropertyMetaData(sdoType="commonj.sdo#Int")
    public String getNumber();
    public void setNumber(String number);

    @SdoPropertyMetaData(sdoType="commonj.sdo#String")
    public Integer getId();
    public void setId(Integer id);

    @SdoPropertyMetaData(sdoType="commonj.sdo#YearMonthDay")
    public Date getDate();
    public void setDate(Date date);

    @SdoPropertyMetaData(sdoType="commonj.sdo#String")
    public int getInt();
    public void setInt(int id);

    @SdoPropertyMetaData(sdoType="commonj.sdo/java#IntObject")
    public int getInteger();
    public void setInteger(int id);
}
