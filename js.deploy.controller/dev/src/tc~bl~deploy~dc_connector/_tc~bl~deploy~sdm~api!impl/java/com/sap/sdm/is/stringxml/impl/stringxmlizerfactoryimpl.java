package com.sap.sdm.is.stringxml.impl;

import com.sap.sdm.is.stringxml.StringXMLizer;
import com.sap.sdm.is.stringxml.StringXMLizerFactory;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
public class StringXMLizerFactoryImpl extends StringXMLizerFactory {

	private final static StringXMLizerFactory INSTANCE = new StringXMLizerFactoryImpl();

	public static void registerToAbstractFactory() {
		StringXMLizerFactory.setInstance(INSTANCE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.client_server.api.client.StringXMLizerFactory#createStringXMLizer
	 * (java.lang.String)
	 */
	public StringXMLizer createStringXMLizer(String rootElemName) {
		return new StringXMLizerImpl(rootElemName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.client_server.api.client.StringXMLizerFactory#createStringXMLizer
	 * (java.lang.String, java.lang.String, java.lang.String)
	 */
	public StringXMLizer createStringXMLizer(String rootElemName,
			String rootAttrName, String rootAttrValue) {
		return new StringXMLizerImpl(rootElemName, rootAttrName, rootAttrValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.client_server.api.client.StringXMLizerFactory#createStringXMLizer
	 * (java.lang.String, java.lang.String[], java.lang.String[])
	 */
	public StringXMLizer createStringXMLizer(String rootElemName,
			String[] rootAttrNames, String[] rootAttrValues) {
		return new StringXMLizerImpl(rootElemName, rootAttrNames,
				rootAttrValues);
	}

}
