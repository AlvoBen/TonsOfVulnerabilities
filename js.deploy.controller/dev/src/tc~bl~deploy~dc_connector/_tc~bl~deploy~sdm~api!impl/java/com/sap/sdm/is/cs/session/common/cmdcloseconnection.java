package com.sap.sdm.is.cs.session.common;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
public class CmdCloseConnection extends CmdSession {
	public final static String NAME = "CloseConnection";

	public CmdCloseConnection(String sessionID) {
		super(sessionID);
	}

	public String getMyName() {
		return NAME;
	}

	public void accept(CmdSessionVisitor visitor) {
		visitor.visitCmdCloseConnection(this);
	}
}
