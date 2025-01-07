package com.sap.engine.services.httpserver.chain.impl;

import com.sap.engine.services.httpserver.chain.HTTPRequest;
import com.sap.engine.services.httpserver.interfaces.HttpParameters;
import com.sap.engine.services.httpserver.server.Client;

public class HTTPRequestImpl implements HTTPRequest {
  private Client client;
  
  /**
   * Holds method of this request
   */
  private String method;

  public HTTPRequestImpl() {}
  
  public void init(Client client) {
    this.client = client;
  }
  
  public Client getClient() {
    return client;
  }
  
  public HttpParameters getHTTPParameters() {
    return client.getRequestAnalizer();
  }

  public String getMethod() {
    if (method == null) {
      method = new String(client.getRequest().getRequestLine().getMethod());
    }
    return method;
  }
  
  public int getID() {
    return client.getClientId();
  }
  
  public String getURLPath() {
    return client.getRequest().getRequestLine().getUrlDecoded().toString();
  }
}
