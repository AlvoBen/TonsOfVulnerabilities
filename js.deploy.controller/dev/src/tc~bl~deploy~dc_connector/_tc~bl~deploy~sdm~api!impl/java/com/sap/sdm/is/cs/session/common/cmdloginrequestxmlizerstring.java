package com.sap.sdm.is.cs.session.common;

import com.sap.sdm.is.cs.cmd.CmdIF;
import com.sap.sdm.is.cs.cmd.CmdReconstructionException;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
public class CmdLoginRequestXMLizerString extends CmdLoginRequestXMLizer {

	private final static CmdLoginRequestXMLizer INSTANCE = new CmdLoginRequestXMLizerString();

	private CmdLoginRequestXMLizerString() {
	}

	public static CmdLoginRequestXMLizer getInstance() {
		return INSTANCE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.is.cs.cmd.CmdXMLizerNew#fromXMLString(java.lang.String)
	 */
	public CmdIF fromXMLString(String xmlString)
			throws CmdReconstructionException {
		// TODO maybe we should separate toXML and fromXML into different
		// classes
		throw new UnsupportedOperationException(
				"CmdLoginRequestXMLizerString.fromXMLString");
	}

}
