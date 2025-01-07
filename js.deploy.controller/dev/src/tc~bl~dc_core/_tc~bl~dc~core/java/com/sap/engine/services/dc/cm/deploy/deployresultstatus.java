package com.sap.engine.services.dc.cm.deploy;

import java.io.Serializable;

/**
 * 
 * Title: Software Deployment Manager Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-4-8
 * 
 * @author dimitar
 * @version 1.0
 * @since 6.40
 * 
 */
public final class DeployResultStatus implements Serializable {

	private static final long serialVersionUID = -9087494664345946912L;

	public transient static final DeployResultStatus ERROR = new DeployResultStatus(
			new Integer(0), "Error");

	public transient static final DeployResultStatus SUCCESS = new DeployResultStatus(
			new Integer(1), "Success");

	public transient static final DeployResultStatus WARNING = new DeployResultStatus(
			new Integer(2), "Warning");

	public transient static final DeployResultStatus UNKNOWN = new DeployResultStatus(
			new Integer(3), "Unknown");

	private final Integer id;
	private final String name;

	private DeployResultStatus(Integer id, String name) {
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

		if (!(obj instanceof DeployResultStatus)) {
			return false;
		}

		DeployResultStatus other = (DeployResultStatus) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return id.hashCode();
	}

}
