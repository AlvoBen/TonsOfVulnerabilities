package com.sap.sdm.is.cs.session.common;

import com.sap.sdm.is.cs.cmd.CmdIF;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
public class CmdLogoffRequest implements CmdIF {
	public final static String NAME = "LogoffRequest";

	public CmdLogoffRequest() {
	}

	public String getMyName() {
		return NAME;
	}

}
