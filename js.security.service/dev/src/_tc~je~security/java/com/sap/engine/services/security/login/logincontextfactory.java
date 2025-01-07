/**
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.security.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.LoginContext;

import com.sap.engine.frame.core.thread.ThreadContext;
import com.sap.engine.interfaces.security.AuthenticationContext;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.interfaces.security.auth.WebCallbackHandler;
import com.sap.engine.interfaces.security.userstore.UserStore;
import com.sap.engine.interfaces.security.userstore.config.LoginModuleConfiguration;
import com.sap.engine.interfaces.security.userstore.config.UserStoreConfiguration;
import com.sap.engine.interfaces.security.userstore.context.UserContext;
import com.sap.engine.services.security.SecurityServerFrame;
import com.sap.engine.services.security.Util;
import com.sap.engine.services.security.server.SecurityContextImpl;
import com.sap.engine.services.security.server.deploy.LoginModuleContainer;
import com.sap.engine.services.security.server.jaas.LoginModuleHelperImpl;
import com.sap.security.core.InternalUMFactory;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 *
 *
 * @author Stephan Zlatarev
 * @version 6.30
 */
public class LoginContextFactory {

  protected final static String AUTH_NAME = "sap.security.auth.configuration.name";

  private static String GLOBAL_TEMPLATE_NAME;
  
  private final static String ADDITIONAL_LOADERS = "LoginModuleClassLoaders";

  private final static String SID = "System-ID";
  private final static String SESSION_LOGOUT = "sap.security.auth.session.logout";
  private final static String SECURITY_CONTEXT_OBJECT = "sap.security.auth.context.object";
  // constant is public because it is checked by the SPNego wizzard
  public final static String AUTHENTICATION_STACK_OPTION_CREATE_SECURITY_SESSION = "create_security_session"; 
  private final static String FALSE = "false";
  
  private static final Location LOCATION = Location.getLocation(AuthenticationTraces.LOGIN_CONTEXT_LOCATION);
  
  private AuthenticationContext authentication = null;
  private AppConfigurationEntry[] configuration = null;
  private SecuritySessionPool sessionsPool = null;
  private String template = null;
  private boolean initialized = false;
  
  private LoginModuleHelperImpl moduleHelper = null;
  
  private Object[] loadedModuleClasses = new Object[0];

  public LoginContextFactory(AuthenticationContext authentication, SecuritySessionPool sessionsPool, int poolLimit) {
    this.authentication = authentication;
    this.sessionsPool = sessionsPool;
    moduleHelper = ((SecurityContextImpl) SecurityServerFrame.getSecurityContext()).getLoginModuleHelper();
  }

  public synchronized void init() {    
    if (!initialized) {      
      template = authentication.getTemplate();      
      if(!isUsingTemplate()){
        loadedModuleClasses = new Object[0];
        this.configuration = getResolvedLoginModuleConfiguration(authentication.getAuthenticationUserStore(), authentication.getLoginModules(), true, true);                
        if ((configuration == null) || (configuration.length == 0)) {
          useGlobalTemplate();
        }
        else{
          initialized = loadModuleClasses(); //Put it true, just only if all classes are loaded
          return;
        }
      }      
      initialized = true;  
    }    
  }

