package com.sap.engine.services.rmi_p4.exception;

import com.sap.engine.services.rmi_p4.P4ConnectionException;
import com.sap.engine.services.rmi_p4.P4ObjectBroker;
import com.sap.exception.BaseExceptionInfo;
import com.sap.exception.IBaseException;
import com.sap.localization.LocalizableText;
import com.sap.localization.LocalizableTextFormatter;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import java.io.ObjectStreamException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.TimeZone;

public class P4BaseConnectionException extends P4ConnectionException implements IBaseException{

  static final long serialVersionUID = -4402465765297560716L;

  private Throwable th = null;

  private BaseExceptionInfo info = null;

  public P4BaseConnectionException(String key){
    super();
    LocalizableTextFormatter ltf = new LocalizableTextFormatter(P4ResourceAccessor.getResourceAccessor(), key, (Object[])null);
    info = new BaseExceptionInfo(P4ResourceAccessor.category, Severity.ERROR, P4ResourceAccessor.location, ltf, this ,null);
  }

  public P4BaseConnectionException(String key, Object[] args){
    super();
    LocalizableTextFormatter ltf = new LocalizableTextFormatter(P4ResourceAccessor.getResourceAccessor(), key, args);
    info = new BaseExceptionInfo(P4ResourceAccessor.category, Severity.ERROR, P4ResourceAccessor.location, ltf, this ,null);
  }

  public P4BaseConnectionException(String key, Throwable th){
    super();
    this.th = th;
    LocalizableTextFormatter ltf = new LocalizableTextFormatter(P4ResourceAccessor.getResourceAccessor(), key, (Object[])null);
    info = new BaseExceptionInfo(P4ResourceAccessor.category, Severity.ERROR, P4ResourceAccessor.location, ltf, this ,th);
  }

  public P4BaseConnectionException(String key, Object[] args, Throwable th){
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

//  public String getNestedMessage() {
//    return info.getNestedMessage();
//  }

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
    return new P4ExceptionWrapper(this.getMessage(), P4ObjectBroker.P4_ConnectionException, th);
  }
}
