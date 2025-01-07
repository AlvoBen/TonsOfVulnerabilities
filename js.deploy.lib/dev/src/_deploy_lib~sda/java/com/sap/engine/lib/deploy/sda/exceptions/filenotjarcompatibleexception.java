package com.sap.engine.lib.deploy.sda.exceptions;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.TimeZone;

import com.sap.exception.BaseExceptionInfo;
import com.sap.localization.LocalizableText;
import com.sap.tc.logging.Location;

public class FileNotJarCompatibleException extends IOException {
	
	static final long serialVersionUID = 330967313410588651L;

	private BaseExceptionInfo info = null;

	public FileNotJarCompatibleException(Location loc, Throwable cause) {

		this.initCause(cause);
		info = new BaseExceptionInfo(loc, this, cause);
	}

	public FileNotJarCompatibleException(Location loc, String msg) {

		super(ExceptionUtils.getLocalizableTextFormatter(msg, null).toString());
		info = new BaseExceptionInfo(loc, ExceptionUtils
				.getLocalizableTextFormatter(msg, null), this, null);
	}

	public FileNotJarCompatibleException(Location loc, String msg, Object[] args) {

		super(ExceptionUtils.getLocalizableTextFormatter(msg, args).toString());
		info = new BaseExceptionInfo(loc, ExceptionUtils
				.getLocalizableTextFormatter(msg, args), this, null);
	}

	public FileNotJarCompatibleException(Location loc, String msg, Throwable cause) {

		super(ExceptionUtils.getLocalizableTextFormatter(msg, null).toString(),
				cause);
		info = new BaseExceptionInfo(loc, ExceptionUtils
				.getLocalizableTextFormatter(msg, null), this, cause);
	}

	public FileNotJarCompatibleException(Location loc, String msg, Object[] args,
			Throwable cause) {

		super(ExceptionUtils.getLocalizableTextFormatter(msg, args).toString(),
				cause);
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

}
