package com.sap.engine.services.security.login;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.services.security.exceptions.BaseLoginException;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * Wraps each login module and writes logs and traces about the result of the
 * invocation of each login module method.
 *
 * @author Svetlana Stancheva
 * @version 6.40
 */
public class LoginModuleLoggingWrapperImpl implements LoginModule {

  public static final String OPTIONAL_CONTROL_FLAG = "OPTIONAL";
  public static final String REQUIRED_CONTROL_FLAG = "REQUIRED";
  public static final String REQUISITE_CONTROL_FLAG = "REQUISITE";
  public static final String SUFFICIENT_CONTROL_FLAG = "SUFFICIENT";
  public static final String UNKNOWN_CONTROL_FLAG = "unknown";

  private static final Location LOCATION = Location.getLocation(AuthenticationTraces.LOGIN_CONTEXT_LOCATION);
  
  private final String SPACING = "                                                                                                                                 ";
  private final String SPACING_NEXT_LINE = "                                                                                                                                    ";
  
  private AppConfigurationEntry appConfigurationEntry = null;
  private String loginModuleClassName = null;
  private Class moduleClass = null;
  private LoginModule loginModule = null;
  private StringBuffer logMessages = null;
  private List<Map.Entry<String, String>> optionsArray; 
  private Map sharedState = null;

  private Throwable loadException = null;
  private Throwable instantiationException = null;
  private Throwable initializationException = null;
  private Throwable loginException = null;
  private Throwable commitException = null;
  private Throwable abortException = null;
  private Throwable logoutException = null;

  private boolean isLoadOK = false;
  private boolean isInstantiationOK = false;
  private boolean isInitializationOK = false;
  private boolean isLoginOK = false;
  private boolean isCommitOK = false;
  private boolean isAbortOK = false;
  private boolean isLogoutOK = false;

  private boolean isInstantiationCalled = false;
  private boolean isInitializationCalled = false;
  private boolean isLoginCalled = false;
  private boolean isCommitCalled = false;
  private boolean isAbortCalled = false;
  private boolean isLogoutCalled = false;
  
  private boolean isLoginDetailsMultiline = false;

  public LoginModuleLoggingWrapperImpl(AppConfigurationEntry appConfigurationEntry, Class loadedClass) {
    this(appConfigurationEntry);

    this.moduleClass = loadedClass;
    this.isLoadOK = (moduleClass != null);
  }

  public LoginModuleLoggingWrapperImpl(AppConfigurationEntry appConfigurationEntry, Throwable exception) {
    this(appConfigurationEntry);

    this.loadException = exception;
  }

  private LoginModuleLoggingWrapperImpl(AppConfigurationEntry appConfigurationEntry) {
    this.appConfigurationEntry = appConfigurationEntry;
    this.loginModuleClassName = appConfigurationEntry.getLoginModuleName();
  }

  public String getLoginModuleClassName() {
    return loginModuleClassName;
  }

  public String getLogMessages() {
    if (logMessages == null) {
      logMessages = new StringBuffer(SPACING);

      logLoginModuleClass();
      logControlFlag();

      logOperationStatus(isLoadOK, loadException);

      logInstantiation();

      logInitialization();
    } else {
      logMessages.replace(AuthenticationLog.LOGIN_INDEX, logMessages.length(), "                                 ");
    }

    boolean exceptionCheck = isLoadOK && isInstantiationOK && isInitializationOK;
    logOperationStatus(isLoginCalled, isLoginOK, exceptionCheck ? loginException : null, AuthenticationLog.LOGIN_INDEX);
    logOperationStatus(isCommitCalled, isCommitOK, exceptionCheck ? commitException : null, AuthenticationLog.COMMIT_INDEX);
    logOperationStatus(isAbortCalled, isAbortOK, exceptionCheck ? abortException : null, AuthenticationLog.ABORT_INDEX);
    
    if (exceptionCheck && isLoginOK && isCommitOK) {
      logDetails();
    }
    
    logLogout();
    
    logOptions();
    
    reinitialize();
    
    return logMessages.toString();
  }
  
  private void logOptions() {
    if (!isLoginDetailsMultiline) {
      String optionString = getOptions();
      if (optionString != "") {
        logMessages.append("\n");
        logMessages.append(optionString);
      }
    }
  }
  
  private String getOptions() {
    if (optionsArray == null || optionsArray.size() == 0)
      return "";

    int index = 1;
    StringBuffer sb = new StringBuffer();
    Iterator it = optionsArray.iterator();
    while (it.hasNext()) {
    	Map.Entry entry = (Map.Entry) it.next();
    	Object key = entry.getKey();
    	Object value = entry.getValue();
    	sb.append("        #" + index + " " + key + " = " + value + (it.hasNext() ? "\n" : ""));
    	index++;
    }
    return sb.toString();
  }

