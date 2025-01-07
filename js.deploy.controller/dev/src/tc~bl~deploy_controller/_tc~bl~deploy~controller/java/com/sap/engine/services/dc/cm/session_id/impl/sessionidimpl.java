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
public class SessionIDImpl implements SessionID {

	private final String id;
	private final int hashCode;

	public SessionIDImpl(String id) {
		this.id = id;
		hashCode = getID().hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.session_id.SessionID#getID()
	 */
	public String getID() {
		return this.id;
	}

	public String toString() {
		return getID();
	}

	public int hashCode() {
		return this.hashCode;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		final SessionIDImpl otherSid = (SessionIDImpl) obj;

		if (!this.getID().equals(otherSid.getID())) {
			return false;
		}

		return true;
	}

}
