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

import java.io.IOException;
import java.util.HashMap;

import javax.security.auth.Subject;

import com.sap.engine.session.callback.Callback;
import com.sap.engine.session.callback.CallbackHandler;
import com.sap.engine.session.callback.DuplicateNameException;
import com.sap.engine.session.trace.Locations;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public abstract class SessionUserContext {

  protected static Location loc = Locations.USER_LOC;

  static final HashMap<String, CallbackHandler> handlers = new HashMap<String, CallbackHandler>();
  static UserContextDelegate delegate = DummyUserContextDelegate.dummyImpl;


  public abstract Subject getSubject();

  public abstract String getUser();

  public abstract void addCallback(Callback callback) throws UserContextException, IOException;

  public abstract void removeCallback(Callback callback) throws IOException;

  public abstract void addAttribute(String key, Object value) throws UserContextException, IOException;

  public abstract Object getAttribute(String key) throws UserContextException, IOException;


  public static SessionUserContext getSessionUserContext() {
    try {
      return delegate.getCurrentUserContext();
    } catch (NullPointerException e) {
      loc.traceThrowableT(Severity.WARNING, "Session management provider is not configured", new IllegalStateException("Session Management is not configured!"));
      throw new IllegalStateException("Session Management is not configured!");
    }
  }

  public static void addCallbackHandler(CallbackHandler handler) throws DuplicateNameException {
    synchronized (handlers) {
      if (handlers.containsKey(handler.handlerName())) {
        DuplicateNameException dn = new DuplicateNameException("The CallbackHandler " + handler.handlerName() + " already exist.");
        loc.traceThrowableT(Severity.DEBUG, "Duplicate callback handler", dn);
        throw dn;
      }
      handlers.put(handler.handlerName(), handler);
    }
  }

  public static CallbackHandler getCallbackHandler(String name) {
    return handlers.get(name);
  }

  public static void removeCallbackHandler(CallbackHandler handler) {
    synchronized (handlers) {
      handlers.remove(handler.handlerName());
    }
  }


}
