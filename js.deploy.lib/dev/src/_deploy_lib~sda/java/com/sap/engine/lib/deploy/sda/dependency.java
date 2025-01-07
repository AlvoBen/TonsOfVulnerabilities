/* 
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.deploy.sda;

import com.sap.engine.lib.deploy.sda.logger.Logger;
import com.sap.engine.lib.deploy.sda.exceptions.DeployLibException;
import com.sap.engine.lib.deploy.sda.exceptions.ExceptionConstants;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * @author Mariela Todorova
 */
public class Dependency {

	private static final Location loc = Location.getLocation(Dependency.class);
	protected String vendor = null;
	protected String name = null;

	public Dependency(String vendor, String name)
			throws DeployLibException {
		setVendor(vendor);
		setName(name);
	}

	private void setVendor(String dep_vendor)
			throws DeployLibException {
		if (dep_vendor == null || dep_vendor.trim().equals("")) {
			throw new DeployLibException(loc,
					ExceptionConstants.ILLEGAL_VALUE, new String[] {
							dep_vendor, "dependency vendor" });
		}

		vendor = SDADescriptor.getCorrected(dep_vendor);
		Logger.trace(loc, Severity.DEBUG, "Dependency vendor " + vendor);
	}

	private void setName(String dep_name) throws DeployLibException {
		if (dep_name == null || dep_name.trim().equals("")) {
			throw new DeployLibException(loc,
					ExceptionConstants.ILLEGAL_VALUE, new String[] { dep_name,
							"dependency name" });
		}

		name = SDADescriptor.getCorrected(dep_name);
		Logger.trace(loc, Severity.DEBUG, "Dependency name " + name);
	}

	public String getVendor() {
		return vendor;
	}

	public String getName() {
		return name;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof Dependency)) {
			return false;
		}

		Dependency dep = (Dependency) obj;

		if (!this.name.equals(dep.name)) {
			return false;
		}

		if (!this.vendor.equals(dep.vendor)) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.toString().hashCode();
	}

	public String toString() {
		return vendor + "/" + name;
	}

}
