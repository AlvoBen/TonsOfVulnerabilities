package com.sap.engine.services.httpserver.interfaces;

/**
 * This interface is used in order DSR service to register real implementation for the HTTP client instrumentation
 * The instrumented poins are:
 * - when HTTP request starts
 * - when HTTP request ends
 * 
 * @author simeon-s
 *
 */
public interface DSRHttpServer {

  /**
   * This method is invoked when HTTP request starts. 
   * The passed context information is host, port, shema and headers
   * 
   * @param context - HTTP context information
   * @param receivedBytes - received bytes from the request
   */
  void requestStart(DSRHttpRequestContext context);
    
  /**
   * This method is invoked when HTTP call finished
   * 
   */
  void requestEnd(DSRHttpRequestStatistics stat);
}
