package com.sap.sdm.is.cs.remoteproxy.common;

import com.sap.sdm.is.cs.cmd.CmdIF;
import com.sap.sdm.is.cs.cmd.CmdReconstructionException;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
public class CmdRemoteCallXMLizerString extends CmdRemoteCallXMLizer {

	private final static CmdRemoteCallXMLizer INSTANCE = new CmdRemoteCallXMLizerString();

	private CmdRemoteCallXMLizerString() {
	}

	public static CmdRemoteCallXMLizer getInstance() {
		return INSTANCE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.client_server.api.xmlize.CmdRemoteCallXMLizer#fromXMLString
	 * (java.lang.String)
	 */
	public CmdIF fromXMLString(String input) throws CmdReconstructionException {
		// TODO maybe we should separate toXML and fromXML into different
		// classes
		throw new UnsupportedOperationException(
				"CmdRemoteCallXMLizerString.fromXMLString");
	}

}
