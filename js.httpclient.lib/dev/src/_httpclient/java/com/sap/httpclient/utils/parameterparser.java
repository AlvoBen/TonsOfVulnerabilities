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

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to parse sequences of name/value pairs.
 *
 * @author Nikolai Neichev
 */
public class ParameterParser {

  /**
   * Char array to be parsed
   */
  private char[] chars = null;

  /**
   * Current position in the string
   */
  private int pos = 0;

  /**
   * Length of the char array
   */
  private int len = 0;

  /**
   * Start of a token
   */
  private int startToken = 0;

  /**
   * End of a token
   */
  private int endToken = 0;

  /**
   * Default ParameterParser constructor
   */
  public ParameterParser() {
    super();
  }

  /**
   * Checks if there are characters left
   * @return <tt>true</tt> if has more characters, <tt>false</tt> if not
   */
  private boolean hasMore() {
    return this.pos < this.len;
  }

  /**
   * Gets the next token from the char array
   * @param quoted if the token is quoted
   * @return the next token
   */
  private String getToken(boolean quoted) {
    // skip leading white spaces
    while ((startToken < endToken) && (Character.isWhitespace(chars[startToken]))) {
      startToken++;
    }
    // skip trailing white spaces
    while ((endToken > startToken) && (Character.isWhitespace(chars[endToken - 1]))) {
      endToken--;
    }

    if (quoted) { // remove quotes if any
      if (((endToken - startToken) >= 2) && (chars[startToken] == '"') && (chars[endToken - 1] == '"')) {
        startToken++;
        endToken--;
      }
    }
    String result = null;
    if (endToken >= startToken) {
      result = new String(chars, startToken, endToken - startToken);
    }
    return result;
  }

  /**
   * Is the specified character one of the specified char array
   * @param ch the character
   * @param charray the char array
   * @return <tt>true</tt> if the character is one of the char array
   */
  private boolean isOneOf(char ch, char[] charray) {
    boolean result = false;
    for (char c: charray) {
      if (ch == c) {
        result = true;
        break;
      }
    }
    return result;
  }

  /**
   * Parses a token until any of the terminating characters is reached
   * @param terminators the terminating characters
   * @return the parsed token as a String
   */
  private String parseToken(final char[] terminators) {
    char ch;
    startToken = pos;
    endToken = pos;
    while (hasMore()) {
      ch = chars[pos];
      if (isOneOf(ch, terminators)) {
        break;
      }
      endToken++;
      pos++;
    }
    return getToken(false);
  }

  /**
   * Parses a token until any of the terminating characters is reached, quoted special chars are escaped
   * @param terminators the terminating characters
   * @return the parsed token a String
   */
  private String parseTokenQuoted(final char[] terminators) {
    char ch;
    startToken = pos;
    endToken = pos;
    boolean quoted = false;
    boolean charEscaped = false;
    while (hasMore()) {
      ch = chars[pos];
      if (!quoted && isOneOf(ch, terminators)) {
        break;
      }
      if (!charEscaped && ch == '"') {
        quoted = !quoted;
      }
      charEscaped = (!charEscaped && ch == '\\');
      endToken++;
      pos++;
    }
    return getToken(true);
  }

  /**
   * Extracts a list of {@link NameValuePair}s separated with specified separator, from the specified string.
   * @param str the specified string
   * @param pairSeparator the separator
   * @return the list of {@link NameValuePair}s
   */
  public List<NameValuePair> parse(final String str, char pairSeparator) {
    if (str == null) {
      return new ArrayList<NameValuePair>();
    }
    return parse(str.toCharArray(), pairSeparator);
  }

  /**
   * Extracts a list of {@link NameValuePair}s separated with pairSeparator, from the specified char array.
   * @param chars the char array, that contains a sequence of name/value pairs
   * @param pairSeparator the separator
   * @return the list of {@link NameValuePair}s
   */
  public List<NameValuePair> parse(final char[] chars, char pairSeparator) {
    if (chars == null) {
      return new ArrayList<NameValuePair>();
    }
    return parse(chars, 0, chars.length, pairSeparator);
  }

  /**
   * * Extracts a list of {@link NameValuePair}s separated with pairSeparator, from the specified sub char array.
   *
   * @param chars the char array, that contains a sequence of name/value pairs
   * @param offset the start position in the char array
   * @param length teh char array length
   * @param separator the separator
   * @return the list of {@link NameValuePair}s
   */
  public List<NameValuePair> parse(final char[] chars, int offset, int length, char separator) {
    if (chars == null) {
      return new ArrayList<NameValuePair>();
    }
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    this.chars = chars;
    this.pos = offset;
    this.len = length;
    String paramName;
    String paramValue;
    while (hasMore()) {
      paramName = parseToken(new char[]{'=', separator});
      paramValue = null;
      if (hasMore() && (chars[pos] == '=')) {
        pos++; // skip '='
        paramValue = parseTokenQuoted(new char[]{separator});
      }
      if (hasMore() && (chars[pos] == separator)) {
        pos++; // skip separator
      }
      if (paramName != null && !(paramName.equals("") && paramValue == null)) {
        params.add(new NameValuePair(paramName, paramValue));
      }
    }
    return params;
  }
}