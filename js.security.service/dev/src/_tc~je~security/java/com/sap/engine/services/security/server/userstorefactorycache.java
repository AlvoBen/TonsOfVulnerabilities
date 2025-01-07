package com.sap.engine.services.security.server;

import java.util.Vector;

import com.sap.engine.interfaces.security.userstore.UserStore;
import com.sap.engine.interfaces.security.userstore.UserStoreFactory;
import com.sap.engine.interfaces.security.userstore.config.PolicyDescriptor;
import com.sap.engine.interfaces.security.userstore.config.UserStoreConfiguration;
import com.sap.engine.interfaces.security.userstore.listener.UserStoreListener;
import com.sap.engine.services.security.Util;
import com.sap.engine.services.security.userstore.emergency.EmergencyUserStoreConfiguration;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 *
 * @author  Jako Blagoev
 * @author  Ekaterina Zheleva
 * @version 6.30
 */
public class UserStoreFactoryCache implements UserStoreFactory {

  private int activeUserStore = -1;
  private UserStore[] stores = null;
  private UserStoreFactory owner = null;
  private Vector listeners = new Vector();
  

  public UserStoreFactoryCache() {
    // initializing emergency user store
    stores = new UserStore[1];
    UserStoreConfiguration userstoreConfig = new EmergencyUserStoreConfiguration();
    stores[0] = new UserStoreImpl(this, userstoreConfig, this.getClass().getClassLoader());
  }

  public void setOwner(UserStoreFactory owner) {
    this.owner = owner;
  }

  public UserStoreFactory getOwner() {
    return owner;
  }

  /**
   *  Returns the active configuration.
   *
   * @return  the active configuration.
   */
  public UserStore getActiveUserStore() {
    return stores[activeUserStore];
  }

  public void setActiveUserStore(String name) {
    for (int i = 0; i < stores.length; i++) {
      if (stores[i].getConfiguration().getName().equals(name)) {
        activeUserStore = i;
        ((UserStoreImpl) stores[i]).setActiveInternal(true);
        Util.SEC_SRV_LOCATION.infoT("Userstore {0} is set as active.",  new Object[]{name});
        UserStoreNotifier.notify(listeners, UserStoreNotifier.ACTIVATE_OPERATION, name);
        break;
      }
    }
  }

  public void setActiveUserStore(String name, PolicyDescriptor policy) {
  }

  /**
   *  Returns the configuration with the name.
   *
   * @param  name  the name of a registered configuration
   *
   * @return  the configuration with the given name or null if such does not exist.
   */
  public UserStore getUserStore(String name) {
    for (int i = 0; i < stores.length; i++) {
      if (stores[i].getConfiguration().getName().equals(name)) {
        UserStore store = stores[i];
        ((UserStoreImpl) stores[i]).setUserStoreTransactionAttribute();
        return store;
      }
    }

    return null;
  }

  /**
   *  Returns all registered configurations.
   *
   * @return  all configurations.
   */
  public UserStore[] listUserStores() {
    return stores;
  }

