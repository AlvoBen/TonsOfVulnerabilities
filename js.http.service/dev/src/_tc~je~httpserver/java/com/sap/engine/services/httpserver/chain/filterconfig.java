package com.sap.engine.services.httpserver.chain;

import java.util.Map;

/**
 * A filter configuration object used by a servlet container to pass
 * information to a filter during initialization
 */
public interface FilterConfig {
  /**
   * Returns the name of this filter
   * 
   * @return
   * a <code>java.lang.String</code> with this filter name
   */
  public String getFilterName();
  
  /**
   * Returns a <code>java.lang.String</code> containing the value of the named
   * initialization parameter, or <code>null</code> if the parameter does not
   * exist.
   * 
   * @param name
   * a <code>java.lang.String</code> with parameter name
   * 
   * @return
   * a <code>java.lang.String</code> containing the value of the 
   * initialization parameter, or <code>null</code> if the parameter
   * does not exist
   */
  public String getParameter(String name);
  
  /**
   * Returns an read-only <code>java.util.Map</code> with filter 
   * initialization parameters or empty <code>java.util.Map</code>
   * if the filter has no initialization parameters.
   * 
   * @return
   * a <code>java.util.Map</code> with initialization parameter names 
   * and corresponding values as <code>java.lang.String</code> objects
   */
  public Map getParameters();
}
