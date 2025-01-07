package com.sap.engine.services.dc.cm.deploy.impl;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-7
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class DeployPhase {

	public static final DeployPhase ONLINE = new DeployPhase(new Integer(0),
			"Online");

	public static final DeployPhase OFFLINE = new DeployPhase(new Integer(1),
			"Offline");

	public static final DeployPhase UNKNOWN = new DeployPhase(new Integer(2),
			"Unknown");

	public static final DeployPhase POST_ONLINE = new DeployPhase(
			new Integer(3), "Post-online");

	private final Integer id;
	private final String name;

	private DeployPhase(Integer id, String name) {
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

		if (!(obj instanceof DeployPhase)) {
			return false;
		}

		final DeployPhase other = (DeployPhase) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return id.hashCode();
	}

}
