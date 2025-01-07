package com.sap.engine.lib.rcm;

/**
 * This interface represents a logical consumer of a resource. Implementations
 * should be provided by the components that consumer resources on behalf on
 * some logical consumer (such logical consumers could be engine users, 
 * applications, services). Consumers are grouped by their logical type (like 
 * type user, type application) and are identified by their id that must be 
 * unique for their consumer type. Resources can be shared/used concurrently by
 * consumers of different types but cannot be shared by different consumers of 
 * the same type. For example if we define threads as a resource - single thread
 * can process at the same time a request (and therefore be consumed by) made
 * by user X  and made for applicaton Y. In this case the consumer X of type 
 * "user" and the conumer Y of type "application" both consume the same unit of
 * the resource "threads". But consumer X and consumer Z of type "user" cannot
 * consume the same unit of resource at the same time.
 */ 
public interface ResourceConsumer {
  
  /**
   * Unique type of consumer within the resource manager
   * @return    the type of the consumer
   */
  public String getType();
  
  /**
   * @return      the id of the consumer, must be unique for the consumer type
   */
  public String getId();
}
