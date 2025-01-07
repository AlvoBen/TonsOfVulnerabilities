package com.sap.sdm.is.cs.cmd;

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
public class CmdErrorXMLizerString extends CmdErrorXMLizer {

	private final static CmdErrorXMLizer INSTANCE = new CmdErrorXMLizerString();
	private final static Logger log = Logger.getLogger();

	private CmdErrorXMLizerString() {
	}

	public static CmdErrorXMLizer getInstance() {
		return INSTANCE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.client_server.api.xmlize.CmdErrorXMLizer#fromXMLString(java
	 * .lang.String)
	 */
	public CmdIF fromXMLString(String input) throws CmdReconstructionException {

		boolean wrongPasswordSupplied = false;
		String errText = null;

		StringPos textPos = null;
		try {
			StringPos excPos = StringFinder.findXMLElemOpen(CmdError.NAME,
					input);
			String wPS = StringFinder.findAttrForXMLElem(CmdError.NAME, input,
					XML_ERROR_WP_ATTR);
			if (wPS != null) {
				wrongPasswordSupplied = new Boolean(wPS).booleanValue();
			}
			textPos = StringFinder.findOptXMLElem(XML_ERROR_TEXT, input);
		} catch (Exception exc) {
			String strErrMsg = "CmdErrorXMLizerString could not reconstruct the command";
			StringBuffer sbErrMsg = new StringBuffer();
			sbErrMsg
					.append(strErrMsg)
					// .append(" from String: \"")
					// .append(input)
					// .append("\". \nThe exception is: ")
					.append(". \nThe exception is: \"")
					.append(exc.getMessage());
			log.error(sbErrMsg.toString(), exc);
			throw new CmdReconstructionException(strErrMsg);
		}
		if (textPos != null) {
			errText = XMLFilter.checkAndFilterInput(input.substring(
					textPos.beginPos + 4, textPos.endPos));
		}
		return CmdErrorFactory.createCmdError(errText, wrongPasswordSupplied);
	}

}
