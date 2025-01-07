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

/**
 * This class represents a concrete log iterator to be used by the 
 * JXBP Web Service.
 * 
 * @author Thomas Mueller (d040939)
 */
public class LogIteratorWS implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private long m_pos = 0;
	private String m_nextChunk = null;


	// default constructor needed for WebService
	public LogIteratorWS() {		
	}
	
	
	public String getNextChunk() {
		return m_nextChunk;
	}

	public void setNextChunk(String chunk) {
		m_nextChunk = chunk;
	}

	public long getPos() {
		return m_pos;
	}

	public void setPos(long m_pos) {
		this.m_pos = m_pos;
	}
	
	public boolean hasNextChunk() {
		return m_pos != -1;
	}
}
