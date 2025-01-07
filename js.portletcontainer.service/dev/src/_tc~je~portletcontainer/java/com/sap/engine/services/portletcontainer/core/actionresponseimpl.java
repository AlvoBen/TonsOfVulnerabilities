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

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.ActionResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletRequest;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.sap.engine.services.portletcontainer.LogContext;
import com.sap.engine.services.portletcontainer.api.IPortletNode;
import com.sap.tc.logging.Location;

/**
* The <code>ActionResponseImpl</code> class is an implemetation of the <code>
* ActionResponse</code> interface that represents the portlet response to 
* an action request. ActionResponse extends the PortletResponse interface 
* to provide specific action response functionality to portlets.
* The portlet container creates an ActionResponse object and passes it as 
* argument to the portlet's processAction method. 
*
* @author Diyan Yordanov
* @version 7.10
*/
public class ActionResponseImpl extends PortletResponseImpl implements ActionResponse, ActionResponseContext {
  
  private Location currentLocation = Location.getLocation(getClass());
  
  /**
   * Shows whether the sendRedirect() method can be still invoked.
   */
  private boolean redirectAllowed = true;
  
  /**
   * Shows if a redirection has been done.
   */
  private boolean redirected = false;
  
  /**
   * The redirect location URL.
   */
  private String redirectLocation = null;
  
  /**
   * Current window state set.
   */
  private WindowState windowState = null;
  
  /**
   * Current portlet mode set.
   */
  private PortletMode portletMode = null;
  
  /**
   * Render parameters set for the render request. 
   */
  private Map renderParameters = new HashMap();
  
  /**
   * Reference to the <code>IPortletNode</code> object that specifies the invoked
   * portlet.
   */
  private IPortletNode portletNode = null;

  /**
   * Creates new <code>ActionResponseImpl</code> object.
   * @param portletRequest the PortletRequest object contains the portlet request 
   * the client has made. 
   * @param servletResponse the servlet response object that contains the response 
   * the portlet sends to the client.
   * @param portletNode the <code>IPortletNode</code> object that specified 
   * the requested portlet.
   */
  public ActionResponseImpl(PortletRequest portletRequest, HttpServletResponse servletResponse, IPortletNode portletNode) {
    super(portletRequest, servletResponse);
    this.portletNode = portletNode;
  }
  
  /**
   * Sets the window state of a portlet to the given window state. 
   * Possible values are the standard window states and any custom window states
   * supported by the portal and the portlet. Standard window states are: 
   * <ul>
   * <li>MINIMIZED 
   * <li>NORMAL 
   * <li>MAXIMIZED 
   * </ul>
   * @param windowState the new portlet window state.
   * @throws WindowStateException if the portlet cannot switch to the specified 
   * window state. To avoid this exception the portlet can check the allowed 
   * window states with <code>Request.isWindowStateAllowed()</code>.
   * @throws IllegalStateException if the method is invoked after <code>sendRedirect
   * </code> has been called.
   */
  public void setWindowState(WindowState windowState) throws WindowStateException {
    if (redirected) {
      throw new IllegalStateException("It is not allowed to invoke setWindowState() after sendRedirect() has been called.");
    }
    
    if (isWindowStateAllowed(windowState)) {
      //put off for the end of processAction
      //portletNode.setWindowState(windowState);
      this.windowState = windowState;
    } else {
      throw new WindowStateException("Can't set this WindowState", windowState);
    }
    
    redirectAllowed = false;
  }

