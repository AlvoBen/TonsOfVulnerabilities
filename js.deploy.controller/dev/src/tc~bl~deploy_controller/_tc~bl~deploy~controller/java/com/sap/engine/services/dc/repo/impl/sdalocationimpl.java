package com.sap.engine.services.dc.repo.impl;

import com.sap.engine.services.dc.repo.Sda;
import com.sap.engine.services.dc.repo.SdaLocation;
import com.sap.engine.services.dc.repo.Sdu;
import com.sap.engine.services.dc.repo.SduLocationVisitor;
import com.sap.engine.services.dc.util.Constants;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-20
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class SdaLocationImpl extends SduLocationImpl implements SdaLocation {

	private final Sda sda;
	private final String toString;

	SdaLocationImpl(Sda sda, String location) {
		super(location);
		this.sda = sda;

		this.toString = "location: '" + location + "'" + Constants.EOL
				+ "sda: '" + sda.getId() + "'";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repo.SdaLocation#getSda()
	 */
	public Sda getSda() {
		return this.sda;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repo.SduLocation#getSdu()
	 */
	public Sdu getSdu() {
		return this.sda;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.SduLocation#accept(com.sap.engine.services
	 * .dc.repo.SduLocationVisitor)
	 */
	public void accept(SduLocationVisitor visitor) {
		visitor.visit(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repo.SduLocation#toString()
	 */
	public String toString() {
		return this.toString;
	}

}
