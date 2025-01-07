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
package com.sap.httpclient.http.methods;

import com.sap.httpclient.net.connection.HttpConnection;
import com.sap.httpclient.HttpMethod;
import com.sap.httpclient.HttpMethodImpl;
import com.sap.httpclient.HttpState;
import com.sap.httpclient.http.Header;

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Implements the HTTP OPTIONS method.
 *
 * @author Nikolai Neichev
 */
public class OPTIONS extends HttpMethodImpl {

  /**
   * Holds the DAV header value, if any
   * Value <tt>NO</tt> if not supported
   */
  private String davHeaderValue = null;

  /**
   * Methods allowed.
   */
  private Vector<String> methodsAllowed = new Vector<String>();

  /**
   * Method constructor.
   */
  public OPTIONS() {
    this(null);
  }

  /**
   * Constructor specifying a URI.
   *
   * @param uri either an absolute or relative URI
   */
  public OPTIONS(String uri) {
    super(uri);
  }

  /**
   * Get the name.
   *
   * @return "OPTIONS"
   */
  public String getName() {
    return HttpMethod.METHOD_OPTIONS;
  }

  /**
   * Checks if the specified method allowed
   *
   * @param method The method to check.
   * @return true if the specified method is allowed.
   */
  public boolean isAllowed(String method) {
    assertUsed();
    return methodsAllowed.contains(method);
  }

  /**
   * Get a list of allowed methods.
   *
   * @return An enumeration of all the allowed methods.
   */
  public Enumeration<String> getAllowedMethods() {
    assertUsed();
    return methodsAllowed.elements();
  }

  /**
   * Checks if DAV is supported
   *
   * @return <code>true</code> if supported, false if not
   * @throws IllegalStateException if the method is not executed yet
   */
  public boolean isDAVSupported() throws IllegalStateException {
    assertUsed();
		return davHeaderValue != null; // true if DAV header is present
	}

  /**
   * Gets the DAV header value
   * @return the DAV header value
   * @throws IllegalStateException if the method is not executed yet, or DAV is not supported
   */
  public String getDAVHeaderValue() throws IllegalStateException {
    if (isDAVSupported()) {
      return davHeaderValue;
    } else {
      throw new IllegalStateException("DAV is not supported.");
    }
  }

  /**
   * Processes the response headers if necessary
   * This implementation will parse the <tt>Allow</tt> header to obtain the set of supported methods
   *
   * @param state the {@link HttpState state} information associated with this method
   * @param conn  the {@link HttpConnection connection} used to execute this HTTP method
   */
  protected void processResponseHeaders(HttpState state, HttpConnection conn) {
    // parse allowed methods
    Header allowHeader = getResponseHeader(Header._ALLOW);
    if (allowHeader != null) {
      String allowHeaderValue = allowHeader.getValue();
      StringTokenizer tokenizer = new StringTokenizer(allowHeaderValue, ",");
      while (tokenizer.hasMoreElements()) {
        String methodAllowed = tokenizer.nextToken().trim().toUpperCase();
        methodsAllowed.addElement(methodAllowed);
      }
    }
    // check for DAV header
    Header davHeader = getResponseHeader(Header.DAV);
    if (davHeader != null) {
      davHeaderValue = davHeader.getValue();
    }

  }



}