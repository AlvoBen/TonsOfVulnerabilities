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
public abstract class CmdLoginAcceptedXMLizer implements CmdXMLizer {
	protected final static String XML_SESSION_ID = "SessionID";
	protected final static String XML_API_VERSION = "APIVersion";

	public String toXMLString(CmdIF cmd) {

		CmdLoginAccepted cmdLA = (CmdLoginAccepted) cmd;
		StringXMLizerFactory factory = StringXMLizerFactory.getInstance();
		StringXMLizer sXML = factory.createStringXMLizer(CmdLoginAccepted.NAME);
		if (cmdLA.getSessionID() != null) {
			sXML.addElemWithContent(XML_SESSION_ID, cmdLA.getSessionID());
			sXML.addElemWithContent(XML_API_VERSION, cmdLA
					.getAPIServerVersion());
		}
		sXML.endRootElem();
		return sXML.getString();
	}

	abstract public CmdIF fromXMLString(String input)
			throws CmdReconstructionException;
}
