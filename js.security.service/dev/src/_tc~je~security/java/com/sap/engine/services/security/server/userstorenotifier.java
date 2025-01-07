package com.sap.engine.services.security.server;

import java.util.Vector;

import com.sap.engine.interfaces.security.userstore.listener.UserStoreListener;
import com.sap.engine.services.security.SecurityServerFrame;
import com.sap.engine.services.security.Util;
import com.sap.tc.logging.Severity;

class UserStoreNotifier implements Runnable {

  private static final String THREAD_NAME = "security:User-Store-Notifier:";

  public static final int REGISTER_OPERATION = 1;
  public static final int UNREGISTER_OPERATION = 2;
  public static final int ACTIVATE_OPERATION = 3;
 
  private String userstore = null;
  private int operation = 0;
  private UserStoreListener listener = null;
  
  public UserStoreNotifier(int operation, String userstore, UserStoreListener listener) {
    this.operation = operation;
    this.userstore = userstore;
    this.listener  = listener;
    SecurityServerFrame.threadContext.startThread(this, false);
  }
  
  public void run() {
    Thread thread = Thread.currentThread();
    String threadName = thread.getName();

    try {
      thread.setName(THREAD_NAME + threadName);

      switch(operation) {
        case REGISTER_OPERATION: {
          listener.userStoreRegistered(userstore);
          break;
        }
        case UNREGISTER_OPERATION: {
          listener.userStoreUnregistered(userstore);
          break;
        }
        case ACTIVATE_OPERATION: {
          listener.userStoreActivated(userstore);
          break;
        }
      }
    } catch (Exception e) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.INFO, "Unable to notify a listener [{0}] of user store change", new Object[]{listener}, e);
    } finally {
      thread.setName(threadName);
    }
  }
  
  public static void notify(Vector listeners, int operation, String userstore) {
    for (int i = 0; i < listeners.size(); i++) {
      try {
        new UserStoreNotifier(operation, userstore, (UserStoreListener) listeners.elementAt(i));
      } catch (Exception e) {
        Util.SEC_SRV_LOCATION.traceThrowableT(Severity.INFO, "Unable to notify a listener [{0}] of user store change", new Object[]{listeners.elementAt(i)}, e);
      }
    }
  }
}