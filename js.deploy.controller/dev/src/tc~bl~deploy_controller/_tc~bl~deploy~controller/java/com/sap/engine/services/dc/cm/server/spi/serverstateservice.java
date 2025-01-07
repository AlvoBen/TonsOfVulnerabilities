package com.sap.engine.services.dc.cm.server.spi;

import com.sap.engine.services.dc.cm.server.ServerService;
import com.sap.engine.services.dc.util.exception.DCBaseException;

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
public interface ServerStateService extends ServerService {

	/**
	 * Determines the current running state of the J2EE Engine. It is not
	 * defined how long the returned <code>ServerState</code> corresponds to the
	 * actual running state of the Engine. For example, the running state may
	 * have been changed soon after it has been determined by this method.
	 * 
	 * @throws Exception
	 *             if an error occurs while determining the running state or if
	 *             the Engine is in state, which is not supported by the SDM.
	 */
	public ServerState determineCurrentEngineState() throws ServiceException;

	public class ServiceException extends DCBaseException {

		public ServiceException(String patternKey) {
			super(patternKey);
		}

		public ServiceException(String patternKey, Object[] parameters) {
			super(patternKey, parameters);
		}

		public ServiceException(String patternKey, Throwable cause) {
			super(patternKey, cause);
		}

		public ServiceException(String patternKey, Object[] parameters,
				Throwable cause) {
			super(patternKey, parameters, cause);
		}

	}

}
