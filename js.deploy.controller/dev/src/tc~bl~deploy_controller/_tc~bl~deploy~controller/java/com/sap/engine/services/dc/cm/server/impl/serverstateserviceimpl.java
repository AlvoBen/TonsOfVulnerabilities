package com.sap.engine.services.dc.cm.server.impl;

import com.sap.engine.services.dc.cm.server.spi.ServerState;
import com.sap.engine.services.dc.cm.server.spi.ServerStateService;

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
final class ServerStateServiceImpl implements ServerStateService {

	ServerStateServiceImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.server.spi.ServerStateService#
	 * determineCurrentEngineState()
	 */
	public ServerState determineCurrentEngineState() throws ServiceException {
		return ServerState.ONLINE;
	}

}
