package com.sap.sdm.is.cs.cmd;

import java.util.HashMap;
import java.util.Map;

import com.sap.sdm.is.stringxml.StringFinder;
import com.sap.sdm.util.log.Logger;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
public final class CmdXMLFactory {

	private final static CmdXMLFactory INSTANCE = new CmdXMLFactory();

	private final static Logger log = Logger.getLogger();

	private final Map xmlizerMap = new HashMap();

	private CmdXMLFactory() {
	}

	public static CmdXMLFactory getInstance() {
		return INSTANCE;
	}

	public CmdIF fromXmlString(String xmlString) {

		String topElement = StringFinder.getNameOfTopXMLElement(xmlString);
		if (topElement == null) {
			com.sap.sdm.is.cs.cmd.CmdError resultCmd = CmdErrorFactory
					.createCmdError("CmdXmlFactory could not find Top Element within String: \""
							+ xmlString + "\".");
			return resultCmd;
		}

		CmdXMLizer xmlizer = (CmdXMLizer) xmlizerMap.get(topElement);
		if (xmlizer == null) {
			com.sap.sdm.is.cs.cmd.CmdError resultCmd = CmdErrorFactory
					.createCmdError("CmdXmlFactory got unknown XML element: \""
							+ topElement + "\".");
			return resultCmd;
		}
		try {
			return xmlizer.fromXMLString(xmlString);
		} catch (CmdReconstructionException xmlre) {
			String strErrMsg = "Error in command reconstruction. "
					+ "\nAdditional information: \n";
			StringBuffer sbErrMsg = new StringBuffer();
			sbErrMsg.append(
					"CmdXmlFactory could not reconstruct the command: \"")
					.append(topElement)
					// .append("\" from the String: \"")
					// .append(xmlString)
					// .append("\". \nThe exception is: ")
					.append(". \nThe exception is: \"").append(
							xmlre.getMessage());
			log.error(strErrMsg + sbErrMsg.toString(), xmlre);
			com.sap.sdm.is.cs.cmd.CmdError errCmd = CmdErrorFactory
					.createCmdError(strErrMsg + xmlre.getMessage());
			return errCmd;
		}

	}

	public void addXmlizer(String topElementName, CmdXMLizer xmlizer) {
		xmlizerMap.put(topElementName, xmlizer);
	}

	public CmdXMLizer getXMLizer(String topElementName) {
		if (topElementName == null) {
			return null;
		}
		return (CmdXMLizer) xmlizerMap.get(topElementName);
	}

}