  public synchronized void registerUserStore(UserStoreConfiguration config, ClassLoader classLoader) {
    UserStore[] temp = new UserStore[stores.length + 1];
    for (int i = 0; i < stores.length; i++) {
      if (!stores[i].getConfiguration().getName().equals(config.getName())) {
        temp[i] = stores[i];
      } else {
        java.util.HashSet presistntStores = ((UserStoreFactoryImpl) owner).listPersistentUserStores();
        if (!presistntStores.contains(config.getName())) {
          ((UserStoreImpl) stores[i]).unregisterUserStoreListener();
          stores[i] = new UserStoreImpl(this, config, classLoader);
          SimpleLogger.log(Severity.WARNING, Util.SEC_SRV_CATEGORY, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000070", "Cannot register user store {0}: such name already exists.", new Object[]{config.getName()});
          UserStoreNotifier.notify(listeners, UserStoreNotifier.REGISTER_OPERATION, config.getName());
          return;
        }
        throw new SecurityException("User Store with such name already exists.");
      }
    }
    temp[stores.length] = new UserStoreImpl(this, config, classLoader);
    stores = temp;
    Util.SEC_SRV_LOCATION.infoT("Userstore {0} registered successfully.", new Object[]{config.getName()});
    UserStoreNotifier.notify(listeners, UserStoreNotifier.REGISTER_OPERATION, config.getName());

    //if (activeUserStore < 0) {
    //  activeUserStore = stores.length - 1;//owner.setActiveUserStore(stores[stores.length - 1].getConfiguration().getName());
    //}

  }

  public synchronized void registerUserStore(UserStoreConfiguration config, String classLoaderName) {
    UserStore[] temp = new UserStore[stores.length + 1];
    for (int i = 0; i < stores.length; i++) {
      if (!stores[i].getConfiguration().getName().equals(config.getName())) {
        temp[i] = stores[i];
      } else {
        SimpleLogger.log(Severity.WARNING, Util.SEC_SRV_CATEGORY, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000070", "Cannot register user store {0}: such name already exists.", new Object[]{config.getName()});
        throw new SecurityException("User Store with such name already exists.");
      }
    }
    temp[stores.length] = new UserStoreImpl(this, config, classLoaderName);
    stores = temp;
    Util.SEC_SRV_LOCATION.infoT("Userstore {0} registered successfully.", new Object[]{config.getName()});
    UserStoreNotifier.notify(listeners, UserStoreNotifier.REGISTER_OPERATION, config.getName());
  }

  public synchronized void updateUserStore(UserStoreConfiguration config, ClassLoader classLoader) {
    if (config.getName().equals(EmergencyUserStoreConfiguration.NAME)) {
      throw new SecurityException("Cannot update the emergency user store!");
    }

    for (int i = 0; i < stores.length; i++) {
      if (stores[i].getConfiguration().getName().equals(config.getName())) {
        ((UserStoreImpl) stores[i]).update(config);
        return;
      }
    }
    throw new SecurityException("User Store does not exists.");
  }

  public synchronized void renameUserStore(String name, String newName) {

  }

  public synchronized void unregisterUserStore(String name) {
    if (name.equals(EmergencyUserStoreConfiguration.NAME)) {
      throw new SecurityException("Cannot unregister the emergency user store!");
    }

    int index = -1;
    for (int i = 0; i < stores.length; i++) {
      if (stores[i].getConfiguration().getName().equals(name)) {
        index = i;
        break;
      }
    }

    if (index >= 0) {
      UserStore[] temp = new UserStore[stores.length - 1];
      System.arraycopy(stores, 0, temp, 0, index);

      if (index + 1 < stores.length) {
        System.arraycopy(stores, index + 1, temp, index, temp.length - index);
        if (index < activeUserStore) {
          activeUserStore--;
        }
      }
      SimpleLogger.log(Severity.INFO, Util.SEC_SRV_CATEGORY, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000071", "User store {0} unregistered successfully.", new Object[]{name});
      ((UserStoreImpl) stores[index]).unregisterUserStoreListener();
      stores = temp;
      UserStoreNotifier.notify(listeners, UserStoreNotifier.UNREGISTER_OPERATION, name);
    }
  }

  public void registerListener(UserStoreListener listener) {
    if (!listeners.contains(listener)) {
      listeners.addElement(listener);
    }
  }

  public void unregisterListener(UserStoreListener listener) {
    listeners.remove(listener);
  }

  //////////////////////////////////////////////////////////////////
  // This patch is needed only in case of internal DBMS userstore //
  //////////////////////////////////////////////////////////////////
  private boolean equalConnectorUserStoreConfigurations(UserStoreConfiguration config1, UserStoreConfiguration config2) {
    String userstoreName1 = config1.getUserStoreProperties().getProperty("CONNECTOR");
    String userstoreName2 = config2.getUserStoreProperties().getProperty("CONNECTOR");
    return userstoreName1 != null && userstoreName2 != null && userstoreName1.equals(userstoreName2);
  }
  //////////////////////////////////////////////////////////////////

}