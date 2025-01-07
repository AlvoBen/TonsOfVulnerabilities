package com.sap.engine.services.dc.event;

import com.sap.engine.services.rmi_p4.P4ConnectionException;
import com.sap.engine.services.rmi_p4.P4IOException;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-4-28
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public interface UndeploymentListener extends DeployControllerListener {

	public static final String NAME = "Undeployment Listener";

	public void undeploymentPerformed(UndeploymentEvent event)
			throws P4IOException, P4ConnectionException;
}
