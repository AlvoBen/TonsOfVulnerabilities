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

import com.sap.httpclient.HttpClientParameters;
import com.sap.httpclient.net.connection.HttpConnection;
import com.sap.httpclient.HttpMethodImpl;
import com.sap.httpclient.HttpState;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.http.HttpVersion;

import java.io.IOException;

/**
 * This class is a base for the HTTP methods that support 'Expect: 100-continue' handshake.
 *
 * @author Nikolai Neichev
 */
public abstract class ExpectingContinueRequest extends HttpMethodImpl {

  /**
   * Default constructor.
   */
  public ExpectingContinueRequest() {
    super();
  }

  /**
   * Constructor specifying a URI.
   *
   * @param uri either an absolute or relative URI
   */
  public ExpectingContinueRequest(String uri) {
    super(uri);
  }

  /**
   * Check wether there is a request content
   *
   * @return boolean <tt>true</tt> if there is a request body to be sent, <tt>false</tt> otherwise.
   */
  protected abstract boolean hasRequestContent();

  /**
   * Sets the <tt>Expect</tt> header in addition to the "standard" set of headers.
   *
   * @param state the {@link HttpState state} information associated with this method
   * @param conn  the {@link HttpConnection connection} used to execute this HTTP method
   * @throws IOException   if an I/O (transport) error occurs.
   * @throws com.sap.httpclient.exception.HttpException if a net exception occurs.
   */
  protected void addRequestHeaders(HttpState state, HttpConnection conn) throws IOException {
    super.addRequestHeaders(state, conn);
    boolean headerPresent = (getRequestHeader(Header.EXPECT) != null); // true - if retry
    if (getParams().isTrue(HttpClientParameters.USE_EXPECT_CONTINUE)
            && getEffectiveVersion().greaterEquals(HttpVersion.HTTP_1_1)
            && hasRequestContent()) {
      if (!headerPresent) {
        setRequestHeader(Header.EXPECT, "100-continue");
      }
    } else {
      if (headerPresent) {
        removeRequestHeader(Header.EXPECT);
      }
    }
  }
}