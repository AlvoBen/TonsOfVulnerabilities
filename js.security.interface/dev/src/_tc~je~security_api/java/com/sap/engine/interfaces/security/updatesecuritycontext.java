package com.sap.engine.interfaces.security;

import javax.security.auth.login.AppConfigurationEntry;

import org.w3c.dom.NodeList;

/**
 * Context for updating the security policy of deployed instances of components.
 *
 * @author  Ekaterina Zheleva
 * @version 6.40
 */
public interface UpdateSecurityContext {

  /**
   * The method is used to associate a context for security configuration of a deployed component
   * to this context for upgrading the security policy.
   *
   * @param ctx the policy configuration context of a deployed component
   */
  public void setSecurityContext(SecurityContext ctx);

  /**
   * The method is used to set the necessary data from the primary descriptor xml of the application,
   * describing the security roles.
   *
   * @param defaultSourceRoles the NodeList, that is the result from retrieving the elements
   *        by tag name <security-role>
   */
  public void setDefaultSourceRoles(NodeList defaultSourceRoles);

  /**
   * The method is used to set the necessary data from the primary additional descriptor xml of the application,
   * describing the security role mappings.
   *
   * @param defaultSourceRoleMappings the NodeList, that is the result from retrieving the elements
   *        by tag name <security-role-map>
   */
  public void setDefaultSourceRoleMappings(NodeList defaultSourceRoleMappings);

   /**
   * The method is used to set the necessary data from the updated descriptor xml of the application,
   * describing the security roles.
   *
   * @param defaultTargetRoles the NodeList, that is the result from retrieving the elements
   *        by tag name <security-role>
   */
  public void setDefaultTargetRoles(NodeList defaultTargetRoles);

  /**
   * The method is used to set the necessary data from the updated additional descriptor xml of the application,
   * describing the security role mappings.
   *
   * @param defaultTargetRoleMappings the NodeList, that is the result from retrieving the elements
   *        by tag name <security-role-map>
   */
  public void setDefaultTargetRoleMappings(NodeList defaultTargetRoleMappings);

  /**
   * The method is used to set the necessary data from the primary descriptor xml of the application,
   * describing the authentication stack.
   * 
   * @param list the NodeList, that is the result from retrieving the elements
   *        by tag name <login-module>
   * @deprecated - use {@link #setDefaultSourceAuthenticationStack(AppConfigurationEntry[])} instead
   */
  public void setDefaultSourceAuthenticationStack(NodeList list);

  /**
   * The method is used to set the necessary data from the updated descriptor xml of the application,
   * describing the authentication stack.
   *
   * @param list the NodeList, that is the result from retrieving the elements
   *        by tag name <login-module>
   * @deprecated - use {@link #setDefaultTargetAuthenticationStack(AppConfigurationEntry[])} instead
   */
  public void setDefaultTargetAuthenticationStack(NodeList list);

  /**
   * The method is used to set the necessary data from the primary descriptor xml of the application,
   * describing the authentication stack.
   *
   * @param entries the AppConfigurationEntry array, that is the result from retrieving the elements
   *        by tag name <login-module>
   */
  public void setDefaultSourceAuthenticationStack(AppConfigurationEntry[] entries);

  /**
   * The method is used to set the necessary data from the primary descriptor xml of the application,
   * describing the authentication method.
   *
   * @param template - the result from retrieving the element by tag name <auth-method>
   */
  public void setDefaultSourceAuthenticationTemplate(String template);
  
  /**
   * The method is used to set the authentication stack properties based on the previous 
   * application deploy.
   *
   * @param key   the key
   * @param value the new value
   */
  public void setDefaultSourceAuthenticationProperty(String key, String value);
  
  /**
   * The method is used to set the necessary data from the updated descriptor xml of the application,
   * describing the authentication stack.
   *
   * @param entries the AppConfigurationEntry array, that is the result from retrieving the elements
   *        by tag name <login-module>
   */
  public void setDefaultTargetAuthenticationStack(AppConfigurationEntry[] entries);
  
  /**
   * The method is used to set the necessary data from the updated descriptor xml of the application,
   * describing the authentication method.
   *
   * @param template - the result from retrieving the element by tag name <auth-method>
   */
  public void setDefaultTargetAuthenticationTemplate(String template);
  
  /**
   * The method is used to set the authentication stack properties based on the current 
   * application deploy.
   *
   * @param key   the key
   * @param value the new value
   */
  public void setDefaultTargetAuthenticationProperty(String key, String value);
  
  /**
   * This method performs the update of the deployed component's
   * security authentication context.
   */
  public void updateAuthentication();

  /**
   * This method performs the update of the deployed component's
   * security authorization context.
   */
  public void updateAuthorization();
  
  public ApplicationSecurityConfigurationAccessor getApplicationSecurityConfigigurationAccessor(String applicationName);
}
