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
package com.sap.engine.services.portletcontainer.core.application;

import com.sap.engine.services.portletcontainer.LogContext;
import com.sap.engine.services.servlets_jsp.webcontainer_api.request.IDispatchHandler;
import com.sap.tc.logging.Location;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * This is a <code>IDispatchHandler<code> object that is started to destroy all portlet
 * application components in the correct application context.
 * 
 * @author Diyan Yordanov
 * @version 7.10
 */
public class PortletApplicationDestroyer implements IDispatchHandler {
  private static final String NA_METHOD_MSG = "This operation is not applicable.";

  private Location currentLocation = Location.getLocation(getClass());

  private PortletApplicationContext portletApplicationContext = null;

  /**
   * Creates new <code>PortletApplicationDestroyer</code> object to destroy the 
   * portlet application specified by the <code>portletApplicationContext</code>.
   * @param portletApplicationContext the <code>PortletApplicationContext<code>
   * of the destroyed portlet application.
   */
  public PortletApplicationDestroyer(PortletApplicationContext portletApplicationContext) {
    this.portletApplicationContext = portletApplicationContext;
  }

  public void service(ServletRequest servletRequest, ServletResponse servletResponse) {
    LogContext.getCategory(LogContext.CATEGORY_REQUESTS).logWarning(currentLocation, "ASJ.portlet.000038", "{0}", new Object[]{NA_METHOD_MSG}, null, null);
  }

  /**
   * The service method of this process.
   * There is guarantee that before calling this method the application environment will be switch on to the correct one and
   * after this the application environment will be switch off. 
   */
  public void service() {
    Thread currentThread = Thread.currentThread();
    ClassLoader threadLoader = currentThread.getContextClassLoader();
//    ResourceContext resourceContext = null;
    //SessionEnumeration enumeration = scf.getSessionServletContext().getSession().enumerateSessions();
    try {
      currentThread.setContextClassLoader(portletApplicationContext.getClassLoader());
      LogContext.getLocation(LogContext.LOCATION_DEPLOY).traceInfo("Start destroying portlet components [" + portletApplicationContext.getPortletModuleName() + "]!");
//      resourceContext = scf.enterResourceContext();
      LogContext.getLocation(LogContext.LOCATION_DEPLOY).traceInfo("Start destroying portlets ... [" + portletApplicationContext.getPortletModuleName() + "]!");
      portletApplicationContext.getPortletComponents().destroyPortlets();
      LogContext.getLocation(LogContext.LOCATION_DEPLOY).traceInfo("Portlets successfully destroyed [" + portletApplicationContext.getPortletModuleName() + "]!");
      LogContext.getLocation(LogContext.LOCATION_DEPLOY).traceInfo("Portlet components destroyed [" + portletApplicationContext.getPortletModuleName() + "]");
    } finally {
      try {
        currentThread.setContextClassLoader(threadLoader);
        //scf.exitResourceContext(resourceContext);
      } catch (Exception e) {
        LogContext.getCategory(LogContext.CATEGORY_DEPLOY).logWarning(currentLocation, "ASJ.portlet.000039", "Error in finalizing resources in starting portlet application [{0}].", new Object[]{portletApplicationContext.getPortletModuleName()}, e, null, null);
      }
    }


  }
}
