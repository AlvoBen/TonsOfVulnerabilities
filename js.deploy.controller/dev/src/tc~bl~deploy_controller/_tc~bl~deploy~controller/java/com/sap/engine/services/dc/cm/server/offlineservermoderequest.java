package com.sap.engine.services.dc.cm.server;

import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-3-30
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public interface OfflineServerModeRequest extends ServerServiceRequest {
	public ConfigurationHandlerFactory getConfigurationHandlerFactory();
}
