package com.sap.sdm.is.cs.session.common;

import com.sap.sdm.is.cs.cmd.CmdIF;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
public class CmdLoginAccepted implements CmdIF {
	public final static String NAME = "LoginAccepted";
	private String sessionID = null;
	private String apiServerVersion = null;

	public CmdLoginAccepted(String sessionID, String apiServerVersion) {

		this.sessionID = sessionID;
		this.apiServerVersion = apiServerVersion;
	}

	public String getMyName() {
		return NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.client_server.api.common.CmdLoginAccepted#getSessionID()
	 */
	public String getSessionID() {
		return this.sessionID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.client_server.api.common.CmdLoginAccepted#getAPIServerVersion
	 * ()
	 */
	public String getAPIServerVersion() {
		return this.apiServerVersion;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.sdm.client_server.api.common.CmdLoginAccepted#
	 * getAPIServerVersionAsInt()
	 */
	public int getAPIServerVersionAsInt() {
		return new Integer(this.apiServerVersion).intValue();
	}

}
