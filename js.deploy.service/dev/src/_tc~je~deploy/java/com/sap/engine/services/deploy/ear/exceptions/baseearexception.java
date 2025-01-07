package com.sap.engine.services.deploy.ear.exceptions;

import com.sap.localization.LocalizableTextFormatter;
import com.sap.tc.logging.Severity;
import com.sap.engine.services.deploy.container.ContainerResourceAccessor;
import com.sap.engine.services.deploy.container.DeploymentException;

public class BaseEarException extends DeploymentException {

	/**
	 * Constructs exception with the specified message.
	 * 
	 * @param s
	 *            message for this exception to be set.
	 */
	public BaseEarException(String s) {
		super(new LocalizableTextFormatter(ContainerResourceAccessor
				.getResourceAccessor(), s, (Object[]) null), (Exception) null);
		super.setLogSettings(ContainerResourceAccessor.category,
				Severity.ERROR, ContainerResourceAccessor.location);
	}

	/**
	 * Constructs exception with the specified message.
	 * 
	 * @param s
	 *            message for this exception to be set.
	 */
	public BaseEarException(String s, Throwable t) {
		super(new LocalizableTextFormatter(ContainerResourceAccessor
				.getResourceAccessor(), s, (Object[]) null), t);
		super.setLogSettings(ContainerResourceAccessor.category,
				Severity.ERROR, ContainerResourceAccessor.location);
	}

	/**
	 * Constructs exception with the specified message.
	 * 
	 * @param s
	 *            message for this exception to be set.
	 */
	public BaseEarException(String s, Object[] args, Throwable t) {
		super(new LocalizableTextFormatter(ContainerResourceAccessor
				.getResourceAccessor(), s, args), t);
		super.setLogSettings(ContainerResourceAccessor.category,
				Severity.ERROR, ContainerResourceAccessor.location);
	}

	public BaseEarException(String s, Object[] args) {
		super(new LocalizableTextFormatter(ContainerResourceAccessor
				.getResourceAccessor(), s, args), null);
		super.setLogSettings(ContainerResourceAccessor.category,
				Severity.ERROR, ContainerResourceAccessor.location);
	}

}
