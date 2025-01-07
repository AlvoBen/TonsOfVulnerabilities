package com.sap.jmx.provider.registry;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.management.JMException;
import javax.management.ObjectName;

import com.sap.jmx.provider.ExtendedMBeanProvider;
import com.sap.jmx.provider.MBeanServerAccess;
import com.sap.jmx.provider.Provider;
import com.sap.jmx.provider.ProviderConnection;
import com.sap.jmx.provider.ProviderContextImpl;
import com.sap.jmx.provider.ProviderException;
import com.sap.jmx.provider.StandardMBeanProvider;
import com.sap.jmx.provider.lazycache.LazyMBeanInfo;
import com.sap.jmx.provider.lazycache.LazyMBeanSystem;
import com.sap.jmx.provider.lazycache.LazyMBeanSystemImpl;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;


public class ProviderMBeanSystemImpl implements ProviderMBeanSystem {

	//---static part---
	
	//the only one instance of the provider mbean system
	private static ProviderMBeanSystem providerSystem = null;
	
	//location where to log the errors
	private static final Location LOCATION = Location.getLocation(ProviderMBeanSystemImpl.class);
	
	//factory method for getting the provider mbean system instance
	public static synchronized ProviderMBeanSystem getProviderMBeanSystem() {
		if (providerSystem == null) {
			providerSystem = new ProviderMBeanSystemImpl();
		}
		return providerSystem;
	}

	//---instance part---
	
	//all recognized keys (Standard & Extended providers only)
	private String[] keysArray;
	
	//array of hashtable(String -> MBeanProviderInfo); for each recognized key, all recognized values and associated with them providers (Standard & Extended providers only) 
	private Hashtable[] valueTable;
	
	//String -> ProviderInfo; all providers that support connections, keys are their names
	private Hashtable providers;	
	
	//String -> ProviderConnectionInfo; all connections, keys are their ids
	private Hashtable providerConnections;	
	
	//counter for the connection ids; it ensures the uniqueness of the connection ids 
	private int connectionCounter;
	
	//names under which the providers are registered (Standard & Extended providers only); it is used during the provider unregistration
	private HashSet registeredNames;
	
	//all extended providers (that support queries)
	private Hashtable extendedProviders;
	
	//constructor
	public ProviderMBeanSystemImpl() {
		keysArray = new String[0];
		valueTable = new Hashtable[0];
		
		providers = new Hashtable();
		providerConnections = new Hashtable();		
		connectionCounter = 0;
		
		registeredNames = new HashSet();
		extendedProviders = new Hashtable();
	}
	
	//----- Methods from Provider MBean System interface -----
	
	/** 
	 * @see com.sap.jmx.provider.registry.ProviderMBeanSystem#registerProvider(com.sap.jmx.provider.StandardMBeanProvider)
	 */
	public synchronized MBeanProviderInfo registerProvider(StandardMBeanProvider provider) throws ProviderException {
		return registerProvider(provider, MBeanProviderInfo.STANDARD_PROVIDER_TYPE);
	}

	/**
	 * @see com.sap.jmx.provider.registry.ProviderMBeanSystem#registerProvider(com.sap.jmx.provider.ExtendedMBeanProvider)
	 */
	public synchronized MBeanProviderInfo registerProvider(ExtendedMBeanProvider provider) throws ProviderException {
		return registerProvider(provider, MBeanProviderInfo.EXTENDED_PROVIDER_TYPE);		
	}
	
	/**
	 * @see com.sap.jmx.provider.registry.ProviderMBeanSystem#registerProvider(java.lang.String, com.sap.jmx.provider.Provider)
	 */
	public synchronized void registerProvider(String name, Provider provider) {
		if (providers.get(name) == null) {
			providers.put(name, new ProviderInfo(provider));
		} 
	}
	
