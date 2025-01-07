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
 * Author: georgi-s
 * Date: 2005-5-3
 */
public class DummyUserContextDelegate extends UserContextDelegate {
  private static UserContextAccessor accessor = new DummyAccessor();
  static UserContextDelegate dummyImpl = new DummyUserContextDelegate();

  DummyUserContextDelegate() {
  }

  protected UserContext getCurrentUserContext() {
    return null;
  }

  protected UserContext getUserContext(Object userIdentity) {
    return null;
  }

  protected UserContextAccessor getUserContextAccessor() {
    return accessor;
  }

  private static class DummyAccessor implements UserContextAccessor {
    public UserContext getUserContext(Object alias) throws SecurityException {
      return null;
    }

    public UserContext getClientContext(Object clientId) throws SecurityException {
      return null;
    }

    public UserContext getByLoginSession(LoginSession session) throws SecurityException {
      return null;
    }

    public UserContext createUserContext(LoginSession loginSession) throws UserContextException {
      return null;
    }

    public void addUserContextAlias(Object alias, UserContext context) throws UserContextException {
    }

    public void removeAlias(Object alias) {
    }

    public Collection userContexts() {
      return null;
    }

    public void destroyUserContext(UserContext usrContext) throws UserContextException {
    }

    public boolean apply(Object alias) throws UserContextException {
      return false;
    }

    public boolean apply() throws UserContextException {
      return false;
    }
    
    public void empty() {
    }

    //ToDO Object session should be changed to SessionModel
    public void joinSession(Object alias, Object session) throws UserContextException {
    }

    public void removeSession(Object session) {
    }

    public void performLocked(String lock, Runnable action) throws UserContextException {
    }

  }
}
