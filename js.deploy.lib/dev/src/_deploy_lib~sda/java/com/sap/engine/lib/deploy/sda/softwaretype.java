package com.sap.engine.lib.deploy.sda;

import com.sap.engine.lib.deploy.sda.constants.Constants;

/**
 * Represents an application software type
 * @author Radoslav Popov
 */
public enum SoftwareType {
	
	J2EE (Constants.J2EE),
	SINGLE_MODULE (Constants.SINGLE_MODULE),
	LIBRARY (Constants.LIBRARY),
	PRIMARY_LIBRARY (Constants.PRIMARY_LIBRARY),
	PRIMARY_SERVICE (Constants.PRIMARY_SERVICE),
	PRIMARY_INTERFACE (Constants.PRIMARY_INTERFACE);
	
	private final String value;
	
	SoftwareType(String value){
		this.value = value;
	}
	
	public String getValue(){
		return value;
	}

}