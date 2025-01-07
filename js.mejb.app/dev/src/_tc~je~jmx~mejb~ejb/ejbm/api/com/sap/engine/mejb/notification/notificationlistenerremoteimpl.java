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

import com.sap.engine.lib.util.HashMapIntObject;

import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.Notification;
import javax.management.ObjectName;

/**
 * Date: 2004-6-11
 * 
 * @author Nikolai Angelov
 */
public class NotificationListenerRemoteImpl
  implements NotificationListenerRemote {
  private NotificationListener listener;
  private HashMapIntObject idHandbackMap = null;
  private HashMapIntObject idFilterMap = null;
  private HashMapIntObject idObjectNameMap = null;

  public NotificationListenerRemoteImpl(NotificationListener listener) {
  	this.listener = listener;
    this.idHandbackMap = new HashMapIntObject();
    this.idFilterMap = new HashMapIntObject();
    this.idObjectNameMap = new HashMapIntObject();
  }

  public void setListener(NotificationListener listener) {
    this.listener = listener;
  }

  public void addObjectNameFilterHandback(
    int id,
    ObjectName objectName,
    NotificationFilter filter,
    Object handback) {
    idObjectNameMap.put(id, objectName);
    if (filter != null) {
      idFilterMap.put(id, filter);
    }
    if (handback != null) {
      idHandbackMap.put(id, handback);
    }
  }

  public Object getObjectName(int id) {
    return idObjectNameMap.get(id);
  }

  public Object getHandback(int id) {
    return idHandbackMap.get(id);
  }

  public NotificationFilter getFilter(int id) {
    return (NotificationFilter)idFilterMap.get(id);
  }

  public void remove(int id) {
  	idObjectNameMap.remove(id);
    idHandbackMap.remove(id);
    idFilterMap.remove(id);
  }

  public int[] getAllIds() {
    return idObjectNameMap.getAllKeys();
  }

  public void removeAll() {
  	idObjectNameMap.clear();
    idHandbackMap.clear();
    idFilterMap.clear();
  }

  public void handleNotification(Notification notification, Object o) {
    IDHolder idHolder = (IDHolder)o;
    int id = idHolder.getId();
    Object handback = idHandbackMap.get(id);
    listener.handleNotification(notification, handback);
  }
  
  public boolean isEmpty() {
  	return idObjectNameMap.size() == 0;
  }

}
