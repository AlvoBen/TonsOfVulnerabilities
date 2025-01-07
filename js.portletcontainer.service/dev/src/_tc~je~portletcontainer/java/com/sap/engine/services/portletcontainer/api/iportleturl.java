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

import java.util.HashMap;

/**
 * The <code>IPortletURL</code> interface allows the Portal Application to get
 * the <code>PortletMode</code> and <code>WindowState</code> and the URL parameters
 * of a <code>PortletURL</code> instance and to be able to create a valid URL for
 * the portlet to include it in its content.
 *
 * @author Diyan Yordanov
 * @version 7.10
 */
public interface IPortletURL {

  /**
   * Returns the type of the PortletURL - action or render.
   * @return the type of the PortletURL - action or render. The possible values are:
   * <ul>
   * <li>RENDER_METHOD
   * <li>ACTION_METHOD
   * </ul>
   */
  public String getMethod();

  /**
   * Returns the <code>String</code> representation of the <code>PortletMode</code>
   * the portlet should be in, if the portlet URL triggers a request.
   * @return the portlet mode set for this portlet URL.
   */
  public String getMode();

  /**
   * Returns the <code>String</code> representation of the <code>WindowState</code>
   * the portlet should be in, if the portlet URL triggers a request.
   * @return the portlet window state set for the portlet URL.
   */
  public String getState();

  /**
   * Checks whether the portlet requests a secure conection between the client
   * and the porltet window for this URL.
   * @return <code>true</code>, if portlet requests to have a secure connection
   * between its portlet window and the client; <code>false</code>, if the portlet
   * does not require a secure connection.
   */
  public boolean isSecure();

  /**
   * Returns the parameter map the portlet has set for this URL.
   * @return <code>Map</code> containing parameter names for the render phase
   * as keys and parameter. 
   */
  public HashMap getParameters();
}
