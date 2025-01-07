package com.sap.sdm.is.cs.filetransfer.common;

import com.sap.sdm.is.cs.cmd.CmdIF;
import com.sap.sdm.is.cs.cmd.CmdReconstructionException;
import com.sap.sdm.is.cs.cmd.CmdXMLizer;
import com.sap.sdm.is.stringxml.StringXMLizer;
import com.sap.sdm.is.stringxml.StringXMLizerFactory;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Thomas Brodkorb
 * @version 1.0
 */
public abstract class CmdFileOKXMLizer implements CmdXMLizer {
	protected static final String XML_FILENAME_ATTR = "name";

	public String toXMLString(CmdIF cmd) {

		StringXMLizerFactory factory = StringXMLizerFactory.getInstance();
		StringXMLizer sXML = null;
		sXML = factory.createStringXMLizer(CmdFileOK.NAME);
		sXML.endRootElem();
		return sXML.getString();
	}

	abstract public CmdIF fromXMLString(String input)
			throws CmdReconstructionException;
}
