package com.sap.engine.services.dc.repo.impl;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.services.dc.repo.Sdu;
import com.sap.engine.services.dc.repo.SduRepoLocation;

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
abstract class SduRepoLocationImpl implements SduRepoLocation {

	private final String location;
	private final Configuration cfg;
	private final int hashCode;
	private final String toString;
	private Sdu sdu;

	SduRepoLocationImpl(String location) {
		this(location, null);
	}

	SduRepoLocationImpl(Configuration cfg) {
		this(cfg, null);
	}

	SduRepoLocationImpl(String location, Sdu sdu) {
		this.location = location;

		this.hashCode = this.getLocation().hashCode();
		this.toString = this.getLocation();

		this.cfg = null;
		this.sdu = sdu;
	}

	SduRepoLocationImpl(Configuration cfg, Sdu sdu) {
		// TODO: check this logic!!! what about the path separators?
		this.location = cfg.getPath();

		this.hashCode = this.getLocation().hashCode();
		this.toString = this.getLocation();

		this.cfg = cfg;
		this.sdu = sdu;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repo.SduRepoLocation#getLocation()
	 */
	public String getLocation() {
		return this.location;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repo.SduRepoLocation#getSdu()
	 */
	public Sdu getSdu() {
		return this.sdu;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.SduRepoLocation#setSdu(com.sap.engine
	 * .services.dc.repo.Sdu)
	 */
	public void setSdu(Sdu sdu) {
		this.sdu = sdu;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repo.SduRepoLocation#getConfiguration()
	 */
	public Configuration getConfiguration() {
		return cfg;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (this.getClass() != obj.getClass()) {
			return false;
		}

		final SduRepoLocation other = (SduRepoLocation) obj;

		if (!this.getLocation().equals(other.getLocation())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.hashCode;
	}

	public String toString() {
		return this.toString;
	}

}
