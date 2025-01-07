package com.sap.engine.services.dc.repo.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.services.dc.repo.Sca;
import com.sap.engine.services.dc.repo.ScaRepoLocation;
import com.sap.engine.services.dc.repo.SdaId;
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
final class ScaRepoLocationImpl extends SduRepoLocationImpl implements
		ScaRepoLocation {

	private final Set sdaRepoLocations = new HashSet();

	ScaRepoLocationImpl(String location) {
		super(location);
	}

	ScaRepoLocationImpl(Configuration cfg) {
		super(cfg);
	}

	ScaRepoLocationImpl(String location, Sca sca) {
		super(location, sca);
		initSduRepoLocations(sca);
	}

	ScaRepoLocationImpl(Configuration cfg, Sca sca) {
		super(cfg, sca);
		initSduRepoLocations(sca);
	}

	private void initSduRepoLocations(Sca sca) {
		for (Iterator iter = sca.getSdaIds().iterator(); iter.hasNext();) {
			this.sdaRepoLocations.add(SduRepoLocationBuilder.getInstance()
					.build((SdaId) iter.next()));
		}
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.ScaRepoLocation#getSdaRepoLocations()
	 */
	public Set getSdaRepoLocations() {
		return this.sdaRepoLocations;
	}

}
