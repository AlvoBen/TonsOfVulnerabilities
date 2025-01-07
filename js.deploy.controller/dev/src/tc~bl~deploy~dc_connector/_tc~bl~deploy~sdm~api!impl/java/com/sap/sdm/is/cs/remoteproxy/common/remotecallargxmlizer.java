package com.sap.sdm.is.cs.remoteproxy.common;

import com.sap.sdm.is.stringxml.StringXMLizer;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
public class RemoteCallArgXMLizer {

	public static void toXMLString(RemoteCallArg arg, StringXMLizer sXML) {
		sXML.startElem(XMLConstants.XMLARG);
		if (arg.isNull()) {
			sXML.addElemWithContent(XMLConstants.XMLVALUE, null);
		} else {
			if (!arg.isArray()) {
				// argument is no array
				String argClassName = arg.getInterfaceName();
				if ((argClassName.equals(ClassNames.BOOLEANCLASSNAME))
						|| (argClassName.equals(ClassNames.BYTECLASSNAME))
						|| (argClassName.equals(ClassNames.DOUBLECLASSNAME))
						|| (argClassName.equals(ClassNames.FLOATCLASSNAME))
						|| (argClassName.equals(ClassNames.INTEGERCLASSNAME))
						|| (argClassName.equals(ClassNames.LONGCLASSNAME))
						|| (argClassName.equals(ClassNames.SHORTCLASSNAME))
						|| (argClassName.equals(ClassNames.FILECLASSNAME))) {
					sXML.addElemWithContent(XMLConstants.XMLIF, argClassName);
					sXML.addElemWithContent(XMLConstants.XMLVALUE, arg
							.getValue());
				} else if (argClassName.equals(ClassNames.STRINGCLASSNAME)) {
					sXML.addElemWithContent(XMLConstants.XMLIF, argClassName);
					sXML.addElemWithContentCDATA(XMLConstants.XMLVALUE, arg
							.getValue());
				} else {
					sXML.addElemWithContent(XMLConstants.XMLIF, argClassName);
					sXML.addElemWithContent(XMLConstants.XMLID, arg
							.getInstanceID());
				}
			} else {
				sXML.addElemWithContent(XMLConstants.XMLIF, arg
						.getInterfaceName());
				sXML.startElem(XMLConstants.XMLARRAY);
				ArrayElem[] arrElemArray = arg.getArrElemArr();
				for (int i = 0; i < arrElemArray.length; i++) {
					ArrayElemXMLizer.toXMLString(arrElemArray[i], sXML);
				}
				sXML.endCurrentElem();
			}
		}
		sXML.endCurrentElem();
	}
}
