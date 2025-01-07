package com.sap.engine.services.iiop.PortableServer;

import org.omg.CORBA.LocalObject;
import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantActivator;
import org.omg.PortableServer.ServantLocator;
import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;

public class ServantDealer extends LocalObject implements ServantLocator,ServantActivator{

  public Servant preinvoke(byte[] oid,POA adapter,String operation,CookieHolder the_cookie)throws ForwardRequest{
    return null;
  }
  
  public void postinvoke(byte[] oid,POA adapter,String operation,java.lang.Object the_cookie,Servant the_servant){
    
  }
  public Servant incarnate(byte[] oid,POA adapter)throws ForwardRequest{
    return null;
  }
  public void etherealize(byte[] oid,POA adapter,Servant serv,boolean cleanup_in_progress,boolean remaining_activations){
  }
  
}