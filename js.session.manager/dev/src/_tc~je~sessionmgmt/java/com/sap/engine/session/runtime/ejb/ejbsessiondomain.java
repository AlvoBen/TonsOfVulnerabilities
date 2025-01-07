/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session.runtime.ejb;

import com.sap.engine.session.*;
import com.sap.engine.session.runtime.RuntimeSessionModel;
import com.sap.bc.proj.jstartup.sadm.ShmApplication;
import com.sap.bc.proj.jstartup.sadm.ShmEjb;
import com.sap.bc.proj.jstartup.sadm.ShmException;
import com.sap.tc.logging.Severity;
/*
 * Author: i024157 /Georgi Stanev/ 
 */

public class EjbSessionDomain extends SessionDomain {

  ShmApplication shmApp;
  ShmEjb shmEjb;

  public EjbSessionDomain(String name, SessionContext context, SessionDomain parent) {
    super(name, context, parent);
  }

  protected SessionDomain subDomainInstance(String name, SessionContext context) {
    return new EjbSessionDomain(name, context, this);
  }
  
  synchronized ShmEjb getShmEjbObject() {
    if (shmEjb == null) {
      ShmApplication shmApp = getShmApplication();
      if (shmApp != null) {
        try {
          shmEjb = shmApp.addEjb(getName(),-1);
        } catch (ShmException e) {
          loc.traceThrowableT(Severity.WARNING,"",e);
        }
      }
    }

    return shmEjb;
  }

  synchronized ShmApplication getShmApplication() {
    if (shmApp == null) {
      EjbSessionDomain parent = (EjbSessionDomain) this.parent();
      if (parent != null) {
        return ((EjbSessionDomain)parent()).getShmApplication();
      }
      try {
        shmApp = new ShmApplication(getName());
      } catch (ShmException e) {
        loc.traceThrowableT(Severity.WARNING,"",e);
      }
    }

    return shmApp;
  }



  protected RuntimeSessionModel createRuntimeSessionModel(String sessionId) throws CreateException {
    return new EJBRuntimeSessionModel(sessionId, this, failoverMode);
  }

  protected RuntimeSessionModel createRuntimeSessionModel(String sessionId, Session session) throws CreateException {
    return new EJBRuntimeSessionModel(sessionId, this, session, failoverMode);
  }



}
