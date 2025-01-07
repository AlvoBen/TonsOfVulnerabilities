/**
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.memory.server;

import java.util.*;
import java.text.*;

import com.sap.localization.ResourceAccessor;
import com.sap.localization.LocalizableTextFormatter;
import com.sap.localization.LocalizationException;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.Category;

/**
 * Provides methods for convinient work with
 * the messages in the resource bundle
 */
public class MemoryResourceAccessor extends ResourceAccessor {

  /**
   * Default part of the key name in the resource bundle
   */
  public static final String DEFAULT_KEY = "memory_";

  /**
   * The name of the bundle to use
   */
  private static String BUNDLE_NAME = "com.sap.engine.services.memory.MemoryResourceBundle";

  private static final Location location = Location.getLocation(MemoryResourceAccessor.class);
  private static final Category category = Category.SYS_SERVER;

  /**
   * IDs of the keys in the resource bundle
   */
  public static final int MEMORY_SERVICE_STARTED               = 1;
  public static final int MEMORY_SERVICE_STOPPED               = 2;
  public static final int MEMORY_STARTUP_EXCEPTION             = 3;
  public static final int FREEING_MEMORY                       = 4;
  public static final int LEVEL_MEMORY                         = 5;
  public static final int INCREASING_LEVEL                     = 6;
  public static final int DECREASING_LEVEL                     = 7;
  public static final int MEMORY_MANAGER_STOPPED               = 8;
  public static final int CANT_START_MEMORY_MANAGER            = 9;
  public static final int CANT_PARSE_MEMORY_MANAGER_PROPERTY   = 10;
  public static final int MEMORY_MANAGER_PROPERTY_NEGATIVE     = 11;
  public static final int MEMORY_MANAGER_STARTED               = 12;
  public static final int MEMORY_LEVELS_NUMBER_INVALID         = 13;
  public static final int MEMORY_LEVELS_PROPERTY_INVALID       = 14;
  public static final int MEMORY_LEVELS_NOT_PROPER_BYTE_VALUE  = 15;
  public static final int MEMORY_LEVELS_NOT_IN_INCREASING_ORDER= 16;
  public static final int MEMORY_LEVELS_HAS_INCORRECT_FORMAT   = 17;
  public static final int SLEEP_TIMES_NUMBER_INVALID           = 18;
  public static final int SLEEP_TIMES_HAS_NEGATIVE_VALUE       = 19;
  public static final int SLEEP_TIMES_NOT_PROPER_LONG_VALUE    = 20;
  public static final int SLEEP_TIMES_NOT_DECREASING           = 21;
  public static final int SLEEP_TIMES_HAS_INCORRECT_FORMAT     = 22;
  public static final int MAXMEMORY_INVALID_FORMAT             = 23;
  public static final int ERROR_LOCALIZING_KEY                 = 50;
  public static final int ERROR_LOCALIZING_ID                  = 51;

  /**
   * Resource Accessor used to obtain strings and localize text
   */
  public static ResourceAccessor resourceAccessor = new MemoryResourceAccessor();

  /**
   * Nubmer formatter for the key IDs
   */
  private static NumberFormat nf = NumberFormat.getInstance();
  static {
    nf.setMinimumIntegerDigits(4);
    nf.setGroupingUsed(false);
  }

  /**
   * Default constructor
   */
  public MemoryResourceAccessor() {
    super(BUNDLE_NAME);
  }

  /**
   * Obtains a string from the resource bundle corresponding to the key given
   *
   * @param key Key in the resource bundle
   * @return String in the bundle
   */
  public static synchronized String getString(String key) {
    return resourceAccessor.getMessageText(Locale.getDefault(), key);
  }

  /**
   * Obtains a string from the resource bundle corresponding to the given message number
   *
   * @param id The ID of the key in the bundle
   * @return String in the bundle
   */
  public static synchronized String getString(int id) {
    return resourceAccessor.getMessageText(Locale.getDefault(), DEFAULT_KEY + nf.format(id));
  }

  /**
   * Formats a string from the resource bundle corresponding to the given key
   *
   * @param key Key in the bundle
   * @param params Objects to format the string with
   * @return The formatted string
   */
  public static synchronized String formatString(String key, Object[] params) {
    try {
      return LocalizableTextFormatter.formatString(resourceAccessor, key, params);
    } catch (LocalizationException e) {
      category.logThrowableT(Severity.WARNING, location, getString(ERROR_LOCALIZING_KEY) + " " + key, e);
      return null;
    }
  }

  /**
   * Formats a string from the resource bundle corresponding to the given message number
   *
   * @param id The ID of the key in the bundle
   * @param params Objects to format the string with
   * @return The formatted string
   */
  public static synchronized String formatString(int id, Object[] params) {
    try {
      return LocalizableTextFormatter.formatString(resourceAccessor, DEFAULT_KEY + nf.format(id), params);
    } catch (LocalizationException e) {
      category.logThrowableT(Severity.WARNING, location, getString(ERROR_LOCALIZING_ID) + " " + id, e);
      return null;
    }
  }

}
