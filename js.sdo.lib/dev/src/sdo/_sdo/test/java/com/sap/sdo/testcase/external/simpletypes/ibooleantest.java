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

import com.sap.sdo.api.SdoPropertyMetaData;

public interface IBooleanTest {
    public final static boolean DEFAULT_ABOOLEAN = true;

    public final static boolean DEFAULT_BBOOLEAN = false;

    @SdoPropertyMetaData(defaultValue="true")
    public boolean getAboolean();

    public void setAboolean(boolean v);
    @SdoPropertyMetaData(defaultValue="false")
    public boolean getBboolean();
    
    public boolean getCboolean();
}
