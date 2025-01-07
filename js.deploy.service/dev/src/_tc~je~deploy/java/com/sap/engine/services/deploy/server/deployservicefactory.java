/* 
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.deploy.server;

import com.sap.engine.frame.cluster.message.ListenerAlreadyRegisteredException;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.services.deploy.server.properties.PropManager;

import java.rmi.RemoteException;

/**
 * Factory for DeployServiceImpl. This class is intended only for internal use
 * by deploy service.
 */
public final class DeployServiceFactory {
	private static DeployServiceImpl instance;

	/**
	 * This method is called by DeployServiceFrame, during the start of the
	 * deploy service. It has default access level, and can be called only from
	 * the same package.
	 * 
	 * @return deploy service implementation.
	 * @throws RemoteException
	 * @throws ListenerAlreadyRegisteredException
	 * @throws ConfigurationException
	 */
	static DeployServiceImpl createDeployService() throws RemoteException {
		instance = PropManager.getInstance().isAdditionalDebugInfo() ? 
			new DeployServiceImplTimeStatWrapper() : new DeployServiceImpl();
		return instance;
	}

	/**
	 * This method is used to obtain the deploy service implementation, needed
	 * for some ATS tests.
	 * 
	 * @return the currently used {@link DeployServiceImpl}.
	 */
	public static DeployServiceImpl getDeployService() {
		return instance;
	}
}