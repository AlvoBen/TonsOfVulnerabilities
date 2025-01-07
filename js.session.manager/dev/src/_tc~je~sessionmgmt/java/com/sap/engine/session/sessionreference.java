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

import com.sap.engine.session.trace.Trace;

import java.io.Serializable;

/**
 * Author: georgi-s
 * Date: 2005-6-12
 */
public class SessionReference implements Serializable {

  transient Session session;
  String sessionId;
  DomainReference domain;

  public SessionReference(Session target) {
    this.session = target;
    domain = session.domain().getReference();
    sessionId = session.sessionId();
  }

  public Session getSession() {
    if (session == null) {
      try {
        session = domain.getEnclosedSessionDomain().getSessionImmutable(sessionId);
      } catch (SessionException e) {
        Trace.logException(e);
      }
    }
    return session;
  }

  public String getSessionId() {
    return sessionId;
  }

  public SessionDomain getDomain() {
    return domain.getEnclosedSessionDomain();
  }


  public int hashCode() {
    return getSession().hashCode();
  }

  public boolean equals(Object obj) {
    return obj instanceof SessionReference && getSession().equals(((SessionReference) obj).getSession());

  }
}
