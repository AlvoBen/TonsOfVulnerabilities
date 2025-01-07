
package com.sap.engine.services.security.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.sap.engine.interfaces.security.AuthenticationContext;
import com.sap.engine.interfaces.security.AuthorizationContext;
import com.sap.engine.interfaces.security.CryptographyContext;
import com.sap.engine.interfaces.security.JACCContext;
import com.sap.engine.interfaces.security.JACCSecurityRoleMappingContext;
import com.sap.engine.interfaces.security.ModificationContext;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.security.userstore.UserStoreFactory;
import com.sap.engine.services.security.server.deploy.PolicyConfigurationContainer;
import com.sap.tc.logging.Severity;

public class DeploySecurityContext extends PolicyConfigurations {
  
  private static ThreadLocal<Collection<PolicyConfigurationRoot>> policyConfigurationRoots = new ThreadLocal<Collection<PolicyConfigurationRoot>>();
  
  private ModificationContextImpl modification = null;

  private String path = null;

  private Map<String, String> registered = new HashMap<String, String>();
  
  public DeploySecurityContext(ModificationContextImpl modification, String path) {
    super();
    this.modification = modification;
    this.path = path;

    if (PolicyConfigurationLog.location.beDebug()) {
      PolicyConfigurationLog.location.logT(Severity.DEBUG, "New deploy security context successfully created for configuration with path [{0}].", new Object[] { path });
    }
  }

  /**
   * Retireve a context for managing authentication configuration.
   * 
   * @return authentication context
   */
  public AuthenticationContext getAuthenticationContext() {
    return modification.getOwner().getAuthenticationContext();
  }

  /**
   * Retireve a context for managing authorization configuration.
   * 
   * @return authorization context
   */
  public AuthorizationContext getAuthorizationContext() {
    return modification.getOwner().getAuthorizationContext();
  }

  /**
   * Retireve a context for access to configured cryptography modules.
   * 
   * @return cryptography context
   */
  public CryptographyContext getCryptographyContext() {
    return modification.getOwner().getCryptographyContext();
  }

  /**
   * Retireve the name of this security context.
   * 
   * @return the name of the security context.
   */
  public String getPolicyConfigurationName() {
    return modification.getOwner().getPolicyConfigurationName();
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
    return modification.getOwner().getPolicyConfigurationType();
  }

  public ModificationContext getModificationContext() {
    return modification;
  }

  /**
   * Retireve a context for managing user stores.
   * 
   * @return user store context
   */
  public UserStoreFactory getUserStoreContext() {
    return modification.getOwner().getUserStoreContext();
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.security.server.PolicyConfigurations#getConfigurationPath(java.lang.String)
   */
  public String getConfigurationPath(String configurationId) {
    return path;
  }
  
  /* (non-Javadoc)
   * @see com.sap.engine.services.security.server.PolicyConfigurations#getRegisteredConfigurationPath(java.lang.String)
   */
  public String getRegisteredConfigurationPath(String configurationId) {
    if (registered.containsKey(configurationId)) {
      return registered.get(configurationId);
    }
    
    return super.getRegisteredConfigurationPath(configurationId);
  }
  
  public SecurityContext getPolicyConfigurationContext(String configId) {
    if (configId.equals(getPolicyConfigurationName())) {
      return modification.getOwner();
    }
    
    if (registered.containsKey(configId)) {
      return new PolicyConfigurationSecurityContext(this, configId, registered.get(configId));
    }
    
    return super.getPolicyConfigurationContext(configId, false);
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
    addPolicyConfigurationRoot(new PolicyConfigurationRoot(configurationId, path, configurationType, this));
    registered.put(configurationId, path);
    
    if (!PolicyConfigurationContainer.isParallelDeployment()) {
      PolicyConfigurationContainer.registerPolicyConfigurationRoots();
      clearPolicyConfigurationRoots();
    }
  }

  public void unregisterPolicyConfiguration(String configurationId) {
    super.unregisterPolicyConfiguration(configurationId);
    registered.remove(configurationId);
  }

  /**
   * Sets the specified name for this security context.
   * 
   * @return the name of the security context.
   */
  public void setPolicyConfigurationName(String newConfigurationId) {
    String configurationId = getPolicyConfigurationName();
    super.renamePolicyConfiguration(configurationId, newConfigurationId);
    registered.remove(configurationId);
    registered.put(newConfigurationId, path);
  }

  /**
   * Retireve a context for managing authentication configuration.
   * 
   * @return JACCSecurityRoleMappingContext
   */
  public JACCSecurityRoleMappingContext getJACCSecurityRoleMappingContext() {
    return modification.getOwner().getJACCSecurityRoleMappingContext();
  }

  public JACCContext getJACCContext(String policyConfiguration) {
    return modification.getOwner().getJACCContext(policyConfiguration);
  }

  
  /**
   * @return the policyConfigurations
   */
  public static Collection<PolicyConfigurationRoot> getPolicyConfigurationRoots() {
    return policyConfigurationRoots.get();
  }
  
  public static void clearPolicyConfigurationRoots() {
    policyConfigurationRoots.remove();
  }
  
  private static void addPolicyConfigurationRoot(PolicyConfigurationRoot pc) {
    Collection<PolicyConfigurationRoot> roots = policyConfigurationRoots.get();
    
    if (roots == null) {
      roots = new HashSet<PolicyConfigurationRoot>();
      policyConfigurationRoots.set(roots);
    }
    
    roots.add(pc);
  }
}
