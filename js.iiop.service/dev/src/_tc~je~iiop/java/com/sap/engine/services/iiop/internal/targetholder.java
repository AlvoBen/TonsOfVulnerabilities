package com.sap.engine.services.iiop.internal;

import com.sap.engine.services.iiop.internal.giop.IncomingRequest;

public interface TargetHolder {

  public void invoke(IncomingRequest request) throws Throwable;

  public org.omg.CORBA.Object getObject();

}
