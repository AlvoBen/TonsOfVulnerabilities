package com.sap.engine.services.security.remote;

import java.rmi.*;
import com.sap.engine.interfaces.security.userstore.config.UserStoreConfiguration;
import com.sap.engine.interfaces.security.userstore.listener.UserStoreListener;

public interface RemoteUserStoreFactory extends Remote {

  /**
   *  Returns the active configuration.
   *
   * @return  the active configuration.
   */
  public RemoteUserStore getActiveUserStore() throws RemoteException;


  public void setActiveUserStore(String name) throws RemoteException;


  /**
   *  Returns the configuration with the name.
   *
   * @param  name  the name of a registered configuration
   *
   * @return  the configuration with the given name or null if such does not exist.
   */
  public RemoteUserStore getUserStore(String name) throws RemoteException;


  /**
   *  Returns all registered configurations.
   *
   * @return  all configurations.
   */
  public RemoteUserStore[] listUserStores() throws RemoteException;

  public void registerUserStore(UserStoreConfiguration config) throws RemoteException;

  public void updateUserStore(UserStoreConfiguration config) throws RemoteException;

  public void unregisterUserStore(String name) throws RemoteException;
 
  public void registerListener(UserStoreListenerCallback listener) throws RemoteException;
  
  public void unregisterListener(UserStoreListenerCallback listener) throws RemoteException; 
}