  /**
   * Sets the portlet mode of a portlet to the given portlet mode.
   * Possible values are the standard portlet modes and any custom portlet modes
   * supported by the portal and the portlet. 
   * Standard portlet modes are: 
   * <ul>
   * <li>EDIT
   * <li>HELP
   * <li>VIEW
   * </ul>
   * @param portletMode the new portlet mode.
   * @throws PortletModeException if the portlet cannot switch to this portlet 
   * mode, because the portlet or portal does not support it for this markup, or 
   * the current user is not allowed to switch to this portlet mode. To avoid 
   * this exception the portlet can check the allowed portlet modes with 
   * <code>Request.isPortletModeAllowed()</code>.
   * @throws IllegalStateException if the method is invoked after <code>sendRedirect
   * </code> has been called.
   */
  public void setPortletMode(PortletMode portletMode) throws PortletModeException {
    if (redirected) {
      throw new IllegalStateException("It is not allowed to invoke setPortletMode() after sendRedirect() has been called.");
    }

    if (isPortletModeAllowed(portletMode)) {
      //portletNode.setPortletMode(portletMode);
      this.portletMode = portletMode;
    } else {
      throw new PortletModeException("Can't set this PortletMode", portletMode);
    }

    redirectAllowed = false;
  }

  /** 
   * Instructs the portlet container to send a redirect response to the client 
   * using the specified redirect location URL.
   * This method only accepts an absolute URL or a full path URI. 
   * The sendRedirect method can not be invoked after any of the following methods 
   * of the <code>ActionResponse</code> interface has been called: 
   * <ul>
   * <li>setPortletMode 
   * <li>setWindowState
   * <li>setRenderParameter
   * <li>setRenderParameters 
   * </ul>
   * @param location the redirect location URL.
   * @throws IOException if an input or output exception occurs.
   * @throws IllegalArgumentException if a relative path URL is given.
   * @throws IllegalStateException if the method is invoked after any of above 
   * mentioned methods of the <code>ActionResponse</code> interface has been called.
   */
  public void sendRedirect(String location) throws IOException {
    //This method accepts only fully qualified URL or a full path URL
    if (checkURL(location)) {
      if (redirectAllowed) {
        HttpServletResponse redirectResponse = getWrappedHttpServletResponse();
        while (redirectResponse instanceof HttpServletResponseWrapper) {
          redirectResponse = (HttpServletResponse)
                             ((HttpServletResponseWrapper)redirectResponse).getResponse();
        }
        location = redirectResponse.encodeRedirectURL(location);
        //put off for the end of processAction - exception handling
        //redirectResponse.sendRedirect(location);
        redirectLocation = location;
        redirected = true;
      } else {
        throw new IllegalStateException("Method is invoked after setPortletMode(), " +
            "setWindowState(), setRenderParameter() or setRenderParameters().");
      }
    } else {
      throw new IllegalArgumentException("Can't invoke sendRedirect() method, incorrect location parameter: " + location);
    }
    
  }

  /**
   * Sets a parameter map for the render request.
   * <p> 
   * All previously set render parameters are cleared.
   * <p> 
   * These parameters will be accessible in all sub-sequent render calls via 
   * the <code>PortletRequest.getParameter</code> call until a new request is 
   * targeted to the portlet.
   * <p> 
   * The given parameters do not need to be encoded prior to calling this method. 
   * @param parameters <code>Map</code> containing parameter names for the render
   * phase as keys and parameter values as map values. The keys in the parameter 
   * map must be of type <code>String</code>. The values in the parameter map must 
   * be of type <code>String</code> array (String[]).
   * @throws IllegalArgumentException - if parameters is <code>null</code>, if 
   * any of the key/values in the <code>Map</code> are </code>null</code>, if 
   * any of the keys is not a <code>String</code>, or if any of the values is not 
   * a <code>String</code> array.
   * @throws IllegalStateException - if the method is invoked after <code>sendRedirect
   * </code> has been called.
   */
  public void setRenderParameters(Map parameters) {
    if (redirected) {
      throw new IllegalStateException("Can't invoke setRenderParameters() after sendRedirect() has been called");
    }
    
    if (parameters == null) {
      throw new IllegalArgumentException("Render parameters must not be null.");
    }
 
    Iterator iterator = parameters.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry entry = (Map.Entry)iterator.next();
      //keys must be String
      if (!(entry.getKey() instanceof String)) {
        throw new IllegalArgumentException("Keys must be of type java.lang.String.");
      }
      //values must be String arrays
      if (!(entry.getValue() instanceof String[])) {
        throw new IllegalArgumentException("Values must be of type java.lang.String[].");
      }
    }

