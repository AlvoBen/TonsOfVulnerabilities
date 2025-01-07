
package com.sap.engine.services.security.server;

import java.util.Map;
import java.util.Properties;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.InconsistentReadException;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.services.security.Util;
import com.sap.engine.services.security.exceptions.SecurityResourceAccessor;
import com.sap.engine.services.security.exceptions.StorageException;
import com.sap.engine.services.security.server.deploy.PolicyConfigurationContainer;
import com.sap.tc.logging.Severity;

public class PolicyRoots {
  private SecurityContext securityContext = null;

  public PolicyRoots( SecurityContext context) {
    this.securityContext = context;
  }

  public void addPolicyRoot(String configurationId, String path) {
    if (PolicyConfigurationLog.location.beDebug()) {
      PolicyConfigurationLog.location.logT(Severity.DEBUG, "Trying to add new policy root with id [{0}] and path [{1}].", new Object[] { configurationId, path });
    }
    ModificationContextImpl modifications = (ModificationContextImpl) securityContext.getModificationContext();
    modifications.beginModifications();

    try {
      Configuration store = modifications.getConfiguration(SecurityConfigurationPath.SECURITY_CONFIGURATIONS_PATH, true, true);

      if (path == null) {
        path = store.getPath();
      }

      if (!store.existsConfigEntry(configurationId)) {
        store.addConfigEntry(configurationId, path);
      } else if (PolicyConfigurationContainer.isParallelDeployment()) {
        store.modifyConfigEntry(configurationId, path);
      } else {
        boolean configexists = false;
        try {
          String oldpath = (String) store.getConfigEntry(configurationId);
          Configuration securityConfig = modifications.getConfiguration(oldpath, false, false);
          configexists = (securityConfig != null) && securityConfig.existsSubConfiguration(SecurityConfigurationPath.SECURITY_PATH);
        } catch (Exception configurationnotexists) {
          configexists = false;
        }
        if (!configexists) {
          store.modifyConfigEntry(configurationId, path);
        } else {
          throw new SecurityException("Exception occurred on adding policy configuration: " + configurationId);
        }
      }
      modifications.commitModifications();
      if (PolicyConfigurationLog.location.beDebug()) {
        PolicyConfigurationLog.location.logT(Severity.DEBUG, "Policy root successfully added [{0}].", new Object[] { configurationId });
      }
    } catch (ConfigurationException e) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "ConfigurationException occurred during add policy root to the storage. [configurationID={0}, path={1}]", new Object[] { configurationId, path }, e);

