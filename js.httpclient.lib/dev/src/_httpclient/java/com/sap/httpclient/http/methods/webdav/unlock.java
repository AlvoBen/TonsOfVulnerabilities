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
import com.sap.httpclient.HttpMethodImpl;
import com.sap.httpclient.http.Header;

/**
 * Implements the HTTP webdav UNLOCK method.
 *
 * @author Nikolai Neichev
 */
public class UNLOCK extends HttpMethodImpl {

  /**
   * No-arg constructor.
   */
  public UNLOCK() {
  }

  /**
   * Constructor specifying a URI.
   *
   * @param uri either an absolute or relative URI
   */
  public UNLOCK(String uri) {
    super(uri);
  }

  /**
   * Returns <tt>"UNLOCK"</tt>.
   *
   * @return <tt>"UNLOCK"</tt>
   */
  public String getName() {
    return HttpMethod.METHOD_UNLOCK;
  }

  /**
   * Sets the lock token value
   * @param lockToken the lock token value
   */
  public void setLockToken(String lockToken) {
    setRequestHeader(Header.LOCK_TOKEN, lockToken);
  }

}
