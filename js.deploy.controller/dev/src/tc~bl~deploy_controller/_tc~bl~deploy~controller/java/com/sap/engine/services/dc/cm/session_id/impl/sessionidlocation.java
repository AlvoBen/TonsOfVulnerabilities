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
package com.sap.engine.services.dc.cm.session_id.impl;

import com.sap.engine.services.dc.cm.session_id.SessionID;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
final class SessionIDLocation {

	private final String location;
	private SessionID sessionId;

	SessionIDLocation(String location) {
		this.location = location;
	}

	SessionIDLocation(String location, SessionID sessionID) {
		this.location = location;
		setSessionId(sessionID);
	}

	public String getLocation() {
		return location;
	}

	public SessionID getSessionId() {
		return sessionId;
	}

	public void setSessionId(SessionID sessionID) {
		sessionId = sessionID;
	}

}
