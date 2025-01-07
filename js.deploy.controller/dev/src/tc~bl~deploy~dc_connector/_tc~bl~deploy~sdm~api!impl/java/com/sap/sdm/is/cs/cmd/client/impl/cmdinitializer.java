package com.sap.sdm.is.cs.cmd.client.impl;

import com.sap.sdm.is.cs.cmd.CmdError;
import com.sap.sdm.is.cs.cmd.CmdErrorXMLizerString;
import com.sap.sdm.is.cs.cmd.CmdNoResponseTest;
import com.sap.sdm.is.cs.cmd.CmdXMLFactory;
import com.sap.sdm.is.cs.cmd.string.CmdNoResponseTestXMLizerString;

/**
 * @author Christian Gabrisch 11.08.2003
 */
public final class CmdInitializer {
	public static void init() {
		CmdClientFactoryImpl.registerToAbstractFactory();

		CmdXMLFactory.getInstance().addXmlizer(CmdError.NAME,
				CmdErrorXMLizerString.getInstance());

		CmdXMLFactory.getInstance().addXmlizer(CmdNoResponseTest.NAME,
				CmdNoResponseTestXMLizerString.getInstance());
	}
}
