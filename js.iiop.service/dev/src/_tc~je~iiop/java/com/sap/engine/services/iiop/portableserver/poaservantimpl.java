package com.sap.engine.services.iiop.PortableServer;

import com.sap.engine.services.iiop.internal.ClientORB;
import com.sap.engine.services.iiop.internal.giop.IncomingRequest;
import org.omg.PortableServer.Servant;

public class POAServantImpl{
  
  private Servant target;
  private ClientORB orb;
  
  public POAServantImpl(Servant t,ClientORB o){
    target = t;
    orb = o;
  }
  
  public void invoke(IncomingRequest ir){
    
  }
}