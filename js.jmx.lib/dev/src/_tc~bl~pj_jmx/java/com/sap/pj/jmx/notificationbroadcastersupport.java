package com.sap.pj.jmx;

import com.sap.pj.jmx.server.ListenerInfo;

import javax.management.*;

/**
 * Provides an implementation of the {@link NotificationEmitter NotificationEmitter}
 * interface. This can be used as the super class of an MBean that sends notifications.
 * The difference to javax.management.NotificationBroadcasterSupport is the getListenersCount()
 * method.
 * @author Reinhold Kautzleben
 */
public class NotificationBroadcasterSupport implements NotificationEmitter {

  public final class ListenersArray {
    public ListenerInfo[] listsners;
    public int nextFreePos;
  }
  
  private final Object lock;

  /*
   * Adding and removing listeners is done in a copy of the list of current listeners in
   * order to avoid synchronization of senders. This improves performance of the sendNotification
   * operation and avoids potential deadlocks with listeners that call addNotificationListener.
   */

  private static int INITIAL_SIZE = 10;
  private ListenerInfo[] listeners = new ListenerInfo[INITIAL_SIZE];
  private int nextFreePos = 0;

  /**
   * Returns the object used for synchronization.
   * @return Object
   */
  public Object getLock() {
    return lock;
  }

  /**
   * Constructor for NotificationBroadcasterSupport.
   */
  public NotificationBroadcasterSupport(Object lock) {
    this.lock = (lock == null) ? this : lock;
  }

  /**
   * Constructor for NotificationBroadcasterSupport.
   */
  public NotificationBroadcasterSupport() {
    this.lock = this;
  }

  /**
   * @see javax.management.NotificationBroadcaster#addNotificationListener(NotificationListener, NotificationFilter, Object)
   */
  public void addNotificationListener(
          NotificationListener listener,
          NotificationFilter filter,
          Object handback) {
    if (listener == null) {
      throw new IllegalArgumentException("Argument 'listener' cannot be null.");
    }
    synchronized (lock) {
      // filling elements to the end of an existing array is thread-safe
      // therefore, we copy only if necessary
      if (nextFreePos >= listeners.length) {
        ListenerInfo[] newListeners = new ListenerInfo[listeners.length + INITIAL_SIZE];
        System.arraycopy(listeners, 0, newListeners, 0, listeners.length);
        newListeners[nextFreePos++] = new ListenerInfo(listener, filter, handback);
        listeners = newListeners;
      } else {
        listeners[nextFreePos++] = new ListenerInfo(listener, filter, handback);
      }
    }
  }

  /**
   * @see javax.management.NotificationBroadcaster#removeNotificationListener(NotificationListener)
   */
  public void removeNotificationListener(NotificationListener listener)
          throws ListenerNotFoundException {
    synchronized (lock) {
      int nbFound = 0;
      int firstIndex = -1;
      int i = 0;
      // find first occurrence
      for (; i < nextFreePos; i++) {
        if (listeners[i].listener == listener) {
          firstIndex = i++;
          nbFound++;
          break;
        }
      }
      // count remaining occurrences
      for (; i < nextFreePos; i++) {
        if (listeners[i].listener == listener) {
          nbFound++;
        }
      }
      // remove and resize
      if (nbFound > 0) {
        ListenerInfo[] newListeners =
                new ListenerInfo[Math.min(nextFreePos - nbFound + INITIAL_SIZE, nextFreePos)];
        System.arraycopy(listeners, 0, newListeners, 0, firstIndex);
        int j = firstIndex;
        for (int k = firstIndex + 1; k < nextFreePos; k++) {
          if (listeners[k].listener == listener) {
          } else {
            newListeners[j++] = listeners[k];
          }
        }
        nextFreePos = j;
        listeners = newListeners;
      } else {
        throw new ListenerNotFoundException("Listener not found");
      }
    }
  }

  /**
   * @see javax.management.NotificationEmitter#removeNotificationListener(NotificationListener, NotificationFilter, Object)
   */
  public void removeNotificationListener(
          NotificationListener listener,
          NotificationFilter filter,
          Object handback)
          throws ListenerNotFoundException {
    synchronized (lock) {
      boolean listenerFound = false;
      boolean tripleFound = false;
      ListenerInfo[] newListeners = new ListenerInfo[nextFreePos];
      int i = 0;
      int j = 0;
      // find first matching listener
      for (; i < nextFreePos; i++) {
        if (listeners[i].listener == listener) {
          listenerFound = true;
          break;
        } else {
          newListeners[j++] = listeners[i];
        }
      }
      // find and skip matching triple
      for (; i < nextFreePos; i++) {
        if (listeners[i].listener == listener
                && listeners[i].filter == filter
                && listeners[i].handback == handback) {
          tripleFound = true;
          i++; // skip found item
          break;
        } else {
          newListeners[j++] = listeners[i];
        }
      }
      // copy tail
      for (; i < nextFreePos; i++) {
        newListeners[j++] = listeners[i];
      }
      listeners = newListeners;
      nextFreePos = j;
      if (!tripleFound) {
        if (listenerFound) {
          throw new ListenerNotFoundException("Listener with given filter and handback not found");
        } else {
          throw new ListenerNotFoundException("Listener not found");
        }
      }
    }
  }

  /**
   * Returns the number of currently registered listeners.
   * @return int
   */
  public int getListenersCount() {
    return nextFreePos;
  }

  /**
   * @see javax.management.NotificationBroadcaster#getNotificationInfo()
   */
  public MBeanNotificationInfo[] getNotificationInfo() {
    return new MBeanNotificationInfo[0];
  }

  /**
   * 
   */
  public ListenersArray getCurrentListeners() {
    ListenersArray listenersCopy = new ListenersArray();
    synchronized (lock) {
      listenersCopy.listsners = listeners;
      listenersCopy.nextFreePos = nextFreePos;
    }
    return listenersCopy;
  }

  /**
   * Sends a notification.
   * @param notification The notification to send.
   */
  public void sendNotification(Notification notification) {
    if (notification == null) {
      return;
    }
    ListenersArray currListeners = getCurrentListeners();
    for (int i = 0; i < currListeners.nextFreePos; i++) {
      if (currListeners.listsners[i].filter == null
              || currListeners.listsners[i].filter.isNotificationEnabled(notification)) {
        currListeners.listsners[i].listener.handleNotification(notification, currListeners.listsners[i].handback);
      }
    }
  }

}
