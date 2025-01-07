package com.sap.jmx.provider.registry;

import java.util.Collection;
import java.util.Properties;

import javax.management.JMException;
import javax.management.ObjectName;

import com.sap.jmx.provider.ExtendedMBeanProvider;
import com.sap.jmx.provider.MBeanServerAccess;
import com.sap.jmx.provider.Provider;
import com.sap.jmx.provider.ProviderException;
import com.sap.jmx.provider.StandardMBeanProvider;

public interface ProviderMBeanSystem {

	/**
	 * Register a Standard MBean provider
	 * 
	 * @param provider	An instance of the provider
	 * @return			The name that the provider is registered within the register. Using this name it could be inregistered later 
	 * @throws ProviderException	If exception occurs during registration
	 */
	public MBeanProviderInfo registerProvider(StandardMBeanProvider provider) throws ProviderException;	
	
	/**
	 * Register an Extended MBean provider
	 * 
	 * @param provider	An instance of the provider
	 * @return			The name that the provider is registered within the register. Using this name it could be inregistered later 
	 * @throws ProviderException	If exception occurs during registration
	 */
	public MBeanProviderInfo registerProvider(ExtendedMBeanProvider provider) throws ProviderException;
	
	/**
	 * Register provider
	 * 
	 * @param name	Name of the provider
	 * @param provider	An instance of the provider 
	 */
	public void registerProvider(String name, Provider provider);
	
	/**
	 * Unregister provider
	 * 
	 * @param name	Name of the provider
	 * @param closeConnections	Flag marking close of all connections associated with the provider. It is used only for the provider that supports connections
	 * @param server	Interface to acccess some functionality of the interceptor
	 */
	public void unregisterProvider(String name, boolean closeConnections, MBeanServerAccess server) throws ProviderException, JMException;
	
	/**
	 * Create new connection for a provider that supports connections
	 * 
	 * @param providerName	Provider name
	 * @param properties	Connection properties
	 * @param server		Interface to acccess some functionality of the interceptor
	 * @return				Id of the connection
	 * @throws ProviderException	If some exception occurs during connection creation
	 * @throws JMException	If some exception occurs during connection creation
	 */
	public String createConnection(String providerName, Properties properties, MBeanServerAccess server) throws ProviderException, JMException;
	
	/**
	 * Close connection associated with a provider
	 * @param connectionName	Connection id
	 * @param server			Interface to acccess some functionality of the interceptor
	 * @param wait				Asynchronously or not; this flag is ignored into the current implementation
	 * @throws ProviderException	If some exception occurs during close of the connection
	 * @throws JMException		If some exception occurs during close of the connection
	 */
	public void closeConnection(String connectionName, MBeanServerAccess server, boolean wait) throws ProviderException, JMException;
	
	/**
	 * Set connection timeout
	 * 
	 * @param connectionName Connection id
	 * @param timeout	Timeout in millisecons
	 */
	public void setConnectionTimeout(String connectionName, long timeout);
	
	/**
	 * Get information for a registered provider associated with the mbean
	 * 
	 * @param mbeanName	The mbean name
	 * @return	Provider information
	 */
	public MBeanProviderInfo getProvider(ObjectName mbeanName);
	
	/**
	 * Add a shared lock on a provider associated with the mbean
	 * 
	 * @param mbeanName	The mbean name
	 * @return	Provider information or null if provider doesn't exists
	 */
	public MBeanProviderInfo addProviderSharedLock(ObjectName mbeanName);	
	
	/**
	 * Release a shared lock on a provider
	 * 
	 * @param provider Provider information	
	 */
	public void releaseProviderSharedLock(MBeanProviderInfo provider);
	
	public void timeoutConnections(long time, MBeanServerAccess server);
	
	public String[] getProviderNames();
	
	public Collection getExtendedProviders();
	
	public void activateExtendedProvider(String name, MBeanProviderInfo provider);
}
