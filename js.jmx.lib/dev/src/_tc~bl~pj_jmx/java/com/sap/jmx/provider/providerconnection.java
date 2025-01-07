/*
 * Created on Jun 22, 2004
 *
 */
package com.sap.jmx.provider;

import javax.management.JMException;

/**
 * @author I024127
 *
 */
public interface ProviderConnection extends MBeanProvider {
	
	/**
	 * The key that is added to the object names of the mbeans associated with the connection 
	 */
	public static final String CONNECTION_KEY = "ext_conn";
	
	/**
	 * Init the provider connection
	 * 
	 * @param context	Provider Context associated with this connection
	 *  
	 * @throws JMException	If exceptiuon occurs during initialization 
	 */
	public void init(ProviderContext context) throws JMException;
	
	/**
	 * Check availability of the connection
	 * 
	 * @return	is connection available
	 */
	public boolean isAlive();
	
	/**
	 * Disconnect the connection
	 */
	public void disconnect();	
}