      try {
        modifications.rollbackModifications();
      } catch (Exception ex) {
        Util.SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "Exception occurred during storage rollback. [configurationID={0}, path={1}]", new Object[] { configurationId, path }, ex);
      }

      throw new SecurityException("Exception occurred on adding policy configuration: " + configurationId, e);
    } catch (Exception e) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "Exception occurred during add policy root to the storage. [configurationID={0}, path={1}]", new Object[] { configurationId, path }, e);

      try {
        modifications.rollbackModifications();
      } catch (Exception ex) {
        Util.SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "Exception occurred during storage rollback. [configurationID={0}, path={1}]", new Object[] { configurationId, path }, ex);
      }
      throw new SecurityException("Exception occurred on adding policy configuration: " + configurationId, e);
    }
  }

  public void renamePolicyRoot(String configurationId, String newConfigurationId) {
    if (PolicyConfigurationLog.location.beDebug()) {
      PolicyConfigurationLog.location.logT(Severity.DEBUG, "Trying to add rename policy root [{0}] to [{1}].", new Object[] { configurationId, newConfigurationId });
    }

    ModificationContextImpl modifications = (ModificationContextImpl) securityContext.getModificationContext();
    modifications.beginModifications();

    try {
      Configuration store = modifications.getConfiguration(SecurityConfigurationPath.SECURITY_CONFIGURATIONS_PATH, true, false);
      String path = (String) store.getConfigEntry(configurationId);

      store.deleteConfigEntry(configurationId);
      store.addConfigEntry(newConfigurationId, path);
      modifications.commitModifications();

      if (PolicyConfigurationLog.location.beDebug()) {
        PolicyConfigurationLog.location.logT(Severity.DEBUG, "Policy root successfully renamed in storage.");
      }
    } catch (Exception ce) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "Exception occurred during rename of policy root [{0}] to [{1}].", new Object[] {
          configurationId, newConfigurationId }, ce);

      try {
        modifications.rollbackModifications();
      } catch (Exception e) {
        Util.SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "Exception occurred during rollback of modifications. [configurationID={0}]", new Object[] { configurationId }, e);
      }
      throw new SecurityException("Exception occurred on renaming policy configuration: " + configurationId, ce);
    }
  }

  public String removePolicyRoot(String policy) {
    ModificationContextImpl modifications = (ModificationContextImpl) securityContext.getModificationContext();
    modifications.beginModifications();

    try {
      Configuration store = modifications.getConfiguration(SecurityConfigurationPath.SECURITY_CONFIGURATIONS_PATH, true, false);
      if (PolicyConfigurationLog.location.beDebug()) {
        if (store != null) {
          PolicyConfigurationLog.location.logT(Severity.DEBUG, "Trying to remove policy root [{0}] from configuration [{1}].", new Object[] { policy, store.getPath() });
        }
      }

      String path = (String) store.getConfigEntry(policy);

      store.deleteConfigEntry(policy);
      modifications.commitModifications();

      if (PolicyConfigurationLog.location.beDebug()) {
        PolicyConfigurationLog.location.logT(Severity.DEBUG, "Policy root successfully removed from storage.");
      }
      return path;
    } catch (ConfigurationException ce) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "Exception occurred during remove of policy root [{0}].", new Object[] { policy }, ce);

      try {
        modifications.rollbackModifications();
      } catch (Exception e) {
        Util.SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "Exception occurred during storage rollback. [policy={0}]", new Object[] { policy }, e);
      }

      return null;
    } catch (Exception se) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "Exception occurred during remove of policy root [{0}].", new Object[] { policy }, se);

      try {
        modifications.rollbackModifications();
      } catch (Exception e) {
        Util.SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "Exception occurred during storage rollback. [policy={0}]", new Object[] { policy }, e);
      }

      return null;
    }
  }

  public String removePolicyRoot(String policy, Configuration store) throws Exception {

    if (PolicyConfigurationLog.location.beDebug()) {
      if (store != null) {
        PolicyConfigurationLog.location.logT(Severity.DEBUG, "Trying to remove policy root [{0}] from configuration [{1}].", new Object[] { policy, store.getPath() });
      }
    }

    try {
      String path = (String) store.getConfigEntry(policy);
      store.deleteConfigEntry(policy);

      if (PolicyConfigurationLog.location.beDebug()) {
        if (store != null) {
          PolicyConfigurationLog.location.logT(Severity.DEBUG, "Removed policy root [{0}] from configuration [{1}].", new Object[] { policy, store.getPath() });
        }
      }
      return path;
    } catch (Exception se) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "Exception occurred during remove of policy root [{0}].", new Object[] { policy }, se);
    }
    return null;
  }

  public Map listPolicyRoots() {
    ModificationContextImpl modifications = (ModificationContextImpl) securityContext.getModificationContext();
    modifications.beginModifications();

    try {
      Configuration store = modifications.getConfiguration(SecurityConfigurationPath.SECURITY_CONFIGURATIONS_PATH, false, false);
      return (store != null) ? store.getAllConfigEntries() : new Properties();
    } catch (ConfigurationException ce) {
      throw new StorageException("Cannot list entries", ce);
    } finally {
      try {
        modifications.forgetModifications();
      } catch (Exception e) {
        Util.SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "Exception occurred during storage forget.", e);
      }
    }
  }

  public String getPolicyRoot(String policy) {
    if (PolicyConfigurationLog.location.beDebug()) {
      PolicyConfigurationLog.location.logT(Severity.DEBUG, "Trying to get policy root [{0}].", new Object[] { policy });
    }

    ModificationContextImpl modifications = (ModificationContextImpl) securityContext.getModificationContext();
    modifications.beginModifications();

    try {
      Configuration store = modifications.getConfiguration(SecurityConfigurationPath.SECURITY_CONFIGURATIONS_PATH, false, false);
      
      if (store == null) {
        return null;
      }
      
      InconsistentReadException ex = null;
      
      for (int i = 0; i < 100; i++) {
        try {
          return (String) store.getConfigEntry(policy);
        } catch (InconsistentReadException e) {
          ex = e;
          
          if (i < 99) {
            store.close();
            
            try {
              Thread.sleep(100);
            } catch (InterruptedException e1) {
              // $JL-EXC$ - ignore
            }
            
            store = modifications.getConfiguration(SecurityConfigurationPath.SECURITY_CONFIGURATIONS_PATH, false, false);
            
            if (store == null) {
              return null;
            }
          }
        }
      }
      
      throw ex;
    } catch (NameNotFoundException ne) {
      return null;
    } catch (ConfigurationException ce) {
      throw new StorageException("Cannot get path", ce);
    } catch (Exception ce) {
      throw new StorageException("Cannot get path", ce);
    } finally {
      try {
        modifications.forgetModifications();
      } catch (Exception e) {
        Util.SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "Exception occurred during storage forget. [policy={0}]", new Object[] { policy }, e);
      }
    }
  }
}
