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

import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sap.engine.services.portletcontainer.api.IPortletNode;
import com.sap.engine.services.portletcontainer.core.application.PortletApplicationContext;
/**
*
* @author Diyan Yordanov
* @version 7.10
*/
public class RenderRequestImpl extends PortletRequestImpl implements RenderRequest {

  public RenderRequestImpl(HttpServletRequest servletRequest, HttpServletResponse servletResponse,
      IPortletNode portletNode, PortletApplicationContext appCtx) {
    super(servletRequest, servletResponse, portletNode, appCtx, IPortletNode.RENDER_METHOD);
  }

}
