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
package com.sap.httpclient.http.methods.webdav;

import com.sap.httpclient.HttpMethod;

/**
 * Implements the HTTP webdav MOVE method.
 *
 * @author Nikolai Neichev
 */
public class MOVE extends COPY {

  /**
   * No-arg constructor.
   */
  public MOVE() {
  }

  /**
   * Constructor specifying a URI.
   *
   * @param uri either an absolute or relative URI
   */
  public MOVE(String uri) {
    super(uri);
  }

  /**
   * Returns <tt>"MOVE"</tt>.
   *
   * @return <tt>"MOVE"</tt>
   */
  public String getName() {
    return HttpMethod.METHOD_MOVE;
  }

}