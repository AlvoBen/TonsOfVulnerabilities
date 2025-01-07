package com.sap.engine.services.security.remote;

import java.rmi.*;
import java.util.Properties;

import com.sap.engine.interfaces.security.userstore.config.*;

public interface RemoteUserStore
  extends Remote {

  public UserStoreConfiguration getConfiguration() throws RemoteException;


  public RemoteGroupContext getGroupContext() throws RemoteException;


  public RemoteUserContext getUserContext() throws RemoteException;


  public boolean isActive() throws RemoteException;


  public void setActive(boolean active) throws RemoteException;


  public void setUserStoreProperties(Properties properties) throws RemoteException;

}

