package com.sap.engine.services.security.userstore;

import java.rmi.RemoteException;
import java.util.Properties;

import com.sap.engine.services.security.remote.*;
import com.sap.engine.interfaces.security.userstore.UserStore;
import com.sap.engine.interfaces.security.userstore.config.UserStoreConfiguration;

public class RemoteUserStoreImpl extends javax.rmi.PortableRemoteObject implements RemoteUserStore {

  private UserStore store = null;

  public RemoteUserStoreImpl(UserStore store) throws RemoteException {
    this.store = store;
  }

  public UserStoreConfiguration getConfiguration() throws RemoteException {
    return store.getConfiguration();
  }

  public RemoteGroupContext getGroupContext() throws RemoteException {
    if (store.getGroupContext() != null) {
      return new RemoteGroupContextImpl(store.getGroupContext());
    }
    return null;
  }

  public RemoteUserContext getUserContext() throws RemoteException {
    if (store.getUserContext() != null) {
      return new RemoteUserContextImpl(store.getUserContext());
    }
    return null;
  }

  public boolean isActive() throws RemoteException {
    return store.isActive();
  }

  public void setActive(boolean active) throws RemoteException {
    store.setActive(active);
  }

  public void setUserStoreProperties(Properties properties) throws RemoteException {
    UserStoreConfiguration configuration = store.getConfiguration();

    configuration.getUserStoreProperties().putAll(properties);
    store.getFactory().updateUserStore(configuration, store.getClass().getClassLoader());
  }

}

