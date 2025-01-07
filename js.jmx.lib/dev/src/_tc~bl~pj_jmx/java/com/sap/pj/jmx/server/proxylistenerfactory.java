/*
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * This software is the confidential and proprietary information
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.pj.jmx.server;

import javax.management.NotificationBroadcaster;
import javax.management.NotificationListener;
import javax.management.ObjectName;

/**
 * Interface for creation of proxy listeners which are registered at the NotificationBroadcaster
 * instead of the original NotificationListener. Different implementations allow for example to
 * switch between synchronous or asynchronous delivery of notifications.
 *
 * @author d025700
 *
 */
public interface ProxyListenerFactory {
  /**
   * Method createProxyListener.
   * @param listener The original NotificationListener.
   * @param broadcaster The instance to be observed. This is nedded by the proxy in order to set the source field
   *                    in the Notification.
   * @param name The name of the object to be observed by the listener. The Proxy listener is responsible to set the source
   *             field of all notifications accordingly before they are delivered to the original listener.
   * @return ProxyListener the newly created proxy listener.
   */
  public ProxyListener createListener(
          NotificationListener listener,
          NotificationBroadcaster broadcaster,
          ObjectName name);

}
