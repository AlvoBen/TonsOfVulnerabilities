package com.sap.engine.services.dc.event;

import com.sap.engine.services.rmi_p4.P4ConnectionException;
import com.sap.engine.services.rmi_p4.P4IOException;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-4-27
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public interface DeploymentListener extends DeployControllerListener {

	public static final String NAME = "Deployment Listener";

	public void deploymentPerformed(DeploymentEvent event)
			throws P4IOException, P4ConnectionException;
}
