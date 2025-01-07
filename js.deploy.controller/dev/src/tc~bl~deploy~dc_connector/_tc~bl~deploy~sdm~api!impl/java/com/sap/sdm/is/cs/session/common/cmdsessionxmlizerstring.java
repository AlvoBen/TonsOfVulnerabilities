package com.sap.sdm.is.cs.session.common;

import com.sap.sdm.is.cs.cmd.CmdIF;
import com.sap.sdm.is.cs.cmd.CmdReconstructionException;
import com.sap.sdm.is.stringxml.StringFinder;
import com.sap.sdm.is.stringxml.StringPos;
import com.sap.sdm.util.log.Logger;
import com.sap.sdm.util.xml.XMLFilter;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
public class CmdSessionXMLizerString extends CmdSessionXMLizer {

	private final static CmdSessionXMLizer INSTANCE = new CmdSessionXMLizerString();
	private final static Logger log = Logger.getLogger();

	private CmdSessionXMLizerString() {
	}

	public static CmdSessionXMLizer getInstance() {
		return INSTANCE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.client_server.api.xmlize.CmdSessionXMLizer#fromXMLString(
	 * java.lang.String)
	 */
	public CmdIF fromXMLString(String input) throws CmdReconstructionException {
		String sessionID = null;
		try {
			StringPos sesPos = StringFinder.findXMLElem(XML_SESSION_ID, input);
			sessionID = XMLFilter.checkAndFilterInput(input.substring(
					sesPos.beginPos + 11, sesPos.endPos));

			StringPos cmdPos = StringFinder.findOptXMLElem(
					CmdCloseConnection.NAME, input);
			if (cmdPos != null) {
				return CmdFactory.createCmdCloseConnection(sessionID);
			}
			cmdPos = StringFinder.findOptXMLElem(CmdCloseSession.NAME, input);
			if (cmdPos != null) {
				return CmdFactory.createCmdCloseSession(sessionID);
			}
			cmdPos = StringFinder.findOptXMLElem(CmdCloseSessionAccepted.NAME,
					input);
			if (cmdPos != null) {
				return CmdFactory.createCmdCloseSessionAccepted(sessionID);
			}
			cmdPos = StringFinder.findOptXMLElem(CmdReopenConnection.NAME,
					input);
			if (cmdPos != null) {
				return CmdFactory.createCmdReopenConnection(sessionID);
			}
			cmdPos = StringFinder.findOptXMLElem(
					CmdReopenConnectionAccepted.NAME, input);
			if (cmdPos != null) {
				return CmdFactory.createCmdReopenConnectionAccepted(sessionID);
			}
		} catch (Exception exc) {
			String strErrMsg = "CmdSessionXMLizerString could not reconstruct the command";
			StringBuffer sbErrMsg = new StringBuffer();
			sbErrMsg
					.append(strErrMsg)
					// .append(" from the String: \"")
					// .append(input)
					// .append("\". \nThe exception is: \"")
					.append(". \nThe exception is: \"")
					.append(exc.getMessage()).append("\".");
			log.error(sbErrMsg.toString(), exc);
			throw new CmdReconstructionException(strErrMsg);
		}
		// this seems to be an unknown element --> return null
		String strErrMsg = "CmdSessionXMLizerString found unexpected element."
				+ "\nThe command could not be reconstructed.";
		throw new CmdReconstructionException(strErrMsg);
	}

}