  public synchronized FastLoginContext getLoginContext(Subject subject, CallbackHandler callbackHandler) throws Exception {
    
    init();
    
    if(isUsingTemplate()){             
      if(!isCyclicDependency()){
        AuthenticationContext authContextRef = getAuthenticationContextForTemplate(template);
        if(authContextRef==null){          
          throw new Exception("Authentication context for " + template + " template is null.");
        }
        else{
          LoginContext loginContext = authContextRef.getLoginContext(subject, callbackHandler);         
          FastLoginContext flc = (FastLoginContext)loginContext;        
          this.configuration = flc.getConfiguration();  
          this.loadedModuleClasses = flc.getModuleClasses();
          flc = null;             
        }
      }
      else{
        throw new Exception("Cyclic dependency has been found for security policy configuration named: " + authentication.getPolicyConfigurationName());
      }
    }
    
    boolean createSession = true;  
    try {
      String createSessionValue = authentication.getProperty(AUTHENTICATION_STACK_OPTION_CREATE_SECURITY_SESSION);
      createSession = !FALSE.equalsIgnoreCase(createSessionValue);
    } catch(Exception ex) {
      if (LOCATION.beDebug()) {
       LOCATION.debugT("Error getting authentication stack option: {0}. Exception message: {1}", 
           new Object[] { AUTHENTICATION_STACK_OPTION_CREATE_SECURITY_SESSION, ex.getMessage() } ); 
      }
    }
    createSession = createSession && (callbackHandler != null);
    FastLoginContext result = new FastLoginContext(this,  createSession, getPolicyConfName());    

    result.set(subject, callbackHandler);

    return result;    
  }

  public synchronized void close(FastLoginContext context) {
  }

  public synchronized void clearLoginModules() {
    initialized = false;
    if (LOCATION.beDebug()) {
      LOCATION.debugT("Login module classes are cleared for policy configuration [{0}].", new Object[] {authentication.getPolicyConfigurationName()});
    }
  }

