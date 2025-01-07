package com.sap.jms.interfaces;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.TimeZone;

import com.sap.exception.BaseException;
import com.sap.exception.BaseExceptionInfo;
import com.sap.localization.LocalizableText;
import com.sap.localization.LocalizableTextFormatter;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * @author michael.koegel@sap.com
 * 
 *
 * This exception is thrown in case anything goes wrong during deployment.
 */
public class JMSDeploymentException extends BaseException {
  private BaseExceptionInfo info = null;
  private DeployExceptionEnum errorCode = null;
  
  public JMSDeploymentException(DeployExceptionEnum errorCode) {
  	this(errorCode, null, null);
  	this.errorCode = errorCode;
  	info = new BaseExceptionInfo(this);
  }

  public JMSDeploymentException() {
	    super();
	    info = new BaseExceptionInfo(this);
  }
  
  /**
   * Constructs an exception with the specified root cause.
   *
   * @param t Throwable object which caused this exception
   */
  public JMSDeploymentException(Throwable t) {
  	
  	super(new LocalizableTextFormatter(JMSDeployResourceAccessor.getResourceAccessor(), t.getLocalizedMessage()), t);
 	super.setLogSettings(JMSDeployResourceAccessor.LOGGER, Severity.ERROR, JMSDeployResourceAccessor.TRACER);
    info = new BaseExceptionInfo(this, t);
   
    
  } 
  
  public JMSDeploymentException(DeployExceptionEnum errorCode, Object[] parameters, Throwable linked_exception) {
	info =
		  new BaseExceptionInfo(
			  JMSDeployResourceAccessor.LOGGER,
			  Severity.ERROR,
			  JMSDeployResourceAccessor.TRACER,
			  new LocalizableTextFormatter(JMSDeployResourceAccessor.getResourceAccessor(), errorCode.getName(), parameters),
			  this,
			  linked_exception);
	this.errorCode = errorCode;
  
  }
 
  
  /**
   * Initializes the cause of this exception to the specified value.
   * (The cause is the throwable that caused this exception to get thrown.)
   *
   * <p>This method can be called at most once. That means the cause
   *    can be specified only once.
   *
   * @param  cause the cause (which is saved for later retrieval by the
   *         {@link #getCause()} method).  (A <tt>null</tt> value is
   *         permitted, and indicates that the cause is nonexistent or
   *         unknown.)
   * @return a reference to this exception instance.
   * @throws IllegalArgumentException if <code>cause</code> is this
   *         throwable.  (A throwable cannot be its own cause.)
   *
   */
  public Throwable initCause(Throwable cause) {
      return info.initCause(cause);
  }

  /**
   * Getter method for root cause.
   *
   * @return throwable object which caused this exception or null
   */
  public Throwable getCause() {
      return info.getCause();
  }

  /**
   * Getter method for localizable message.
   *
   * @return localizable message or null
   */
  public LocalizableText getLocalizableMessage() {
        return info.getLocalizableMessage();
  }

  /**
   * Gets localized message.
   * <p>The default locale and default time zone are used for localization.
   * <p>These values have no effect if the <tt>finallyLocalize</tt> method
   * has already been called.
   *
   * @return message string or null
   */
  public String getLocalizedMessage() {
      return info.getLocalizedMessage();
  }

  /**
   * Gets localized message.
   * <p>The specified locale and the default time zone are
   * used for localization. The default locale will be used
   * if the Locale parameter is null.
   * <p>These values have no effect if the <tt>finallyLocalize</tt>
   * method has been already called.
   *
   * @param loc locale
   * @return message string or null
   */
  public String getLocalizedMessage(Locale loc) {
        return info.getLocalizedMessage(loc);
  }

  /**
   * Gets localized message.
   * <p>The specified time zone and the default locale are
   * used for localization. The default time zone will be
   * used, if the time zone parameter is null.
   * <p>These values have no effect if the <tt>finallyLocalize</tt>
   * method has been already called.
   *
   * @param timeZone time zone
   * @return message string or null
   */
  public String getLocalizedMessage(TimeZone timeZone) {
        return info.getLocalizedMessage(timeZone);
  }

  /**
   * Gets localized message.
   * <p>The specified time zone and locale are
   * used for localization. The default time zone and the
   * default locale will be used, if the time zone parameter
   * and the locale are null respectively.
   * <p>These values have no effect if the <tt>finallyLocalize</tt>
   * method has been already called.
   *
   * @param loc locale
   * @param timeZone time zone
   * @return message string or null
   */
  public String getLocalizedMessage(Locale loc, TimeZone timeZone) {
      return info.getLocalizedMessage(loc, timeZone);
  }

  /**
   * Chains localized messages of the nested exceptions.
   * <p>The default locale and the default time zone are
   * used for localization.
   * <p>These values have no effect if the <tt>finallyLocalize</tt> method
   * has already been called.
   *
   * @return message string or null
   */
  public String getNestedLocalizedMessage() {
      return info.getNestedLocalizedMessage();
  }

