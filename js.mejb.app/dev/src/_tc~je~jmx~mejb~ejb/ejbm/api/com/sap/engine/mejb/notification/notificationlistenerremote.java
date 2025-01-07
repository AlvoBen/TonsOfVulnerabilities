/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * This software is the confidential and proprietary information
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.mejb.notification;

import javax.management.NotificationListener;
import javax.management.Notification;
import java.rmi.Remote;

/**
 * This class provides remote stub to the server side 
 * with methods from the NotificationListener interface
 * 
 * @author Nikolai Angelov
 */
public interface NotificationListenerRemote extends NotificationListener, Remote {
  void handleNotification(Notification notification, Object o);
}
