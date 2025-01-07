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
 * Creates proxy listeners which delegate the handleNotification request synchronously to the
 * original listener.
 *
 * @author d025700
 */
public class SynchronousProxyListenerFactory implements ProxyListenerFactory {

  /**
   * @see com.sap.pj.jmx.server.ProxyListenerFactory#createListener(NotificationListener, NotificationBroadcaster, ObjectName)
   */
  public ProxyListener createListener(
          NotificationListener listener,
          NotificationBroadcaster broadcaster,
          ObjectName name) {
    return new ProxyListener(listener, broadcaster, name);
  }

}
