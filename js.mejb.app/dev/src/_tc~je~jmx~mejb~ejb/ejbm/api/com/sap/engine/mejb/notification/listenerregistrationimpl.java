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

import com.sap.engine.lib.util.HashMapObjectObject;

import javax.management.*;
import javax.management.j2ee.ListenerRegistration;
import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * Date: 2004-6-11
 * This class is returned by the MEJB getListenerRegistry() method to the MEJB client side.
 * Holds the listener to the ListenerConnectionServerImpl that is substituted by it's remote Stub 
 * when this object is returned as a result of a remote call 
 * Implements addNotificationListener() in a way that for NotificationListener passed as a parameter 
 * NotificationListenerRemoteImpl object is created and registered in listenerConnector.
 * @author Nikolai Angelov
 * @see ListenerRegistration
 * @see ListenerConnectionServerImpl
 */
public class ListenerRegistrationImpl
  implements Serializable, ListenerRegistration {
  private ListenerConnectionServer listenerConnector = null; //$JL-SER$
  private HashMapObjectObject listenersMap = null;
  private int idCounter = 0;

  public ListenerRegistrationImpl(ListenerConnectionServer listenerConnector) {
    this.listenerConnector = listenerConnector;
    listenersMap = new HashMapObjectObject();
  }

  public void addNotificationListener(
    ObjectName name,
    NotificationListener listener,
    NotificationFilter filter,
    Object handback)
    throws InstanceNotFoundException, RemoteException {
    int id = generateId();
    Object o = listenersMap.get(listener);
    if (o != null) {
      NotificationListenerRemoteImpl listenerRemote =
        (NotificationListenerRemoteImpl)o;
      listenerRemote.addObjectNameFilterHandback(id, name, filter, handback);
      // if the same NotificationListener is added with different filter and handback	  
      listenerConnector.addNotificationListener(
        name,
        id,
        listenerRemote,
        filter);
    } else {
      NotificationListenerRemoteImpl listenerRemote =
        new NotificationListenerRemoteImpl(listener);
      listenerRemote.addObjectNameFilterHandback(id, name, filter, handback);
      listenersMap.put(listener, listenerRemote);
      listenerConnector.addNotificationListener(
        name,
        id,
        listenerRemote,
        filter);
    }
  }

  public void removeNotificationListener(
    ObjectName name,
    NotificationListener listener)
    throws InstanceNotFoundException, ListenerNotFoundException, RemoteException {
    NotificationListenerRemoteImpl listenerRemote =
      (NotificationListenerRemoteImpl)listenersMap.get(listener);
    int[] ids = listenerRemote.getAllIds();
    int[] filteredIds = filterIds(ids, name, listenerRemote);
    // remove the ids from the server
    listenerConnector.removeNotificationListener(name, filteredIds);
    // clear the ListenerRemoteImpl tables
    for (int i = 0; i < filteredIds.length; i++) {
      int id = filteredIds[i];
      listenerRemote.remove(id);
    }
    if (listenerRemote.isEmpty()) {
      listenersMap.remove(listener);
    }
  }

  private int[] filterIds(
    int[] ids,
    ObjectName name,
    NotificationListenerRemoteImpl listenerRemote) {
    // filter the ids for the same ObjectName
    int[] filteredIdsTemp = new int[ids.length];
    int filteredIndex = 0;
    for (int i = 0; i < ids.length; i++) {
      int id = ids[i];
      if (name.equals(listenerRemote.getObjectName(id))) {
        filteredIdsTemp[filteredIndex] = id;
        filteredIndex++;
      }
    }
    int[] filteredIds = new int[filteredIndex];
    System.arraycopy(filteredIdsTemp, 0, filteredIds, 0, filteredIndex);
    return filteredIds;
  }

  public void removeNotificationListener(
    ObjectName name,
    NotificationListener listener,
    NotificationFilter filter,
    Object handback)
    throws InstanceNotFoundException, ListenerNotFoundException, RemoteException {
    NotificationListenerRemoteImpl listenerRemote =
      (NotificationListenerRemoteImpl)listenersMap.get(listener);
    int[] ids = listenerRemote.getAllIds();
    if (handback != null) {
      // if handback != null - find and remove id with the same handback
      for (int i = 0; i < ids.length; i++) {
        int id = ids[i];
        Object storedHandback = listenerRemote.getHandback(id);
        if (storedHandback == handback) {
          listenerConnector.removeNotificationListener(name, id);
          listenerRemote.remove(id);
          break;
        }
      }
    } else if (filter != null) {
      // if filter != null - find and remove id with the same filter
      for (int i = 0; i < ids.length; i++) {
        int id = ids[i];
        NotificationFilter storedFilter = listenerRemote.getFilter(id);
        if (storedFilter == filter) {
          listenerConnector.removeNotificationListener(name, id);
          listenerRemote.remove(id);
          break;
        }
      }

    }
    if (listenerRemote.isEmpty()) {
      listenersMap.remove(listener);
    }
  }

  private synchronized int generateId() {
    int result = idCounter;
    idCounter++;
    return result;
  }

}
