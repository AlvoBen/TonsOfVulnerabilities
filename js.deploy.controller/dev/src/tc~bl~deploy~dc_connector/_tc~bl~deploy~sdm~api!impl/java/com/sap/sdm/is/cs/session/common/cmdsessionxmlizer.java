package com.sap.sdm.is.cs.session.common;

import com.sap.sdm.is.cs.cmd.CmdIF;
import com.sap.sdm.is.cs.cmd.CmdReconstructionException;
import com.sap.sdm.is.cs.cmd.CmdXMLizer;
import com.sap.sdm.is.stringxml.StringXMLizer;
import com.sap.sdm.is.stringxml.StringXMLizerFactory;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
public abstract class CmdSessionXMLizer implements CmdXMLizer,
		CmdSessionVisitor {
	protected final static String XML_SESSION_ID = "SessionID";

	String result = null;
	CmdSession cmdSession;

	public String toXMLString(CmdIF cmd) {

		this.cmdSession = (CmdSession) cmd;
		cmdSession.accept(this);

		return this.result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.sdm.client_server.api.common.CmdSessionVisitor#
	 * visitCmdCloseConnection
	 * (com.sap.sdm.client_server.api.common.CmdCloseConnection)
	 */
	public void visitCmdCloseConnection(CmdCloseConnection cmd) {
		result = toXMLStringInt(CmdCloseConnection.NAME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.client_server.api.common.CmdSessionVisitor#visitCmdCloseSession
	 * (com.sap.sdm.client_server.api.common.CmdCloseSession)
	 */
	public void visitCmdCloseSession(CmdCloseSession cmd) {
		result = toXMLStringInt(CmdCloseSession.NAME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.sdm.client_server.api.common.CmdSessionVisitor#
	 * visitCmdCloseSessionAccepted
	 * (com.sap.sdm.client_server.api.common.CmdCloseSessionAccepted)
	 */
	public void visitCmdCloseSessionAccepted(CmdCloseSessionAccepted cmd) {
		result = toXMLStringInt(CmdCloseSessionAccepted.NAME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.sdm.client_server.api.common.CmdSessionVisitor#
	 * visitCmdReopenConnection
	 * (com.sap.sdm.client_server.api.common.CmdReopenConnection)
	 */
	public void visitCmdReopenConnection(CmdReopenConnection cmd) {
		result = toXMLStringInt(CmdReopenConnection.NAME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.sdm.client_server.api.common.CmdSessionVisitor#
	 * visitCmdReopenConnectionAccepted
	 * (com.sap.sdm.client_server.api.common.CmdReopenConnectionAccepted)
	 */
	public void visitCmdReopenConnectionAccepted(CmdReopenConnectionAccepted cmd) {
		result = toXMLStringInt(CmdReopenConnectionAccepted.NAME);
	}

	public abstract CmdIF fromXMLString(String input)
			throws CmdReconstructionException;

	private String toXMLStringInt(String elemName) {

		StringXMLizerFactory factory = StringXMLizerFactory.getInstance();
		StringXMLizer sXML = factory.createStringXMLizer(elemName);
		sXML.addElemWithContent(XML_SESSION_ID, this.cmdSession.getSessionID());
		sXML.endRootElem();
		return sXML.getString();
	}

}
