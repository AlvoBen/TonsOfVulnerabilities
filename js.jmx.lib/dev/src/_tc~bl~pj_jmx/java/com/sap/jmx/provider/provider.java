/*
 * Created on Jun 30, 2004
 *
 */
package com.sap.jmx.provider;

import java.util.Properties;

import javax.management.JMException;

/**
 * @author Jasen Minov
 *
 */
public interface Provider {

	/**
	 * Create new connection associated with the provider
	 * 
	 * @param properties	Properties of the connection
	 * @param availableConnections	Available connections that could be reused
	 * @return	The instance of a connection
	 * 
	 * @throws ProviderException	If exception occurs during connection creation
	 * @throws JMException	If exception occurs during connection creation
	 */
	public ProviderConnection connect(Properties properties, ProviderConnection[] availableConnections) throws ProviderException, JMException;

}
