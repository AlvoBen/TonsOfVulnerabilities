
package com.sap.engine.services.security.server.storage;


import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationChangedListener;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationLockedException;
import com.sap.engine.services.deploy.container.AppConfigurationHandler;
import com.sap.engine.services.security.server.PolicyConfigurationLog;
import com.sap.engine.services.security.server.SecurityConfigurationPath;
import com.sap.tc.logging.Severity;

public abstract class Storage {
  
  public static final char SEPARATOR_SUBCONFIGURATION = '/';

  static boolean global = true;

  static StartupStorage startup = null;

  private static ThreadLocal<Configuration> locker = new ThreadLocal<Configuration>();

  protected abstract ConfigurationHandler getAppConfigurationHandler();

  public abstract void engineBegin();

  public abstract void engineCommit() throws ConfigurationException;

  public abstract void engineRollback() throws ConfigurationException;

  public abstract void engineForget() throws ConfigurationException;

  public abstract Configuration engineGetConfiguration(String name, boolean writeAccess, boolean createIfMissing) throws SecurityException;

  public abstract void registerConfigurationListener(ConfigurationChangedListener listener, String path);

  public final Configuration getConfiguration(String name, boolean writeAccess, boolean createIfMissing) throws SecurityException {
    if (PolicyConfigurationLog.location.beDebug()) {
      PolicyConfigurationLog.location.logT(Severity.DEBUG, "Trying to get configuration [{0}] from storage; write access [{1}]; create if missing [{2}]", new Object[] { name,
          new Boolean(writeAccess), new Boolean(createIfMissing) });
    }

    Configuration configuration = null;
    try {
      configuration = engineGetConfiguration(name, writeAccess, createIfMissing);      
    } finally {
      if (PolicyConfigurationLog.location.beDebug()) {
        PolicyConfigurationLog.location.logT(Severity.DEBUG, "Configuration returned from storage successfully [{0}].", new Object[] { configuration });
      }
    }

    return configuration;
  }
  
  protected boolean isLocked() {
    Configuration rootConfiguration = locker.get();
    if (rootConfiguration == null) {
      return false;
    } else {
      if (rootConfiguration.isValid()) {
        return true;
      } else {
        locker.remove();
        return false;
      }
    }
  }
  
  protected final Configuration openConfiguration(String name, int accessFlags) throws ConfigurationException {
    
    if (name.startsWith(SecurityConfigurationPath.SECURITY_PATH) && (accessFlags == ConfigurationHandler.WRITE_ACCESS)) {
      
      openRootConfiguration();
      
      if (name.equals(SecurityConfigurationPath.SECURITY_PATH)) {       
        if (PolicyConfigurationLog.location.beDebug()) {
          PolicyConfigurationLog.location.debugT("Root configuration already opened. Returning root configuration [{0}].", new Object[] {locker.get()});
        }
        return locker.get();
      } else {
        String subConfigurationName = name.substring(SecurityConfigurationPath.SECURITY_PATH.length() + 1); // + 1 for the '/'
        if (PolicyConfigurationLog.location.beDebug()) {
          PolicyConfigurationLog.location.debugT("Root configuration already opened. Getting subconfiguration [{0}]. Root = [{1}]", new Object[] {subConfigurationName, locker.get()});
        }
        Configuration config = locker.get().getSubConfiguration(subConfigurationName);
        return config;
      }
    
    } else {
      if (PolicyConfigurationLog.location.beDebug()) {
        PolicyConfigurationLog.location.debugT("Root configuration not opened. Opening new configuration [{0}].", new Object[] {name});
      }
      Configuration config = getAppConfigurationHandler().openConfiguration(name, accessFlags);
      return config;
    }
  }
  
  private final void openRootConfiguration() throws ConfigurationException {
    
    if (!isLocked()) {
      long waitLimit = 30 * 60 * 1000; // 30 min
      long waitInterval = 100;
      long beginTime = System.currentTimeMillis();
      ConfigurationLockedException exception = null;
      while (System.currentTimeMillis() <= beginTime + waitLimit) {
        try {
          Configuration rootConfiguration = getAppConfigurationHandler().openConfiguration(SecurityConfigurationPath.SECURITY_PATH, ConfigurationHandler.WRITE_ACCESS);
          locker.set(rootConfiguration);          
          if (PolicyConfigurationLog.location.beDebug()) {
            PolicyConfigurationLog.location.debugT("Root configuration [{0}] opened successfully.", new Object[] {SecurityConfigurationPath.SECURITY_PATH});
          }
          return ;
        } catch (ConfigurationLockedException e) {
          PolicyConfigurationLog.location.traceThrowableT(Severity.DEBUG, "Root security configuration locked.", e);
          exception = e;
          try {
            Thread.sleep(waitInterval);
          } catch (InterruptedException ex) {
            break;
          }
        }
      }
      throw exception;  
    }
  }

