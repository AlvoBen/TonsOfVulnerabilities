package com.sap.engine.services.dc.repo.impl;

import java.util.Collection;

import com.sap.engine.services.dc.repo.Sca;
import com.sap.engine.services.dc.repo.ScaLocation;
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
final class ScaLocationImpl extends SduLocationImpl implements ScaLocation {

	private final Sca sca;
	private final Collection groupedLocations;
	private final String toString;

	ScaLocationImpl(Sca sca, String location, Collection groupedLocations) {
		super(location);
		this.sca = sca;
		this.groupedLocations = groupedLocations;

		this.toString = "location: '" + location + "'" + Constants.EOL
				+ "sca: '" + sca.getId() + "'" + "grouped locations: '"
				+ groupedLocations + "'";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repo.ScaLocation#getSca()
	 */
	public Sca getSca() {
		return this.sca;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repo.SduLocation#getSdu()
	 */
	public Sdu getSdu() {
		return this.sca;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repo.ScaLocation#getGroupedLocations()
	 */
	public Collection getGroupedLocations() {
		return this.groupedLocations;
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
