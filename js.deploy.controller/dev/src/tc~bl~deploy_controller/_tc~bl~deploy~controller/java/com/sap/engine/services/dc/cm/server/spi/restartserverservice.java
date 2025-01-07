package com.sap.engine.services.dc.cm.server.spi;

import com.sap.engine.services.dc.cm.lock.LockAction;
import com.sap.engine.services.dc.cm.server.ServerService;
import com.sap.engine.services.dc.util.exception.DCBaseException;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-7
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov( i030791 )
 * @version 1.0
 * @since 7.0
 * 
 */
public interface RestartServerService extends ServerService {

	// public void restart() throws RestartServerServiceException;

	public void restartInSafeMode(LockAction action, boolean keepLastMode, String operationId)
			throws RestartServerServiceException;

	public void restartToPreviousMode() throws RestartServerServiceException;
	
	public void restartToPreviousMode(String operationId) throws RestartServerServiceException;

	public void restartCurrentInstance(LockAction action, boolean keepLastMode)
			throws RestartServerServiceException;

	public void restartCurrentInstInPrevMode()
			throws RestartServerServiceException;

	public void restartCurrentInstance() throws RestartServerServiceException;

	public class RestartServerServiceException extends DCBaseException {

		public RestartServerServiceException(String patternKey) {
			super(patternKey);
		}

		public RestartServerServiceException(String patternKey, Throwable cause) {
			super(patternKey, cause);
		}

		public RestartServerServiceException(String patternKey,
				Object[] parameters, Throwable cause) {
			super(patternKey, parameters, cause);
		}

		public RestartServerServiceException(String patternKey,
				Object[] parameters) {
			super(patternKey, parameters);
		}
	}

}
