package com.sap.engine.services.iiop.PortableServer.state;

import com.sap.engine.services.iiop.PortableServer.util.RequestStack;
import com.sap.engine.services.iiop.internal.giop.IncomingRequest;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.OBJ_ADAPTER;
import org.omg.CORBA.TRANSIENT;
import org.omg.PortableServer.POAManagerPackage.State;

public class HoldingState implements StateAction {

  public void preinvoke(IncomingRequest request) throws TRANSIENT, OBJ_ADAPTER {
    if (RequestStack.holdingRequests() < 30){
      RequestStack.push(request);
    }else{
      throw new TRANSIENT("Queue Overflow",1,CompletionStatus.from_int(CompletionStatus._COMPLETED_YES));
    }
  }

  public void postinvoke(IncomingRequest request) throws TRANSIENT, OBJ_ADAPTER {
    if (RequestStack.holdingRequests() < 30){
      RequestStack.push(request);
    }else{
      throw new TRANSIENT("Queue Overflow",1,CompletionStatus.from_int(CompletionStatus._COMPLETED_YES));
    }
  }

  public int value() {
    return State._HOLDING;
  }
}
