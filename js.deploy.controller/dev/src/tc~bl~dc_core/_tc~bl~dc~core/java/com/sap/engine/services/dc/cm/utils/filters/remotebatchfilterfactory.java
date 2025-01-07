package com.sap.engine.services.dc.cm.utils.filters;

import java.rmi.Remote;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-29
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface RemoteBatchFilterFactory extends Remote {

	public abstract BatchFilter createSoftwareTypeBatchFilter(
			String softwareType) throws RemoteBatchFilterFactoryException;

	public abstract BatchFilter createSoftwareTypeBatchFilter(
			String softwareType, String softwareSubType)
			throws RemoteBatchFilterFactoryException;

}
