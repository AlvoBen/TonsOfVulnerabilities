package com.sap.sdm.apiimpl.remote.client.p4;

import java.io.File;
import java.io.IOException;

import com.sap.sdm.api.remote.DeployItem;
import com.sap.sdm.api.remote.DeployResult;
import com.sap.sdm.api.remote.RemoteException;

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
class P4DeployItemImpl implements DeployItem {

	private final File archive;
	private DeployResult deplResult = null;
	private String name = null;
	private String vendor = null;

	P4DeployItemImpl() {
		archive = null;
	}

	P4DeployItemImpl(File archive) throws IOException {
		if (archive == null) {
			throw new IOException("Archive specified is null");
		}
		if (archive.exists() == false) {
			throw new IOException("Archive \"" + archive.getAbsolutePath()
					+ "\" does not exist");
		}
		this.archive = archive;
	}

	P4DeployItemImpl(com.sap.engine.services.dc.api.deploy.DeployItem deployItem) {
		this.archive = deployItem.getArchive();
		if (deployItem.getSdu() != null) {
			this.name = deployItem.getSdu().getName();
			this.vendor = deployItem.getSdu().getVendor();
		}
	}

	public File getFile() {
		return archive;
	}

	void setDeployResult(DeployResult result) {
		AssertionCheck.checkForNullArg(getClass(), "setDeployResult", result);

		this.deplResult = result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.DeployItem#getDeployResult()
	 */
	public DeployResult getDeployResult() throws RemoteException {
		return deplResult;
	}

	public String getName() {
		return this.name;
	}

	void setName(String name) {
		AssertionCheck.checkForNullArg(getClass(), "setName", name);
		this.name = name;
	}

	public String getVendor() {
		return this.vendor;
	}

	void setVendor(String vendor) {
		AssertionCheck.checkForNullArg(getClass(), "setVendor", vendor);
		this.vendor = vendor;
	}
}
