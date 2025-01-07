/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session.runtime.http;

import com.sap.engine.session.CreateException;
import com.sap.engine.session.Session;
import com.sap.engine.session.SessionContext;
import com.sap.engine.session.SessionDomain;
import com.sap.engine.session.runtime.RuntimeSessionModel;
/*
 * Author: i024157 /Georgi Stanev/ 
 */

public class HttpSessionDomain extends SessionDomain {

  public HttpSessionDomain(String name, SessionContext context, SessionDomain parent) {
    super(name, context, parent);
  }

   protected RuntimeSessionModel createRuntimeSessionModel(String sessionId) throws CreateException {
		 return new HttpRuntimeSessionModel(sessionId, this, failoverMode);
  }

  protected RuntimeSessionModel createRuntimeSessionModel(String sessionId, Session session) throws CreateException {
		return new HttpRuntimeSessionModel(sessionId, this, session, failoverMode);
  }


  protected SessionDomain subDomainInstance(String name, SessionContext context) {
    return new HttpSessionDomain(name, context, this);
  }
}
