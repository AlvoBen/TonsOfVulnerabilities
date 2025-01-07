/*
 * ...Copyright...
 */

package com.sap.engine.services.security.server;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationChangedListener;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.interfaces.security.ModificationContext;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.security.SecurityModificationContextObject;
import com.sap.engine.services.security.SecurityServerFrame;
import com.sap.engine.services.security.Util;
import com.sap.engine.services.security.server.storage.Storage;
import com.sap.engine.services.deploy.container.AppConfigurationHandler;
import com.sap.tc.logging.Severity;

/**
 * Context used to bundle modification operations over security context. The
 * bundle contains all operation done between a call to beginModifications and
 * commit/rollbackModifications in the thread of the invocation of
 * beginModifications method.
 * 
 * @author Stephan Zlatarev
 * @version 6.30
 */
public class ModificationContextImpl implements ModificationContext {

  public Storage storage = null;

  private SecurityContext owner = null;

  public boolean reusable = false;

  private AppConfigurationHandler appConfigHandler = null;

  public ModificationContextImpl( SecurityContext owner) {
    this.owner = owner;
  }

  public SecurityContext getOwner() {
    return owner;
  }

  /**
   * Defines a modifications bundle for the current thread.
   */
  public void beginModifications() throws SecurityException {
    if (storage == null) {
      this.storage = Storage.getStorage(null);
    }
    storage.begin();
    
    if (PolicyConfigurationLog.location.beDebug()) {
      PolicyConfigurationLog.location.logT(Severity.DEBUG, "New modification bundle started for the current thread.");        
    } 
  }

  /**
   * Defines a modifications bundle for the given configuration instance. In
   * this case no commit will be invoked from the modifications context and no
   * invocation of <code>commitModifications</code> or
   * <code>rollbackModifications</code> will be expected.
   * 
   * @param configuration the configuration to associate operations bundle with.
   * 
   * @return a securty context all operations over which are done using the
   *         configuration instance and will be committed when the configuration
   *         handler is committed.
   */
  public SecurityContext beginModifications(Configuration configuration) throws SecurityException {
    this.storage = Storage.getStorage(configuration);
    storage.begin();
    
    if (PolicyConfigurationLog.location.beDebug()) {
      if (configuration != null) {
        PolicyConfigurationLog.location.logT(Severity.DEBUG, "New modification bundle started for configuration with path [{0}].", new Object[] {configuration.getPath()});
      } else {
        PolicyConfigurationLog.location.logT(Severity.DEBUG, "New modification bundle started for the current thread.");        
      }
    } 
    
    if (SecurityServerFrame.getServiceContext().getCoreContext().getThreadSystem().getThreadContext() != null) {
      SecurityModificationContextObject ctxObject = (SecurityModificationContextObject) SecurityServerFrame.getServiceContext().getCoreContext().getThreadSystem().getThreadContext().getContextObject(
          SecurityModificationContextObject.NAME);
      if (ctxObject != null) {
        ctxObject.setConfiguration(configuration);
      }
    }
    reusable = true;
    return new DeploySecurityContext(this, configuration.getPath());
  }

  /**
   * @see com.sap.engine.interfaces.security.ModificationContext#beginModifications(ConfigurationHandler,
   *      Configuration)
   */
  public SecurityContext beginModifications(AppConfigurationHandler configHandler, Configuration configuration) throws SecurityException {
    this.storage = Storage.getStorage(configHandler, configuration);
    storage.begin();
    
    if (PolicyConfigurationLog.location.beDebug()) {
      if (configuration != null) {
        PolicyConfigurationLog.location.logT(Severity.DEBUG, "New modification bundle started for configuration with path [{0}] and configuration handler [{1}].", new Object[] {configuration.getPath(), configHandler});
      } else {
        PolicyConfigurationLog.location.logT(Severity.DEBUG, "New modification bundle started for the current thread and configuration handler [{0}].", new Object[] {configHandler});        
      }
    } 
    
    if (SecurityServerFrame.getServiceContext().getCoreContext().getThreadSystem().getThreadContext() != null) {
      SecurityModificationContextObject ctxObject = (SecurityModificationContextObject) SecurityServerFrame.getServiceContext().getCoreContext().getThreadSystem().getThreadContext().getContextObject(
          SecurityModificationContextObject.NAME);
      if (ctxObject != null) {
        ctxObject.setConfiguration(configuration);
        ctxObject.setModificationContext(this);
        ctxObject.setAppConfigurationHandler(configHandler);
      }
    }
    reusable = true;
    return new DeploySecurityContext(this, configuration.getPath());
  }

  /**
   * Commits a modifications bundle for the current thread.
   * 
   * Does nothing if a configuration was provided on beginModifications
   */
  public void commitModifications() throws SecurityException {
    try {
      storage.commit();
      if (!reusable) {
        storage = null;
      }
            
      if (PolicyConfigurationLog.location.beDebug()) {
        PolicyConfigurationLog.location.logT(Severity.DEBUG, "Successful commit of modification bundle for the current thread.");
      } 
    } catch (ConfigurationException ce) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "Exception occurred during commit of modification bundle for the current thread.", ce);
      rollbackModifications();
    }
  }

  public void forgetModifications() {
    try {
      storage.forget();
      if (PolicyConfigurationLog.location.beDebug()) {
        PolicyConfigurationLog.location.debugT("Successful forget of modification bundle for the current thread.");
      } 
    } catch (ConfigurationException ce) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "Exception occurred during forget of modification bundle for the current thread.", ce);
    }
  }

  /**
   * Rollbacks a modifications bundle for the current thread.
   * 
   * Does nothing if a configuration was provided on beginModifications
   */
  public void rollbackModifications() throws SecurityException {
    try {
      storage.rollback();
      if (!reusable) {
        storage = null;
      }
      
      if (PolicyConfigurationLog.location.beDebug()) {
        PolicyConfigurationLog.location.logT(Severity.DEBUG, "Successful rollback of modification bundle for the current thread.");
      } 
      
    } catch (Exception e) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "Exception occurred during rollback of modification bundle for the current thread.", e);
    }    
  }

  public Configuration getConfiguration(String name, boolean writeAccess, boolean createIfMissing) throws SecurityException {
    return storage.getConfiguration(name, writeAccess, createIfMissing);
  }

  public void registerConfigurationListener(ConfigurationChangedListener listener, String path) {
    storage.registerConfigurationListener(listener, path);
  }
}
