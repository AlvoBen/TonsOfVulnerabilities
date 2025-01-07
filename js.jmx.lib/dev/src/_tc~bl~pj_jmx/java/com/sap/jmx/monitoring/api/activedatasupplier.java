package com.sap.jmx.monitoring.api;

import javax.management.ListenerNotFoundException;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;

/**
 * Interface implemented by those ResourceMBeans that report their values active. In
 * connection with the component agent all ResourceMBeans have to implement
 * this interface, because there is no support of passive data suppliers.
 */
public interface ActiveDatasupplier extends NotificationEmitter
{
  /**
   * Adds a listener to this resource mbean.
   * @param listener the listener to be registered, may not be null.
   * @throws IllegalArgumentException if the listener is null.
   */
  public void addNotificationListener(NotificationListener listener, String mbeanType) 
    throws IllegalArgumentException;
    
  /**
   * Removes a listener from this resource mbean.
   * @param listener the listener to be removed, may not be null.
   * @throws IllegalArgumentException if the listener is null.
   */
  public void removeNotificationListener(NotificationListener listener, String mbeanType) 
    throws ListenerNotFoundException;
}
