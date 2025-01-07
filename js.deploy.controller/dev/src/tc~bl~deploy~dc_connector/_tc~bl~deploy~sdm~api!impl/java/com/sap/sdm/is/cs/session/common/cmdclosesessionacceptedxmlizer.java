package com.sap.sdm.is.cs.session.common;

import com.sap.sdm.is.stringxml.StringXMLizer;
import com.sap.sdm.is.stringxml.StringXMLizerFactory;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
abstract public class CmdCloseSessionAcceptedXMLizer {
	protected final String XML_SESSION_ID = "SessionID";

	public String toXMLString(CmdCloseSessionAccepted cmdCS) throws Exception {

		StringXMLizerFactory factory = StringXMLizerFactory.getInstance();
		StringXMLizer sXML = factory
				.createStringXMLizer(CmdCloseSessionAccepted.NAME);
		sXML.addElemWithContent(XML_SESSION_ID, cmdCS.getSessionID());
		sXML.endRootElem();
		return sXML.toString();
	}

	abstract public CmdCloseSessionAccepted fromXMLString(String input)
			throws Exception;
}
