package com.sap.engine.services.dc.cm.undeploy;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-23
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface SafeModeUndeployer {

	public void undeployOfflineData() throws UndeploymentException;

	public void undeployOnlineData() throws UndeploymentException;

	public void clearData();

}
