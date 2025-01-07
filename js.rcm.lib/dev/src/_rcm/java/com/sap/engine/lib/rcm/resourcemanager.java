package com.sap.engine.lib.rcm;

/**
 * Created by Asen Petrov.
 * IUser: I030789
 * Date: 2008-1-8
 * Time: 12:41:59
 */
public interface ResourceManager {
  
  /**
   * Registers the resource provided by the resource provider with the resource
   * manager
   * @param provider      provider that provides some resource implementation
   */
  public void registerResource(ResourceProvider provider);

  /**
   * Removes the resource that the resource provider provides from resource 
   * management
   * @param provider      provider whose resource is to be removed from the 
   *                      resource manager
   */
  public void unregisterResource(ResourceProvider provider);

  /**
   * Creates a resource context for the resource and consumer type. The context
   * will be activated when the actual resource is registered with the resource
   * manager by a resource provider.
   * 
   * @param resourceName      the name of the resource for the context
   * @param consumerType      the consumer type for the context
   * @return                  a ResourceContext implementation to add Constraints
   *                          and Notifications, etc.
   */
  public ResourceContext createResourceContext(String resourceName, String consumerType);

  /**
   * Gets the resource context for the specified resource name and consumer
   * 
   * @param consumer        the consumer to identify the consumer type for the 
   *                        resource context
   * @param resourceName    the resource name for the context
   * @return                the ResourceContext for that pair or null if such
   *                        ResourceContext does not exist
   */
  public ResourceContext getResourceContext(ResourceConsumer consumer, String resourceName);

  /**
   * Gets the active ResourceContexts (for all resources) for the given consumer 
   * type
   * 
   * @param consumer        consumer for whom to get the active ResourceContexts
   * @return                an array of all active ResourceContexts for that 
   *                        consumer type
   */
  public ResourceContext[] getActiveResourceContexts(ResourceConsumer consumer);

  /**
   * Gets the inactive ResourceContexts (for resources that are currently not 
   * registered in the resource manager) for the given consumer type
   * 
   * @param consumer        consumer for whom to get the active ResourceContexts
   * @return                an array of all inactive ResourceContexts for that
   *                        consumer type
   */
  public ResourceContext[] getInactiveResourceContexts(ResourceConsumer consumer);

  /**
   * Removes a resource context from the resource manager. Any currently 
   * accounted resource usage for that context is lost.
   * 
   * @param context         context to destroy
   */
  public void destroyResourceContext(ResourceContext context);

  /**
   * Request to consume a quantity of a resource units by a resource consumer.
   * Constraints associated with the consumer type and the resource are 
   * evaluated and if the consume request is granted the Notifications 
   * associated with the corresponding ResourceContext are triggered.
   * 
   * @param consumer         the consumer that needs to consume the resource
   * @param resourceName     the resource that is requested
   * @param quantity         how may resource units are requested
   * @return                 true if the consume request was granted; false if
   *                         rejected (by constraints or lack of available 
   *                         resource units)
   */
  public boolean consume(ResourceConsumer consumer, String resourceName, long quantity);

  /**
   * Request multiple resources at once. Resource names are passed as an array
   * and the quantities of each resource in another array - the arrays have to
   * be the same size and the resource quantity index is same as the resource
   * name index.
   * 
   * @param consumer          consumer that needs to consume the resources
   * @param resourceNames     array of the resource names to be consumed
   * @param quantities        array for the quantities of the resources to be
   *                          consumed
   * @return                  true if the consume request was granted; false if
   *                          rejected (by constraints or lack of available 
   *                          resource units)
   */
  public boolean consume(ResourceConsumer consumer, String[] resourceNames, long[] quantities);

  /**
   * Releases quantity of resource units for the resource consumed by the 
   * consumer. Notifications registered in the corresponding ResourceContext
   * are triggered
   * 
   * @param consumer                  consumer that releases resource units
   * @param resourceName              resource that is being released
   * @param quantity                  quantity of resource units to release
   * @throws IllegalStateException    if the consumer is not consuming the 
   *                                  given resource units quantity
   */
  public void release(ResourceConsumer consumer, String resourceName, long quantity);

  /**
   * Releases quantity of resource units for multiple resources consumed by the
   * consumer. Notifications registered in the corresponding ResourceContexts 
   * are triggered. Resource names are passed as an array and the quantities of
   * each resource in another array - the arrays have to be the same size and
   * the resource quantity index is same as the resource name index.
   * 
   * @param consumer                    consumer that releases resource units
   * @param resourceNames               resources that are being released
   * @param quantities                  quantities of resource units to release
   * @throws IllegalStateException      if the consumer is not consuming the 
   *                                    given resource units quantity
   */
  public void release(ResourceConsumer consumer, String[] resourceNames, long[] quantities);

  /**
   * If a component can detect that some resource units are no longer in use,
   * but are formally not released (release method has not been called because 
   * of a bug for example) or the conponent has some means to logically or 
   * physically reclaim used resource units from consumers - it can call this 
   * method to set the resource counters to the correct state. Notifications 
   * are triggered as with normal release of resource units.
   * 
   * @param consumer                  consumer whose used resource is reclaied
   * @param resourceName              resouce to reclaim
   * @param quantity                  number of resource units to reclaim
   */
  public void reclaimResource(ResourceConsumer consumer, String resourceName,  long quantity);

  /**
   *
   * @param consumer         consumer to check for resource usage
   * @param resourceName     resource whose usage to check
   * @return                 number of resource units that are currently
   *                         consumed by the consumer
   */
  public long getUsage(ResourceConsumer consumer, String resourceName);

  /**
   *
   * @param consumerType       consumer type to check for resource usage
   * @param resourceName       resource whose usage to check
   * @return                   number of resource units consumed by all 
   *                           consumers of the given type
   */
  public long getTotalUsage(String consumerType, String resourceName);

  /**
   *
   * @return     array of the currently registered resources in the resource manager
   */
  public Resource[] getRegisteredResources();

  /**
   *
   * @return      array of the consumer type names for which there are active
   *              ResourceContexts
   */
  public String[] getActiveConsumerTypes();

}
