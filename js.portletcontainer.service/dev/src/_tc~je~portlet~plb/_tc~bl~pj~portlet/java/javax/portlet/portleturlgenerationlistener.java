/*
 * Copyright 2006 IBM Corporation.
 *
 */
package javax.portlet;

/**
 * Portlet applications can register portlet URL listeners in order to filter URLs 
 * before they get generated.
 * In order to receive a callback from the portlet container before a 
 * portlet URL is generated the portlet application needs to implement this 
 * interface and register it in the deployment descriptor with the
 * <code>listener</code> element.
 *
 * @since 2.0
 */
public interface PortletURLGenerationListener {

	/**
	 * Callback being called by the portlet container
	 * before <code>toString</code> or <code>write</code>
	 * are executed on action URLs.
	 * 
	 * @param actionURL  action URL to be generated
	 */
	public void filterActionURL(PortletURL actionURL);

	/**
	 * Callback being called by the portlet container
	 * before <code>toString</code> or <code>write</code>
	 * are executed on render URLs.
	 * 
	 * @param renderURL  render URL to be generated
	 */
	public void filterRenderURL(PortletURL renderURL);
	
	/**
	 * Callback being called by the portlet container
	 * before <code>toString</code> or <code>write</code>
	 * are executed on resource URLs.
	 * 
	 * @param resourceURL  resource URL to be generated
	 */
	public void filterResourceURL(ResourceURL resourceURL);
	
}
