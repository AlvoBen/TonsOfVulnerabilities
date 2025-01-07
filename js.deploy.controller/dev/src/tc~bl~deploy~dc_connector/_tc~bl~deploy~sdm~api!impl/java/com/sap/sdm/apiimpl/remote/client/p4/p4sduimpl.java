/*
 * Created on 2005-2-8
 *
 * Author: Radoslav Ivanov
 * Team: Software Deployment Manager(SDM)
 */
package com.sap.sdm.apiimpl.remote.client.p4;

import com.sap.sdm.api.remote.model.Sdu;

/**
 * @author lalo-i
 * 
 */
abstract class P4SduImpl implements Sdu {

	private String name;
	private String vendor;
	private String location;
	private String version;
	private static final String NEW_LINE = "\r\n";

	P4SduImpl(com.sap.engine.services.dc.api.model.Sdu sdu) {
		name = sdu.getName();
		vendor = sdu.getVendor();
		location = sdu.getLocation();
		version = sdu.getVersion().getVersionAsString();
	}

	public String toString() {
		return "Component[Name: " + name + ", Vendor: " + vendor
				+ ", Location: " + location + ", Version: " + version + "]"
				+ NEW_LINE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.model.Sdu#getLocation()
	 */
	public String getLocation() {
		return location;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.model.Sdu#getName()
	 */
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.model.Sdu#getVendor()
	 */
	public String getVendor() {
		return vendor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.model.Sdu#getVersion()
	 */
	public String getVersion() {
		return version;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.model.Sdu#getArchiveFileLocation()
	 */
	public String getArchiveFileLocation() {
		throw new UnsupportedOperationException(
				"The operation is not supported!");
	}

}
