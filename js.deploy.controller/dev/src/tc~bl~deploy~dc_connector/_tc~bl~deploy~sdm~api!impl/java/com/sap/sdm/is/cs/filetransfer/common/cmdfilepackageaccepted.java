package com.sap.sdm.is.cs.filetransfer.common;

import com.sap.sdm.is.cs.cmd.CmdIF;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
public class CmdFilePackageAccepted implements CmdIF {
	public final static String NAME = "FilePackageAccepted";

	String remoteFilename = null;

	public CmdFilePackageAccepted(String filename) {
		this.remoteFilename = filename;
	}

	public String getMyName() {
		return NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.client_server.api.common.CmdFilePackageAccepted#getRemoteFileName
	 * ()
	 */
	public String getRemoteFileName() {
		return this.remoteFilename;
	}

}
