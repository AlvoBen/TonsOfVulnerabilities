package com.sap.engine.services.dc.cm.deploy.storage;

import com.sap.engine.services.dc.cm.deploy.DeploymentData;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-9
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface DeploymentDataLocation {

	public String getLocation();

	public DeploymentData getDeploymentData();

	public void setDeploymentData(DeploymentData deploymentData);

}
