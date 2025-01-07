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
package com.sap.sdo.testcase.external.simpletypes;

import java.util.List;

import com.sap.sdo.api.SdoPropertyMetaData;

public interface IStringsTest {

    @SdoPropertyMetaData(sdoType="commonj.sdo#Strings")
    public List<String> getStrings();

    public void setStrings(List<String> s);

}
