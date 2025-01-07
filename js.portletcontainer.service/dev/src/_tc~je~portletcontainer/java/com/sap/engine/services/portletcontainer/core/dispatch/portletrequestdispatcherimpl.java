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
package com.sap.engine.services.portletcontainer.core.dispatch;

import java.io.IOException;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

import com.sap.engine.services.portletcontainer.core.RenderRequestImpl;
import com.sap.engine.services.portletcontainer.core.application.PortletApplicationContext;
import com.sap.tc.logging.Location;

/**
 * The <code>PortletRequestDispatcherImpl</code> class is an implemetation of the
 * <code>PortletRequestDispatcher</code> interface which defines an object that 
 * receives requests from the client and sends them to the specified resources 
 * (such as a servlet, HTML file, or JSP file) on the server. The portlet 
 * container creates the PortletRequestDispatcher object, which is used as a 
 * wrapper around a server resource located at a particular path or given by a 
 * particular name.
 * 
 * @author Diyan Yordanov
 * @version 7.10
 */
public class PortletRequestDispatcherImpl implements PortletRequestDispatcher {
  
  private static final String JAVAX_PORTLET_RESPONSE = "javax.portlet.response";
  private static final String JAVAX_PORTLET_REQUEST = "javax.portlet.request";
  private static final String JAVAX_PORTLET_CONFIG = "javax.portlet.config";
  
  private Location currentLocation = Location.getLocation(getClass());
  private RequestDispatcher requestDispatcher = null;
  private PortletApplicationContext appCtx = null;

  /**
   * @param requestDispatcher
   */
  public PortletRequestDispatcherImpl(RequestDispatcher requestDispatcher,
      PortletApplicationContext appCtx) {
    this.requestDispatcher = requestDispatcher;    
    this.appCtx = appCtx;
  }

  /* (non-Javadoc)
   * @see javax.portlet.PortletRequestDispatcher#include(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
   */
  public void include(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException, IOException {
    try {
      DispatchedHttpServletRequest httpRequest = new DispatchedHttpServletRequest(renderRequest, appCtx);
      DispatchedHttpServletResponse httpResponse = new DispatchedHttpServletResponse(renderResponse, appCtx);
      
      PortletConfig prevPortletConfig = (PortletConfig) httpRequest.getAttribute(JAVAX_PORTLET_CONFIG);
      RenderRequest prevRenderRequest = (RenderRequest) httpRequest.getAttribute(JAVAX_PORTLET_REQUEST);
      RenderResponse prevRenderResponse = (RenderResponse) httpRequest.getAttribute(JAVAX_PORTLET_RESPONSE);
      
      httpRequest.setAttribute(JAVAX_PORTLET_CONFIG, ((RenderRequestImpl)renderRequest).getPortletConfig());
      httpRequest.setAttribute(JAVAX_PORTLET_REQUEST, renderRequest);
      httpRequest.setAttribute(JAVAX_PORTLET_RESPONSE, renderResponse);
      
      requestDispatcher.include(httpRequest, httpResponse);
      
      httpRequest.setAttribute(JAVAX_PORTLET_CONFIG, prevPortletConfig);
      httpRequest.setAttribute(JAVAX_PORTLET_REQUEST, prevRenderRequest);
      httpRequest.setAttribute(JAVAX_PORTLET_RESPONSE, prevRenderResponse);

    } catch (IOException ioe) {
      throw ioe;
    } catch (ServletException se) {
      if (se.getRootCause()!=null) {
        throw new PortletException(se.getRootCause());
      } else {
        throw new PortletException(se);
      }
    }
  }
}
