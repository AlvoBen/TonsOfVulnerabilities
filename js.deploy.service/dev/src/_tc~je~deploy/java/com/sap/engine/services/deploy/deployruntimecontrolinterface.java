/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * <<http://www.sap.com>>
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.deploy;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.sap.engine.frame.state.ManagementInterface;

/* This class belongs to the public API of the DeployService project. */
/**
 * Interface for monitoring of deploy service configured using
 * \server\descriptors\monitor-configuration.xml file from service deploy sda
 * file. This information is visualized in NetWeaver Administrator.
 * 
 * @author Maria Jordanova
 * @version
 */
public interface DeployRuntimeControlInterface extends ManagementInterface,
		Remote {

	/**
	 * This method returns the status of the application, which is deployed on
	 * the server with specified serverName. Application status can be:
	 * 
	 * <code>STOPPED, IMPLICIT_STOPPED, STARTED, STARTING, STOPPING, UPGRADING, UNKNOWN.</code>
	 * 
	 * @param applicationName
	 *            the name of the application, which status will be returned.
	 * @param serverName
	 *            the name of the server, on which the application is deployed.
	 * 
	 * @return <code>String</code> representation of application status.
	 * 
	 * @exception <code>RemoteException</code> if a problem during getting
	 *            application status occurs.
	 * 
	 * @deprecated Visual Administrator, which was its only official client, was
	 *             removed in NY.
	 */
	public String getApplicationStatus(String applicationName, String serverName)
			throws RemoteException;

	/**
	 * Checks if the specified application is active. Returns true if its status
	 * is STARTED.
	 * 
	 * @param applicationName
	 *            the name of the application.
	 * 
	 * @return true - if application is STARTED, false - otherwise.
	 * 
	 * @throws RemoteException
	 *             if some abnormal condition occurs.
	 * 
	 * @deprecated Visual Administrator, which was its only official client, was
	 *             removed in NY.
	 */
	public boolean isApplicationStarted(String applicationName)
			throws RemoteException;

	/**
	 * Returns Detailed information for all applications. The result string
	 * array is with dimensions Mx7 where M is the number of deployed
	 * applications and 7 is the number for properties for each of them. The
	 * information is organized as a table with the following format:
	 * <table * border="1">
	 * <tr>
	 * <td ></td>
	 * <td>[m][0]</td>
	 * <td>[m][1]</td>
	 * <td>[m][2]</td>
	 * <td>[m][3]</td>
	 * <tr>
	 * <tr>
	 * <td></td>
	 * <td>Application name</td>
	 * <td>Status</td>
	 * <td>Startup Mode</td>
	 * <td>Start Error</td>
	 * <tr>
	 * <tr>
	 * <td >[0][n]</td>
	 * <td>sap.com/tc~sld~abapapi_ear</td>
	 * <td>STARTED</td>
	 * <td>lazy</td>
	 * <td>.</td>
	 * </tr>
	 * <tr>
	 * <td >[1][n]</td>
	 * <td>sap.com/tc~sec~vsi~app</td>
	 * <td>STOPPED</td>
	 * <td>lazy</td>
	 * <td>Cannot start, because ...</td>
	 * </tr>
	 * </table>
	 * 
	 * @return detailed information for all applications
	 * @throws <code>RemoteException</code> thrown if a remote problem during
	 *         process occurs
	 */
	public String[][] getApplicationsInfo() throws RemoteException;

}
