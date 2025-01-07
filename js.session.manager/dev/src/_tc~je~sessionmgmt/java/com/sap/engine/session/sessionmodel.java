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

import com.sap.engine.session.mgmt.MgmtModel;
import com.sap.engine.session.spi.persistent.PersistentSessionModel;
import com.sap.engine.session.runtime.RuntimeSessionModel;

/**
 * This class present the system view of the used session model.
 * The session is present form two parts management and persistent.
 * The management Model is an abstraction used for session management.
 * The Persistent Model present system view of the user data stored in
 * the session.
 *
 * @author georgi-s
 *
 */
public abstract class SessionModel {

  private Session session;
  protected SessionDomain domain;
  protected PersistentSessionModel persistentModel;
  /**
   * indicate that the session is newly created and it is still not commited.
   */
  private boolean isNew = true;

  protected SessionModel() {
  }

  protected SessionModel(SessionDomain domain, Session s) {
    this.domain = domain;
    this.session = s;
  }

  /**
   * Returns the management model of the session
   *
   * @return the management model of this session
   */
  public abstract MgmtModel mgmtModel();


  /**
   * returns the Persistent Model of the session
   *
   * @return the Persistent model of this session
   */
  protected PersistentSessionModel persistentModel() {
    return persistentModel;
  }

  protected abstract RuntimeSessionModel runtimeModel();
  /**
   * Returns the session that is present from the model
   *
   * @return the session presents from the model
   */
  public Session session() {
    return session;
  }

  protected synchronized void setSession(Session s) {
    if (s == null) {
      if (this.session != null) {
        this.session.setSessionModel(null);
        this.session = null;
      }
      return;
    }

    this.session = s;
    s.setSessionModel(this);
    runtimeModel().setMaxInactiveInterval(s.maxInactivInterval);
  }

  protected abstract void invalidate();

  /**
   * Called when the session should be removed from the Session domain, without invalidation
   */
  protected abstract void remove();

  protected void setIsNew(boolean isNew) {
    this.isNew = isNew;
  }

  protected boolean isNew() {
    return isNew;
  }


}