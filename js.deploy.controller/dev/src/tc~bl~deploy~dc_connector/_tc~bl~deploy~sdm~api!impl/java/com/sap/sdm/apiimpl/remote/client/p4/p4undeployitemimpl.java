package com.sap.sdm.apiimpl.remote.client.p4;

import com.sap.sdm.api.remote.RemoteException;
import com.sap.sdm.api.remote.UnDeployItem;
import com.sap.sdm.api.remote.UnDeployResult;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-7-8
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
class P4UnDeployItemImpl implements UnDeployItem {

	private final String componentVendor;
	private final String componentName;
	private final String componentLocation;
	private final String componentVersion;

	private UnDeployResult undeplResult;
	private String deploymentID;

	P4UnDeployItemImpl(String componentVendor, String componentName,
			String componentLocation, String componentCounter) {
		this.componentVendor = componentVendor;
		this.componentName = componentName;

		if (null != componentCounter && null == componentLocation) {
			throw new IllegalArgumentException(
					"A UnDeployItem cannot be created with null!=Counter and null==Location");
		}

		this.componentLocation = componentLocation;
		this.componentVersion = componentCounter;
	}

	public String getDeploymentID() {
		return this.deploymentID;
	}

	public String getComponentVendor() {
		return this.componentVendor;
	}

	public String getComponentName() {
		return this.componentName;
	}

	public String getComponentLocation() {
		return this.componentLocation;
	}

	public String getComponentVersion() {
		return this.componentVersion;
	}

	public void setUnDeployResult(UnDeployResult result) {
		AssertionCheck.checkForNullArg(getClass(), "setUnDeployResult", result);

		this.undeplResult = result;
	}

	public void setDeploymentID(String id) {
		this.deploymentID = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.UnDeployItem#getUnDeployResult()
	 */
	public UnDeployResult getUnDeployResult() throws RemoteException {
		return this.undeplResult;
	}

}
