package com.sap.engine.services.iiop.PortableServer.state;

import com.sap.engine.services.iiop.internal.giop.IncomingRequest;
import org.omg.CORBA.OBJ_ADAPTER;
import org.omg.CORBA.TRANSIENT;
import org.omg.PortableServer.POAManagerPackage.State;

public class InactiveState implements StateAction {

  public void preinvoke(IncomingRequest request) throws TRANSIENT, OBJ_ADAPTER {
  }

  public void postinvoke(IncomingRequest request) throws TRANSIENT, OBJ_ADAPTER {
  }

  public int value() {
    return State._INACTIVE;
  }
}
