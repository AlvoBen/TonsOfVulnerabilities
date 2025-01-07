package com.sap.engine.lib.deploy.sda;

import com.sap.engine.lib.deploy.sda.constants.Constants;

/**
 * Represents an application software sub type
 * @author Radoslav Popov
 */
public enum SoftwareSubType {
	
	JAR (Constants.JAR_SUBTYPE),
	WAR (Constants.WAR_SUBTYPE),
	RAR (Constants.RAR_SUBTYPE);
	
	private final String value;
	
	SoftwareSubType(String value){
		this.value = value;
	}
	
	public String getValue(){
		return value;
	}

}