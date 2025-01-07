/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.jndi.persistent.exceptions;

import com.sap.exception.BaseExceptionInfo;
import com.sap.exception.IBaseException;
import com.sap.localization.LocalizableTextFormatter;
import com.sap.localization.LocalizableText;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.engine.services.jndi.persistent.JNDIResourceAccessor;

import java.util.Locale;
import java.util.TimeZone;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.PrintStream;

/*
 *
 * @author Elitsa Pancheva
 * @version 6.30
 */
public class NamingException extends javax.naming.NamingException implements IBaseException {
  private BaseExceptionInfo exceptionInfo = null;
  static final long serialVersionUID = -2705913835782139546L;
  public static String NAMING_NOT_STARTED = "jndi_registry_0015";
  public static String NOT_CORRECT_NAME_PARAMETER = "jndi_registry_0022";
  public static String CANNOT_LOOKUP = "jndi_registry_0023";
  public static String NO_CONNECTION_WITH_SERVER = "jndi_registry_0024";
  public static String CANNOT_RESOLVE_OBJECT_REFERENCE = "jndi_registry_0025";
  public static String BAD_CORBA_NAME = "jndi_registry_0027";
  public static String PROTOCOL_EXCEPTION = "jndi_registry_0030";
  public static String CANNOT_BIND = "jndi_registry_0032";
  public static String CANNOT_REBIND = "jndi_registry_0035";
  public static String CANNOT_UNBIND = "jndi_registry_0038";
  public static String CANNOT_RENAME = "jndi_registry_0040";
  public static String CANNOT_LIST   = "jndi_registry_0042";
  public static String CANNOT_SEARCH   = "jndi_registry_0044";
  public static String CANNOT_LIST_BINDINGS = "jndi_registry_0046";
  public static String CANNOT_DESTROY_SUBCONTEXT = "jndi_registry_0048";
  public static String CANNOT_CREATE_SUBCONTEXT = "jndi_registry_0051";
  public static String CANNOT_GET_ATTRIBUTES = "jndi_registry_0053";
  public static String CANNOT_MODIFY_ATTRIBUTES = "jndi_registry_0055";
  public static String CANNOT_ADD_TO_ENVIROMENT = "jndi_registry_0058";
  public static String CANNOT_CLOSE = "jndi_registry_0060";
  public static String CANNOT_PRINT = "jndi_registry_0062";
  public static String CANNOT_START_NAMING ="jndi_registry_0065";
  public static String REMOTE_EXCEPTION_IN_START_OF_NAMING = "jndi_registry_0066";
  public static String ERROR_CLOSING_OBJECT_FACTORIES = "jndi_registry_0067";
  public static String CANNOT_GET_INITIAL_CONTEXT = "jndi_registry_0068";
  public static String USER_NOT_LOGGED_IN_IN_GET_INITIAL_CONTEXT = "jndi_registry_0069";
  public static String ERROR_IN_LOGGING_IN_ON_SERVER = "jndi_registry_0070";
  public static String CANNOT_GET_NAMING_ON_SERVER = "jndi_registry_0071";
  public static String NO_SERVER_RUNNING = "jndi_registry_0072";
  public static String WRONG_URL = "jndi_registry_0073";
  public static String ERROR_IN_NAMING_ENUMERATION = "jndi_registry_0074";
  public static String ERROR_IN_GETTING_NEXT_ELEMENT_OF_NAMING_ENUMERATION = "jndi_registry_0075";
  public static String CANNOT_DETERMINE_CONTEXT_NAME = "jndi_registry_0085";
  public static String CANNOT_TRANSFORM_NAME_PARAMETER = "jndi_registry_0086";
  public static String PATH_TO_OBJECT_DOES_NOT_EXISTS = "jndi_registry_0087";
  public static String INTERMEDIATE_CONTEXT_NOT_FOUND = "jndi_registry_0088";
  public static String INVALID_NAME_PARAMETER = "jndi_registry_0089";
  public static String ERROR_IN_PROCESSING_NAME_PARAMETER = "jndi_registry_0090";
  public static String BIND_LOCAL_OBJECT_IN_GLOBAL_CONTEXT = "jndi_registry_0091";
  public static String ATTEMPT_TO_REBIND_OVER_CONTEXT = "jndi_registry_0092";
  public static String REBIND_GLOBAL_OBJECT_OVER_LOCAL_OBJECT = "jndi_registry_0093";
  public static String REBIND_LOCAL_OBJECT_OVER_GLOBAL_OBJECT = "jndi_registry_0094";
  public static String REBIND_LOCAL_OBJECT_IN_GLOBAL_CONTEXT = "jndi_registry_0095";
  public static String REBIND_GLOBAL_OBJECT_IN_LOCAL_CONTEXT = "jndi_registry_0096";
  public static String CANNOT_UNBIND_CONTEXT = "jndi_registry_0099";
  public static String GLOBAL_UNBIND_OVER_LOCAL_OBJECT = "jndi_registry_0100";
  public static String LOCAL_UNBIND_OVER_GLOBAL_OBJECT = "jndi_registry_0101";
  public static String GLOBAL_RENAME_OVER_LOCAL_OBJECT = "jndi_registry_0106";
  public static String LOCAL_RENAME_OVER_GLOBAL_OBJECT = "jndi_registry_0107";
  public static String CANNOT_DESTROY_CURRENT_CONTEXT = "jndi_registry_0110";
  public static String ATTEMPT_FOR_GLOBAL_DESTROY_OF_LOCAL_CONTEXT = "jndi_registry_0114";
  public static String ATTEMPT_FOR_GLOBAL_MODIFY_ATTRIBUTES_OF_LOCAL_OBJECT = "jndi_registry_0122";
  public static String ATTEMPT_FOR_LOCAL_MODIFY_ATTRIBUTES_OF_GLOBAL_OBJECT = "jndi_registry_0123";
  public static String CANNOT_SEARCH_LIST_OPERATION_IS_NOT_ALLOWED = "jndi_registry_0128";
  public static String CANNOT_GET_ENVIROMENT = "jndi_registry_0130";
  public static String CANNOT_REMOVE_FROM_ENVIROMENT = "jndi_registry_0132";
  public static String CANNOT_GET_OBJECT_NAME = "jndi_registry_0133";
  public static String CAN_NOT_ALLOW_OPERATION = "jndi_registry_0000";
  public static String CAN_NOT_DENY_OPERATION = "jndi_registry_0004";
  public static String VERSION_IS_NOT_A_DIGIT = "jndi_registry_0136";
  public static String BAD_VERSION_NUMBER = "jndi_registry_0137";
  public static String OBJECT_MUST_IMPLEMENT_CORBA_OBJECT = "jndi_registry_0138";
  public static String CANNOT_LIST_OR_LIST_BINDINGS = "jndi_registry_0150";
  public static String INVALID_PATH = "jndi_registry_0155";
  public static String EXCEPTION_IN_GETTING_INITIAL_CONTEXT = "jndi_registry_0157";
  public static String UNABLE_TO_GET_CONNECTION_PARAMETERS = "jndi_registry_0236";
  public static String WRONG_PARAMETERS = "jndi_registry_0237";
  public static String MISSING_CONNECTION_PARAMETERS = "jndi_registry_0238";
  public static String WRONG_MS_URL = "jndi_registry_0239";


