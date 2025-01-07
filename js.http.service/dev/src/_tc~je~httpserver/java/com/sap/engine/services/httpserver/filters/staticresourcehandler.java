package com.sap.engine.services.httpserver.filters;

import static com.sap.engine.services.httpserver.server.Log.LOCATION_HTTP;

import java.io.IOException;

import com.sap.engine.services.httpserver.chain.FilterConfig;
import com.sap.engine.services.httpserver.chain.FilterException;
import com.sap.engine.services.httpserver.chain.HTTPRequest;
import com.sap.engine.services.httpserver.chain.HTTPResponse;
import com.sap.engine.services.httpserver.chain.HostChain;
import com.sap.engine.services.httpserver.chain.HostFilter;
import com.sap.engine.services.httpserver.server.HttpFile;
import com.sap.engine.services.httpserver.server.ServiceContext;

public class StaticResourceHandler extends HostFilter {

  @Override
  public void process(HTTPRequest request, HTTPResponse response,
                      HostChain chain) throws FilterException, IOException {
    if (request.getClient().getResponse().isDone()) {
      return;
    }
    HttpFile httpFile;
    long time = System.currentTimeMillis();
    try {
      httpFile = request.getClient().getRequestAnalizer().findRequestedFile();
    } catch (Exception e) {
      // TODO: Something
      throw new FilterException(e);
    }
    if (ServiceContext.getServiceContext().getHttpProperties().getTraceResponseTimeAbove() > -1) {
      time = System.currentTimeMillis() - time;
      request.getClient().getRequestAnalizer().getTimeStatisticsMap().put(
        (new StringBuilder()).append("/request/findRequestedFile (")
          .append(((httpFile != null) ? httpFile.getFileNameCannonical() : "not found")).append(")").toString(), time);
      if (LOCATION_HTTP.beDebug()) {
        LOCATION_HTTP.debugT((new StringBuilder()).append("StaticResourceHandler.process(): ").append("Put new entry '/request/findRequestedFile (")
          .append(((httpFile != null) ? httpFile.getFileNameCannonical() : "not found")).append(")' with time [").append(time).append("].").toString());
      }
    }

    time = System.currentTimeMillis();
    request.getClient().getResponse().makeAnswer(httpFile);
    if (ServiceContext.getServiceContext().getHttpProperties().getTraceResponseTimeAbove() > -1) {
      time = System.currentTimeMillis() - time;
      request.getClient().getRequestAnalizer().getTimeStatisticsMap().put("/request/makeAnswer", time);
      if (LOCATION_HTTP.beDebug()) {
        LOCATION_HTTP.debugT((new StringBuilder()).append("StaticResourceHandler.process(): ").append("Put new entry '/request/makeAnswer' with time [").append(time).append("].").toString());
      }
    }
  }

  public void init(FilterConfig config) throws FilterException {
    // TODO Auto-generated method stub

  }

  public void destroy() {
    // TODO Auto-generated method stub

  }

}
