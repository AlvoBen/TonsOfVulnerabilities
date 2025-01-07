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

import java.io.IOException;
import java.io.OutputStream;

/**
 * The interface represents a request data
 * @author Nikolai Neichev
 */
public interface RequestData {

  /**
   * Checks if {@link #writeRequest(OutputStream)} can be called more than once.
   *
   * @return <tt>true</tt> if the data can be written more than once, <tt>false</tt> otherwise.
   */
  boolean isRepeatable();

  /**
   * Writes the request data to the specified stream.
   *
   * @param out the specified stream
   * @throws IOException if an I/O exception occurs
   */
  void writeRequest(OutputStream out) throws IOException;

  /**
   * Gets the request data length.
   *
   * @return the content length
   */
  long getContentLength();

  /**
   * Gets the content type.  This will be used as the value for the "Content-Type" header.
   *
   * @return the content type
   */
  String getContentType();

}