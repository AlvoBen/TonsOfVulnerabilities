package com.sap.jmx.provider;

import javax.management.ObjectName;

import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class ObjectNameContextImpl implements ObjectNameContext {

	private static final Location LOCATION = Location.getLocation(ObjectNameContextImpl.class);
	
	private String sap_ITSAMJ2eeClusterCreationClassName = "SAP_ITSAMJ2eeCluster";
	private String sap_ITSAMJ2eeApplicationCreationClassName = "SAP_ITSAMJ2eeApplication";
	private String sap_ITSAMJ2eeInstanceCreationClassName = "SAP_ITSAMJ2eeInstance";
	
	private String sap_ITSAMJ2eeClusterName;	
	private String sap_ITSAMJ2eeInstanceName;
	private String j2eeServer;
	private String sap_J2EEClusterNode;
	
	private boolean isInitialized;
	private MBeanServerAccess server;
	
	public ObjectNameContextImpl(MBeanServerAccess server) {
		isInitialized = false;
		this.server = server;
	}
	
	public String getSAP_ITSAMJ2eeClusterName() {
		if (!isInitialized) {
			initialize();
		}
		return sap_ITSAMJ2eeClusterName;
	}
	
	public String getSAP_ITSAMJ2eeClusterCreationClassName() {
		return sap_ITSAMJ2eeClusterCreationClassName;
	}
	
	public String getSAP_ITSAMJ2eeApplicationCreationClassName() {
		return sap_ITSAMJ2eeApplicationCreationClassName;
	}
	
	public String getSAP_ITSAMJ2eeInstanceCreationClassName() {
		return sap_ITSAMJ2eeInstanceCreationClassName;
	}
	
	public String getSAP_ITSAMJ2eeInstanceName() {
		if (!isInitialized) {
			initialize();
		}
		return sap_ITSAMJ2eeInstanceName;
	}
	
	public String getJ2EEServer() {
		if (!isInitialized) {
			initialize();
		}
		return j2eeServer;
	}
	
	public String getSAP_J2EEClusterNode() {
		if (!isInitialized) {
			initialize();
		}
		return sap_J2EEClusterNode;
	}
	
	private synchronized void initialize() {
		if (!isInitialized) {
			//initialize the cluster & instance name
			ObjectName localNode = server.getLocalSAP_ITSAMJ2eeNode();
			if (localNode == null) {
				LOCATION.logT(Severity.ERROR, "Error when trying to initialize the object name context before registration of the SAP_ITSAMJ2eeNode mbean");		
			} else {
				sap_ITSAMJ2eeClusterName = localNode.getKeyProperty("SAP_ITSAMJ2eeCluster.Name");	
				sap_ITSAMJ2eeInstanceName = localNode.getKeyProperty("SAP_ITSAMJ2eeInstance.Name");
				j2eeServer = localNode.getKeyProperty("J2EEServer");
				sap_J2EEClusterNode = localNode.getKeyProperty("SAP_J2EEClusterNode");
				isInitialized = true;
			}
		}
	}
}
