package com.sap.engine.services.dc.repo.impl;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.services.dc.repo.Sda;
import com.sap.engine.services.dc.repo.SdaRepoLocation;
import com.sap.engine.services.dc.repo.SduRepoLocationVisitor;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-2
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class SdaRepoLocationImpl extends SduRepoLocationImpl implements
		SdaRepoLocation {

	SdaRepoLocationImpl(String location) {
		super(location);
	}

	SdaRepoLocationImpl(Configuration cfg) {
		super(cfg);
	}

	SdaRepoLocationImpl(String location, Sda sda) {
		super(location, sda);
	}

	SdaRepoLocationImpl(Configuration cfg, Sda sda) {
		super(cfg, sda);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.SduRepoLocation#accept(com.sap.engine
	 * .services.dc.repo.SduRepoLocationVisitor)
	 */
	public void accept(SduRepoLocationVisitor visitor) {
		visitor.visit(this);
	}

}
