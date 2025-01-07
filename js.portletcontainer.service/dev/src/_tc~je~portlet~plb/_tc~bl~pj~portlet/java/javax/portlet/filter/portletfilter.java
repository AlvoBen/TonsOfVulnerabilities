/*
 * Copyright 2006 IBM Corporation.
 *
 */
package javax.portlet.filter;

import javax.portlet.PortletException;

/**
 * The <code>PortletFilter</code> is the base interface for all portlet filters.
 * It provides the lifecycle methods <code>init</code> and <code>destroy</code>
 * for putting a portlet filter into and out of service.
 *
 * @since 2.0
 */
public interface PortletFilter {

    /**
     * Called by the portlet container to indicate to a filter
     * that it is being placed into service. The portlet container 
     * calls the init method exactly once after instantiating the filter. 
     * The init method must complete successfully before the filter 
     * is asked to do any filtering work.
     * <p>
     * The portlet container cannot place the filter into service if the init method either
     * <ul>
     *   <li>throws a PortletException</li>
     *   <li>does not return within a time period defined by the portlet container</li>
     * </ul>
     * 
     * @param filterConfig    the filter configuration data defined 
     *                        in the portlet deployment descriptor
     * @throws PortletException  if an error occurs in the filter initialization
     */
    public void init(FilterConfig filterConfig) throws PortletException;

    
    /**
     * Called by the portlet container to indicate to a filter that it is 
     * being taken out of service. This method is only called once all threads 
     * within the filter's <code>doFilter</code> method have exited or 
     * after a timeout period has passed. 
     * <p>
     * After the portlet container calls this method, it will not call the 
     * <code>doFilter</code> method again on this instance of the filter.
     * <p>
     * This method gives the filter an opportunity to clean up any resources 
     * that are being held (for example, memory, file handles, threads) and 
     * make sure that any persistent state is synchronized with the 
     * filter's current state in memory.
     */
    public void destroy();

}
