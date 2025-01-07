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

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A class for combining a set of headers.
 *
 * @author Nikolai Neichev
 */
public class HeaderGroup {

  /**
   * The list of headers for this group
   */
  private ArrayList<Header> headers;

  /**
   * Constructor
   */
  public HeaderGroup() {
    this.headers = new ArrayList<Header>();
  }

  /**
   * Removes all headers
   */
  public void clear() {
    headers.clear();
  }

  /**
   * Adds the specified header to the group
   *
   * @param header the header to add
   */
  public void addHeader(Header header) {
    headers.add(header);
  }

  /**
   * Removes the specified header
   *
   * @param header the header to remove
   */
  public void removeHeader(Header header) {
    headers.remove(header);
  }

  /**
   * Sets the specified headers to the group
   *
   * @param headers the headers to set
   */
  public void setHeaders(ArrayList<Header> headers) {
    clear();
    for (Header header : headers) {
      addHeader(header);
    }
  }

  /**
   * Gets a header representing all of the header values with the specified name.
   * If more that one header with the specified name exists the values will be
   * combined with a "," as per RFC 2616.
   * <p/>
   * <p>Header name comparison is case insensitive.
   *
   * @param name the name of the header(s) to get
   * @return a header with a condensed value or <code>null</code> if no
   *         headers by the specified name are present
   */
  public Header getCondensedHeader(String name) {
    ArrayList<Header> headers = getHeaders(name);
    if (headers.size() == 0) {
      return null;
    } else if (headers.size() == 1) {
      return new Header(headers.get(0).getName(), headers.get(0).getValue());
    } else {
      StringBuilder valueBuffer = new StringBuilder(headers.get(0).getValue());
      for (int i = 1; i < headers.size(); i++) {
        valueBuffer.append(", ");
        valueBuffer.append(headers.get(i).getValue());
      }
      return new Header(name.toLowerCase(), valueBuffer.toString());
    }
  }

  /**
   * Gets all of the headers with the specified name.
   *
   * @param name the name of the header(s) to get
   * @return an array of headers
   */
  public ArrayList<Header> getHeaders(String name) {
    ArrayList<Header> headersFound = new ArrayList<Header>(2);
    for (Header header : headers) {
      if (header.getName().equalsIgnoreCase(name)) {
        headersFound.add(header);
      }
    }
    return headersFound;
  }

  /**
   * Gets the first header with the specified name.
   *
   * @param name the name of the header to get
   * @return the first header, <code>null</code> in not found
   */
  public Header getFirstHeader(String name) {
    for (Header header : headers) {
      if (header.getName().equalsIgnoreCase(name)) {
        return header;
      }
    }
    return null;
  }

  /**
   * Gets the last header with the specified name.
   * @param name the name of the header to get
   * @return the last header, <code>null</code> if not found
   */
  public Header getLastHeader(String name) {
    for (int i = headers.size() - 1; i >= 0; i--) {
      Header header = headers.get(i);
      if (header.getName().equalsIgnoreCase(name)) {
        return header;
      }
    }
    return null;
  }

  /**
   * Gets all of the headers from the group.
   *
   * @return an array of headers
   */
  public ArrayList<Header> getAllHeaders() {
    return headers;
  }

  /**
   * Tests if there is a header with the specified name in the group.
   *
   * @param name the header name to test for
   * @return <code>true</code> if at least one header with the name is
   *         contained, <code>false</code> otherwise
   */
  public boolean containsHeader(String name) {
    for (Header header : headers) {
      if (header.getName().equalsIgnoreCase(name)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns a iterator of the headers.
   *
   * @return iterator of the headers.
   */
  public Iterator getIterator() {
    return this.headers.iterator();
  }

}