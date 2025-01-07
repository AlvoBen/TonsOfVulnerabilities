package com.sap.engine.services.httpserver.chain;

import java.util.Collections;
import java.util.Map;

public class BaseFilterConfig implements FilterConfig {
  private String name;
  private Map<String, String> parameters;

  public BaseFilterConfig(String name) {
    this.name = name;
    // Type safety is OK cause the map is empty and immutable
    this.parameters = Collections.EMPTY_MAP;
  }
  
  public BaseFilterConfig(String name, Map<String, String> parameters) {
    this.name = name;
    this.parameters = parameters;
  }
  
  public String getFilterName() {
    return name;
  }

  public String getParameter(String name) {
    return (String)parameters.get(name);
  }

  public Map<String, String> getParameters() {
    return parameters;
  }

}
