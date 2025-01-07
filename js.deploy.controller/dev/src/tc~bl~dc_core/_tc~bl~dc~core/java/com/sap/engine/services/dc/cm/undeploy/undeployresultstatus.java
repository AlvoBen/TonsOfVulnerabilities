package com.sap.engine.services.dc.cm.undeploy;

import java.io.Serializable;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-22
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public final class UndeployResultStatus implements Serializable {

	private static final long serialVersionUID = -2637955818837365637L;

	public transient static final UndeployResultStatus ERROR = new UndeployResultStatus(
			new Integer(0), "Error");

	public transient static final UndeployResultStatus SUCCESS = new UndeployResultStatus(
			new Integer(1), "Success");

	public transient static final UndeployResultStatus WARNING = new UndeployResultStatus(
			new Integer(2), "Warning");

	public transient static final UndeployResultStatus UNKNOWN = new UndeployResultStatus(
			new Integer(3), "Unknown");

	private final Integer id;
	private final String name;

	private UndeployResultStatus(Integer id, String name) {
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
		return name;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof UndeployResultStatus)) {
			return false;
		}

		UndeployResultStatus other = (UndeployResultStatus) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return id.hashCode();
	}

}
