
package com.sap.engine.services.security.server.storage;


import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationChangedListener;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationLockedException;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.services.security.exceptions.StorageLockedException;
import com.sap.engine.services.security.server.PolicyConfigurationLog;
import com.sap.tc.logging.Severity;

/* 
 * AtomicStorage class is not thread-safe.
 */

public class AtomicStorage extends Storage {
  
  ConfigurationHandler handler = null;
  
  int modificationSemaphore = 0;

  public AtomicStorage() {
  }

  protected ConfigurationHandler getAppConfigurationHandler() {
    return handler;
  }
  
  public void engineBegin() {
    if (handler == null) {
      handler = (ConfigurationHandler) HandlerPool.getFreeHandler();
    }
    modificationSemaphore++;
  }

  public void engineCommit() throws ConfigurationException {
    if (handler != null) {
      handler.commit();
      clearHandler();
      modificationSemaphore--;
    }    
  }

  public void engineRollback() throws ConfigurationException {
    if (handler != null) {
      handler.rollback();
      clearHandler();
      modificationSemaphore--;
    }
  }

  public void engineForget() throws ConfigurationException {
    if (handler != null) {
      if ( modificationSemaphore == 1) {
        clearHandler();
      }
      modificationSemaphore--;
    }
  }

  private void clearHandler() throws ConfigurationException {
    handler.closeAllConfigurations();
    HandlerPool.freeHandler(handler);
    handler = null;
  }

  public Configuration engineGetConfiguration(String name, boolean writeAccess, boolean createIfMissing) throws SecurityException {
    return recursiveGetConfiguration(name, writeAccess, createIfMissing);
  }

  public void registerConfigurationListener(ConfigurationChangedListener listener, String path) {
    if (PolicyConfigurationLog.location.bePath()) {
      PolicyConfigurationLog.location.logT(Severity.PATH, "Entering AtomicStorage.registerConfigurationListener(ConfigurationChangedListener listener, String path)");
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
        PolicyConfigurationLog.location.logT(Severity.PATH, "Exiting AtomicStorage.registerConfigurationListener(ConfigurationChangedListener listener, String path)");
      }

    }
  }

  private final Configuration recursiveGetConfiguration(String name, boolean writeAccess, boolean createIfMissing) throws SecurityException {
    Configuration result = null;

    try {
      result = openConfiguration(name, writeAccess ? ConfigurationHandler.WRITE_ACCESS : ConfigurationHandler.READ_ACCESS);
    } catch (NameNotFoundException nnfe) {
      if (!createIfMissing) {
        if (PolicyConfigurationLog.location.beDebug()) {
          PolicyConfigurationLog.location.logT(Severity.DEBUG, "NameNotFoundException occurred during get of configuration [{0}].", new Object[] { name });
        }

        return null;
      }
    } catch (Exception e) {
      PolicyConfigurationLog.category.logThrowableT(Severity.WARNING, PolicyConfigurationLog.location, "Exception occurred during open of configuration [{0}].", new Object[] { name }, e);

      if (e instanceof ConfigurationLockedException) {
        throw new StorageLockedException("Configuration lock on retrieving configuration [" + name + "]", (ConfigurationLockedException) e);
      }
      throw new SecurityException("Exception occurred on retrieving configuration [" + name + "]", e);
    }

    if ((result == null) && (createIfMissing)) {
      int index = name.lastIndexOf('/');

      if (index < 0) {
        try {
          handler.createRootConfiguration(name);
          if (PolicyConfigurationLog.location.beDebug()) {
            PolicyConfigurationLog.location.logT(Severity.DEBUG, "Root configuration [{0}] created.", new Object[] { name });
          }
        } catch (Exception e) {
          PolicyConfigurationLog.category.logThrowableT(Severity.WARNING, PolicyConfigurationLog.location, "Exception occurred during create of root configuration [{0}].", new Object[] { name }, e);
          if (e instanceof ConfigurationLockedException) {
            throw new StorageLockedException("Configuration lock on retrieving configuration [" + name + "]", (ConfigurationLockedException) e);
          }
          throw new SecurityException("Exception occurred on retrieving configuration [" + name + "]", e);
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
}