    //For those method parameters or return values that take String[] or Map 
    //with values that are String[], the portlet container must make copies of 
    //the String[] such that modification to the String[] by the portlet after 
    //the method call will not affect the function of the portlet container.
    Map newParameters = new HashMap(parameters);
    iterator = newParameters.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry entry = (Map.Entry)iterator.next();
      String[] value = (String[])entry.getValue();
      int length = value.length;
      String[] newValue = new String[length];
      System.arraycopy(value, 0, newValue, 0, length);
      entry.setValue(newValue);
    }

    renderParameters = newParameters;
    //to keep parameters for subsequent request
    //put off for the end of the processAction
    //portletNode.setRenderParameters(newParameters);
    redirectAllowed = false; 
  }

  /**
   * Sets a String parameter for the render request.
   * <p>
   * These parameters will be accessible in all sub-sequent render calls via the
   * <code>PortletRequest.getParameter</code> call until a request is targeted to 
   * the portlet.
   * <p>
   * This method replaces all parameters with the given key.
   * <p>
   * The given parameter do not need to be encoded prior to calling this method.
   * @param key key of the render parameter.
   * @param value value of the render parameter.
   * @throws IllegalArgumentException - if key or value are <code>null</code>.
   * @throws IllegalStateException - if the method is invoked after <code>sendRedirect
   * </code> has been called.
   */
  public void setRenderParameter(String key, String value) {
    if (redirected) {
      throw new IllegalStateException("Can't invoke setRenderParameter() after sendRedirect() has been called");
    }
    if ((key == null) || (value == null)) {
      throw new IllegalArgumentException("Render parameter key or value must not be null.");
    }
    
    renderParameters.put(key, new String[] {value});
    //The portlet-container must not propagate parameters received in an action 
    //request to subsequent render requests of the portlet.
    //If a portlet wants to do that, it can use render URLs or it must use 
    //the setRenderParameter or setRenderParameters methods of the ActionResponse
    //object within the processAction call.
    
    //portletNode.setRenderParameter(key, value);
    
    redirectAllowed = false;
  }

  /**
   * Sets a String array parameter for the render request.
   * <p>
   * These parameters will be accessible in all sub-sequent render calls via the
   * <code>PortletRequest.getParameter</code> call until a request is targeted to the portlet.
   * <p>
   * This method replaces all parameters with the given key.
   * <p>
   * The given parameter do not need to be encoded prior to calling this method.
   * @param key key of the render parameter.
   * @param values values of the render parameter.
   * @exception	IllegalArgumentException if key or value are <code>null</code>.
   * @exception IllegalStateException if the method is invoked after <code>sendRedirect
   * </code> has been called.
   */
  public void setRenderParameter(String key, String[] values) {
    if (redirected) {
      throw new IllegalStateException("Can't invoke setRenderParameter() after sendRedirect() has been called");
    }
    if ((key == null) || (values == null) || (values.length == 0)) {
      throw new IllegalArgumentException("Render parameter key or value must not be null or values be an empty array.");
    }
    int length = values.length;
    String[] newValues = new String[length];
    System.arraycopy(values, 0, newValues, 0, length);
    renderParameters.put(key, newValues); 
    //to keep parameters for subsequent request
    //portletNode.setRenderParameter(key, values);
    redirectAllowed = false;
  }
  
  /**
   * Overrides the same method from the <code>PortletRespnse</cide> interface in
   * order to encode implementation specific data in the URLs. Because of that, 
   * portlets should use this method to create URLs.
   * <p>
   * Returns the encoded URL of the resource, like servlets,
   * JSPs, images and other static files, at the given path.
   * <p>
   * The <code>encodeURL</code> method may include the session ID 
   * and other portal/portlet-container specific information into the URL. 
   * If encoding is not needed, it returns the URL unchanged. 
   * @param path the URI path to the resource. This must be either an absolute URL
   * or a full path URI.
   * @exception IllegalArgumentException if path doesn't have a leading slash or
   * is not an absolute URL. 
   * @return the encoded resource URL as string.
   */  
  public String encodeURL(String path) {
    if (!checkURL(path)) {
      throw new IllegalArgumentException("Only absolute URLs or full path URIs are allowed [" + path + "].");
    }
    //return portletNode.convertPortletURLToString(new PortletURLImpl(portletNode, getPortletRequest(), PortletURLImpl.ACTION));
    // TODO The encodeURL method may include the session ID 
    // and other portal/portlet-container specific information into the URL.
    return super.encodeURL(path);
  }
  
  /**
   * Overrides the same method from the <code>PortletResponse</code> interface.
   * Adds a String property to an existing key to be returned to the portal.
   * <p>
   * This method allows response properties to have multiple values.
   * <p>
   * Properties can be used by portlets to provide vendor specific 
   * information to the portal.
   * @param key the key of the property to be returned to the portal.
   * @param value the value of the property to be returned to the portal.
   * @exception IllegalArgumentException if key is <code>null</code>.
   */  
  public void addProperty(String key, String value) {
    super.addProperty(key, value);
    //  These parameters will be used in all subsequent render requests until a new client request targets the portlet.
    portletNode.addProperty(key, value);
  }
  
  /**
   * Overrides the same method from the <code>PortletResponse</code> interface.
   * Sets a String property to be returned to the portal.
   * <p>
   * Properties can be used by portlets to provide vendor specific 
   * information to the portal.
   * <p>
   * This method resets all properties previously added with the same key.
   * @param key the key of the property to be returned to the portal.
   * @param value the value of the property to be returned to the portal.
   * @exception IllegalArgumentException if key is <code>null</code>.
   */
  public void setProperty(String key, String value) {
    super.setProperty(key, value);
    //  These parameters will be used in all subsequent render requests until a new client request targets the portlet.
    portletNode.setProperty(key, value);
  }
  
  //TODO private methods
  
  /**
   * Checks whether the specified location is either an absolute URL (e.g. 
   * <code>http://my.co/myportal/mywebap/myfolder/myresource.gif</code>)
   * or a full path URI (e.g. <code>/myportal/mywebap/myfolder/myresource.gif</code>).
   * @return false if relative path is specified.
   */
  private boolean checkURL(String location) {
    //only a fully qualified URL or a full path, relative path URL is not allowed
    if (location.indexOf("://") == -1 && !location.startsWith("/")) {
      LogContext.getCategory(LogContext.CATEGORY_REQUESTS).logWarning(currentLocation, "ASJ.portlet.000043", "Only absolute URLs or full path URIs are allowed [{0}].", new Object[]{location}, null, null);
      return false;
    }
    return true;
  }
  
  /**
   * Checks whether switching of the windows state to the given window state is allowed.
   * @param windowState window state to check.
   * @return true, if the current state can be changed.
   */
  private boolean isWindowStateAllowed(WindowState windowState) {
    if (redirected) {
      return false;
    }
    return getPortletRequest().isWindowStateAllowed(windowState);
  }

  /**
   * Checks whether switching of the portlet mode to the given portlet mode is allowed.
   * @param portletMode portlet mode to check.
   * @return true, if the current portlet mode can be changed.
   */
  private boolean isPortletModeAllowed(PortletMode portletMode) {
    if (redirected) {
      return false;
    }
    return getPortletRequest().isPortletModeAllowed(portletMode);
  }

  //ActionResponseContext implementation
  
  /**
   * Returns the redirect location URL.
   * @return the redirect location URL.
   */
  public String getRedirectLocation() {
    return redirectLocation;
  }

  /**
   * Returns a parameter map set for the render request.
   * @return a parameter map set for the render request.
   */
  public Map getRenderParameters() {
    return renderParameters;
  }

  /**
   * Returns the new portlet window state.
   * @return the new portlet window state.
   */
  public WindowState getChangedWindowState() {
    return windowState;
  }

  /**
   * Returns the new portlet mode.
   * @return the new portlet mode.
   */
  public PortletMode getChangedPortletMode() {
    return portletMode;
  }
  
}
