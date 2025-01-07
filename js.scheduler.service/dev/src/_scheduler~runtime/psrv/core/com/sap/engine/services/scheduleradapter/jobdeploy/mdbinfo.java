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
package com.sap.engine.services.scheduleradapter.jobdeploy;


/**
 * This class represntes the key fields of the ebj jar descriptor
 * for a job
 * 
 * @author Dirk Marwinski
 */
public class MDBInfo { 
    
    public String messageSelector = "";
    public String[][] messageSelectors;
    public String displayName = "";
    public String ejbName = "";
    public String destinationType = "";
    public String applicationName;

	/**
	 * @return Returns the destinationType.
	 */
	public String getDestinationType() {
		return destinationType;
	}
	/**
	 * @param destinationType The destinationType to set.
	 */
	public void setDestinationType(String destinationType) {
		this.destinationType = destinationType;
	}
	/**
	 * @return Returns the displayName.
	 */
	public String getDisplayName() {
		return displayName;
	}
	/**
	 * @param displayName The displayName to set.
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	/**
	 * @return Returns the ejbName.
	 */
	public String getEjbName() {
		return ejbName;
	}
	/**
	 * @param ejbName The ejbName to set.
	 */
	public void setEjbName(String ejbName) {
		this.ejbName = ejbName;
	}
	/**
	 * @return Returns the messageSelector.
	 */
	public String[][] getMessageSelectors() {
		return messageSelectors;
	}
	/**
	 * @param messageSelector The messageSelector to set.
	 */
	public void setMessageSelector(String[][] messageSelector) {
		this.messageSelectors = messageSelector;
	}
    
    public String getMessageSelector() {
        return this.messageSelector;
    }
	
    public void setApplicationName(String name) {
        applicationName = name;
    }
    
    public String getApplicationName() {
        return applicationName;
    }
}
