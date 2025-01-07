package com.sap.engine.services.httpserver.filters;

import java.io.IOException;

import com.sap.engine.services.httpserver.chain.FilterConfig;
import com.sap.engine.services.httpserver.chain.FilterException;
import com.sap.engine.services.httpserver.chain.HTTPRequest;
import com.sap.engine.services.httpserver.chain.HTTPResponse;
import com.sap.engine.services.httpserver.chain.ServerChain;
import com.sap.engine.services.httpserver.chain.ServerFilter;
import com.sap.engine.services.httpserver.server.ServiceContext;
import com.sap.engine.services.httpserver.server.sessionsize.SessionSizeManager;

public class SessionSizeFilter extends ServerFilter {
  
  @Override
  public void process(HTTPRequest request, HTTPResponse response,
      ServerChain chain) throws FilterException, IOException {
	
	//start calculation if the session size measurement feature is enabled
    if (ServiceContext.getServiceContext().getHttpProperties().isSessionSizeEnabled()) {
      SessionSizeManager.startCalculationInfo(request.getID());
      request.getHTTPParameters().getRequest().setSessionSizeEnabled(true);
    }
    
    chain.process(request, response);
    
    SessionSizeManager.calculateSessionSize(request.getID());    
  }

  @Override
  public void destroy() {
    // TODO Auto-generated method stub

  }

  @Override
  public void init(FilterConfig config) throws FilterException {
    // TODO Auto-generated method stub

  }

}
