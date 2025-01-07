/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.services.security.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.LoginContext;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.thread.ThreadContext;
import com.sap.engine.interfaces.security.AuthenticationContext;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.interfaces.security.auth.WebCallbackHandler;
import com.sap.engine.interfaces.security.userstore.UserStore;
import com.sap.engine.interfaces.security.userstore.config.LoginModuleConfiguration;
import com.sap.engine.interfaces.security.userstore.config.UserStoreConfiguration;
import com.sap.engine.services.security.SecurityServerFrame;
import com.sap.engine.services.security.Util;
import com.sap.engine.services.security.login.ConfigEntrySerializator;
import com.sap.engine.services.security.login.LoginContextFactory;
import com.sap.engine.services.security.login.SecuritySessionPool;
import com.sap.engine.services.security.remoteimpl.RemoteSecurityImpl;
import com.sap.engine.services.security.restriction.Restrictions;
import com.sap.engine.services.security.server.deploy.LoginModuleContainer;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 *  Context of the J2EE Engine or a deployed instance of a component that
 * gives access to the configuration of login modules and user store target.
 *
 * @author  Svetlana Stancheva
 * @author  Stephan Zlatarev
 * @version 6.30
 */
public class AuthenticationContextImpl implements AuthenticationContext {

  public final static String TEMPLATE_ENTRY_KEY = "template";
  public final static String QUEUE_AND_RESUME = "WSSEC$QueueAndResume";
  private final static String USERSTORE_CHECK = "VerifyLoginModulesOnDeploy";
  private final static String CLIENT_CONTEXT_ATTRIBUTE_IDP_SESSION_EXPIRATION = "sap.com/idp_session_expiration";
  
  private static final Location LOCATION = Location.getLocation(AuthenticationTraces.LOGIN_CONTEXT_LOCATION);

  private String name;
  private boolean userStoreIsTheActiveOne = true;
  private UserStore userStore = null;
  private LoginContextFactory loginContextFactory;
  private SecurityContext root = null;
  private String policyDomain = null;
  private boolean resetPolicyDomainProperty = true;
  private String configurationPath = null;
  
  private static String[] authenticationProperties = null;
  private static int threadContextId = -1;

  static {
    authenticationProperties = new String[5];
    authenticationProperties[0] = SECURITY_POLICY_DOMAIN_PROPERTY;
    authenticationProperties[1] = WebCallbackHandler.FORM_LOGIN_PAGE;
    authenticationProperties[2] = WebCallbackHandler.FORM_ERROR_PAGE;
    authenticationProperties[3] = WebCallbackHandler.PASSWORD_CHANGE_LOGIN_PAGE;
    authenticationProperties[4] = WebCallbackHandler.PASSWORD_CHANGE_ERROR_PAGE;
  }

  protected AuthenticationContextImpl(String name, SecurityContext root, String configurationPath) {
    this.name = name;
    this.root = root;
    this.configurationPath = configurationPath;

    if (SecurityContextImpl.J2EE_ENGINE_CONFIGURATION.equals(name)) {
      ModificationContextImpl modifications = (ModificationContextImpl) root.getModificationContext();
      modifications.beginModifications();
      Configuration configuration = null;

      try {
        configuration = modifications.getConfiguration(getPath(false), false, false);
      } catch (Exception e) {
        /////
        //  ignore it see next if
        configuration = null;
      }

      if (configuration == null) {
        try {
          configuration = modifications.getConfiguration(getPath(false), true, true);
        } catch (Exception e) {
          if (LOCATION.beWarning()) {
            LOCATION.traceThrowableT(Severity.WARNING, e.getLocalizedMessage(), e);
          }
        }
      }

      modifications.commitModifications();
    }
  }

  /**
   *  Retrieves the target user store for a component or the active user store
   * for J2EE Engine
   *
   * @return  a user store instance
   */
  public UserStore getAuthenticationUserStore() throws SecurityException {
    if (userStore == null) {
      ModificationContextImpl modifications = (ModificationContextImpl) root.getModificationContext();
      modifications.beginModifications();
      Configuration configuration = null;
      
      try {
        configuration = modifications.getConfiguration(getPath(false), false, false);
        if (configuration != null) {
          String userStoreName = (String) configuration.getConfigEntry(name + ".userstore");
          userStore = root.getUserStoreContext().getUserStore(userStoreName);
          userStoreIsTheActiveOne = false;
        }
      } catch (Exception e) {
        // checked later
        userStore = null;
      } finally {
        modifications.forgetModifications();
      }
    }

    if (userStore == null) {
      userStore = root.getUserStoreContext().getActiveUserStore();
      userStoreIsTheActiveOne = true;
    }

    return userStore;
  }

