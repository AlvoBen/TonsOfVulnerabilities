package com.sap.engine.lib.deploy.sda;

import com.sap.engine.lib.deploy.sda.exceptions.DeployLibException;
import com.sap.engine.lib.deploy.sda.logger.Logger;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class RuntimeDependency extends Dependency {

	private static final Location loc = Location
			.getLocation(RuntimeDependency.class);
	private String softwareType = null;
	private String referenceType = null;

	public RuntimeDependency(String softwareType, String referenceType,
			String vendor, String name) throws DeployLibException {
		super(vendor, name);
		this.setSoftwareType(softwareType);
		this.setReferenceType(referenceType);

	}

	public void setSoftwareType(String softwareType) {
		this.softwareType = softwareType;
		Logger.trace(loc, Severity.DEBUG, "Dependency software type "
				+ softwareType);
	}

	public String getSoftwareType() {
		return softwareType;
	}

	public void setReferenceType(String referenceType) {
		this.referenceType = referenceType;
		Logger.trace(loc, Severity.DEBUG, "Dependency reference type "
				+ referenceType);
	}

	public String getReferenceType() {
		return referenceType;
	}
	
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof RuntimeDependency)) {
			return false;
		}

		RuntimeDependency dep = (RuntimeDependency) obj;

		if (!this.name.equals(dep.name)) {
			return false;
		}

		if (!this.vendor.equals(dep.vendor)) {
			return false;
		}
		
		if (!this.softwareType.equals(dep.softwareType)) {
			return false;
		}

		if (!this.referenceType.equals(dep.referenceType)) {
			return false;
		}

		return true;
	}
	
	public int hashCode() {
		return this.toString().hashCode();
	}

	public String toString() {
		return softwareType + "/" + referenceType + "/" + vendor + "/" + name;
	}

}
