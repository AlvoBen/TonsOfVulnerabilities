package com.sap.engine.services.httpserver.chain.impl;

import java.util.Collections;
import java.util.Map;

import com.sap.engine.services.httpserver.chain.FilterConfig;

public class FilterConfigImpl implements FilterConfig {
  private String name;
  private Map parameters;

  public FilterConfigImpl(String name, Map parameters) {
    this.name = name;
    this.parameters = parameters;
  }
  
  public String getFilterName() {
    return name;
  }

  public String getParameter(String name) {
    return (String)parameters.get(name);
  }

  public Map getParameters() {
    return Collections.unmodifiableMap(parameters);
  }

}
