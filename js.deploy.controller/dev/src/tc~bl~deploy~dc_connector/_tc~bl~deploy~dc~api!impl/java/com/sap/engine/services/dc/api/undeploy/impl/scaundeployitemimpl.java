package com.sap.engine.services.dc.api.undeploy.impl;

import com.sap.engine.services.dc.api.undeploy.ScaUndeployItem;

public final class ScaUndeployItemImpl extends UndeployItemImpl implements ScaUndeployItem{

	private static final String prefix = "ScaUndeployItem";
	
	ScaUndeployItemImpl(String name, String vendor) {
		super(name, vendor);
	}

	ScaUndeployItemImpl(String name, String vendor, String location,
			String version) {
		super(name, vendor, location, version);
	}

	public String toString() {
		return prefix + super.toString();
	}

}
