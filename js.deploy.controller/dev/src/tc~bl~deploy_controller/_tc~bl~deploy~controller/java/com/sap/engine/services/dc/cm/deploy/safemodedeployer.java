package com.sap.engine.services.dc.cm.deploy;

import java.util.List;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-11
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface SafeModeDeployer {

	public void deployOfflineData() throws DeploymentException;

	public void deployOnlineData() throws DeploymentException;

	public void deployPostOnlineData(List<DeploymentBatchItem> postOnlines)
			throws DeploymentException;

	public void clearData();

	public void finalizeDeployment();

}
