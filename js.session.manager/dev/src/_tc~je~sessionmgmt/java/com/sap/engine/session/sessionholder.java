/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session;

import com.sap.engine.session.callback.Callback;

import java.io.IOException;

/**
 * Author: georgi-s
 * Date: May 4, 2004
 */
public interface SessionHolder extends Callback {

  public static final String CALLBACK_HANDLER = "session.SessionInvalidationCallback";

  String getSessionName();

  Session getSession() throws SessionNotFoundException;
  
  /*
   * Check if there is a session associated with this SessionHolder.
   * If so then creates RuntimeSessionModel for it.
   * RuntimeSessionModel allows a timeout session to expire. 

   * @return <tt>true</tt> if session exists
   */
  boolean sessionExist();

  Session getSession(SessionFactory factory) throws SessionException;

  void commitAccess() throws SessionException, IOException;

  boolean commited();

  void releaseAccess();

  void remove()throws SessionException;

}