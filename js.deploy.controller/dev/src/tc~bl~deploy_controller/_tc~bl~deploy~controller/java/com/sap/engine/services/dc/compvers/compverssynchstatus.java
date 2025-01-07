package com.sap.engine.services.dc.compvers;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-26
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public final class CompVersSynchStatus {

	public static final CompVersSynchStatus COMPVERS_SYNC_OK = new CompVersSynchStatus(
			new Integer(0), "OK");

	public static final CompVersSynchStatus COMPVERS_SYNC_PARTIALLY_FAILED = new CompVersSynchStatus(
			new Integer(1), "Some updates of the component status failed");

	public static final CompVersSynchStatus COMPVERS_SYNC_FAILED = new CompVersSynchStatus(
			new Integer(2),
			"There is a general problem with updates of the component status");

	private final Integer id;
	private final String name;

	private CompVersSynchStatus(Integer id, String name) {
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

		if (!(obj instanceof CompVersSynchStatus)) {
			return false;
		}

		CompVersSynchStatus other = (CompVersSynchStatus) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return id.hashCode();
	}

}
