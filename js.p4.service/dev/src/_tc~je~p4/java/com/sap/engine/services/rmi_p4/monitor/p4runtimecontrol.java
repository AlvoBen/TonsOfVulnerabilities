/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.services.rmi_p4.monitor;

import com.sap.engine.frame.state.ManagementListener;
import com.sap.engine.services.rmi_p4.P4ObjectBroker;
import com.sap.engine.services.rmi_p4.P4RemoteObject;
import com.sap.engine.services.rmi_p4.server.P4ObjectBrokerServerImpl;
import com.sap.engine.services.rmi_p4.server.P4MessageProcessor;
import com.sap.engine.services.rmi_p4.server.P4ServiceFrame;

import java.rmi.RemoteException;

/**
 * @author Nikolai Neychev, Mladen Droshev
 * @version 7.0
 */
public class P4RuntimeControl extends P4RemoteObject implements P4RuntimeControlInterface {

  private ManagementListener ml = null;

  public void registerManagementListener(ManagementListener managementListener) {
    ml = managementListener;
  }

  public int getExportedRemoteObjectsCount() {
    return P4ObjectBroker.init().objManager.getObjectCount();
  }

  public int getP4ThreadUsageRate() {
    P4ObjectBroker broker = P4ObjectBroker.init();
    if (broker.isServerBroker()) {
      P4MessageProcessor processor = ((P4ObjectBrokerServerImpl) broker).sessionProcessor.getMessageProcessor();
      int i = ((processor.busyThreads) * 100) / processor.threads;
      if (i > 100) { //+the cross threads ignore
          return 100;
      } else {
          return i;
      }
    } else {
      return 0;
    }
  }

  public long getRequestCount() throws RemoteException {
    P4ObjectBroker broker = P4ObjectBroker.init();
    if (broker.isServerBroker()) {
      return ((P4ObjectBrokerServerImpl) broker).sessionProcessor.getRequestCount();
    } else {
      return 0;
    }
  }

  public long getErrorRequestCount() throws RemoteException {
    P4ObjectBroker broker = P4ObjectBroker.init();
    if (broker.isServerBroker()) {
      return ((P4ObjectBrokerServerImpl) broker).sessionProcessor.getErrorRequestCount();
    } else {
      return 0;
    }
  }

}
