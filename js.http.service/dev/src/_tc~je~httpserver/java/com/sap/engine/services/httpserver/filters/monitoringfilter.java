package com.sap.engine.services.httpserver.filters;

import java.io.IOException;
import java.util.NoSuchElementException;

import com.sap.engine.services.httpserver.chain.FilterConfig;
import com.sap.engine.services.httpserver.chain.FilterException;
import com.sap.engine.services.httpserver.chain.HTTPRequest;
import com.sap.engine.services.httpserver.chain.HTTPResponse;
import com.sap.engine.services.httpserver.chain.ServerChain;
import com.sap.engine.services.httpserver.chain.ServerFilter;
import com.sap.engine.services.httpserver.chain.ServerScope;
import com.sap.engine.services.httpserver.server.HttpMonitoring;
import com.sap.engine.services.httpserver.server.HttpServerFrame;
import com.sap.engine.services.httpserver.server.Log;
import com.sap.engine.services.httpserver.server.ServiceContext;

public class MonitoringFilter extends ServerFilter {
  private ServerScope serverScope;

  @Override
  public void process(HTTPRequest request, HTTPResponse response,
      ServerChain chain) throws FilterException, IOException {
    if (HttpServerFrame.isMonitoringStarted()) {
      HttpMonitoring monitoring = serverScope.getHttpMonitoring();
      monitoring.newRequest(request.getMethod());
      long systemTimeOfStart = System.currentTimeMillis();
      try {
        chain.process(request, response);
      } finally {
        try {
          // TODO: Correct monitoring to not throw any exceptions
          monitoring.addResponseTime(System.currentTimeMillis() 
            - systemTimeOfStart, response.getStatusCode());
        } catch (NoSuchElementException e) {
          Log.logError("ASJ.http.000105", 
            "Cannot calculate HTTP response time for client id [{0}]. " +
            "Possible reason: request processing start time missing. Cannot provide request statistics to Monitoring service.",
        		new Object[]{request.getID()}, null, null, null);
        }
      }
    } else {
      chain.process(request, response);
    }
  }

  public void destroy() {
    // TODO Auto-generated method stub

  }

  public void init(FilterConfig config) throws FilterException {
    this.serverScope = ServiceContext.getServiceContext();
  }
}
