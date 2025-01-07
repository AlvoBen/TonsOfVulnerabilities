package com.sap.engine.services.httpserver.chain.impl;

import java.util.Iterator;

import com.sap.engine.services.httpserver.chain.AbstractChain;
import com.sap.engine.services.httpserver.chain.Filter;
import com.sap.engine.services.httpserver.chain.HostChain;
import com.sap.engine.services.httpserver.chain.HostScope;
import com.sap.engine.services.httpserver.chain.ServerScope;


public class HostChainImpl extends AbstractChain implements HostChain {
  private ServerScope serverScope;
  private HostScope hostScope;

  public HostChainImpl(Iterator<Filter> filters, ServerScope serrverScope) {
    super(filters);
    this.serverScope = serrverScope;
  }
  
  public ServerScope getServerScope() {
    return serverScope;
  }
  
  public HostScope getHostScope() {
    return hostScope;
  }

  public void setHostScope(HostScope scope) {
    this.hostScope = scope;
  }
}
