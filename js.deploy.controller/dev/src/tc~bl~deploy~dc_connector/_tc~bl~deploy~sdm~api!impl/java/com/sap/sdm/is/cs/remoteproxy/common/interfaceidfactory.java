package com.sap.sdm.is.cs.remoteproxy.common;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
public class InterfaceIDFactory {

	private final static InterfaceIDFactory INSTANCE = new InterfaceIDFactory();

	public static InterfaceIDFactory getInstance() {
		return INSTANCE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.client_server.api.common.InterfaceIDFactory#createInterfaceID
	 * (java.lang.String, java.lang.String)
	 */
	public InterfaceID createInterfaceID(String className, String ID) {
		return new InterfaceID(className, ID);
	}

}
