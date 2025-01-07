/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * This software is the confidential and proprietary information
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.rmi_p4.exception;

import java.rmi.RemoteException;

/**
 * @author Ivan Atanassov
 */
public class NoSuchMethodException extends RemoteException {

  static final long serialVersionUID = 4717111268639794844L;
  
  public NoSuchMethodException() {
  }

  public NoSuchMethodException(String s) {
    super(s);
  }

  public NoSuchMethodException(String s, Throwable ex) {
    super(s, ex);
  }
}
