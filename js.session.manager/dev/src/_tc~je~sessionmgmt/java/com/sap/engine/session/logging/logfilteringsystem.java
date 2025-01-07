/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session.logging;

import java.util.HashMap;

/**
 * Manages the log filters
 *
 * @author Nikolai Neichev
 */
public class LogFilteringSystem {

    // all filters store
  private static HashMap<String, LogFilter> filters = new HashMap<String, LogFilter>();

  /**
   * Registers a filter implementation
   * @param filter the filter impl
   */
  public static void registerFilter(LogFilter filter) {
    filters.put(filter.getName(), filter);
  }

  /**
   * Unregisters a filter implementation
   * @param name the filter name
   */
  public static void unregisterFilter(String name) {
    filters.remove(name);
  }

  /**
   * Unregisters a filter implementation
   * @param filter the filter impl
   */
  public static void unregisterFilter(LogFilter filter) {
    unregisterFilter(filter.getName());
  }

  /**
   * Gets a filter by name, creates an empty filter if there is no such filter registered
   * @param name the name
   * @return the log filter, null if not set
   */
  public static LogFilter getFilter(String name) {
    LogFilter filter = filters.get(name);
    if (filter == null) {
      filter = new EmptyLogFilter(); 
      filters.put(name, filter);
    }
    return filter;
  }

  /**
   * Gets all the filter names
   * @return the registered filter names
   */
  public static HashMap<String, LogFilter> getFilters() {
    return filters;
  }

  /**
   * Checks if there the filter applies
   *
   * @param filterName the filter name
   * @param info the info object
   * @return TRUE if the fog should be logged, FALSE if filteres
   */
  public static boolean toLog(String filterName, Object info) {
    LogFilter filter = filters.get(filterName);
    return (filter == null) || (filter.toLog(info));
  }

}