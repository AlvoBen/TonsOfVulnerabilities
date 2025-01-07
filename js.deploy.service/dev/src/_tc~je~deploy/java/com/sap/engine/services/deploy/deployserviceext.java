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

import java.rmi.RemoteException;

import com.sap.engine.services.deploy.zdm.DSRollingPatch;

/**
 * This interface shall be used only by Deploy Controller. It should not be used
 * externally by other components that access DeployService.
 * 
 * @author I046522
 * 
 */
public interface DeployServiceExt extends DeployService, DSRollingPatch {

	/**
	 * Deploy Controller deploys sda file with AS Java service
	 * 
	 * @param sda
	 * @throws RemoteException
	 * @deprecated
	 */
	public void deployService(String sda) throws RemoteException;

	/**
	 * Deploy Controller removes AS Java service
	 * 
	 * @param vendor
	 * @param name
	 * @throws RemoteException
	 * @deprecated
	 */
	public void removeService(String vendor, String name)
			throws RemoteException;

	/**
	 * Deploy Controller deploys sda file with AS Java interface
	 * 
	 * @param sda
	 * @throws RemoteException
	 * @deprecated
	 */
	public void deployInterface(String sda) throws RemoteException;

	/**
	 * Deploy Controller removes AS Java interface
	 * 
	 * @param vendor
	 * @param name
	 * @throws RemoteException
	 * @deprecated
	 */
	public void removeInterface(String vendor, String name)
			throws RemoteException;

}
