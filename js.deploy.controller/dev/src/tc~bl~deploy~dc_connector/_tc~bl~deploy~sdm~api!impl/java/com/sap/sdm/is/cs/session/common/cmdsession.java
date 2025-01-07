package com.sap.sdm.is.cs.session.common;

import com.sap.sdm.is.cs.cmd.CmdIF;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
public abstract class CmdSession implements CmdIF {

	String sessionID = null;

	CmdSession(String sessionID) {
		this.sessionID = sessionID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.client_server.api.common.CmdSession#getSessionID()
	 */
	public String getSessionID() {
		return this.sessionID;
	}

	public abstract void accept(CmdSessionVisitor visitor);
}
