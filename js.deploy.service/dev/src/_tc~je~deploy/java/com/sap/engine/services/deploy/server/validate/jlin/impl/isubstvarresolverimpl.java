/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * <<http://www.sap.com>>
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.server.validate.jlin.impl;

import java.io.InputStream;

import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.CustomParameterMappings;
import com.sap.engine.lib.converter.DescriptorParseException;
import com.sap.engine.lib.converter.ISubstVarResolver;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.utils.ValidateUtils;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public class ISubstVarResolverImpl implements ISubstVarResolver {

	private static ISubstVarResolverImpl INSTANCE;
	private CustomParameterMappings customParameterMappings;

	private ISubstVarResolverImpl(
			CustomParameterMappings aCustomParameterMappings) {
		this.customParameterMappings = aCustomParameterMappings;
	}

	public synchronized static ISubstVarResolverImpl getInstance()
			throws ConfigurationException {
		if (INSTANCE == null) {
			INSTANCE = new ISubstVarResolverImpl(PropManager.getInstance()
					.getAppServiceCtx().getCoreContext()
					.getConfigurationHandlerFactory().getConfigurationHandler()
					.getCustomParameterMappings());
		}
		return INSTANCE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.lib.converter.ISubstVarResolver#substituteParamString(
	 * java.lang.String)
	 */
	public String substituteParamString(String arg0)
			throws DescriptorParseException {
		ValidateUtils.nullValidator(customParameterMappings,
				"CustomParameterMappings");
		try {
			return customParameterMappings.substituteParamString(arg0);
		} catch (ConfigurationException ce) {
			throw new DescriptorParseException(ce);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.lib.converter.ISubstVarResolver#substituteParamStream(
	 * java.io.InputStream)
	 */
	public InputStream substituteParamStream(InputStream arg0)
			throws DescriptorParseException {
		ValidateUtils.nullValidator(customParameterMappings,
				"CustomParameterMappings");
		try {
			return customParameterMappings.substituteParamStream(arg0);
		} catch (ConfigurationException ce) {
			throw new DescriptorParseException(ce);
		}
	}
}