  /**
   *  Retrieves the configuration of login modules for this authentication context.
   *
   * @return  an array of login module configurations.
   */
  public AppConfigurationEntry[] getLoginModules() throws SecurityException {
    if (QUEUE_AND_RESUME.equals(name)) {
      Map properties = new Properties();

      return new AppConfigurationEntry[] {
        new AppConfigurationEntry("com.sap.security.core.server.wssec.jaas.ResumeAsLoginModule", AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, properties),
      };
    }

    if ((SecurityServerFrame.isEmergencyMode() && SecurityContextImpl.J2EE_ENGINE_CONFIGURATION.equals(name)) || 
    		(RemoteSecurityImpl.DEFAULT_POLICY_CONFIGURATION.equals(name) && this.configurationPath == null)) {
      Map properties = new Properties();

      return new AppConfigurationEntry[] {
        new AppConfigurationEntry("com.sap.engine.services.security.server.jaas.BasicPasswordLoginModule", AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT, properties)
      };
    }

    String template = getTemplate();

    if ((template == null) || template.equals(name)) {
      ModificationContextImpl modifications = (ModificationContextImpl) root.getModificationContext();
      modifications.beginModifications();
      Configuration configuration = null;

      try {
        configuration = modifications.getConfiguration(getPath(true), false, false);
      } catch (Exception e) {
        // checked later
        configuration = null;
      }


      try {
        if (configuration != null) {          
          return getAppConfigurationEntries(null, configuration);          
        }
      } catch (Exception e) {
        LOCATION.traceThrowableT(Severity.DEBUG, "Cannot retrieve login modules.", e);
      } finally {
        modifications.rollbackModifications();
      }
      
      return new AppConfigurationEntry[0];
    } else {
      SecurityContext securityContext = root.getPolicyConfigurationContext(template);

      if (securityContext != null) {
        //Avoid stackoverflow exception              
        if(getLoginContextFactory().isCyclicDependency()){
          throw new SecurityException("Cyclic dependency has been found for policy configuration named: " + name);
        }        
        AuthenticationContext templateAuthentication = securityContext.getAuthenticationContext();
        return templateAuthentication.getLoginModules();
      } else {
        return new AppConfigurationEntry[0];
      }
    }
  }

  /**
   *  Retrieves the configuration of login modules for this authentication context.
   *
   * @return  an array of login module configurations.
   */
  public AppConfigurationEntry[] getLoginModules(String userStore) throws SecurityException {
    String template = getTemplate();
    
    if ((template == null) || template.equals(name)) {
      ModificationContextImpl modifications = (ModificationContextImpl) root.getModificationContext();
      modifications.beginModifications();
      Configuration configuration = null;
      
      try {
        configuration = modifications.getConfiguration(getPath(userStore), false, false);
      } catch (Exception e) {
        // checked later
        configuration = null;
      }

      try {
        if (configuration != null) {
          return getAppConfigurationEntries(userStore, configuration);
        }
      } catch (Exception e) {
        LOCATION.traceThrowableT(Severity.DEBUG, "Cannot retrieve login modules.", e);
      } finally {
        modifications.forgetModifications();
      }

      return new AppConfigurationEntry[0];
    } else {
      SecurityContext securityContext = root.getPolicyConfigurationContext(template);
      if(securityContext!=null){        
        if(getLoginContextFactory().isCyclicDependency()){
          throw new SecurityException("Cyclic dependency has been found for policy configuration named: " + name);
        }      
        AuthenticationContext templateAuthentication = securityContext.getAuthenticationContext();
        return ((AuthenticationContextImpl) templateAuthentication).getLoginModules(userStore);
      }
      else{
        return new AppConfigurationEntry[0];
      }
    }
  }

