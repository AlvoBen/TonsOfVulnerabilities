package com.sap.engine.services.security.patch;

import com.sap.engine.frame.ServiceContext;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.services.security.server.SecurityConfigurationPath;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

public class ChangeDaemon {
  public static final String LAST_CHANGE_LEVEL = "last_change";
  /* Different change level is needed because new changes in older releses
  should be distinguished from the changes that exist only in newer releases.*/
  public static final String LAST_ADDITIONAL_CHANGE_LEVEL = "last_additional_change";
  static ConfigurationHandlerFactory configFactory = null;

  private int patchLevel = 0;
  private int additionalPatchLevel = 0;
  /* Additioanl changes are run both on scratch installations and on upgrade. */
  private boolean runAdditionalChanges = true;
  private ClassLoader classloader = null;
  private static final Location LOCATION = Location.getLocation(ChangeDaemon.class);
  
  public ChangeDaemon(ServiceContext environment) {
    classloader = getClass().getClassLoader();
    configFactory = environment.getCoreContext().getConfigurationHandlerFactory();
  }

  public boolean init() {
    ConfigurationHandler configHandler = null;
    try {
      configHandler = configFactory.getConfigurationHandler();
      Configuration config = null;
      try {
        config = configHandler.openConfiguration(SecurityConfigurationPath.SECURITY_PATH, ConfigurationHandler.WRITE_ACCESS);
        if (config.getAllSubConfigurations().size() == 0) {
          patchLevel = extractLastChangeLevel("com.sap.engine.services.security.patch.Change");
          config.addConfigEntry(LAST_CHANGE_LEVEL, new Integer(patchLevel));
          runAdditionalChanges = false;

          configHandler.commit();
          return false;
        }

        boolean configModified = false;
        if (config.existsConfigEntry(LAST_CHANGE_LEVEL)) {
          patchLevel = ((Integer) config.getConfigEntry(LAST_CHANGE_LEVEL)).intValue();
        } else {
          patchLevel = 0;
          config.addConfigEntry(LAST_CHANGE_LEVEL, new Integer(patchLevel));
          configModified = true;
        }
        if (config.existsConfigEntry(LAST_ADDITIONAL_CHANGE_LEVEL)) {
          additionalPatchLevel = ((Integer) config.getConfigEntry(LAST_ADDITIONAL_CHANGE_LEVEL)).intValue();
        } else {
          additionalPatchLevel = 0;
          config.addConfigEntry(LAST_ADDITIONAL_CHANGE_LEVEL, new Integer(additionalPatchLevel));
          configModified = true;
        }

        if (configModified) {
          configHandler.commit();
        } else {
          configHandler.rollback();
        }
      } catch (NameNotFoundException nnfe) {
        config = configHandler.createRootConfiguration(SecurityConfigurationPath.SECURITY_PATH);

        patchLevel = extractLastChangeLevel("com.sap.engine.services.security.patch.Change");
        config.addConfigEntry(LAST_CHANGE_LEVEL, new Integer(patchLevel));
        runAdditionalChanges = false;

        configHandler.commit();
        return false;
      } catch (Exception _) {
        throw _;
      }
    } catch (Exception e) {
      try {
        configHandler.rollback();
      } catch (Exception _) {
        LOCATION.traceThrowableT(Severity.WARNING, "Cannot rollback changes: ", _);
      }
      LOCATION.traceThrowableT(Severity.WARNING, "Cannot initialize ChangeDaemon. ", e);
      return false;
    } finally {
      try {
        configHandler.closeAllConfigurations();
      } catch (Exception e) {
        LOCATION.traceThrowableT(Severity.WARNING, "", e);
      }
    }
    return true;
  }

  public boolean run() throws Exception {
    boolean toUpgrade = false;

    int next = patchLevel + 1;
    while (true) {
      Class nextChange = null;
      try {
        nextChange = classloader.loadClass("com.sap.engine.services.security.patch.Change" + next);
      } catch (ClassNotFoundException cnfe) {
        LOCATION.traceThrowableT(Severity.DEBUG, "Class com.sap.engine.services.security.patch.Change" + next + " cannot be loaded.", cnfe);
        break;
      }
      try {
        nextChange.getMethod("run", new Class[0]).invoke(nextChange.newInstance(), new Object[0]);
        toUpgrade = true;
        savePatchLevel(next, LAST_CHANGE_LEVEL);
        LOCATION.logT(Severity.INFO, "Patch level [" + next + "] finished successfully.");
      } catch (Exception e) {
        SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, e, "ASJ.secsrv.000182", "Error occured on patch level [{0}].", new Object[]{ next });
        throw e;
      }
      next++;
    }

    if (runAdditionalChanges) {
      next = additionalPatchLevel + 1;
      while (true) {
        Class nextChange = null;
        try {
          nextChange = classloader.loadClass("com.sap.engine.services.security.patch.AdditionalChange" + next);
        } catch (ClassNotFoundException cnfe) {
          LOCATION.traceThrowableT(Severity.DEBUG, "Class com.sap.engine.services.security.patch.AdditionalChange" + next + " cannot be loaded.", cnfe);
          break;
        }
        try {
          nextChange.getMethod("run", new Class[0]).invoke(nextChange.newInstance(), new Object[0]);
          toUpgrade = true;
          savePatchLevel(next, LAST_ADDITIONAL_CHANGE_LEVEL);
          LOCATION.logT(Severity.INFO, "Additional patch level [" + next + "] finished successfully.");
        } catch (Exception e) {
          SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, e, "ASJ.secsrv.000182", "Error occured on patch level [{0}].", new Object[]{ next });
          throw e;
        }
        next++;
      }
    }

    return toUpgrade;
  }

  private void savePatchLevel(int level, String changeLevelName) throws ConfigurationException {
    ConfigurationHandler configHandler = null;
    try {
      configHandler = configFactory.getConfigurationHandler();
      Configuration config = configHandler.openConfiguration(SecurityConfigurationPath.SECURITY_PATH, ConfigurationHandler.WRITE_ACCESS);
      config.modifyConfigEntry(changeLevelName, new Integer(level));
      configHandler.commit();
    } catch (Exception e) {
      configHandler.rollback();
    } finally {
      try {
        configHandler.closeAllConfigurations();
      } catch (ConfigurationException e) {
        throw e;
      }
    }
  }

  private int extractLastChangeLevel(String changeClassName) {
    int lastChange = 0;
    while (true) {
      try {
        Class.forName(changeClassName + (++lastChange));
      } catch (ClassNotFoundException cnfe) {
        break;
      }
    }

    return (--lastChange);
  }

}
