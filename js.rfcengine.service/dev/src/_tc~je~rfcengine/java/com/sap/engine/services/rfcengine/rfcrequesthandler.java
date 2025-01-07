/*
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.rfcengine;

import com.sap.mw.jco.JCO;

public interface RFCRequestHandler {

  public void handleRequest(JCO.Function function) throws Exception;
  public void setTicket(String ticket);

}

