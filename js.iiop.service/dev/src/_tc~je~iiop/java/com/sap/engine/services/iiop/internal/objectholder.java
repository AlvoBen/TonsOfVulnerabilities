package com.sap.engine.services.iiop.internal;

import com.sap.engine.services.iiop.internal.giop.IncomingRequest;
import org.omg.CORBA.Object;

/**
 * Copyright: Copyright (c) 2003
 * Company: SAP AG - Sap Labs Bulgaria
 *
 * @author Ivan Atanassov
 * @version 7.1
 */
public class ObjectHolder implements TargetHolder {
  private Object object;

  public ObjectHolder(Object obj) {
    this.object = obj;
  }

  public void invoke(IncomingRequest request) throws Throwable {
    //nothing to invoke
  }

  public org.omg.CORBA.Object getObject() {
    return object;
  }
}