  public NamingException(String msg) {
    super();
    LocalizableTextFormatter formater = new LocalizableTextFormatter(JNDIResourceAccessor.getResourceAccessor(), msg);
    exceptionInfo = new BaseExceptionInfo(JNDIResourceAccessor.category, Severity.WARNING, JNDIResourceAccessor.location, formater, this, null);
  }

  public NamingException(String msg, Object [] parameters) {
    super();
    LocalizableTextFormatter formater = new LocalizableTextFormatter(JNDIResourceAccessor.getResourceAccessor(), msg, parameters);
    exceptionInfo = new BaseExceptionInfo(JNDIResourceAccessor.category, Severity.WARNING, JNDIResourceAccessor.location, formater, this, null);
  }

  public NamingException(String msg, Throwable linkedException) {
    super();
    LocalizableTextFormatter formater = new LocalizableTextFormatter(JNDIResourceAccessor.getResourceAccessor(), msg);
    exceptionInfo = new BaseExceptionInfo(JNDIResourceAccessor.category, Severity.WARNING, JNDIResourceAccessor.location, formater, this, linkedException);
  }

  public NamingException(String msg, Object [] parameters, Throwable linkedException) {
    super();
    LocalizableTextFormatter formater = new LocalizableTextFormatter(JNDIResourceAccessor.getResourceAccessor(), msg, parameters);
    exceptionInfo = new BaseExceptionInfo(JNDIResourceAccessor.category, Severity.WARNING, JNDIResourceAccessor.location, formater, this, linkedException);
  }

