package com.sap.engine.services.iiop.csiv2.interceptors;

import org.omg.CORBA.*;
import org.omg.IOP.Codec;
import com.sap.engine.services.iiop.server.CorbaServiceFrame;
import com.sap.engine.interfaces.security.SecurityContextObject;

public class SecurityClientRequestInterceptor extends ClientInterceptor {

  public SecurityClientRequestInterceptor(ORB orb, Codec codec) {
    super(orb, codec);
  }

  protected SecurityContextObject getCurrentSecurityContext() {
    return (SecurityContextObject) CorbaServiceFrame.getThreadSystem().getThreadContext().getContextObject("security");
  }
}

