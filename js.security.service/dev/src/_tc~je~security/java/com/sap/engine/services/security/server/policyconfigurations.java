
package com.sap.engine.services.security.server;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.security.UpdateSecurityContext;
import com.sap.engine.services.security.Util;
import com.sap.engine.services.security.exceptions.StorageLockedException;
import com.sap.engine.services.security.server.storage.DeployStorage;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

public abstract class PolicyConfigurations implements SecurityContext {
  private static PolicyConfigurationRemovalListener listener = null;

  static Hashtable<String, SecurityContext> configurations = null;

  protected PolicyRoots roots = null;

  private final String TYPE_ENTRY = "type";

  public PolicyConfigurations() {
    if (PolicyConfigurationLog.location.bePath()) {
      PolicyConfigurationLog.location.logT(Severity.PATH, "Entering PolicyConfigurations constructor method");
    }
    try {
      if (configurations == null) {
        configurations = new Hashtable<String, SecurityContext>();
        listener = new PolicyConfigurationRemovalListener(this);
      }
      roots = new PolicyRoots(this);
    } finally {
      if (PolicyConfigurationLog.location.bePath()) {
        PolicyConfigurationLog.location.logT(Severity.PATH, "Exiting PolicyConfigurations constructor method");
      }
    }
  }

  /**
   * Retireve a context for security configuration of a deployed component. Note
   * that policy configurations are not hierarhical.
   * 
   * @param configurationId string identifier of the deployed instance of the
   *        component.
   * 
   * @return security context of a deployed instance of a component.
   */
  public SecurityContext getPolicyConfigurationContext(String configurationId) {
    return getPolicyConfigurationContext(configurationId, true);
  }

