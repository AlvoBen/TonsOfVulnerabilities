﻿/*
 * Copyright (c) 2002 by SAP Labs Bulgaria AG.,
 * url: http://www.saplabs.bg
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Sofia AG.
 */

package com.sap.engine.services.httpserver.exceptions;

import java.util.*;
import java.io.*;
import com.sap.exception.BaseExceptionInfo;
import com.sap.exception.IBaseException;
import com.sap.localization.LocalizableTextFormatter;
import com.sap.localization.LocalizableText;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;

/**
 * Wrapper of IllagalArgumentException if http service
 *
 * @author Violeta Uzunova
 * @version 6.30
 */
public class HttpIllegalArgumentException extends IllegalArgumentException implements IBaseException {
  private BaseExceptionInfo exceptionInfo = null;

  public static final String BUFFER_IS_NULL_OR_OFFSET_AND_LENGTH_ARE_NOT_CORRECT = "http_0000";
  public static final String ILLEGAL_MARK_PARAMETER = "http_0001";

  public HttpIllegalArgumentException(String msg) {
    super();
    LocalizableTextFormatter formater = new LocalizableTextFormatter(HttpResourceAccessor.getResourceAccessor(), msg);
    exceptionInfo = new BaseExceptionInfo(HttpResourceAccessor.location, formater, this, null);
  }

  public HttpIllegalArgumentException(String msg, Object [] parameters) {
    super();
    LocalizableTextFormatter formater = new LocalizableTextFormatter(HttpResourceAccessor.getResourceAccessor(), msg, parameters);
    exceptionInfo = new BaseExceptionInfo(HttpResourceAccessor.location, formater, this, null);
  }

  public HttpIllegalArgumentException(String msg, Throwable linkedException) {
    super();
    LocalizableTextFormatter formater = new LocalizableTextFormatter(HttpResourceAccessor.getResourceAccessor(), msg);
    exceptionInfo = new BaseExceptionInfo(HttpResourceAccessor.location, formater, this, linkedException);
  }

  public HttpIllegalArgumentException(String msg, Object [] parameters, Throwable linkedException) {
    super();
    LocalizableTextFormatter formater = new LocalizableTextFormatter(HttpResourceAccessor.getResourceAccessor(), msg, parameters);
    exceptionInfo = new BaseExceptionInfo(HttpResourceAccessor.location, formater, this, linkedException);
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
    printStackTrace(new PrintWriter(stringWriter,true));
    return new IllegalArgumentException(stringWriter.toString());
  }

}
