package com.sap.engine.services.iiop.jmx.model;

import javax.management.openmbean.CompositeData;

public interface IiopManagementMBean {

	public CompositeData[] getRemoteObjects();	
	
}
