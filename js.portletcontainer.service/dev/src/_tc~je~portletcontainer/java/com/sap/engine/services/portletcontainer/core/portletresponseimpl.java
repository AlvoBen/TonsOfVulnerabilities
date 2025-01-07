/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.portletcontainer.core;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.sap.engine.services.portletcontainer.LogContext;
import com.sap.tc.logging.Location;

/**
* The <code>PortletResponseImpl</code> class is an implementation of the <code>
* PortletResponse</code> interface which assist a portlet in creating and 
* sending a response to the client. The portlet container uses two specialized
* versions of this interface when invoking a portlet, ActionResponse and 
* RenderResponse. The portlet container creates these objects and passes them
* as arguments to the portlet's processAction and render methods. 
*
* @author Diyan Yordanov
* @version 7.10
*/
public abstract class PortletResponseImpl extends HttpServletResponseWrapper implements PortletResponse {

  private Location currentLocation = Location.getLocation(getClass());
  /**
   * the servlet request of the target/portlet's web module
   */
  private PortletRequest portletRequest = null;
  
  private Map properties;
  
  //IPortletNode portletNode = null;
  
  /**
   * Creates new <code>PortletResponseImpl</code> object.
   * @param portletRequest the portlet request object that contains the request 
   * the client has made.
   * @param servletResponse the servlet response object that contains the response 
   * the portlet sends to the client.
   */
  public PortletResponseImpl(PortletRequest portletRequest, HttpServletResponse servletResponse) {
    super(servletResponse);
    this.portletRequest = portletRequest;
  }

  /**
   * Adds a String property to an existing key to be returned to the portal.
   * <p>
   * This method allows response properties to have multiple values.
   * <p>
   * Properties can be used by portlets to provide vendor specific 
   * information to the portal.
   * @param  key    the key of the property to be returned to the portal.
   * @param  value  the value of the property to be returned to the portal.
   * @exception IllegalArgumentException if key is <code>null</code>.
   */
  public void addProperty(String key, String value) {
    //if key is null - throw IllegalArgumentException.
    if (key == null) {
      LogContext.getCategory(LogContext.CATEGORY_REQUESTS).logWarning(currentLocation, "ASJ.portlet.000055", "The key of the property cannot be null.", null, null);     
      throw new IllegalArgumentException("The key of the property cannot be null.");
    }
    Map properties = getProperties();
    String[] oldValues = (String[]) properties.get(key);
    String[] newValues = null;
    if (oldValues == null) {
      newValues = new String[]{value}; 
    } else {
      int len = oldValues.length;
      newValues = new String[len+1];
      System.arraycopy(oldValues, 0, newValues, 0, len);
      newValues[len] = value;
    }
    properties.put(key, newValues);
    // TODO IPortletNode.addProperty() trqbwa li da se izvika
    //These parameters will be used in all subsequent render requests until a new client request targets the portlet.
    //overriden in subclasses
  }

  /**
   * Sets a String property to be returned to the portal.
   * <p>
   * Properties can be used by portlets to provide vendor specific 
   * information to the portal.
   * <p>
   * This method resets all properties previously added with the same key.
   * @param  key    the key of the property to be returned to the portal.
   * @param  value  the value of the property to be returned to the portal.
   * @exception  IllegalArgumentException if key is <code>null</code>.
   */

  public void setProperty(String key, String value) {
    if (key == null) {
      LogContext.getCategory(LogContext.CATEGORY_REQUESTS).logWarning(currentLocation, "ASJ.portlet.000056", "The key of the property cannot be null.", null, null);
      throw new IllegalArgumentException("The key of the property cannot be null.");
    }

    Map properties = getProperties();
    
    String[] newValues = new String[]{value}; 
    properties.put(key, newValues);
    //TODO IPortletNode.setProperty() trqbwa li da se izvika
    //These parameters will be used in all subsequent render requests until a new client request targets the portlet.
    //overriden in subclasses
  }

  /**
   * Returns the encoded URL of the resource, like servlets, JSPs, images and 
   * other static files, at the given path.
   * <p>
   * Some portal/portlet-container implementation may require those URLs to contain 
   * implementation specific data encoded in it. Because of that, portlets should 
   * use this method to create such URLs.
   * <p>
   * The <code>encodeURL</code> method may include the session ID 
   * and other portal/portlet-container specific information into the URL. 
   * If encoding is not needed, it returns the URL unchanged. 
   * @param path the URI path to the resource. This must be either an absolute 
   * URL (e.g. <code>http://my.co/myportal/mywebap/myfolder/myresource.gif</code>)
   * or a full path URI (e.g. <code>/myportal/mywebap/myfolder/myresource.gif</code>).
   * @exception IllegalArgumentException if path doesn't have a leading slash or 
   * is not an absolute URL.
   * @return the encoded resource URL as string.
   */
  public String encodeURL(String path) {
    if (path.indexOf("://") == -1 && !path.startsWith("/")) {
      LogContext.getCategory(LogContext.CATEGORY_REQUESTS).logWarning(currentLocation, "ASJ.portlet.000057", "Only absolute URLs or full path URIs are allowed.", null, null);
      throw new IllegalArgumentException("Only absolute URLs or full path URIs are allowed.");
    }
    return path;
  }

  /**
   * Returns the encoded URL of the resource, like servlets, JSPs, images and 
   * other static files, at the given path.
   * <p>
   * Some portal/portlet-container implementation may require those URLs to contain 
   * implementation specific data encoded in it. Because of that, portlets should 
   * use this method to create such URLs.
   * <p>
   * The <code>encodeURL</code> method may include the session ID 
   * and other portal/portlet-container specific information into the URL. 
   * If encoding is not needed, it returns the URL unchanged. 
   * @param path the URI path to the resource. This must be either an absolute 
   * URL (e.g. <code>http://my.co/myportal/mywebap/myfolder/myresource.gif</code>)
   * or a full path URI (e.g. <code>/myportal/mywebap/myfolder/myresource.gif</code>).
   * @exception IllegalArgumentException if path doesn't have a leading slash or 
   * is not an absolute URL.
   * @return the encoded resource URL as string.
   */
  public String encodeUrl(String path) {
    return this.encodeURL(path);
  }
  
  protected HttpServletResponse getWrappedHttpServletResponse() {
      return(HttpServletResponse) super.getResponse();
  }

  protected PortletRequest getPortletRequest() {
      return portletRequest;
  }
  
  private Map getProperties() {
    if (properties == null)
        properties = new HashMap();
    return properties;
  }

}
