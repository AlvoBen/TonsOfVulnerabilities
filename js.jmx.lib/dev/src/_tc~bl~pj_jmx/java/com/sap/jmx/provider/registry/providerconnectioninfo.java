/*
 * Created on Jul 8, 2004
 *
 */
package com.sap.jmx.provider.registry;

import com.sap.jmx.provider.MBeanServerAccess;
import com.sap.jmx.provider.ProviderConnection;
import com.sap.jmx.provider.ProviderContext;
import com.sap.jmx.provider.ProviderContextImpl;

/**
 * @author I024127
 *
 */
public class ProviderConnectionInfo extends MBeanProviderInfo {

	//the provider to which the connection is associated 
	private ProviderInfo provider;
	
	//the context of the connection
	private ProviderContext context;

	//how many times the instance of the connection is re-used
	private int usedCount;
	
	//timeout of the connection
	private long timeout;
	
	private long accessTimestamp = 0;
	
	//constructor
	public ProviderConnectionInfo(String connectionId, ProviderConnection connection, ProviderInfo provider, MBeanServerAccess server) {
		super(connection, MBeanProviderInfo.CONNECTION_PROVIDER_TYPE);
		this.provider = provider;
		context = ProviderContextImpl.createProviderContext(connectionId, server);
		usedCount = 0;
		timeout = 0;
	}
	
	/**
	 *	Increment the usedCount variable   
	 */
	public synchronized void incUsedCount() {
		usedCount++;
	}

	/**
	 * Decrement the usedCount variable
	 */
	public synchronized void decUsedCount() {
		usedCount--;
	}
	
	/**
	 * Get usedCount variable
	 * 
	 * @return how many times the instance of the connection is re-used
	 */
	public int getUsedCount() {
		return usedCount;
	}
	
	/**
	 * Get provider context associated with the connection
	 * 
	 * @return instance of the provider context associated with the connection
	 */
	public ProviderContext getProviderContext() {
		return context;
	}

    /**
     * Get provider of the connection
     * 
     * @return provider of the connection
     */
    public ProviderInfo getProvider() {
        return provider;
    }

	/**
	 * Set connection timeout
	 * 
	 * @param timeout	the new timeout
	 */
	public void setConnectionTimeout(long timeout) {
		this.timeout = timeout;	
	}
	
	public long getConnectionTimeout() {
		return timeout;
	}
	
	public void setAccessTimestamp(long timestamp) {
		accessTimestamp = timestamp;
	}
	
	public long getAccessTimestamp() {
		return accessTimestamp;
	}
}
