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

import com.sap.sdo.api.SdoPropertyMetaData;

/**
 * @author D042774
 *
 */
public interface SimpleSdoNameIntf {
    @SdoPropertyMetaData(sdoName="foo")
    String getBar();
    void setBar(String pBar);
}
