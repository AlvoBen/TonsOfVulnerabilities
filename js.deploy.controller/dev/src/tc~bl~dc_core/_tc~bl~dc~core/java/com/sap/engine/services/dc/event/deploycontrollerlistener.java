package com.sap.engine.services.dc.event;

import java.rmi.Remote;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-5-4
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public interface DeployControllerListener extends Remote {

	public int getId();
}
