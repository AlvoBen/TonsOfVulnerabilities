package com.sap.engine.services.dc.cm.server.impl;

import java.io.File;

import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.services.dc.cm.server.spi.ServerModeService;
import com.sap.engine.services.dc.manage.ServiceConfigurer;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-7
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class ServerModeServiceImpl extends AbstractServerModeService implements
		ServerModeService {

	private static final String EXEC_WORKING_DIR = ".." + File.separator;

	ServerModeServiceImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.server.impl.AbstractServerModeService#
	 * getExecWorkDir()
	 */
	protected String getExecWorkDir() {
		return EXEC_WORKING_DIR;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.server.impl.AbstractServerModeService#
	 * getConfigurationHandlerFactory()
	 */
	protected ConfigurationHandlerFactory getConfigurationHandlerFactory() {
		ServiceConfigurer serviceConfigurer = ServiceConfigurer.getInstance();
		ConfigurationHandlerFactory cfgHandlerFactory = serviceConfigurer
				.getConfigurationHandlerFactory();
		return cfgHandlerFactory;
	}

}
