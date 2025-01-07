package com.sap.engine.services.security.userstore.persistent;

import com.sap.engine.frame.core.configuration.ChangeEvent;
import com.sap.engine.frame.core.configuration.ConfigurationChangedListener;
import com.sap.engine.interfaces.security.userstore.config.UserStoreConfiguration;
import com.sap.engine.services.security.SecurityServerFrame;
import com.sap.engine.services.security.server.SecurityConfigurationPath;
import com.sap.engine.services.security.server.UserStoreFactoryCache;
import com.sap.engine.services.security.userstore.descriptor.UserStoreConfigurationImpl;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * Handles the change events in the userstores persistent storage.
 *
 * @version 6.30
 * @author  Ekaterina Zheleva
 */
public class UserStoreChangedConfigurationListener implements ConfigurationChangedListener {

  private static final Location LOCATION = Location.getLocation(UserStoreChangedConfigurationListener.class);
  
  private UserStoreFactoryCache cache = null;
  private UserStorePersistent persistentStorage = null;
  
  public UserStoreChangedConfigurationListener(UserStoreFactoryCache cache) {
    this.cache = cache;
  }

  public void setPersistentStorage(UserStorePersistent persistent) {
    this.persistentStorage = persistent;
  }

 /**
  * @see  com.sap.engine.frame.core.configuration.ConfigurationChangedListener#configurationChanged(ChangeEvent e)
  */
  public void configurationChanged(ChangeEvent e) {
    ChangeEvent[] events = e.getDetailedChangeEvents();
    if (events[0].getPath().equals(SecurityConfigurationPath.USERSTORES_PATH) && events[0].getAction() == ChangeEvent.ACTION_MODIFIED) {
      if (events.length == 1) {
        try {
          cache.setActiveUserStore(persistentStorage.getActiveUserStore());
        } catch (SecurityException se) {
          LOCATION.traceThrowableT(Severity.WARNING, "Active user store configuration change failed.", se);
        }
      } else if (events[1].getAction() == ChangeEvent.ACTION_CREATED) {
        String path = events[1].getPath();
        if (path.startsWith(SecurityConfigurationPath.USERSTORES_PATH + "/")) {
          String newUserStoreName = path.substring(path.lastIndexOf("/") + 1);
          UserStoreConfiguration userStoreConfiguration = persistentStorage.loadUserStoreConfiguration(newUserStoreName);
          String loaderName = ((UserStoreConfigurationImpl) userStoreConfiguration).getClassLoaderName();
          try {
            cache.registerUserStore(userStoreConfiguration, loaderName);
          } catch (SecurityException se) {
            LOCATION.traceThrowableT(Severity.WARNING, "Active user store configuration change failed.", se);
          }
        }
      } else if (events[1].getAction() == ChangeEvent.ACTION_DELETED) {
        String path = events[1].getPath();
        if (path.startsWith(SecurityConfigurationPath.USERSTORES_PATH + "/")) {
          String userStoreName = path.substring(path.lastIndexOf("/") + 1);
          try {
            cache.unregisterUserStore(userStoreName);
          } catch (SecurityException se) {
            LOCATION.traceThrowableT(Severity.WARNING, "Active user store configuration change failed.", se);
          }
        }
      }
    } else if (events[0].getPath().startsWith(SecurityConfigurationPath.USERSTORES_PATH + "/")) {
      String path = events[0].getPath();
      byte userstoreNameBegin = (byte) (SecurityConfigurationPath.USERSTORES_PATH.length() + 1);
      byte userstoreNameEnd = (byte) path.indexOf("/", userstoreNameBegin);
      String userstoreName = (userstoreNameEnd > userstoreNameBegin) ? path.substring(userstoreNameBegin, userstoreNameEnd) : path.substring(userstoreNameBegin);
      UserStoreConfiguration userStoreConfiguration = persistentStorage.loadUserStoreConfiguration(userstoreName);
      ClassLoader loader = null;
      String loaderName = ((UserStoreConfigurationImpl) userStoreConfiguration).getClassLoaderName();
      if (loaderName != null && !loaderName.equals("")) {
        loader = SecurityServerFrame.frame.getServiceContext().getCoreContext().getLoadContext().getClassLoader(loaderName);
      } else {
        loader = this.getClass().getClassLoader();
      }
      try {
        cache.updateUserStore(userStoreConfiguration, loader);
      } catch (SecurityException se) {
        LOCATION.traceThrowableT(Severity.WARNING, "Active user store configuration change failed.", se);
      }
    }
  }

}