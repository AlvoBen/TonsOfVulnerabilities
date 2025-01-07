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

import javax.management.*;

/**
 * Wrapper to register MBeanServerDelegate and subclasses. Always returns MBeanServerDelegate in
 * MBeanInfo.
 *
 * @author Reinhold Kautzleben
 */
public class MBeanServerDelegateWrapper extends StandardMBean implements NotificationEmitter {

  private static final String CLASS_NAME = MBeanServerDelegate.class.getName();

  /**
   * Constructor for MBeanServerDelegateWrapper.
   * @param implementation
   * @param mbeanInterface
   * @throws NotCompliantMBeanException
   */
  public MBeanServerDelegateWrapper(MBeanServerDelegate implementation)
          throws NotCompliantMBeanException {
    super(implementation, null);
    if (!(implementation instanceof NotificationEmitter)) {
      throw new NotCompliantMBeanException("given MBeanServerDelegate does not implement NotificationEmitter interface");
    }
  }

  /**
   * @see javax.management.StandardMBean#getClassName(MBeanInfo)
   */
  protected String getClassName(MBeanInfo info) {
    return CLASS_NAME;
  }

  /**
   * @see javax.management.NotificationEmitter#removeNotificationListener(NotificationListener, NotificationFilter, Object)
   */
  public void removeNotificationListener(
          NotificationListener listener,
          NotificationFilter filter,
          Object handback)
          throws ListenerNotFoundException {
    ((NotificationEmitter) getImplementation()).removeNotificationListener(
            listener,
            filter,
            handback);
  }

  /**
   * @see javax.management.NotificationBroadcaster#addNotificationListener(NotificationListener, NotificationFilter, Object)
   */
  public void addNotificationListener(
          NotificationListener listener,
          NotificationFilter filter,
          Object handback)
          throws IllegalArgumentException {
    ((NotificationEmitter) getImplementation()).addNotificationListener(listener, filter, handback);
  }

  /**
   * @see javax.management.NotificationBroadcaster#getNotificationInfo()
   */
  public MBeanNotificationInfo[] getNotificationInfo() {
    return ((NotificationEmitter) getImplementation()).getNotificationInfo();
  }

  /**
   * @see javax.management.NotificationBroadcaster#removeNotificationListener(NotificationListener)
   */
  public void removeNotificationListener(NotificationListener listener)
          throws ListenerNotFoundException {
    ((NotificationEmitter) getImplementation()).removeNotificationListener(listener);
  }

  /**
   * @see javax.management.StandardMBean#setImplementation(Object)
   */
  public void setImplementation(Object implementation) throws NotCompliantMBeanException {
    if (!(implementation instanceof NotificationEmitter)
            && !(implementation instanceof MBeanServerDelegate)) {
      throw new NotCompliantMBeanException("given MBeanServerDelegate does not implement NotificationEmitter interface");
    }
    super.setImplementation(implementation);
  }

}
