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

/**
 * Author: georgi-s
 * Date: May 4, 2004
 */
public class SessionHolderImpl extends AbstractSessionHolder {

  public SessionHolderImpl() {
     super();
  }

  public SessionHolderImpl(String sessionId, SessionDomain domain) {
    super(sessionId, domain);
  }

  public boolean isApplied() {
    return false;
  }

}
