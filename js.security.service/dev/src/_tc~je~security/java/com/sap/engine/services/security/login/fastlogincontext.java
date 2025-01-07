/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.security.login;

import java.io.IOException;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedActionException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.LanguageCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginException;

import com.sap.engine.frame.core.thread.ThreadContext;
import com.sap.engine.interfaces.security.auth.AbstractLoginModule;
import com.sap.engine.interfaces.security.auth.AuthStateCallback;
import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.interfaces.security.auth.AbstractWebCallbackHandler;
import com.sap.engine.interfaces.security.userstore.context.UserContext;
import com.sap.engine.interfaces.security.userstore.context.UserInfo;
import com.sap.engine.lib.security.LoginExceptionDetails;
import com.sap.engine.lib.security.ReusableLoginContext;
import com.sap.engine.lib.security.http.HttpCallback;
import com.sap.engine.lib.security.http.HttpGetterCallback;
import com.sap.engine.services.security.SecurityServerFrame;
import com.sap.engine.services.security.exceptions.BaseLoginException;
import com.sap.engine.services.security.exceptions.SecurityResourceAccessor;
import com.sap.engine.services.security.login.monitor.MonitorTable;
import com.sap.engine.services.security.login.monitor.PolicyConfigurationMonitor;
import com.sap.engine.services.security.server.AuthenticationContextImpl;
import com.sap.engine.services.security.server.jaas.LoginModuleHelperImpl;
import com.sap.engine.session.runtime.SessionRequest;
import com.sap.security.api.UMFactory;
import com.sap.security.core.util.ResourceBean;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 *  Login Context for an application. It is notified of changes of the configuration
 * and optimized to start login without getting the configurations.
 *
 * @author  Stephan Zlatarev
 * @version 6.30
 */
public class FastLoginContext extends ReusableLoginContext {

  public static final String USER_INFO = "com.sap.engine.security.UserInfo";

  protected final static byte OPERATION_LOGIN = 0;
  protected final static byte OPERATION_ABORT = 1;
  protected final static byte OPERATION_COMMIT = 2;
  protected final static byte OPERATION_LOGOUT = 3;

  private static final Location TRACER = SecurityContext.TRACER;
  private static final Location LOCATION = Location.getLocation(AuthenticationTraces.LOGIN_CONTEXT_LOCATION);
  private static final Location LOCATION_TABLE = Location.getLocation(AuthenticationLog.TABLE_AUTHENTICATION_LOCATION);
  private static final Category CATEGORY = Category.getCategory(Category.SYS_SECURITY, AuthenticationLog.AUTHENTICATION_CATEGORY);
  
  protected Subject subject = null;
  protected CallbackHandler callbackHandler = null;


  private static int threadContextId = -1;

  private boolean isSubjectProvided = false;
  private AppConfigurationEntry[] configuration = null;
  public AppConfigurationEntry[] configurationEntries = null;
  private Principal userPrincipal = null;
  private Map sharedState = null;
  private LoginContextFactory factory = null;
  private LoginModuleLoggingWrapperImpl[] modules = null;
  private boolean createSession = true;
  private ModulesProcessAction processAction = null;
  private String authenticationStack = null;
  private boolean isReAuthenticationRequested = false;
  private boolean isReAuthenticationSuccessful = false;

  private PolicyConfigurationMonitor policyMonitor    = null;
  private PolicyConfigurationMonitor aggregateMonitor = null;
  private boolean isLoginSucceeded = false;
  
  private static final Locale DEFAULT_LOCALE = Locale.getDefault();
  
