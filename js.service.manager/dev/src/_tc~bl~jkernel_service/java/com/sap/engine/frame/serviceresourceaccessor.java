package com.sap.engine.frame;

import com.sap.localization.ResourceAccessor;

/**
 * The ResourceAccessor for the software component "Service Manager".
 */
public class ServiceResourceAccessor extends ResourceAccessor {

  static final long serialVersionUID = -5496993443136635262L;
  
  //service resource bundle name
  private static final String BUNDLE_NAME = "com.sap.engine.frame.ServiceResourceBundle";
  //resource accessor singleton
  private static final ServiceResourceAccessor instance = new ServiceResourceAccessor();

  /**
   * Create service resource accessor singleton
   */
  private ServiceResourceAccessor() {
    super(BUNDLE_NAME);
  }

  /**
   * Returns a resource accessor instance for service resource bundle
   *
   * @return service resource accessor
   */
  public static ServiceResourceAccessor getInstance() {
    return instance;
  }
}
