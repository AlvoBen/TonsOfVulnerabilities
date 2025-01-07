/*
 * Created on Jul 8, 2004
 *
 */
package com.sap.jmx.provider.registry;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import com.sap.jmx.provider.Provider;
import com.sap.jmx.provider.ProviderConnection;

/**
 * @author I024127
 *
 */
public class ProviderInfo {
	
	//the instance of the provider
	private Provider provider;
	
	//ProviderConnection -> ProviderConnectionInfo; a hashtable with all connection associated with the provider 
	private Hashtable connections;
	
	//constructor
	public ProviderInfo(Provider provider) {
		this.provider = provider;
		connections = new Hashtable();	
	}
	
	//----- Public methods -----
	
	/**
	 * Get the provider instance
	 * 
	 * @return provider instance
	 */
	public Provider getProvider() {
		return provider;
	}
	
	/**
	 * Get all connections associated with the provider
	 * 
	 * @return array of connections
	 */
	public ProviderConnection[] getConnections() {
		ProviderConnection[] result = new ProviderConnection[connections.size()];
		Iterator i = connections.keySet().iterator();
		int off = 0;
		while (i.hasNext()) {
			result[off++] = (ProviderConnection) i.next();
		}
		return result;
	}
	
	/**
	 * Get connection info for a specific connection associated with the provider
	 * 
	 * @param connection	the instance of the connection
	 * @return		the infor for the connection
	 */
	public ProviderConnectionInfo getConnectionInfo(ProviderConnection connection) {
		return (ProviderConnectionInfo) connections.get(connection);
	}
	
	/**
	 * Add connection to the provider info
	 * 
	 * @param connection	instance of the connection
	 * @param connectionInfo	information for the connection
	 */
	public void addConnection(ProviderConnection connection, ProviderConnectionInfo connectionInfo) {
		connections.put(connection, connectionInfo);
	}
	
	/**
	 * Get collection containing all connections associated the provider
	 *  
	 * @return	collection with element of ProviderConnectionInfo type 
	 */
	public Collection getConnectionInfos() {
		return connections.values();
	}	
	
    /**
     * Removes connection from the provider info
     * 
     * @param connection	The connection instance 
     */
    public void removeConnection(ProviderConnection connection) {
        connections.remove(connection);
    }
}
