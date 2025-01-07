/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.frame.core.reflect;

/**
 * Using this interface you can access core objects and call their methods
 * using java.lang.reflect package. This will be useful for some special tools
 * for debuging, tracing, monitoring...
 *
 * @author Jasen Minov
 * @version 6.30
 */
public interface ReflectContext {

  /**
   * Get kernel component by name. If there is no component with this name
   * returns <source> null </source>
   *
   * @param    name - the name of kernel component
   */
  public Object getCoreComponent(String name);

}