  public void instantiateLoginModule() {
    if (isLoadOK) {
      isInstantiationCalled = true;

      try {
        loginModule = (LoginModule) moduleClass.newInstance();

        if (loginModule != null) {
          isInstantiationOK = true;
        }
      } catch (Exception t) {
        instantiationException = t;
        SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, t, "ASJ.secsrv.000194", "Cannot instantiate login module class {0}.", new Object[] {loginModuleClassName});
      }
    }
  }

  public boolean isIgnored() {
    return !isLoadOK || !isInstantiationOK || !isInitializationOK;
  }
  
  public boolean isSuccessful() {
    return isLoginOK && isCommitOK;
  }

  public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
    if (isInstantiationOK) {
      isInitializationCalled = true;
      this.sharedState = sharedState;
      
      if (options != null && options.size() != 0) {
        TreeMap sortedOptions = new TreeMap(new Comparator() {
          public int compare (Object o1, Object o2) {
            return ((String)o1).compareToIgnoreCase((String)o2);
          }
        });    
        
        sortedOptions.putAll (options);        
        optionsArray = new ArrayList<Map.Entry<String,String>>(options.size());
        Iterator it = sortedOptions.entrySet().iterator();
        while (it.hasNext()) {
          Map.Entry<String,String> entry = (Map.Entry<String,String>) it.next();
          optionsArray.add(entry);
        }
      }

      try {
        loginModule.initialize(subject, callbackHandler, sharedState, options);
        isInitializationOK = true;
      } catch (ThreadDeath td) {
        initializationException = td;
        throw td;
      } catch (OutOfMemoryError oome) {
        initializationException = oome;
        throw oome;
      } catch (Throwable t) {
        initializationException = t;
        SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, t, "ASJ.secsrv.000195", "Cannot initialize login module {0}.", new Object[] {loginModuleClassName});
      }
    }
  }

  private void checkStatus() throws BaseLoginException {
    
    if (!isLoadOK) {
      if (loadException == null) {
        throw new BaseLoginException("The login module is not loaded.");
      } else {
        throw new BaseLoginException("The login module is not loaded.", loadException);
      }
    } 
    
    if (!isInstantiationOK) {
      if (instantiationException == null) {
        throw new BaseLoginException("The login module is not instantiated.");
      } else {
        throw new BaseLoginException("The login module is not instantiated.", instantiationException);
      }
    }

    if (!isInitializationOK) {
      if (initializationException == null) {
        throw new BaseLoginException("The login module is not initialized.");
      } else {
        throw new BaseLoginException("The login module is not initialized.", initializationException);
      }
    }
  }

  public boolean login() throws LoginException {
    
    isLoginCalled = true;

    try {
      
      checkStatus();
    
      isLoginOK = loginModule.login();    
    
    } catch (LoginException e) {
      loginException = e;

      throw e;
    } catch (RuntimeException e) {
      loginException = e;

      throw e;
    } catch (Error e) {
      loginException = e;

      throw e;
    }

    return isLoginOK;
  }

  public boolean commit() throws LoginException {

    isCommitCalled = true;
    
    if (isIgnored()) { 
      isCommitOK = false;
      return isCommitOK;
    }

    try {
      
      isCommitOK = loginModule.commit();
      
    } catch (LoginException e) {
      commitException = e;

      throw e;
    } catch (RuntimeException e) {
      commitException = e;

      throw e;
    } catch (Error e) {
      commitException = e;

      throw e;
    }

    return isCommitOK;
  }

  public boolean abort() throws LoginException {

    isAbortCalled = true;
    
    if (isIgnored()) {
      isAbortOK = false;
      return isAbortOK;
    }

    try {

      isAbortOK = loginModule.abort();
      
    } catch (LoginException e) {
      abortException = e;

      throw e;
    } catch (RuntimeException e) {
      abortException = e;

      throw e;
    } catch (Error e) {
      abortException = e;

      throw e;
    }

    return isAbortOK;
  }

  public boolean logout() throws LoginException {
    isLogoutCalled = true;
    
    try {
      
      checkStatus();
      
      isLogoutOK = loginModule.logout();
    } catch (LoginException e) {
      logoutException = e;

      throw e;
    } catch (RuntimeException e) {
      logoutException = e;

      throw e;
    } catch (Error e) {
      logoutException = e;

      throw e;
    }

    return isLogoutOK;
  }

  private String convertControlFlag(AppConfigurationEntry.LoginModuleControlFlag flag) {
    if (flag == AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL) {
      return OPTIONAL_CONTROL_FLAG;
    } else if (flag == AppConfigurationEntry.LoginModuleControlFlag.REQUIRED) {
      return REQUIRED_CONTROL_FLAG;
    } else if (flag == AppConfigurationEntry.LoginModuleControlFlag.REQUISITE) {
      return REQUISITE_CONTROL_FLAG;
    } else if (flag == AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT) {
      return SUFFICIENT_CONTROL_FLAG;
    } else {
      return UNKNOWN_CONTROL_FLAG;
    }
  }
  
  private void logDetails() {
    if (moduleClass != null) {
      String message = (String) sharedState.get(moduleClass.getName() + AuthenticationLog.LOGIN_MODULE_DETAILS_MESSAGE);
      
      if (message != null) {
        String[] parts = message.split("\n");
        
        if (parts.length > 1) {
          isLoginDetailsMultiline = true;
          
          int lineIndex = 0;
          int optionIndex = 0;
          
          for (String part : parts) {
            if (lineIndex == 0) {
              logMessages.append(part);
            } else {
              logMessages.append("\n");
              
              int beg = logMessages.length();
              
              logMessages.append(SPACING_NEXT_LINE);
              logMessages.append(part);
     
              if (optionsArray != null) {
                if (optionIndex < optionsArray.size()) {
                  Map.Entry<String, String> entry = optionsArray.get(optionIndex);
                  String optString = "        #" + (optionIndex + 1) + " " + entry.getKey() + " = " + entry.getValue();
                  logMessages.replace(beg, beg + optString.length(), optString);
                  optionIndex++;
                }
              }
            }
            
            lineIndex++;
          }
          
          if (optionsArray != null) {
            while (optionIndex < optionsArray.size()) {
              Map.Entry<String, String> entry = optionsArray.get(optionIndex);
              String optString = "\n        #" + (optionIndex + 1) + " " + entry.getKey() + " = " + entry.getValue();
              logMessages.append(optString);
              optionIndex++;
            }
          }
        } else {
          logMessages.append(parts[0]);
        }
      }
    }
  }

  private void logException(int beginIndex, Throwable t) {
    logMessages.replace(beginIndex, (beginIndex + AuthenticationLog.EXCEPTION.length()), AuthenticationLog.EXCEPTION);
    logMessages.append(t.getLocalizedMessage());
  }
  
  private void logExceptionOnLogout(Throwable t) {
    logMessages.replace(AuthenticationLog.LOGOUT_INDEX, (AuthenticationLog.LOGOUT_INDEX + AuthenticationLog.EXCEPTION.length()), AuthenticationLog.EXCEPTION);
    logMessages.delete(AuthenticationLog.LOGOUT_DETAILS_INDEX, logMessages.length());
    logMessages.append(t.getLocalizedMessage());
  }

  private void logStatus(int beginIndex, boolean status) {
    String returnStatus = new Boolean(status).toString();
    logMessages.replace(beginIndex, (beginIndex + returnStatus.length()), returnStatus);
  }
  
  private void logStatusOnLogout(boolean status) {
    logStatus(AuthenticationLog.LOGOUT_INDEX, status);
    logMessages.delete(AuthenticationLog.LOGOUT_DETAILS_INDEX, logMessages.length());
  }

  private void logOperationStatus(boolean isOperationOK, Throwable exception) {
    if (!isOperationOK) {
      if (exception != null) {
        logMessages.append(exception.getLocalizedMessage());
      } 
    }
  }

  private void logOperationStatus(boolean isCalled, boolean isOperationOK, Throwable exception, int index) {
    if (isCalled) {
      if (exception == null) {
        logStatus(index, isOperationOK);
      } else {
        logException(index, exception);
      }
    }
  }

  private void logLoginModuleClass() {
    int length = loginModuleClassName.length();

    if (length < (AuthenticationLog.FLAG_INDEX - 2)) {
      logMessages.replace(AuthenticationLog.START_INDEX, AuthenticationLog.START_INDEX + length, loginModuleClassName);
    } else {
      logMessages.replace(AuthenticationLog.START_INDEX, AuthenticationLog.START_INDEX + AuthenticationLog.PREFIX.length(), AuthenticationLog.PREFIX);
      String name = loginModuleClassName.substring(length - (AuthenticationLog.FLAG_INDEX - 2));
      logMessages.replace(AuthenticationLog.NAME_INDEX, (AuthenticationLog.NAME_INDEX + name.length()), name);
    }
  }

  private void logControlFlag() {
    String flag = convertControlFlag(appConfigurationEntry.getControlFlag());
    logMessages.replace(AuthenticationLog.FLAG_INDEX, AuthenticationLog.FLAG_INDEX + flag.length(), flag);
  }

  private void logInstantiation() {
    if (isInstantiationCalled) {
      logOperationStatus(isInstantiationOK, instantiationException);
    }
  }

  private void logInitialization() {
    if (isInitializationCalled) {
      if (isInitializationOK) {
        logMessages.replace(AuthenticationLog.INITIALIZE_INDEX, (AuthenticationLog.INITIALIZE_INDEX + AuthenticationLog.OK.length()), AuthenticationLog.OK);
      } else {
        if (initializationException != null) {
          logException(AuthenticationLog.INITIALIZE_INDEX, initializationException);
        } else {
          logMessages.append("Cannot initialize login module class.");
        }
      }
    }
  }
  
  private void logLogout() {
    if (isLogoutCalled) {
      if (logoutException == null) {
        logStatusOnLogout(isLogoutOK);
      } else {
        logExceptionOnLogout(logoutException);
      }
    }
  }

  private void reinitialize() {
    loginException = null;
    commitException = null;
    abortException = null;
    logoutException = null;

    isLoginOK = false;
    isCommitOK = false;
    isAbortOK = false;
    isLogoutOK = false;

    isLoginCalled = false;
    isCommitCalled = false;
    isAbortCalled = false;
    isLogoutCalled = false;
  }
}