  /**
   *  Returns an instance login context configured with the given subject
   * and callback handler. The login context is ready to use.
   *
   * @param  subject  optional ( can be null ).
   * @param  handler  mandatory handler for callbacks.
   *
   * @return  a configured instance of login context.
   */
  public LoginContext getLoginContext(Subject subject, CallbackHandler handler) throws SecurityException {
    try {
      return getLoginContextFactory().getLoginContext(subject, handler);
    } catch (Exception e) {
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, e, "ASJ.secsrv.000172", "{0}", new Object[]{e.getLocalizedMessage()});
      return null;
    }
  }  

  /**
   * Checks whether authentication has to be enforced for the current context.
   * @deprecated Use isAuthenticatedInPolicyDomain method instead.
   * @return true, if authentication has to be enforced.
   */
  public boolean isLoginNeeded() {
    return !isAuthenticatedInPolicyDomain();
  }
  
  /**
   * Checks if an application should extend its session on the identity provider
   * @param  applicationSessionTimeout specifies the application session timeout
   * @return true, session prolongation has to be done
   */
  public boolean isSessionProlongationNeeded(int applicationSessionTimeout)
  {
    final String METHOD_NAME = "isSessionProlongationNeeded";
    if (LOCATION.bePath()) {
      LOCATION.entering(METHOD_NAME);
    }
    
    boolean prolongated = false;
    com.sap.engine.interfaces.security.SecuritySession currentSession = getCurrentSecuritySessionFromClientContext();
    if (currentSession != null) {
      try {
        com.sap.engine.services.security.login.SecuritySession securitySession = (com.sap.engine.services.security.login.SecuritySession) currentSession;
        Object result = securitySession.getAttribute(CLIENT_CONTEXT_ATTRIBUTE_IDP_SESSION_EXPIRATION);
        if (result != null) {
          long localSessionExpiration = System.currentTimeMillis() + (applicationSessionTimeout*1000);
          long idpSessionExpiration = (Long)result;
          prolongated = localSessionExpiration >= idpSessionExpiration;
          if (LOCATION.beDebug()) {
            LOCATION.debugT("Session prolongation check: localSessionExpiration [{0}] >= idpSessionExpiration [{1}]. Session prolongation needed: {2}", 
                new Object[] {localSessionExpiration, idpSessionExpiration, prolongated});
          }
        } else {
          if (LOCATION.beDebug()) {
            LOCATION.debugT("Attribute [{0}] not found in user context.", new Object[] {CLIENT_CONTEXT_ATTRIBUTE_IDP_SESSION_EXPIRATION});
          }
        }
      } catch (Exception e) {
        SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "ASJ.secsrv.000173", "Error getting user context attribute for session prolongation.", e);
      } 
    }
    
    if (LOCATION.bePath()) {
      LOCATION.exiting(METHOD_NAME, prolongated);
    }    
    return prolongated;
  }
  
  /**
   * Checks whether authentication is already done in the security policy domain
   * 
   * @return true, if authenticated
   */
  public boolean isAuthenticatedInPolicyDomain() {
    final String METHOD_NAME = "isAuthenticatedInPolicyDomain";
    if (LOCATION.beDebug()) {
      LOCATION.entering(METHOD_NAME);
    }
    
    boolean authenticated = false;    
    com.sap.engine.interfaces.security.SecuritySession currentSession = getCurrentSecuritySessionFromClientContext();
    com.sap.engine.services.security.login.SecurityContext context = getSecurityContext();
    if (currentSession != null && context != null) {
      String securityPolicyDomain = context.getSecurityPolicyDomain();      
      Set policyDomains = currentSession.getSecurityPolicyDomains();
      if (LOCATION.beDebug()) {
        LOCATION.debugT("Current application security policy domain: [{0}]. Set of already authenticated policy domains: [{1}]",  
            new Object[] {securityPolicyDomain, policyDomains.toString()});
      }
      authenticated = policyDomains.contains(securityPolicyDomain);
    } else {
      if (LOCATION.beDebug()) {
        LOCATION.debugT("Current security session is null.");
      }
    }
    
    if (LOCATION.beDebug()) {
      LOCATION.exiting(METHOD_NAME, new Boolean(authenticated));
    }    
    return authenticated;
  }
    
  private com.sap.engine.interfaces.security.SecuritySession getCurrentSecuritySessionFromClientContext() {
    com.sap.engine.services.security.login.SecurityContext context = getSecurityContext();
    if (context != null) {
      com.sap.engine.interfaces.security.SecuritySession currentSession = context.getSessionWithoutDomainCombiner();
      return currentSession;
    } 
    return null;
  }
  
  private com.sap.engine.services.security.login.SecurityContext getSecurityContext() {
    ThreadContext currentThreadContext = SecurityServerFrame.threadContext.getThreadContext();
    if (currentThreadContext != null) {
      if (threadContextId == -1) {
        threadContextId = currentThreadContext.getContextObjectId(com.sap.engine.services.security.login.SecurityContext.NAME);
      }
      try {
        com.sap.engine.services.security.login.SecurityContext context = 
          (com.sap.engine.services.security.login.SecurityContext) currentThreadContext.getContextObject(threadContextId);
        return context;
      } catch (Exception e) {
        SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, e, "ASJ.secsrv.000174", "{0}", new Object[]{e.getLocalizedMessage()});
      }
    }
    return null;
  }

  /**
   *  Retrieve the name of the policy configuration.
   *
   * @return  the name of the policy configuration.
   */
  public String getPolicyConfigurationName() {
    return name;
  }
  
  /**
   *  Retrieves the property with the given name.
   *
   * @param  name  the name of the desired property
   *
   * @return  the value of the property. null if it does not exist
   *
   * @throws SecurityException   if the value cannot be obtained.
   */
  public String getProperty(String name) throws SecurityException {
    
    String value = null;
    
    if (AuthenticationContext.SECURITY_POLICY_DOMAIN_PROPERTY.equals(name) && !resetPolicyDomainProperty) {
      return policyDomain;
    }
    
    ModificationContextImpl modifications = (ModificationContextImpl) root.getModificationContext();
    modifications.beginModifications();
    Configuration configuration = null;
    
    try {
      configuration = modifications.getConfiguration(getPath(false), false, false);
    } catch (Exception e) {
      // checked later
      configuration = null;
    }

    try {
      if (configuration != null) {        
        if (AuthenticationContext.SECURITY_POLICY_DOMAIN_PROPERTY.equals(name)) {
          policyDomain = getConfigEntry(configuration, name);
          
          if (policyDomain == null) {
            policyDomain = getPolicyConfigurationName();
            
            if (LOCATION.beDebug()) {
              LOCATION.debugT("Security policy domain retrieved from policy configuration name: " + policyDomain);
            }
          }
          
          resetPolicyDomainProperty = false;
          return policyDomain;
        }
        
        value = getConfigEntry(configuration, name);
      }
    } catch (Exception e) {
      value = null;
    } finally {
      modifications.forgetModifications();
    }
    
    //The bellow commented code is for PC's properties inheritance.
    //If you want to switch on this feature uncomment it.
    //Do the same for method: getProperties()
    /*
    if(value==null){
      //Get the value from parent template(s) if there are such.  
      String template = getTemplate();
      if(template!=null){        
        if(!getLoginContextFactory().isCyclicDependency()){
          SecurityContext secContext = root.getPolicyConfigurationContext(template);
          if(secContext!=null){
            AuthenticationContext authContextRef = secContext.getAuthenticationContext();
            if(authContextRef!=null){
              value = authContextRef.getProperty(name);
            }
          }       
        }            
      }      
    }
    */
    
    return value;    
  }
  
  private static String getConfigEntry(Configuration configuration, String name) throws ConfigurationException {
    return (String) (configuration.existsConfigEntry(name) ? configuration.getConfigEntry(name) : null);
  }

  public Map getProperties() throws SecurityException {
    
    Map properties = null;
    ModificationContextImpl modifications = (ModificationContextImpl) root.getModificationContext();
    modifications.beginModifications();
    Configuration configuration = null;
    
    try {
      configuration = modifications.getConfiguration(getPath(false), false, false);
    } catch (Exception e) {
      // checked later
      configuration = null;
    }

    try {
      if (configuration != null) {
        properties = configuration.getAllConfigEntries(); 
      }
    } catch (Exception e) {
      properties = null;
    } finally {
      modifications.forgetModifications();
    }    
    
    return properties;
    
    //The bellow commented code is for PC's properties inheritance.
    //If you want to switch on this feature uncomment it.
    //Do the same for method: getProperty()
    /*
    //Get the properties from all parent template(s) if there are such - inheritance feature
    Map parentProperties = null;
    String template = getTemplate();
    if(template!=null){        
      if(!getLoginContextFactory().isCyclicDependency()){
        SecurityContext secContext = root.getPolicyConfigurationContext(template);
        if(secContext!=null){
          AuthenticationContext authContextRef = secContext.getAuthenticationContext();
          if(authContextRef!=null){
            parentProperties = authContextRef.getProperties();
          }
        }       
      }            
    }
    
    //Ovewrite all parent properties with local one
    if(parentProperties!=null && properties!=null){
      parentProperties.putAll(properties);      
    }
    else{
      parentProperties = properties;
    }
    
    return parentProperties;
    */    
  }

  /**
   * Writes the specified property to the authentication configuration. If the value is null, then the specified property is deleted.
   *
   * @param key  the name of the property
   * @param value  the value of the property
   * @throws SecurityException  if the property cannot be set
   */
  public void setProperty(String key, String value) throws SecurityException {
    Restrictions.checkPermission(Restrictions.COMPONENT_AUTHENTICATION, Restrictions.RESTRICTION_SET_PROPERTY);

    ModificationContextImpl modifications = (ModificationContextImpl) root.getModificationContext();
    modifications.beginModifications();

    Configuration configuration = null;
    
    try {
      configuration = modifications.getConfiguration(getPath(false), true, true);
    } catch (Exception e) {
      if (LOCATION.beDebug()) {
        LOCATION.traceThrowableT(Severity.DEBUG, "Error getting configuration", e);
      }
    } 

    if (configuration == null) {
      modifications.rollbackModifications();
      return;
    }

    if (value != null) {
      if (TEMPLATE_ENTRY_KEY.equals(key) && "client-cert".equalsIgnoreCase(value)) {
        value = "client_cert";
      }

      for (int i = 0; i < authenticationProperties.length; i++) {
        if (authenticationProperties[0].equals(key)) {
          if (!value.startsWith("/")) {
            value = "/" + value;
          }

          break;
        }
      }
    }

    try {
      if (value != null) {
        if (configuration.existsConfigEntry(key)) {
          configuration.modifyConfigEntry(key, value);
        } else {
          configuration.addConfigEntry(key, value);
        }
      } else {
        if (configuration.existsConfigEntry(key)) {
          configuration.deleteConfigEntry(key);
        }
      }

      modifications.commitModifications();
    } catch (Exception e) {
      modifications.rollbackModifications();
      throw new SecurityException("Cannot set property " + key + " = " + name, e);
    }

    update();
  }

  /**
   *  Retrieves the configuration name used as template for the authentication part.
   *
   * @return  the name of a valid policy configuration or null if no template is used.
   *
   * @throws SecurityException   if the operation cannot be completed.
   */
  public String getTemplate() throws SecurityException {
    if (QUEUE_AND_RESUME.equals(name)) {
      return null;
    }

    ModificationContextImpl modifications = (ModificationContextImpl) root.getModificationContext();
    modifications.beginModifications();
    Configuration configuration = null;
    
    try {
      configuration = modifications.getConfiguration(getPath(false), false, false);
    } catch (Exception e) {
      // checked later
      configuration = null;
    }

    try {
      if (configuration != null) {
        return getConfigEntry(configuration, TEMPLATE_ENTRY_KEY);
      }
    } catch (Exception e) {
      return null;
    } finally {
      modifications.rollbackModifications();
    }

    return null;
  }

  /**
   *  Returns the security session pool used by authentication.
   *
   * @return a security session instance
   */
  public static SecuritySessionPool getSessionPool() {
    return SecuritySessionPool.getPool();
  }

  /**
   *  Updates authentication caches such as loaded login contexts.
   * The method is invoked on change of the active user store.
   */
  public void update() {
    synchronized (this) {
      if (loginContextFactory != null) {
        loginContextFactory.clearLoginModules();
      }

      if (userStoreIsTheActiveOne) {
        userStore = null;
        userStore = getAuthenticationUserStore();
      }
      policyDomain = null;
      resetPolicyDomainProperty = true;
    }
  }

  /**
   *  Changes the target user store for a component or the active user store
   * for J2EE Engine
   *
   * @param  userStore user store instance
   */
  public void setAuthenticationUserStore(UserStore userStore) throws SecurityException {
    Restrictions.checkPermission(Restrictions.COMPONENT_AUTHENTICATION, Restrictions.RESTRICTION_SET_AUTHENTICATION_USER_STORE);

    this.userStore = userStore;
    userStoreIsTheActiveOne = (userStore == null);//false;
    ModificationContextImpl modifications = (ModificationContextImpl) root.getModificationContext();
    modifications.beginModifications();
    
    try {
      Configuration configuration = modifications.getConfiguration(getPath(false), true, true);
      if (configuration != null) {
        String entryName = name + ".userstore";

        if (configuration.existsConfigEntry(entryName)) {
          configuration.deleteConfigEntry(entryName);
        }

        if (userStore != null) {
          configuration.addConfigEntry(entryName, userStore.getConfiguration().getName());
        }
        modifications.commitModifications();
      } else {
        modifications.rollbackModifications();
      }
    } catch (Exception e) {
      modifications.rollbackModifications();
      throw new SecurityException("Cannot set authentication user store to: " + name, e);
    }
  
    update();
  }

  /**
   *  Changes the configuration of login modules for this AuthenticationContext.
   *
   * @param  modules  an array of login module configurations.
   */
  public synchronized void setLoginModules(AppConfigurationEntry[] modules) throws SecurityException {
    Restrictions.checkPermission(Restrictions.COMPONENT_AUTHENTICATION, Restrictions.RESTRICTION_SET_LOGIN_MODULES);
    
    checkEntries(modules);

    String userStore = null;
    ModificationContextImpl modifications = (ModificationContextImpl) root.getModificationContext();
    modifications.beginModifications();
    
    try {
      boolean appAlreadyExist = isAppAlreadyExist(modifications);
      Configuration configuration = modifications.getConfiguration(getPath(false), true, true);
      if (configuration != null) {
        if (configuration.existsConfigEntry(TEMPLATE_ENTRY_KEY)) {
          configuration.deleteConfigEntry(TEMPLATE_ENTRY_KEY);
        }

        userStore = root.getUserStoreContext().getActiveUserStore().getConfiguration().getName();
        if (configuration.existsSubConfiguration(userStore)) {
          configuration = configuration.getSubConfiguration(userStore);
        } else {
          configuration = configuration.createSubConfiguration(userStore);
        }
        
        storeLoginModuleConfiguration(userStore, configuration, modules, appAlreadyExist);
        
        modifications.commitModifications();
      } else {
        modifications.rollbackModifications();
      }
    } catch (Exception e) {
      modifications.rollbackModifications();
      throw new SecurityException("Cannot change login modules of " + name, e);
    }

    update();
  }
  
  /**
   *  Changes the configuration of login modules for this AuthenticationContext without checking the entries.
   *
   * @param  modules  an array of login module configurations.
   */
  public synchronized void setLoginModulesNoCheck(AppConfigurationEntry[] modules) throws SecurityException {
    Restrictions.checkPermission(Restrictions.COMPONENT_AUTHENTICATION, Restrictions.RESTRICTION_SET_LOGIN_MODULES);
    
    String userStore = null;
    ModificationContextImpl modifications = (ModificationContextImpl) root.getModificationContext();
    modifications.beginModifications();
    
    boolean appAlreadyExist = isAppAlreadyExist(modifications);
    
    try {
      Configuration configuration = modifications.getConfiguration(getPath(false), true, true);
      if (configuration != null) {
        if (configuration.existsConfigEntry(TEMPLATE_ENTRY_KEY)) {
          configuration.deleteConfigEntry(TEMPLATE_ENTRY_KEY);
        }

        userStore = root.getUserStoreContext().getActiveUserStore().getConfiguration().getName();
        if (configuration.existsSubConfiguration(userStore)) {
          configuration = configuration.getSubConfiguration(userStore);
        } else {
          configuration = configuration.createSubConfiguration(userStore);
        }
        
        storeLoginModuleConfiguration(userStore, configuration, modules, appAlreadyExist);
        
        modifications.commitModifications();
      } else {
        modifications.rollbackModifications();
      }
    } catch (Exception e) {
      modifications.rollbackModifications();
      throw new SecurityException("Cannot change login modules of " + name, e);
    }

    update();
  }

  /**
   *  Changes the configuration of login modules for this AuthenticationContext.
   *
   * @param  userStore  the user store to authentication against.
   * @param  modules    an array of login module configurations.
   */
  public synchronized void setLoginModules(String userStore, AppConfigurationEntry[] modules) throws SecurityException {
    Restrictions.checkPermission(Restrictions.COMPONENT_AUTHENTICATION, Restrictions.RESTRICTION_SET_LOGIN_MODULES);
    
    checkEntries(modules, userStore);

    ModificationContextImpl modifications = (ModificationContextImpl) root.getModificationContext();
    modifications.beginModifications();
    
    try {
      boolean appAlreadyExist = isAppAlreadyExist(modifications);
    
      Configuration configuration = modifications.getConfiguration(getPath(false), true, true);
  
      if (configuration != null) {
        if (configuration.existsConfigEntry(TEMPLATE_ENTRY_KEY)) {
          configuration.deleteConfigEntry(TEMPLATE_ENTRY_KEY);
        }

        if (configuration.existsSubConfiguration(userStore)) {
          configuration = configuration.getSubConfiguration(userStore);
        } else {
          configuration = configuration.createSubConfiguration(userStore);
        }
        
        storeLoginModuleConfiguration(userStore, configuration, modules, appAlreadyExist);
        
        modifications.commitModifications();
      } else {
        modifications.rollbackModifications();
      }
    } catch (Exception e) {
      modifications.rollbackModifications();
      throw new SecurityException("Cannot change login modules of " + name, e);
    }

    update();
  }

  /**
   *  Changes the configuration of login modules in effect.
   *
   * @param  template  name of login context template, e.g. BASIC, CLIENT_CERT, ...
   */
  public void setLoginModules(String template) throws SecurityException {
    Restrictions.checkPermission(Restrictions.COMPONENT_AUTHENTICATION, Restrictions.RESTRICTION_SET_LOGIN_MODULES);

    ModificationContextImpl modifications = (ModificationContextImpl) root.getModificationContext();
    modifications.beginModifications();
    
    boolean appAlreadyExist = isAppAlreadyExist(modifications);
    
    Configuration configuration = null;
    
    try {
      if (template == null) {
        String userStore = null;
        AppConfigurationEntry[] modules = null;

        configuration = modifications.getConfiguration(getPath(false), true, true);
        if (configuration == null) {
          modifications.rollbackModifications();
          return;
        }
        
        if (configuration.existsConfigEntry(TEMPLATE_ENTRY_KEY)) {
          String oldtemplate = (String) configuration.getConfigEntry(TEMPLATE_ENTRY_KEY);

          try {
            modules = root.getPolicyConfigurationContext(oldtemplate).getAuthenticationContext().getLoginModules();
          } catch (Exception e) {
            if (LOCATION.beWarning()) {
              LOCATION.traceThrowableT(Severity.WARNING, e.getLocalizedMessage(), e);
            }
          }

          configuration.deleteConfigEntry(TEMPLATE_ENTRY_KEY);
        }

        if (modules != null) {
          userStore = root.getUserStoreContext().getActiveUserStore().getConfiguration().getName();

          if (configuration.existsSubConfiguration(userStore)) {
            configuration = configuration.getSubConfiguration(userStore);
          } else {
            configuration = configuration.createSubConfiguration(userStore);
          }
          
          storeLoginModuleConfiguration(userStore, configuration, modules, appAlreadyExist);
          //ConfigEntrySerializator.writeAppConfigurationEntryArray(configuration, modules);
        }

        modifications.commitModifications();      
      } else {                
        Configuration subconfiguration = modifications.getConfiguration(getPath(false), true, true);

        if (subconfiguration == null) {
          modifications.rollbackModifications();
          return;
        }

        String userStore = root.getUserStoreContext().getActiveUserStore().getConfiguration().getName();

          if (subconfiguration.existsSubConfiguration(userStore)) {
            configuration = subconfiguration.getSubConfiguration(userStore);
            configuration.deleteAllConfigEntries();
            configuration.deleteAllSubConfigurations();
          }

        if ("client-cert".equalsIgnoreCase(template)) {
          template = "client_cert";
        }

        if (subconfiguration.existsConfigEntry(TEMPLATE_ENTRY_KEY)) {
          subconfiguration.modifyConfigEntry(TEMPLATE_ENTRY_KEY, template);
        } else {
          subconfiguration.addConfigEntry(TEMPLATE_ENTRY_KEY, template);
        }

        modifications.commitModifications();
      }
    } catch (Exception exc) {
      modifications.rollbackModifications();
      throw new SecurityException("Cannot change authentication stack template of " + name, exc);
    }

    update();
  }

  public boolean getUserStoreIsTheActiveOne() {
    return userStoreIsTheActiveOne;
  }

  private final String getPath(boolean withActiveUserStore) throws SecurityException {
    String componentPath = SecurityConfigurationPath.AUTHENTICATION_PATH;

    if (withActiveUserStore) {
      return componentPath + "/" + root.getUserStoreContext().getActiveUserStore().getConfiguration().getName();
    } else {
      return componentPath;
    }
  }

  private final String getPath(String userStore) throws SecurityException {
    String componentPath = SecurityConfigurationPath.AUTHENTICATION_PATH;

    return componentPath + "/" + userStore;
  }

  private void checkEntries(AppConfigurationEntry[] entries) {
    UserStore store = null;
    try {
      store = getAuthenticationUserStore();
    } catch (Exception e) {
      store = null;
    }
    finally{
      checkEntries(entries, store);
    }
  }

  private void checkEntries(AppConfigurationEntry[] entries,String userStoreName) {    
    UserStore store = null;
    if (userStoreName != null) {
      try{
        store = root.getUserStoreContext().getUserStore(userStoreName);
      }
      catch(Exception e){
        store = null;
      }
    }
    checkEntries(entries, store);
  }
  
  private void checkEntries(AppConfigurationEntry[] entries, UserStore store) {
    if (entries == null) {
      entries = new AppConfigurationEntry[0];
    }

    if (store != null) {      
      String property = null;
      try {
        property = SecurityServerFrame.getServiceProperties().getProperty(USERSTORE_CHECK, "true");
      } 
      catch (Exception e) {
        SimpleLogger.log(Severity.WARNING, Util.SEC_SRV_CATEGORY, LOCATION, "ASJ.secsrv.000043", "Unable to retrieve property '{0}'!", new Object[]{USERSTORE_CHECK});

        if (LOCATION.beWarning()) {
          LOCATION.traceThrowableT(Severity.WARNING, "Unable to retrieve property '" + USERSTORE_CHECK + "'!", e);
        }
      }
      finally{
        if(property==null){
          property = "false";
        }
      }

      if (!"false".equalsIgnoreCase(property)) {
        LoginModuleConfiguration[] configurations = store.getConfiguration().getLoginModules();
        boolean isLoginModuleExists;
        
        if (configurations != null) {
          for (int i = 0; i < entries.length; i++) {
            isLoginModuleExists = false;
            //check if login module entry exists in userstore
            for (int j = 0; j < configurations.length; j++) {
              if (entries[i].getLoginModuleName().equals(configurations[j].getName()) ||
                  entries[i].getLoginModuleName().equals(configurations[j].getLoginModuleClassName())) {
                isLoginModuleExists = true;
                break;
              }
            }
            
            if (!isLoginModuleExists) {
              //check if login module entry is in deployment
              LoginModuleContainer lmContainer = SecurityServerFrame.getLoginModuleContainer();
              isLoginModuleExists = lmContainer.isLoginModuleInDeploy(entries[i].getLoginModuleName());
            }
            
            //check if no such entry has been found in the userstore as already created or in deployment
            if (!isLoginModuleExists) {
              throw new SecurityException("Invalid login module name: " + entries[i].getLoginModuleName());
            }
          }          
        }
      }
    }      
  }
  
  private LoginContextFactory getLoginContextFactory(){
    if (loginContextFactory == null) {
      synchronized (this) {
        if (loginContextFactory == null) {
          loginContextFactory = new LoginContextFactory(this, getSessionPool(), 50);
        }
      }
    }
    return loginContextFactory;
  }
  
  private AppConfigurationEntry[] getAppConfigurationEntries(String userStoreName, Configuration config){    
    AppConfigurationEntry[] ace = ConfigEntrySerializator.readAppConfigurationEntryArray(config);    
    UserStore userStore = getUserStoreByName(userStoreName);
    return getLoginContextFactory().getResolvedLoginModuleConfiguration(userStore, ace, false, false);
  }
    
  
  private synchronized void storeLoginModuleConfiguration(String userStoreName, Configuration configuration, AppConfigurationEntry[] entries, boolean applicationExists){
    
    AppConfigurationEntry[] appEntries = null;
    if(entries!=null){
      appEntries = new AppConfigurationEntry[entries.length];
      System.arraycopy(entries, 0, appEntries, 0, entries.length);
    }
    
    UserStore uStore = getUserStoreByName(userStoreName);
    
    if(uStore!=null && appEntries!=null){      
      
      UserStoreConfiguration userStoreConfiguration = uStore.getConfiguration();
      LoginModuleConfiguration[] definedLoginModules = userStoreConfiguration.getLoginModules();
      
      int lmLen = definedLoginModules.length;
      int aceLen = appEntries.length;
      
      for(int j=0; j<aceLen; j++){
        AppConfigurationEntry appEntry = appEntries[j];
        String appLM = appEntry.getLoginModuleName();
        
        Map appOptions = null;
        Map readAppOptions = appEntry.getOptions();
        if(readAppOptions==null){
          appOptions = new HashMap();
        }
        else{
          appOptions = new HashMap(readAppOptions);
        }
        
        boolean lmDNIsFoundInUserStore = false; //Found LM by Display name
        boolean lmCNIsFoundInUserStore = false; //Found LM by Class name
        for(int i=0; !lmDNIsFoundInUserStore && i<lmLen; i++){ //Exit only if there is a match by display name
          
          LoginModuleConfiguration lmc = definedLoginModules[i];          
                    
          Map lmOptions = null;          
          Map readLMOptions = lmc.getOptions();
          if(readLMOptions==null){
            lmOptions = new HashMap();
          }
          else{
            lmOptions = new HashMap(readLMOptions);
          }
          
          //First check by displayname - higher priority than by classname          
          if(appLM.equals(lmc.getName())){
            lmDNIsFoundInUserStore = true;            
            
            //>Limited inheritance
            if(!applicationExists){ //Application is not deployed. It is called during the deploy
              //Ignore all options and make it reference
              appEntry = new AppConfigurationEntry(
                  lmc.getName(),
                  appEntry.getControlFlag(),
                  new HashMap());                                
              appEntries[j]=appEntry;
              
              if(!appOptions.isEmpty()){
                LOCATION.warningT("The following options:'" + appOptions + "' of Login Module:'" + appLM + "' part of policy configuration named:'" + getPolicyConfigurationName() + 
                    "' are skipped, because of it is a reference to the following Login Module in User Store:'" + appLM + 
                    "' with options:'" + lmOptions + "'.");
              }
            }
            else if(!lmOptions.equals(appOptions)){ //inheritance is broken by the user
              appEntry = new AppConfigurationEntry(
                  lmc.getLoginModuleClassName(),
                  appEntry.getControlFlag(),
                  appOptions);                                
              appEntries[j]=appEntry;
            }
            else{
              appEntry = new AppConfigurationEntry( //Exclude all derived parent options
                  lmc.getName(),
                  appEntry.getControlFlag(),
                  new HashMap());                                
              appEntries[j]=appEntry;
            }
            //<End limited inheritance
            //>Full inheritance
            /*
            Set lmKeys = lmOptions.keySet();
            Set appKeys = appOptions.keySet();
             
            //Do not check for broken inheritance if application does not have
            //any authentication policy defined so far - not deployed
            if(applicationExists && !appKeys.containsAll(lmKeys)){
              //The inheritance is broken
              appEntry = new AppConfigurationEntry(
                  lmc.getLoginModuleClassName(),
                  appEntry.getControlFlag(),
                  appOptions);                  
              appEntries[j]=appEntry;                  
            }                
            //exclude all derived options
            else {
              Map options = new HashMap();
              for(Iterator itr = appKeys.iterator(); itr.hasNext();){                    
                String appKey = (String)itr.next();
                String appValue =  (String)appOptions.get(appKey);
                String lmValue = (String)lmOptions.get(appKey);
                if(appValue!=null){
                  if(lmValue==null || !appValue.equals(lmValue)){
                    options.put(appKey, appValue);
                  }                      
                }
              }
              appEntry = new AppConfigurationEntry(
                  lmc.getName(),
                  appEntry.getControlFlag(),
                  options);                  
              appEntries[j]=appEntry;
            }
            */
            //<End full inheritance
          }           
          else if(appLM.equals(lmc.getLoginModuleClassName())){            
            lmCNIsFoundInUserStore = true;
            
            //>Limited inheritance
            if(lmOptions.equals(appOptions)){ //Found a reference               
              appEntry = new AppConfigurationEntry(
                  lmc.getName(),
                  appEntry.getControlFlag(),
                  new HashMap());                  
              appEntries[j]=appEntry;
            }
            //<End limited inheritance
            
            
            //>Full inheritance            
            /*            
            Set lmKeys = lmOptions.keySet();
            Set appKeys = appOptions.keySet();
            
            if(lmKeys.containsAll(appKeys) && appKeys.containsAll(lmKeys)){
              //Make it reference and exclude all derived options
              Map options = new HashMap();
              for(Iterator itr = appKeys.iterator(); itr.hasNext();){                    
                String appKey = (String)itr.next();
                String appValue =  (String)appOptions.get(appKey);
                String lmValue = (String)lmOptions.get(appKey);
                if(appValue!=null){
                  if(lmValue==null || !appValue.equals(lmValue)){
                    options.put(appKey, appValue);
                  }                      
                }
              }
              appEntry = new AppConfigurationEntry(
                  lmc.getName(),
                  appEntry.getControlFlag(),
                  options);                  
              appEntries[j]=appEntry;
            }
            */
            //<End full inheritance
            
            //This case is wrong, because of when the user is deleted all options manually
            //and its parent options are not empty, than the inheritance is broken
            //If we put back reference again on next save it will become not reference again.
            //This is cyclic process.
            /*
            if(!makeItReference && appOptions.size()==0){
              //TODO check this with Dimitar
              //Whether it must be set as reference if no Options are provided.
              //What will happen if the user manually deletes all options - it will inherit all options from its parent
              //makeItReference = true;
            }
            */                        
          }          
        }
        //There is no such LM in user store. Perhaps this LM is deleted from User Store
        //Write warning message in the log.
        //Furter on read this will be checked again and error message will be written.
        //See the method: LoginContextFactory.getResolvedLoginModuleConfiguration(...)
        if(!(lmDNIsFoundInUserStore || lmCNIsFoundInUserStore)){
          LOCATION.warningT("The policy configuration named:" + getPolicyConfigurationName() + 
              " has wrong authentication stack. LoginModule '" + appLM + 
              "' does not exist in the User Store.");
        }
      }
    }        
    ConfigEntrySerializator.writeAppConfigurationEntryArray(configuration, appEntries);
  }
  
  private boolean isAppAlreadyExist(ModificationContextImpl modifications){
    boolean appAlreadyExist = false;
    try{
      Configuration configurationCheck = modifications.getConfiguration(getPath(false), false, false);
      if(configurationCheck!=null){
        String[] subConfigurations = configurationCheck.getAllSubConfigurationNames();
        appAlreadyExist = subConfigurations!=null && subConfigurations.length>0; 
      }      
    }
    catch(Exception e){
      appAlreadyExist = false;
    }
    return appAlreadyExist;
  }
  
  private UserStore getUserStoreByName(String userStoreName){
    UserStore uStore = null;
    if (userStoreName != null) {
      try{
        uStore = root.getUserStoreContext().getUserStore(userStoreName);
      }
      catch(Exception e){
        uStore = null;
      }
    }
    else{
      uStore = getAuthenticationUserStore();
    }
    return uStore;
  }
}
