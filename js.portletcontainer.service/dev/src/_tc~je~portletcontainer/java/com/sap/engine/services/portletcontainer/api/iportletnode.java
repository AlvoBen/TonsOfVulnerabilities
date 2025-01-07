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
package com.sap.engine.services.portletcontainer.api;

import java.util.Enumeration;
import java.util.Map;

import javax.portlet.PortalContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.WindowState;

import com.sap.security.api.IUser;

/**
* The <code>IPortletNode</code> interface provides additional information for
* the invoked Portlet that is related to the Portal runtime environment.
*
* @author Diyan Yordanov
* @version 7.10
*/
public interface IPortletNode {

  /**
   * Defines that the portlet is invoked in the scope of render() method.
   */
  public static final String RENDER_METHOD = "render";

  /**
   * Defines that the portlet is invoked in the scope of processAction() method.
   */
  public static final String ACTION_METHOD = "action";

  /**
   * Returns the name of the application, the part of which is this portlet. 
   * @return the name of the application, the part of which is this portlet.
   */
  public String getPortletApplicationName();
  
  /**
   * Returns the name of the portlet.
   * @return the name of the portlet.
   */
  public String getPortletName();

  /**
   * Returns the current Window State of this Portlet
   * @return the current Window State of this Portlet
   */
  public WindowState getWindowState();
  
  /**
   * Updates the Window State of this Portlet
   * @param windowState the new <code>WindowState</code> to be set.
   */
  public void setWindowState(WindowState windowState);
  
  /**
   * Checks whether the current user is allowed to switch to this state. 
   * @param state window state to check.
   * @return true, if the current user can change the state to the given window state.
   */
  public boolean isWindowStateAllowed(WindowState state);

  /**
   * Returns the current Portlet Mode of this Portlet.
   * @return the current Portlet Mode of this Portlet.
   */
  public PortletMode getPortletMode();
  
  /**
   * Updates the Portlet Mode of this Portlet
   * @param portletMode the new <code>PortletMode</code> to be set.
   */
  public void setPortletMode(PortletMode portletMode);

  /**
   * Checks whether the current user is allowed to switch to this mode. 
   * @param mode portlet mode to check
   * @return true, if the current user can change the mode to the given portlet mode.
   */
  public boolean isPortletModeAllowed(PortletMode mode);

  /**
   * Returns the type of the portlet request (action or render). 
   * @return the type of the portlet request. The possible values are:
   * <ul>
   * <li>RENDER_METHOD
   * <li>ACTION_METHOD
   * </ul>
   */
  public String getMethod();
  
  /**
   * Converts the specified portet URL to string for use in the portlet's
   * render method.
   * @param portletURL the portlet URL to be converted.
   * @return string representation of this URL.
   */
  public String convertPortletURLToString(IPortletURL portletURL);

  // Method providing request parameters in the scope of the current portlet

  /**
   * Returns the value of a request parameter as a <code>String</code>, or <code>null</code>
   * if the parameter does not exist.
   *
   * @param name a <code>String</code> specifying the name of the parameter.
   * @return a <code>String</code> representing the single value of the parameter.
   */
  public String getParameter(String name);

  /**
   * Returns an array of <code>String</code> objects containing all of the values
   * the given request parameter has, or null if the parameter does not exist
   * @param name a <code>String<code> specifying the name of the parameter.
   * @return  an array of <code>String</code> objects containing the parameter values.
   */
  public String[] getParameterValues(String name);

  /**
   * Returns an <code>Enumeration</code> of <code>String</code> objects containing the names
   * of the request parameters. If there are no parameters set in the request,
   * the method returns an empty <code>Enumeration</code>.
   * @return an <code>Enumeration</code> of <code>String</code> objects, each <code>String</code>
   * containing the name of a request parameter; or an empty <code>Enumeration</code>
   * if the request has no parameters
   */
  public Enumeration getParameterNames();

  /**
   * Retrieves a unique ID for the portlet (even if the same portlet is included
   * twice in the same page).
   * @return	context name - a unique identification for the portlet window
   * that must not contain a "?" character.
   */
  public String getContextName(); //Renamed from getObjectId to be compatible with PRT terms

  /**
   * Sets a String property for the sub-sequent render request.
   * <p>This property will be accessible in all sub-sequent render calls
   * via the <code>PortletRequest.getProperty</code> call until a request is targeted
   * to the portlet.
   * <p>This method is called each time the <code>PortletResponse.setProperty</code>
   * mathod is called.
   * @param key the key of the property.
   * @param value the value of the property.
   */
  public void setProperty(String key, String value);

  /**
   * Adds a String property to an existing key property for the sub-sequent render request.
   * <p>This property will be accessible in all sub-sequent render calls
   * via the <code>PortletRequest.getProperty</code> call until a request is targeted
   * to the portlet.
   * <p>This method is called each time the <code>PortletResponse.setProperty</code>
   * mathod is called.
   * @param key
   * @param value
   */
  public void addProperty(String key, String value);

  /**
   * Sets a parameter map for the sub-sequent render request.
   * <p>These parameters will be accessible in all sub-sequent render calls
   * via the <code>PortletRequest.getParameterMap</code> call until a request is targeted
   * to the portlet.
   * <p>This method is called each time the <code>ActionResonse.setRenderParameters</code>
   * method is called.
   * @param parameters Map containing parameter names for the render phase as keys and parameter.
   */
  public void setRenderParameters(Map parameters);

  /**
   * Sets a String parameter for the sub-sequent render request.
   * <p>This parameter will be accessible in all sub-sequent render calls
   * via the <code>PortletRequest.getParameter</code> call until a request is targeted
   * to the portlet.
   * <p>This method is called each time the <code>ActionResonse.setRenderParameter</code>
   * method is called.
   * @param key key of the render parameter.
   * @param value value of the render parameter.
   */
  public void setRenderParameter(String key, String value);

  /**
   * Sets a String array parameter for the sub-sequent render request.
   * <p>These parameters will be accessible in all sub-sequent render calls
   * via the <code>PortletRequest.getParameter</code> call until a request is targeted
   * to the portlet.
   * <p>This method is called each time the <code>ActionResonse.setRenderParameter</code>
   * method is called.
   * @param key key of the render parameter.
   * @param values values of the render parameter.
   */
  public void setRenderParameter(String key, String[] values);

  /**
   * Retrieves PortletPreferences (user-specific property set!)
   * @return the portlet preferences for this request to this portlet window
   */
  public PortletPreferences getPortletPreferences();

  /**
   * Gets the portal context.
   * @return	the portal context object
   */
  public PortalContext getPortalContext();

  /**
   * Gets the user making the request.
   * Gets read-access to the user's attributes
   * @return the user making the request.
   */
  public IUser getUser();
  
  /**
   * Passes the preferred portlet title to be used by the Portal for the title-bar.
   * The title from the Resource Bundle title may be overrided by the portal or 
   * programmatically by the portlet.
   * @param title the title set by the portlet
   */
  public void setPortletTitle(String title);
  
  /**
   * Passes the expiration cache timeout defined in the portlet deployment descriptor.
   * @param expirationValue expiration time in seconds. If expiration value is
   * set to 0, caching is disabled for the portlet, if the value is set to -1, 
   * the cache does not expire. If the value is set to <code>null</code> - portlet
   * has not defined expipration cache and if the expiration cache property in 
   * the <code>RenderResponse</code> is set, it must be ignored. 
   */
  public void setExpirationCache(Integer expirationValue);
}
