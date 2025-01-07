package com.sap.sdm.is.cs.remoteproxy.common;

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
public abstract class CmdRemoteCallXMLizer implements CmdXMLizer {
	protected final static String XML_SESSION_ID = "SessionID";
	protected final static String XML_DEL_LIST = "DelList";
	protected final static String XML_DEL_ITEM = "DelI";

	public String toXMLString(CmdIF cmd) {
		CmdRemoteCall cmdRC = (CmdRemoteCall) cmd;
		StringXMLizerFactory factory = StringXMLizerFactory.getInstance();
		StringXMLizer sXML = factory.createStringXMLizer(CmdRemoteCall.NAME);
		sXML.addElemWithContent(XML_SESSION_ID, cmdRC.getSessionID());
		InterfaceID[] delArr = cmdRC.getDelArr();
		if ((delArr != null) && (delArr.length > 0)) {
			sXML.startElem(XML_DEL_LIST);
			for (int i = 0; i < delArr.length; i++) {
				sXML.startElem(XML_DEL_ITEM);
				sXML.addElemWithContent(XMLConstants.XMLIF, delArr[i]
						.getClientClassName());
				if (delArr[i].getInstanceID() != null) {
					sXML.addElemWithContent(XMLConstants.XMLID, delArr[i]
							.getInstanceID());
				}
				sXML.endCurrentElem();
			}
			sXML.endCurrentElem();
		}
		sXML.addElemWithContent(XMLConstants.XMLIF, cmdRC.getInterfaceName());
		sXML.startElem(XMLConstants.XMLMETHOD);
		sXML.addElemWithContent(XMLConstants.XMLMETHODNAME, cmdRC
				.getMethodName());
		sXML.startElem(XMLConstants.XMLSIGNATURE);
		String[] sigClassNames = cmdRC.getSignatureClassNames();
		if (sigClassNames != null) {
			for (int i = 0; i < sigClassNames.length; i++) {
				sXML.addElemWithContent(XMLConstants.XMLIF, sigClassNames[i]);
			}
		}
		sXML.endCurrentElem();
		sXML.endCurrentElem();
		if (cmdRC.getInstanceID() != null) {
			sXML.addElemWithContent(XMLConstants.XMLID, cmdRC.getInstanceID());
		}
		RemoteCallArg[] callArgs = cmdRC.getCallArgs();
		if ((callArgs != null) && (callArgs.length > 0)) {
			sXML.startElem(XMLConstants.XMLARGS);
			for (int i = 0; i < callArgs.length; i++) {
				RemoteCallArgXMLizer.toXMLString(callArgs[i], sXML);
			}
			sXML.endCurrentElem();
		}

		sXML.endRootElem();
		return sXML.getString();
	}

	abstract public CmdIF fromXMLString(String input)
			throws CmdReconstructionException;

}
