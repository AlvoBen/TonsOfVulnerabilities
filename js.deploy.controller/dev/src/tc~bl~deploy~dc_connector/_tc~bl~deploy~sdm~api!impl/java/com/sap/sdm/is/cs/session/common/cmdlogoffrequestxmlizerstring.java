package com.sap.sdm.is.cs.session.common;

import com.sap.sdm.is.cs.cmd.CmdIF;
import com.sap.sdm.is.cs.cmd.CmdReconstructionException;

/**
 * @author Christian Gabrisch 12.08.2003
 */
public final class CmdLogoffRequestXMLizerString extends
		CmdLogoffRequestXMLizer {

	private final static CmdLogoffRequestXMLizer INSTANCE = new CmdLogoffRequestXMLizerString();

	private CmdLogoffRequestXMLizerString() {
	}

	public static CmdLogoffRequestXMLizer getInstance() {
		return INSTANCE;
	}

	public CmdIF fromXMLString(String xmlString)
			throws CmdReconstructionException {
		// TODO maybe we should separate toXML and fromXML into different
		// classes
		throw new UnsupportedOperationException(
				"CmdLogoffRequestXMLizerString.fromXMLString");
	}

}
