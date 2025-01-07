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
package com.sap.jmx.notification;
import javax.management.NotificationFilter;

/**
 * Tagging interface for NotificationFilters that are interested in
 * MBeanServerNotifications about MBeans on the local MBeanServer only.
 * 
 * @author d025700
 *
 */
public interface LocalMBeanServerNotificationFilter {

  /** 
   * An instance of LocalMBeanServerNotificationFilter that always 
   * returns always.
   */
  public static final NotificationFilter NULL_FILTER = new NullFilter();
}
