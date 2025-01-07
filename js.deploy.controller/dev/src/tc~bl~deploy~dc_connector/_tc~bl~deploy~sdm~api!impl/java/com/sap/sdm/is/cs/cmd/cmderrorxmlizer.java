package com.sap.sdm.is.cs.cmd;

import com.sap.sdm.is.stringxml.StringXMLizer;
import com.sap.sdm.is.stringxml.StringXMLizerFactory;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
public abstract class CmdErrorXMLizer implements CmdXMLizer {
	protected static final String XML_ERROR_EXC = "Ex";
	protected static final String XML_ERROR_TEXT = "Er";
	protected static final String XML_ERROR_NAME_ATTR = "n";
	protected static final String XML_ERROR_WP_ATTR = "wP";

	public String toXMLString(CmdIF cmd) {
		CmdError cmdErr = (CmdError) cmd;
		StringXMLizerFactory factory = StringXMLizerFactory.getInstance();
		StringXMLizer sXML = null;
		if (cmdErr.isWrongPasswordSupplied()) {
			sXML = factory.createStringXMLizer(CmdError.NAME,
					XML_ERROR_WP_ATTR, new Boolean(cmdErr
							.isWrongPasswordSupplied()).toString());
		} else {
			sXML = factory.createStringXMLizer(CmdError.NAME);
		}
		Exception exc = cmdErr.getException();
		if (exc != null) {
			sXML.addElemWithAttrAndContent(XML_ERROR_EXC, XML_ERROR_NAME_ATTR,
					exc.getClass().getName(), exc.getMessage());
		}

		if (cmdErr.getErrorText() != null) {
			sXML.addElemWithContent(XML_ERROR_TEXT, cmdErr.getErrorText());
		}

		sXML.endRootElem();
		return sXML.getString();
	}

	abstract public CmdIF fromXMLString(String input)
			throws CmdReconstructionException;
}
