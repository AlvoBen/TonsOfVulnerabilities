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

import java.io.Serializable;

/**
 * Author: georgi-s
 * Date: Jun 22, 2004
 */
public class DomainReference implements Serializable {

  private String contextName;
  private String path;

  private long timestamp;

  public DomainReference(SessionDomain domain) {
    this.contextName = domain.getEnclosingContext().getName();
    this.path = domain.path();
    this.timestamp = domain.creationTime;
  }

  public SessionDomain getEnclosedSessionDomain() {
		return SessionContextFactory.getInstance().getSessionContext(contextName, false).findSessionDomain(path);
  }

  public Object getLocalAttribute(String name) {
    return getEnclosedSessionDomain().getDomainAttribute(name);
  }

  public void setLocalAttribute(String name, Object value) {
    getEnclosedSessionDomain().setDomainAttribute(name, value);
  }

  public boolean isDomainChanged() {
    return timestamp != getEnclosedSessionDomain().creationTime;
  }
}
