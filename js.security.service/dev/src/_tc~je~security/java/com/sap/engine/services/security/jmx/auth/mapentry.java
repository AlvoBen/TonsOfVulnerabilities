package com.sap.engine.services.security.jmx.auth;

import javax.management.openmbean.CompositeData;

/**
 * @author Georgi Dimitrov (i031654)
 * 
 */
public interface MapEntry extends CompositeData {

    public static final String EMPTY = "";
    public static final String KEY = "Key";
    public static final String VALUE = "Value";
   
    public String getKey();
    public String getValue();

}

 