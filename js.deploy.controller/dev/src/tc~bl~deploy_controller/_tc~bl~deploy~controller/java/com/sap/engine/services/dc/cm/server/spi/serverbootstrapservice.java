package com.sap.engine.services.dc.cm.server.spi;

import com.sap.engine.services.dc.cm.server.ServerService;
import com.sap.engine.services.dc.util.exception.DCBaseException;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-5
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface ServerBootstrapService extends ServerService {

	public BootstrapMode getBootstrapMode()
			throws ServerBootstrapServiceException;

	public class ServerBootstrapServiceException extends DCBaseException {

		public ServerBootstrapServiceException(String patternKey) {
			super(patternKey);
		}

		public ServerBootstrapServiceException(String patternKey,
				Throwable cause) {
			super(patternKey, cause);
		}

	}

}
