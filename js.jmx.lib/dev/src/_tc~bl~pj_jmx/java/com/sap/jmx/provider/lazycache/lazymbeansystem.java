package com.sap.jmx.provider.lazycache;

import javax.management.JMException;
import javax.management.ObjectName;

import com.sap.jmx.provider.MBeanServerAccess;
import com.sap.jmx.provider.registry.MBeanProviderInfo;

/**
 * @author i024127
 *
 */
public interface LazyMBeanSystem {
	
	/**
	 * Method tries to put a shared lock on the MBean. If MBean is missing than method will try to register it using the
	 * provider info. If don't succeed than it will return false, otherwise true is returned. 
	 * 
	 * @param name		The object name of the MBean 
	 *		  provider  The MBean provider 	 
	 * @return			True if succeed, false otherwise
	 */
	public boolean lockMBean(ObjectName name, ObjectName fullName, MBeanProviderInfo provider, MBeanServerAccess server, boolean lazy);
	
	public void unlockMBean(ObjectName fullName);	
	
	public void addExclusiveLock(ObjectName name);
	
	public boolean forceExclusiveLock(ObjectName name);
	
	public void removeExclusiveLock(ObjectName name);
	
	
	/**
	 * Check if the MBean registered and if no than register it
	 * 
	 * @param name		Name of the MBean
	 * @param mBean		The instance of MBean
	 * @param server	Connection to MBeanServer interceptor chain
	 * @return			true if this instance of the MBean is registered
	 */
	public boolean registerMBean(ObjectName name, ObjectName fullName, Object mBean, MBeanProviderInfo provider, MBeanServerAccess server, boolean excludeFromLazyGC) throws JMException;	
	/**
	 * Mark MBean that is no more regsitered into the MBean server. 
	 * This operation should be used only when an exclusive lock is set for the MBean.
	 * 
	 * @param name
	 * @return		true if the MBean is registered into the MBean server
	 */
	public boolean unregisterMBean(ObjectName name);
	
	public void refreshConnected(ObjectName name) throws JMException;
	
	public Object getMBean(ObjectName name, ObjectName fullName, MBeanProviderInfo provider, MBeanServerAccess server, boolean instantiate) throws JMException;
	
	public int getAllCalls();
	
	public int getCacheSize();
	
	public int getCacheHits();
	
	public String getCacheDump();
	
	public void timeoutMBeans(long time, MBeanServerAccess server);
	
	public void reduceCacheSize(int targetSize, MBeanServerAccess server);	
}
