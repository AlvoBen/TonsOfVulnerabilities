package com.sap.engine.services.dc.event;

import java.io.Serializable;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-4-29
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public final class ClusterEventAction implements Serializable {

	private static final long serialVersionUID = 7854778461609866894L;

	public transient static final ClusterEventAction CLUSTER_RESTART_TRIGGERED = new ClusterEventAction(
			new Integer(0), "cluster restart triggered",
			"The Application Server Java will be restarted");

	private final Integer id;
	private final String name;
	private final String description;

	private ClusterEventAction(Integer id, String name, String description) {
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

		if (!(obj instanceof ClusterEventAction)) {
			return false;
		}

		final ClusterEventAction other = (ClusterEventAction) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return id.hashCode();
	}

}
