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

import com.sap.exception.IBaseException;
import com.sap.exception.BaseExceptionInfo;
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
public class NoPermissionException extends javax.naming.NoPermissionException implements IBaseException {
  private BaseExceptionInfo exceptionInfo = null;
  static final long serialVersionUID = 3662160812767466243L;
  public static String NO_PERMISSION_TO_LOOKUP                = "jndi_registry_0026";
  public static String NO_PERMISSION_TO_BIND                  = "jndi_registry_0031";
  public static String NO_PERMISSION_TO_REBIND                = "jndi_registry_0034";
  public static String NO_PERMISSION_TO_UNBIND                = "jndi_registry_0037";
  public static String NO_PERMISSION_TO_RENAME                = "jndi_registry_0039";
  public static String NO_PERMISSION_TO_LIST                  = "jndi_registry_0041";
  public static String NO_PERMISSION_TO_SEARCH                = "jndi_registry_0043";
  public static String NO_PERMISSION_TO_LIST_BINDINGS         = "jndi_registry_0045";
  public static String NO_PERMISSION_TO_DESTROY_SUBCONTEXT    = "jndi_registry_0047";
  public static String NO_PERMISSION_TO_CREATE_SUBCONTEXT     = "jndi_registry_0050";
  public static String NO_PERMISSION_TO_GET_ATTRIBUTES        = "jndi_registry_0052";
  public static String NO_PERMISSION_TO_MODIFY_ATTRIBUTES     = "jndi_registry_0054";
  public static String NO_PERMISSION_TO_ADD_TO_ENVIROMENT     = "jndi_registry_0057";
  public static String NO_PERMISSION_TO_CLOSE                 = "jndi_registry_0059";
  public static String NO_PERMISSION_TO_PRINT                 = "jndi_registry_0061";
  public static String NO_PERMISSION_TO_ALLOW_OPERATION       = "jndi_registry_0063";
  public static String NO_PERMISSION_TO_DENY_OPERATION        = "jndi_registry_0064";
  public static String CANNOT_GET_INITIAL_CONTEXT             = "jndi_registry_0068";
  public static String NO_PERMISSION_TO_GET_ENVIROMENT        = "jndi_registry_0129";
  public static String NO_PERMISSION_TO_REMOVE_FROM_ENVIROMENT        = "jndi_registry_0131";
  public static String LOGIN_EXCEPTION_IN_GETTING_INITIAL_CONTEXT = "jndi_registry_0170";


  public NoPermissionException(String msg) {
    super();
    LocalizableTextFormatter formater = new LocalizableTextFormatter(JNDIResourceAccessor.getResourceAccessor(), msg);
    exceptionInfo = new BaseExceptionInfo(JNDIResourceAccessor.category, Severity.WARNING, JNDIResourceAccessor.location, formater, this, null);
  }

  public NoPermissionException(String msg, Object [] parameters) {
    super();
    LocalizableTextFormatter formater = new LocalizableTextFormatter(JNDIResourceAccessor.getResourceAccessor(), msg, parameters);
    exceptionInfo = new BaseExceptionInfo(JNDIResourceAccessor.category, Severity.WARNING, JNDIResourceAccessor.location, formater, this, null);
  }

  public NoPermissionException(String msg, Throwable linkedException) {
    super();
    LocalizableTextFormatter formater = new LocalizableTextFormatter(JNDIResourceAccessor.getResourceAccessor(), msg);
    exceptionInfo = new BaseExceptionInfo(JNDIResourceAccessor.category, Severity.WARNING, JNDIResourceAccessor.location, formater, this, linkedException);
  }

  public NoPermissionException(String msg, Object [] parameters, Throwable linkedException) {
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


