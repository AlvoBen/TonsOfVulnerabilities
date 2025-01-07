package com.sap.httpclient;

import com.sap.httpclient.http.Header;
import com.sap.httpclient.http.HttpVersion;
import com.sap.httpclient.net.connection.HttpConnection;

/**
 * For test-nohost testing purposes only.
 */
public class FakeHttpMethod extends HttpMethodImpl {

  public FakeHttpMethod() {
    super();
  }

  public FakeHttpMethod(String path) {
    super(path);
  }

  public String getName() {
    return "Simple";
  }

  public void addResponseHeader(final Header header) {
    getResponseHeaderGroup().addHeader(header);
  }

  public String generateRequestLine(final HttpConnection connection, final HttpVersion version) {
    if (connection == null) {
      throw new IllegalArgumentException("Connection may not be null");
    }
    if (version == null) {
      throw new IllegalArgumentException("HTTP version may not be null");
    }
    return HttpMethodImpl.generateRequestLine(connection,
            this.getName(), this.getPath(), this.getQuery(), version.toString());
  }

}