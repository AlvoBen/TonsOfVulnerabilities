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
import com.sap.engine.mejb.ManagementProxy;

import javax.management.*;
import java.rmi.RemoteException;

/**
 * Date: 2004-6-11
 * Implementation of ListenerConnectionServer
 * 
 * @author Nikolai Angelov
 * @see javax.management.j2ee.ListenerRegistration
 * @see ListenerConnectionServer
 */
public class ListenerConnectionServerImpl implements ListenerConnectionServer {
  HashMapIntObject idHandbackMap = null;
  private ManagementProxy managementProxy;

  public ListenerConnectionServerImpl(ManagementProxy managementProxy) {
    this.managementProxy = managementProxy;
    idHandbackMap = new HashMapIntObject();
  }

  /**
   * Add a listener to a registered managed object.
   * Similar to javax.management.j2ee.ListenerRegistration#addNotificationListener
   * but NotificationListenerRemoteImpl is required as parameter instead of NotificationListener
   * 
   * @param name   
   * @param id     
   * @param filter 
   * @param id     
   * @throws InstanceNotFoundException 
   * @throws RemoteException           
   * @see javax.management.j2ee.ListenerRegistration#addNotificationListener(javax.management.ObjectName, javax.management.NotificationListener, javax.management.NotificationFilter, java.lang.Object)
   */
  public void addNotificationListener(ObjectName name, int id, NotificationListenerRemote listenerRemote, NotificationFilter filter) throws InstanceNotFoundException, RemoteException {
    IDHolder handback = new IDHolder(id);
    managementProxy.addNotificationListener(name, listenerRemote, filter, handback);
    idHandbackMap.put(id, handback);
  }

  /**
   * Enables to remove a listener from a registered managed object.
   * Similar to javax.management.j2ee.ListenerRegistration#removeNotificationListener
   * 
   * @param name 
   * @throws InstanceNotFoundException 
   * @throws ListenerNotFoundException 
   * @throws RemoteException           
   * @see javax.management.j2ee.ListenerRegistration#removeNotificationListener(javax.management.ObjectName, javax.management.NotificationListener)
   */
  public void removeNotificationListener(ObjectName name, int id) throws InstanceNotFoundException, ListenerNotFoundException, RemoteException {
    IDHolder handback = (IDHolder) idHandbackMap.get(id);
    NotificationListener listener = handback.getListener();
    managementProxy.removeNotificationListener(name, listener);
    idHandbackMap.remove(id);
  }

  public void removeNotificationListener(ObjectName name, int[] ids) throws InstanceNotFoundException, ListenerNotFoundException, RemoteException {
    for (int i = 0; i < ids.length; i++) {
      int id = ids[i];
      removeNotificationListener(name, id);
    }
  }

}