	/**
	 * @see com.sap.jmx.provider.registry.ProviderMBeanSystem#unregisterProvider(java.lang.String, boolean, com.sap.jmx.provider.MBeanServerAccess)
	 */
	public synchronized void unregisterProvider(String name, boolean closeConnections, MBeanServerAccess server) throws ProviderException, JMException {
		if ((name == null) || (name.equals(""))) {
			return;
		}
		ProviderInfo providerInfo = (ProviderInfo) providers.get(name);
		if (providerInfo != null) {
			providers.remove(name);
			Collection connections = providerInfo.getConnectionInfos();
			if (connections != null) {
				Iterator iter = connections.iterator();
				while (iter.hasNext()) {
					ProviderConnectionInfo connectionInfo = (ProviderConnectionInfo) iter.next();
					closeConnection(connectionInfo, server, true, true, false);
					providerConnections.remove(connectionInfo.getProviderContext().getConnectionId());
				}
			}
		} else if (name.startsWith("Standard Provider, ") || name.startsWith("Extended Provider, ")) {
			if (registeredNames.contains(name)) {
				String internal_name = name.substring(19);
				Hashtable table = parseName(internal_name);
				Iterator iter = table.keySet().iterator();
				MBeanProviderInfo provider = null;
				while (iter.hasNext()) {
					String key = (String) iter.next();
					for (int i = 0; i < keysArray.length; i++) {
						if (key.equals(keysArray[i])) {
							HashSet values = (HashSet) table.get(key);
							Iterator valueIter = values.iterator();
							while (valueIter.hasNext()) {
								String value = (String) valueIter.next();
								provider = (MBeanProviderInfo) valueTable[i].remove(value);
							}
							break;
						}
					}
				}
				if (provider != null) {
					unregisterMBeans(provider, server);					
					StandardMBeanProvider standardProvider = (StandardMBeanProvider) provider.getMBeanProvider();
					standardProvider.destroy();
				}
				registeredNames.remove(name);
				
				if (name.startsWith("Extended Provider, ")) {
					Hashtable newExtendedProviders = new Hashtable();
					Iterator iterator = extendedProviders.keySet().iterator();
					while (iterator.hasNext()) {
						String key = (String) iterator.next();
						newExtendedProviders.put(key, extendedProviders.get(key));
					}
					newExtendedProviders.remove(name);
					extendedProviders = newExtendedProviders;
				}
			} else {
				LOCATION.warningT("Lazy MBean provider with name " + name + " doesn't exist. Unregistration failed");				
			}
		}
	}
	
	/**
	 * @see com.sap.jmx.provider.registry.ProviderMBeanSystem#createConnection(java.lang.String, java.util.Properties, com.sap.jmx.provider.MBeanServerAccess)
	 */
	public synchronized String createConnection(String providerName, Properties properties, MBeanServerAccess server) throws ProviderException, JMException {
		//search for provider
		ProviderInfo providerInfo = (ProviderInfo) providers.get(providerName);
		if (providerInfo == null) {
			return null;
		}
		//try to reuse existing connection
		ProviderConnection[] availableConnections = providerInfo.getConnections();
		ProviderConnection connection = providerInfo.getProvider().connect(properties, availableConnections);
		
		for (int i = 0; i < availableConnections.length; i++) {
			if (availableConnections.equals(connection)) {
				providerInfo.getConnectionInfo(availableConnections[i]).incUsedCount();
				return providerInfo.getConnectionInfo(connection).getProviderContext().getConnectionId(); //reuse existing one
			}
		}

		//it is new connection; register it
		ProviderConnectionInfo connectionInfo = new ProviderConnectionInfo(Integer.toString(connectionCounter++), connection, providerInfo, server);
		connectionInfo.incUsedCount();
		providerInfo.addConnection(connection, connectionInfo);
		
		providerConnections.put(connectionInfo.getProviderContext().getConnectionId(), connectionInfo);
		connection.init(connectionInfo.getProviderContext());			

		return connectionInfo.getProviderContext().getConnectionId();	
	}
	
