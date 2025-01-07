/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session.usr;
/*
 * Author: i024157 /Georgi Stanev/ 
 */

public interface ClientSession {
  String getClientId();
  String getSessionId();
  void beforeInvalidateClientSession();
  void invalidateClientSession();

  /**
   * This method is called when the AbstractSecuritySession is logged out
   * In this case all sessions should be invalidated, since the user logouts
   */
  void invalidateIfNotActive();
}
