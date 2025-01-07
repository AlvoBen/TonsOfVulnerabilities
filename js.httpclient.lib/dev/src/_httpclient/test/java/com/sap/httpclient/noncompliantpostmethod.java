package com.sap.httpclient;

import java.io.IOException;
import java.util.ArrayList;
import com.sap.httpclient.http.methods.POST;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.net.connection.HttpConnection;

/**
 * HTTP POST methid intended to simulate side-effects of
 * interaction with non-compiant HTTP servers or proxies
 */

public class NoncompliantPostMethod extends POST {

  public NoncompliantPostMethod() {
    super();
  }

  public NoncompliantPostMethod(String uri) {
    super(uri);
  }

  /**
   * NoncompliantPostMethod class skips "Expect: 100-continue"
   * header when sending request headers to an HTTP server.
   * <p/>
   * <p/>
   * That makes the server expect the request body to follow immediately after the request head.
	 * The HTTP server does not send status code 100 expected by the client. The client should
   * be able to recover gracefully by sending the request body after a defined timeout without
	 * having received "continue" code.
   * </p>
   */
  protected void writeRequestHeaders(HttpState state, HttpConnection conn) throws IOException {
    addRequestHeaders(state, conn);
    ArrayList<Header> headers = getRequestHeaders();
    for (Header header : headers) {
      // Write all the headers but "Expect"
      if (!header.getName().equalsIgnoreCase("Expect")) {
        conn.print(header.toText(), "US-ASCII");
      }
    }
  }

}