  private static final String[] LOGIN_MESSAGE_KEYS = {
    "authentication_000", //WRONG_USERNAME_PASSWORD_COMBINATION
    "authentication_001", //PASSWORD_EXPIRED
    "authentication_002", //USER_IS_CURRENTLY_NOT_VALID
    "authentication_003", //USER_IS_LOCKED
    "authentication_004", //CERTIFICATE_IS_NOT_TRUSTED
    "authentication_005", //NO_USER_MAPPED_TO_THIS_CERTIFICATE
    null, //SAP_LOGON_TICKET_HAS_EXPIRED
    null, //SAP_LOGON_TICKET_IS_NOT_TRUSTED
    null, //UNABLE_TO_CREATE_SAP_LOGON_TICKET
    "authentication_009", //EMERGENCY_USER_IS_ACTIVE
    null, //UNABLE_TO_PASS_SAP_LOGON_TICKET
    null, //UNABLE_TO_GET_SAP_LOGON_TICKET
    "authentication_012", //DIGEST_CREDENTIALS_NOT_VALID
    null, //P4_TICKET_NOT_VALID
    "authentication_014", //ACTIVE_USERS_LIMIT_RECHED
    "authentication_015", //USER_ALREADY_LOGGED_IN
    null, //USER_NOT_LOGGED_IN
    null, //NO_LOGIN_MODULE_SUCCEEDED
    null, //IO_EXCEPTION
    "authentication_019", //NO_PASSWORD
    "authentication_020", //PASSWORD_NOT_USED_FOR_LONG_TIME
    "authentication_021", //PASSWORD_LOCKED
    "authentication_022", //CHANGE_PASSWORD_NO_PASSWORD
    "authentication_023", //CHANGE_PASSWORD_NO_NEW_PASSWORD
    "authentication_024", //CHANGE_PASSWORD_NO_CONFIRM_PASSWORD
    "authentication_025", //CHANGE_PASSWORD_NO_IDENTICAL_PASSWORDS
    "authentication_026", //CHANGE_PASSWORD_FAILED
    null,
    null,
    null,
    null,
    null,
    null,
    null,
    null,
    null,
    null,
    null,
    null,
    null,
    null,
    null,
    null,
    null,
    null,
    null,
    null,
    null,
    null,
    "authentication_049" //no such user in userstore
  };
  

  FastLoginContext(LoginContextFactory factory, boolean createSession, String authenticationStack) throws Exception {
    super(LoginContextConfiguration.EMPTY_STACK);
    this.factory = factory;
    this.createSession = createSession;
    this.authenticationStack = authenticationStack;
    
    policyMonitor    = MonitorTable.getMonitor(authenticationStack);
    aggregateMonitor = MonitorTable.getAggregatedMonitor();
  }
  
  FastLoginContext(String name, Subject subject, CallbackHandler callbackHandler, Configuration config, boolean createSession) throws Exception {
    super(LoginContextConfiguration.EMPTY_STACK);
    this.configurationEntries = config.getAppConfigurationEntry(name);
    this.createSession = createSession;
    
    this.authenticationStack = name;
    this.subject = subject;
    this.callbackHandler = callbackHandler;


    this.factory = new LoginContextFactoryRuntimeExtension(name, AuthenticationContextImpl.getSessionPool(), 50, configurationEntries);

    policyMonitor    = MonitorTable.getMonitor(authenticationStack);
    aggregateMonitor = MonitorTable.getAggregatedMonitor();
  }
  
