package com.sap.engine.services.rmi_p4.server;

import com.sap.engine.interfaces.cross.ObjectReference;
import com.sap.engine.lib.lang.ConvertTools;
import com.sap.engine.services.rmi_p4.*;
import com.sap.engine.services.rmi_p4.exception.P4Logger;

/**
 * @author Georgy Stanev
 * @version 7.0
 */
public class P4ServerObjectInfo extends P4RemoteObjectInfo implements ObjectReference {

  static final long serialVersionUID = 1284708367544595829L;

  public static final ConvertTools ct = new ConvertTools(false);

  public Object toObject(ClassLoader loader) {
    Class stubClass = null;
    P4ObjectBroker broker = P4ObjectBroker.init();
    for (int i = 0; i < stubs.length; i++) {
      try {
        stubClass = Class.forName(stubs[i], true, loader);
        return broker.narrow(StubBaseInfo.makeStubBaseInfo(this), stubClass, null, loader);
      } catch (ClassNotFoundException cnf) {
        // $JL-EXC$ try with next
        continue;
      }
    }
    return null;
  }

  public Object toObject(ClassLoader loader, Object properties) {
    Class stubClass = null;
    P4ObjectBroker broker = P4ObjectBroker.init();
    //Assure that given classloader sees at least one of the remote interfaces.
    for (int i = 0; i < stubs.length; i++) {
      try {
        stubClass = Class.forName(stubs[i], true, loader);
        if (stubs[i].equals("com.sap.engine.interfaces.cross.RedirectableExt") || 
            stubs[i].equals("com.sap.engine.interfaces.cross.Redirectable")    ||
            stubs[i].equals("com.sap.engine.services.rmi_p4.interfaces.P4Notification") ){
          //Ignore remote interfaces that are always available as part of P4 or Cross.
          if (P4Logger.getLocation().beDebug()) {
            P4Logger.getLocation().debugT("P4ServerObjectInfo.toObject(ClassLoader, Object)", "Interface " + stubs[i] + " will not be counted as remote interface, if the stub implements only this interface it would not be raised. There should have no stub, which implements only this interface");
          }
          continue;
        }
        StubBaseInfo info = StubBaseInfo.makeStubBaseInfo(this);
        if (properties instanceof StubImpl){
          StubImpl base = (StubImpl) properties;
          String id = base.p4_getInfo().getIncomingProfile();
          if((id != null) && info.ownerId == Integer.parseInt(id.substring(0, id.indexOf(':')))){
             info.setIncomingProfile(base.p4_getInfo().getIncomingProfile());
          }
        } else {
          //ignore invalid properties
          if (P4Logger.getLocation().beDebug()) {
            P4Logger.getLocation().debugT("P4ServerObjectInfo.toObject(ClassLoader, Object)", "Not P4 stub received for making P4 Object: " + properties);
          }
        }
        return broker.narrow(info, stubClass, null, loader);
      } catch (ClassNotFoundException cnf) {
        //$JL-EXC$ try with next
        continue;
      }
    }
    return null;
  }

  public int hashCode() {
    if (key == null) {
        return super.hashCode();
    }
    int hash = ct.arrToInt(key, 0);
    return hash;
  }

  public boolean equals(Object obj) {
    if (obj instanceof P4ServerObjectInfo) {
      P4ServerObjectInfo p4Obj = (P4ServerObjectInfo) obj;

      if ((server_id == p4Obj.server_id) && (ct.arrToInt(key, 0) == ct.arrToInt(p4Obj.key, 0))) {
        return true;
      }
    }

    return false;
  }

  public static P4ServerObjectInfo cloneInfo(RemoteObjectInfo info) {
    P4ServerObjectInfo inf = new P4ServerObjectInfo();
    inf.connectionProfiles = info.connectionProfiles;
    inf.client_id = info.client_id;
    inf.server_id = info.server_id;
    inf.ownerId = info.ownerId;
    inf.key = info.key;
    inf.stubs = info.stubs;
    inf.isRedirectable = info.isRedirectable;
    inf.factoryName = info.getFactoryName();
    inf.objIdentity = info.getObjIdentity();
    inf.redirIdent = info.redirIdent;
    inf.setUrls(info.getUrls());
    inf.hosts = info.hosts;
    inf.server_classLoaderName = info.server_classLoaderName;
    inf.setOptimization(info.supportOptimization());
    return inf;
  }
}

