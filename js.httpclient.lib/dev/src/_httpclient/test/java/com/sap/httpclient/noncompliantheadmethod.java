package com.sap.httpclient;

import com.sap.httpclient.http.methods.HEAD;

/**
 * HTTP GET methid intended to simulate side-effects of
 * interaction with non-compiant HTTP servers or proxies
 */

public class NoncompliantHeadMethod extends HEAD {

  public NoncompliantHeadMethod() {
    super();
  }

  public NoncompliantHeadMethod(String uri) {
    super(uri);
  }

  /**
   * Expect HTTP HEAD but perform HTTP GET instead in order to
   * simulate the behaviour of a non-compliant HTTP server sending
   * body content in response to HTTP HEAD request
   */
  public String getName() {
    return "GET";
  }

}
