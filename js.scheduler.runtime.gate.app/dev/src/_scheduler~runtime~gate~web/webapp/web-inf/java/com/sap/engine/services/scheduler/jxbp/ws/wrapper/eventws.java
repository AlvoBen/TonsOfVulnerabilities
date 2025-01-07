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
package com.sap.engine.services.scheduler.jxbp.ws.wrapper;

import java.io.Serializable;
import java.util.Date;

import com.sap.scheduler.runtime.AbstractIdentifier;
import com.sap.scheduler.runtime.Event;

/**
 * This class represents a concrete event to be used by the 
 * JXBP Web Service.
 * 
 * @author Thomas Mueller (d040939)
 */
public class EventWS implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
    private String  m_type = null;
    private String  m_parameter = null;
    private String  m_additionalParameter = null;
    private Date m_raisedDate = null;
    private AbstractIdentifier m_raisedByDetails = null;
    private byte[] m_eventId = null;
	
	// default constructor needed for WebService
	public EventWS() {		
	}
	
	
	public EventWS(Event event) {	
		m_type = event.getType();
		m_parameter = event.getParameter();
		m_additionalParameter = event.getAdditionalParameter();
		m_raisedDate = event.getRaisedDate();
		m_raisedByDetails = event.getRaisedByDetails();
		m_eventId = event.getId();
	}


	public String getAdditionalParameter() {
		return m_additionalParameter;
	}


	public void setAdditionalParameter(String parameter) {
		m_additionalParameter = parameter;
	}


	public byte[] getEventId() {
		return m_eventId;
	}


	public void setEventId(byte[] id) {
		m_eventId = id;
	}


	public String getParameter() {
		return m_parameter;
	}


	public void setParameter(String m_parameter) {
		this.m_parameter = m_parameter;
	}


	public AbstractIdentifier getRaisedByDetails() {
		return m_raisedByDetails;
	}


	public void setRaisedByDetails(AbstractIdentifier byDetails) {
		m_raisedByDetails = byDetails;
	}


	public Date getRaisedDate() {
		return m_raisedDate;
	}


	public void setRaisedDate(Date date) {
		m_raisedDate = date;
	}


	public String getType() {
		return m_type;
	}


	public void setType(String m_type) {
		this.m_type = m_type;
	}

	
}
