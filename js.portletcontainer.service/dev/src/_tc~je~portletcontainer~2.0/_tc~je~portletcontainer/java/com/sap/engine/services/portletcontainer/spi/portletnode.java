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
package com.sap.engine.services.portletcontainer.spi;

import java.util.Collection;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.servlet.http.Cookie;

import com.sap.security.api.IUser;

/**
* The <code>PortletNode</code> interface provides additional information for
* the invoked Portlet that is related to the Portal runtime environment.
*
* @author Diyan Yordanov
* @version 7.10
*/
public interface PortletNode {

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
   * @return true, if the current user can change the state to the given window 
   * state.
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
   * @return true, if the current user can change the mode to the given portlet 
   * mode.
   */
  public boolean isPortletModeAllowed(PortletMode mode);

  
  /**
   * This method allows the portlet container to tell the portal the next possible 
   * portlet modes that make sense from the portlet point of view.
   * <p>
   * If set, the portal should honor these enumeration of portlet modes and only 
   * provide the end user with choices to the provided portlet modes or a subset 
   * of these modes based on access control considerations.
   * <p>
   * If the portlet container does not set any next possible portlet modes the 
   * default is that all portlet modes that the portlet has defined supporting 
   * in the portlet deployment descriptor are meaningful new portlet modes.
   * 
   * @param portletModes <code>Enumeration</code> of <code>PortletMode</code> 
   * objects with the next possible portlet modes that the make sense from the
   * portlet point of view, must not be <code>null</code> or an empty enumeration.
   * 
   * @since 2.0
   */
  public void setNextPossiblePortletModes(Collection<PortletMode> portletModes);
  
  
  /**
   * Returns a <code>Map</code> containing all private render parameters. 
   * If there are no parameters set, the method returns an empty <code>Map</code>.
   * Private render parameters should only be set from the portal 
   * if there is a render request as a result of a render URL to this portlet.
   * <p/>
   * Public render parameters can be obtained from <code>CoordinationService</code>.
   * 
   * @return an <code>Map<code> of private render parameter names as keys 
   * and their corresponding values as value. 
   */
  public Map<String, String[]> getPrivateRenderParameters();
  
  /**
   * Returns a merged set of parameters set on the URL 
   * (if the call is result of a portlet URL) and client request parameters.
   * If there is a parameter with the same name 
   * in URL and in client request, then the values set on the URL should precede 
   * the values from the client request.
   * 
   * @return an <code>Map<code> of request parameter names as keys 
   * and their corresponding values as value.
   */
  public Map<String, String[]> getRequestParameters();
  
  /**
   * Retrieves a unique ID for the portlet (even if the same portlet is included
   * twice in the same page).
   * 
   * @return context name - a unique identification for the portlet window
   * that must not contain a "?" character.
   */
  public String getContextName(); //Renamed from getObjectId to be compatible with PRT terms

 
  /**
   * Sets a String property for the sub-sequent render request.
   * <p>This property will be accessible in all sub-sequent render calls
   * via the <code>PortletRequest.getProperty</code> call until a request is 
   * targeted to the portlet.
   * <p>This method is called each time the <code>PortletResponse.setProperty
   * </code> mathod is called.
   * @param key the key of the property.
   * @param value the value of the property.
   */
  public void setProperty(String key, String value);

  /**
   * Adds a String property to an existing key property for the sub-sequent render
   * request. <p>This property will be accessible in all sub-sequent render calls
   * via the <code>PortletRequest.getProperty</code> call until a request is 
   * targeted to the portlet.
   * <p>This method is called each time the <code>PortletResponse.setProperty
   * </code> mathod is called.
   * @param key the key of the property.
   * @param value the value of the property.
   * 
   * @since 2.0
   */
  public void addProperty(String key, String value);

  /**
   * Sets a private parameter map for the sub-sequent render request. <p>
   * Private render parameters need only to be set from the portal 
   * if there is a render request as a result of a render URL to this portlet.
   * <p/>
   * Public render parameters should be set in the <code>CoordinationService</code>.
   * 
   * @param parameters Map containing parameter names for the render phase as keys 
   * and parameter.
   */
  public void setPrivateRenderParameters(Map<String, String[]> parameters);

  /**
   * Gets the portal context.
   * @return	the portal context object
   */
  public PortalContextExt getPortalContext();

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
   * Adds a HTTP cookie added via PortletResponse.addPorpertie(Cookie).
   * 
   * The portlet should note that the cookie may not make it to the client, 
   * but may be stored at the portal.
   * 
   * @param cookie the cookie to be added to the response 
   * 
   * @since 2.0 
   */
  public void addCookie(Cookie cookie);
  
  /**
   * Retrieves Cookies set by the portlet via PortletResponse.addProperty(Cookie)
   * @return Cookies set by the portlet via PortletResponse.addProperty(Cookie)
   * 
   * @since 2.0
   */
  public Cookie[] getCookies();

  /**
   * Returns the PortletPreferencesService
   * @return the PortletPreferencesService
   * 
   * @since 2.0
   */
  public PortletPreferencesService getPortletPreferencesService();

  /**
   * Returns the PortletURLProviderService
   * @return the PortletURLProviderService
   * 
   * @since 2.0
   */
  public PortletURLProviderService getPortletURLProviderService();

  /**
   * Returns the CoordinationService
   * @return the CoordinationService
   * 
   * @since 2.0
   */
  public CoordinationService getCoordinationService();

  /**
   * Returns the CachingService
   * @return the CachingService
   * 
   * @since 2.0
   */
  public CachingService getCachingService();

  /**
   * Returns the cache level of this resource request.
   * <p>
   * Possible return values are: 
   * <code>ResourceURL.FULL, ResourceURL.PORTLET</code> 
   * or <code>ResourceURL.PAGE</code>.
   * 
   * @since 2.0
   * @return  the cache level of this resource request.
   */
  public String getCacheability();
  
  /**
   * Returns the resource ID set on the ResourceURL or <code>null</code>
   * if no resource ID was set on the URL.
   * 
   * @since 2.0
   * @return  the resource ID set on the ResourceURL,or <code>null</code>
   *          if no resource ID was set on the URL. 
   */
  public String getResourceID(); 
  
}
