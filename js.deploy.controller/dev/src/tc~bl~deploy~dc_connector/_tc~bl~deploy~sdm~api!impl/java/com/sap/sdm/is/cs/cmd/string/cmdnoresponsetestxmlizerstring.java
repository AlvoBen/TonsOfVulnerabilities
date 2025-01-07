package com.sap.sdm.is.cs.cmd.string;

import com.sap.sdm.is.cs.cmd.CmdIF;
import com.sap.sdm.is.cs.cmd.CmdNoResponseTest;
import com.sap.sdm.is.cs.cmd.CmdNoResponseTestXMLizer;
import com.sap.sdm.is.cs.cmd.CmdReconstructionException;
import com.sap.sdm.is.stringxml.StringFinder;
import com.sap.sdm.util.log.Logger;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
public class CmdNoResponseTestXMLizerString extends CmdNoResponseTestXMLizer {
	private final static Logger log = Logger.getLogger();

	private final static CmdNoResponseTestXMLizer INSTANCE = new CmdNoResponseTestXMLizerString();

	private CmdNoResponseTestXMLizerString() {
	}

	public static CmdNoResponseTestXMLizer getInstance() {
		return INSTANCE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.client_server.api.xmlize.CmdNoResponseTestXMLizer#fromXMLString
	 * (java.lang.String)
	 */
	public CmdIF fromXMLString(String input) throws CmdReconstructionException {

		boolean reply = false;
		String replyS = null;

		try {

			replyS = StringFinder.findAttrForXMLElem(CmdNoResponseTest.NAME,
					input, XML_REPLY_ATTR);

			// the default needs to be true
			if (null != replyS) {
				if (replyS.equalsIgnoreCase("false"))
					reply = false;
				else
					reply = true;
			} else {
				reply = true;
			}
		} catch (Exception exc) {
			String strErrMsg = "CmdNoResponseTestXMLizerString could not reconstruct the command";
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
		return new CmdNoResponseTest(reply);
	}
}
