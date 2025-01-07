/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.services.rmi_p4.monitor;

import com.sap.engine.frame.state.ManagementInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Nikolai Neychev, Mladen Droshev
 * @version 7.0
 */
public interface P4RuntimeControlInterface extends ManagementInterface, Remote {

  
  public int getExportedRemoteObjectsCount();

  public int getP4ThreadUsageRate();

  public long getRequestCount() throws RemoteException;

  public long getErrorRequestCount() throws RemoteException;

}
