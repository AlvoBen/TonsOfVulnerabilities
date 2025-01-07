package com.sap.engine.services.dc.cm.undeploy;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-27
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface UndeployFactory extends Remote {

	/**
	 * @return a new <code>Undeployer</code>.
	 * @throws RemoteException
	 */
	public abstract Undeployer createUndeployer() throws RemoteException;

	/**
	 * Creates a <code>UndeployItem</code>. The two arguments
	 * <code>vendor</code> and <code>name</code> are used to identify the
	 * component (development or software component) of the deployment that
	 * should be undeployed.
	 * 
	 * Thus any deployment that belongs to the same component will be undeployed
	 * not matter what the current version (location/counter) of the deployment
	 * is.
	 * 
	 * @param vendor
	 *            the vendor of the component to be undeployed
	 * @param name
	 *            the name of the component to be undeployed
	 * @return a <code>UndeployItem</code>
	 * @throws IllegalArgumentException
	 *             if <code>vendor</code> or <code>name</code> was null
	 *  Note: Returned UndeployItem belongs to SdaUndeployItem class.
	 *        The method returns UndeployItem due to backward compatibility. 
	 */
	public abstract UndeployItem createUndeployItem(String name, String vendor)
			throws UndeploymentException;

	/**
	 * Creates a <code>UndeployItem</code>. The four arguments
	 * <code>vendor</code>, <code>name</code>, <code>location</code> and
	 * <code>version</code> are used to identify the component (development or
	 * software component) of the deployment that should be undeployed.
	 * 
	 * @param vendor
	 *            the vendor of the component to be undeployed
	 * @param name
	 *            the name of the component to be undeployed
	 * @param location
	 *            the location of the component to be undeployed
	 * @param version
	 *            the version of the component to be undeployed
	 * @return a <code>UndeployItem</code>
	 * @throws IllegalArgumentException
	 *             if <code>vendor</code>, <code>name</code>,
	 *             <code>location</code> or <code>version</code> was null or
	 *             <code>version</code> had a wrong format.
	 *  Note: Returned UndeployItem belongs to SdaUndeployItem class.
	 *        The method returns UndeployItem due to backward compatibility. 
	 */
	public abstract UndeployItem createUndeployItem(String name, String vendor,
			String location, String version) throws UndeploymentException;
	
	/**
	 * Creates a <code>UndeployItem</code>. The two arguments
	 * <code>vendor</code> and <code>name</code> are used to identify the
	 * component (development or software component) of the deployment that
	 * should be undeployed.
	 * 
	 * Thus any deployment that belongs to the same component will be undeployed
	 * not matter what the current version (location/counter) of the deployment
	 * is.
	 * 
	 * @param vendor
	 *            the vendor of the component to be undeployed
	 * @param name
	 *            the name of the component to be undeployed
	 * @return a <code>UndeployItem</code>
	 * @throws IllegalArgumentException
	 *             if <code>vendor</code> or <code>name</code> was null
	 */
	public abstract ScaUndeployItem createScaUndeployItem(String name, String vendor)
			throws UndeploymentException;

	/**
	 * Creates a <code>UndeployItem</code>. The four arguments
	 * <code>vendor</code>, <code>name</code>, <code>location</code> and
	 * <code>version</code> are used to identify the component (development or
	 * software component) of the deployment that should be undeployed.
	 * 
	 * @param vendor
	 *            the vendor of the component to be undeployed
	 * @param name
	 *            the name of the component to be undeployed
	 * @param location
	 *            the location of the component to be undeployed
	 * @param version
	 *            the version of the component to be undeployed
	 * @return a <code>UndeployItem</code>
	 * @throws IllegalArgumentException
	 *             if <code>vendor</code>, <code>name</code>,
	 *             <code>location</code> or <code>version</code> was null or
	 *             <code>version</code> had a wrong format.
	 */
	public abstract ScaUndeployItem createScaUndeployItem(String name, String vendor,
			String location, String version) throws UndeploymentException;
	
}
