package com.sap.sdm.is.cs.session.client;

import com.sap.sdm.is.cs.cmd.CmdXMLFactory;
import com.sap.sdm.is.cs.session.common.CmdCloseConnection;
import com.sap.sdm.is.cs.session.common.CmdCloseSession;
import com.sap.sdm.is.cs.session.common.CmdCloseSessionAccepted;
import com.sap.sdm.is.cs.session.common.CmdLoginAccepted;
import com.sap.sdm.is.cs.session.common.CmdLoginAcceptedXMLizerString;
import com.sap.sdm.is.cs.session.common.CmdLoginRequest;
import com.sap.sdm.is.cs.session.common.CmdLoginRequestXMLizerString;
import com.sap.sdm.is.cs.session.common.CmdLogoffAccepted;
import com.sap.sdm.is.cs.session.common.CmdLogoffAcceptedXMLizerString;
import com.sap.sdm.is.cs.session.common.CmdLogoffRequest;
import com.sap.sdm.is.cs.session.common.CmdLogoffRequestXMLizerString;
import com.sap.sdm.is.cs.session.common.CmdReopenConnection;
import com.sap.sdm.is.cs.session.common.CmdReopenConnectionAccepted;
import com.sap.sdm.is.cs.session.common.CmdSessionXMLizerString;

/**
 * @author Christian Gabrisch 11.08.2003
 */
public final class SessionInitializer {
	public static void init() {
		CmdXMLFactory.getInstance().addXmlizer(CmdCloseConnection.NAME,
				CmdSessionXMLizerString.getInstance());
		CmdXMLFactory.getInstance().addXmlizer(CmdCloseSessionAccepted.NAME,
				CmdSessionXMLizerString.getInstance());
		CmdXMLFactory.getInstance().addXmlizer(CmdCloseSession.NAME,
				CmdSessionXMLizerString.getInstance());
		CmdXMLFactory.getInstance().addXmlizer(CmdLoginAccepted.NAME,
				CmdLoginAcceptedXMLizerString.getInstance());
		CmdXMLFactory.getInstance().addXmlizer(CmdLoginRequest.NAME,
				CmdLoginRequestXMLizerString.getInstance());
		CmdXMLFactory.getInstance().addXmlizer(
				CmdReopenConnectionAccepted.NAME,
				CmdSessionXMLizerString.getInstance());
		CmdXMLFactory.getInstance().addXmlizer(CmdReopenConnection.NAME,
				CmdSessionXMLizerString.getInstance());
		CmdXMLFactory.getInstance().addXmlizer(CmdLogoffAccepted.NAME,
				CmdLogoffAcceptedXMLizerString.getInstance());
		CmdXMLFactory.getInstance().addXmlizer(CmdLogoffRequest.NAME,
				CmdLogoffRequestXMLizerString.getInstance());

	}
}