  public void login() throws LoginException {
    isLoginSucceeded = false;
    String userName = null;
    int initializedLoginModulesCount = 0;
    boolean centralChecksMade = false;
    SecuritySession session = null;
    SecurityContext context = null;

    ThreadContext currentThreadContext = SecurityServerFrame.threadContext.getThreadContext();

    if (currentThreadContext != null) {
      if (threadContextId == -1) {
        threadContextId = currentThreadContext.getContextObjectId(SecurityContext.NAME);
      }
      
      context = (SecurityContext) currentThreadContext.getContextObject(threadContextId);
      session = context.getSession();

      if (createSession && (context != null) && !context.isAuthenticationPending() && (session != null)) {
        if (!SecurityContext.getLoginAccessor().getClientLoginSession().isAnonymous()) {
          isReAuthenticationRequested = true;
          
          if (LOCATION.beInfo()) {
            LOCATION.infoT("Re-authentication requested.");
          }
        }
      }
    }

    if (configuration == null) {
      // the login context is not initialized
      sharedState = factory.initializeLoginContext(this, false);
    }

    if (!isSubjectProvided) {
      subject = new Subject();
    }

    boolean passwordChangeFails = false;
    SubjectWrapper subjectWrapper = null;      
    
    for (int i = 0; i < modules.length; i++) {
      if (!modules[i].isIgnored()) {
        initializedLoginModulesCount++;
      }
    }
    
    LoginModuleHelperImpl moduleHelper = factory.getModuleHelper();
    UserContext userContext = factory.getUserContext();            
    
    ClassLoader saveLoader = Thread.currentThread().getContextClassLoader();
    
    try {
      Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
      if (LOCATION.beDebug()) {
        LOCATION.debugT("LOGIN() for auth stack ["+authenticationStack+"].");
      }
      
      int successfulLoginModuleIndex = (Integer) AccessController.doPrivileged(processAction.getAction(configuration, modules, OPERATION_LOGIN, authenticationStack));
      if (successfulLoginModuleIndex != -1) {
        if (sharedState instanceof SharedState) {
          ((SharedState<String, Object>) sharedState).setSuccessfulLoginModuleIndex(successfulLoginModuleIndex);
          if (LOCATION.beDebug()) {
            LOCATION.debugT("The index of the login module that authenticated the subject in the login stack is: {0}", new Object[] {successfulLoginModuleIndex});
          }
        } else {
          throw new LoginException("The shared state object is not an instance of class [" + SharedState.class.getName() + "]");
        }
      }
      
      if (LOCATION.beDebug()) {
        LOCATION.debugT("COMMIT() for auth stack ["+authenticationStack+"].");
      }
      AccessController.doPrivileged(processAction.getAction(configuration, modules, OPERATION_COMMIT, authenticationStack));
      
      if (LOCATION.beDebug()) {
        LOCATION.debugT("CALL CENTRAL CHECKS");
      }
      centralChecksMade = true;
      userPrincipal = (Principal) sharedState.get(AbstractLoginModule.PRINCIPAL);
      subjectWrapper = new SubjectWrapper(subject, userPrincipal);
      
      if (TRACER.beInfo()) {
        TRACER.infoT("Created new {0} from login subject and principal", new Object[] {subjectWrapper});
      }
      
      userName = subjectWrapper.getPrincipal().getName();

      if ((context != null) && createSession) {
        
        UserInfo userInfo = (UserInfo) sharedState.get(USER_INFO);
	      try {
	        moduleHelper.refreshUserInfo(userContext, userName, sharedState);
	
	        if (userInfo == null) {
	          userInfo = moduleHelper.getUserInfo(userContext, userName);
	        }
	      } catch (SecurityException e) {
	        throw new BaseLoginException("Username does not exists in the userstore.", LoginExceptionDetails.USERNAME_IS_NOT_VALID);
	      }
	
        
	      if (isReAuthenticationRequested) {
          String oldUserName = SecurityContext.getLoginAccessor().getClientLoginSession().getPrincipal().getName();
	        String newUserName = userName;
	          
	        if (oldUserName.equalsIgnoreCase(newUserName)) {
	          isReAuthenticationSuccessful = true;
	              
	          if (LOCATION.beInfo()) {
	            LOCATION.infoT("User [{0}] attempt to re-authenticate.", new Object[] {newUserName});
	          }
	        } else {
	          if (LOCATION.beInfo()) {
	            LOCATION.infoT("User is already logged in with user name [{0}] but attempt to re-authenticate with user name [{1}].", new Object[] {oldUserName, newUserName});
	          }
	              
	          // todo: send appropriate response?
	          throw new BaseLoginException("Reauthentication failed.", LoginExceptionDetails.USER_ALREADY_LOGGED_IN);
	        }
	      }
      
	      if (UMFactory.getAnonymousUserFactory().isAnonymousUser(userName) && !userContext.isEmergencyUser(userName)) {
	        throw new BaseLoginException("Cannot login with anonymous user.", LoginExceptionDetails.USER_IS_LOCKED);
	      }
	      
	      if (SecurityServerFrame.isEmergencyMode() && !userContext.isEmergencyUser(userName)) {
	        throw new BaseLoginException("The user is not the emergency user.", LoginExceptionDetails.EMERGENCY_USER_IS_ACTIVE);
	      }
	      
	      if (moduleHelper.isUserAccountExpired(userInfo, userContext, sharedState)) {
	        throw new BaseLoginException("The user account is either expired, or not valid yet.", LoginExceptionDetails.USER_IS_CURRENTLY_NOT_VALID);
	      }
	      
	      moduleHelper.checkUserLockStatus(userContext, userInfo, sharedState);
	        
	      passwordChangeFails = true;
	      if (callbackHandler != null) {
          if (LOCATION.beDebug()) {
            LOCATION.debugT("Call changePasswordIfNeeded. userInfo: "+userInfo + ", subject: "+subject);
          }
          moduleHelper.setSubject( subject ); 
	        moduleHelper.changePasswordIfNeeded(userContext, userInfo, callbackHandler);
	      }
	      passwordChangeFails = false;
	      
	      
        if (isReAuthenticationRequested && isReAuthenticationSuccessful) {
          session.mergeSubject(subjectWrapper);
          
          if (LOCATION.beInfo()) {
            LOCATION.infoT("Re-authentication for user [{0}] successfull.", new Object[] {userName});
          }
        } else {
          if (TRACER.beInfo()) {
            TRACER.infoT("Request new empty client context to be created");
          }
          
          SecurityContext.getLoginAccessor().login();
          session.setSubjectHolder(subjectWrapper);
          session.setAuthenticationConfiguration(authenticationStack);

          if (TRACER.beInfo()) {
            TRACER.infoT("{0} attached to this thread", new Object[] {session});
          }
          
          if (LOCATION.beInfo()) {
            LOCATION.infoT("Authentication for user [{0}] is successful.", new Object[] {userName});
          }
        }
        session.addSecurityPolicyDomain(getSecurityPolicyDomain());
        
        if (SessionRequest.isCheckMarkIdEnabled()) {

          // setting new JSESSIONMARKID starts here
          String newMarkID = MarkIDGenerator.generateMarkID();
          context.getLoginAccessor().setMarkIdToClient(newMarkID);  
           
          if (LOCATION.beDebug()) {
            String jSessionID = context.getLoginAccessor().getClientId();
            LOCATION.debugT("Set new mark ID [{0}] for JSESSIONID [{1}].",new Object[]{newMarkID, jSessionID});
          }
          //setting new JSESSIONMARKID ends here
        
        }
        
        notifyAuthState(AuthStateCallback.PASSED);
      }
        
      isLoginSucceeded = true;
      
    } catch (Exception e) {
      if (LOCATION.beDebug()) {
        LOCATION.traceThrowableT(Severity.DEBUG, "Login failed!", e);
      }

      try {
        if (LOCATION.beDebug()) {
          LOCATION.debugT("ABORT() for auth stack ["+authenticationStack+"].");
        }
        AccessController.doPrivileged(processAction.getAction(configuration, modules, OPERATION_ABORT, authenticationStack));
      } catch (PrivilegedActionException ee) {
        //$JL-EXC$
        // already logged
      } catch (RuntimeException re) {
        String ipAddress = getLogIpAddress(Severity.ERROR, callbackHandler);
        AuthenticationLog.log(Severity.ERROR, true, AuthenticationLog.LOGIN_FAILED, userName, ipAddress, e.getLocalizedMessage(), centralChecksMade, authenticationStack, modules);
        throw re;
      } 

      if (!isSubjectProvided) {
        subject = null;
      }


      Exception exception = e;        
      if (e instanceof PrivilegedActionException) {
        exception = ((PrivilegedActionException) e).getException();
      } 
        
      if (initializedLoginModulesCount == 0) {   
        String ipAddress = getLogIpAddress(Severity.ERROR, callbackHandler);
        AuthenticationLog.log(Severity.ERROR, true, AuthenticationLog.LOGIN_FAILED, userName, ipAddress, exception.getLocalizedMessage(), centralChecksMade, authenticationStack, modules);
      } else if (exception instanceof BaseLoginException) {
        
        int severity = ((BaseLoginException) exception).getLogSeverity();
        
        if (((BaseLoginException) exception).getExceptionCause() == LoginExceptionDetails.NO_LOGIN_MODULE_SUCCEEDED) {
          String ipAddress = getTracesIpAddress(severity, callbackHandler);
          AuthenticationLog.trace(severity, true, AuthenticationLog.LOGIN_FAILED, userName, ipAddress, exception.getLocalizedMessage(), centralChecksMade, authenticationStack, modules);
        } else {
          String ipAddress = getLogIpAddress(severity, callbackHandler);
          AuthenticationLog.log(severity, true, AuthenticationLog.LOGIN_FAILED, userName, ipAddress, exception.getLocalizedMessage(), centralChecksMade, authenticationStack, modules);
        }
      } else if (exception instanceof RuntimeException) { 
        SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, exception, "ASJ.secsrv.000186", "{0}", new Object[]{exception.getLocalizedMessage()});
        String ipAddress = getLogIpAddress(Severity.ERROR, callbackHandler);
        AuthenticationLog.log(Severity.ERROR, true, AuthenticationLog.LOGIN_FAILED, userName, ipAddress, exception.getLocalizedMessage(), centralChecksMade, authenticationStack, modules);
      } else {
        if (LOCATION.beDebug()) {
          LOCATION.traceThrowableT(Severity.DEBUG, exception.getLocalizedMessage(), exception);
        }
        String ipAddress = getLogIpAddress(Severity.INFO, callbackHandler);
        AuthenticationLog.log(Severity.INFO, true, AuthenticationLog.LOGIN_FAILED, userName, ipAddress, exception.getLocalizedMessage(), centralChecksMade, authenticationStack, modules);
      }
      
      if ( ! (exception instanceof LoginExceptionDetails && ((LoginExceptionDetails) exception).getExceptionCause() == LoginExceptionDetails.NO_LOGIN_MODULE_SUCCEEDED) ) { 
        monitorUnsuccessfulLoginAttempt();  
      } 

      BaseLoginException loginException = null;
      if (exception instanceof BaseLoginException) {
        loginException = (BaseLoginException) exception;
      } else if (exception instanceof LoginExceptionDetails) {
        loginException = new BaseLoginException("Access Denied.", exception, ((LoginExceptionDetails) exception).getExceptionCause());
      } else {
        loginException = new BaseLoginException("Access Denied.", exception);
      }

      notifyAuthState(passwordChangeFails ? AuthStateCallback.PASSWORD_CHANGE_FAILED : AuthStateCallback.FAILED, loginException);
      throw loginException;
    } catch (Error e) {
      String ipAddress = getLogIpAddress(Severity.ERROR, callbackHandler);
      AuthenticationLog.log(Severity.ERROR, true, AuthenticationLog.LOGIN_FAILED, userName, ipAddress, e.getLocalizedMessage(), centralChecksMade, authenticationStack, modules);
      throw e;
    } finally {
      Thread.currentThread().setContextClassLoader(saveLoader);
    }
    
