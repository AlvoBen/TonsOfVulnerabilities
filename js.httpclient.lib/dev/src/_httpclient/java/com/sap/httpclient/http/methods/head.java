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

import com.sap.httpclient.*;
import com.sap.httpclient.net.connection.HttpConnection;
import com.sap.httpclient.exception.HttpException;
import com.sap.httpclient.exception.ProtocolException;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import java.io.IOException;

/**
 * Implements the HTTP HEAD method.
 *
 * @author Nikolai Neichev
 */
public class HEAD extends HttpMethodImpl {

  /**
   * Log object for this class.
   */
  private static final Location LOG = Location.getLocation(HEAD.class);

  /**
   * Default constructor.
   */
  public HEAD() {
    setFollowRedirects(true);
  }

  /**
   * Constructor specifying a URI.
   *
   * @param uri either an absolute or relative URI
   */
  public HEAD(String uri) {
    super(uri);
    setFollowRedirects(true);
  }

  /**
   * Returns <tt>"HEAD"</tt>.
   *
   * @return <tt>"HEAD"</tt>
   */
  public String getName() {
    return HttpMethod.METHOD_HEAD;
  }

  /**
   * Overrides {@link HttpMethodImpl} method to <i>not</i> read a response body, despite
   * the presence of a <tt>Content-Length</tt> or <tt>Transfer-Encoding</tt> header.
   *
   * @param state the {@link HttpState state} information associated with this method
   * @param conn  the {@link HttpConnection connection} used to execute this HTTP method
   * @throws IOException   if an I/O (transport) error occurs.
   * @throws HttpException if a net exception occurs.
   */
  protected void readResponseBody(HttpState state, HttpConnection conn) throws IOException {
    int bodyCheckTimeout = getParams().getInt(Parameters.HEAD_BODY_CHECK_TIMEOUT, -1);
    if (bodyCheckTimeout < 0) {
      responseBodyConsumed();
    } else {
      if (LOG.beDebug()) {
        LOG.debugT("Check for non-compliant response body. Timeout in " + bodyCheckTimeout + " ms");
      }
      boolean responseAvailable;
      try {
        responseAvailable = conn.isResponseAvailable(bodyCheckTimeout);
      } catch (IOException e) {
        LOG.traceThrowableT(Severity.DEBUG, "An IOException occurred while testing if a response was available,"
                + " we will assume one is not.",
                e);
        responseAvailable = false;
      }
      if (responseAvailable) {
        if (getParams().isTrue(Parameters.REJECT_HEAD_BODY)) {
          throw new ProtocolException("Body content may not be sent in response to HTTP HEAD request");
        } else {
          LOG.warningT("Body content returned in response to HTTP HEAD");
        }
        super.readResponseBody(state, conn);
      }
    }
  }
}