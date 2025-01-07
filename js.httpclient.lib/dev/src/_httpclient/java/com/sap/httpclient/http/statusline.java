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
package com.sap.httpclient.http;

import com.sap.httpclient.exception.HttpException;
import com.sap.httpclient.exception.ProtocolException;

import java.io.Serializable;

/**
 * Represents a Status-Line as returned from a HTTP server.
 * <p/>
 *      Status-Line = HTTP-Version SP Status-Code SP Reason-Phrase CRLF
 * <p/>
 *
 * @author Nikolai Neichev
 */
public class StatusLine implements Serializable {

  /**
   * The original Status-Line.
   */
  private final String statusLine;

  /**
   * The HTTP-Version.
   */
  private final String httpVersion;

  /**
   * The Status-Code.
   */
  private final int statusCode;

  /**
   * The Reason-Phrase.
   */
  private final String reasonPhrase;

  /**
   * Default constructor.
   *
   * @param statusLine the status line returned from the HTTP server
   * @throws HttpException if the status line is invalid
   */
  public StatusLine(final String statusLine) throws HttpException {
    int length = statusLine.length();
    int at = 0;
    int start = 0;
    try {
      while (Character.isWhitespace(statusLine.charAt(at))) {
        ++at;
        ++start;
      }
      if (!"HTTP".equals(statusLine.substring(at, at += 4))) {
        throw new HttpException("Status-Line '" + statusLine + "' doesn't start with HTTP");
      }
      // HTTP-Version
      at = statusLine.indexOf(" ", at);
      if (at <= 0) {
        throw new ProtocolException("Unable to parse HTTP-Version from the Status-Line: '" + statusLine + "'");
      }
      this.httpVersion = (statusLine.substring(start, at)).toUpperCase();
      while (statusLine.charAt(at) == ' ') { // empty spaces
        at++;
      }
      // Status-Code
      int to = statusLine.indexOf(" ", at);
      if (to < 0) {
        to = length;
      }
      try {
        this.statusCode = Integer.parseInt(statusLine.substring(at, to));
      } catch (NumberFormatException e) {
        throw new ProtocolException("Unable to parse Status-Code from Status-Line: '" + statusLine + "'");
      }
      // Reason-Phrase
      at = to + 1;
      if (at < length) {
        this.reasonPhrase = statusLine.substring(at).trim();
      } else {
        this.reasonPhrase = "";
      }
    } catch (StringIndexOutOfBoundsException e) {
      throw new HttpException("Status-Line not valid: " + statusLine);
    }
    this.statusLine = statusLine;
  }

  /**
   * Returns the status line
   *
   * @return the Status-Code
   */
  public final int getStatusCode() {
    return statusCode;
  }

  /**
   * Returns the http version
   *
   * @return the HTTP-Version
   */
  public final String getHttpVersion() {
    return httpVersion;
  }

  /**
   * Returns the rason phrase
   *
   * @return the Reason-Phrase
   */
  public final String getReasonPhrase() {
    return reasonPhrase;
  }

  /**
   * Return a string representation of this object.
   *
   * @return a string represenation of this object.
   */
  public final String toString() {
    return statusLine;
  }

  /**
   * Tests if the string starts with 'HTTP'.
   *
   * @param s string to test
   * @return <tt>true</tt> if the line starts with 'HTTP' signature, <tt>false</tt> otherwise.
   */
  public static boolean startsWithHTTP(final String s) {
    try {
      int at = 0;
      while (Character.isWhitespace(s.charAt(at))) { // skip empty spaces
        ++at;
      }
      return ("HTTP".equals(s.substring(at, at + 4)));
    } catch (StringIndexOutOfBoundsException e) {
      return false;
    }
  }
}