package com.sap.engine.lib.rcm;

/**
 * Components that are interested in receiving notifications of the current
 * usage of a resource can provide implementation of the Notification and 
 * register it with the corresponding resource context. Possible users would be
 * ResourceProvider implementations, monitoring tools, etc.
 * 
 * @author Asen Petrov
 */
public interface Notification {
  
  /**
   * This method is called every time when units of the resource are granted to
   * a consumer or when units of the resource are released.
   * 
   * @param consumer        resource consumer that consumes/releases some 
   *                        resource units
   * @param previousUsage   used resource units by that consumer before the
   *                        consume/release operation
   * @param currentUsage    used resource units by that consumer after the 
   *                        consume/release operation
   */
  void update(ResourceConsumer consumer, long previousUsage, long currentUsage);
}
