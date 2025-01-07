package com.sap.engine.services.httpserver.chain.impl;

import com.sap.engine.services.httpserver.chain.HTTPResponse;
import com.sap.engine.services.httpserver.interfaces.ErrorData;
import com.sap.engine.services.httpserver.server.Client;
import com.sap.engine.services.httpserver.server.ResponseImpl;

public class HTTPResponseImpl implements HTTPResponse {
  private Client client;
  private ResponseImpl oresponse;

  public HTTPResponseImpl() {}
  
  public void init(Client client) {
    this.client = client;
    oresponse = client.getResponse();
  }
  
  public int getStatusCode() {
    return client.getResponse().getResponseCode();
  }
  
  public int getContentLength() {
    return client.getResponse().getResponseLength();
  }

  public ResponseImpl getRawResponse() {
    return oresponse;
  }

  /**
   * Sends an error response to the client using the specified status. 
   * By default the server creates the response to look like an HTML-formatted
   * error page containing the specified message and details, setting the
   * content type to "text/html", leaving cookies and other headers unmodified.
   * Based on <code>ErrorData.isHtmlAllowed()</code> constructs the error page with html allowed or not.
   * 
   * <p>If the response has already been committed, this method throws an
   * IllegalStateException. After using this method, the response should be
   * considered to be committed and should not be written to.</p>
   * 
   * @param errorData the error data. 
   * @see For more info see <code>com.sap.engine.services.httpserver.interfaces.ErrorData</code>
   * @throws IllegalStateException if the response was committed.
   */
  public void sendError(ErrorData errorData) {
    oresponse.sendError(errorData);
  }

  public void sendRedirect(String location) {
    client.getRequestAnalizer().redirect(location.getBytes());   
  }
}
