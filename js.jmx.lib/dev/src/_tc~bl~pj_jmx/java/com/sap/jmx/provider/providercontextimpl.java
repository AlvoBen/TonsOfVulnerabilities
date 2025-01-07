package com.sap.jmx.provider;

import java.util.Hashtable;

import javax.management.JMException;
import javax.management.ObjectName;

import com.sap.jmx.provider.lazycache.LazyMBeanSystemImpl;
import com.sap.jmx.provider.registry.MBeanProviderInfo;
import com.sap.jmx.provider.registry.ProviderMBeanSystem;
import com.sap.jmx.provider.registry.ProviderMBeanSystemImpl;

public class ProviderContextImpl implements ProviderContext {

	//----- Static Part -----
	public static ProviderRootContext createProviderContext(MBeanServerAccess server) {
		return new ProviderContextImpl(null, server);
	}
	
	//factory method for creation of provider context instances
	public static ProviderContext createProviderContext(String connectionId, MBeanServerAccess server) {
		return new ProviderContextImpl(connectionId, server);
	}
	
	//----- Instance Part -----
	
	//connection id
	private String connectionId;
	
	//access to provider interceptor
	private MBeanServerAccess server;
	
	//object name context
	private ObjectNameContext objectNameContext;
	
	//----constructor----
	private ProviderContextImpl(String connectionId, MBeanServerAccess server) {
		this.connectionId = connectionId;
		this.server = server;
		objectNameContext = new ObjectNameContextImpl(server);
	}
	
	//return connection id, only for connection providers
	public String getConnectionId() {
		return connectionId;
	}

	//creates object name; if connection id is not null than is added to the object name
	public ObjectName createObjectName(String key, String value) throws JMException {
		if (connectionId != null) {
			Hashtable nameValuePairs = new Hashtable();
			nameValuePairs.put(key, value);
			nameValuePairs.put(ProviderConnection.CONNECTION_KEY, connectionId);
			return new ObjectName(server.getDefaultDomain(), nameValuePairs);
		} else {
			Hashtable nameValuePairs = new Hashtable();
			nameValuePairs.put(key, value);			
			return new ObjectName(server.getDefaultDomain(), nameValuePairs);
		}
	}

	//creates object name; if connection id is not null than is added to the object name	
	public ObjectName createObjectName(Hashtable nameValuePairs) throws JMException {
		if (connectionId != null) {
			nameValuePairs.put(ProviderConnection.CONNECTION_KEY, connectionId);
			return new ObjectName(server.getDefaultDomain(), nameValuePairs);
		} else {
			return new ObjectName(server.getDefaultDomain(), nameValuePairs);
		}
	}

	//register the MBean if it is not already registered	
	public void registerMBean(ObjectName name, Object mBean) throws JMException {
		ProviderMBeanSystem providerRegistry = ProviderMBeanSystemImpl.getProviderMBeanSystem();
		MBeanProviderInfo provider = providerRegistry.addProviderSharedLock(name);
		if (provider != null) {
			try {
				ObjectName fullName = server.getFullName(name);
				
				if (provider.getType() != MBeanProviderInfo.CONNECTION_PROVIDER_TYPE) {
					// extend name in all cases when the provider is not connection provider (for connection providers - don't change because of backward compatability 
					name = fullName;
				}
				
				LazyMBeanSystemImpl.getLazyMBeanSystem(server).registerMBean(name, fullName, mBean, provider, server, false);
			} finally {
				provider.releaseSharedLock();
			}
        } else {
            //MBean name is not associated with the connection name;
            //it is not recomended to use such MBeans
            server.registerToMBeanServer(name, mBean);
        }
	}

