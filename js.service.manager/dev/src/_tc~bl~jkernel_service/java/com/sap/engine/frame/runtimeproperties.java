/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.frame;

/**
 * This class is for use by libraries. Library component has no access to the
 * core API, but sometimes it is usefull to get some runtime info (for example
 * for logging purposes). Names of runtime properties depend from service
 * that register implementation of the abstract class.
 *
 * @author Jasen Minov
 * @version 6.30
 * @deprecated Applications should not rely on information provided by the RuntimeProperties since this class will not be part of the core layer facade in the future.
 */
public abstract class RuntimeProperties {
  public final static int PROPERTY_SESSION = 1;
  public final static int PROPERTY_USER = 2;
  public final static int PROPERTY_TRANSACTION = 3;
  public final static int PROPERTY_APPLICATION = 4;
  public final static int PROPERTY_APPLICATION_COMPONENT = 5;

  public final static int PROPERTY_DSR_COMPONENT_NAME = 6;
  public final static int PROPERTY_DSR_PREV_COMPONENT_NAME = 7;
  public final static int PROPERTY_DSR_TRANSACTION_ID = 8;
  public final static int PROPERTY_DSR_USER_ID = 9;

  public final static int PROPERTY_KERNEL_VERSION = 10;

  public final static int PROPERTY_DISPATCHER_HOST = 11;
  public final static int PROPERTY_DISPATCHER_HTTP_PORT = 12;

  private static RuntimeProperties runtime = null;

  /**
   * Set connection to server core.
   *
   * @deprecated Applications should not rely on information provided by the RuntimeProperties since this class will not be part of the core layer facade in the future.
   */
  public static void setProvider(RuntimeProperties serverConnection) {
    runtime = serverConnection;
  }

  /**
   * @deprecated  Use the method that takes an int parameter instead.<br/>Applications should not rely on information provided by the RuntimeProperties since this class will not be part of the core layer facade in the future.
   */
  public static String get(String name) {
    return null;
  }

  /**
   * Gets runtime property by name.
   *
   * @deprecated Applications should not rely on information provided by the RuntimeProperties since this class will not be part of the core layer facade in the future.
   */
  public static String get(int name) {
    if (runtime == null) {
      return null;
    } else {
      return runtime.getProperty(name);
    }
  }

  /**
   * Gets runtime property by name. This method is implemented by the runtime
   * property provider.
   *
   * @deprecated Applications should not rely on information provided by the RuntimeProperties since this class will not be part of the core layer facade in the future.
   */
  protected abstract String getProperty(int property);


}

