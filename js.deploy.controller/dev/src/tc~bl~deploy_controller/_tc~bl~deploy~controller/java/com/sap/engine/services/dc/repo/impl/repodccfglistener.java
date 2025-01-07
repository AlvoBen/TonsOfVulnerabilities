package com.sap.engine.services.dc.repo.impl;

import com.sap.engine.services.dc.repo.LocationConstants;
import com.sap.engine.services.dc.repo.SduRepoLocation;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-18
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class RepoDCCfgListener extends AbstractRepoCfgListener {

	private static final String DC_REPO_ROOT = LocationConstants.ROOT_REPO_DC;

	RepoDCCfgListener() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.repo.impl.AbstractRepoCfgListener#
	 * getSduRepoLocation(java.lang.String)
	 */
	protected SduRepoLocation getSduRepoLocation(String admittedEventPath) {
		return new SdaRepoLocationImpl(admittedEventPath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.impl.AbstractRepoCfgListener#getRepoSduRoot
	 * ()
	 */
	protected String getRepoSduRoot() {
		return DC_REPO_ROOT;
	}

}
