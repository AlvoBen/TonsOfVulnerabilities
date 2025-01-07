package com.sap.engine.frame.container.monitor;

/**
 * General interface for interface monitoring
 *
 * @see com.sap.engine.frame.container.monitor.ComponentMonitor
 */
public interface InterfaceMonitor  extends ComponentMonitor {

  /**
   * Double name interfaces support list
   */
  public static final String[] INTERFACE_NAMES_API = new String[] {"appcontext_api",
                                                                "container_api",
                                                                "cross_api",
                                                                "csiv2_api",
                                                                "ejbcomponent_api",
                                                                "ejblocking_api",
                                                                "ejbmonitor_api",
                                                                "ejbormapping_api",
                                                                "ejbserialization_api",
                                                                "log_api",
                                                                "naming_api",
                                                                "security_api",
                                                                "shell_api",
                                                                "transactionext_api",
                                                                "visual_administration_api",
                                                                "webservices_api"};
  /**
   * Double name interfaces support list
   */
  public static final String[] INTERFACE_NAMES = new String[] {"appcontext",
                                                                "container",
                                                                "cross",
                                                                "csiv2",
                                                                "ejbcomponent",
                                                                "ejblocking",
                                                                "ejbmonitor",
                                                                "ejbormapping",
                                                                "ejbserialization",
                                                                "log",
                                                                "naming",
                                                                "security",
                                                                "shell",
                                                                "transactionext",
                                                                "visual_administration",
                                                                "webservices"};

  /**
   * Gets the runtime name of the service that provides this interface.
   *
   * @return - the name of the service which provides this interface or <code>null</code> if non of the services
   * declared that provides the interface.
   */
  public String getProvidingServiceName();

}

