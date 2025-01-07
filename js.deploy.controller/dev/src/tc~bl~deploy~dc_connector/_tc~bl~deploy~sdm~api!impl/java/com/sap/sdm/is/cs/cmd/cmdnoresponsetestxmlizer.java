package com.sap.sdm.is.cs.cmd;

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
public abstract class CmdNoResponseTestXMLizer implements CmdXMLizer {
	protected static final String XML_REPLY_ATTR = "reply";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.is.cs.cmd.CmdXMLizerNew#toXMLString(com.sap.sdm.is.cs.cmd
	 * .CmdIFNew)
	 */
	public String toXMLString(CmdIF cmd) {
		CmdNoResponseTest cmdNoResponseTest = (CmdNoResponseTest) cmd;
		StringXMLizerFactory factory = StringXMLizerFactory.getInstance();
		StringXMLizer sXML = null;
		String[] attrs = new String[] { XML_REPLY_ATTR };
		String[] values = new String[] { new Boolean(cmdNoResponseTest.reply())
				.toString() };
		sXML = factory.createStringXMLizer(CmdNoResponseTest.NAME, attrs,
				values);
		sXML.endRootElem();

		return sXML.getString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.is.cs.cmd.CmdXMLizerNew#fromXMLString(java.lang.String)
	 */
	abstract public CmdIF fromXMLString(String xmlString)
			throws CmdReconstructionException;;

}
