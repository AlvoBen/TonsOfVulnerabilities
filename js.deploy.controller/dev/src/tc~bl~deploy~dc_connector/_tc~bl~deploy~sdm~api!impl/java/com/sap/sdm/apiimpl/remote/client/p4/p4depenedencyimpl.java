/*
 * Created on 2005-7-4 by radoslav-i
 */
package com.sap.sdm.apiimpl.remote.client.p4;

import com.sap.sdm.api.remote.model.Dependency;

/**
 * @author radoslav-i
 */
class P4DepenedencyImpl implements Dependency {

	private String name;
	private String vendor;
	private static final String NEW_LINE = "\r\n";

	P4DepenedencyImpl(com.sap.engine.services.dc.api.model.Dependency dependency) {
		name = dependency.getName();
		vendor = dependency.getVendor();
	}

	public String toString() {
		return "Dependency[Name: " + name + ", Vendor: " + vendor + "]"
				+ NEW_LINE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.model.Dependency#getName()
	 */
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.model.Dependency#getVendor()
	 */
	public String getVendor() {
		return vendor;
	}

}
