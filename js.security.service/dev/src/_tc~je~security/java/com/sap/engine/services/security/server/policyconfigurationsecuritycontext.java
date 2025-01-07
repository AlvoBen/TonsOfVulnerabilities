/**
 * Property of SAP AG, Walldorf (c) Copyright SAP AG, Walldorf, 2000-2002. All
 * rights reserved.
 */

package com.sap.engine.services.security.server;

import com.sap.engine.interfaces.security.userstore.UserStoreFactory;
import com.sap.engine.interfaces.security.*;
import com.sap.tc.logging.Severity;

/**
 * The root context for security in a deployed component in J2EE Engine.
 * 
 * @author Stephan Zlatarev
 * @version 6.30
 */
public class PolicyConfigurationSecurityContext implements SecurityContext {

  private String name;

  private byte type = TYPE_INVALID;

  private String configurationPath;

  private SecurityContext root;

  private AuthenticationContext authentication;

  private AuthorizationContext authorization;

  private CryptographyContext cryptography;

  /**
   * Constructs PolicyConfigurationSecurityContext instance
   * @param root
   * @param name - is policy configuration name
   * @param configurationPath - is a path which will be used to locate the policy configuration in the database. 
   * This parameter might accept null values
   */
  public PolicyConfigurationSecurityContext( SecurityContext root, String name, String configurationPath) {
    this.name = name;
    this.root = root;
    this.configurationPath = configurationPath;

    if (PolicyConfigurationLog.location.beDebug()) {
      PolicyConfigurationLog.location.logT(Severity.DEBUG, "New policy configuration security context successfully created for configuration [{0}] with path [{1}].", new Object[] { name,
          configurationPath });
    }
  }

  /**
   * Retireve a context for managing authentication configuration.
   * 
   * @return authentication context
   */
  public AuthenticationContext getAuthenticationContext() {
    if (PolicyConfigurationLog.location.bePath()) {
      PolicyConfigurationLog.location.logT(Severity.PATH, "Entering PolicyConfigurationSecurityContext.getAuthenticationContext()");
    }
    try {
      if (authentication == null) {
        synchronized (this) {
          if (authentication == null) {
            authentication = new AuthenticationContextImpl(name, this, configurationPath);
            ((AuthenticationContextImpl) authentication).update();
            if (PolicyConfigurationLog.location.beDebug()) {
              PolicyConfigurationLog.location.logT(Severity.DEBUG, "AuthenticationContext successfully created for configuration [{0}] and configuration path [{1}].", new Object[] {name, configurationPath});
            }
          }
        }
      }
      return authentication;
    } finally {
      if (PolicyConfigurationLog.location.bePath()) {
        PolicyConfigurationLog.location.logT(Severity.PATH, "Exiting PolicyConfigurationSecurityContext.getAuthenticationContext()");
      }
    }
  }

  /**
   * Retireve a context for managing authorization configuration.
   * 
   * @return authorization context
   */
  public AuthorizationContext getAuthorizationContext() {
    if (PolicyConfigurationLog.location.bePath()) {
      PolicyConfigurationLog.location.logT(Severity.PATH, "Entering PolicyConfigurationSecurityContext.getAuthorizationContext()");
    }
    try {
      if (authorization == null) {
        synchronized (this) {
          if (authorization == null) {
            authorization = new AuthorizationContextImpl(name, this);
            ((AuthorizationContextImpl) authorization).update();
            if (PolicyConfigurationLog.location.beDebug()) {
              PolicyConfigurationLog.location.logT(Severity.DEBUG, "AuthorizationContext successfully created for configuration [{0}].", new Object[] {name});
            }
          }
        }
      }
      return authorization;
    } finally {
      if (PolicyConfigurationLog.location.bePath()) {
        PolicyConfigurationLog.location.logT(Severity.PATH, "Exiting PolicyConfigurationSecurityContext.getAuthorizationContext()");
      }
    }
  }

