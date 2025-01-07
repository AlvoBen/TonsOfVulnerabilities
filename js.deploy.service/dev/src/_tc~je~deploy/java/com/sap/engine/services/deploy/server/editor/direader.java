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

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.engine.services.deploy.container.op.util.FailOver;
import com.sap.engine.services.deploy.container.op.util.ModuleProvider;
import com.sap.engine.services.deploy.container.op.util.StartUp;
import com.sap.engine.services.deploy.container.op.util.Status;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.server.TransactionCommunicator;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.dpl_info.module.InitiallyStarted;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public interface DIReader extends VersionInterface {

	/**
	 * Read the persisted deployment info. The default application status is
	 * STOPPED.
	 * @param appID the application ID (&lt;vendor&gt;/&lt;name&gt;)
	 * @param appsCfg application configuration
	 * @param deployCfg deploy configuration.
	 * @param customAppConfig custom configuration.
	 * @return the deployment info.
	 * @throws ServerDeploymentException
	 */
	public DeploymentInfo readDI(String appID, Configuration appsCfg,
		Configuration deployCfg, Configuration customAppConfig)
		throws ServerDeploymentException;

	public void bootstrapApp(Configuration config,
		TransactionCommunicator communicator, DeploymentInfo dInfo,
		String action) throws ServerDeploymentException;

	public ReferenceObject[] readReferences(Configuration appOrDeployCfg)
		throws ServerDeploymentException;

	public InitiallyStarted readInitiallyStarted(Configuration deployCfg)
		throws ServerDeploymentException;

	public ModuleProvider readModuleProvider(Configuration deployCfg,
		String thisAppWorkDir) throws ServerDeploymentException;

	public Status readStatus(Configuration deployCfg,
		Configuration customAppConfig, boolean isStrict)
		throws ServerDeploymentException;

	public FailOver readFailOver(Configuration appsCfg)
		throws ServerDeploymentException;

	public StartUp readStartUp(Configuration deployCfg)
		throws ServerDeploymentException;
}