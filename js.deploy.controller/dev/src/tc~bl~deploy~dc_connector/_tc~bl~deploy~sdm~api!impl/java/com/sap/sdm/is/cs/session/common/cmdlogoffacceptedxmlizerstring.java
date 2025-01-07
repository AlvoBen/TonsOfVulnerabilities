package com.sap.sdm.is.cs.session.common;

import com.sap.sdm.is.cs.cmd.CmdIF;
import com.sap.sdm.is.cs.cmd.CmdReconstructionException;
import com.sap.sdm.is.stringxml.StringFinder;

/**
 * @author Christian Gabrisch 12.08.2003
 */
public final class CmdLogoffAcceptedXMLizerString extends
		CmdLogoffAcceptedXMLizer {

	private final static CmdLogoffAcceptedXMLizer INSTANCE = new CmdLogoffAcceptedXMLizerString();

	private CmdLogoffAcceptedXMLizerString() {
	}

	public static CmdLogoffAcceptedXMLizer getInstance() {
		return INSTANCE;
	}

	public CmdIF fromXMLString(String xmlString)
			throws CmdReconstructionException {
		String topXMLElementName = StringFinder
				.getNameOfTopXMLElement(xmlString);
		if (CmdLogoffAccepted.NAME.equals(topXMLElementName) == false) {
			StringBuffer sbErrMsg = new StringBuffer();
			sbErrMsg.append("CmdLogoffAcceptedXMLizerString does not ").append(
					"recognize this command. The XMLElementIf name is: \"")
					.append(topXMLElementName).append("\", but \"").append(
							CmdLogoffAccepted.NAME).append("\" is expected.");
			throw new CmdReconstructionException(sbErrMsg.toString());
		}

		return new CmdLogoffAccepted();
	}

}
