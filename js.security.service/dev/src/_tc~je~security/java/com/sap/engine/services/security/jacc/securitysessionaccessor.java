/*
 * Copyright (c) 2004 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.security.jacc;

import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;

import com.sap.engine.frame.core.thread.ThreadContext;
import com.sap.engine.interfaces.security.SecurityContextObject;
import com.sap.engine.interfaces.security.SecuritySession;
import com.sap.engine.lib.security.SecurityContextAccessor;
import com.sap.engine.services.security.SecurityServerFrame;
import com.sap.engine.services.security.login.SecurityContext;

/**
 * 
 * @version 7.0
 * @author  Stephan Zlatarev
 */
public class SecuritySessionAccessor extends SecurityContextAccessor {

  private int threadContextId = -1;

  public boolean isCurrentSecurityContextAnonymous() {
    SecurityContextObject context = getCurrentContext();

    if (context != null) {
      return context.getSession().getAuthenticationConfiguration() == null;
    } else {
      return true;
    }
  }

  public Subject getCurrentSubject() {
    SecurityContextObject context = getCurrentContext();
    return (context != null) ? context.getSession().getSubject() : null;
  }

  public Subject getAuthenticatedSubject() {
    SecurityContext context = (SecurityContext) getCurrentContext();
    SecuritySession session = (context != null) ? context.getSessionWithoutDomainCombiner() : null;
    Subject subject = null;

    if ((session != null) && PolicyContext.getContextID() != null) {
      subject = session.getSubject();
    }
    return subject;
  }

  private SecurityContextObject getCurrentContext() {
    ThreadContext currentThreadContext  = SecurityServerFrame.threadContext.getThreadContext();

    if (currentThreadContext != null) {
      if (threadContextId == -1) {
        threadContextId = currentThreadContext.getContextObjectId("security");
      }
  
      return (SecurityContextObject) currentThreadContext.getContextObject(threadContextId);
    } else {
      return null;
    }
  }

}
