package com.sap.sdm.apiimpl.remote.client.p4;

import com.sap.sdm.api.remote.Client;
import com.sap.sdm.api.remote.ClientLog;
import com.sap.sdm.api.remote.ClientSessionFactory;
import com.sap.sdm.api.remote.DeployProcessor;
import com.sap.sdm.api.remote.HelperFactory;
import com.sap.sdm.api.remote.RemoteException;
import com.sap.sdm.api.remote.RepositoryExplorer;
import com.sap.sdm.api.remote.SDMConfig;
import com.sap.sdm.api.remote.URLMimic;
import com.sap.sdm.api.remote.UnDeployProcessor;
import com.sap.sdm.api.remote.cvers.CVersManager;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-7-7
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
class P4ClientImpl implements Client {

	private static final int API_SERVER_VERSION = ClientSessionFactory
			.getAPIClientVersion();

	private final com.sap.engine.services.dc.api.Client dcClient;

	P4ClientImpl(com.sap.engine.services.dc.api.Client dcClient) {
		this.dcClient = dcClient;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.Client#getAPIServerVersion()
	 */
	public int getAPIServerVersion() throws RemoteException {
		return API_SERVER_VERSION;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.Client#getSDMConfiguration()
	 */
	public SDMConfig getSDMConfiguration() throws RemoteException {
		return P4SDMConfigImpl.createInstance(this.dcClient);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.Client#getDeployProcessor()
	 */
	public DeployProcessor getDeployProcessor() throws RemoteException {
		return P4DeployProcessorImpl.createInstance(this.dcClient);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.Client#getUnDeployProcessor()
	 */
	public UnDeployProcessor getUnDeployProcessor() throws RemoteException {
		return P4UnDeployProcessorImpl.createInstance(this.dcClient);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.Client#getHelperFactory()
	 */
	public HelperFactory getHelperFactory() throws RemoteException {
		return P4HelperFactoryImpl.getInstance();
	}

	/**
	 * @see com.sap.sdm.api.remote.Client#getClientLog()
	 * @deprecated
	 */
	public URLMimic getClientLog() throws RemoteException {
		throw new UnsupportedOperationException(
				"The operation is not supported!");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.Client#getLog()
	 */
	public ClientLog getLog() throws RemoteException {
		return P4ClientLogImpl.createInstance(this.dcClient);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.Client#getRepositoryExplorer()
	 */
	public RepositoryExplorer getRepositoryExplorer() throws RemoteException {
		return P4RepositoryExplorerImpl.createInstance(this.dcClient);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.Client#getCVersManager()
	 */
	public CVersManager getCVersManager() throws RemoteException {
		throw new UnsupportedOperationException(
				"The operation is not supported!");
	}

}
