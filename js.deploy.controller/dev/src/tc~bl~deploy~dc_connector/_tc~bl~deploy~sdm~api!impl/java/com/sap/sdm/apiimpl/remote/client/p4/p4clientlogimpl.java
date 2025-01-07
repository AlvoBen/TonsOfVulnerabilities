package com.sap.sdm.apiimpl.remote.client.p4;

import java.io.IOException;

import com.sap.sdm.api.remote.ClientLog;
import com.sap.sdm.api.remote.RemoteException;
import com.sap.sdm.api.remote.URLMimic;

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
class P4ClientLogImpl implements ClientLog {

	private final com.sap.engine.services.dc.api.Client dcClient;

	static ClientLog createInstance(
			com.sap.engine.services.dc.api.Client dcClient) {
		return new P4ClientLogImpl(dcClient);
	}

	private P4ClientLogImpl(com.sap.engine.services.dc.api.Client dcClient) {
		this.dcClient = dcClient;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.ClientLog#getAsURL()
	 */
	public URLMimic getAsURL() throws RemoteException {
		throw new UnsupportedOperationException(
				"The operation is not supported!");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.ClientLog#getAsStrings()
	 */
	public String[] getAsStrings() throws RemoteException, IOException {
		return FileUtils.getFileLines(this.dcClient.getLog().getCatPath());
	}
}
