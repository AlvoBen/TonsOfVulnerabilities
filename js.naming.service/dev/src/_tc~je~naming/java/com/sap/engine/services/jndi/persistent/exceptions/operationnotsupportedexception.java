﻿/*
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
public class OperationNotSupportedException extends javax.naming.OperationNotSupportedException implements IBaseException {
  private BaseExceptionInfo exceptionInfo = null;

  public static String NOT_SUPPORT_SCHEMAS = "jndi_registry_0056";
  public static String EXTENSIBLE_MATCH_NOT_SUPPORTED = "jndi_registry_0077";
  public static String NOT_SUPPORTED_OPERATION_IN_COSNAMING = "jndi_registry_0139";
  static final long serialVersionUID = -8793794095844317579L;

  public OperationNotSupportedException(String msg) {
    super();
    LocalizableTextFormatter formater = new LocalizableTextFormatter(JNDIResourceAccessor.getResourceAccessor(), msg);
    exceptionInfo = new BaseExceptionInfo(JNDIResourceAccessor.category, Severity.WARNING, JNDIResourceAccessor.location, formater, this, null);
  }

  public OperationNotSupportedException(String msg, Object [] parameters) {
    super();
    LocalizableTextFormatter formater = new LocalizableTextFormatter(JNDIResourceAccessor.getResourceAccessor(), msg, parameters);
    exceptionInfo = new BaseExceptionInfo(JNDIResourceAccessor.category, Severity.WARNING, JNDIResourceAccessor.location, formater, this, null);
  }

  public OperationNotSupportedException(String msg, Throwable linkedException) {
    super();
    LocalizableTextFormatter formater = new LocalizableTextFormatter(JNDIResourceAccessor.getResourceAccessor(), msg);
    exceptionInfo = new BaseExceptionInfo(JNDIResourceAccessor.category, Severity.WARNING, JNDIResourceAccessor.location, formater, this, linkedException);
  }

  public OperationNotSupportedException(String msg, Object [] parameters, Throwable linkedException) {
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


