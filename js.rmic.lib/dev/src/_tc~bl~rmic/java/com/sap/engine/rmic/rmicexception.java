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

package com.sap.engine.rmic;

public class RMICException extends Exception{

  static final long serialVersionUID = 1480489431142384970L;

  public RMICException(String msg){
    super(msg);
  }

  public RMICException(Throwable t){
    super(t);
  }
}
