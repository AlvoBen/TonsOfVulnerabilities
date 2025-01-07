package com.sap.httpclient.server;

import java.io.IOException;

/**
 * Defines an HTTP request/response service for the SimpleHttpServer
 */
public interface HttpService {
  /**
   * This interface represents a serice to process HTTP requests.
   *
   * @param request  The HTTP request object.
   * @param response The HTTP response object.
   * @return true if this service was able to handle the request, false otherwise.
   * @throws IOException if any IOException occures
   */
  public boolean process(final SimpleRequest request, final SimpleResponse response) throws IOException;

}