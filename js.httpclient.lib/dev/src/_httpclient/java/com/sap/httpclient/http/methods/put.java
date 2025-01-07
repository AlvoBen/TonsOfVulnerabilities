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

import com.sap.httpclient.HttpMethod;

/**
 * Implements the HTTP PUT method.
 *
 * @author Nikolai Neichev
 */
public class PUT extends DataContainingRequest {

  /**
   * Default constructor.
   */
  public PUT() {
    super();
  }

  /**
   * Constructor specifying a URI.
   *
   * @param uri either an absolute or relative URI
   */
  public PUT(String uri) {
    super(uri);
  }

  /**
   * Return <tt>"PUT"</tt>.
   *
   * @return <tt>"PUT"</tt>
   */
  public String getName() {
    return HttpMethod.METHOD_PUT;
  }
}