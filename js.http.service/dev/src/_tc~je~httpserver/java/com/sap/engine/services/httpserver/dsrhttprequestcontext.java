/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver;

import java.util.Hashtable;

/*
 * This interface is used by DSR service to retrieve runtime information about the current http request.
 *
 * @author Maria Jurova
 * @version 6.30
 */
public interface DSRHttpRequestContext {
  /*
   * Returns the name of the host as specified in the http request.
   *
   * @return      the name of the host as specified in the http request
   */
  public String getHost();

  /*
   * Returns the name of the scheme used in the http request.
   *
   * @return      the name of the scheme used in the http request
   */
  public String getScheme();

  /*
   * Returns the full URL from the request line of the http request.
   *
   * @return      the full URL from the request line of the http request
   */
  public String getURL();

  /*
   * Returns a port as specified in the http request.
   *
   * @return      a port as specified in the http request
   */
  public int getPort();

  /*
   * Returns all headers from the http request. The keys in the hashtable are Strings representing
   * header names. The values are String arrays representing all values of headers with the given
   * as key name.
   *
   * @return      all headers from the http request as name/values pairs
   */
  public Hashtable getHeaders();

  /*
   * Returns the value of http header from the http request.
   * @param headerName        header name
   *
   * @return      the value of the header with name <code>headerName</code>
   */
  public String getHeader(String headerName);
}
