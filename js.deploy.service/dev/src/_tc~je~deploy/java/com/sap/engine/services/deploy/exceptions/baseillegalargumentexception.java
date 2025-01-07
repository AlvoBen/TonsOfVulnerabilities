/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.deploy.exceptions;

import java.util.Locale;
import java.util.TimeZone;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.PrintStream;

import com.sap.exception.IBaseException;
import com.sap.exception.BaseExceptionInfo;
import com.sap.localization.LocalizableTextFormatter;
import com.sap.localization.LocalizableText;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.engine.services.deploy.DeployResourceAccessor;

/* This class belongs to the public API of the DeployService project. */
/**
 * This class is used to specify exceptions which indicate that a method has
 * been passed an illegal or inappropriate argument.
 * 
 * @author Mariela Todorova
 * @version 6.30
 */

// TODO IBaseException to be removed from the code
public class BaseIllegalArgumentException extends IllegalArgumentException
		implements IBaseException {

	private BaseExceptionInfo exceptionInfo = null;

	/**
	 * Constructs exception with the specified message.
	 * 
	 * @param msg
	 *            message for this exception to be set.
	 */
	public BaseIllegalArgumentException(String msg) {
		super();
		LocalizableTextFormatter formater = new LocalizableTextFormatter(
				DeployResourceAccessor.getResourceAccessor(), msg);
		exceptionInfo = new BaseExceptionInfo(DeployResourceAccessor.category,
				Severity.ERROR, DeployResourceAccessor.location, formater,
				this, null);
	}

	/**
	 * Constructs exception with the specified message and parameter.
	 * 
	 * @param msg
	 *            message for this exception to be set.
	 * @param parameter
	 *            the parameter.
	 */
	public BaseIllegalArgumentException(String msg, Object parameter) {
		this(msg, new Object[] { parameter });
	}

	/**
	 * Constructs exception with the specified message, parameter and nested
	 * <code>Throwable</code>.
	 * 
	 * @param msg
	 *            message for this exception to be set.
	 * @param parameter
	 *            the parameter.
	 * @param e
	 *            the nested <code>Throwable</code>.
	 */
	public BaseIllegalArgumentException(String msg, Object parameter,
			Throwable e) {
		this(msg, new Object[] { parameter }, e);
	}

	/**
	 * Constructs exception with the specified message and parameters.
	 * 
	 * @param msg
	 *            message for this exception to be set.
	 * @param parameters
	 *            the parameters.
	 */
	public BaseIllegalArgumentException(String msg, Object[] parameters) {
		super();
		LocalizableTextFormatter formater = new LocalizableTextFormatter(
				DeployResourceAccessor.getResourceAccessor(), msg, parameters);
		exceptionInfo = new BaseExceptionInfo(DeployResourceAccessor.category,
				Severity.ERROR, DeployResourceAccessor.location, formater,
				this, null);
	}

	/**
	 * Constructs exception with the specified message and nested
	 * <code>Throwable</code>.
	 * 
	 * @param msg
	 *            message for this exception to be set.
	 * @param linkedException
	 *            the nested <code>Throwable</code>.
	 */
	public BaseIllegalArgumentException(String msg, Throwable linkedException) {
		super();
		LocalizableTextFormatter formater = new LocalizableTextFormatter(
				DeployResourceAccessor.getResourceAccessor(), msg);
		exceptionInfo = new BaseExceptionInfo(DeployResourceAccessor.category,
				Severity.ERROR, DeployResourceAccessor.location, formater,
				this, linkedException);
	}

	/**
	 * Constructs exception with the specified message, parameters and nested
	 * <code>Throwable</code>.
	 * 
	 * @param msg
	 *            message for this exception to be set.
	 * @param parameters
	 *            the parameters.
	 * @param linkedException
	 *            the nested <code>Throwable</code>.
	 */
	public BaseIllegalArgumentException(String msg, Object[] parameters,
			Throwable linkedException) {
		super();
		LocalizableTextFormatter formater = new LocalizableTextFormatter(
				DeployResourceAccessor.getResourceAccessor(), msg, parameters);
		exceptionInfo = new BaseExceptionInfo(DeployResourceAccessor.category,
				Severity.ERROR, DeployResourceAccessor.location, formater,
				this, linkedException);
	}

	public Throwable initCause(Throwable cause) {
		return exceptionInfo.initCause(cause);
	}

	public Throwable getCause() {
		return exceptionInfo.getCause();
	}

	public String getMessage() {
		return "";
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
	 * @param cat
	 *            the logging category.
	 * @param severity
	 *            the logging severity.
	 * @param loc
	 *            the logging location.
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