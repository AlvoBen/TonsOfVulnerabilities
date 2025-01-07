/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.jndi.persistent.exceptions720;



public class NameNotFoundException extends javax.naming.NameNotFoundException {

  public boolean missingPathComponent = false;
  
  public NameNotFoundException(String msg) {
    super(msg);
  }
  
  public NameNotFoundException(String msg, Throwable linkedException) {
    super(msg);
    super.setRootCause(linkedException);
  }
  
  
}


