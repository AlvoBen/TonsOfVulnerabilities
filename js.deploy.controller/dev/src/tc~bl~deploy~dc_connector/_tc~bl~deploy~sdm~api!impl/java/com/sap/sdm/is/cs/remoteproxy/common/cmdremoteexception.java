package com.sap.sdm.is.cs.remoteproxy.common;

import com.sap.sdm.is.cs.cmd.CmdIF;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
public class CmdRemoteException implements CmdIF {
	public final static String NAME = "RemoteException";

	private String exceptionName = null;
	private String exceptionMsg = null;

	public CmdRemoteException(String name, String msg) {
		this.exceptionName = name;
		this.exceptionMsg = msg;
	}

	public String getMyName() {
		return NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.client_server.api.common.CmdRemoteException#getExceptionName
	 * ()
	 */
	public String getExceptionName() {
		return this.exceptionName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.client_server.api.common.CmdRemoteException#getExceptionMsg()
	 */
	public String getExceptionMsg() {
		return this.exceptionMsg;
	}

}
