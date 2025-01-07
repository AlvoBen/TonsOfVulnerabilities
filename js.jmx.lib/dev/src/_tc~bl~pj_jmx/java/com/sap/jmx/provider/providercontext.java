/*
 * Created on Jun 30, 2004
 *
 */
package com.sap.jmx.provider;

import java.util.Hashtable;

import javax.management.JMException;
import javax.management.ObjectName;

/**
 * @author I024127
 *
 */
public interface ProviderContext extends ProviderRootContext {

	/**
	 * Get connection id
	 * 
	 * @return	connection id
	 */
	public String getConnectionId();
	
	/**
	 * Create mbean object name associated with the connection 
	 * 
	 * @param key	Key that should be part from the object name
	 * @param value	Value assigned to the key
	 * @return	Object Name associated with the connection
	 * 
	 * @throws JMException	If exception occurs
	 */
	public ObjectName createObjectName(String key, String value) throws JMException;
	
	/**
	 * Create mbean object name associated with the connection
	 * 
	 * @param nameValuePairs	Name value pairs that should be part from the object name
	 * @return	Object Name associated with the connection
	 * @throws JMException	If exception occurs
	 */
	public ObjectName createObjectName(Hashtable nameValuePairs) throws JMException;

	/**
	 * Register MBean
	 * 
	 * @param name	Name of the mbean 
	 * @param mBean	Instance of the mbean
	 * 
	 * @throws JMException	If exception occurs
	 */
	public void registerMBean(ObjectName name, Object mBean) throws JMException;
	
	/**
	 * Unregister MBean
	 * 
	 * @param name	Name of the mbean
	 * 
	 * @throws JMException	If exception occurs
	 */
	public void unregisterMBean(ObjectName name) throws JMException;
	
	/**
	 * Unregister MBean
	 * 
	 * @param name	Name of the mbean
	 * 
	 * @throws JMException	If exception occurs
	 */
	
	/**
	 * Invalidate an lazy MBean
	 * 
	 * @param mBean	Name of the mbean
	 * @param wait	Synchronous invalidation; in current version it is always synchronous and the flag is ignored
	 * 
	 * @throws JMException	If exception occurs
	 */
	public void invalidate(ObjectName mBean, boolean wait) throws JMException;
	
	/**
	 * Invalidate lazy MBeans
	 *  
	 * @param mBeans	Names of the mbeans
	 * @param wait	Synchronous invalidation; in current version it is always synchronous and the flag is ignored
	 * 
	 * @throws JMException	If exception occurs
	 */
	public void invalidate(ObjectName[] mBeans, boolean wait) throws JMException;
	
	/**
	 * Set connection timeout
	 * 
	 * @param timeout	New value of the timeout; if 0 or negative - connection has no timeout 
	 */
	public void setConnectionTimeout(long timeout);
}