/*
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.httpclient.utils;

import java.io.OutputStream;

/**
 * Implementations are used to intercept the HTTP request,
 * so the request body can be written directly to the stream.
 *
 * @author Nikolai Neichev
 */
public interface OutputStreamInterceptor {

  /**
   * Thsi method is invoked, when the request body should be written to the passed output stream.
   * After the method execution the data is flushed and sent.
   *
   * @param stream the output stream
   */
  public void writeRequestBodyToStream(OutputStream stream);

}
