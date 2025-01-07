package com.sap.sdm.is.security.impl;

import com.sap.sdm.is.security.SecurityFactory;
import com.sap.sdm.is.security.String2SHA;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
public class SecurityFactoryImpl extends SecurityFactory {

	private final static SecurityFactory INSTANCE = new SecurityFactoryImpl();

	public static void registerToAbstractFactory() {
		SecurityFactory.setInstance(INSTANCE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.sdm.client_server.api.client.APIClientSecurityFactory#
	 * createHashedString(java.lang.String)
	 */
	public String2SHA createHashedString(String text) {
		return new String2SHAImpl(text);
	}

}
