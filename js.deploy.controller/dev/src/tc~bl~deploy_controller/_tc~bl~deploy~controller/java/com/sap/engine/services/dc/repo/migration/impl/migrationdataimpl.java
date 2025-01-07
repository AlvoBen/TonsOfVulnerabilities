package com.sap.engine.services.dc.repo.migration.impl;

import com.sap.engine.services.dc.repo.migration.MigrationData;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-2-2
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
final class MigrationDataImpl implements MigrationData {

	private final int version;
	private final String description;
	private final String toString;

	MigrationDataImpl(int version, String description) {
		this.version = version;
		this.description = description;
		this.toString = generateToString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repo.migration.MigrationData#getVersion()
	 */
	public int getVersion() {
		return this.version;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.migration.MigrationData#getDescription()
	 */
	public String getDescription() {
		return this.description;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof MigrationData)) {
			return false;
		}

		final MigrationData other = (MigrationData) obj;

		if (this.getVersion() != other.getVersion()) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.version;
	}

	public String toString() {
		return this.toString;
	}

	private String generateToString() {
		return "migration version: " + this.version + ", description: "
				+ this.description;
	}

}
