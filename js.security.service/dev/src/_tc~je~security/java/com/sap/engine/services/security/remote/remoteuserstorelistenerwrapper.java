package com.sap.engine.services.security.remote;

import com.sap.engine.interfaces.security.userstore.listener.UserStoreListener;
import com.sap.engine.services.security.remote.UserStoreListenerCallback;
import com.sap.engine.services.rmi_p4.P4RemoteObject;

public class RemoteUserStoreListenerWrapper extends P4RemoteObject implements UserStoreListenerCallback {
  private UserStoreListener listener;
  
  public RemoteUserStoreListenerWrapper(UserStoreListener listener) {
    this.listener = listener;
  }
  
  public void userStoreRegistered(String userstore) throws SecurityException {
    listener.userStoreRegistered(userstore);
  }


  public void userStoreUnregistered(String userstore) throws SecurityException {
    listener.userStoreUnregistered(userstore);
  }


  public void userStoreActivated(String userstore) throws SecurityException {
    listener.userStoreActivated(userstore);
  }
  
}