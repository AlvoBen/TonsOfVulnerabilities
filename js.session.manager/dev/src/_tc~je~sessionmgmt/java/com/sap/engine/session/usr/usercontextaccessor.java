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


import java.util.Collection;

/**
 * Author: Georgi-S
 * Date: 2005-4-11
 */
// TODO - try to remove this class
public interface UserContextAccessor {

  /**
   * @deprecated use getClientContext(String clientId) instead
   */
  UserContext getUserContext(Object alias) throws SecurityException;

  UserContext getClientContext(Object clientId) throws SecurityException;

  /**
   * @deprecated will be removed
   */
  UserContext getByLoginSession(LoginSession session) throws SecurityException;

  /**
   * @deprecated will be removed
   */
  UserContext createUserContext(LoginSession loginSession) throws UserContextException;

  Collection userContexts();

  /**
   * @deprecated will be removed
   */
  void addUserContextAlias(Object alias, UserContext context) throws UserContextException;

  void removeAlias(Object alias);

  void destroyUserContext(UserContext usrContext) throws UserContextException;

  boolean apply(Object alias) throws UserContextException;

  boolean apply() throws UserContextException;

  void empty();

  //ToDO Object session should be changed to SessionModel
  /**
   * @deprecated will be deleted
   */
  void joinSession(Object alias, Object session) throws UserContextException;

  void removeSession(Object session);

  public void performLocked(String lock, Runnable action) throws UserContextException;

}