  public final void begin() {
    if (PolicyConfigurationLog.location.bePath()) {
      PolicyConfigurationLog.location.logT(Severity.PATH, "Entering Storage.begin()");
    }
    try {
      engineBegin();
    } finally {
      if (PolicyConfigurationLog.location.bePath()) {
        PolicyConfigurationLog.location.logT(Severity.PATH, "Exiting Storage.begin()");
      }
    }
  }

  public final void commit() throws ConfigurationException {
    if (PolicyConfigurationLog.location.bePath()) {
      PolicyConfigurationLog.location.logT(Severity.PATH, "Entering Storage.commit()");
    }
    try {
      engineCommit();
    } finally {
      if (PolicyConfigurationLog.location.bePath()) {
        PolicyConfigurationLog.location.logT(Severity.PATH, "Exiting Storage.commit()");
      }
    }
  }

  public final void rollback() throws ConfigurationException {
    if (PolicyConfigurationLog.location.bePath()) {
      PolicyConfigurationLog.location.logT(Severity.PATH, "Entering Storage.rollback()");
    }
    try {
      engineRollback();
    } finally {
      if (PolicyConfigurationLog.location.bePath()) {
        PolicyConfigurationLog.location.logT(Severity.PATH, "Exiting Storage.rollback()");
      }
    }
  }

  public final void forget() throws ConfigurationException {
    if (PolicyConfigurationLog.location.bePath()) {
      PolicyConfigurationLog.location.logT(Severity.PATH, "Entering Storage.forget()");
    }
    try {
      engineForget();
    } finally {
      if (PolicyConfigurationLog.location.bePath()) {
        PolicyConfigurationLog.location.logT(Severity.PATH, "Exiting Storage.forget()");
      }
    }
  }

  public static Storage getStorage(Configuration config) {
    if (PolicyConfigurationLog.location.bePath()) {
      PolicyConfigurationLog.location.logT(Severity.PATH, "Entering Storage.getStorage(Configuration config)");
    }

    if (PolicyConfigurationLog.location.beDebug()) {
      if (config != null) {
        PolicyConfigurationLog.location.logT(Severity.DEBUG, "Trying to get storage for configuration [{0}].", new Object[] { config.getPath() });
      }
    }

    Storage result = null;
    try {
      if (global) {
        if (startup == null) {
          startup = new StartupStorage();
        }

        result = startup;
      } else if (config != null) {
        result = new DeployStorage(config);
      } else {
        result = new AtomicStorage();
      }

      if (PolicyConfigurationLog.location.beDebug()) {
        PolicyConfigurationLog.location.logT(Severity.DEBUG, "New storage [{0}] created.", new Object[] { result });
      }

      return result;
    } finally {
      if (PolicyConfigurationLog.location.bePath()) {
        PolicyConfigurationLog.location.logT(Severity.PATH, "Exiting Storage.getStorage(Configuration config)");
      }
    }
  }

  public static Storage getStorage(AppConfigurationHandler configHandler, Configuration config) {
    if (PolicyConfigurationLog.location.bePath()) {
      PolicyConfigurationLog.location.logT(Severity.PATH, "Entering Storage.getStorage(AppConfigurationHandler configHandler, Configuration config)");
    }

    if (PolicyConfigurationLog.location.beDebug()) {
      if (config != null) {
        PolicyConfigurationLog.location.logT(Severity.DEBUG, "Trying to get storage for configuration [{0}] and configuration handler [{1}].", new Object[] { config.getPath(), configHandler });
      }
    }

    Storage result = null;
    try {
      if (global) {
        if (startup == null) {
          startup = new StartupStorage();
        }

        result = startup;
      } else if (config != null) {
        result = new DeployStorage(configHandler, config);
      } else {
        result = new AtomicStorage();
      }

      if (PolicyConfigurationLog.location.beDebug()) {
        PolicyConfigurationLog.location.logT(Severity.DEBUG, "New storage [{0}] created.", new Object[] { result });
      }

      return result;
    } finally {
      if (PolicyConfigurationLog.location.bePath()) {
        PolicyConfigurationLog.location.logT(Severity.PATH, "Exiting Storage.getStorage(AppConfigurationHandler configHandler, Configuration config)");
      }
    }
  }
}
