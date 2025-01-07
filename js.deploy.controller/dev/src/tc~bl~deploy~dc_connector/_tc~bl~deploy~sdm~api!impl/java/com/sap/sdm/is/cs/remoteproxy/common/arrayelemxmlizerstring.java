package com.sap.sdm.is.cs.remoteproxy.common;

import com.sap.sdm.is.stringxml.StringFinder;
import com.sap.sdm.is.stringxml.StringPos;
import com.sap.sdm.util.xml.XMLFilter;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
public class ArrayElemXMLizerString extends ArrayElemXMLizer {

	public static ArrayElem fromXMLString(String input) throws Exception {

		String instanceID = null;
		String ifName = null;
		String value = null;

		StringPos arrEPos = StringFinder.findXMLElem(XMLConstants.XMLARRAYELEM,
				input);

		StringPos ifPos = StringFinder.findXMLElem(XMLConstants.XMLIF, input);
		ifName = XMLFilter.checkAndFilterInput(input.substring(
				ifPos.beginPos + 4, ifPos.endPos));
		if (ifName.startsWith(DMIConstants.SERVERPACKAGEPREFIX)
				&& !ifName.startsWith(DMIConstants.CLIENTPACKAGEPREFIX)) {
			StringBuffer ifNameClient = new StringBuffer(
					DMIConstants.CLIENTPACKAGEPREFIX);
			ifNameClient.append(ifName
					.substring(DMIConstants.SERVERPACKAGEPREFIX.length()));
			ifName = ifNameClient.toString();
		}

		StringPos idPos = StringFinder
				.findOptXMLElem(XMLConstants.XMLID, input);
		if (idPos == null) {
			instanceID = null;
		} else {
			instanceID = XMLFilter.checkAndFilterInput(input.substring(
					idPos.beginPos + 4, idPos.endPos));
		}

		StringPos valPos = StringFinder.findOptXMLElem(XMLConstants.XMLVALUE,
				input);
		if (valPos == null) {
			value = null;
		} else {
			value = input.substring(valPos.beginPos + 5, valPos.endPos);
			value = XMLFilter.checkAndFilterInput(value);
		}

		StringPos hasCacheablePos = StringFinder.findOptXMLElem(
				XML_CACHEABLE_NOARG_METHODS, input);
		boolean hasCacheableNoArgMethods;
		if (hasCacheablePos != null) {
			hasCacheableNoArgMethods = new Boolean(input.substring(
					// 2 is for the < and >
					hasCacheablePos.beginPos
							+ XML_CACHEABLE_NOARG_METHODS.length() + 2,
					hasCacheablePos.endPos)).booleanValue();
		} else {
			hasCacheableNoArgMethods = false;
		}

		return CmdFactory.createArrayElem(ifName, instanceID, value,
				hasCacheableNoArgMethods);

	}
}
