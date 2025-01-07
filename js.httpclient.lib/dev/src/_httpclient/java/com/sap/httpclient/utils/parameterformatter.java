/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.httpclient.utils;

import com.sap.httpclient.NameValuePair;

/**
 * This class is used for producing a textual representation of name/value pairs.
 *
 * @author Nikolai Neichev
 */
public class ParameterFormatter {

  /**
   * Special characters that can be used as separators in HTTP parameters.
   * These special characters MUST be in a quotes
   */
  private static final char[] SEPARATORS = {
    '(', ')', '<', '>', '@', ',', ';', ':', '\\', '"', '/', '[', ']', '?', '=', '{', '}', ' ', '\t'
  };

  /**
   * Unsafe special characters. Must be escaped with the backslash character '\'
   */
  private static final char[] UNSAFE_CHARS = {'"', '\\'};

  /**
   * This flag determines whether all parameter values must be enclosed in
   * quotation marks, even if they do not contain any special(separator or LWS) characters
   */
  private boolean useQuotes = true;

  /**
   * Default ParameterFormatter constructor
   */
  public ParameterFormatter() {
    super();
  }

  private static boolean isOneOf(char[] chars, char ch) {
    for (char c : chars) {
      if (ch == c) {
        return true;
      }
    }
    return false;
  }

  private static boolean isUnsafeChar(char ch) {
    return isOneOf(UNSAFE_CHARS, ch);
  }

  private static boolean isSeparator(char ch) {
    return isOneOf(SEPARATORS, ch);
  }

  /**
   * Determines whether all parameter values will be enclosed in quotes
   *
   * @return <tt>true</tt> if all parameter values will be enclosed in quotation marks, <tt>false</tt> if not
   */
  public boolean isUseQuotes() {
    return useQuotes;
  }

  /**
   * Defines whether all parameter values must be enclosed in quotes
   *
   * @param useQuotes the boolean setting
   */
  public void setUseQuotes(boolean useQuotes) {
    this.useQuotes = useQuotes;
  }

  /**
   * Formats the specified value
   *
   * @param buffer outgoing buffer
   * @param value the parameter value to be formatted
   * @param useQuotes if <tt>true</tt> the parameter value will be enclosed in quotes
   */
  public static void formatValue(final StringBuilder buffer, final String value, boolean useQuotes) {
    if (buffer == null) {
      throw new IllegalArgumentException("String buffer is null");
    }
    if (value == null) {
      throw new IllegalArgumentException("Value buffer is null");
    }
    if (useQuotes) { // always quoted
      buffer.append('"');
      for (int i = 0; i < value.length(); i++) {
        char ch = value.charAt(i);
        if (isUnsafeChar(ch)) {
          buffer.append('\\');
        }
        buffer.append(ch);
      }
      buffer.append('"');
    } else {
      int offset = buffer.length();
      boolean unsafe = false;
      for (int i = 0; i < value.length(); i++) {
        char ch = value.charAt(i);
        if (isSeparator(ch)) {
          unsafe = true;
        }
        if (isUnsafeChar(ch)) {
          buffer.append('\\');
        }
        buffer.append(ch);
      }
      if (unsafe) { // add quotes if there are unsafe characters
        buffer.insert(offset, '"');
        buffer.append('"');
      }
    }
  }

  /**
   * Produces textual representaion of the attribute/value pair
   *
   * @param buffer outgoing buffer
   * @param pair  the parameter to be formatted
   */
  public void format(final StringBuilder buffer, final NameValuePair pair) {
    if (buffer == null) {
      throw new IllegalArgumentException("String buffer is null");
    }
    if (pair == null) {
      throw new IllegalArgumentException("Name/Value pair is null");
    }
    buffer.append(pair.getName());
    String value = pair.getValue();
    if (value != null) {
      buffer.append("=");
      formatValue(buffer, value, this.useQuotes);
    }
  }

  /**
   * Produces textual representaion of the attribute/value pair
   *
   * @param pair the attribute/value to be formatted
   * @return RFC 2616 conformant textual representaion of the attribute/value pair
   */
  public String format(final NameValuePair pair) {
    StringBuilder buffer = new StringBuilder();
    format(buffer, pair);
    return buffer.toString();
  }

}