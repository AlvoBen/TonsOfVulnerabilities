/*
 * Copyright 2006 IBM Corporation.
 *
 */
package javax.portlet.filter;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

/**
 * A <code>FilterChain</code> is an object provided by the portlet container 
 * to the developer giving a view into the invocation chain of a 
 * filtered request for a portlet. Filters use the <code>FilterChain</code> 
 * to invoke the next filter in the chain, or if the calling filter is the 
 * last filter in the chain, to invoke the portlet at the end of the chain.
 *
 * @since 2.0
 */
public interface FilterChain {

    /**
     * Causes the next filter in the chain to be invoked, 
     * or if the calling filter is the last filter in the chain, 
     * causes the portlet at the end of the chain to be invoked.
     * 
     * @param request  the current action request. 
     * @param response  the current action response
     *                   
     * @throws IOException  if an IO error occurred in the filter processing
     * @throws PortletException  if a portlet exception occurred in the filter processing
     */
    public void doFilter(ActionRequest request, ActionResponse response)
     throws IOException, PortletException;

    /**
     * Causes the next filter in the chain to be invoked, 
     * or if the calling filter is the last filter in the chain, 
     * causes the portlet at the end of the chain to be invoked.
     * 
     * @param request  the current event request. 
     * @param response  the current event response.
     *  
     * @throws IOException  if an IO error occured in the filter processing
     * @throws PortletException  if a portlet exception occured in the filter processing
     */
    public void doFilter(EventRequest request, EventResponse response)
     throws IOException, PortletException;

    /**
     * Causes the next filter in the chain to be invoked, 
     * or if the calling filter is the last filter in the chain, 
     * causes the portlet at the end of the chain to be invoked.
     * 
     * @param request  the current render request.
     *  
     * @param response  the current render response.
     *  
     * @throws IOException  if an IO error occurred in the filter processing
     * @throws PortletException  if a portlet exception occurred in the filter processing
     */
    public void doFilter(RenderRequest request, RenderResponse response)
     throws IOException, PortletException;
    
    /**
     * Causes the next filter in the chain to be invoked, 
     * or if the calling filter is the last filter in the chain, 
     * causes the portlet at the end of the chain to be invoked.
     * 
     * @param request  the current resource request. 
     * @param response  the current resource response.
     *  
     * @throws IOException  if an IO error occurred in the filter processing
     * @throws PortletException  if a portlet exception occurred in the filter processing
     */
    public void doFilter(ResourceRequest request, ResourceResponse response)
     throws IOException, PortletException;


}
