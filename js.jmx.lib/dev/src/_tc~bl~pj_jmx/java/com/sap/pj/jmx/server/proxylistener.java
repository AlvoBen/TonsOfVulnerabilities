package com.sap.pj.jmx.server;

import javax.management.Notification;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationListener;
import javax.management.ObjectName;

/**
 * Default proxy listener that implements synchronous delivery of notifications.
 *
 * @author Reinhold Kautzleben
 */
public class ProxyListener implements NotificationListener {
  protected final NotificationListener listener;
  protected final ObjectName name;
  protected final Object broadcaster;
  private final int hashCode;

  /**
   * Method ProxyListener.
   * @param listener
   * @param mbean
   * @param name
   */
  protected ProxyListener(
          final NotificationListener listener,
          final NotificationBroadcaster broadcaster,
          final ObjectName name) {
    this.listener = listener;
    this.name = name;
    this.broadcaster = broadcaster;
    hashCode = System.identityHashCode(listener) ^ name.hashCode();
  }

  /**
   * @see javax.management.NotificationListener#handleNotification(Notification, Object)
   */
  public void handleNotification(
          Notification notification,
          Object handback) {
    if (notification != null && notification.getSource() == broadcaster) {
      notification.setSource(name);
    }
    listener.handleNotification(notification, handback);
  }

  /**
   * @see java.lang.Object#equals(Object)
   */
  public final boolean equals(Object obj) {
    if (!(obj instanceof ProxyListener)) {
      return false;
    }
    ProxyListener other = (ProxyListener) obj;
    return (
            other.listener == listener
            && (other.name == null && name == null || other.name.equals(name)));
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  public final int hashCode() {
    return hashCode;
  }

}
