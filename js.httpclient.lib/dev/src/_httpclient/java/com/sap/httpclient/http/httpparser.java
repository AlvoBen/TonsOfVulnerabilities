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

import com.sap.httpclient.exception.ProtocolException;
import com.sap.httpclient.uri.EncodingUtil;

import java.io.*;
import java.util.ArrayList;

/**
 * Helper class for parsing http header values according to RFC-2616 Section 4 and 19.3.
 *
 * @author Nikolai Neichev
 */
public class HttpParser {

  /**
   * Constructor for HttpParser.
   */
  private HttpParser() {
  }

  /**
   * Reads a raw line from the specified stream.
   *
   * @param inputStream the stream to read from
   * @return a byte array from the stream, <code>null</code> if no data is available
   * @throws IOException if an I/O problem occurs
   */
  public static byte[] readRawLine(InputStream inputStream) throws IOException {
    byte[] storage = new byte[64];
    int ch;
    int ind = 0;
    while ((ch = inputStream.read()) > -1) {
      storage[ind++] = (byte) ch;
      if (ind == storage.length) { // resize
        byte[] temp = new byte[storage.length*2];
        System.arraycopy(storage, 0, temp, 0, storage.length);
        storage = temp;
      }
      if (ch == '\n') { // be tolerant (RFC-2616 Section 19.3)
        break;
      }
    }
    if (ind == 0) { // empty line
      return null;
    } else {
      byte[] temp = new byte[ind];
      System.arraycopy(storage, 0, temp, 0, ind);
      return temp;
    }
  }

  /**
   * Reads a raw line from the specified stream.
   *
   * @param inputStream the stream to read from
   * @return a byte array from the stream, <code>null</code> if no data is available
   * @throws IOException if an I/O problem occurs
   */
  private static byte[] readRawLine_Mark_Reset(InputStream inputStream) throws IOException {
    inputStream.mark(Integer.MAX_VALUE);
    byte[] storage = new byte[64];
    boolean NL_found = false;
    int NL_ind = 0;
    while (!NL_found) {
      int r = inputStream.read(storage);
      if (r == -1) { // stream closed
        return null;
      }
      for (int i = 0; i < r; i++) {
        NL_ind++;
        if (storage[i] == '\n') {
          NL_found = true;
          break;
        }
      }
    }
    byte[] rawLine = new byte[NL_ind];
    inputStream.reset();
    if (inputStream.read(rawLine) == -1) {
			throw new IOException("Unexpected end of stream reached.");
		}
    return rawLine;
  }

  /**
   * Reads a line from the specified stream.
   *
   * @param inputStream the stream to read from
   * @param charset charset of HTTP elements
   * @return a line from the stream as String using the specified charset
   * @throws IOException if an I/O problem occurs
   */
  public static String readLine(InputStream inputStream, String charset) throws IOException {
    byte[] rawdata;
    if (inputStream.markSupported()) {
      rawdata = readRawLine_Mark_Reset(inputStream);
    } else {
      rawdata = readRawLine(inputStream);
    }
    if (rawdata == null) {
      return null;
    }
    // remove the CR and LF from the end of the line
    int len = rawdata.length;
    if ( (len > 1) && (rawdata[len - 2] == '\r') ) { // \r\n
      return EncodingUtil.getString(rawdata, 0, len - 2, charset);
    } else { // only \n
      return EncodingUtil.getString(rawdata, 0, len - 1, charset);
    }
  }

  /**
   * Parses headers from the specified stream.
   *
   * @param is the stream to read headers from
   * @param charset the charset to use for reading the data
   * @return an array of headers in the order in which they were parsed
   * @throws IOException   if an IO error occurs while reading from the stream
   * @throws com.sap.httpclient.exception.HttpException if there is an error parsing a header value
   */
  public static ArrayList<Header> parseHeaders(InputStream is, String charset) throws IOException {
    ArrayList<Header> headers = new ArrayList<Header>();
    String name = null;
    StringBuilder value = null;
    for (; ;) {
      String line = HttpParser.readLine(is, charset);
      if ((line == null) || (line.trim().length() < 1)) { // no data left in stream
        break;
      }
      // Detect LWS-char see HTTP/1.0 or HTTP/1.1 Section 2.2
      // discussion on folded headers
      if ((line.charAt(0) == ' ') || (line.charAt(0) == '\t')) { // check for folded headers first
        if (value != null) { // continuation folded header, so append value
          value.append(' ');
          value.append(line.trim());
        }
      } else { // begining of new header
        if (name != null) { // save the previous name/value pair if present
          headers.add(new Header(name, value.toString()));
        }
        int colon = line.indexOf(":");
        if (colon < 0) {
          throw new ProtocolException("Illegal header: " + line);
        }
        name = line.substring(0, colon).trim();
        value = new StringBuilder(line.substring(colon + 1).trim());
      }
    }
    if (name != null) { // save the last name/value pair if present
      headers.add(new Header(name, value.toString()));
    }
    return headers;
  }
}