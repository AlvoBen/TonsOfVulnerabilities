/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.interfaces.security;

import com.sap.engine.interfaces.security.userstore.UserStoreFactory;

/**
 *  The root context for security in J2EE Engine.
 *
 * @author  Stephan Zlatarev
 * @version 6.30
 */
public interface SecurityContext {

  /**
   *  Name of root policy configuration. Value is "SAP-J2EE-Engine".
   */
  public final static String ROOT_POLICY_CONFIGURATION = "SAP-J2EE-Engine";

  /**
   *  Constant for invalid policy configuration type. //used for checking 
   */
  public final static byte TYPE_INVALID = 0;
  
  /**
   *  Constant for all policy configuration types that 
   *  (1) do not belong to any of the other types, 
   *  or (2) have not been successfully migrated.
   */
  public final static byte TYPE_OTHER = 1;
  
  /**
   *  Constant for all standard policy configurations, 
   *  for example basic, form, client-cert, etc.
   */
  public final static byte TYPE_TEMPLATE = 2;
  
  /**
   *  Constant for the policy configuration type of a custom created policy configuration.
   */
  public final static byte TYPE_CUSTOM = 3;
  
  /**
   *  Constant for the policy configuration type of a service. 
   *  // such as service.iiop, service.telnet, etc. 
   */
  public final static byte TYPE_SERVICE = 4;
  
  /**
   *  Constant for policy configuration type of  a web services component.
   */
  public final static byte TYPE_WEB_SERVICE = 5;
  
  /**
   *  Constant for the policy configuration type of a web component.
   */
  public final static byte TYPE_WEB_COMPONENT = 6;
  
  /**
   *  Constant for the policy configuration type of an ejb component.
   */
  public final static byte TYPE_EJB_COMPONENT = 7;

  
  /**
   *  Constant for the policy configuration type of a authscheme component.
   */
  public final static byte TYPE_AUTHSCHEME = 8;
  
  /**
   *  Constant for the policy configuration type of an authscheme reference component.
   */
  public final static byte TYPE_AUTHSCHEME_REFERENCE = 9;
  
  /**
   *  Retireve a context for managing authentication configuration.
   *
   * @return  authentication context
   */
  public AuthenticationContext getAuthenticationContext();

  /**
   *  Retireve a context for managing authorization configuration.
   *
   * @return  authorization context
   */
  public AuthorizationContext getAuthorizationContext();

  /**
   *  Retireve a context for access to configured cryptography modules.
   *
   * @deprecated Cryptography context is deprecated since NW04 AS Java. Use Secure Store service or
   *      the encryption of Configuration Manager API instead.
   * @return  cryptography context
   */
  public CryptographyContext getCryptographyContext();

  /**
   *  Retireve a context for security configuration of a deployed component.
   * Note that policy configurations are not hierarhical.
   *
   * @param  configurationId  string identifier of the deployed instance of
   *                           the component.
   *
   * @return  security context of a deployed instance of a component.
   */
  public SecurityContext getPolicyConfigurationContext(String configurationId);

  /**
   *  Retireve the name of this security context.
   *
   * @return  the name of the security context.
   */
  public String getPolicyConfigurationName();
  
  /**
   *  Retireve the type of this security context.
   * In case a valid type cannot be retrieved 
   * this method returns <code>SecurityContext.TYPE_INVALID</code>
   * 
   * @return  the type of the security context.
   * Valid type values are all policy configuration type constants 
   * defined in the SecurityContext class with the exception of 
   * <code>SecurityContext.TYPE_INVALID</code>.
   */
  public byte getPolicyConfigurationType();
  
  /**
   * Sets the specified name for this security context.
   *
   * @param  name of the security context.
   */
  public void setPolicyConfigurationName(String name);

  /**
   * Sets the specified type of a security policy configuration. 
   * This method must not be called on the root security context. 
   *
   * @param  type of the security context.
   * Valid type values are all policy configuration type constants 
   * defined in the SecurityContext class with the exception of 
   * <code>SecurityContext.TYPE_INVALID</code>.
   */
  public void setPolicyConfigurationType(byte configType);

  /**
   *  Retireve a context for bundling of modification operations.
   *
   * @return  modification bundling context
   */
  public ModificationContext getModificationContext();

  /**
   *  Retireve a context for managing user stores.
   *
   * @return  user store context
   */
  public UserStoreFactory getUserStoreContext();

  /**
   *  Retrieve a context for updating the security policy of deployed instances of components.
   *
   * @return  upgrade security context
   */
  public UpdateSecurityContext getUpdateSecurityContext();

  /**
   *  Retrieve a context for mapping of the jacc roles to ume roles.
   *
   * @return  jacc security role mapping context
   */  
  public JACCSecurityRoleMappingContext getJACCSecurityRoleMappingContext();  
 
  public JACCContext getJACCContext(String policyConfiguration);
  /**
   *  Lists all registered policy configurations.
   *
   * @return  the String identifiers of the policy configurations.
   */
  public String[] listPolicyConfigurations();

  /**
   * @deprecated 
   * use registerPolicyConfiguration(String configurationId, byte configurationType) instead
   **/
  public void registerPolicyConfiguration(String configurationId);

  /**
   *  Registers a new policy configuration of specified type. This could represent a J2EE
   * application, J2EE component or JCA resource adapter.
   *  Note that policy configurations are not hierarhical
   *
   * @param  configurationId  identifier of the component
   * @param  configurationType  type of the component
   * Valid type values are all policy configuration type constants 
   * defined in the SecurityContext class with the exception of 
   * <code>SecurityContext.TYPE_INVALID</code>.
   */
  public void registerPolicyConfiguration(String configurationId, byte configurationType);
  
  /**
   * 
   *  Unregisters a policy configuration.
   *
   * @param  configurationId  identifier of the policy configuration
   */
  public void unregisterPolicyConfiguration(String configurationId);

}