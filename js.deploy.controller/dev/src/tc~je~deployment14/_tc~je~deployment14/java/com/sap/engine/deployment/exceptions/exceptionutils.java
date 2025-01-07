package com.sap.engine.deployment.exceptions;

import com.sap.localization.LocalizableTextFormatter;

/**
 * It is used to create a LocalizableTextFormatter instance in order to
 * initialize the BaseExceptionInfo object of each exception
 * 
 * @author Radoslav Popov
 */
public class ExceptionUtils {

	public static LocalizableTextFormatter getLocalizableTextFormatter(
			String msg, Object[] args) {
		return new LocalizableTextFormatter(DeploymentResourceAccessor
				.getInstance(), msg, args);
	}

}