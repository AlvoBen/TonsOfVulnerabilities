package com.sap.engine.deployment.proxy;

import javax.naming.NamingException;

import com.sap.engine.deployment.SAPTarget;
import com.sap.engine.deployment.exceptions.SAPDeploymentManagerCreationException;
import com.sap.engine.deployment.exceptions.SAPRemoteException;
import com.sap.engine.services.dc.api.ConnectionException;

/**
 * @author Mariela Todorova
 */
public interface ConnectionManager {

	public void connect() throws SAPDeploymentManagerCreationException;

	public void disconnect() throws NamingException, ConnectionException;

	public SAPTarget[] getTargets() throws SAPRemoteException;

}