/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.api.util.measurement;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;
/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Measurement</DD>
 * 
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>Jan 11, 2009</DD>
 * </DL>
 * 
 * @author Radoslav Ivanov(i031258)
 * @version 1.0
 * @since 7.20
 */
public interface DAMeasurement extends Serializable {
	/**
	 * Returns development component name, performing the operation/process
	 * @return development component name, performing the operation/process
	 */
	String getDcName();
	
	/**
	 * Returns human readable name of operation/process
	 * @return human readable name of operation/process
	 */
	String getTagName();
	
	/**
	 * Returns set of consumed resource statistics
	 * @return set of consumed resource statistics
	 */
	Set getStatistics();
	
	/**
	 * Returns children measurements of the current one
	 * @return children measurements of the current one
	 */
	List getChildrenMeasurments();
	
	/**
	 * Returns document representation of this measurement
	 * @return document representation of this measurement
	 */
	Document toDocument();
	
	/**
	 * Returns string representation of measurement's document
	 * @return string representation of measurement's document
	 */
	String toDocumentAsString();
	
	/**
	 * Checks if a new thread was started in this measurement (except in the children measurements)
	 * @return <code>true</code> it new thread was started, otherwise <code>false</code>
	 */
	Boolean hasNewThreadStarted();

}
