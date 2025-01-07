
package com.sap.engine.services.security.server.storage;

import java.util.Hashtable;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationChangedListener;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationLockedException;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.services.security.exceptions.StorageLockedException;
import com.sap.engine.services.security.server.PolicyConfigurationLog;
import com.sap.tc.logging.Severity;

public class StartupStorage extends Storage {
  ConfigurationHandler handler = null;

  Hashtable readConfigs = null;

  Hashtable writeConfigs = null;

  boolean writeAllowed = false;

  public StartupStorage() {
    handler = HandlerPool.getFreeHandler();
    readConfigs = new Hashtable();
    writeConfigs = new Hashtable();
  }

  protected ConfigurationHandler getAppConfigurationHandler() {
    return handler;
  }
  
  public void globalBegin(boolean writeAllowed) {
    this.writeAllowed = writeAllowed;
  }

  public void engineBegin() {
    // ignores
  }

  public void engineCommit() throws ConfigurationException {
    // ignores
  }

  public void engineRollback() throws ConfigurationException {
    // ignores
  }

  public void engineForget() throws ConfigurationException {
    // ignores
  }

  public Configuration engineGetConfiguration(String name, boolean writeAccess, boolean createIfMissing) throws SecurityException {
    return recursiveGetConfiguration(name, writeAccess, createIfMissing);
  }

  public void registerConfigurationListener(ConfigurationChangedListener listener, String path) {
    if (PolicyConfigurationLog.location.bePath()) {
      PolicyConfigurationLog.location.logT(Severity.PATH, "Entering StartupStorage.registerConfigurationListener(ConfigurationChangedListener listener, String path)");
    }

    if (PolicyConfigurationLog.location.beDebug()) {
      PolicyConfigurationLog.location.logT(Severity.DEBUG, "Trying to register listener for configuration path [{0}]", new Object[] { path });
    }

    try {
      if (handler != null) {
        handler.addConfigurationChangedListener(listener, path);
      }
    } finally {
      if (PolicyConfigurationLog.location.bePath()) {
        PolicyConfigurationLog.location.logT(Severity.PATH, "Exiting StartupStorage.registerConfigurationListener(ConfigurationChangedListener listener, String path)");
      }
    }
  }

  public void globalCommit() throws ConfigurationException {
    if (PolicyConfigurationLog.location.bePath()) {
      PolicyConfigurationLog.location.logT(Severity.PATH, "Entering StartupStorage.globalCommit()");
    }
    try {
      Storage.global = false;
      handler.commit();
      handler.closeAllConfigurations();
      clear();
    } finally {
      if (PolicyConfigurationLog.location.bePath()) {
        PolicyConfigurationLog.location.logT(Severity.PATH, "Exiting StartupStorage.globalCommit()");
      }
    }
  }

  public void globalRollback() throws ConfigurationException {
    if (PolicyConfigurationLog.location.bePath()) {
      PolicyConfigurationLog.location.logT(Severity.PATH, "Entering StartupStorage.globalRollback()");
    }
    try {
      Storage.global = false;
      handler.rollback();
      handler.closeAllConfigurations();
      clear();
    } finally {
      if (PolicyConfigurationLog.location.bePath()) {
        PolicyConfigurationLog.location.logT(Severity.PATH, "Exiting StartupStorage.globalRollback()");
      }
    }
  }

  private final void clear() {
    Storage.startup = null;
    handler = null;
    readConfigs.clear();
    writeConfigs.clear();
  }

  private final synchronized Configuration recursiveGetConfiguration(String name, boolean writeAccess, boolean createIfMissing) throws SecurityException {
    Configuration result = (Configuration) writeConfigs.get(name);

    if (result != null) {
      return result;
    }

    result = (Configuration) readConfigs.get(name);
    if (result != null) {
      if (!writeAccess) {
        return result;
      } else {
        readConfigs.remove(name);

        try {
          result = openConfiguration(name, ConfigurationHandler.WRITE_ACCESS);
        } catch (ConfigurationException e) {
          PolicyConfigurationLog.location.traceThrowableT(Severity.WARNING, "Exception occurred during open of policy configuration [{0}].", new Object[] { name }, e);
        }

        if (result != null) {
          writeConfigs.put(name, result);
          return result;
        }
      }
    }

    int index = name.lastIndexOf('/');

    if (index < 0) {
      try {
        try {
          result = openConfiguration(name, (writeAccess && writeAllowed) ? ConfigurationHandler.WRITE_ACCESS : ConfigurationHandler.READ_ACCESS);
        } catch (NameNotFoundException nfe) {
          if (PolicyConfigurationLog.location.beDebug()) {
            PolicyConfigurationLog.location.logT(Severity.DEBUG, "Configuration missing. Will attempt to create it if requested.", new Object[] { name });
          }
          result = null;
        }
        if (result == null && createIfMissing && writeAllowed) {
          result = handler.createRootConfiguration(name);
          if (PolicyConfigurationLog.location.beDebug()) {
            PolicyConfigurationLog.location.logT(Severity.DEBUG, "Root configuration [{0}] created.", new Object[] { name });
          }
        }
      } catch (Exception e) {
        PolicyConfigurationLog.location.traceThrowableT(Severity.WARNING, "Exception occurred during open of configuration [{0}].", new Object[] { name }, e);
        if (e instanceof ConfigurationLockedException) {
          throw new StorageLockedException("Configuration lock on retrieving configuration [" + name + "]", (ConfigurationLockedException) e);
        }
        throw new SecurityException("Exception occurred on retrieving configuration [" + name + "]", e);
      }
    } else {
      boolean existing = false;
      result = recursiveGetConfiguration(name.substring(0, index), writeAccess, createIfMissing);
      if (result == null) {
        return null;
      }
      try {
        result = result.getSubConfiguration(name.substring(index + 1));
        existing = true;
      } catch (NameNotFoundException _) {
        existing = false;
      } catch (Exception e) {
        PolicyConfigurationLog.location.traceThrowableT(Severity.WARNING, "Exception occurred during open of sub configuration [{0}].", new Object[] { name
            .substring(index + 1) }, e);
        if (e instanceof ConfigurationLockedException) {
          throw new StorageLockedException("Configuration lock on retrieving configuration [" + name + "]", (ConfigurationLockedException) e);
        }
        throw new SecurityException("Exception occurred on retrieving configuration [" + name + "]", e);
      }

      if (!existing) {
        if (createIfMissing && writeAllowed) {
          try {
            String path = result.getPath();
            result = result.createSubConfiguration(name.substring(index + 1));
            if (PolicyConfigurationLog.location.beDebug()) {
              PolicyConfigurationLog.location.logT(Severity.DEBUG, "Sub configuration [{0}] created for configuration [{1}]", new Object[] { name.substring(index + 1), path });
            }
          } catch (Exception e) {
            PolicyConfigurationLog.location.traceThrowableT(Severity.WARNING, "Exception occurred during create of sub configuration [{0}].", new Object[] { name
                .substring(index + 1) }, e);
            if (e instanceof ConfigurationLockedException) {
              throw new StorageLockedException("Configuration lock on retrieving configuration [" + name + "]", (ConfigurationLockedException) e);
            }
            throw new SecurityException("Exception occurred on retrieving configuration [" + name + "]", e);
          }
        } else {
          return null;
        }
      }
    }

    if (result != null) {
      if (writeAccess && writeAllowed) {
        writeConfigs.put(name, result);
      } else {
        readConfigs.put(name, result);
      }
    }
    return result;
  }
}
