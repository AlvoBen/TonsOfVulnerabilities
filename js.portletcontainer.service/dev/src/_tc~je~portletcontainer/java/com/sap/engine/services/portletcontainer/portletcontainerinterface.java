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
package com.sap.engine.services.portletcontainer;

import com.sap.engine.services.portletcontainer.api.IPortletAdmin;
import com.sap.engine.services.portletcontainer.api.IPortletContainer;

/**
 * This is the runtime interface that the Portlet Container registers to be used 
 * by the Portal application. It has two methods which provide access to the main 
 * interfaces provided by the Portlet Container.
 * 
 * @author diyan-y
 * @version 7.10
 */
public interface PortletContainerInterface {

  /**
   * Returns a reference to the implementation of the <code>IPortletAdmin</code> 
   * interface that allows the Portal to administrate the portlet applications.
   * @return an implementation of the <code>IPortletAdmin</code> interface.
   */
  public IPortletAdmin getPortletAdmin();

  /**
   * Returns a reference to the implementation of the <code>IPortletContainer</code> 
   * interface that allows the Portal to invoke portlets.
   * @return an implementation of the <code>IPortletContainer</code> interface.
   */
  public IPortletContainer getPortletContainer();
}
