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

import java.io.Serializable;

import javax.management.Notification;
import javax.management.NotificationFilter;

/**
 * Returns always true.
 * 
 * @author d025700
 *
 */
final class NullFilter
  implements LocalMBeanServerNotificationFilter, NotificationFilter, Serializable {

  /**
   * @see javax.management.NotificationFilter#isNotificationEnabled(javax.management.Notification)
   */
  public boolean isNotificationEnabled(Notification notfication) {
    return true;
  }

}
