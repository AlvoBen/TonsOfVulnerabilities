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
package com.sap.engine.services.rmi_p4.dsr;

/**
 * This interface is used in order DSR service to register real implementation for the P4 client instrumentation
 * The instrumented points are:
 * - when P4 request starts
 * - when P4 request ends
 * 
 * @author Simeon Stefanov, Tsvetko Trendafilov
 */
public interface DSRP4Server {

  /**
   * This method is invoked when P4 request starts. 
   * The passed context information is host, port, transport type, 
   * transferred bytes.
   * 
   * @param context - P4 context information, including remote host, 
   * P4 port, (or server ID in case of internal instance communication),
   * received or sent bytes, invoked operation.
   */
  public void requestStart(DSRP4RequestContextImpl context);

  /**
   * This method is invoked when P4 reply is received or sent. 
   * The passed context information is host, port, transport type, 
   * transferred bytes.
   * 
   * @param statistics - P4 context information, including remote host, 
   * P4 port, (or server ID in case of internal instance communication),
   * received or sent bytes.
   */
  public void requestEnd(DSRP4RequestContextImpl statistics);
  
}
