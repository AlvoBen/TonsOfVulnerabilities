/*
 * Created on May 5, 2005
 */
package com.sap.engine.services.dc.api.event;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: Introduce actions over cluster during un/deployment.</B></DT>
 * <DD></DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2005-4-27</DD>
 * 
 * @author Boris Savov(i030791)
 * @version 1.0
 * @since 7.1
 * 
 */
public final class ClusterEventAction {
	/** Cluster restart is triggered. */
	public transient static final ClusterEventAction CLUSTER_RESTART_TRIGGERED = new ClusterEventAction(
			new Integer(0), "cluster restart triggered",
			"The J2EE Engine will be restarted");
	/** Cluster is available. */
	public transient static final ClusterEventAction CLUSTER_AVAILABLE = new ClusterEventAction(
			new Integer(1), "cluster available", "The J2EE Engine is available");

	private final Integer id;
	private final String name;
	private final String description;

	private ClusterEventAction(Integer id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	/**
	 * Returns the id of this action.
	 * 
	 * @return action id
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Gets this action's name.
	 * 
	 * @return the name of the action
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the description of this action.
	 * 
	 * @return description
	 */
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
		return this.id.hashCode();
	}

}
