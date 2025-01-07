/*
 * Copyright 2006 IBM Corporation.
 *
 */
package javax.portlet.filter;



import java.util.Enumeration;

import javax.portlet.PortletContext;

/**
 * A filter configuration object used by a portlet container 
 * to pass information to a filter during initialization.
 *
 * @since 2.0
 */
public interface FilterConfig {

    /**
     * Returns the filter-name of this filter as defined in the 
     * portlet deployment descriptor.
     * 
     * @return  the filter name
     */
    public String getFilterName();
    
    /**
     * Returns a reference to the <code>PortletContext</code> in which the caller is executing.
     * 
     * @return  the portlet context
     */
    public PortletContext getPortletContext();
    
    /**
     * Returns a String containing the value of the named 
     * initialization parameter, or <code>null</code> if the parameter does not exist.
     * <p>
     * Initialization parameters are defined in the portlet deployment descriptor.
     * 
     * @param name  the name of the initialization parameter to return
     * @return  initialization parameter, or <code>null</code> if the parameter does not exist.  
     */
    public String getInitParameter(String name);
    
    /**
     * Returns the names of the filter's initialization parameters 
     * as an Enumeration of String objects, or an empty Enumeration 
     * if the filter has no initialization parameters.
     * <p>
     * Initialization parameters are defined in the portlet deployment descriptor.
     * 
     * @return the names of the filter's initialization parameters, 
     *         or an empty Enumeration if the filter has no initialization parameters.
     */
    public Enumeration<String> getInitParameterNames();
}
