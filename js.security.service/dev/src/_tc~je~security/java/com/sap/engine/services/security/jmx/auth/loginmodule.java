package com.sap.engine.services.security.jmx.auth;

import javax.management.openmbean.CompositeData;

/**
 * @author Georgi Dimitrov (i031654)
 */
public interface LoginModule extends CompositeData{

    public final static String DISPLAY_NAME = "DisplayName"; 
	public final static String CLASS_NAME = "ClassName"; 
	public final static String DESCRIPTION = "Description"; 
	public final static String OPTIONS = "Options"; 
   
    public String getDisplayName();
	public String getClassName();
	public String getDescription();
 	public MapEntry[] getOptions();

}
