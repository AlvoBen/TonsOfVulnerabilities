﻿/*
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

public interface ICharTest {

    public Character getAcharacter();

    public void setAcharacter(Character v);

    @SdoPropertyMetaData(defaultValue="a")
    public char getAchar();

    public void setAchar(char v);

}
