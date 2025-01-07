package com.sap.sdm.is.cs.filetransfer.common;

import com.sap.sdm.is.cs.cmd.CmdIF;
import com.sap.sdm.util.log.Trace;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Thomas Brodkorb
 * @version 1.0
 */
public class CmdFileOK implements CmdIF {
	private final static Trace trace = Trace.getTrace(CmdFileOK.class);
	public final static String NAME = "FileOK";

	public CmdFileOK() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.is.cs.cmd.CmdIFNew#getMyName()
	 */
	public String getMyName() {
		return NAME;
	}

}
