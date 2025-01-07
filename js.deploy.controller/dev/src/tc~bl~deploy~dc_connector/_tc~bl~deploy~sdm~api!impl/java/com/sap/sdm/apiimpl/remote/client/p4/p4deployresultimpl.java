package com.sap.sdm.apiimpl.remote.client.p4;

import com.sap.sdm.api.remote.DeployResult;
import com.sap.sdm.api.remote.RemoteException;
import com.sap.sdm.api.remote.deployresults.DeployResultType;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-7-9
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
class P4DeployResultImpl implements DeployResult {

	private final DeployResultType type;
	private final String text;

	P4DeployResultImpl(DeployResultType type, String text) {
		AssertionCheck.checkForNullArg(getClass(), "<init>", type);

		this.type = type;
		this.text = text;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.DeployResult#getType()
	 */
	public DeployResultType getType() throws RemoteException {
		return this.type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.DeployResult#getResultText()
	 */
	public String getResultText() {
		return this.text;
	}
}
