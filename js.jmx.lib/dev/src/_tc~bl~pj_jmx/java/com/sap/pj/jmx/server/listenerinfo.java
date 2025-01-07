package com.sap.pj.jmx.server;

import javax.management.NotificationFilter;
import javax.management.NotificationListener;

/**
 * @author Reinhold Kautzleben
 * @version 1.0
 */
public class ListenerInfo {

  public NotificationListener listener;
  public NotificationFilter filter;
  public Object handback;

  public ListenerInfo(
          NotificationListener listener,
          NotificationFilter filter,
          Object handback) {
    this.listener = listener;
    this.filter = filter;
    this.handback = handback;
  }
}
