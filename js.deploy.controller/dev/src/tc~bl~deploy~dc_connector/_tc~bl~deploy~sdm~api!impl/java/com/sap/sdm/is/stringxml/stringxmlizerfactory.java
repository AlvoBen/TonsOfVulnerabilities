package com.sap.sdm.is.stringxml;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
abstract public class StringXMLizerFactory {

	private static StringXMLizerFactory instance = null;

	public static void setInstance(StringXMLizerFactory instance) {
		StringXMLizerFactory.instance = instance;

		return;
	}

	public static StringXMLizerFactory getInstance() {
		return instance;
	}

	abstract public StringXMLizer createStringXMLizer(String rootElemName);

	abstract public StringXMLizer createStringXMLizer(String rootElemName,
			String rootAttrName, String rootAttrValue);

	abstract public StringXMLizer createStringXMLizer(String rootElemName,
			String[] rootAttrNames, String[] rootAttrValues);

}
