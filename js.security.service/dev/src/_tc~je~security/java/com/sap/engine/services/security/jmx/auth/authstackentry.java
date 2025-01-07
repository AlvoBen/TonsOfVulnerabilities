package com.sap.engine.services.security.jmx.auth;

import javax.management.openmbean.CompositeData;

/**
 * @author Georgi Dimitrov  (i031654)
 *
 */
public interface AuthStackEntry extends CompositeData {

    public static final String CLASS_NAME = "ClassName";
	public static final String FLAG = "Flag";
	public static final String OPTIONS = "Options";
  

    public String getClassName();
 	public String getFlag();
 	public MapEntry[] getOptions();
 	
}
