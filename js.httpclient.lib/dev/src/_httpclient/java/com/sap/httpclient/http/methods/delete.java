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
import com.sap.httpclient.HttpMethodImpl;

/**
 * Implements the HTTP DELETE method.
 *
 * @author Nikolai Neichev
 */
public class DELETE extends HttpMethodImpl {

  /**
   * Default constructor.
   */
  public DELETE() {
    this(null);
  }

  /**
   * Constructor specifying a URI.
   *
   * @param uri either an absolute or relative URI
   */
  public DELETE(String uri) {
    super(uri);
  }

  /**
   * Returns <tt>"DELETE"</tt>.
   *
   * @return <tt>"DELETE"</tt>
   */
  public String getName() {
    return HttpMethod.METHOD_DELETE;
  }
}