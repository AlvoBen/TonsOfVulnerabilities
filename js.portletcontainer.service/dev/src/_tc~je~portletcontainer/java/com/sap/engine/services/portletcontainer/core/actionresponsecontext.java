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

import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

/**
 * This interface is used by the portlet container to control all operations
 * that a portlet performs over the <code>ActionResponse</code>. This is especially 
 * valid in case the portlet throws an exception in the <code>processAction</code>
 * method. 
 * @author diyan-y
 * @version 7.10 
 */
public interface ActionResponseContext {
  
  /**
   * Returns the redirect location URL.
   * @return the redirect location URL.
   */
  public String getRedirectLocation();
  
  /**
   * Returns a parameter map set for the render request.
   * @return a parameter map set for the render request.
   */
  public Map getRenderParameters();
  
  /**
   * Returns the new portlet window state.
   * @return the new portlet window state.
   */
  public WindowState getChangedWindowState();
  
  /**
   * Returns the new portlet mode.
   * @return the new portlet mode.
   */
  public PortletMode getChangedPortletMode();

}
