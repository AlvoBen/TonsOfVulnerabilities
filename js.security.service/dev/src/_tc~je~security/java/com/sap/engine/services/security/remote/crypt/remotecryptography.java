package com.sap.engine.services.security.remote.crypt;

import java.rmi.*;
import java.util.Vector;

public interface RemoteCryptography extends Remote {

  public Vector getInstalledProviders() throws RemoteException;


  public String getProviderInfo(String provider) throws RemoteException;


  public void setInstalledProviders(Vector providers) throws RemoteException;

}