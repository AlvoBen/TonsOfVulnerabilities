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

import java.io.IOException;

import javax.portlet.PortletException;
import javax.portlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* The <code>IPortletContainer</code> interface allows the portal application to
* invoke Portlets.
* <p>The Portlet Container manages the life cycle of the portlets. It implements the
* <code>IPortletContainer</code> interface in order to invoke portlets to process
* requests.
* <p>Request processing is divided into two types:
* <ul>
* <li>action requests handled through the <code>processAction</code> method, to perform
* actions targeted to the portlet;
* <li>render requests handled through the <code>render</code> method, to perform the render
* operation;
* </ul>
*
* @author Diyan Yordanov
* @version 7.10
*/
public interface IPortletContainer {

  /**
   * Invokes the <code>processAction</code> method of the portlet specified in the passed
   * <code>IPortletNode</code> object. The Portlet Container creates the <code>ActionRequest</code>
   * and the <code>ActionResponse</code> objects using the passed HttpServletRequest and
   * HttpServletResponse objects. Sets the request parameters and invokes the <code>processAction</code>
   * method of the portlet defined by the <code>portletNode</code> object.
   * 
   * @param request the servlet request object that contains the request the client has made.
   * @param response the servlet response object that contains the response the portlet sends to the client.
   * @param portletNode the <code>IPortletNode</code> object that specified the portlet to be invoked.
   * @throws IOException - if the streaming causes an I/O problem.
   * @throws PortletException - if the portlet has problems fulfilling the request.
   * @throws UnavailableException - if the portlet is unavailable to process the action at this time.
   * @throws PortletSecurityException - if the portlet cannot fullfill this request because of security reasons.
   */
  public void processAction(HttpServletRequest request, HttpServletResponse response, IPortletNode portletNode ) 
    throws IOException, PortletException;
 
  /**
   * Invokes the <code>render</code> method of the portlet specified in the given <code>IPortletNode</code>
   * object. The Portlet Container creates the <code>RenderRequest</code> and the
   * <code>RenderResponse</code> objects using the passed HttpServletRequest and
   * HttpServletResponse objects. Sets the render request parameters and invokes the <code>render</code>
   * method of the portlet defined by the <code>portletNode</code> object.
   * 
   * @param request the servlet request object that contains the request the client has made.
   * @param response the servlet response object that contains the response the portlet sends to the client.
   * @param portletNode the <code>IPortletNode</code> object that specified the portlet to be invoked.
   * @throws IOException - if the streaming causes an I/O problem.
   * @throws PortletException - if the portlet has problems fulfilling the rendering request.
   * @throws PortletSecurityException - if the portlet cannot fullfill this request because of security reasons.
   * @throws UnavailableException - if the portlet is unavailable to perform render at this time.
   */
  public void render(HttpServletRequest request, HttpServletResponse response, IPortletNode portletNode ) 
    throws IOException, PortletException;

}