	/**
	 * @see com.sap.jmx.provider.registry.ProviderMBeanSystem#closeConnection(java.lang.String, com.sap.jmx.provider.MBeanServerAccess, boolean)
	 */
	public synchronized void closeConnection(String connectionName, MBeanServerAccess server, boolean wait) throws ProviderException, JMException {
		closeConnection((ProviderConnectionInfo)providerConnections.get(connectionName), server, false, wait, true);		
	}

	/**
	 * @see com.sap.jmx.provider.registry.ProviderMBeanSystem#setConnectionTimeout(java.lang.String, long)
	 */
	public void setConnectionTimeout(String connectionName, long timeout) {
		ProviderConnectionInfo info = (ProviderConnectionInfo)providerConnections.get(connectionName);
		if (info != null) {
			info.setConnectionTimeout(timeout);
		}
	}
	
	/**
	 * @see com.sap.jmx.provider.registry.ProviderMBeanSystem#getProvider(javax.management.ObjectName)
	 */
	public MBeanProviderInfo getProvider(ObjectName mbeanName) {
		String value = mbeanName.getKeyProperty(ProviderConnection.CONNECTION_KEY);
		if (value != null) {
			ProviderConnectionInfo info = (ProviderConnectionInfo) providerConnections.get(value);
			return info;	
		}
		for (int i = 0; i < keysArray.length; i++) {
			value = mbeanName.getKeyProperty(keysArray[i]);
			if (value != null) {
				MBeanProviderInfo info = (MBeanProviderInfo) valueTable[i].get(value);
				return info;
			}
		}
		return null;
	}
	
