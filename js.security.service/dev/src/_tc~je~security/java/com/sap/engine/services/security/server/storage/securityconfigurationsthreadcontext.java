
package com.sap.engine.services.security.server.storage;

import java.util.Enumeration;
import java.util.Hashtable;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationLockedException;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.services.deploy.container.AppConfigurationHandler;
import com.sap.engine.services.security.exceptions.StorageLockedException;
import com.sap.engine.services.security.server.PolicyConfigurationLog;
import com.sap.tc.logging.Severity;

public class SecurityConfigurationsThreadContext {

  protected static InheritableThreadLocal current = new InheritableThreadLocal();

  private Hashtable writeConfigs = new Hashtable();

  private Hashtable readConfigs = new Hashtable();

  private AppConfigurationHandler handler = null;
  
  private Storage storage = null;
  
  protected static SecurityConfigurationsThreadContext getContext() {
    if (PolicyConfigurationLog.location.bePath()) {
      PolicyConfigurationLog.location.logT(Severity.PATH, "Entering SecurityConfigurationsThreadContext.getContext()");
    }

    try {
      SecurityConfigurationsThreadContext context = (SecurityConfigurationsThreadContext) current.get();
      if (context == null) {
        context = new SecurityConfigurationsThreadContext();
        current.set(context);
        if (PolicyConfigurationLog.location.beDebug()) {
          PolicyConfigurationLog.location.logT(Severity.DEBUG, "New SecurityConfigurationsThreadContext object created and set in InheritableThreadLocal object.");
        }
      }
      return context;
    } finally {
      if (PolicyConfigurationLog.location.bePath()) {
        PolicyConfigurationLog.location.logT(Severity.PATH, "Exiting SecurityConfigurationsThreadContext.getContext()");
      }
    }
  }

  protected void setConfigurationHandler(AppConfigurationHandler handler) {
    this.handler = handler;
  }
  
  protected void setStorage(Storage storage) {
    this.storage = storage;
  }
  
  protected void removeConfigurationHandler() {
    this.handler = null;
  }

  public Configuration getConfiguration(String id, boolean write, boolean createIfMissing) {
    Configuration config = getConfigurationFromCache(id, write);
    if (config != null) {
      if (PolicyConfigurationLog.location.beDebug()) {
        PolicyConfigurationLog.location.logT(Severity.DEBUG, "Configuration object loaded from cache [{0}].", new Object[] { config });
      }
      return config;
    }
    config = recursiveGetConfiguration(id, write, createIfMissing);
    if (config != null) {
      if (write) {
        writeConfigs.put(id, config);
        if (PolicyConfigurationLog.location.beDebug()) {
          PolicyConfigurationLog.location.logT(Severity.DEBUG, "Configuration object in write mode stored in cache [{0}].", new Object[] { config });
        }
      } else {
        readConfigs.put(id, config);
        if (PolicyConfigurationLog.location.beDebug()) {
          PolicyConfigurationLog.location.logT(Severity.DEBUG, "Configuration object in read mode stored in cache [{0}].", new Object[] { config });
        }
      }
    }
    return config;
  }

  private Configuration getConfigurationFromCache(String id, boolean write) {
    Configuration config = (Configuration) (write ? writeConfigs.get(id) : readConfigs.get(id));
    boolean gotFromWrite = write;

    if (config == null) {
      if (write) {
        config = (Configuration) readConfigs.remove(id);
        if (config != null && config.isValid()) {
          try {
            config.close();
          } catch (ConfigurationException e) {
            // $JL-EXC$
          }
        }
        config = null;
      } else {
        config = (Configuration) writeConfigs.get(id);
        gotFromWrite = true;
      }
    }

    if (config != null && !config.isValid()) {
      if (gotFromWrite) {
        writeConfigs.remove(id);
      } else {
        readConfigs.remove(id);
      }
      config = null;
    }

    return config;
  }

  private final Configuration recursiveGetConfiguration(String name, boolean writeAccess, boolean createIfMissing) throws SecurityException {
    Configuration result = null;

    try {
      result = internalOpenConfiguration(name, writeAccess);
    } catch (NameNotFoundException nnfe) {
      if (!createIfMissing) {
        if (PolicyConfigurationLog.location.beDebug()) {
          PolicyConfigurationLog.location.logT(Severity.DEBUG, "NameNotFoundException occurred during get of configuration [{0}] from handler [{1}].", new Object[] { name, handler });
        }
        return null;
      }
    } catch (Exception e) {
      PolicyConfigurationLog.location.traceThrowableT(Severity.WARNING, "Exception occurred during open of configuration [{0}] from handler [{1}].", new Object[] {
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
          handler.createRootConfiguration(name);
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
              PolicyConfigurationLog.location.logT(Severity.DEBUG, "Sub configuration [{0}] created for configuration [{1}] from configuration handler [{2}].", new Object[] { name, path, handler });
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

  private final Configuration internalOpenConfiguration(String name, boolean writeAccess) throws Exception {
    Configuration result = null;
    Exception exception = null;

    try {
      result = storage.openConfiguration(name, writeAccess ? ConfigurationHandler.WRITE_ACCESS : ConfigurationHandler.READ_ACCESS);
    } catch (Exception e) {
      exception = e;

      if (PolicyConfigurationLog.location.beDebug()) {
        PolicyConfigurationLog.location.logT(Severity.DEBUG, "Unable to load configuration [{0}] directly.", new Object[] { name });
      }
    }

    if ((result == null) || !result.isValid()) {
      Enumeration paths = writeConfigs.keys();

      while (((result == null) || (!result.isValid())) && paths.hasMoreElements()) {
        String path = (String) paths.nextElement();

        if (name.startsWith(path)) {
          try {
            result = ((Configuration) writeConfigs.get(path)).getSubConfiguration(name.substring(path.length() + 1));
          } catch (Exception e) {
            if (PolicyConfigurationLog.location.beDebug()) {
              PolicyConfigurationLog.location.logT(Severity.DEBUG, "Unable to load sub-configuration [{0}] of [{1}].", new Object[] { name, path });
            }
          }
        }
      }
    }

    if ((result == null) || !result.isValid()) {
      Enumeration paths = readConfigs.keys();

      while (((result == null) || (!result.isValid())) && paths.hasMoreElements()) {
        String path = (String) paths.nextElement();

        if (name.startsWith(path)) {
          try {
            result = ((Configuration) readConfigs.get(path)).getSubConfiguration(name.substring(path.length() + 1));
          } catch (Exception e) {
            if (PolicyConfigurationLog.location.beDebug()) {
              PolicyConfigurationLog.location.logT(Severity.DEBUG, "Unable to load sub-configuration [{0}] of [{1}].", new Object[] { name, path });
            }
          }
        }
      }
    }

    if ((exception != null) && ((result == null) || !result.isValid())) {
      throw exception;
    }

    return result;
  }

}
