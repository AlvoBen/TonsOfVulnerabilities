package com.sap.sdm.apiimpl.remote.client.p4;

import com.sap.sdm.api.remote.DynSizeParamContainer;
import com.sap.sdm.api.remote.RemoteException;
import com.sap.sdm.api.remote.SDMConfig;
import com.sap.sdm.api.remote.ServerType;
import com.sap.sdm.api.remote.TargetSystemContainer;

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
class P4SDMConfigImpl implements SDMConfig {

	private final com.sap.engine.services.dc.api.Client dcClient;

	private P4SDMConfigImpl(com.sap.engine.services.dc.api.Client dcClient) {
		this.dcClient = dcClient;
	}

	static SDMConfig createInstance(
			com.sap.engine.services.dc.api.Client dcClient) {
		return new P4SDMConfigImpl(dcClient);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.SDMConfig#getSubstVarContainer()
	 */
	public DynSizeParamContainer getSubstVarContainer() throws RemoteException {
		return new P4DynSizeParamContainer(this.dcClient);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.SDMConfig#getTargetSystemContainer()
	 */
	public TargetSystemContainer getTargetSystemContainer()
			throws RemoteException {
		throw new UnsupportedOperationException(
				"The Target Systems are no more supported. "
						+ "All the target systems are moved as Java EE Containers "
						+ "within the SAP Application Server Java!");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.SDMConfig#getServerTypes()
	 */
	public ServerType[] getServerTypes() throws RemoteException {
		throw new UnsupportedOperationException(
				"The Server Types are no more supported!");
	}
}