  private void registerConfigurationListener(String configurationPath) {
    ModificationContextImpl modifications = (ModificationContextImpl) getModificationContext();
    modifications.beginModifications();
    try {
      if (configurationPath.length() == 0) {
        modifications.registerConfigurationListener(listener, SecurityConfigurationPath.AUTHENTICATION_PATH);
      } else {
        modifications.registerConfigurationListener(listener, configurationPath + "/" + SecurityConfigurationPath.AUTHENTICATION_PATH);
      }
      modifications.commitModifications();
      if (PolicyConfigurationLog.location.beDebug()) {
        PolicyConfigurationLog.location.logT(Severity.DEBUG, "Policy configuration listener successfully registered for configuration path [{0}] ", new Object[] { configurationPath });
      }

    } catch (Exception e) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "Exception occurred during register of listener for policy configuration [{0}].", new Object[] { configurationPath }, e);
      modifications.rollbackModifications();
    }
  }

  protected SecurityContext getPolicyConfigurationContext(String configurationId, boolean inCache) {
    SecurityContext sc = null;
    if (inCache) {
      sc = (SecurityContext) configurations.get(configurationId);
      if (PolicyConfigurationLog.location.beDebug()) {
        PolicyConfigurationLog.location.logT(Severity.DEBUG, "Security context [{0}] successfully loaded from cache.", new Object[] { configurationId });
      }
    }
    if (sc == null) {
      String path = getRegisteredConfigurationPath(configurationId);
      
      if (path == null) {
        if (PolicyConfigurationLog.location.beInfo()) {
          PolicyConfigurationLog.location.logT(Severity.INFO, "Security context [{0}] does not exist", new Object[] { configurationId });
        }
        return null;
      }
      sc = loadSecurityContext(configurationId, path, false);

      if (PolicyConfigurationLog.location.beDebug()) {
        PolicyConfigurationLog.location.logT(Severity.DEBUG, "Security context [{0}] successfully loaded from path [{1}].", new Object[] { configurationId, path });
      }

      if (sc != null) {
        if (sc instanceof PolicyConfigurationSecurityContext) {
          if (!((PolicyConfigurationSecurityContext) sc).isTemporary()) {
            configurations.put(configurationId, sc);
            if (PolicyConfigurationLog.location.beDebug()) {
              PolicyConfigurationLog.location.logT(Severity.DEBUG, "Policy configuration security context [{0}] stored in cache.", new Object[] { configurationId });
            }
            registerConfigurationListener(path);
          }
        } else {
          configurations.put(configurationId, sc);
          if (PolicyConfigurationLog.location.beDebug()) {
            PolicyConfigurationLog.location.logT(Severity.DEBUG, "Security context [{0}] stored in cache.", new Object[] { configurationId });
          }
          registerConfigurationListener(path);
        }
      }
    }
    return sc;
  }

  /**
   * Lists all registered policy configurations.
   * 
   * @return the String identifiers of the policy configurations.
   */
  public String[] listPolicyConfigurations() {
    if (PolicyConfigurationLog.location.bePath()) {
      PolicyConfigurationLog.location.logT(Severity.PATH, "Entering PolicyConfigurations.listPolicyConfigurations()");
    }
    
    try {
      Map<String, String> map = roots.listPolicyRoots();
      
      for (String key: map.keySet()) {
        String path = (String) map.get(key);
        
        if (isUnregisteredConfiguration(path)) {
          configurations.remove(key);
          unregisterPolicyConfiguration(key);
          continue;
        } 
        
        if (configurations.get(key) == null) {
          SecurityContext policy = loadSecurityContext(key, path, false);
          
          if (policy != null) {
            configurations.put(key, policy);
            registerConfigurationListener(path);
          }
        } 
      }
      
      int size = configurations.size();
      String[] result = configurations.keySet().toArray(new String[size]);

      if (PolicyConfigurationLog.location.beInfo()) {
        PolicyConfigurationLog.location.logT(Severity.INFO, "Number of successfully loaded policy configurations [{0}].", new Object[] { new Integer(size) });
      }

      return result;
    } finally {
      if (PolicyConfigurationLog.location.bePath()) {
        PolicyConfigurationLog.location.logT(Severity.PATH, "Exiting PolicyConfigurations.listPolicyConfigurations()");
      }
    }
  }

  /**
   * @deprecated use registerPolicyConfiguration(String configurationId, byte
   *             configurationType) instead
   * 
   */
  public void registerPolicyConfiguration(String configurationId) {
    registerPolicyConfiguration(configurationId, SecurityContext.TYPE_OTHER);
  }

  /**
   * Registers a new policy configuration of specified type. This could
   * represent a J2EE application, J2EE component or JCA resource adapter. Note
   * that policy configurations are not hierarhical
   * 
   * @param configurationId identifier of the component
   * @param configurationType type of the component Valid type values are all
   *        policy configuration type constants defined in the SecurityContext
   *        class with the exception of
   *        <code>SecurityContext.TYPE_INVALID</code>.
   */
  public void registerPolicyConfiguration(String configurationId, byte configurationType) {
    if (PolicyConfigurationLog.location.bePath()) {
      PolicyConfigurationLog.location.logT(Severity.PATH, "Entering PolicyConfigurations.registerPolicyConfiguration(String configurationId, byte configurationType)");
    }

    try {
      String configurationPath = getConfigurationPath(configurationId);
      registerPolicyConfiguration(configurationId, configurationPath, configurationType);
    } finally {
      if (PolicyConfigurationLog.location.bePath()) {
        PolicyConfigurationLog.location.logT(Severity.PATH, "Exiting PolicyConfigurations.registerPolicyConfiguration(String configurationId, byte configurationType)");
      }
    }
  }

  private void registerPolicyConfiguration(String configurationId, String configurationPath, byte configurationType) {
    if (PolicyConfigurationLog.location.beDebug()) {
      PolicyConfigurationLog.location.logT(Severity.DEBUG, "Trying to register new policy configuration [{0}] .", new Object[] { configurationId });
    }
    
    roots.addPolicyRoot(configurationId, configurationPath);
    loadSecurityContext(configurationId, configurationPath, true);
    if (PolicyConfigurationLog.location.beDebug()) {
      PolicyConfigurationLog.location.logT(Severity.DEBUG, "Security context [{0}] successfully loaded from path [{1}].", new Object[] {configurationId, configurationPath});
    }    
    
    setPolicyConfigurationType(configurationId, configurationType);
    registerConfigurationListener(configurationPath);
    
    if (PolicyConfigurationLog.location.beInfo()) {
      PolicyConfigurationLog.location.logT(Severity.INFO, "Policy configuration  [{0}]  successfully registered.", new Object[] {configurationId});
    }
  }

  public void registerPolicyConfiguration(PolicyConfigurationRoot pc) {
    if (PolicyConfigurationLog.location.bePath()) {
      PolicyConfigurationLog.location.logT(Severity.PATH, "Entering PolicyConfigurations.registerPolicyConfiguration(PolicyConfigurationRoot pc)");
    }
    
    try {
      registerPolicyConfiguration(pc.getId(), pc.getPath(), pc.getType());
    } finally {
      if (PolicyConfigurationLog.location.bePath()) {
        PolicyConfigurationLog.location.logT(Severity.PATH, "Exiting PolicyConfigurations.registerPolicyConfiguration(PolicyConfigurationRoot pc)");
      }
    }
  }

  /**
   * Sets the specified type of a security policy configuration.
   * 
   * @param configurationId identifier of the component
   * @param type of the security context. Valid type values are all policy
   *        configuration type constants defined in the SecurityContext class
   *        with the exception of <code>SecurityContext.TYPE_INVALID</code>.
   */
  public void setPolicyConfigurationType(String configurationId, byte type) {
    if (PolicyConfigurationLog.location.bePath()) {
      PolicyConfigurationLog.location.logT(Severity.PATH, "Entering PolicyConfigurations.setPolicyConfigurationType(String configurationId, byte type) ");
    }
    try {
      if (!PolicyConfigurations.isValidConfigurationType(type)) {
        throw new IllegalArgumentException("The specified policy configuration type is not valid.");
      }

      ModificationContextImpl modifications = (ModificationContextImpl) getModificationContext();
      modifications.beginModifications();
      try {
        Configuration sub = null;
        String path = roots.getPolicyRoot(configurationId);

        if (path == null) {
          throw new SecurityException("Unable to get the path for the specified policy configuration");
        }

        sub = modifications.getConfiguration(path + "/" + SecurityConfigurationPath.SECURITY_PATH, true, true);

        if (sub == null) {
          throw new SecurityException("Unable to load the security configuration for the specified policy configuration");
        }

        if (sub.existsConfigEntry(TYPE_ENTRY)) {
          sub.modifyConfigEntry(TYPE_ENTRY, String.valueOf(type));
        } else {
          sub.addConfigEntry(TYPE_ENTRY, String.valueOf(type));
        }
        modifications.commitModifications();
        SimpleLogger.log(Severity.INFO, PolicyConfigurationLog.category, PolicyConfigurationLog.location, "ASJ.secsrv.000057", "Type of policy configuration [{0}] successfully set to [{1}].", new Object[] { configurationId, type });
      } catch (Exception e) {
        SimpleLogger.log(Severity.INFO, Util.SEC_SRV_CATEGORY, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000055", "Cannot set type for policy configuration [{0}].", new Object[] { configurationId } );
        Util.SEC_SRV_LOCATION.traceThrowableT(Severity.INFO, "Cannot set type for policy configuration [{0}].", new Object[] { configurationId } , e);
        modifications.rollbackModifications();
      }
    } finally {
      if (PolicyConfigurationLog.location.bePath()) {
        PolicyConfigurationLog.location.logT(Severity.PATH, "Exiting PolicyConfigurations.setPolicyConfigurationType(String configurationId, byte type) ");
      }
    }
  }

  /**
   * Sets the specified type of a security policy configuration. This method
   * must not be called on the root security context.
   * 
   * @param type of the security context.
   */
  public void setPolicyConfigurationType(byte type) {
    throw new IllegalStateException("Not allowed to change the type of the root security context!");
  }

  /**
   * Retireve the type of this security context. In case a valid type cannot be
   * retrieved this method returns <code>SecurityContext.TYPE_INVALID</code>
   * 
   * @param configurationId identifier of the component
   * 
   * @return the type of the security context. Valid type values are all policy
   *         configuration type constants defined in the SecurityContext class
   *         with the exception of <code>SecurityContext.TYPE_INVALID</code>.
   */
  public byte getPolicyConfigurationType(String configurationId) {
    if (PolicyConfigurationLog.location.bePath()) {
      PolicyConfigurationLog.location.logT(Severity.PATH, "Entering PolicyConfigurations.getPolicyConfigurationType(String configurationId)");
    }

    try {
      byte type = TYPE_INVALID;
      ModificationContextImpl modifications = (ModificationContextImpl) getModificationContext();
      modifications.beginModifications();

      try {
        Configuration sub = null;
        String path = roots.getPolicyRoot(configurationId);

        if (path == null) {
          throw new SecurityException("Unable to get the path for the specified policy configuration");
        }

        sub = modifications.getConfiguration(path + "/" + SecurityConfigurationPath.SECURITY_PATH, false, false);

        if (sub == null) {
          throw new SecurityException("Unable to load the security configuration for the specified policy configuration");
        }

        if (sub.existsConfigEntry(TYPE_ENTRY)) {
          type = Byte.parseByte(sub.getConfigEntry(TYPE_ENTRY).toString());
          if (isValidConfigurationType(type)) {
            if (PolicyConfigurationLog.location.beDebug()) {
              PolicyConfigurationLog.location.logT(Severity.DEBUG, "Type of policy configuration [{0}] successfully get: [{1}]", new Object[] { configurationId, type });
            }
            return type;
          }
        }
      } catch (Exception e) {
        SimpleLogger.log(Severity.INFO, Util.SEC_SRV_CATEGORY, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000056", "Cannot get type for policy configuration [{0}].", new Object[] { configurationId });
        Util.SEC_SRV_LOCATION.traceThrowableT(Severity.INFO, "Cannot get type for policy configuration [{0}].", new Object[] { configurationId }, e);
      } finally {
        modifications.rollbackModifications();
      }

      return TYPE_INVALID;
    } finally {
      if (PolicyConfigurationLog.location.bePath()) {
        PolicyConfigurationLog.location.logT(Severity.PATH, "Exiting PolicyConfigurations.getPolicyConfigurationType(String configurationId)");
      }
    }
  }

  public void renamePolicyConfiguration(String configurationId, String newConfigurationId) {
    if (PolicyConfigurationLog.location.bePath()) {
      PolicyConfigurationLog.location.logT(Severity.PATH, "Entering PolicyConfigurations.renamePolicyConfiguration(String configurationId, String newConfigurationId)");
    }
    try {
      // String configurationPath = getConfigurationPath(configurationId);
      configurations.remove(configurationId);
      if (PolicyConfigurationLog.location.beDebug()) {
        PolicyConfigurationLog.location.logT(Severity.DEBUG, "Security context [{0}] removed from cache.", new Object[] { configurationId });
      }
      roots.renamePolicyRoot(configurationId, newConfigurationId);
    } finally {
      if (PolicyConfigurationLog.location.bePath()) {
        PolicyConfigurationLog.location.logT(Severity.PATH, "Exiting PolicyConfigurations.renamePolicyConfiguration(String configurationId, String newConfigurationId)");
      }
    }
  }

  /**
   * Unregisters a policy configuration.
   * 
   * @param configurationId identifier of the policy configuration
   */
  public void unregisterPolicyConfiguration(String configurationId) {
    if (PolicyConfigurationLog.location.bePath()) {
      PolicyConfigurationLog.location.logT(Severity.PATH, "Entering PolicyConfigurations.unregisterPolicyConfiguration(String configurationId)");
    }

    if (PolicyConfigurationLog.location.beDebug()) {
      PolicyConfigurationLog.location.logT(Severity.DEBUG, "Trying to unregister policy configuration [{0}]", new Object[] { configurationId });
    }

    try {
      ModificationContextImpl modifications = (ModificationContextImpl) getModificationContext();
      modifications.beginModifications();
      
      try {
        Configuration store = modifications.getConfiguration(SecurityConfigurationPath.SECURITY_CONFIGURATIONS_PATH, true, false);
        String path = roots.removePolicyRoot(configurationId, store);
        if (path == null) {
          modifications.commitModifications();
          if (PolicyConfigurationLog.location.beDebug()) {
            PolicyConfigurationLog.location.logT(Severity.DEBUG, "Security context [{0}] does not exist.", new Object[] { configurationId });
          }
          return;
        }
        Configuration config = null;
        if (path.startsWith(SecurityConfigurationPath.SECURITY_CONFIGURATIONS_PATH) && path.endsWith(configurationId)) {
          config = store.getSubConfiguration(configurationId);
        } else {
          config = modifications.getConfiguration(path, true, false);
        }
        if (config == null) {
          modifications.commitModifications();
          return;
        }
        Configuration sub = null;
        try {
          sub = config.getSubConfiguration("security");
        } catch (Exception ee) {
          // $JL-EXC$
        }
        if (PolicyConfigurationLog.location.beInfo()) {
          PolicyConfigurationLog.location.logT(Severity.INFO, "Deleting security configuration [{0}]", new Object[] { config.getPath() });
        }
        if (path.endsWith("security") || (path.startsWith(SecurityConfigurationPath.SECURITY_CONFIGURATIONS_PATH) && path.endsWith(configurationId))
            || (path.startsWith(SecurityConfigurationPath.CUSTOM_POLICY_CONFIGURATION_PATH) && path.endsWith(configurationId))) {
          config.deleteConfiguration();
          modifications.commitModifications();
          configurations.remove(configurationId);
          if (PolicyConfigurationLog.location.beDebug()) {
            PolicyConfigurationLog.location.logT(Severity.DEBUG, "Security context [{0}] removed from cache.", new Object[] { configurationId });
          }
        } else if (sub != null) {
          sub.deleteConfiguration();
          modifications.commitModifications();
          configurations.remove(configurationId);
          if (PolicyConfigurationLog.location.beDebug()) {
            PolicyConfigurationLog.location.logT(Severity.DEBUG, "Security context [{0}] removed from cache.", new Object[] { configurationId });
          }
        } else {
          modifications.rollbackModifications();
        }
      } catch (Exception e) {
        Util.SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "Exception occurred during unregistering of policy configuration [{0}].", new Object[] { configurationId }, e);
        modifications.rollbackModifications();
      }
      // da se iztrie bazovia configuration

      if (PolicyConfigurationLog.location.beInfo()) {
        PolicyConfigurationLog.location.logT(Severity.INFO, "Policy configuration [{0}] successfully unregistered.", new Object[] { configurationId });
      }
    } finally {
      if (PolicyConfigurationLog.location.bePath()) {
        PolicyConfigurationLog.location.logT(Severity.PATH, "Exiting PolicyConfigurations.unregisterPolicyConfiguration(String configurationId)");
      }
    }
  }
  
  /**
   * This method is being called for a policy configuration, that is being currently created and is possibly still
   * not persisted
   * 
   * @param configurationId
   * @return the corresponding path to the application security configuration
   */
  public abstract String getConfigurationPath(String configurationId);
  
  /**
   * This method is being called for a policy configuration for which registerPolicyConfiguration(configurationId) has been
   * called, although the configuration might not have been persisted in the data base. It is responsibility of the descendant 
   * classes to provide the return value from the memory.
   * 
   * @param configurationId
   * @return the corresponding path to the application security configuration
   */
  public String getRegisteredConfigurationPath(String configurationId) {
    return roots.getPolicyRoot(configurationId);
  }
  
  protected void init() {
    configurations.clear();
    configurations.put(SecurityContextImpl.J2EE_ENGINE_CONFIGURATION, this);

    if (PolicyConfigurationLog.location.beDebug()) {
      PolicyConfigurationLog.location.logT(Severity.DEBUG, "Security context [{0}] stored to cache.", new Object[] { SecurityContextImpl.J2EE_ENGINE_CONFIGURATION });
    }
    registerConfigurationListener("");

    Map map = roots.listPolicyRoots();
    Iterator keys = map.keySet().iterator();
    String key = null;
    String path = null;
    SecurityContext policy = null;
    while (keys.hasNext()) {
      key = (String) keys.next();
      path = (String) map.get(key);
      policy = loadSecurityContext(key, path, false);
      if (PolicyConfigurationLog.location.beDebug()) {
        PolicyConfigurationLog.location.logT(Severity.DEBUG, "Security context [{0}] successfully loaded from path [{1}].", new Object[] { key, path });
      }
      if (policy != null) {
        configurations.put(key, policy);
        if (PolicyConfigurationLog.location.beDebug()) {
          PolicyConfigurationLog.location.logT(Severity.DEBUG, "Security context [{0}] stored to cache.", new Object[] { key });
        }
        registerConfigurationListener(path);
      } else {
        if (isUnregisteredConfiguration(path)) {
          unregisterPolicyConfiguration(key);
        }
      }
    }

    if (PolicyConfigurationLog.location.beInfo()) {
      PolicyConfigurationLog.location.logT(Severity.INFO, "All security contexts are successfully initialized.");
    }
  }

  protected void delete() {
    Map map = roots.listPolicyRoots();
    Iterator iter = map.keySet().iterator();
    String key = null;
    String path = null;
    while (iter.hasNext()) {
      key = (String) iter.next();
      path = (String) map.get(key);
      SecurityContext policy = loadSecurityContext(key, path, false);
      if (PolicyConfigurationLog.location.beDebug()) {
        PolicyConfigurationLog.location.logT(Severity.DEBUG, "Security context [{0}] successfully loaded from path [{1}].", new Object[] { key, path });
      }
      if (policy == null) {
        configurations.remove(key);
        if (PolicyConfigurationLog.location.beDebug()) {
          PolicyConfigurationLog.location.logT(Severity.DEBUG, "Security context [{0}] removed from cache.", new Object[] { key });
        }
      }
    }
  }

  protected void modify(String path) {
    // if the structure of the security tree in configuration manager is
    // changed, this code must also be changed.
    if (path.startsWith(SecurityConfigurationPath.AUTHENTICATION_PATH) && path.lastIndexOf(SecurityConfigurationPath.SECURITY_PATH) == 0) {
      // it is 'SAP-J2EE-Engine' configuration path
      ((AuthenticationContextImpl) getAuthenticationContext()).update();
      // ((AuthorizationContextImpl) getAuthorizationContext()).update();
      if (PolicyConfigurationLog.location.beInfo()) {
        PolicyConfigurationLog.location.logT(Severity.INFO, "Authentication context for security context [{0}] updated.", new Object[] { SecurityContextImpl.J2EE_ENGINE_CONFIGURATION });
      }
    } else {
      Map map = roots.listPolicyRoots();
      if (!map.containsValue(path) && path.lastIndexOf(SecurityConfigurationPath.SECURITY_PATH) > 0) {
        path = path.substring(0, (path.lastIndexOf(SecurityConfigurationPath.SECURITY_PATH) - 1));
      }

      if (map.containsValue(path)) {
        Iterator keys = map.keySet().iterator();

        while (keys.hasNext()) {
          Object key = keys.next();
          Object value = map.get(key);

          if ((value != null) && value.equals(path)) {
            SecurityContext securityContext = getPolicyConfigurationContext((String) key);
            if (securityContext != null) {
              ((AuthenticationContextImpl) securityContext.getAuthenticationContext()).update();
              if (PolicyConfigurationLog.location.beInfo()) {
                PolicyConfigurationLog.location.logT(Severity.INFO, "Authentication context for security context [{0}] updated.", new Object[] { key });
              }
              // ((AuthorizationContextImpl)
              // securityContext.getAuthorizationContext()).update();
            }
            break;
          }
        }
      }
    }
  }

  private SecurityContext loadSecurityContext(String key, String path, boolean createIt) {
    ModificationContextImpl modifications = (ModificationContextImpl) this.getModificationContext();
    modifications.beginModifications();

    try {
      if ((createIt) && (modifications.storage instanceof DeployStorage)) {
        createIt = false;
      }

      if (modifications.getConfiguration(path + "/" + SecurityConfigurationPath.SECURITY_PATH, false, createIt) == null) {
        if (PolicyConfigurationLog.location.beDebug()) {
          PolicyConfigurationLog.location.logT(Severity.DEBUG, "Configuration [{0}] returned from storage is null.", new Object[] { path });
        }
        return null;
      }

    } catch (Exception e) {
      SimpleLogger.traceThrowable(Severity.ERROR, PolicyConfigurationLog.location, e, "ASJ.secsrv.000175", "Cannot get configuration [{0}] from storage.", new Object[] { path });

      if (!(e instanceof StorageLockedException)) {
        return null;
      }
    } finally {
      try {
        if (createIt) {
          modifications.commitModifications();
        } else {
          modifications.forgetModifications();
        }
      } catch (Exception e) {
        Util.SEC_SRV_LOCATION.traceThrowableT(Severity.INFO, "loadSecurityContext", e);
      }
    }

    return new PolicyConfigurationSecurityContext(this, key, path);
  }

  /**
   * Retrieve a context for updating the security policy of deployed instances
   * of components.
   * 
   * @return upgrade security context
   */
  public UpdateSecurityContext getUpdateSecurityContext() {
    return new UpdateSecurityContextImpl();
  }

  private boolean isUnregisteredConfiguration(String path) {
    ModificationContextImpl modifications = (ModificationContextImpl) this.getModificationContext();
    modifications.beginModifications();
    try {
      if (modifications.getConfiguration(path, false, false) == null) {
        if (PolicyConfigurationLog.location.beDebug()) {
          PolicyConfigurationLog.location.debugT("Configuration [{0}] does not exist - it is unregistered", new Object[] {path});
        }
        
        return true;
      }
      
      if (PolicyConfigurationLog.location.beDebug()) {
        PolicyConfigurationLog.location.debugT("Configuration [{0}] exists - it is not unregistered", new Object[] {path});
      }
    } catch (Exception se) {
      if (PolicyConfigurationLog.location.beWarning()) {
        PolicyConfigurationLog.location.traceThrowableT(Severity.WARNING, "Cannot load policy configuration [{0}] from storage.", new Object[] { path }, se);
      }
      return false;
    } finally {
      modifications.forgetModifications();
    }
    return false;
  }

  public static boolean isValidConfigurationType(byte type) {
    return (type == SecurityContext.TYPE_CUSTOM || type == SecurityContext.TYPE_OTHER || type == SecurityContext.TYPE_EJB_COMPONENT || type == SecurityContext.TYPE_SERVICE
        || type == SecurityContext.TYPE_TEMPLATE || type == SecurityContext.TYPE_WEB_COMPONENT || type == SecurityContext.TYPE_WEB_SERVICE || 
        type == SecurityContext.TYPE_AUTHSCHEME || type == SecurityContext.TYPE_AUTHSCHEME_REFERENCE );
  }
}
