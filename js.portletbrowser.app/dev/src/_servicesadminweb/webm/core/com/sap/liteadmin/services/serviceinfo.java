package com.sap.liteadmin.services;

import java.util.logging.Logger;

public class ServiceInfo {
	
	String name = null;
	String status = null;
	Boolean isCore;
	
	private Logger log = Logger.getLogger(ServiceInfo.class.getName());
	
	public ServiceInfo(String name, String status, Boolean isCore) {
		this.name = name;
		this.status = status;
		this.isCore = isCore;
	}


	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

 /**
	 * @return Returns the status.
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @return Returns the statusString.
	 */
	public String getStatusString() {
      return getStatus();
	}

	/**
	 * @return Returns the isCore.
	 */
	public Boolean getIsCore() {
		return isCore;
	}	
}
