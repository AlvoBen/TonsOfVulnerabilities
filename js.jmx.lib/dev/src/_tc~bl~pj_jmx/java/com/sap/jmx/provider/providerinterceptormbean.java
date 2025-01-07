/*
 * Created on Jun 24, 2004
 *
 */
package com.sap.jmx.provider;

import java.util.Properties;

import javax.management.JMException;

/**
 * @author Jasen Minov
 *
 */
public interface ProviderInterceptorMBean {
	
	/**
	 * Register a provider that supports connections
	 * 
	 * @param name	Name of the provider
	 * @param provider	Instance of the provider
	 */
	public void registerProvider(String name, Provider provider);
	
	/**
	 * Register Standard MBean Provider
	 * 
	 * @param provider	Instance of the provider
	 * @return	Name with which the provider is registered
	 * 
	 * @throws ProviderException	If exception occurs during registration
	 * @throws JMException	If exception occurs during registration
	 */
	public String registerStandardProvider(StandardMBeanProvider provider) throws ProviderException, JMException;
	
	/**
	 * Register Extended MBean Provider
	 * 
	 * @param provider	Instance of the provider
	 * @return	Name with which the provider is registered
	 * 
	 * @throws ProviderException	If exception occurs during registration
	 * @throws JMException	If exception occurs during registration
	 */
	public String registerExtendedProvider(ExtendedMBeanProvider provider) throws ProviderException, JMException;
	
	/**
	 * Unregister provider by name
	 * 
	 * @param name	Name of the provider
	 */
	public void unregisterProvider(String name) throws ProviderException, JMException;
	
	/**
	 * Unregister provider by name
	 * 
	 * @param name	Name of the provider
	 * @param closeConnections	Close all connection associated with the provider during the unregistration
	 */
	public void unregisterProvider(String name, boolean closeConnections) throws ProviderException, JMException;
	
	/**
	 * Create connection associated to a provider that supports connections
	 *  
	 * @param providerName	Provider name
	 * @param properties	Properties used to instantiated the connection
	 * @return	Id of the connection
	 * 
	 * @throws ProviderException	If exception occurs during connection creation
	 * @throws JMException	If exception occurs during connection creation
	 */
	public String createConnection(String providerName, Properties properties) throws ProviderException, JMException;
	
	/**
	 * Close connection
	 * 
	 * @param connectionName Name of the connection
	 * 
	 * @throws ProviderException	If exception occurs during connection close
	 * @throws JMException	If exception occurs during connection close
	 */
	public void closeConnection(String connectionName) throws ProviderException, JMException;
	
	/**
	 * Close connection
	 * 
	 * @param connectionName Name of the connection
	 * @param wait	The flag that allows asynchnous unregistration of the mbeans; it is ignored into the current implementation
	 * 
	 * @throws ProviderException	If exception occurs during connection close
	 * @throws JMException	If exception occurs during connection close
	 */
	public void closeConnection(String connectionName, boolean wait) throws ProviderException, JMException;	
	
	/**
	 * Get type of the interceptor
	 * 
	 * @return	ProviderInterceptor as a type
	 */
	public String getType();
		
	/**
	 * The number of all calls to lazy mbeans
	 *  
	 * @return	The number of all calls to lazy mbeans
	 */
	public int allCalls();
	
	/**
	 * The size of the cache 
	 * 
	 * @return	How many lazy mbeans are loaded at the moment
	 */
	public int cacheSize();
	
	/**
	 * The number of all calls that found a lazy mbean into the cache
	 * 
	 * @return The number of all calls that found a lazy mbean into the cache
	 */
	public int cacheHits();
	
	/**
	 * If log severity of the ProviderInterceptor is set to DEBUG, than dump the whole cache content into the default trace file.
	 * 
	 * @throws Exception	If exception occurs
	 */
	public void dumpCache() throws Exception;
	
	public String[] getProviderNames();
}
