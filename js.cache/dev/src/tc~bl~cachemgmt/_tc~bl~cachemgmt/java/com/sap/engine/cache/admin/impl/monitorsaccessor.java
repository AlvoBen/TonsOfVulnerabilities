package com.sap.engine.cache.admin.impl;

import com.sap.engine.cache.admin.Monitor;

import java.util.Map;
import java.util.Hashtable;
import java.util.Collection;

/**
 * @author Petev, Petio, i024139
 */
public class MonitorsAccessor {

  private static Map monitors = new Hashtable();

  public static Collection getMonitors() {
    synchronized (monitors) {
      return (monitors.values());
    }
  }

  public static void setMonitors(Map monitors) {
    MonitorsAccessor.monitors = monitors;
  }

  public static Monitor getMonitor(String region) {
    return (Monitor) monitors.get(region);
  }

  public static void addMonitor(Monitor monitor) {
    synchronized (monitors) {
      monitors.put(monitor.name(), monitor);
    }
  }

  public static void removeMonitor(Monitor monitor) {
    synchronized (monitors) {
      monitors.remove(monitor);
    }
  }


}
