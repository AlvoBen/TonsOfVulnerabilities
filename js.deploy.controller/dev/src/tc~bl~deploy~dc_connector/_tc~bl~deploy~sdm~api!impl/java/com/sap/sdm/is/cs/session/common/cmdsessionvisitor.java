package com.sap.sdm.is.cs.session.common;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
public interface CmdSessionVisitor {

	void visitCmdCloseConnection(CmdCloseConnection cmd);

	void visitCmdCloseSession(CmdCloseSession cmd);

	void visitCmdCloseSessionAccepted(CmdCloseSessionAccepted cmd);

	void visitCmdReopenConnection(CmdReopenConnection cmd);

	void visitCmdReopenConnectionAccepted(CmdReopenConnectionAccepted cmd);

}
