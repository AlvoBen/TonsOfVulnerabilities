package com.sap.engine.services.portletcontainer.api;

/**
 * The <code>ResourceURL</code> allows portlet consumer application to retrieve cache level  
 * of this ResourceURL.
 * 
 * @author Nikolai Dokovski
 * @version 7.12
 * @since 2.0
 */
public interface ResourceURL extends PortletConsumerURL{
	/**
	 * This field is a String representation of Resource URL
	 */
	public final String RESOURCE_TYPE = "RESOURCE";
	
	/**
	 * Returns the cache level of this resource URL.
	 * Possible return values are: FULL, PORTLET or PAGE. 
	 * @return the cache level of this resource URL.
	 */
	public String getCacheability();
	
	/**
	 * Returns the resource ID set on the ResourceURL or null  if no resource ID was set on the URL.
	 * @return the resource ID set on the ResourceURL,or null  if no resource ID was set on the URL.
	 */
	public String getResourceID();
	
}
