/**
 * Copyright (c) 2007 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.session.exec;

/**
 * This interface grant execution details for remote communication 
 * to thread that access Execution Context in Common Session Management.
 * 
 * @author I041949, I024157
 */
public interface ExecutionDetails<T> {

  /**
   * This method provides execution details for P4 protocol, 
   * if caller thread performs P4 related communication  
   * 
   * @return Returns execution details as generic type.
   */
  public T getExecutionDetails();
  
  /**
   * This method returns the host name for remote 
   * caller if the thread is in processing remote communication.
   * 
   * @return IP of remote caller as String, when caller thread processes remote call or
   *         empty string - if the thread is not in remote call;
   */
  public String getHost();

}
