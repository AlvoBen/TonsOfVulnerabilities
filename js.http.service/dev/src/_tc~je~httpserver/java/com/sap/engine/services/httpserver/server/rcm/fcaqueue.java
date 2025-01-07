/*
 * Copyright (c) 2000-2009 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver.server.rcm;

import com.sap.bc.proj.jstartup.fca.FCAConnection;
import com.sap.bc.proj.jstartup.fca.FCAException;

public interface FCAQueue {

  public FCAConnection poll(String key) throws FCAException;
  
  public void add(FCAConnection conn, String key) throws FCAException;
}
