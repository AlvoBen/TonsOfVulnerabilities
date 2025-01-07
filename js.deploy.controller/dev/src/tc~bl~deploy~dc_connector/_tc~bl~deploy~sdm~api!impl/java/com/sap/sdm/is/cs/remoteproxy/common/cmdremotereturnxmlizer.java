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
public abstract class CmdRemoteReturnXMLizer implements CmdXMLizer {
	protected final static String XML_CACHEABLE_NOARG_METHS = "CacheableNoArgMethods";

	public String toXMLString(CmdIF cmd) {
		CmdRemoteReturn cmdRet = (CmdRemoteReturn) cmd;
		StringXMLizerFactory factory = StringXMLizerFactory.getInstance();
		StringXMLizer sXML = factory.createStringXMLizer(CmdRemoteReturn.NAME);

		sXML.addElemWithContent(XMLConstants.XMLIF, cmdRet.getInterfaceName());
		if (cmdRet.getInstanceID() != null) {
			sXML.addElemWithContent(XMLConstants.XMLID, cmdRet.getInstanceID());
		}
		if (cmdRet.getValue() != null) {
			sXML.addElemWithContent(XMLConstants.XMLVALUE, cmdRet.getValue());
		}
		ArrayElem[] arrElemArr = cmdRet.getArrElementArray();
		if (arrElemArr != null) {
			sXML.startElem(XMLConstants.XMLARRAY);
			for (int i = 0; i < arrElemArr.length; i++) {
				ArrayElemXMLizer.toXMLString(arrElemArr[i], sXML);
			}
			sXML.endCurrentElem();
		}

		if (cmdRet.hasCacheableNoArgMethods() == true) {
			sXML.addElemWithContent(XML_CACHEABLE_NOARG_METHS, "true");
		}

		sXML.endRootElem();
		return sXML.getString();
	}

	abstract public CmdIF fromXMLString(String input)
			throws CmdReconstructionException;

}
