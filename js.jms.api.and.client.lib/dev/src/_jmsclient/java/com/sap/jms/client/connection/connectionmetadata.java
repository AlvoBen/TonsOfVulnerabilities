/**
 * Connection.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.connection;

import java.util.Enumeration;
import java.util.Vector;

import javax.jms.JMSException;


public class ConnectionMetaData implements javax.jms.ConnectionMetaData {
	
	// TODO Should this data be kept in a properties file ?
	
	private static final int JMS_MAJOR_VERSION = 1;
	private static final int JMS_MINOR_VERSION = 1;
	private static final String JMS_VERSION = JMS_MINOR_VERSION + "." + JMS_MINOR_VERSION;

	private static final int PROVIDER_MAJOR_VERSION = 1;
	private static final int PROVIDER_MINOR_VERSION = 0;
	private static final String PROVIDER_VERSION = "Version " + PROVIDER_MINOR_VERSION + "." + PROVIDER_MINOR_VERSION;


	public int getJMSMajorVersion() throws JMSException {
		return JMS_MAJOR_VERSION;
	}

	public int getJMSMinorVersion() throws JMSException {
		return JMS_MINOR_VERSION;
	}

	public String getJMSVersion() throws JMSException {
		return JMS_VERSION;
	}

	public String getJMSProviderName() throws JMSException {
		return "SAP AG";
	}

	public String getProviderVersion() throws JMSException {
		return PROVIDER_VERSION;
	}                     

	public int getProviderMajorVersion() throws JMSException {
		return PROVIDER_MAJOR_VERSION;
	}

	public int getProviderMinorVersion() throws JMSException {
		return PROVIDER_MINOR_VERSION;
	}


	public Enumeration<String> getJMSXPropertyNames() throws JMSException {
		
		Vector<String> jmxProperties = new Vector<String>();
		jmxProperties.add("JMSXGroupID");
		jmxProperties.add("JMSXGroupSeq");

		return jmxProperties.elements();
	}
}

