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
package com.sap.engine.services.portletcontainer.container;

import com.sap.engine.lib.util.ConcurrentReadHashMap;
import com.sap.engine.services.portletcontainer.core.application.PortletApplicationContext;
import com.sap.engine.services.servlets_jsp.webcontainer_api.container.IWebContainerDeploy;
import com.sap.engine.services.servlets_jsp.webcontainer_api.container.IWebContainerLifecycle;
import com.sap.engine.services.servlets_jsp.webcontainer_api.extension.IWebContainerExtension;
import com.sap.engine.services.servlets_jsp.webcontainer_api.extension.IWebContainerExtensionContext;

/**
 * The <code>PortletContainerExtension<code> class is the object that creates 
 * the Portlet Container as Web Container Extension. It brings life cycle and 
 * deployment handlers.
 * 
 * @author Diyan Yordanov
 * @version 7.10
 */
public class PortletContainerExtension implements IWebContainerExtension {
  private IWebContainerExtensionContext portletContainerExtensionContext = null;
  private PortletContainerDeployHandler portletContainerDeployHandler = null;
  private PortletContainerLifecycleHandler portletContainerLifecycleHandler = null;
  private static ConcurrentReadHashMap startedPortletApplications = new ConcurrentReadHashMap();

  /**
   * Creates the PortletContainerExtension
   */
  public PortletContainerExtension() {
    portletContainerDeployHandler = new PortletContainerDeployHandler();
    portletContainerLifecycleHandler = new PortletContainerLifecycleHandler(this);
  }

  /**
   * Returns the handler for all web container modules that have a life cycle to 
   * be used by the Web Container.
   * @return  the handler for all web container modules that have a life cycle to 
   * be used by the Web Container.
   * 
   * @see com.sap.engine.services.servlets_jsp.webcontainer_api.extension.IWebContainerExtension#getWebDeployHandler()
   */
  public IWebContainerDeploy getWebDeployHandler() {
    return portletContainerDeployHandler;
  }

  /**
   * Returns an implementation of the life cycle handler.
   * @return an implementation of the life cycle handler.
   * 
   * @see com.sap.engine.services.servlets_jsp.webcontainer_api.extension.IWebContainerExtension#getWebLifecycleHandler()
   */
  public IWebContainerLifecycle getWebLifecycleHandler() {
    return portletContainerLifecycleHandler;
  }

  /**
   * Called to initialize the portlet container extension and to pass on a 
   * call-back interface from the web container.
   * @param webContainerExtensionContext the web container extension context. 
   * 
   * @see com.sap.engine.services.servlets_jsp.webcontainer_api.extension.IWebContainerExtension#init(com.sap.engine.services.servlets_jsp.webcontainer_api.extension.IWebContainerExtensionContext)
   */
  public void init(IWebContainerExtensionContext webContainerExtensionContext) {
    this.portletContainerExtensionContext = webContainerExtensionContext;
  }

  /**
   * Destroys the portlet container extension.
   * 
   * @see com.sap.engine.services.servlets_jsp.webcontainer_api.extension.IWebContainerExtension#destroy()
   */
  public void destroy() {
    portletContainerExtensionContext = null;
  }

  /**
   * Returns the <code>WebContainerExtensionContext</code> object which is the 
   * call-back interface from the web container.
   * @return the <code>WebContainerExtensionContext</code> object which is the 
   * call-back interface from the web container.
   */
  public IWebContainerExtensionContext getPortletContainerExtensionContext() {
    return portletContainerExtensionContext;
  }
  
  /**
   * Invoked to store the <code>PortletApplicationContext</code> of the started 
   * portlet application.
   * @param portletApplicationName name of the portlet application.
   * @param portletApplicationContext the PortletApplicationContext.
   */
  public void portletApplicationStarted(String portletApplicationName, PortletApplicationContext portletApplicationContext) {
    startedPortletApplications.put(portletApplicationName, portletApplicationContext);
  }

  /**
   * Invoked to remove the <code>PortletApplicationContext</code> of specified
   * portlet application that is no more available.
   * @param portletApplicationName the name of the portlet application.
   */
  public void removeStartedApplication(String portletApplicationName) {
    startedPortletApplications.remove(portletApplicationName);
  }
  
  /**
   * Returns the <code>PortletApplicationContext</code> for the specified portlet
   * application.
   * @param portletApplicationName the name of the portlet application.
   * @return the <code>PortletApplicationContext</code>, or null if the application
   * is not available.
   */
  public PortletApplicationContext getPortletApplicationContext(String portletApplicationName) {
    return (PortletApplicationContext)startedPortletApplications.get(portletApplicationName);
  }
}