package com.sap.engine.services.security.userstore;

import java.rmi.RemoteException;
import com.sap.engine.services.security.remote.*;
import com.sap.engine.services.security.SecurityServerFrame;
import com.sap.engine.interfaces.security.userstore.listener.UserStoreListener;
import com.sap.engine.interfaces.security.userstore.config.UserStoreConfiguration;
import com.sap.engine.interfaces.security.userstore.UserStoreFactory;
import com.sap.engine.interfaces.security.userstore.UserStore;
import com.sap.engine.interfaces.security.SecurityContext;

public class RemoteUserStoreFactoryImpl extends javax.rmi.PortableRemoteObject implements RemoteUserStoreFactory {

  private UserStoreFactory factory = null;

  public RemoteUserStoreFactoryImpl(UserStoreFactory factory) throws RemoteException {
    this.factory = factory;
  }

  /**
   *  Returns the active configuration.
   *
   * @return  the active configuration.
   */
  public RemoteUserStore getActiveUserStore() throws RemoteException {
    return new RemoteUserStoreImpl(factory.getActiveUserStore());
  }

  public void setActiveUserStore(String name) throws RemoteException {
    factory.setActiveUserStore(name);
  }

  /**
   *  Returns the configuration with the name.
   *
   * @param  name  the name of a registered configuration
   *
   * @return  the configuration with the given name or null if such does not exist.
   */
  public RemoteUserStore getUserStore(String name) throws RemoteException {
    if (factory.getUserStore(name) != null) {
      return new RemoteUserStoreImpl(factory.getUserStore(name));
    }
    return null;
  }

  /**
   *  Returns all registered configurations.
   *
   * @return  all configurations.
   */
  public RemoteUserStore[] listUserStores() throws RemoteException {
    UserStore[] stores = factory.listUserStores();
    RemoteUserStore[] remotes = new RemoteUserStore[stores.length];

    for (int i = 0; i < remotes.length; i++) {
      remotes[i] = new RemoteUserStoreImpl(stores[i]);
    }

    return remotes;
  }

  public void registerUserStore(UserStoreConfiguration config) throws RemoteException {
    factory.registerUserStore(config, this.getClass().getClassLoader());
  }

  public void updateUserStore(UserStoreConfiguration config) throws RemoteException {
    factory.updateUserStore(config, this.getClass().getClassLoader());
  }

  public void unregisterUserStore(String name) throws RemoteException {
    factory.unregisterUserStore(name);
  }

  public void registerListener(UserStoreListenerCallback listener) throws RemoteException {
    factory.registerListener(listener);
  }
  
  public void unregisterListener(UserStoreListenerCallback listener) throws RemoteException {
    factory.unregisterListener(listener);
  }

}