  /**
   * Chains localized messages of the nested exceptions.
   * <p>The specified locale and the default time zone are
   * used for localization. The default locale will be used
   * if the Locale parameter is null.
   * <p>These values have no effect if the <tt>finallyLocalize</tt>
   * method has been already called.
   * <p> If the localized message is not specified, the
   * non-localizable message is returned (if specified).
   *
   * @param loc locale
   * @return message string or null
   */
  public String getNestedLocalizedMessage(Locale loc) {
      return info.getNestedLocalizedMessage(loc);
  }

  /**
   * Chains localized message of the nested exceptions.
   * <p>The specified time zone and the default locale are
   * used for localization. The default time zone will be
   * used, if the time zone parameter is null.
   * <p>These values have no effect if the <tt>finallyLocalize</tt>
   * method has been already called.
   *
   * @param timeZone time zone
   * @return message string or null
   */
  public String getNestedLocalizedMessage(TimeZone timeZone) {
      return info.getNestedLocalizedMessage(timeZone);
  }

  /**
   * Chains localized message of the nested exceptions.
   * <p>The specified time zone and locale are
   * used for localization. The default time zone and the
   * default locale will be used, if the time zone parameter
   * and the locale parameter are null respectively.
   * <p>These values have no effect if the <tt>finallyLocalize</tt>
   * method has been already called.
   *
   * @param loc locale
   * @param timeZone time zone
   * @return message string
   */
  public String getNestedLocalizedMessage(Locale loc, TimeZone timeZone) {
         return info.getNestedLocalizedMessage(loc, timeZone);
  }

  /**
   * Finally localizes the <code>LocalizableText</code> message
   * (if there is one attached).
   * <p>That means no further localization process can be performed
   * on that object. If there is a nested exception implementing
   * <code>IBaseException</code>, it will be localized recursively.
   * <p> The default locale and the default time zone are
   * used for localization.
   */
  public void finallyLocalize() {
      info.finallyLocalize();
  }

  /**
   * Finally localizes the <code>LocalizableText</code> message
   * (if there is one attached).
   * <p>That means no further localization process can be performed
   * on that object. If there is a nested exception implementing
   * <code>IBaseException</code>, it will be localized recursively.
   * <p> The specified locale and the default time zone
   * are used for localization. If the locale parameter is null,
   * the default locale will be used.
   *
   * @param loc locale
   */
  public void finallyLocalize(Locale loc) {
      info.finallyLocalize(loc);
  }

  /**
   * Finally localizes the <code>LocalizableText</code> message
   * (if there is one attached).
   * <p>That means no further localization process can be performed
   * on that object. If there is a nested exception implementing
   * <code>IBaseException</code>, it will be localized recursively.
   * <p> The specified time zone and the default locale are used for
   * localization. If the time zone parameter is null, the
   * default time zone will be used.
   *
   * @param timeZone time zone
   */
  public void finallyLocalize(TimeZone timeZone) {
      info.finallyLocalize(timeZone);
  }

  /**
   * Finally localizes the <code>LocalizableText</code> message
   * (if there is one attached).
   * <p>That means no further localization process can be performed
   * on that object. If there is a nested exception implementing
   * <code>IBaseException</code>, it will be localized recursively.
   * <p>The specified locale and time zone parameter are used for
   * localization. If the locale parameter or the time zone are
   * null, the default values will be used respectively.
   *
   *
   * @param loc locale
   * @param timeZone time zone
   */
  public void finallyLocalize(Locale loc, TimeZone timeZone) {
        info.finallyLocalize(loc, timeZone);
  }

//  /**
//   * Gets the stack information of this exception
//   * in respect of the current system environment.
//   *
//   * @return the stack trace as a string in respect of the
//   *         current system
//   */
//  public String getSystemStackTraceString() {
//      StringWriter s = new StringWriter();
//      super.printStackTrace(new PrintWriter(s));
//
//      return s.toString();
//  }

  /**
   * Gets stack trace information of this exception only.
   * The stack traces of nested exceptions are not chained.
   *
   * @return the stack trace as a string
   *          without information of chained exceptions.
   */
  public String getStackTraceString() {
      return info.getStackTraceString();
  }

   /**
   * Chains the stack trace information of nested exceptions.
   * The caused stack trace is displayed first.
   *
   * @return the stack trace as a string
   */
  public String getNestedStackTraceString() {
      return info.getNestedStackTraceString();
  }

  /**
   * Prints this exception and its backtrace to the
   * standard error stream. This method prints a stack trace for
   * this exception object on the error output stream that is
   * the value of the field <code>System.err</code>.
   */
  public void printStackTrace() {
      info.printStackTrace();
  }

  /**
   * Prints this exception object and its backtrace to the
   * specified print stream.
   *
   * @param s <code>PrintStream</code> to use for output
   */
  public void printStackTrace(PrintStream s) {
      info.printStackTrace(s);
  }

  /**
   * Prints this exception object and its backtrace
   * to the specified print writer.
   *
   * @param s <code>PrintWriter</code> to use for output
   */
  public void printStackTrace(PrintWriter s) {
      info.printStackTrace(s);
  }

public String getErrorCode() {
	if (errorCode != null) {
	 return errorCode.getName();
	} else {
		return "";
	}
}
public DeployExceptionEnum getErrorCodeEnum() {
	return errorCode;
}

public void setErrorCode(String errorCode) {
	this.errorCode = new DeployExceptionEnum(errorCode);
}


  
}
