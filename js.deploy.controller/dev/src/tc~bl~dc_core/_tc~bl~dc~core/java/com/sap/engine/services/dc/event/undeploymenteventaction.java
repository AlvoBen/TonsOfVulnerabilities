package com.sap.engine.services.dc.event;

import java.io.Serializable;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-4-28
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public final class UndeploymentEventAction implements Serializable {

	private static final long serialVersionUID = -882160610292583405L;

	public transient static final UndeploymentEventAction UNDEPLOYMENT_PERFORMED = new UndeploymentEventAction(
			new Integer(0), "undeployment performed",
			"The undeployment has been finished");

	public transient static final UndeploymentEventAction UNDEPLOYMENT_TRIGGERED = new UndeploymentEventAction(
			new Integer(1), "undeployment triggered",
			"The undeployment has been triggered");
	private final Integer id;
	private final String name;
	private final String description;

	private UndeploymentEventAction(Integer id, String name, String description) {
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
		return this.name;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof UndeploymentEventAction)) {
			return false;
		}

		final UndeploymentEventAction other = (UndeploymentEventAction) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.id.hashCode();
	}

}