    // LOGIN.OK
    String writeLastSuccessfulLogonDate = SecurityServerFrame.getServiceProperties().getProperty("WriteSuccessfulLogonStatistics", "false");
    if ("true".equals(writeLastSuccessfulLogonDate)) {
      try {          
   		  UserInfo userInfo = moduleHelper.getUserInfo(userContext, userName);
   		  Integer successfulLogonCount = (Integer)userInfo.readUserProperty(UserContext.PROPERTY_SUCCESSFUL_LOGON_COUNT);
   		  Date lastSuccessfulLogonDate = (Date)userInfo.readUserProperty(UserContext.PROPERTY_LAST_SUCCESSFUL_LOGON_DATE);
   		  Map properties = new HashMap();
   		  successfulLogonCount = new Integer(successfulLogonCount.intValue()+1);
   		  lastSuccessfulLogonDate = new Date();
   		  properties.put(new Integer(UserContext.PROPERTY_SUCCESSFUL_LOGON_COUNT), successfulLogonCount);
   		  properties.put(new Integer(UserContext.PROPERTY_LAST_SUCCESSFUL_LOGON_DATE), lastSuccessfulLogonDate);
   		  userInfo.writeUserProperty(properties);
   	  }
   	  catch (Exception e) {
   	    SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "ASJ.secsrv.000187", "Error reading/writing properties for successful logon", e);
   	  }
    }      
    String ipAddress = getLogIpAddress(Severity.INFO, callbackHandler);
    AuthenticationLog.log(Severity.INFO, true, AuthenticationLog.LOGIN_OK, userName, ipAddress, null, centralChecksMade, authenticationStack, modules);
  }

  public void logout() throws LoginException {
    String userName = null;
    SecurityContext sc = SecurityServerFrame.getCurrentSecurityContextObject();
    SecuritySession session = (sc != null) ? sc.getSession() : null;
 
    if (subject == null) {
      throw new BaseLoginException("Call login before logout.", LoginExceptionDetails.USER_NOT_LOGGED_IN);
    }
    
    if (session != null) {
      Principal principal = session.getPrincipal();
      
      if (principal != null) {
        userName = principal.getName();
      }
    }
    
    if (configuration == null) {
      // the login context is not initialized
      sharedState = factory.initializeLoginContext(this, true);
    }

    try {
      if (LOCATION.beDebug()) {
        LOCATION.debugT("LOGOUT() for auth stack ["+authenticationStack+"].");
        }
        AccessController.doPrivileged(processAction.getAction(configuration, modules, OPERATION_LOGOUT, authenticationStack));
      } catch (PrivilegedActionException e) {
 
        if (!isSubjectProvided) {
          subject = null;
        }

        Exception exception = e.getException();

        // LOGOUT.FAILED
      String ipAddress = getLogIpAddress(Severity.INFO, callbackHandler);
      AuthenticationLog.log(Severity.INFO, false, AuthenticationLog.LOGOUT_FAILED, userName, ipAddress, e.getLocalizedMessage(), false, authenticationStack, modules);

      if (exception instanceof LoginExceptionDetails) {
        throw new BaseLoginException("Cannot logout user.", exception, ((LoginExceptionDetails) exception).getExceptionCause());
      } else {
        throw new BaseLoginException("Cannot logout user.", exception);
      }
    } catch (RuntimeException e) {
      String ipAddress = getLogIpAddress(Severity.ERROR, callbackHandler);
      AuthenticationLog.log(Severity.ERROR, false, AuthenticationLog.LOGOUT_FAILED, userName, ipAddress, e.getLocalizedMessage(), false, authenticationStack, modules);
      throw e;
    }
          
    if (session != null) {
      Principal principal = session.getPrincipal();

      session.logout();
      
      // LOGOUT.OK
      String ipAddress = getLogIpAddress(Severity.INFO, callbackHandler);
      AuthenticationLog.log(Severity.INFO, false, AuthenticationLog.LOGOUT_OK, userName, ipAddress, null, false, authenticationStack, modules);
    } 
  }

  public void close() {
    factory.close(this);
  }

  protected void logoutSession() throws LoginException {
    factory.initializeLoginContext(this, true);
    logout();
  }

  void set(SecuritySessionPool sessions, Subject subject, LoginModuleLoggingWrapperImpl[] modules, AppConfigurationEntry[] configuration) {
    this.configuration = configuration;
    this.subject = subject;
    this.modules = modules;
    this.isSubjectProvided = (subject != null);
    this.processAction = new ModulesProcessAction();
  }

  void set(Subject subject, CallbackHandler handler) {
    this.subject = subject;
    this.callbackHandler = handler;
  }

  private void monitorUnsuccessfulLoginAttempt() {
    policyMonitor.failedLogonCount++;
    aggregateMonitor.failedLogonCount++;
  }

  private void notifyAuthState(AuthStateCallback cbk) throws IOException, BaseLoginException {
    try {
      if (callbackHandler != null) {
        this.callbackHandler.handle(new Callback[] {cbk});
      }
    } catch (UnsupportedCallbackException e) {
      if (LOCATION.beDebug()) {
        LOCATION.traceThrowableT(Severity.DEBUG, "Unsupported callback.", e);
      }
    } catch (IllegalStateException ex) {
      if (LOCATION.beWarning()) {
        LOCATION.traceThrowableT(Severity.WARNING, "Error setting authentication result in session.", ex);
      } 
      throw new BaseLoginException("Maximum number of login sessions reached. Check Shared Memory configuration and set property [BrSessions] to a larger value.", ex, LoginExceptionDetails.ACTIVE_USERS_LIMIT_RECHED, Severity.WARNING);
    }
  }

  private void notifyAuthState(AuthStateCallback cbk, BaseLoginException exception) {
    try {
      if (callbackHandler != null) {
      	LanguageCallback languageCallback = null;
      	Locale locale = null;
        String message = null;
      	TextOutputCallback textCallback = null;
      	
      	try {
          languageCallback = new LanguageCallback();
          this.callbackHandler.handle(new Callback[] {languageCallback});
          locale = languageCallback.getLocale();
      	} catch (Exception e) {
          if (LOCATION.beInfo()) {
            LOCATION.traceThrowableT(Severity.INFO, "Handle of language callback failed. The default language for the engine will be used for localization of the login messages.", e);
          }
          
          if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
          }
      	}
      	
      	if (locale != null) {
          message = getLocalizedMessage(exception, locale);
      	} else {
          message = getLocalizedMessage(exception, DEFAULT_LOCALE);
      	}

        if (message != null) {
          try {
            textCallback = new TextOutputCallback(TextOutputCallback.ERROR, message);
            this.callbackHandler.handle(new Callback[] {textCallback});
          } catch (Exception e) {
            if (LOCATION.beInfo()) {
              LOCATION.traceThrowableT(Severity.INFO, "Handle of TextOutputCallback failed. No error message will be displayed to the user.", e);
            }
            
            if (e instanceof RuntimeException) {
              throw (RuntimeException) e;
            }
          }
        }
        cbk.setShowErrorOnLogonPage(exception.getExceptionCause() == LoginExceptionDetails.CERTIFICATE_IS_NOT_TRUSTED);        
        this.callbackHandler.handle(new Callback[] {cbk});
      }
    } catch (Exception e) {
      if (LOCATION.beDebug()) {
        LOCATION.traceThrowableT(Severity.DEBUG, "Failed to send authentication status callback.", e);
      }
      
      if (e instanceof RuntimeException) {
        throw (RuntimeException) e;
      }
    }
  }
  
  private String getLocalizedMessage(BaseLoginException exception, Locale locale) {
    int cause = exception.getExceptionCause();
    String message = null;
    
    if (LOCATION.beDebug()) {
      LOCATION.debugT("Getting message to be displayed to the user for exception cause " + cause);
    }

    if ((cause >= 0) && (cause < LOGIN_MESSAGE_KEYS.length)) {
      if (cause == LoginExceptionDetails.CHANGE_PASSWORD_FAILED) {
        Exception exc = exception;
        String localizedMessage = null;
        
        while (exc.getCause() instanceof Exception) {
          exc = (Exception) exc.getCause();
          localizedMessage = UMFactory.getSecurityPolicy().getLocalizedMessage(locale, exc);

          if (!ResourceBean.DEFAULT_LABEL.equals(localizedMessage)) {
            message = localizedMessage;
            break;
          }
        }

        if (message == null) {
          message = SecurityResourceAccessor.getResourceAccessor().getMessageText(locale, LOGIN_MESSAGE_KEYS[26]);
        }
      } else {
        String loginMessage = null;
        
        if ((cause == 0) && (this.callbackHandler instanceof AbstractWebCallbackHandler)) {
          String[] parameters = null;
          
          try {
            HttpGetterCallback callback = new HttpGetterCallback();
            callback.setType(HttpCallback.REQUEST_PARAMETER);
            callback.setName("j_sap_current_password");
            
            this.callbackHandler.handle(new Callback[] {callback});
            
            parameters = (String[]) callback.getValue();
          } catch (Exception e) {
            if (LOCATION.beDebug()) {
              LOCATION.traceThrowableT(Severity.DEBUG, "Failed to get callback.", e);
            }
            
            if (e instanceof RuntimeException) {
              throw (RuntimeException) e;
            }
          }
          
          if ((parameters != null) && (parameters.length > 0)) {
            String currentPassword = parameters[0];
            
            if ((currentPassword == null) || currentPassword.equals("")) {
              loginMessage = LOGIN_MESSAGE_KEYS[22];
            } else {
              loginMessage = LOGIN_MESSAGE_KEYS[26];
            }
          } else {
            loginMessage = LOGIN_MESSAGE_KEYS[cause];
          }
        } else {
          loginMessage = LOGIN_MESSAGE_KEYS[cause];
        }
        
        if (loginMessage != null) {
          message = SecurityResourceAccessor.getResourceAccessor().getMessageText(locale, loginMessage);
        }
      }
    }
    
    if (LOCATION.beDebug()) {
      LOCATION.debugT("The localized message to be dispalyed to the user is " + message);
    }
    
    return message;
  }

  private boolean isLoginNeeded(com.sap.engine.interfaces.security.SecuritySession session, String policyDomain) {
  	return !session.getSecurityPolicyDomains().contains(policyDomain);
  }
  
  private String getSecurityPolicyDomain() {
    final String METHOD_NAME = "getSecurityPolicyDomain";
    if (LOCATION.beDebug()) {
      LOCATION.entering(METHOD_NAME);
    }
    
    String securityPolicyDomain = null;
    ThreadContext currentThreadContext = SecurityServerFrame.threadContext.getThreadContext();
    if (currentThreadContext != null) {
      if (threadContextId == -1) {
        threadContextId = currentThreadContext.getContextObjectId(com.sap.engine.services.security.login.SecurityContext.NAME);
      }
      com.sap.engine.services.security.login.SecurityContext context = 
        (com.sap.engine.services.security.login.SecurityContext) currentThreadContext.getContextObject(threadContextId);
      
      securityPolicyDomain = context.getSecurityPolicyDomain();
      if (LOCATION.beDebug()) {
        LOCATION.debugT("Security policy domain retrieved from thread context: " + securityPolicyDomain);
      }
    } else {
      securityPolicyDomain = factory.getSecurityPolicyDomain();
      if (LOCATION.beDebug()) {
        LOCATION.debugT("Security policy domain retrieved from authentication context: " + securityPolicyDomain);
      } 
    }
    
    if (LOCATION.beDebug()) {
      LOCATION.exiting(METHOD_NAME, securityPolicyDomain);
    } 
    return securityPolicyDomain;
  }
  
  public Subject getSubject() {
    Subject sub = super.getSubject();
    
    if (sub != null) {
      return sub;
    }
    
    if (isLoginSucceeded && isSubjectProvided) {
      return subject;
    }
    
    return null;
  }

  AppConfigurationEntry[] getConfiguration(){
    return factory.getConfiguration();
  }
  
  Object[] getModuleClasses(){
    return factory.getModuleClasses();
  }
  
  private String getTracesIpAddress(int severity, CallbackHandler cbkHandler){
    if (LOCATION_TABLE.beLogged(severity)){
      return obtainIpAddress(cbkHandler);
    } else {
      return null;
    }
  }
  
  private String getLogIpAddress(int severity, CallbackHandler cbkHandler){
    if (CATEGORY.beLogged(severity)){
      return obtainIpAddress(cbkHandler);
    } else {
      return null;
    }
  }
  
  /**
   * @param cbkHandler is an instance of an WebCallbackHandler created for the current authentication request
   * @return string representation of IP address, the authentication request is made from.
   */
  private String obtainIpAddress(CallbackHandler cbkHandler){
    if (cbkHandler != null && cbkHandler instanceof AbstractWebCallbackHandler) {
      AbstractWebCallbackHandler webCbkHandler = (AbstractWebCallbackHandler) cbkHandler;
      return webCbkHandler.getIpAddress();
    } else {
      return null;
    }
  }

}

