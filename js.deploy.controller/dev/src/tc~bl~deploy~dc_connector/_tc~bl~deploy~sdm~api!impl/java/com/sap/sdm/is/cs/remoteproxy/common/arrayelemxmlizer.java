package com.sap.sdm.is.cs.remoteproxy.common;

import com.sap.sdm.is.stringxml.StringXMLizer;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
abstract public class ArrayElemXMLizer {
	protected final static String XML_CACHEABLE_NOARG_METHODS = "CacheableNoArgMethods";

	public static void toXMLString(ArrayElem arrElem, StringXMLizer sXML) {
		sXML.startElem(XMLConstants.XMLARRAYELEM);
		sXML.addElemWithContent(XMLConstants.XMLIF, arrElem.getInterfaceName());
		if (arrElem.getInstanceID() != null) {
			sXML
					.addElemWithContent(XMLConstants.XMLID, arrElem
							.getInstanceID());
		}
		if (arrElem.getValue() != null) {
			sXML.addElemWithContentCDATA(XMLConstants.XMLVALUE, arrElem
					.getValue());
		}

		if (arrElem.hasCacheableNoArgMethods() == true) {
			sXML.addElemWithContent(XML_CACHEABLE_NOARG_METHODS, "true");
		}

		sXML.endCurrentElem();
	}

}
