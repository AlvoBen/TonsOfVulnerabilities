﻿package com.sap.sdm.is.cs.session.common;

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
public abstract class CmdLogoffAcceptedXMLizer implements CmdXMLizer {

	public String toXMLString(CmdIF cmd) {
		CmdLogoffAccepted cmdLA = (CmdLogoffAccepted) cmd;
		StringXMLizerFactory factory = StringXMLizerFactory.getInstance();
		StringXMLizer sXML = factory.createStringXMLizer(cmdLA.getMyName());

		sXML.endRootElem();
		return sXML.getString();
	}

	abstract public CmdIF fromXMLString(String xmlString)
			throws CmdReconstructionException;

}