  public Throwable initCause(Throwable cause) {
    return exceptionInfo.initCause(cause);
  }

  public Throwable getCause() {
    return exceptionInfo.getCause();
  }

  public String getMessage() {
    return exceptionInfo.getLocalizedMessage();
  }

  public String getNestedMessage() {
    return exceptionInfo.getLocalizedMessage();
  }

  public LocalizableText getLocalizableMessage() {
    return exceptionInfo.getLocalizableMessage();
  }

  public String getLocalizedMessage() {
    return exceptionInfo.getLocalizedMessage();
  }

  public String getLocalizedMessage(Locale loc) {
    return exceptionInfo.getLocalizedMessage(loc);
  }

  public String getLocalizedMessage(TimeZone timeZone) {
    return exceptionInfo.getLocalizedMessage(timeZone);
  }

  public String getLocalizedMessage(Locale loc, TimeZone timeZone) {
    return exceptionInfo.getLocalizedMessage(loc, timeZone);
  }

  public String getNestedLocalizedMessage() {
    return exceptionInfo.getNestedLocalizedMessage();
  }

  public String getNestedLocalizedMessage(Locale loc) {
    return exceptionInfo.getNestedLocalizedMessage(loc);
  }

  public String getNestedLocalizedMessage(TimeZone timeZone) {
    return exceptionInfo.getNestedLocalizedMessage(timeZone);
  }

  public String getNestedLocalizedMessage(Locale loc, TimeZone timeZone) {
    return exceptionInfo.getNestedLocalizedMessage(loc, timeZone);
  }

  public void finallyLocalize() {
    exceptionInfo.finallyLocalize();
  }

  public void finallyLocalize(Locale loc) {
    exceptionInfo.finallyLocalize(loc);
  }

  public void finallyLocalize(TimeZone timeZone) {
    exceptionInfo.finallyLocalize(timeZone);
  }

  public void finallyLocalize(Locale loc, TimeZone timeZone) {
    exceptionInfo.finallyLocalize(loc, timeZone);
  }

  public String getSystemStackTraceString() {
    StringWriter s = new StringWriter();
    super.printStackTrace(new PrintWriter(s));
    return s.toString();
  }

  public String getStackTraceString() {
    return exceptionInfo.getStackTraceString();
  }

  public String getNestedStackTraceString() {
    return exceptionInfo.getNestedStackTraceString();
  }

  public void printStackTrace() {
    exceptionInfo.printStackTrace();
  }

  public void printStackTrace(PrintStream s) {
    exceptionInfo.printStackTrace(s);
  }

  public void printStackTrace(PrintWriter s) {
    exceptionInfo.printStackTrace(s);
  }

  /**
	 * Setter method for logging information.
	 *
	 * @param cat logging category
	 * @param severity logging severity
	 * @param loc logging location
	 */
  public void setLogSettings(Category cat, int severity, Location loc) {
    exceptionInfo.setLogSettings(cat, severity, loc);
  }

  /**
	 * Logs the exception message.
	 */
	public void log() {
		exceptionInfo.log();
	}
}

