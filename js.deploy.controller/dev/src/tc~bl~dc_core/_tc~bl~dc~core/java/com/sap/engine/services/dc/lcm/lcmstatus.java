package com.sap.engine.services.dc.lcm;

import java.io.Serializable;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-3-27
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public final class LCMStatus implements Serializable {

	private static final long serialVersionUID = -7872291559864250686L;

	public transient static final LCMStatus UNKNOWN = new LCMStatus(
			new Integer(-1), "unknown");

	public transient static final LCMStatus NOT_SUPPORTED = new LCMStatus(
			new Integer(0), "not supported");

	public transient static final LCMStatus STARTED = new LCMStatus(
			new Integer(1), "started");

	public transient static final LCMStatus STOPPED = new LCMStatus(
			new Integer(2), "stopped");

	public transient static final LCMStatus STARTING = new LCMStatus(
			new Integer(3), "starting");

	public transient static final LCMStatus STOPPING = new LCMStatus(
			new Integer(4), "stopping");

	public transient static final LCMStatus UPGRADING = new LCMStatus(
			new Integer(5), "upgrading");

	public transient static final LCMStatus IMPLICIT_STOPPED = new LCMStatus(
			new Integer(6), "implicit stopped");
	
	public transient static final LCMStatus MARKED_FOR_REMOVAL = new LCMStatus(
			new Integer(7), "marked for removal");

	private final Integer id;
	private final String name;
	private LCMStatusDetails lcmStatusDetails;

	private LCMStatus(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	private Integer getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String toString() {
		return this.name;
	}

	public void setLCMStatusDetails(LCMStatusDetails lcmStatusDetails) {
		this.lcmStatusDetails = lcmStatusDetails;
	}

	public LCMStatusDetails getLCMStatusDetails() {
		return this.lcmStatusDetails;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof LCMStatus)) {
			return false;
		}

		final LCMStatus other = (LCMStatus) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return id.hashCode();
	}

}
