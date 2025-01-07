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
package com.sap.engine.services.portletcontainer.container;

import com.sap.engine.services.portletcontainer.PortletContainerInterface;
import com.sap.engine.services.portletcontainer.ServiceContext;
import com.sap.engine.services.portletcontainer.api.IPortletAdmin;
import com.sap.engine.services.portletcontainer.api.IPortletContainer;

/**
 * Implementation of the <code>PortletContainerInterface<code> interface.
 *
 * @author diyan-y
 * @version 7.10
 */
public class PortletContainerInterfaceImpl implements PortletContainerInterface {

  private ServiceContext serviceContext = null;

  /**
   * Creates the <code>PortletContainerInterfaceImpl</code> object.
   * @param serviceContext the Portlet Container service context object.
   */
  public PortletContainerInterfaceImpl(ServiceContext serviceContext) {
    this.serviceContext = serviceContext;
  }

  /**
   * Returns a reference to the implementation of the <code>IPortletAdmin</code>
   * interface that allows the Portal to administrate the portlet applications.
   * @return an implementation of the <code>IPortletAdmin</code> interface.
   *
   * @see com.sap.engine.services.portletcontainer.PortletContainerInterface#getPortletAdmin()
   */
  public IPortletAdmin getPortletAdmin() {
    return serviceContext.getPortletAdmin();
  }

  /**
   * Returns a reference to the implementation of the <code>IPortletContainer</code>
   * interface that allows the Portal to invoke portlets.
   * @return an implementation of the <code>IPortletContainer</code> interface.
   *
   * @see com.sap.engine.services.portletcontainer.PortletContainerInterface#getPortletContainer()
   */
  public IPortletContainer getPortletContainer() {
    return serviceContext.getPortletContainer();
  }

}
