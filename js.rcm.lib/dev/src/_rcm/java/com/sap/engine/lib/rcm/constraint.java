package com.sap.engine.lib.rcm;


/**
 * Constraints implement rules based on the resource consumer identity, current
 * resource usage, proposed resource usage combination of those or some other 
 * specific condition that is checked by the implementation outside the scope 
 * of this API. In order to place a constraint on a resource usage the 
 * Constraint implementation must be registered in the corresponding 
 * ResourceContext.
 * 
 * @author Asen Petrov
 */
public interface Constraint {
  
  /**
   * Checks if the consumer is allowed to consume the requested amount of a 
   * resource according to that constraint implementation.
   * 
   * @param consumer      consumer that tries to consume units of the resource.
   * @param currentUsage  units of that resource that are currently consumed 
   *                      by the consumer
   * @param proposedUsage units of that resource that will be consumed by the 
   *                      consumer if the request is granted.
   * @return              ture if the consumer is allowed to consumer the resource
   *                      according to this rule; false if the consumer is not 
   *                      allowed to consume the resource.
   */
  boolean preConsume(ResourceConsumer consumer, long currentUsage, long proposedUsage);
}
