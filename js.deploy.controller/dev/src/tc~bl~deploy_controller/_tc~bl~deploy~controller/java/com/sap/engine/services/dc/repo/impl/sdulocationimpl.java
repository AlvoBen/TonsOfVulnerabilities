package com.sap.engine.services.dc.repo.impl;

import com.sap.engine.services.dc.repo.SduLocation;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-8
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
abstract class SduLocationImpl implements SduLocation {

	private final String location;

	SduLocationImpl(String location) {
		this.location = location;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repo.SduLocation#getLocation()
	 */
	public String getLocation() {
		return this.location;
	}

}
