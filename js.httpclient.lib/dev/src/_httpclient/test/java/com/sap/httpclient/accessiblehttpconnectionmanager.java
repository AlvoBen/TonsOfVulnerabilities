package com.sap.httpclient;

import com.sap.httpclient.net.connection.SingleConnectionManager;
import com.sap.httpclient.net.connection.HttpConnection;

/**
 * A simple connection manager that provides access to the connection used.
 */
public class AccessibleHttpConnectionManager extends SingleConnectionManager {

  public AccessibleHttpConnectionManager() {
  }

  public HttpConnection getConection() {
    return httpConnection;
  }

}