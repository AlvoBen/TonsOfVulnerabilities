
package com.sap.engine.services.security.server.storage;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationChangedListener;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationLockedException;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.services.deploy.container.AppConfigurationHandler;
import com.sap.engine.services.security.exceptions.StorageLockedException;
import com.sap.engine.services.security.server.PolicyConfigurationLog;
import com.sap.engine.services.security.server.SecurityConfigurationPath;
import com.sap.tc.logging.Severity;

public class DeployStorage extends Storage {
  private Configuration config = null;

  private ConfigurationHandler tempHandler = null;

  private AppConfigurationHandler appConfigHandler = null;

  private SecurityConfigurationsThreadContext sctContext = null;

  public DeployStorage( Configuration config) {
    this.config = config;
  }

  protected ConfigurationHandler getAppConfigurationHandler() {
    if (appConfigHandler != null) {
      return appConfigHandler;
    } else {
      if (tempHandler != null) { 
      	return tempHandler;
      } else {
      	tempHandler = HandlerPool.getFreeHandler();
      	return tempHandler;
      }
    }
  }
  
  public DeployStorage( AppConfigurationHandler configHandler, Configuration config) {
    this.config = config;
    this.appConfigHandler = configHandler;
    if (configHandler != null) {
      sctContext = SecurityConfigurationsThreadContext.getContext();
      sctContext.setConfigurationHandler(configHandler);
      sctContext.setStorage(this);
    }
  }

  public void engineBegin() {
    // ignores
  }

  public void engineCommit() throws ConfigurationException {
    if (tempHandler != null) {
      tempHandler.commit();
      clearTempHandler();
    }
  }

  public void engineRollback() throws ConfigurationException {
    if (tempHandler != null) {
      tempHandler.rollback();
      clearTempHandler();
    }
  }

  public void engineForget() throws ConfigurationException {
    if (tempHandler != null && !isLocked()) {
      clearTempHandler();
    }
  }

  private void clearTempHandler() throws ConfigurationException {
    tempHandler.closeAllConfigurations();
    HandlerPool.freeHandler(tempHandler);
    tempHandler = null;
  }

  public Configuration engineGetConfiguration(String name, boolean writeAccess, boolean createIfMissing) throws SecurityException {
    if (name.startsWith(SecurityConfigurationPath.SECURITY_CONFIGURATIONS_PATH) || name.startsWith(SecurityConfigurationPath.USERSTORES_PATH)) {
      if (sctContext != null) {
        return sctContext.getConfiguration(name, writeAccess, createIfMissing);
      } else {
        return recursiveGetConfigurationFromHandler(name, getAppConfigurationHandler(), writeAccess, createIfMissing);
      }
    }

    if (name.startsWith(config.getPath())) {
      name = name.substring(config.getPath().length());
    } else {
      if (PolicyConfigurationLog.location.beDebug()) {
        PolicyConfigurationLog.location.logT(Severity.WARNING, "Configuration name [{0}] does not start with [{1}].", new Object[] { name, config.getPath() });
      }
    }

    if (name.length() == 0) {
      return config;
    } else {
      // the rest of the config path must not starts with "/"
      if (name.charAt(0) == Storage.SEPARATOR_SUBCONFIGURATION) {
        name = name.substring(1);
      } 
    }

    return recursiveGetConfiguration(name, writeAccess, createIfMissing);
  }

  public void registerConfigurationListener(ConfigurationChangedListener listener, String path) {
    if (PolicyConfigurationLog.location.bePath()) {
      PolicyConfigurationLog.location.logT(Severity.PATH, "Entering DeployStorage.registerConfigurationListener(ConfigurationChangedListener listener, String path)");
    }

    if (PolicyConfigurationLog.location.beDebug()) {
      PolicyConfigurationLog.location.logT(Severity.DEBUG, "Trying to register listener for configuration path [{0}]", new Object[] { path });
    }

    try {
      ConfigurationHandler handler = null;
      if (appConfigHandler != null) {
        handler = appConfigHandler;
      } else {
        handler = tempHandler;
      }

      if (handler != null) {
        handler.addConfigurationChangedListener(listener, path);
      }
    } finally {
      if (PolicyConfigurationLog.location.bePath()) {
        PolicyConfigurationLog.location.logT(Severity.PATH, "Exiting DeployStorage.registerConfigurationListener(ConfigurationChangedListener listener, String path)");
      }
    }
  }

