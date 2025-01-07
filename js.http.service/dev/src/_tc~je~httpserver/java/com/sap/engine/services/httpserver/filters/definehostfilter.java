package com.sap.engine.services.httpserver.filters;

import java.io.IOException;

import com.sap.engine.services.httpserver.chain.FilterConfig;
import com.sap.engine.services.httpserver.chain.FilterException;
import com.sap.engine.services.httpserver.chain.HTTPRequest;
import com.sap.engine.services.httpserver.chain.HTTPResponse;
import com.sap.engine.services.httpserver.chain.HostScope;
import com.sap.engine.services.httpserver.chain.ServerChain;
import com.sap.engine.services.httpserver.chain.ServerFilter;
import com.sap.engine.services.httpserver.chain.impl.HostChainImpl;

public class DefineHostFilter extends ServerFilter {

  @Override
  public void process(HTTPRequest request, HTTPResponse response,
      ServerChain chain) throws FilterException, IOException {
    // TODO: Make getHost() return the default one instead of null
    // when host is unavailable
    HostScope hostScope = chain.getServerScope().getHttpHosts().getHost(
        request.getClient().getRequest().getHost());
    if (hostScope == null) {
      hostScope = chain.getServerScope().getHttpHosts().getHost("default");
    }
    ((HostChainImpl)chain).setHostScope(hostScope);
    chain.process(request, response);
  }

  public void destroy() {
    // TODO Auto-generated method stub

  }

  public void init(FilterConfig config) throws FilterException {
    // TODO Auto-generated method stub

  }

}
