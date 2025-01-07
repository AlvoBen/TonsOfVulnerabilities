package com.sap.engine.services.dc.event;

import java.io.Serializable;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-4-27
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public final class DeploymentEventAction implements Serializable {

	private static final long serialVersionUID = -1595217746011312483L;

	public transient static final DeploymentEventAction DEPLOYMENT_TRIGGERED = new DeploymentEventAction(
			new Integer(0), "deployment triggered",
			"The deployment has been triggered");

	public transient static final DeploymentEventAction DEPLOYMENT_PERFORMED = new DeploymentEventAction(
			new Integer(1), "deployment performed",
			"The deployment has been performed");

	private final Integer id;
	private final String name;
	private final String description;

	private DeploymentEventAction(Integer id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public Integer getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
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

		if (!(obj instanceof DeploymentEventAction)) {
			return false;
		}

		final DeploymentEventAction other = (DeploymentEventAction) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return id.hashCode();
	}

}
