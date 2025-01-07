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


import java.util.Map;

/**
 * The <code>PortletURL</code> interface allows the Portal Application to get
 * the <code>PortletMode</code> and <code>WindowState</code> and the URL parameters
 * of a <code>PortletURL</code> instance and to be able to create a valid URL for
 * the portlet to include it in its content.
 *
 * @author Diyan Yordanov
 * @version 7.12
 * @since 2.0
 */
public interface PortletURL extends PortletConsumerURL{

  public final String ACTION_TYPE = "ACTION";
  public final String RENDER_TYPE = "RENDER";


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
   * Returns public render parameters map associated with this Portlet URL
   * The public render parameters are defined in the portlet deployment descriptor.
   * The keys are the name of the parameter
   * The values are String[] of the parameter's values
   * @return render parameters map associated with this Portlet URL
   */
  public Map<String, String[]> getPublicRenderParametersMap();



}
