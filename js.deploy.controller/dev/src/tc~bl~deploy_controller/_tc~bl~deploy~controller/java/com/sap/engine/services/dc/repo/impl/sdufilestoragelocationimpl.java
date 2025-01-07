package com.sap.engine.services.dc.repo.impl;

import java.io.File;

import com.sap.engine.services.dc.repo.SduFileStorageLocation;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-24
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
abstract class SduFileStorageLocationImpl implements SduFileStorageLocation {

	private final String sduFileStorageLocation;
	private final int hashCode;
	private String toString;
	private File sduFile;

	SduFileStorageLocationImpl(String sduFileStorageLocation) {
		this(sduFileStorageLocation, null);
	}

	SduFileStorageLocationImpl(String sduFileStorageLocation, File sduFile) {
		this.sduFileStorageLocation = sduFileStorageLocation;
		this.hashCode = this.getLocation().hashCode();

		setSduFile(sduFile);
		this.toString = this.generateToString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repo.SduFileStorageLocation#getLocation()
	 */
	public String getLocation() {
		return this.sduFileStorageLocation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repo.SduFileStorageLocation#getSduFile()
	 */
	public File getSduFile() {
		return this.sduFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.SduFileStorageLocation#setSduFile(java
	 * .io.File)
	 */
	public void setSduFile(File sduFile) {
		this.sduFile = sduFile;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (this.getClass() != obj.getClass()) {
			return false;
		}

		final SduFileStorageLocation other = (SduFileStorageLocation) obj;

		if (!this.getLocation().equals(other.getLocation())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.hashCode;
	}

	public String toString() {
		return this.toString;
	}

	private String generateToString() {
		String filePath = getSduFile() == null ? null : getSduFile()
				.getAbsolutePath();

		return "location: '" + this.getLocation() + "', sdu file path: '"
				+ filePath + "'.";
	}
}
