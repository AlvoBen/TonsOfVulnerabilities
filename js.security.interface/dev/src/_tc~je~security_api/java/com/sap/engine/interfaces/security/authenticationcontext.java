/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.interfaces.security;

import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.LoginContext;

import com.sap.engine.interfaces.security.userstore.UserStore;

/**
 *  Context of the J2EE Engine or a deployed instance of a component that
 * gives access to the configuration of login modules and user store target.
 *
 * @author  Stephan Zlatarev
 * @version 6.30
 *
 * @see com.sap.engine.interfaces.security.SecurityContext
 */
public interface AuthenticationContext {
  
  public static final String SECURITY_POLICY_DOMAIN_PROPERTY = "policy_domain";
  
  /**
   *  Retrieves the target user store for a component or the active user store
   * for J2EE Engine
   *
   * @return  a user store instance
   *
   * @throws SecurityException   if the operation cannot be completed.
   */
  public UserStore getAuthenticationUserStore() throws SecurityException;


  /**
   *  Retrieves the configuration of login modules for this authentication context.
   *
   * @return  an array of login module configurations.
   *
   * @throws SecurityException   if the operation cannot be completed.
   */
  public AppConfigurationEntry[] getLoginModules() throws SecurityException;


  /**
   *  Retrieves the configuration name used as template for the authentication part.
   *
   * @return  the name of a valid policy configuration or null if no template is used.
   *
   * @throws SecurityException   if the operation cannot be completed.
   */
  public String getTemplate() throws SecurityException;


  /**
   *  Returns an instance login context configured with the given subject
   * and callback handler. The login context is ready to use.
   *
   * @param  subject  optional ( can be null ).
   * @param  handler  mandatory handler for callbacks.
   *
   * @return  a configured instance of login context.
   *
   * @throws SecurityException   if the operation cannot be completed.
   */
  public LoginContext getLoginContext(Subject subject, CallbackHandler handler) throws SecurityException;
  
  /**
   * Checks whether authentication has to be enforced for the current context.
   * @deprecated Use isAuthenticatedInPolicyDomain method instead.
   * @return true, if authentication has to be enforced.
   */
  public boolean isLoginNeeded();
  
  /**
   * Checks whether authentication has already been done for the current security policy domain
   * 
   * @return true, if authentication has been done.
   */
  public boolean isAuthenticatedInPolicyDomain();
  
  /**
   * Checks if an application should extend its session on the identity provider
   * @param  applicationSessionTimeout specifies the application session timeout
   * @return true, session prolongation has to be done
   */
  public boolean isSessionProlongationNeeded(int applicationSessionTimeout);
  
  /**
   *  Retireve the name of the policy configuration.
   *
   * @return  the name of the policy configuration.
   */
  public String getPolicyConfigurationName();


  /**
   *  Retrieves the property with the given name.
   *
   * @param  name  the name of the desired property
   *
   * @return  the value of the property. null if it does not exist
   *
   * @throws SecurityException   if the value cannot be obtained.
   */
  public String getProperty(String name) throws SecurityException;

  /**
   *  Retrieves the all properties of the authentication context.
   *
   * @return Map of all properties. null if no properties exist
   *
   * @throws SecurityException   if properties cannot be obtained.
   */
  public Map getProperties() throws SecurityException;

  
  /**
   *  Changes the target user store for a component or the active user store
   * for J2EE Engine
   *
   * @param  userStore  user store instance
   *
   * @throws SecurityException   if the operation cannot be completed.
   */
  public void setAuthenticationUserStore(UserStore userStore) throws SecurityException;


  /**
   *  Changes the configuration of login modules for this AuthenticationContext.
   *
   * @param  modules  an array of login module configurations.
   *
   * @throws SecurityException   if the operation cannot be completed.
   */
  public void setLoginModules(AppConfigurationEntry[] modules) throws SecurityException;

  /**
   *  Changes the configuration of login modules for this AuthenticationContext and non active userstore.
   *
   * @param  modules  an array of login module configurations.
   * @param  userstore the name of a userstore.
   *
   * @throws SecurityException   if the operation cannot be completed.
   */
  public void setLoginModules(String userstore, AppConfigurationEntry[] modules) throws SecurityException;


  /**
   *  Changes the configuration of login modules in effect.
   *
   * @param  template  name of login context template, e.g. BASIC, CLIENT_CERT, ...
   *
   * @throws SecurityException   if the operation cannot be completed.
   */
  public void setLoginModules(String template) throws SecurityException;


  /**
   *  Sets the value of the property with the given key.
   *
   * @param key   the key
   * @param value the new value
   *
   * @throws  SecurityException  if the value of the key cannot be set.
   */
  public void setProperty(String key, String value) throws SecurityException;

}

