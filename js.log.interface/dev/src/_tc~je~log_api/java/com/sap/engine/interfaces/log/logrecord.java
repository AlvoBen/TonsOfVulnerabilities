﻿/*
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.interfaces.log;

import java.util.Properties;

public interface LogRecord {

  public String getUser();


  public String getClientIP();


  public byte getLevel();


  public String getMessage();


  public String getAdditionalFieldValue(String additionalFieldName);


  public String[] getAdditionalFieldNames();


  public Properties getAllAditionalFields();

}

