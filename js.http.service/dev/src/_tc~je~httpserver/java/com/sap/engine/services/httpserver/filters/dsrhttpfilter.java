package com.sap.engine.services.httpserver.filters;

import java.io.IOException;

import com.sap.engine.services.httpserver.chain.FilterConfig;
import com.sap.engine.services.httpserver.chain.FilterException;
import com.sap.engine.services.httpserver.chain.HTTPRequest;
import com.sap.engine.services.httpserver.chain.HTTPResponse;
import com.sap.engine.services.httpserver.chain.ServerChain;
import com.sap.engine.services.httpserver.chain.ServerFilter;
import com.sap.engine.services.httpserver.interfaces.DSRHttpServer;
import com.sap.engine.services.httpserver.server.dsr.DSRHttpRequestContextImpl;
import com.sap.engine.services.httpserver.server.dsr.DSRHttpRequestStatisticsImpl;
import com.sap.engine.services.httpserver.server.sessionsize.SessionSizeManager;
import com.sap.engine.services.httpserver.server.RequestImpl;

/**
 * This filter is instrumention the critical points for processing of the http 
 * request in the http server (the beginning and the end of the request processing). 
 * DSR service is calculating processing time by itslef as difference of the time of
 * call of dsrHttpServer.requestStart() and dsrHttpServer.requestEnd() method.
 * Thus this filter has to be called as early as possible to get the most accurate statistics   
 * 
 * It also provides the context information for the request as host, port, URL, request 
 * headers in a form of DSRHttpRequestContext
 *  
 * @author Violeta Uzunova (I024174) 
 */
public class DSRHttpFilter extends ServerFilter {
  
  // the real implemention of the object for dsr tracing; it will be initialized when the dsr server is started 
  private static DSRHttpServer dsrHttpServer = null;
  
  @Override
  public void process(HTTPRequest request, HTTPResponse response, ServerChain chain) throws FilterException, IOException {
    // if dsr server is not started, skip generating the objects for tracing
    if (dsrHttpServer != null) {
      DSRHttpRequestContextImpl dsrContext = new DSRHttpRequestContextImpl();
      dsrContext.init(request.getClient().getRequest());    
      dsrHttpServer.requestStart(dsrContext);
    }
    
    chain.process(request, response);
    
    //if dsr server is not started, skip generating the objects for tracing
    if (dsrHttpServer != null) {
      DSRHttpRequestStatisticsImpl dsrStatistics = new DSRHttpRequestStatisticsImpl();
      dsrStatistics.init(((RequestImpl)request.getClient().getRequest()), response.getRawResponse());    
      dsrHttpServer.requestEnd(dsrStatistics);
    }
    // leave it here because dsr might be off
    SessionSizeManager.removeSessionSizeInfo(request.getClient().getClientId());
  }

  public void destroy() {
    // TODO Auto-generated method stub

  }

  public void init(FilterConfig config) throws FilterException {
    // TODO Auto-generated method stub

  }

  /**
   * This method is invoked by DSR service in order to set real implementation.
   * Normaly it is invoked at startup of the service 
   */ 
  public static void registerHTTPServer(DSRHttpServer real) {
    dsrHttpServer = real;
  }
  
  /**
   * This method is invoked by DSr service in order to unregiser real implementation
   * Normaly it is invoked when service is stopped
   */ 
  public static void unregisterHTTPServer() {
    dsrHttpServer = null;
  }
  
}
