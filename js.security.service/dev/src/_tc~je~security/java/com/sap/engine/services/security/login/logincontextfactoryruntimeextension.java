/**
 * Copyright (c) 2008 by SAP Labs Bulgaria,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * 
 * Created on Feb 13, 2008 by I032049
 *   
 */
 
package com.sap.engine.services.security.login;

import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.login.AppConfigurationEntry;

import com.sap.engine.frame.core.thread.ThreadContext;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.interfaces.security.userstore.config.LoginModuleConfiguration;
import com.sap.engine.interfaces.security.userstore.config.UserStoreConfiguration;
import com.sap.engine.interfaces.security.userstore.context.UserContext;
import com.sap.engine.services.security.SecurityServerFrame;
import com.sap.engine.services.security.Util;
import com.sap.engine.services.security.server.SecurityContextImpl;
import com.sap.engine.services.security.server.deploy.LoginModuleContainer;
import com.sap.engine.services.security.server.jaas.LoginModuleHelperImpl;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

public class LoginContextFactoryRuntimeExtension extends LoginContextFactory {

  protected final static String AUTH_NAME = "sap.security.auth.configuration.name";
  
  private final static String ADDITIONAL_LOADERS = "LoginModuleClassLoaders";

  private final static String SID = "System-ID";
  private final static String SESSION_LOGOUT = "sap.security.auth.session.logout";
  private final static String SECURITY_CONTEXT_OBJECT = "sap.security.auth.context.object";
   
  private static final Location LOCATION = Location.getLocation(AuthenticationTraces.LOGIN_CONTEXT_LOCATION);
  
  private AppConfigurationEntry[] configuration = null;
  private SecuritySessionPool sessionsPool = null;

  private LoginModuleHelperImpl moduleHelper = null;  
  private UserStoreConfiguration userStoreConfiguration = null;

  private Object[] loadedModuleClasses = new Object[0];
  private String policyName = null;
  
  public LoginContextFactoryRuntimeExtension(String name, SecuritySessionPool sessionsPool, int poolLimit, AppConfigurationEntry[] entries) {
    super(null, sessionsPool, poolLimit);
    this.sessionsPool = sessionsPool;
    this.policyName = name;
    this.configuration = entries;
    moduleHelper = ((SecurityContextImpl) SecurityServerFrame.getSecurityContext()).getLoginModuleHelper();
    userStoreConfiguration = SecurityContextImpl.getRoot().getUserStoreContext().getActiveUserStore().getConfiguration();
  }


  public synchronized void close(FastLoginContext context) {
  }

  synchronized Map initializeLoginContext(FastLoginContext context, boolean isSessionLogout) {
    Map sharedState = new SharedState<String, Object>();

    if (context.subject == null) {
      context.subject = new Subject();
    }    

    sharedState.put(SID, SecurityContextImpl.getSystemID());
    sharedState.put(AUTH_NAME, getPolicyConfName());

    ThreadContext threadContext = SecurityServerFrame.threadContext.getThreadContext();
    if (threadContext != null) {
      sharedState.put(SECURITY_CONTEXT_OBJECT, threadContext.getContextObject("security"));
    }

    if (isSessionLogout) {
      sharedState.put(SESSION_LOGOUT, "true");
    }
    
    LoginModuleLoggingWrapperImpl[] loginModules = getLoginModules(context, sharedState);
    context.set(sessionsPool, context.subject, loginModules, configuration);

    return sharedState;
  }
  
  protected synchronized String getSecurityPolicyDomain() {
    return policyName;
  }
  
  private String getPolicyConfName(){
    return policyName;    
  }
  
  UserContext getUserContext(){    
    return SecurityContextImpl.getRoot().getAuthenticationContext().getAuthenticationUserStore().getUserContext();
  }
  
  LoginModuleHelperImpl getModuleHelper(){
    return moduleHelper;
  }
  
  AppConfigurationEntry[] getConfiguration(){
    return configuration;
  }
  