  private final Configuration recursiveGetConfiguration(String name, boolean writeAccess, boolean createIfMissing) throws SecurityException {
    Configuration result = null;
    try {
      result = config.getSubConfiguration(name);
    } catch (NameNotFoundException _) {
      if (!createIfMissing) {
        if (PolicyConfigurationLog.location.beDebug()) {
          PolicyConfigurationLog.location.logT(Severity.DEBUG, "NameNotFoundException occurred during get of configuration [{0}].", new Object[] { name });
        }

        return null;
      }
    } catch (Exception e) {
      PolicyConfigurationLog.location.traceThrowableT(Severity.WARNING, "Exception occurred during open of sub configuration [{0}].", new Object[] { name }, e);
      if (e instanceof ConfigurationLockedException) {
        throw new StorageLockedException("Configuration lock on retrieving configuration [" + name + "]", (ConfigurationLockedException) e);
      }
      throw new SecurityException("Exception occurred on retrieving configuration [" + name + "]", e);
    }

    if ((result == null) && (createIfMissing)) {
      int index = name.lastIndexOf('/');

      if (index < 0) {
        if (name.trim().length() == 0) {
          return config;
        } else {
          try {
            String path = config.getPath();
            result = config.createSubConfiguration(name);
            if (PolicyConfigurationLog.location.beDebug()) {
              PolicyConfigurationLog.location.logT(Severity.DEBUG, "Sub configuration [{0}] created for configuration [{1}]", new Object[] { name, path });
            }
          } catch (Exception e) {
            PolicyConfigurationLog.location.traceThrowableT(Severity.WARNING, "Exception occurred during create of sub configuration [{0}].", new Object[] { name }, e);
            if (e instanceof ConfigurationLockedException) {
              throw new StorageLockedException("Configuration lock on retrieving configuration [" + name + "]", (ConfigurationLockedException) e);
            }
            throw new SecurityException("Exception occurred on retrieving configuration [" + name + "]", e);
          }
        }
      } else {
        boolean existing = false;
        result = recursiveGetConfiguration(name.substring(0, index), true, true);
        try {
          result = result.getSubConfiguration(name.substring(index + 1));
          existing = true;
        } catch (Exception e) {
          if (e instanceof ConfigurationLockedException) {
            PolicyConfigurationLog.location.traceThrowableT(Severity.WARNING, "ConfigurationLockedException occurred during open of sub configuration [{0}].",
                new Object[] { name.substring(index + 1) }, e);
            throw new StorageLockedException("Configuration lock on retrieving configuration [" + name + "]", (ConfigurationLockedException) e);
          }
        }

        if (!existing) {
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
        }
      }
    }

    return result;
  }

  private final Configuration recursiveGetConfigurationFromHandler(String name, ConfigurationHandler handler, boolean writeAccess, boolean createIfMissing) throws SecurityException {
    Configuration result = null;

    try {
      result = openConfiguration(name, writeAccess ? ConfigurationHandler.WRITE_ACCESS : ConfigurationHandler.READ_ACCESS);
    } catch (NameNotFoundException nnfe) {
      if (!createIfMissing) {
        if (PolicyConfigurationLog.location.beDebug()) {
          PolicyConfigurationLog.location.logT(Severity.DEBUG, "NameNotFoundException occurred during get of configuration [{0}] from handler [{1}].", new Object[] { name, handler });
        }
        return null;
      }
    } catch (Exception e) {
      PolicyConfigurationLog.location.traceThrowableT(Severity.WARNING, "Exception occurred during open of sub configuration [{0}] from handler [{1}].", new Object[] {
          name, handler }, e);
      if (e instanceof ConfigurationLockedException) {
        throw new StorageLockedException("Configuration lock on retrieving configuration [" + name + "]", (ConfigurationLockedException) e);
      }
      throw new SecurityException("Exception occurred on retrieving configuration [" + name + "]", e);
    }

    if ((result == null) && (createIfMissing)) {
      int index = name.lastIndexOf('/');

      if (index < 0) {
        try {
          result = handler.createRootConfiguration(name);
          if (PolicyConfigurationLog.location.beDebug()) {
            PolicyConfigurationLog.location.logT(Severity.DEBUG, "Root configuration [{0}] created from configuration handler [{1}]", new Object[] { name, handler });
          }
        } catch (Exception e) {
          PolicyConfigurationLog.location.traceThrowableT(Severity.WARNING, "Exception occurred during create of root configuration [{0}] from handler [{1}].",
              new Object[] { name, handler }, e);
          if (e instanceof ConfigurationLockedException) {
            throw new StorageLockedException("Configuration lock on retrieving configuration [" + name + "]", (ConfigurationLockedException) e);
          }
          throw new SecurityException("Exception occurred on retrieving configuration [" + name + "]", e);
        }
      } else {
        boolean existing = false;
        result = recursiveGetConfigurationFromHandler(name.substring(0, index), handler, true, true);
        try {
          result = result.getSubConfiguration(name.substring(index + 1));
          existing = true;
        } catch (Exception e) {
          if (e instanceof ConfigurationLockedException) {
            PolicyConfigurationLog.location.traceThrowableT(Severity.WARNING, "ConfigurationLockedException occurred during open of sub configuration [{0}].",
                new Object[] { name.substring(index + 1) }, e);
            throw new StorageLockedException("Configuration lock on retrieving configuration [" + name + "]", (ConfigurationLockedException) e);
          }
        }

        if (!existing) {
          try {
            String path = result.getPath();
            result = result.createSubConfiguration(name.substring(index + 1));
            if (PolicyConfigurationLog.location.beDebug()) {
              PolicyConfigurationLog.location.logT(Severity.DEBUG, "Sub configuration [{0}] created for configuration [{1}] from configuration handler [{2}].", new Object[] {
                  name.substring(index + 1), path, handler });
            }
          } catch (Exception e) {
            PolicyConfigurationLog.location.traceThrowableT(Severity.WARNING, "Exception occurred during create of sub configuration [{0}] from handler [{1}].",
                new Object[] { name, handler }, e);
            if (e instanceof ConfigurationLockedException) {
              throw new StorageLockedException("Configuration lock on retrieving configuration [" + name + "]", (ConfigurationLockedException) e);
            }
            throw new SecurityException("Exception occurred on retrieving configuration [" + name + "]", e);
          }
        }
      }
    }

    return result;
  }

}
