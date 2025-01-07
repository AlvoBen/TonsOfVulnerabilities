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
package com.sap.engine.services.rmi_p4.exception;

import com.sap.exception.IBaseException;
import com.sap.exception.BaseExceptionInfo;
import com.sap.engine.services.rmi_p4.P4ParseRequestException;
import com.sap.engine.services.rmi_p4.P4ObjectBroker;
import com.sap.localization.LocalizableTextFormatter;
import com.sap.localization.LocalizableText;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;

import java.util.Locale;
import java.util.TimeZone;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.PrintStream;
import java.io.ObjectStreamException;

/**
 * Copyright: Copyright (c) 2003
 * Company: SAP AG - Sap Labs Bulgaria
 * 
 * @version 6.30
 */
public class P4BaseParseRequestException extends P4ParseRequestException implements IBaseException{

  static final long serialVersionUID = 805875107173049306L;

  public static final String Invalid_messgae_header_size = "p4_0023";

  private BaseExceptionInfo info = null;
  private Throwable th = null;

  public P4BaseParseRequestException(String key){
    super();
    LocalizableTextFormatter ltf = new LocalizableTextFormatter(P4ResourceAccessor.getResourceAccessor(), key, (Object[])null);
    info = new BaseExceptionInfo(P4ResourceAccessor.category, Severity.ERROR, P4ResourceAccessor.location, ltf, this ,null);
  }

  public P4BaseParseRequestException(String key, Object[] args){
    super();
    LocalizableTextFormatter ltf = new LocalizableTextFormatter(P4ResourceAccessor.getResourceAccessor(), key, args);
    info = new BaseExceptionInfo(P4ResourceAccessor.category, Severity.ERROR, P4ResourceAccessor.location, ltf, this ,null);
  }

  public P4BaseParseRequestException(String key, Throwable th){
    super();
    this.th = th;
    LocalizableTextFormatter ltf = new LocalizableTextFormatter(P4ResourceAccessor.getResourceAccessor(), key, (Object[])null);
    info = new BaseExceptionInfo(P4ResourceAccessor.category, Severity.ERROR, P4ResourceAccessor.location, ltf, this ,th);
  }

  public P4BaseParseRequestException(String key, Object[] args, Throwable th){
    super();
    this.th = th;
    LocalizableTextFormatter ltf = new LocalizableTextFormatter(P4ResourceAccessor.getResourceAccessor(), key, args);
    info = new BaseExceptionInfo(P4ResourceAccessor.category, Severity.ERROR, P4ResourceAccessor.location, ltf, this ,th);
  }

  public Throwable initCause(Throwable throwable) {
    return info.initCause(throwable);
  }

  public Throwable getCause() {
    return info.getCause();
  }

  public String getMessage() {
    return getLocalizedMessage();
  }

  public LocalizableText getLocalizableMessage() {
    return info.getLocalizableMessage();
  }

  public String getLocalizedMessage() {
    return info.getLocalizedMessage();
  }

  public String getLocalizedMessage(Locale locale) {
    return info.getLocalizedMessage(locale);
  }

  public String getLocalizedMessage(TimeZone timeZone) {
    return info.getLocalizedMessage(timeZone);
  }

  public String getLocalizedMessage(Locale locale, TimeZone timeZone) {
    return info.getLocalizedMessage(locale, timeZone);
  }

  public String getNestedLocalizedMessage() {
    return info.getNestedLocalizedMessage();
  }

  public String getNestedLocalizedMessage(Locale locale) {
    return info.getNestedLocalizedMessage(locale);
  }

  public String getNestedLocalizedMessage(TimeZone timeZone) {
    return info.getNestedLocalizedMessage(timeZone);
  }

  public String getNestedLocalizedMessage(Locale locale, TimeZone timeZone) {
    return info.getNestedLocalizedMessage(locale, timeZone);
  }

  public void finallyLocalize() {
    info.finallyLocalize();
  }

  public void finallyLocalize(Locale locale) {
    info.finallyLocalize(locale);
  }

  public void finallyLocalize(TimeZone timeZone) {
    info.finallyLocalize(timeZone);
  }

  public void finallyLocalize(Locale locale, TimeZone timeZone) {
    info.finallyLocalize(locale, timeZone);
  }

  public String getSystemStackTraceString() {
    StringWriter sw = new StringWriter();
    super.printStackTrace(new PrintWriter(sw));
    return sw.toString();
  }

  public String getStackTraceString() {
    return info.getStackTraceString();
  }

  public String getNestedStackTraceString() {
    return info.getNestedStackTraceString();
  }

  public void printStackTrace() {
    info.printStackTrace();
  }

  public void printStackTrace(PrintStream printStream) {
    info.printStackTrace(printStream);
  }

  public void printStackTrace(PrintWriter printWriter) {
    info.printStackTrace(printWriter);
  }

  public void setLogSettings(Category category, int i, Location location) {
    info.setLogSettings(category,i,location);
  }

  public void log() {
    info.log();
  }

  public Object writeReplace() throws ObjectStreamException{
    return new P4ExceptionWrapper(this.getMessage(), P4ObjectBroker.P4_MarshalException, th);
  }
}
