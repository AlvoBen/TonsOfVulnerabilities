package com.sap.sdm.apiimpl.remote.client.p4;

import com.sap.sdm.api.remote.RemoteException;
import com.sap.sdm.api.remote.UnDeployResult;
import com.sap.sdm.api.remote.undeployresults.UnDeployResultType;

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
class P4UnDeployResultImpl implements UnDeployResult {

	private final UnDeployResultType type;
	private final String text;

	P4UnDeployResultImpl(UnDeployResultType type, String text) {
		AssertionCheck.checkForNullArg(getClass(), "<init>", type);

		this.type = type;
		this.text = text;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.UnDeployResult#getType()
	 */
	public UnDeployResultType getType() throws RemoteException {
		return this.type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.UnDeployResult#getResultText()
	 */
	public String getResultText() {
		return this.text;
	}
}
