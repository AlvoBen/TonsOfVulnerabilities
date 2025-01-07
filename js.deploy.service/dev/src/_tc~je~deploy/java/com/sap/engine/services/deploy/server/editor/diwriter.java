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
package com.sap.engine.services.deploy.server.editor;

import java.util.Date;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public interface DIWriter extends VersionInterface {

	/**
	 * 
	 * @param appCfg
	 * @param deployCfg
	 * @param dInfo
	 * @throws DeploymentException
	 */
	public void modifyDeploymentInfo(Configuration appCfg,
			Configuration deployCfg, DeploymentInfo dInfo)
			throws DeploymentException;

	/**
	 * 
	 * @param appCfg
	 * @param dInfo
	 * @throws DeploymentException
	 */
	public void modifySerialized(Configuration appCfg, DeploymentInfo dInfo)
			throws DeploymentException;

	/**
	 * 
	 * @param appOrDeployCfg
	 * @param refObjs
	 * @throws ConfigurationException
	 * @throws ServerDeploymentException
	 */
	public void modifyReferences(Configuration appOrDeployCfg,
			ReferenceObject[] refObjs) throws ConfigurationException,
			ServerDeploymentException;

}
