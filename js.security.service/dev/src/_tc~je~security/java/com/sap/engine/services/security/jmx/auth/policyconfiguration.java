package com.sap.engine.services.security.jmx.auth;

import javax.management.openmbean.CompositeData;

/**
 * @author Georgi Dimitrov (i031654)
 *
 */
public interface PolicyConfiguration extends CompositeData{

    public final static String NAME = "Name";
    public final static String TEMPLATE = "Template";
    public final static String STACK_ENTRIES = "AuthStack";
    public final static String PROPERTIES = "Properties";
    public final static String TYPE = "Type";
   
    public String getName();
	public Byte getType();
 	public String getTemplate();
	public AuthStackEntry[] getAuthStack();
	public MapEntry[] getProperties();
 
}
