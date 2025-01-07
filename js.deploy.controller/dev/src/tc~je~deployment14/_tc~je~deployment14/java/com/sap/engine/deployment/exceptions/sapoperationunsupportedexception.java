package com.sap.engine.deployment.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.TimeZone;

import javax.enterprise.deploy.spi.exceptions.OperationUnsupportedException;

import com.sap.exception.BaseExceptionInfo;
import com.sap.exception.IBaseException;
import com.sap.localization.LocalizableText;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;

/**
 * @author Mariela Todorova
 */
public class SAPOperationUnsupportedException extends
		OperationUnsupportedException implements IBaseException {

	static final long serialVersionUID = -3485363503663607675L;

	private BaseExceptionInfo info = null;

	public SAPOperationUnsupportedException(Location loc, Throwable cause) {
		super(null);
		this.initCause(cause);
		info = new BaseExceptionInfo(loc, this, cause);
	}

	public SAPOperationUnsupportedException(Location loc, String msg) {

		super(ExceptionUtils.getLocalizableTextFormatter(msg, null).toString());
		info = new BaseExceptionInfo(loc, ExceptionUtils
				.getLocalizableTextFormatter(msg, null), this, null);
	}

	public SAPOperationUnsupportedException(Location loc, String msg,
			Object[] args) {

		super(ExceptionUtils.getLocalizableTextFormatter(msg, args).toString());
		info = new BaseExceptionInfo(loc, ExceptionUtils
				.getLocalizableTextFormatter(msg, args), this, null);
	}

	public SAPOperationUnsupportedException(Location loc, String msg,
			Throwable cause) {

		super(ExceptionUtils.getLocalizableTextFormatter(msg, null).toString());
		this.initCause(cause);
		info = new BaseExceptionInfo(loc, ExceptionUtils
				.getLocalizableTextFormatter(msg, null), this, cause);
	}

	public SAPOperationUnsupportedException(Location loc, String msg,
			Object[] args, Throwable cause) {

		super(ExceptionUtils.getLocalizableTextFormatter(msg, args).toString());
		this.initCause(cause);
		info = new BaseExceptionInfo(loc, ExceptionUtils
				.getLocalizableTextFormatter(msg, args), this, cause);
	}

	/**
	 * @see com.sap.exception.IBaseException#getLocalizableMessage()
	 */
	public LocalizableText getLocalizableMessage() {
		return info.getLocalizableMessage();
	}

	/**
	 * @see com.sap.exception.IBaseException#getLocalizedMessage(java.util.Locale)
	 */
	public String getLocalizedMessage(Locale locale) {
		return info.getLocalizedMessage(locale);
	}

	/**
	 * @see com.sap.exception.IBaseException#getLocalizedMessage(java.util.TimeZone)
	 */
	public String getLocalizedMessage(TimeZone timeZone) {
		return info.getLocalizedMessage(timeZone);
	}

	/**
	 * @see com.sap.exception.IBaseException#getLocalizedMessage(java.util.Locale,
	 *      java.util.TimeZone)
	 */
	public String getLocalizedMessage(Locale locale, TimeZone timeZone) {
		return info.getLocalizedMessage(locale, timeZone);
	}

	/**
	 * @see com.sap.exception.IBaseException#getNestedLocalizedMessage()
	 */
	public String getNestedLocalizedMessage() {
		return info.getNestedLocalizedMessage();
	}

	/**
	 * @see com.sap.exception.IBaseException#getNestedLocalizedMessage(java.util.Locale)
	 */
	public String getNestedLocalizedMessage(Locale locale) {
		return info.getLocalizedMessage(locale);
	}

	/**
	 * @see com.sap.exception.IBaseException#getNestedLocalizedMessage(java.util.TimeZone)
	 */
	public String getNestedLocalizedMessage(TimeZone timeZone) {
		return info.getLocalizedMessage(timeZone);
	}

	/**
	 * @see com.sap.exception.IBaseException#getNestedLocalizedMessage(java.util.Locale,
	 *      java.util.TimeZone)
	 */
	public String getNestedLocalizedMessage(Locale locale, TimeZone timeZone) {
		return info.getNestedLocalizedMessage(locale, timeZone);
	}

	/**
	 * @see com.sap.exception.IBaseException#finallyLocalize()
	 */
	public void finallyLocalize() {
		info.finallyLocalize();
	}

	/**
	 * @see com.sap.exception.IBaseException#finallyLocalize(java.util.Locale)
	 */
	public void finallyLocalize(Locale locale) {
		info.finallyLocalize(locale);
	}

	/**
	 * @see com.sap.exception.IBaseException#finallyLocalize(java.util.TimeZone)
	 */
	public void finallyLocalize(TimeZone timeZone) {
		info.finallyLocalize(timeZone);
	}

	/**
	 * @see com.sap.exception.IBaseException#finallyLocalize(java.util.Locale,
	 *      java.util.TimeZone)
	 */
	public void finallyLocalize(Locale locale, TimeZone timeZone) {
		info.finallyLocalize(locale, timeZone);
	}

	/**
	 * @see com.sap.exception.IBaseException#getSystemStackTraceString()
	 */
	public String getSystemStackTraceString() {
		StringWriter s = new StringWriter();
		super.printStackTrace(new PrintWriter(s));
		return s.toString();
	}

	/**
	 * @see com.sap.exception.IBaseException#getStackTraceString()
	 */
	public String getStackTraceString() {
		return info.getStackTraceString();
	}

	/**
	 * @see com.sap.exception.IBaseException#getNestedStackTraceString()
	 */
	public String getNestedStackTraceString() {
		return info.getNestedStackTraceString();
	}

	/**
	 * @see com.sap.exception.IBaseException#setLogSettings(com.sap.tc.logging.Category,
	 *      int, com.sap.tc.logging.Location)
	 * 
	 * @deprecated Category and severity must not be used inside of Exception
	 *             API
	 */
	public void setLogSettings(Category cat, int severity, Location loc) {
		info.setLogSettings(cat, severity, loc);
	}

	/**
	 * @see com.sap.exception.IBaseException#log()
	 * 
	 * @deprecated The method is left for backward compatibility. Use some
	 *             suitable method from Logging API instead.
	 */
	public void log() {
		info.log();
	}

}
