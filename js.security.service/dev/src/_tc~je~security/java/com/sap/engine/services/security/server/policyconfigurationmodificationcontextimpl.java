
package com.sap.engine.services.security.server;

import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.services.security.server.PolicyConfigurationLog;
import com.sap.tc.logging.Severity;

public class PolicyConfigurationModificationContextImpl extends ModificationContextImpl {
  private String configurationPath = null;

  private ModificationContextImpl worker = null;

  public PolicyConfigurationModificationContextImpl( SecurityContext root, ModificationContextImpl worker, String configurationPath) {
    super(root);
    this.worker = worker;
    this.configurationPath = configurationPath;

    if (PolicyConfigurationLog.location.beDebug()) {
      PolicyConfigurationLog.location.logT(Severity.DEBUG, "New policy configuration modification context successfully created for configuration with path [{0}].", new Object[] { configurationPath });
    }
  }

  /**
   * Defines a modifications bundle for the current thread.
   * 
   * @throws SecurityException if the operation cannot be completed.
   */
  public void beginModifications() throws SecurityException {
    worker.beginModifications();
  }

  public SecurityContext beginModifications(Configuration config) {
    throw new SecurityException("Statement not allowed!!");
  }

  public SecurityContext beginModifications(ConfigurationHandler handler, Configuration config) {
    throw new SecurityException("Statement not allowed!!");
  }

  /**
   * Commits a modifications bundle for the current thread.
   * 
   * Does nothing if a configuration was provided on beginModifications
   * 
   * @throws SecurityException if the operation cannot be completed.
   */
  public void commitModifications() throws SecurityException {
    worker.commitModifications();
  }

  /**
   * Rollbacks a modifications bundle for the current thread.
   * 
   * Does nothing if a configuration was provided on beginModifications
   * 
   * @throws SecurityException if the operation cannot be completed.
   */
  public void rollbackModifications() throws SecurityException {
    worker.rollbackModifications();
  }

  public void forgetModifications() {
    worker.forgetModifications();
  }

  public Configuration getConfiguration(String name, boolean writeAccess, boolean createIfMissing) throws SecurityException {
    if (/* !worker.reusable && */!name.startsWith(configurationPath) && !configurationPath.startsWith(name)) {
      name = configurationPath + "/" + name;
    }
    return worker.getConfiguration(name, writeAccess, createIfMissing);
  }
}
