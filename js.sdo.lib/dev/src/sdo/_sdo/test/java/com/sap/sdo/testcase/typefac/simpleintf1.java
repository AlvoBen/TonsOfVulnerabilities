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
public interface SimpleIntf1 {

    public final static String DEFAULT_DATA = "data";
    public final static String DEFAULT_ID = "an id";

    @SdoPropertyMetaData(xmlInfo=@XmlPropertyMetaData())
    public String getName();

    public void setName(String name);

    @SdoPropertyMetaData(defaultValue="an id")
    public String getId();

    @SdoPropertyMetaData(defaultValue="data")
    public String getData();

    public void setData(String data);

    @SdoPropertyMetaData(defaultValue="true")
    public boolean isGreen();

    public void setGreen(boolean green);

    @SdoPropertyMetaData(defaultValue="true")
    public boolean isBlue();
}
