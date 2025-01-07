package com.sap.engine.services.rmi_p4.jmx.model;

import javax.management.openmbean.CompositeData;

public interface P4ManagementMBean {
	
	public CompositeData[] getRemoteObjects();	

}
