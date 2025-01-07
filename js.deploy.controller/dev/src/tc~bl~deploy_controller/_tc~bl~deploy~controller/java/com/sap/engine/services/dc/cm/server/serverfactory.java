package com.sap.engine.services.dc.cm.server;

import org.w3c.dom.Document;

import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.services.dc.util.Constants;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-28
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public abstract class ServerFactory {

	private static ServerFactory INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.cm.server.impl.ServerFactoryImpl";

	protected ServerFactory() {
	}

	public static synchronized ServerFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static ServerFactory createFactory() {

		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (ServerFactory) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "ASJ.dpl_dc.003166 An error occurred while creating an instance of "
					+ "class ServerFactory! " + Constants.EOL + e.getMessage();

			throw new RuntimeException(errMsg);
		}
	}

	public abstract Server createServer();

	public abstract SoftwareTypeRequest createSoftwareTypeRequest();

	public abstract SoftwareTypeRequest createSoftwareTypeRequest(
			Document cfgDocument);

	public abstract ServerStateRequest createServerStateRequest();

	public abstract ServerModeRequest createServerModeRequest();

	public abstract OfflineServerModeRequest createOfflineServerModeRequest(
			ConfigurationHandlerFactory configurationHandlerFactory);

	public abstract RestartServerRequest createRestartServerRequest();

	public abstract OfflineRestartServerRequest createOfflineRestartServerRequest(
			ConfigurationHandlerFactory configurationHandlerFactory,
			String osUserName, String osUserPass);

	public abstract UnsupportedUndeployComponentsRequest createUnsupportedUndeployComponentsRequest();

	public abstract ServerBootstrapRequest createServerBootstrapRequest();

}
