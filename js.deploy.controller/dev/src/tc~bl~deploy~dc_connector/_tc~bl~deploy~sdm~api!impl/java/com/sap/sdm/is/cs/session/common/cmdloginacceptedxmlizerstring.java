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
public class CmdLoginAcceptedXMLizerString extends CmdLoginAcceptedXMLizer {

	private final static CmdLoginAcceptedXMLizer INSTANCE = new CmdLoginAcceptedXMLizerString();
	private final static Logger log = Logger.getLogger();

	private CmdLoginAcceptedXMLizerString() {
	}

	public static CmdLoginAcceptedXMLizer getInstance() {
		return INSTANCE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.client_server.api.xmlize.CmdLoginAcceptedXMLizer#fromXMLString
	 * (java.lang.String)
	 */
	public CmdIF fromXMLString(String input) throws CmdReconstructionException {

		String sessionID = null;
		String apiServerVersion = null;

		try {
			StringPos laPos = StringFinder.findXMLElem(CmdLoginAccepted.NAME,
					input);
			StringPos sesPos = StringFinder.findOptXMLElem(XML_SESSION_ID,
					input);
			if (sesPos != null) {
				sessionID = XMLFilter.checkAndFilterInput(input.substring(
						sesPos.beginPos + 11, sesPos.endPos));
			}

			StringPos apiPos = StringFinder.findOptXMLElem(XML_API_VERSION,
					input);
			if (apiPos != null) {
				apiServerVersion = XMLFilter.checkAndFilterInput(input
						.substring(apiPos.beginPos + 12, apiPos.endPos));
			}

			return CmdFactory.createCmdLoginAccepted(sessionID,
					apiServerVersion);
		} catch (Exception exc) {
			String strErrMsg = "CmdLoginAcceptedXMLizer could not reconstruct the command";
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
	}

}