  synchronized Map initializeLoginContext(FastLoginContext context, boolean isSessionLogout) {
    
    Object[] moduleClasses = getModuleClasses();
    
    Map sharedState = new SharedState<String,Object>();
    LoginModuleLoggingWrapperImpl[] loginModules = new LoginModuleLoggingWrapperImpl[moduleClasses.length];

    if (context.subject == null) {
      context.subject = new Subject();
    }    
    
    for (int i = 0; i < loginModules.length; i++) {
      if (moduleClasses[i] instanceof Class) {
        loginModules[i] = new LoginModuleLoggingWrapperImpl(configuration[i], (Class) moduleClasses[i]);
        loginModules[i].instantiateLoginModule();
      } else {
        loginModules[i] = new LoginModuleLoggingWrapperImpl(configuration[i], (Throwable) moduleClasses[i]);
      }
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
    
    if (LOCATION.beDebug()) {
      LOCATION.debugT("INITIALIZE() for auth stack ["+getPolicyConfName()+"].");
    }
    for (int i = 0; i < moduleClasses.length; i++) {
      loginModules[i].initialize(context.subject, context.callbackHandler, sharedState, configuration[i].getOptions());
    }

    context.set(sessionsPool, context.subject, loginModules, configuration);

    return sharedState;
  }
  
  protected synchronized String getSecurityPolicyDomain() {
    return (String) authentication.getProperty(AuthenticationContext.SECURITY_POLICY_DOMAIN_PROPERTY);
  }

  private void useGlobalTemplate() {
    
    //Speed up the performance by skipping reading and retrieveing ume property
    if(GLOBAL_TEMPLATE_NAME==null){
      GLOBAL_TEMPLATE_NAME = InternalUMFactory.getConfiguration().getStringDynamic(WebCallbackHandler.GLOBAL_AUTH_TEMPLATE, WebCallbackHandler.DEFAULT_AUTH_TEMPLATE);
  
      if (LOCATION.beInfo()) {
        LOCATION.infoT("Property {0} is {1}.", new Object[] {WebCallbackHandler.GLOBAL_AUTH_TEMPLATE, GLOBAL_TEMPLATE_NAME});
      }      
      /*
      AuthenticationContext authenticationContext = getAuthenticationContextForTemplate(GLOBAL_TEMPLATE_NAME);
      if (authenticationContext != null) {
        //The configuration will be returned by the template, when getLoginContext() method is called for this template
        //this.configuration = authenticationContext.getLoginModules();
        this.template = GLOBAL_TEMPLATE_NAME;
      } else {
        LOCATION.errorT("Authentication context for {0} template is null.", new Object[] {template});
      }
      */
    }
    this.template = new String(GLOBAL_TEMPLATE_NAME);
  }
  
  private AuthenticationContext getAuthenticationContextForTemplate(String template){
    com.sap.engine.interfaces.security.SecurityContext templateContext = SecurityContextImpl.getRoot().getPolicyConfigurationContext(template);
    if (templateContext != null) {
      return templateContext.getAuthenticationContext();
    }
    else{
      SimpleLogger.trace(Severity.ERROR, LOCATION, "ASJ.secsrv.000188", "Security context for {0} template is null.", new Object[] {template});
      return null;    
    }
  }
  
  private boolean isUsingTemplate(){
    return template!=null && template.trim().length()>0 && !template.equals("no");
  }
  
  private String getPolicyConfName(){
    return authentication.getPolicyConfigurationName();    
  }
  
  UserContext getUserContext(){    
    return authentication.getAuthenticationUserStore().getUserContext();
  }
  
  LoginModuleHelperImpl getModuleHelper(){
    return moduleHelper;
  }
  
  AppConfigurationEntry[] getConfiguration(){
    return configuration;
  }
  
  public boolean isCyclicDependency(){
    return isCyclicDependency(authentication);
  }
  
  public boolean isCyclicDependency(AuthenticationContext authContext){
    List lstPassed = new ArrayList();
    lstPassed.add(authentication);
    
    AuthenticationContext ac = authContext;    
    do{                   
      String acTemplate = ac.getTemplate();
      ac = null;
      
      if(acTemplate!=null){
        try{
          SecurityContext securityContext = SecurityContextImpl.getRoot().getPolicyConfigurationContext(acTemplate);
          ac = securityContext.getAuthenticationContext();
        }
        catch(Exception e){
          ac = null;
        }
      }
      
      if(ac!=null){
        for(int i=0; i<lstPassed.size(); i++){
          if(lstPassed.get(i)==ac){
            SimpleLogger.trace(Severity.ERROR, LOCATION, "ASJ.secsrv.000189", "Cyclic dependency has been found for security policy configuration named: {0}", new Object[]{ac.getPolicyConfigurationName()});
            return true;
          }
        }
        lstPassed.add(ac);
      }                 
    }
    while(ac!=null);
    
    return false;
  }
  
  synchronized Object[] getModuleClasses(){   
    if(loadedModuleClasses==null){
      return new Object[0];
    }
    else{
      int len = loadedModuleClasses.length;
      Object[] copyModuleClasses = new Object[len];
      System.arraycopy(loadedModuleClasses, 0, copyModuleClasses, 0, len);
      return copyModuleClasses;
    }
  }
  
  private boolean loadModuleClasses(){    
    boolean loaded = true;
    if(configuration==null){
      loadedModuleClasses = new Object[0];
    }
    else{      
      loadedModuleClasses = new Object[configuration.length];
      ClassLoader classloader = getClass().getClassLoader();
      ClassLoader applicationLoader = Thread.currentThread().getContextClassLoader();
      
      for (int i = 0; i < loadedModuleClasses.length; i++) {
        String lmName = configuration[i].getLoginModuleName();
        try{          
          if (applicationLoader != null) {
            try {
              loadedModuleClasses[i] = applicationLoader.loadClass(lmName);
            } catch (ClassNotFoundException tt) {
              loadedModuleClasses[i] = null;
            }
          }
          if (loadedModuleClasses[i] == null) {
            if (classloader != null) {
              try {
                loadedModuleClasses[i] = classloader.loadClass(lmName);
              } catch (ClassNotFoundException tt) {
                loadedModuleClasses[i] = null;
              }
            } else {
              try {
                loadedModuleClasses[i] = Class.forName(lmName);
              } catch (ClassNotFoundException tt) {
                loadedModuleClasses[i] = null;
              }
            }
          }
          if (loadedModuleClasses[i] == null) {
            try {
              loadedModuleClasses[i] = Util.loadClassFromAdditionalLoaders(lmName, ADDITIONAL_LOADERS);
            } catch (ClassNotFoundException t) {
              loadedModuleClasses[i] = null;              
            }
          }
          if (loadedModuleClasses[i] == null) {
            LoginModuleContainer securityContainer = SecurityServerFrame.getLoginModuleContainer();
            if (securityContainer != null) {
              loadedModuleClasses[i] = securityContainer.getLoginModuleClass(lmName);
            }
          }         
        }
        catch (Exception t) {
          loadedModuleClasses[i] = t;
          loaded = false;
          SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, t, "ASJ.secsrv.000190", "Cannot load login module class {0}.", new Object[] {lmName});
        } finally {
          if(loadedModuleClasses[i] == null) {
            loaded = false;
            SimpleLogger.trace(Severity.ERROR, LOCATION, "ASJ.secsrv.000191", "Cannot load login module class: {0}",  new Object[]{lmName});
          }
        }
      }
    }
    return loaded;
  }
  
