package com.sap.engine.services.dc.api.undeploy.impl;

import com.sap.engine.services.dc.api.undeploy.SdaUndeployItem;

public final class SdaUndeployItemImpl extends UndeployItemImpl implements SdaUndeployItem{

	private static final String prefix = "SdaUndeployItem";
	
	SdaUndeployItemImpl(String name, String vendor) {
		super(name, vendor);
	}

	SdaUndeployItemImpl(String name, String vendor, String location,
			String version) {
		super(name, vendor, location, version);
	}
	
	public String toString() {
		return prefix + super.toString();
	}

}
