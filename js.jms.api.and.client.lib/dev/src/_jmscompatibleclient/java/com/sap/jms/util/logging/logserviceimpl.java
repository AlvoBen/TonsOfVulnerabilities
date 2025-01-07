/*
 * LogServiceImpl.java
 *
 * Property of SAP AG
 * (c) Copyright SAP AG, 2004.
 * All rights reserved.
 */
package com.sap.jms.util.logging;

import java.util.Map;
import com.sap.jms.util.compat.concurrent.ConcurrentHashMap;

import com.sap.jms.JMSConstants;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;

/**
 * @author Sabine Heider
 */
public class LogServiceImpl extends BaseLogService {
  /**
   * The default source code location <code>com.sap.jms</code>
   */
  public static final int JMS_LOCATION = 1;
  private static final String JMS_LOCATION_STRING = "com.sap.jms";
  /**
   * Source code location for the client part <code>com.sap.jms.client</code>
   */
  public static final int CLIENT_LOCATION = 2;
  private static final String CLIENT_LOCATION_STRING = "com.sap.jms.client";
  /**
   * Source code location for the administration part
   * <code>com.sap.jms.admin</code>
   */
  public static final int ADMIN_LOCATION = 3;
  private static final String ADMIN_LOCATION_STRING = "com.sap.jms.admin";
  /**
   * Source code location for the protocol layer
   * <code>com.sap.jms.protocol</code>
   */
  public static final int PROTOCOL_LOCATION = 4;
  private static final String PROTOCOL_LOCATION_STRING = "com.sap.jms.protocol";

  private static Map/*<Integer, LogService>*/ services = new ConcurrentHashMap/*<Integer, LogService>*/();

  //  /**
  //   * Creates a new log service using the Location <code>com.sap.jms</code> and
  //   * the Category <code>/System/Server</code>
  //   */
  //  protected BaseLogService() {
  //    this(JMSConstants.TRACER, JMSConstants.LOGGER);
  //  }
  //
  //  /**
  //   * Creates a new log service using the Location <code>com.sap.jms</code> and
  //   * the given Category.
  //   *
  //   * @param category The Category object to be used for logging
  //   * @see com.sap.tc.logging.Category
  //   */
  //  protected BaseLogService(Category category) {
  //    this(JMSConstants.TRACER, category);
  //  }

  /**
   * Creates a new log service using the given Location and Category objects.
   * @param location The Location object to be used for logging and tracing
   * @param category The Category object to be used for tracing
   * @see com.sap.tc.logging.Location
   * @see com.sap.tc.logging.Category
   */
  protected LogServiceImpl(Location location, Category category) {
    super(location, category);
  }

  /**
   * Gets the log service for the default location <code>JMS_LOCATION</code>
   * (i.e. <code>com.sap.jms</code>).
   * @return The log service for the default location
   */
  public static LogService getLogService() {
    return getLogService(JMS_LOCATION);
  }  
  
  /**
   * Gets the log service for the specified base location. If the log service
   * does not exist, it is instantiated.
   * <p>
   * There are several predefined locations (see the constants defined in this
   * interface), for example <code>CLIENT_LOCATION</code> for the package
   * <code>com.sap.jms.client</code> and all packages below. Use
   * <code>JMS_LOCATION</code> if none of the other predefined locations
   * apply.
   * @param baseLocation Integer constant specifying the base location
   * @return The log service for the specified base location
   * @see #JMS_LOCATION
   * @see #CLIENT_LOCATION
   * @see #ADMIN_LOCATION
   * @see #PROTOCOL_LOCATION
   */
  public static LogService getLogService(int baseLocation) {
    LogService logService = (LogService)services.get(new Integer(baseLocation));
    if (logService == null) {
      Location location = getLocation(baseLocation);
      logService = new LogServiceImpl(location, JMSConstants.LOGGER);
      services.put(new Integer(baseLocation), logService);
    }
    return logService;
  }
  
  /**
   * Creates the log service for the specified base location and the given category. 
   * <p>
   * There are several predefined locations (see the constants defined in this
   * interface), for example <code>CLIENT_LOCATION</code> for the package
   * <code>com.sap.jms.client</code> and all packages below. Use
   * <code>JMS_LOCATION</code> if none of the other predefined locations
   * apply.
   * @param baseLocation Integer constant specifying the base location
   * @param category The Category object to be used for logging
   * @return The log service for the specified base location and the given category
   * @see #JMS_LOCATION
   * @see #CLIENT_LOCATION
   * @see #ADMIN_LOCATION
   * @see #PROTOCOL_LOCATION
   */
  public static LogService getLogService(int baseLocation, Category category) {
      Location location = getLocation(baseLocation);
      if (category == null)
      	category = JMSConstants.LOGGER;
      LogService logService = new LogServiceImpl(location, category);
    return logService;
  }

  /**
   * Gets the location object for the specified base location.
   * @param baseLocation Integer constant specifying the base location
   * @return The location object for the specified base location
   */
  private static Location getLocation(int baseLocation) {
    Location location;
    switch (baseLocation) {
      case JMS_LOCATION:
        location = Location.getLocation(JMS_LOCATION_STRING);
        break;
      case CLIENT_LOCATION:
        location = Location.getLocation(CLIENT_LOCATION_STRING);
        break;
      case ADMIN_LOCATION:
        location = Location.getLocation(ADMIN_LOCATION_STRING);
        break;
      case PROTOCOL_LOCATION:
        location = Location.getLocation(PROTOCOL_LOCATION_STRING);
        break;
      default:
        location = Location.getLocation(JMS_LOCATION_STRING);
        break;
    }
    return location;
  }

}
