package com.sap.jmx.provider;

import javax.management.JMException;
import javax.management.ObjectName;

public interface MBeanProvider {

	/**
	 * Instantiates mbean with given object name. If not possible returns null
	 * @param name	Name of the mbean
	 * @return	MBean instance
	 */
	public Object instantiateMBean(ObjectName name);
	
	/**
	 * Get connected mbeans to a given mbean; the lazy system will try to load them in advance
	 * 
	 * @param name	Name of the mbean
	 * @param mBean	Instance of the mbean
	 * @return	Array of object names of mbeans connected to the mbean
	 * 
	 * @throws ProviderException	If error occurs
	 * @throws JMException	If error occurs
	 */
	public ObjectName[] getConnected(ObjectName name, Object mBean) throws ProviderException, JMException;

}
