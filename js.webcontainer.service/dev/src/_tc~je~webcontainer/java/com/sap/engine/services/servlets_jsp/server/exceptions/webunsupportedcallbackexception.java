﻿/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.servlets_jsp.server.exceptions;

import com.sap.exception.BaseExceptionInfo;
import com.sap.exception.IBaseException;
import com.sap.localization.LocalizableTextFormatter;
import com.sap.localization.LocalizableText;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;

import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.callback.Callback;
import java.util.Locale;
import java.util.TimeZone;
import java.io.StringWriter;
import java.io.PrintWriter;

public class WebUnsupportedCallbackException extends UnsupportedCallbackException implements IBaseException {
  private BaseExceptionInfo exceptionInfo = null;

  public static String UNSUPPORTED_CALLBACK_CONSTANT = "servlet_jsp_0360";

  public WebUnsupportedCallbackException(String msg, Object [] parameters, Callback linkedCallback) {
    super(linkedCallback);
    LocalizableTextFormatter formater = new LocalizableTextFormatter(WebResourceAccessor.getResourceAccessor(), msg, parameters);
    exceptionInfo = new BaseExceptionInfo(WebResourceAccessor.location, formater, this, null);
  }

  public WebUnsupportedCallbackException(String msg, Callback linkedCallback) {
    super(linkedCallback);
    LocalizableTextFormatter formater = new LocalizableTextFormatter(WebResourceAccessor.getResourceAccessor(), msg);
    exceptionInfo = new BaseExceptionInfo(WebResourceAccessor.location, formater, this, null);
  }

  public Throwable initCause(Throwable cause) {
    return exceptionInfo.initCause(cause);
  }

  public Throwable getCause() {
    return exceptionInfo.getCause();
  }

  public String getMessage() {
    return getLocalizedMessage();
  }

  public LocalizableText getLocalizableMessage() {
    return exceptionInfo.getLocalizableMessage();
  }

  public String getLocalizedMessage(Locale locale) {
    return exceptionInfo.getLocalizedMessage(locale);
  }

  public String getLocalizedMessage() {
    return exceptionInfo.getLocalizedMessage();
  }

  public String getLocalizedMessage(TimeZone timezone) {
    return exceptionInfo.getLocalizedMessage(timezone);
  }

  public String getLocalizedMessage(Locale locale, TimeZone timezone) {
    return exceptionInfo.getLocalizedMessage(locale, timezone);
  }

  public String getNestedLocalizedMessage() {
    return exceptionInfo.getNestedLocalizedMessage();
  }

  public String getNestedLocalizedMessage(Locale locale) {
    return exceptionInfo.getNestedLocalizedMessage(locale);
  }

  public String getNestedLocalizedMessage(TimeZone timezone) {
    return exceptionInfo.getNestedLocalizedMessage(timezone);
  }

  public String getNestedLocalizedMessage(Locale locale, TimeZone timezone) {
    return exceptionInfo.getNestedLocalizedMessage(locale, timezone);
  }

  public void finallyLocalize() {
    exceptionInfo.finallyLocalize();
  }

  public void finallyLocalize(Locale locale) {
    exceptionInfo.finallyLocalize(locale);
  }

  public void finallyLocalize(TimeZone timezone) {
    exceptionInfo.finallyLocalize(timezone);
  }

  public void finallyLocalize(Locale locale, TimeZone timezone) {
    exceptionInfo.finallyLocalize(locale, timezone);
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

  /**
	 * Setter method for logging information.
	 *
	 * @param cat logging category
	 * @param severity logging severity
	 * @param loc logging location
	 * @deprecated
	 */
  public void setLogSettings(Category cat, int severity, Location loc) {
    //exceptionInfo.setLogSettings(cat, severity, loc);
  }

  /**
	 * Logs the exception message.
	 * @deprecated
	 */
	public void log() {
		//exceptionInfo.log();
	}

  private Object writeReplace() {
    StringWriter stringWriter = new StringWriter();
    printStackTrace(new PrintWriter(stringWriter, true));
    return new UnsupportedCallbackException(getCallback(), stringWriter.toString());
  }

}
