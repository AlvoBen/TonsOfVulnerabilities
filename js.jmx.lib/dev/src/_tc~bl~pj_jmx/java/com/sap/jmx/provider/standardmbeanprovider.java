package com.sap.jmx.provider;

import javax.management.JMException;

public interface StandardMBeanProvider extends MBeanProvider {

	/**
	 * Get all keys that are recognized from the provider
	 * 
	 * @return	An array of all keys recognized from the provider
	 */
	public String[] getSuppliedKeys();

	/**
	 * Get all value that are recognized per a specific key
	 * 
	 * @return	An array of all values that are recognized per a specific key
	 */	
	public String[] getSuppliedValues(String key);
	
	/**
	 * Init the provider using the context that is passed as argument
	 * 
	 * @param context The context of the provider
	 * 
	 * @throws JMException If exception occurs during the initialization
	 */
	public void init(ProviderRootContext context) throws JMException;
	
	/**
	 *	This method is called during the unregistration of the provider. Whithin it all the 
	 *	MBeans that were registered into the init method should be unregistered 
	 *
	 * @throws JMException
	 */
	public void destroy() throws JMException;

}