	/** 
	 * @see com.sap.jmx.provider.ProviderRootContext#registerMBeanOnStartup(javax.management.ObjectName, java.lang.Object)
	 */
	public void registerMBeanOnStartup(ObjectName name, Object mBean) throws JMException {
		ProviderMBeanSystem providerRegistry = ProviderMBeanSystemImpl.getProviderMBeanSystem();
		MBeanProviderInfo provider = providerRegistry.addProviderSharedLock(name);
		if (provider != null) {
			try {
				ObjectName fullName = server.getFullName(name);
				
				if (provider.getType() != MBeanProviderInfo.CONNECTION_PROVIDER_TYPE) {
					// extend name in all cases when the provider is not connection provider (for connection providers - don't change because of backward compatability 
					name = fullName;
				}
				
				LazyMBeanSystemImpl.getLazyMBeanSystem(server).registerMBean(name, fullName, mBean, provider, server, true);
			} finally {
				provider.releaseSharedLock();
			}
        } else {
            //MBean name is not associated with the connection name;
            //it is not recomended to use such MBeans
            server.registerToMBeanServer(name, mBean);
        }
	}
	
	/**
	 * @see com.sap.jmx.provider.ProviderContext#unregisterMBean(javax.management.ObjectName)
	 */
	public void unregisterMBean(ObjectName name) throws JMException {
		server.unregisterMBean(null, name);
	}

	/** 
	 * @see com.sap.jmx.provider.ProviderContext#invalidate(javax.management.ObjectName, boolean)
	 */
	public void invalidate(ObjectName mBean, boolean wait) throws JMException {
		invalidate(mBean, wait, true);		
	}

	/**
	 * @see com.sap.jmx.provider.ProviderContext#invalidate(javax.management.ObjectName[], boolean)
	 */
	public void invalidate(ObjectName[] mBeans, boolean wait) throws JMException {
		if (mBeans != null) {
			for(int i = 0; i < mBeans.length; i++) {
				invalidate(mBeans[i], wait);
			}
		}
	}

	/**
	 * @see com.sap.jmx.provider.ProviderContext#setConnectionTimeout(long)
	 */
	public void setConnectionTimeout(long timeout) {
		ProviderMBeanSystemImpl.getProviderMBeanSystem().setConnectionTimeout(connectionId, timeout);
	}

	/**
	 * @see com.sap.jmx.provider.ProviderRootContext#invalidate(javax.management.ObjectName)
	 */
	public void invalidate(ObjectName mBean) throws JMException {
		if (connectionId == null) {
			//invalidate only from the cache when standard or extended provider			
			invalidate(mBean, false, false);
		} else {
			invalidate(mBean, false, true);
		}
	}

	/**
	 * @see com.sap.jmx.provider.ProviderRootContext#invalidate(javax.management.ObjectName[])
	 */
	public void invalidate(ObjectName[] mBeans) throws JMException {
		if (mBeans != null) {
			for(int i = 0; i < mBeans.length; i++) {
				invalidate(mBeans[i]);
			}
		}
	}

	/**
	 * @see com.sap.jmx.provider.ProviderRootContext#refreshConnected(javax.management.ObjectName)
	 */
	public void refreshConnected(ObjectName mBean) throws JMException {
		LazyMBeanSystemImpl.getLazyMBeanSystem(server).refreshConnected(mBean);		
	}

	/**
	 * @see com.sap.jmx.provider.ProviderRootContext#getMBean(javax.management.ObjectName, boolean)
	 */
	public Object getMBean(ObjectName name, boolean instantiate) throws JMException {
		ProviderMBeanSystem providerRegistry = ProviderMBeanSystemImpl.getProviderMBeanSystem();		
		MBeanProviderInfo provider = providerRegistry.addProviderSharedLock(name);
		if (provider != null) {
			try {
				ObjectName fullName = server.getFullName(name);
				
				if (provider.getType() != MBeanProviderInfo.CONNECTION_PROVIDER_TYPE) {
					// extend name in all cases when the provider is not connection provider (for connection providers - don't change because of backward compatability 
					name = fullName;
				}
				
				return LazyMBeanSystemImpl.getLazyMBeanSystem(server).getMBean(name, fullName, provider, server, instantiate);
			} finally {
				provider.releaseSharedLock();
			}
		} else {
			return null;
		}
	}
	
	private void invalidate(ObjectName mBean, boolean wait, boolean unregister) throws JMException {
		if (unregister) {
			server.unregisterMBean(null, mBean);			
		} else {
			server.invalidateMBean(mBean, wait);			
		}
	}
	
	public ObjectNameContext getObjectNameContext() {
		return objectNameContext;
	}
}
