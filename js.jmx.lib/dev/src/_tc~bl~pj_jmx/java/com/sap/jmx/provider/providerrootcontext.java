package com.sap.jmx.provider;

import javax.management.JMException;
import javax.management.ObjectName;

public interface ProviderRootContext {

	/**
	 * Invalidate a lazy mbean
	 * 
	 * @param mBean	The object name of the MBean
	 * 
	 * @throws JMException	If exception occurs
	 */
	public void invalidate(ObjectName mBean) throws JMException;
	
	/**
	 * Invalidate lazy mbeans
	 * 
	 * @param mBeans	Array containing object names of the lazy MBeans
	 *  
	 * @throws JMException If exception occurs
	 */
	public void invalidate(ObjectName[] mBeans) throws JMException;
	
	/**
	 * Refresh connected mbeans with a lazy mbean
	 * 
	 * @param mBean	the name of the lazy mbean
	 * 
	 * @throws JMException If exception occurs
	 */
	public void refreshConnected(ObjectName mBean) throws JMException;	
	
	/**
	 * 	Register MBean that will be available all the time and will not removed from the lazy load cache.
	 * 
	 * @param name	Name of the MBean
	 * @param mBean	The MBean instance
	 * @throws JMException	The exception occured during registration
	 */
	public void registerMBeanOnStartup(ObjectName name, Object mBean) throws JMException;
	
	/**
	 * Get instance of the lazy MBean
	 * 
	 * @param name	Object name of the mbean
	 * @param instantiate	Instantiate if is missing into the cache
	 * @return	The instance of the lazy MBean or null
	 * @throws JMException If exception occurs
	 */
	public Object getMBean(ObjectName name, boolean instantiate) throws JMException;
	
	/**
	 * Get the ObjectNameContext instance
	 * 
	 * @return The instance ObjectNameContext
	 */
	public ObjectNameContext getObjectNameContext();
}