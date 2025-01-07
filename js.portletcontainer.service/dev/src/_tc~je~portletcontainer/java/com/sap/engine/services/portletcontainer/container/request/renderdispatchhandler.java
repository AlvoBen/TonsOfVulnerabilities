/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.portletcontainer.container.request;

import java.io.IOException;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.sap.engine.services.portletcontainer.LogContext;
import com.sap.engine.services.servlets_jsp.webcontainer_api.request.IDispatchHandler;
import com.sap.tc.logging.Location;

/**
 * RenderDispatchHandler
 * 
 * @date 2005-9-27
 * @author vera-b
 */
public class RenderDispatchHandler implements IDispatchHandler {

  private static final String NA_METHOD_MSG = "This operation is not applicable " +
		"for the portlet container; this may indicate that the request or the response is null.";

  private Location currentLocation = Location.getLocation(getClass());
  private Portlet portlet = null;
  private PortletException pex = null;
  private IOException ioex = null;
  
  /**
   * 
   */
  public RenderDispatchHandler(Portlet portlet) {
    super();
    this.portlet = portlet;
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.servlets_jsp.webcontainer_api.request.IDispatchHandler#service(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
   */
  public void service(ServletRequest renderRequest, ServletResponse renderResponse) {
    try {
      portlet.render((RenderRequest) renderRequest, (RenderResponse) renderResponse);
    } catch (PortletException e) {
      pex = e;
    } catch (IOException e) {
      ioex = e;
    }
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.servlets_jsp.webcontainer_api.request.IDispatchHandler#service()
   */
  public void service() {
    LogContext.getCategory(LogContext.CATEGORY_REQUESTS).logWarning(currentLocation, "ASJ.portlet.000002", "{0}", new Object[]{NA_METHOD_MSG}, null, null);
  }

  public PortletException getPortletException() {
    return pex;
  }
  
  public IOException getIOException() {
    return ioex;
  }
}
