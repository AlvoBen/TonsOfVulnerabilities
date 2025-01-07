/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.scheduler.util;

import java.util.TimeZone;

import com.sap.localization.LocalizableTextFormatter;

/**
 * This class provides a plain-text-only implementation
 * of the LocalizableText interface. This class is used
 * to pass a plain text message to the constructor of an
 * exception class written according to the SAP Exception
 * framework. SAP Exception framework has now been deprecated
 * and currently no localization of exception messaged can be
 * performed. Thus detailed infomration must be supplied as
 * english text only. However a lot of exception classes
 * defined in other components' APIs used by EJB Container
 * service don't accept plain text message but only
 * LocalizableText or LocalizableTextFormatter.
 
 * @author Hristo Sabev (I027642)
 */
public class LTF extends LocalizableTextFormatter {

	/**
	 * The serial version uid of this class as it
	 * is serializable
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The plain-text message represented by this
	 * LocalizableTextFormatter
	 */
	private String plainTextMessage;
	
	/**
	 * Constructs a new LTF instance for a given
	 * plain text messsage.
	 * @param the plain text message that is kept
	 * intact after every attempt to localize it
	 */
	public LTF(String plainTextMessage) {
		this.plainTextMessage = plainTextMessage;
	}
	
	/**
	 * Obtains the locale used by this LocalizableTextFormatter.
	 * It always return super.defaultLocale
	 * @return super.defaultLocale
	 * 
	 * @see com.sap.localization.LocalizableTextFormatter#getLocale()
	 */
	public java.util.Locale getLocale() {
		return defaultLocale;
	}
    
	/**
	 * Always returns TimeZone.getDefault() i.e the default
	 * time zone of the system.
	 * @return TimeZone.getDefault()
	 * @see com.sap.localization.LocalizableTextFormatter#getTimeZone()
	 */
    public java.util.TimeZone getTimeZone() {
    	return TimeZone.getDefault();
    }
    
    /**
     * Does nothing
     * 
     * @see com.sap.localization.LocalizableTextFormatter#setLocale(java.util.Locale)
     */
    public void setLocale(java.util.Locale locale) {
    	
    }
    
    /**
     * Does nothing
     * @see com.sap.localization.LocalizableTextFormatter#setTimeZone(java.util.TimeZone)
     */
    public void setTimeZone(java.util.TimeZone timeZone) {
    	
    }
    
    /**
     * Does nothing
     * @see com.sap.localization.LocalizableTextFormatter#finallyLocalize()
     */
    public void finallyLocalize() {
    	
    }
    
    /**
     * Does nothing
     * @see com.sap.localization.LocalizableTextFormatter#finallyLocalize(java.util.Locale)
     */
    public void finallyLocalize(java.util.Locale loc) {
    	
    }

    /**
     * Does nothing
     * @see com.sap.localization.LocalizableTextFormatter#finallyLocalize(java.util.Locale, java.util.TimeZone)
     */
    public void finallyLocalize(java.util.Locale loc, java.util.TimeZone tZone) {
    	
    }

    /**
     * Does nothing
     * @see com.sap.localization.LocalizableTextFormatter#finallyLocalize(java.util.TimeZone)
     */
    public void finallyLocalize(java.util.TimeZone tZone) {
    	
    }

    /**
     * Obtains the plain text message represented by this LTF
     * @return the plain text message represented by this LTF
     * @see com.sap.localization.LocalizableTextFormatter#format()
     */
    public java.lang.String format() {
    	return plainTextMessage;
    }
    
    /**
     * Obtains the plain message represented by this LTF. Thus supplied
     * paramter loc is ignored
     * @param loc - the locale according to which to localize the text.
     * This param is completely igonered.
     * @return the plain text message represented by this LTF
     * @see com.sap.localization.LocalizableTextFormatter#format(java.util.Locale)
     */
    public java.lang.String format(java.util.Locale loc) {
    	return plainTextMessage;
    }

    /**
     * Obtains the plain message represeneted by this LTF. Supplied parameters
     * are ignored
     * @param loc - ignored
     * @param tZone - ignored
     * @return the plain text message represented by this LTF
     * @see com.sap.localization.LocalizableTextFormatter#format(java.util.Locale, java.util.TimeZone)
     */
    public java.lang.String format(java.util.Locale loc, java.util.TimeZone tZone) {
    	return plainTextMessage;
    }

    /**
     * Obtains the plain message represeneted by this LTF. Supplied parameter
     * tZone is ignored
     * @param tZone - ignored
     * @return the plain text message represented by this LTF
     * @see com.sap.localization.LocalizableTextFormatter#format(java.util.TimeZone)
     */

    public java.lang.String format(java.util.TimeZone tZone) {
    	return plainTextMessage;
    }

    
    

}
