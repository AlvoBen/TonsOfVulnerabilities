package com.sap.engine.services.dc.cm.undeploy.impl;

import com.sap.engine.services.dc.cm.undeploy.ScaUndeployItemId;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-30
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class ScaUndeployItemIdImpl implements ScaUndeployItemId {

	private static final long serialVersionUID = 2822739452287465151L;


	private final String name;
	private final String vendor;
	private final String toString;
	private final int hashCode;

	private int idCount = -1;

	ScaUndeployItemIdImpl(String name, String vendor) {
		this.name = name;
		this.vendor = vendor;

		final int offset = 17;
		final int multiplier = 59;
		int hash = offset + this.getName().hashCode();

		this.hashCode = hash * multiplier + this.getVendor().hashCode();

		this.toString = this.getVendor() + "_" + this.getName();
	}

	public String getName() {
		return this.name;
	}

	public String getVendor() {
		return this.vendor;
	}

	public int getIdCount() {
		return this.idCount;
	}

	public void setIdCount(int idCount) {
		this.idCount = idCount;
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

		final ScaUndeployItemIdImpl otherId = (ScaUndeployItemIdImpl) obj;

		if (!this.getName().equals(otherId.getName())) {
			return false;
		}

		if (!this.getVendor().equals(otherId.getVendor())) {
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

}
