package com.sap.engine.services.dc.cm.undeploy.impl;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-22
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class UndeployPhase {

	public static final UndeployPhase ONLINE = new UndeployPhase(
			new Integer(0), "Online");

	public static final UndeployPhase OFFLINE = new UndeployPhase(
			new Integer(1), "Offline");

	public static final UndeployPhase UNKNOWN = new UndeployPhase(
			new Integer(2), "Unknown");

	private final Integer id;
	private final String name;

	private UndeployPhase(Integer id, String name) {
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

		if (!(obj instanceof UndeployPhase)) {
			return false;
		}

		final UndeployPhase other = (UndeployPhase) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return id.hashCode();
	}

}