  /**
   * Retireve a context for access to configured cryptography modules.
   * 
   * @return cryptography context
   */
  public CryptographyContext getCryptographyContext() {
    if (cryptography == null) {
      synchronized (this) {
        if (cryptography == null) {
          cryptography = new CryptographyContextImpl(name, root);
        }
      }
    }
    return cryptography;
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
    return root.getPolicyConfigurationContext(configurationId);
  }

  /**
   * Retireve the name of this security context.
   * 
   * @return the name of the security context.
   */
  public String getPolicyConfigurationName() {
    return name;
  }

  /**
   * Retireve the type of this security context. In case a valid type cannot be
   * retrieved this method returns <code>SecurityContext.TYPE_INVALID</code>
   * 
   * @return the type of the security context. Valid type values are all policy
   *         configuration type constants defined in the SecurityContext class
   *         with the exception of <code>SecurityContext.TYPE_INVALID</code>.
   */
  public byte getPolicyConfigurationType() {
    if ((type == TYPE_INVALID) && (root instanceof PolicyConfigurations)) {
      type = ((PolicyConfigurations) root).getPolicyConfigurationType(name);
    }
    return type;
  }

  /**
   * Retireve a context for bundling of modification operations.
   * 
   * @return modification bundling context
   */
  public ModificationContext getModificationContext() {
    return new PolicyConfigurationModificationContextImpl(root, (ModificationContextImpl) root.getModificationContext(), configurationPath);
  }

  /**
   * Retireve a context for managing user stores.
   * 
   * @return user store context
   */
  public UserStoreFactory getUserStoreContext() {
    return root.getUserStoreContext();
  }

  /**
   * Lists all registered policy configurations.
   * 
   * @return the String identifiers of the policy configurations.
   * 
   * @see PolicyConfigurationSecurityContext :: registerPolicyConfiguration.
   */
  public String[] listPolicyConfigurations() {
    return root.listPolicyConfigurations();
  }

  /**
   * @deprecated use registerPolicyConfiguration(String configurationId, byte
   *             configurationType) instead
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
    root.registerPolicyConfiguration(configurationId, configurationType);
  }

  /**
   * Sets the specified name for this security context.
   * 
   * @return the name of the security context.
   */
  public void setPolicyConfigurationName(String newConfigurationId) {
    String configurationId = getPolicyConfigurationName();
    if (root instanceof PolicyConfigurations) {
      ((PolicyConfigurations) root).renamePolicyConfiguration(configurationId, newConfigurationId);
    }
  }

  /**
   * Sets the specified type of a security policy configuration.
   * 
   * @param type of the security context. Valid type values are all policy
   *        configuration type constants defined in the SecurityContext class
   *        with the exception of <code>SecurityContext.TYPE_INVALID</code>.
   */
  public void setPolicyConfigurationType(byte configurationType) {
    String configurationId = getPolicyConfigurationName();
    if (root instanceof PolicyConfigurations) {
      ((PolicyConfigurations) root).setPolicyConfigurationType(configurationId, configurationType);
    }
  }

  /**
   * Unregisters a policy configuration.
   * 
   * @param configurationId identifier of the policy configuration
   */
  public void unregisterPolicyConfiguration(String configurationId) {
    root.unregisterPolicyConfiguration(configurationId);
  }

  public boolean isTemporary() {
    return ((ModificationContextImpl) root.getModificationContext()).reusable;
  }

  /**
   * Retrieve a context for updating the security policy of deployed instances
   * of components.
   * 
   * @return upgrade security context
   */
  public UpdateSecurityContext getUpdateSecurityContext() {
    return root.getUpdateSecurityContext();
  }

  /**
   * Retireve a context for managing authentication configuration.
   * 
   * @return JACCSecurityRoleMappingContext
   */
  public JACCSecurityRoleMappingContext getJACCSecurityRoleMappingContext() {
    return root.getJACCSecurityRoleMappingContext();
  }

  public JACCContext getJACCContext(String policyConfigruation) {
    return root.getJACCContext(policyConfigruation);
  }
}