  private LoginModuleLoggingWrapperImpl[] getLoginModules(FastLoginContext context, Map sharedState){    
    LoginModuleLoggingWrapperImpl[] loginModules = null;

    if(configuration == null){
      
      loginModules = new LoginModuleLoggingWrapperImpl[0];
      loadedModuleClasses = new Object[0];
      
    } else{      
      
      loginModules = new LoginModuleLoggingWrapperImpl[configuration.length];
      loadedModuleClasses = new Object[configuration.length];
      
      ClassLoader classloader = getClass().getClassLoader();
      ClassLoader applicationLoader = Thread.currentThread().getContextClassLoader();
      LoginModuleContainer securityContainer = SecurityServerFrame.getLoginModuleContainer();
      
      for (int i = 0; i < configuration.length; i++) {
        String loginModuleName = configuration[i].getLoginModuleName();
        String loginModuleClassName = null;    
        Map options = null;
        AppConfigurationEntry entry = null;
        boolean loadedByClassName = false;
                
        LoginModuleConfiguration lmConfiguration = getLoginModuleConfigurationByDisplayName(loginModuleName);        
        
        if (lmConfiguration != null) {
          loginModuleClassName = lmConfiguration.getLoginModuleClassName(); 
          options = lmConfiguration.getOptions();
          loadedModuleClasses[i] = loadLoginModuleClasses(loginModuleClassName, applicationLoader, classloader, securityContainer);  
        }
        
        if (!(loadedModuleClasses[i] instanceof Class)) {
          loadedByClassName = true;
          loginModuleClassName = loginModuleName;    
          options = configuration[i].getOptions();
          loadedModuleClasses[i] = loadLoginModuleClasses(loginModuleClassName, applicationLoader, classloader, securityContainer);
        } 

        entry = new AppConfigurationEntry(loginModuleClassName, configuration[i].getControlFlag(), options);
        
        if (loadedModuleClasses[i] instanceof Class) {
          loginModules[i] = new LoginModuleLoggingWrapperImpl(entry, (Class) loadedModuleClasses[i]);
          loginModules[i].instantiateLoginModule();
          
          if (loadedByClassName) {
            LOCATION.logT(Severity.DEBUG, "Login module class successfully loaded by class name: {0} ", new Object[] {loginModuleName});
          } else {
            LOCATION.logT(Severity.DEBUG, "Login module class successfully loaded by display name: {0} ", new Object[] {loginModuleName});
          }          
        } else if (loadedModuleClasses[i] instanceof Throwable){
          loginModules[i] = new LoginModuleLoggingWrapperImpl(entry, (Throwable) loadedModuleClasses[i]);
          SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, (Throwable)loadedModuleClasses[i], "ASJ.secsrv.000193", "Cannot load login module class: {0}.", new Object[] {loginModuleClassName});
        }

        loginModules[i].initialize(context.subject, context.callbackHandler, sharedState, options);
      }      
    }    
    return loginModules;
  }  
  
  
  private Object loadLoginModuleClasses(String loginModuleClassName, ClassLoader applicationLoader, ClassLoader classloader, LoginModuleContainer securityContainer) {
    Object result = null;
    
    if (applicationLoader != null) {
      try {
        result = applicationLoader.loadClass(loginModuleClassName);
      } catch (ClassNotFoundException tt) {
        result = null;      
      }
    }
    
    if (result == null) {
      if (classloader != null) {
        try {
          result = classloader.loadClass(loginModuleClassName);
        } catch (ClassNotFoundException tt) {
          result = null;
        }
      } else {
        try {
          result = Class.forName(loginModuleClassName);
        } catch (ClassNotFoundException tt) {
          result = null;
        }
      }
    }
    
    if (result == null) {
      try {
        result = Util.loadClassFromAdditionalLoaders(loginModuleClassName, ADDITIONAL_LOADERS);
      } catch (ClassNotFoundException t) {
        result = null;              
      }
    }
    
    if (result == null) {
       if (securityContainer != null) {
        try {
          result = securityContainer.getLoginModuleClass(loginModuleClassName);
        } catch (Exception t) {
          result = t;  
        }
      }
    }
    
    return result;
  } 
  
  private LoginModuleConfiguration getLoginModuleConfigurationByDisplayName (String displayName) {
    LoginModuleConfiguration[] loginModuleConfigurations = userStoreConfiguration.getLoginModules();
    
    for (int i = 0; i < loginModuleConfigurations.length; i++) {
      if (displayName.equals(loginModuleConfigurations[i].getName())) {
        return loginModuleConfigurations[i];        
      }      
    }    
    return null;
  }
}