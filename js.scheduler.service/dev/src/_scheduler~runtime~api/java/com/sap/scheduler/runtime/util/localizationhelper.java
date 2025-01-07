/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.scheduler.runtime.util;

import java.util.HashMap;
import java.util.Locale;


/**
 * This class provides some helper methods for the handling of the localization 
 * information. 
 *
 * @author Thomas Mueller (d040939)
 */

public class LocalizationHelper {    
    
    /**
     * Adds a property to the given Locale. If a Locale does not yet exist it 
     * will be added.
     *  
     * @param map the map to add the property
     * @param l the Locale to which the key value paier should be added
     * @param key the key to add
     * @param value the value to add
     */
    public static void addPropertyToLocale(HashMap<String, HashMap<String, String>> map, Locale l, String key, String value) { 
        String loc = getStringFromLocale(l);
    	HashMap<String, String> innerMap = map.get(loc); 
        if (innerMap != null) {
            innerMap.put(key, value);
        } else {
        	innerMap = new HashMap<String, String>();
        	innerMap.put(key, value);
            map.put(loc, innerMap);
        }        
    }
    
 
    /**
     * Compares two Map objects of equality. There are equal if the Properties-
     * and the Locale-instances are equal in their sense of equality.
     * 
     * @param map1 Map to compare
     * @param map2 Map to compare
     * 
     * @return true if they are equal, false otherwise
     */
    public static boolean compareTo(HashMap<String, HashMap<String, String>> map1, HashMap<String, HashMap<String, String>> map2) {
        if (map1 == null && map2 == null) {
            return true;
        }
        if ( (map1 == null && map2 != null) || (map1 != null && map2 == null)) {            
            return false;
        }
        
        return map1.equals(map2);
    }
    
    
    /**
     * Method takes a String in the format language or language concatenated
     * with the country (e.g. 'de_CH' or 'en') and returns a Locale instance
     * accordingly. Locales with given variant are not supported.
     * 
     * @param str the String 
     * @return the Locale instance according to the given String
     * 
     * @throws IllegalArgumentException if the incoming String is not valid
     */
    public static Locale getLocaleFromString(String str) throws IllegalArgumentException {
        // The Locale of an SAP_ITSAMJavaSchedulerProperty can only have the format
        // only with the language of with both, the language and the country, 
        // separated by a '_'
        
        if (str == null || str.length() < 2 || str.length() > 5) {
            throw new IllegalArgumentException("Locale-String '"+str+"' is not a valid Locale-String.");
        }
        
        Locale locale = null;
        
        // only 2 or 5 are possible as lengths, e.g. 'de_CH' or 'en'
        if (str.length() == 2) {            
            locale = new Locale(str);
        } else if (str.length() == 5) {            
            locale = new Locale(str.substring(0, 2), str.substring(3));
        }
        
        return locale;
    }
    
    
    /**
     * Method returns a String representation of a given Locale instance. The format
     * of the returned String is e.g. 'de_CH'. The variant information will be 
     * ignored.
     * 
     * @param l the Locale 
     * @return the String representation according to the given Locale
     * 
     * @throws IllegalArgumentException if the incoming Locale is not valid
     */
    public static String getStringFromLocale(Locale l) throws IllegalArgumentException {     
    	String locale = "";
    	
        if ( l == null || l.getLanguage() == null || l.getLanguage().length() == 0 ) {
            throw new IllegalArgumentException("Locale '"+l+"' is not a valid Locale.");
        } 
        
        locale = l.getLanguage();
        
        if (l.getCountry() != null && l.getCountry().length() != 0) {
        	locale = locale + "_" + l.getCountry();
        }
        
        return locale;
    }
    
    
}
