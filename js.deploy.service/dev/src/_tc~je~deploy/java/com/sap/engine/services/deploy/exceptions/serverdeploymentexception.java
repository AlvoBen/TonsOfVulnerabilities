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

import com.sap.engine.frame.core.load.ClassInfo;
import com.sap.engine.frame.core.load.ClassWithLoaderInfo;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.DeployResourceAccessor;
import com.sap.localization.LocalizableTextFormatter;
import com.sap.tc.logging.Severity;

/* This class belongs to the public API of the DeployService project. */
/**
 * Class for specifying exceptions of fatal severity when errors in Deploy
 * service occur.
 * 
 * @author Rumiana Angelova
 * @version 6.30
 */
public class ServerDeploymentException extends DeploymentException {
	private static final long serialVersionUID = 1L;
	private static final String defaultMessageID = "ASJ.dpl_ds.008733";
	private static final String defaultDcName = "Deploy Service";

	/**
	 * Constructs exception with the specified message key.
	 * 
	 * @param messageKey the message key for this exception. Message keys 
	 * together with the format of the messages are defined in 
	 * DeployResourceBundle.properties which is part of js.container.interface
	 * project.
	 */
	public ServerDeploymentException(final String messageKey) {
		super(new LocalizableTextFormatter(DeployResourceAccessor
			.getResourceAccessor(),	messageKey, (Object[]) null),
			(Exception)null);
		super.setLogSettings(DeployResourceAccessor.category, 
			Severity.ERROR, DeployResourceAccessor.location);
		setDefaults();
	}

	/**
	 * Constructs exception with the specified message key and cause.
	 * 
	 * @param messageKey the message key for this exception. Message keys 
	 * together with the format of the messages are defined in 
	 * DeployResourceBundle.properties which is part of js.container.interface
	 * project.
	 * 
	 * @param cause the cause of the exception.
	 */
	public ServerDeploymentException(
		final String messageKey, final Throwable cause) {
		super(new LocalizableTextFormatter(DeployResourceAccessor
			.getResourceAccessor(), messageKey, (Object[]) null), cause);
		super.setLogSettings(DeployResourceAccessor.category, 
			Severity.ERROR, DeployResourceAccessor.location);
		setDefaults();
	}

	/**
	 * Constructs exception with the specified message key, parameters and 
	 * cause.
	 * 
	 * @param messageKey the message key for this exception. Message keys 
	 * together with the format of the messages are defined in 
	 * DeployResourceBundle.properties which is part of js.container.interface
	 * project.
	 * 
	 * @param msgParams the parameters for the message.
	 * 
	 * @param cause the cause of the exception.
	 */
	public ServerDeploymentException(final String messageKey, 
		final Object[] msgParams, final Throwable cause) {
		super(new LocalizableTextFormatter(DeployResourceAccessor
			.getResourceAccessor(),	messageKey, msgParams), 
			cause);
		super.setLogSettings(DeployResourceAccessor.category, 
			Severity.ERROR, DeployResourceAccessor.location);
		setDefaults();
	}

	/**
	 * Constructs exception with the specified message key and parameters.
	 *
	 * @param messageKey the message key for this exception. Message keys 
	 * together with the format of the messages are defined in 
	 * DeployResourceBundle.properties which is part of js.container.interface
	 * project.
	 * 
	 * @param msgParams the parameters for the message.
	 */
	public ServerDeploymentException(final String messageKey, 
		final Object... msgParams) {
		super(new LocalizableTextFormatter(
			DeployResourceAccessor.getResourceAccessor(), 
			messageKey, msgParams), null);
		super.setLogSettings(DeployResourceAccessor.category, 
			Severity.ERROR, DeployResourceAccessor.location);
		setDefaults();
	}
	
	public void setDcNameForObjectCaller(Object obj) {
		if (obj != null) {
			final ClassInfo classInfo = PropManager.getInstance().getLoadContext()
				.getLoaderComponentInfo(obj.getClass().getClassLoader());
			if (classInfo != null && classInfo instanceof ClassWithLoaderInfo) {
				setDcName(((ClassWithLoaderInfo) classInfo).getComponentName());
			} //else, the DC name is set to default
		}
	}
	
	private void setDefaults() {
		setMessageID(defaultMessageID);
		setDcName(defaultDcName);
	}
}