  public AppConfigurationEntry[] getResolvedLoginModuleConfiguration(UserStore uStore, AppConfigurationEntry[] entries, boolean useClassName, boolean clone){
    
    AppConfigurationEntry[] appEntries = null;
    if(entries!=null){
      if(clone){
        appEntries = new AppConfigurationEntry[entries.length];
        System.arraycopy(entries, 0, appEntries, 0, entries.length);
      }
      else{
        appEntries = entries;
      }
    }
    
    if(uStore!=null && appEntries!=null){
      UserStoreConfiguration userStoreConfiguration = uStore.getConfiguration();
      LoginModuleConfiguration[] definedLoginModules = userStoreConfiguration.getLoginModules();
      
      int lmLen = definedLoginModules.length;
      int appLen = appEntries.length;
            
      for(int j=0; j<appLen; j++){
        
        AppConfigurationEntry appEntry = appEntries[j];
        String appLM = appEntry.getLoginModuleName();
        Map appOptions = appEntry.getOptions();
                
        boolean lmDNIsFoundInUserStore = false; //Found LM by Display Name       
        boolean lmCNIsFoundInUserStore = false; //Found LM by Class Name
        for(int i=0; !lmDNIsFoundInUserStore && i<lmLen; i++){ //Exit only if there is a much by Disply Name
          
          LoginModuleConfiguration lmc = definedLoginModules[i];
                    
          //Check whether this LM is a reference to the User Store's LM
          //It is true if this LM has a class name equals to display name property in User Store
          if(appLM.equals(lmc.getName())) {
            lmDNIsFoundInUserStore = true;
            
            Map lmOptions = lmc.getOptions();
            
            Map options = new HashMap();
            if(lmOptions!=null && !lmOptions.isEmpty()){
              options.putAll(lmOptions);
            }
            //Do not add PC's LM options
            /*
            if(appOptions!=null && !appOptions.isEmpty()){
              options.putAll(appOptions);
            } 
            */           
            
            String className = null;
            if(useClassName){
              className = lmc.getLoginModuleClassName();
            }         
            else{
              className = lmc.getName();
            }
            
            appEntry = new AppConfigurationEntry(
                className,
                appEntry.getControlFlag(),
                options);
            appEntries[j]=appEntry;            
          }
          //Check by class name
          else if(appLM.equals(lmc.getLoginModuleClassName())){
            lmCNIsFoundInUserStore = true;
          }
        }
        //There is no such LM in user store. Perhaps this LM is deleted from User Store
        //Write warning message in the log.        
        if(!(lmDNIsFoundInUserStore || lmCNIsFoundInUserStore)){    
          final String errorTxt = "The policy configuration named:" + authentication.getPolicyConfigurationName() + 
          " has wrong authentication stack. LoginModule '" + appLM + 
          "' does not exist in the User Store."; 
          if(useClassName){
            SimpleLogger.trace(Severity.ERROR, LOCATION, "ASJ.secsrv.000192", "The policy configuration named: {0} has wrong authentication stack. LoginModule '{1}' does not exist in the User Store.", new Object[]{authentication.getPolicyConfigurationName(), appLM});
          }
          else{
            LOCATION.warningT(errorTxt);
          }          
        }
      }            
    }            
    return appEntries;
  }

}