	/**
	 * @see com.sap.jmx.provider.registry.ProviderMBeanSystem#addProviderSharedLock(javax.management.ObjectName)
	 */
	public MBeanProviderInfo addProviderSharedLock(ObjectName mbeanName) {
		MBeanProviderInfo provider = getProvider(mbeanName);
		if (provider != null) {
			if (provider.addSharedLock()) {
				return provider;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	/**
	 * @see com.sap.jmx.provider.registry.ProviderMBeanSystem#releaseProviderSharedLock(com.sap.jmx.provider.registry.MBeanProviderInfo)
	 */
	public void releaseProviderSharedLock(MBeanProviderInfo provider) {
		provider.releaseSharedLock();
	}
	
	public void timeoutConnections(long time, MBeanServerAccess server) {
		Iterator iter = providerConnections.values().iterator();
		Vector markedConnections = null;
		while (iter.hasNext()) {
			ProviderConnectionInfo connection = (ProviderConnectionInfo) iter.next();
			if (connection.getConnectionTimeout() > 0) {
				if ((connection.getConnectionTimeout() + connection.getAccessTimestamp()) < time) {
					//mark to close it
					if (markedConnections == null) {
						markedConnections = new Vector();
					}
					markedConnections.add(connection);
				}
			}
		}
		if (markedConnections != null) {
			for (int i = 0; i < markedConnections.size(); i++) {
				ProviderConnectionInfo connection = (ProviderConnectionInfo) markedConnections.elementAt(i);
				try {
					closeConnection(connection.getProviderContext().getConnectionId(), server, true);
				} catch(Exception e) {
					LOCATION.traceThrowableT(Severity.DEBUG, "Exception occurs during lazy close of connection", e);
					// $JL-EXC$					
				}
			}
		}
	}
	
	/**
	 * @see com.sap.jmx.provider.registry.ProviderMBeanSystem#getProviderNames()
	 */
	public synchronized String[] getProviderNames() {
		Vector tmp = new Vector();
		Iterator iter = registeredNames.iterator();
		while (iter.hasNext()) {
			tmp.add((String) iter.next());
		}
		iter = providers.keySet().iterator();
		while (iter.hasNext()) {
			tmp.add("LazyProvider " + (String) iter.next());
		}
		iter = providerConnections.keySet().iterator();
		while (iter.hasNext()) {
			tmp.add("ConnectionProvider " + (String) iter.next());
		}
		String[] result = new String[tmp.size()];
		return (String[]) tmp.toArray(result);
	}
	
	public Collection getExtendedProviders() {
		
		return extendedProviders.values();
	}
	
	public synchronized void activateExtendedProvider(String name, MBeanProviderInfo provider) {
		Hashtable newExtendedProviders = new Hashtable();
		Iterator iterator = extendedProviders.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			newExtendedProviders.put(key, extendedProviders.get(key));
		}
		newExtendedProviders.put(name, provider);
		extendedProviders = newExtendedProviders;
	}
	
	//----- Private Methods -----
	
	private MBeanProviderInfo registerProvider(StandardMBeanProvider provider, int type) throws ProviderException {
		String[] keys = provider.getSuppliedKeys();
		if ((keys == null) || (keys.length == 0)) {
			throw new ProviderException("Supplied keys are not defined for provider that is tried to be registered");
		}
		int[] keyExistanceIndex = new int[keys.length];
		for (int i = 0; i < keyExistanceIndex.length; i++) {
			keyExistanceIndex[i] = -1;
		}
		
		int addedKeys = 0;
		//check existance
		String[][] values = new String[keys.length][];
		
		for (int i = 0; i < keys.length; i++) {
			values[i] = provider.getSuppliedValues(keys[i]);
			if ((values[i] == null) || values[i].length == 0) {
				throw new ProviderException("Values are not defined for provider key " + keys[i]);
			}
			if ((keys[i] == null) || keys[i].equals("")) {
				throw new ProviderException("An empty supplied keys is defined for provider that is tried to be registered");
			}
			for (int j = 0; j < keysArray.length; j++) {
				if (keys[i].equals(keysArray[j])) {
					keyExistanceIndex[i] = j;
					break;
				}
			}
			if (keyExistanceIndex[i] == -1) {
				//key doesn't exist
				addedKeys++;
			}
			for (int j = 0; j < values[i].length; j++) {
				if ((values[i][j] == null) || (values[i][j].equals(""))) {
					throw new ProviderException("An empty value is defined for key " + keys[i]);
				}
				if (keyExistanceIndex[i] >= 0) {
					if (valueTable[keyExistanceIndex[i]].get(values[i][j]) != null) {
						throw new ProviderException("There is already defined provider for key = " + keys[i] + " and value = " + values[i][j]);
					}
				}
			}
		}
		
		//check is OK; do real registration
		String[] newKeysArray = null;
		Hashtable[] newValueTable = null;
		if (addedKeys > 0) {
			//new key should be added
			newKeysArray = new String[keysArray.length + addedKeys];
			System.arraycopy(keysArray, 0, newKeysArray, 0, keysArray.length);
			newValueTable = new Hashtable[keysArray.length + addedKeys];
			System.arraycopy(valueTable, 0, newValueTable, 0, valueTable.length);
			addedKeys = 0;
		} else {
			newKeysArray = keysArray;
			newValueTable = valueTable;
		}
		String result = "";
		MBeanProviderInfo providerInfo = new MBeanProviderInfo(provider, type);
		for (int i = 0; i < keys.length; i++) {
			if (keyExistanceIndex[i] == -1) {
				keyExistanceIndex[i] = keysArray.length + addedKeys;
				newKeysArray[keyExistanceIndex[i]] = keys[i];
				newValueTable[keyExistanceIndex[i]] = new Hashtable();
				addedKeys++;
			}
			result = result + ", {" + keys[i];  
			for (int j = 0; j < values[i].length; j++) {
				newValueTable[keyExistanceIndex[i]].put(values[i][j], providerInfo);
				result = result + ", " + values[i][j];				
			}
			result = result + "}";
		}
		
		//do replacement
		valueTable = newValueTable;
		keysArray = newKeysArray;
		
		//register provider key
		if (type == MBeanProviderInfo.STANDARD_PROVIDER_TYPE) {
			result = "Standard Provider" + result;
		} else {
			result = "Extended Provider" + result;
		}
		registeredNames.add(result);
		providerInfo.setProviderName(result);
		return providerInfo;
	}

	private Hashtable parseName(String name) throws ProviderException, JMException {
		Hashtable result = new Hashtable();
		StringTokenizer tokenizer = new StringTokenizer(name, ",");
		int state = 0;
		String key = null;
		String value = null;
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			token = token.trim();
			if (state == 0) {
				if (token.startsWith("{")) {
					key = token.substring(1);
					state = 1;
				} else {
					//wrong name
					LOCATION.warningT("Lazy MBean provider name " + name + " has wrong syntax. Parser exits in state 0. Current token is " + token + ". Unregistration failed");
					throw new ProviderException("Lazy MBean provider name " + name + " has wrong syntax. Parser exits in state 0. Current token is " + token + ". Unregistration failed");					
				}
			}
			while ((state == 1) && tokenizer.hasMoreTokens()) {
				token = tokenizer.nextToken();
				token = token.trim();
				if (token.endsWith("}")) {
					state = 2;
					value = token.substring(0, token.length() - 1);
				} else {
					value = token;
				}
				HashSet valueSet = (HashSet) result.get(key);
				if (valueSet == null) {
					valueSet = new HashSet();
					result.put(key, valueSet);
				}
				valueSet.add(value);
			}
			if (state == 2) {
				if (tokenizer.hasMoreTokens()) {
					state = 0;
				} 
			}
		}
		if (state != 2) {
			//wrong name
			LOCATION.warningT("Lazy MBean provider name " + name + " has wrong syntax. Unregistration failed");
			throw new ProviderException("Lazy MBean provider name " + name + " has wrong syntax. Unregistration failed");			
		}
		return result;
	}
		
	private synchronized void closeConnection(ProviderConnectionInfo connectionInfo, MBeanServerAccess server, boolean force, boolean wait, boolean unregisterFromProvider) {
		if (connectionInfo != null) {
			connectionInfo.decUsedCount();
			if (!force) {
				if (connectionInfo.getUsedCount() == 0) {
					ProviderConnection connection = (ProviderConnection) connectionInfo.getMBeanProvider();
					providerConnections.remove(connectionInfo.getProviderContext().getConnectionId());
					connection.disconnect();
					unregisterMBeans(connectionInfo,server);
				}
			} else {
				ProviderConnection connection = (ProviderConnection) connectionInfo.getMBeanProvider();
				providerConnections.remove(connectionInfo.getProviderContext().getConnectionId());
				connection.disconnect();				
				unregisterMBeans(connectionInfo,server);
                //remove from provider info
                if (unregisterFromProvider) {
                    connectionInfo.getProvider().removeConnection(connection);				                
                }
			}
		}
	}

	private void unregisterMBeans(MBeanProviderInfo provider, MBeanServerAccess server) {
		provider.deactivate();
		//provider.addExclusiveLock();
		LazyMBeanSystem lazySystem = LazyMBeanSystemImpl.getLazyMBeanSystem(server); 		
		LazyMBeanInfo mbeans = provider.unregisterAll();
		while (mbeans != null) {
			//unregister from the cache and from the MBean server
			lazySystem.forceExclusiveLock(mbeans.getObjectName());
			lazySystem.unregisterMBean(mbeans.getObjectName());
			try {
				server.unregisterFromMBeanServer(mbeans.getObjectName());
			} catch(JMException jmException) {
				LOCATION.traceThrowableT(Severity.WARNING, "An error occured during unregistration of lazy MBean with name " + mbeans.getObjectName().getCanonicalName(), jmException);
			}
			LazyMBeanInfo nextMbeans = mbeans.nextProviderMBean;
			mbeans.nextProviderMBean = null;
			mbeans.prevProviderMBean = null;
			lazySystem.removeExclusiveLock(mbeans.getObjectName());
			mbeans = nextMbeans;
		}
		//provider.releaseExclusiveLock();
	}
}
