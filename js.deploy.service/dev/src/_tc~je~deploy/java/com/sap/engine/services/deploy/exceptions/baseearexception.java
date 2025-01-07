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

import com.sap.exception.BaseException;
import com.sap.localization.LocalizableTextFormatter;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.engine.services.deploy.DeployResourceAccessor;

/* This class belongs to the public API of the DeployService project. */
/**
 * This class is used to specify exceptions which indicate that an EAR file is
 * incorrect.
 * 
 * @author Mariela Todorova
 * @version 6.30
 */
// TODO BaseException to be replaced by Exception
public class BaseEarException extends BaseException {

	/**
	 * Constructs exception with the specified message.
	 * 
	 * @param s
	 *            message for this exception to be set.
	 */
	public BaseEarException(String s) {
		super(new LocalizableTextFormatter(DeployResourceAccessor
				.getResourceAccessor(), s, (Object[]) null), (Exception) null);
		super.setLogSettings(DeployResourceAccessor.category, Severity.ERROR,
				DeployResourceAccessor.location);
	}

	/**
	 * Constructs exception with the specified message and nested
	 * <code>Throwable.</code>
	 * 
	 * @param s
	 *            message for this exception to be set.
	 * @param t
	 *            the nested <code>Throwable.</code>
	 */
	public BaseEarException(String s, Throwable t) {
		super(new LocalizableTextFormatter(DeployResourceAccessor
				.getResourceAccessor(), s, (Object[]) null), t);
		super.setLogSettings(DeployResourceAccessor.category, Severity.ERROR,
				DeployResourceAccessor.location);
	}

	/**
	 * Constructs exception with the specified message, parameters and nested
	 * <code>Throwable.</code>
	 * 
	 * @param s
	 *            message for this exception to be set.
	 * @param args
	 *            the parameters.
	 * @param t
	 *            the nested <code>Throwable.</code>
	 */
	public BaseEarException(String s, Object[] args, Throwable t) {
		super(new LocalizableTextFormatter(DeployResourceAccessor
				.getResourceAccessor(), s, args), t);
		super.setLogSettings(DeployResourceAccessor.category, Severity.ERROR,
				DeployResourceAccessor.location);
	}

	/**
	 * Constructs exception with the specified message and parameters.
	 * 
	 * @param s
	 *            message for this exception to be set.
	 * @param args
	 *            the parameters.
	 */
	public BaseEarException(String s, Object[] args) {
		super(new LocalizableTextFormatter(DeployResourceAccessor
				.getResourceAccessor(), s, args), null);
		super.setLogSettings(DeployResourceAccessor.category, Severity.ERROR,
				DeployResourceAccessor.location);
	}

}
