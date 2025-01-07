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

import java.security.PrivilegedExceptionAction;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.LoginException;

import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.lib.security.LoginExceptionDetails;
import com.sap.engine.services.security.Util;
import com.sap.engine.services.security.exceptions.BaseLoginException;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.LogRecord;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * PrivilegedExceptionAction for calling login modules.
 *
 * @author Svetlana Stancheva
 * @version 6.30
 */
public class ModulesProcessAction implements PrivilegedExceptionAction {

  private static final Location LOCATION = Location.getLocation(AuthenticationTraces.LOGIN_CONTEXT_LOCATION);
  
  private AppConfigurationEntry[] configuration = null;
  private LoginModuleLoggingWrapperImpl[] modules = null;
  private byte type = -1;
  String authStack = null;

  public ModulesProcessAction() {
  }

  public Object run() throws LoginException {
    // Logout operation
    if (type == FastLoginContext.OPERATION_LOGOUT) {
      logout();
      return -1;
    }
    
    // Login operation
    int successfulLoginModule = -1;
    boolean processed = false;
    boolean localProcessed = false;
    LoginException exception = null;
    LoginException weakException = null;

    for (int i = 0; i < modules.length; i++) {
      localProcessed = false;
	
      try {
        switch (type) {
	        case FastLoginContext.OPERATION_LOGIN: {
	          localProcessed = modules[i].login();
	          if (localProcessed && (successfulLoginModule == -1) && (configuration[i].getControlFlag() != AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL)) {
	            successfulLoginModule = i;
	          }
	          break;
	        }
	          case FastLoginContext.OPERATION_ABORT: {
	            localProcessed = modules[i].abort();
              break;
	          }
	          case FastLoginContext.OPERATION_COMMIT: {
              localProcessed = modules[i].commit();
              break;
	          }
	        }
	
	    if (localProcessed) {
	      weakException = null;
	      processed = true;
	      	          
	      if ((type == FastLoginContext.OPERATION_LOGIN) && (configuration[i].getControlFlag() == AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT)) {
	        break;
	      }
	    }
	  } catch (LoginException e) {	
	    if (configuration[i].getControlFlag() == AppConfigurationEntry.LoginModuleControlFlag.REQUISITE) {
	      if (type == FastLoginContext.OPERATION_ABORT) {
	        if (exception == null) {
	          exception = e;
	        }
	      } else {
	        throw e;
	      }
	    } else if (configuration[i].getControlFlag() == AppConfigurationEntry.LoginModuleControlFlag.REQUIRED) {
	      if (exception == null) {
	        exception = e;
	      }
        } else if ((configuration[i].getControlFlag() == AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT)
                || (configuration[i].getControlFlag() == AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL)) {
	      if (weakException == null) {
	        weakException = e;
	      }
	    }
	  } catch (Throwable e) {
	    SimpleLogger.log(Severity.INFO, Util.SEC_SRV_CATEGORY, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000208", "Login module {0} from authentication stack {1} errors while authenticating the caller. Most probably the authentication stack is not set up correctly.", new Object[] {modules[i].getClass().getName(), this.authStack});
      traceThrowable(e);
	  }
    }

    if (exception != null) {     
      if (exception instanceof BaseLoginException) {
        throw new BaseLoginException("Authentication failed.", exception, ((BaseLoginException) exception).getExceptionCause(), ((BaseLoginException) exception).getLogSeverity());
      } else if (exception instanceof LoginExceptionDetails) {
        throw new BaseLoginException("Authentication failed.", exception, ((LoginExceptionDetails) exception).getExceptionCause());
      } else {
        throw new BaseLoginException("Authentication failed.", exception);
      }
    }

    if (!processed && (weakException != null)) {
      if (weakException instanceof BaseLoginException) {
        throw new BaseLoginException("Cannot authenticate the user.", weakException, ((BaseLoginException) weakException).getExceptionCause(), ((BaseLoginException) weakException).getLogSeverity());
      } else if (weakException instanceof LoginExceptionDetails) {
        throw new BaseLoginException("Cannot authenticate the user.", weakException, ((LoginExceptionDetails) weakException).getExceptionCause());
      } else {
        throw new BaseLoginException("Cannot authenticate the user.", weakException);
      }
    }

    if (!processed) {
      if (LOCATION.beDebug()) {
        LOCATION.debugT("Unsuccessful login: no login module succeeded. The size of the used authentication stack {0} is {1}.", new Object[] {this.authStack, new Integer(modules.length)});
      }
      throw new BaseLoginException("Login failed.", LoginExceptionDetails.NO_LOGIN_MODULE_SUCCEEDED, Severity.DEBUG);
    }

    return successfulLoginModule;
  }

  protected ModulesProcessAction getAction(AppConfigurationEntry[] configuration, LoginModuleLoggingWrapperImpl[] modules, byte type, String authStack) {
    this.authStack = authStack;
    this.configuration = configuration;
    this.modules = modules;
    this.type = type;
    return this;
  }

  private void logout() throws LoginException {
    LoginException exception = null;
  
    for (int i = 0; i < modules.length; i++) {
      try {
        modules[i].logout();
      } catch (LoginException e) {  
        exception = e;
      } catch (Throwable e) {
        SimpleLogger.log(Severity.INFO, Util.SEC_SRV_CATEGORY, Util.SEC_SRV_LOCATION, "ASJ.secsrv.000198", "{0}", new Object[] {e.getMessage()});
        traceThrowable(e);
      }
    }

    if (exception != null) {  
      if (exception instanceof BaseLoginException) {
        throw exception;
      } else {
        throw new BaseLoginException("Authentication failed.", exception);
      }
    }
  }

  private final void traceThrowable(Throwable throwable) {
    BaseLoginException ble = new BaseLoginException("Error in some of the login modules.", throwable);

    SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, ble, "ASJ.secsrv.000198", "{0}", new Object[] {ble});
    LogRecord record = SimpleLogger.trace(Severity.ERROR, LOCATION, "ASJ.secsrv.000199", "Exception {0}", new Object[] { ble.getMessage() });
        
    if (record != null) {
      throw new SecurityException("Internal server error. An error log with ID [" + record.getId() + "] is created. For more information contact your system administrator.");
    } else {
      throw new SecurityException("Internal server error.");
    }
  }

}