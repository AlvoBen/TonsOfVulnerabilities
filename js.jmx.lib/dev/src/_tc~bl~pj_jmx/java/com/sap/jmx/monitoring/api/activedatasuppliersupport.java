package com.sap.jmx.monitoring.api;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;

/**
 * The <code>ActiveDatasupplierSupport</code> class can be used as super class of own 
 * resource mbean implementations. If this would conflict with an existing derivation
 * use the delegation pattern (<code>MyResource</code> delegates the listener 
 * administration to a <code>ActiveDatasupplierSupport</code> object). 
 */
public class ActiveDatasupplierSupport implements ActiveDatasupplier
{
	private Hashtable listeners = new Hashtable();

  /**
   * @see javax.management.NotificationBroadcaster#addNotificationListener(NotificationListener, NotificationFilter, Object)
   */
  public void addNotificationListener(
    NotificationListener listener,
    NotificationFilter filter,
    Object handback)
    throws IllegalArgumentException
  {
    if (!(handback instanceof String))
    {
      throw new IllegalArgumentException("Handback is not a String.");
    }
    else
    {
      addNotificationListener(listener, (String) handback);
    }
  }

  /**
   * @see com.sap.jmx.monitoring.api.ActiveDatasupplier#addNotificationListener(NotificationListener, String)
   */
  public void addNotificationListener(NotificationListener listener, String mbeanType)
    throws IllegalArgumentException
  {
    if (listener == null)
    {
      throw new IllegalArgumentException("Listener is null.");
    }
    
    if (mbeanType == null)
    {
      throw new IllegalArgumentException("MBeanType is null.");
    }
    
    Vector typedListeners = (Vector) listeners.get(mbeanType);
    if (typedListeners == null)
    {
      typedListeners = new Vector();
      typedListeners.add(listener);
      listeners.put(mbeanType, typedListeners);
    }
    else
    {
      typedListeners.add(listener);
    }
  }

  /**
   * @see javax.management.NotificationEmitter#removeNotificationListener(NotificationListener, NotificationFilter, Object)
   */
  public void removeNotificationListener(
    NotificationListener listener,
    NotificationFilter filter,
    Object handback)
    throws ListenerNotFoundException
  {
    if (!(handback instanceof String))
    {
      throw new ListenerNotFoundException("Handback is not a String.");
    }
    else
    {
      removeNotificationListener(listener, (String) handback);
    }
  }

  /**
   * @see javax.management.NotificationBroadcaster#removeNotificationListener(NotificationListener)
   */
  public void removeNotificationListener(NotificationListener listener)
    throws ListenerNotFoundException
  {
    removeNotificationListener(listener, null, null);
  }
  
  /**
   * @see com.sap.jmx.monitoring.api.ActiveDatasupplier#removeNotificationListener(NotificationListener, String)
   */
  public void removeNotificationListener(
    NotificationListener listener,
    String mbeanType)
    throws ListenerNotFoundException
  {
    final Vector typedListeners = (Vector) listeners.get(mbeanType);
    if (typedListeners == null)
    {
      throw new ListenerNotFoundException("Listener not registered under " + mbeanType + ".");
    }
    else
    {
      if (!typedListeners.remove(listener))
      {
        throw new ListenerNotFoundException("Listener not registered under " + mbeanType + ".");
      }
      /* Maybe the typedListeners are empty now. Nevertheless do not remove the type. 
       * Because of saveNotificationRegistration in J2EEComponentAgent it is very likely
       * that the type will soon be registered again. */
    }
  }

  /**
   * @see javax.management.NotificationBroadcaster#getNotificationInfo()
   */
  public MBeanNotificationInfo[] getNotificationInfo()
  {
    return null;
  }

  /**
   * Sends the given notification to all registered listeners.
   * @param notification the notification to be sent.
   */
  public void sendNotification(Notification notification)
  {
  	if (notification == null)
  	{
  		return;
  	}

    final String mbeanType = notification.getClass().getName();
    
    final Vector typedListeners = (Vector) listeners.get(mbeanType);
    if (typedListeners == null)
    {
      return;
    }

  	final Iterator iter = typedListeners.iterator();
  	while (iter.hasNext())
    {
      final NotificationListener listener = (NotificationListener) iter.next();
      listener.handleNotification(notification, null);
    }
  }  
}
