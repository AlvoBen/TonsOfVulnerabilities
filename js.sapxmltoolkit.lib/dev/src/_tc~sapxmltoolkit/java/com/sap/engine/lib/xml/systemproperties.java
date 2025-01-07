/*
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.xml;

import java.util.*;

public class SystemProperties {

  private static Properties props = new Properties();

  public static Properties getProperties() {
    Properties result = null;
    try {
      result = System.getProperties();
    } catch (SecurityException e) {
      //$JL-EXC$
      result = props;
    }
    return result;
  }

  public static String getProperty(String key) {
    String result = null;
    try {
      result = System.getProperty(key);
    } catch (SecurityException e) {
      //$JL-EXC$
      if (key == null) {
        throw new NullPointerException("key can't be null");
      }

      if (key.equals("")) {
        throw new IllegalArgumentException("key can't be empty");
      }

      result = props.getProperty(key);
    }
    return result;
  }

  public static String getProperty(String key, String def) {
    String result = null;
    try {
      result = System.getProperty(key, def);
    } catch (SecurityException e) {
      //$JL-EXC$
      if (key == null) {
        throw new NullPointerException("key can't be null");
      }

      if (key.equals("")) {
        throw new IllegalArgumentException("key can't be empty");
      }

      result = props.getProperty(key, def);
    }
    return result;
  }

  public static void setProperties(Properties p) {
    try {
      System.setProperties(p);
    } catch (SecurityException e) {
      //$JL-EXC$
      if (p == null) {
        p = new Properties();
      }

      props = p;
    }
  }

  public static String setProperty(String key, String value) {
    String result = null;
    try {
      result = System.setProperty(key, value);
    } catch (SecurityException e) {
      //$JL-EXC$
      if (key == null) {
        throw new NullPointerException("key can't be null");
      }

      if (key.equals("")) {
        throw new IllegalArgumentException("key can't be empty");
      }

      result = (String) props.setProperty(key, value);
    }
    return result;
  }

  public static boolean getBoolean(String name) {
    boolean result = false;
    try {
      result = Boolean.getBoolean(name);
    } catch (SecurityException e) {
      //$JL-EXC$
      try {
        result = toBoolean(props.getProperty(name));
      } catch (IllegalArgumentException ie) {
        //$JL-EXC$
		int i;
      } catch (NullPointerException ne) {
        //$JL-EXC$
		int i;
      }
    }
    return result;
  }

  private static boolean toBoolean(String name) {
    return ((name != null) && name.equalsIgnoreCase("true"));
  }

}

