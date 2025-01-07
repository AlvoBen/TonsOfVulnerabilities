package com.sap.sdm.apiimpl.remote.client.p4;

import com.sap.sdm.api.remote.Client;

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
public final class P4ClientFactory {

	private static final P4ClientFactory INSTANCE = new P4ClientFactory();

	private P4ClientFactory() {
	}

	public static P4ClientFactory getInstance() {
		return INSTANCE;
	}

	public Client createClient(com.sap.engine.services.dc.api.Client dcClient) {
		return new P4ClientImpl(dcClient);
	}

}
