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

import javax.management.*;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Date: 2004-6-11
 * Define methods corresponding to once in ListenerRegistration
 * @see javax.management.j2ee.ListenerRegistration
 * @author Nikolai Angelov
 */
public interface ListenerConnectionServer extends Remote {
  
  /** 
   * Add a listener to a registered managed object.
   * Similar to javax.management.j2ee.ListenerRegistration#addNotificationListener 
   * but NotificationListenerRemoteImpl is required as parameter instead of NotificationListener
   * @param name
   * @param listener
   * @param filter
   * @param id
   * @throws InstanceNotFoundException
   * @throws RemoteException
   * @see javax.management.j2ee.ListenerRegistration#addNotificationListener(javax.management.ObjectName, javax.management.NotificationListener, javax.management.NotificationFilter, java.lang.Object)  
   */ 
  void addNotificationListener(ObjectName name, int id, NotificationListenerRemote listener, NotificationFilter filter) throws InstanceNotFoundException, RemoteException;

  /**
   * Enables to remove a listener record from a registered managed object. 
   * Similar to javax.management.j2ee.ListenerRegistration#removeNotificationListener
   * @param name
   * @param id
   * @throws InstanceNotFoundException
   * @throws ListenerNotFoundException
   * @throws RemoteException
   * @see javax.management.j2ee.ListenerRegistration#removeNotificationListener(javax.management.ObjectName, javax.management.NotificationListener) 
   */ 
  void removeNotificationListener(ObjectName name, int id) throws InstanceNotFoundException, ListenerNotFoundException, RemoteException;
  
  /**
   * Enables to remove a listener from a registered managed object. 
   * Similar to javax.management.j2ee.ListenerRegistration#removeNotificationListener
   * @param name
   * @param ids
   * @throws InstanceNotFoundException
   * @throws ListenerNotFoundException
   * @throws RemoteException
   * @see javax.management.j2ee.ListenerRegistration#removeNotificationListener(javax.management.ObjectName, javax.management.NotificationListener) 
   */ 
  void removeNotificationListener(ObjectName name, int[] ids) throws InstanceNotFoundException, ListenerNotFoundException, RemoteException;
  
}
