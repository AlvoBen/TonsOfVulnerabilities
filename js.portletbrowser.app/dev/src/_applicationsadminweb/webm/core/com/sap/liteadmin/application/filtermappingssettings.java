package com.sap.liteadmin.application;

public class FilterMappingsSettings {
  
  private String filterName = null;
  private String servletName = null;
  private String urlPattern = null;
  private boolean dispatcherError = false;
  private boolean dispatcherForward = false;
  private boolean dispatcherInclude = false;
  private boolean dispatcherRequest = false;
   
  public FilterMappingsSettings() {

  }
  
  public boolean isDispatcherError() {
    return dispatcherError;
  }
  public void setDispatcherError(boolean dispatcherError) {
    this.dispatcherError = dispatcherError;
  }
  public boolean isDispatcherForward() {
    return dispatcherForward;
  }
  public void setDispatcherForward(boolean dispatcherForward) {
    this.dispatcherForward = dispatcherForward;
  }
  public boolean isDispatcherInclude() {
    return dispatcherInclude;
  }
  public void setDispatcherInclude(boolean dispatcherInclude) {
    this.dispatcherInclude = dispatcherInclude;
  }
  public boolean isDispatcherRequest() {
    return dispatcherRequest;
  }
  public void setDispatcherRequest(boolean dispatcherRequest) {
    this.dispatcherRequest = dispatcherRequest;
  }
  public String getFilterName() {
    return filterName;
  }
  public void setFilterName(String filterName) {
    this.filterName = filterName;
  }
  public String getServletName() {
    return servletName;
  }
  public void setServletName(String servletName) {
    this.servletName = servletName;
  }
  public String getUrlPattern() {
    return urlPattern;
  }
  public void setUrlPattern(String urlPattern) {
    this.urlPattern = urlPattern;
  }

}
