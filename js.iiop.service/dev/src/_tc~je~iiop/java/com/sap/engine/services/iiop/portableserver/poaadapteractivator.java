package com.sap.engine.services.iiop.PortableServer;

import org.omg.PortableServer.AdapterActivator;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAPackage.AdapterAlreadyExists;
import org.omg.PortableServer.POAPackage.InvalidPolicy;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;

public class POAAdapterActivator extends POAImpl implements AdapterActivator {
  

  
  public POAAdapterActivator(){
    super(null);//??
 
  }
  
  public boolean unknown_adapter(POA parent,String name) {
    POAManagerImpl manager = new POAManagerImpl(name);
    try {
      POA p = parent.create_POA(name,manager,null);
      manager.addPOA(p);
      return true;
    } catch(AdapterAlreadyExists aae) {
      LoggerConfigurator.getLocation().debugT("POAAdapterActivator.unknown_adapter(org.omg.PortableServer.POA, String)", LoggerConfigurator.exceptionTrace(aae));
      return false;
    } catch(InvalidPolicy ip) {
      LoggerConfigurator.getLocation().debugT("POAAdapterActivator.unknown_adapter(org.omg.PortableServer.POA, String)", LoggerConfigurator.exceptionTrace(ip));
      return false;
    }
  }
}