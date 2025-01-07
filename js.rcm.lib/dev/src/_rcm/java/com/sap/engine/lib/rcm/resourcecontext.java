package com.sap.engine.lib.rcm;

/**
 * The resource context is a mapping between a resource and specific consumer
 * type. It holds the constraits defined for that consumer type - resource 
 * configuration, notifications, conuters for every cousumer of that type for 
 * that resource and for the total usage of the resource by the consumers of
 * that type.
 * 
 * @author Asen Petrov
 */
public interface ResourceContext {

  /**   
   * @return    the type of the consumers for the context
   */
  public String getConsumerType();

  /**  
   * @return    the resource for this context
   */
  public Resource getResource();

  /**
   * Adds a Constraint to be checked when a consumer tires to consume some 
   * units of the resource. Constraints are evaluated in the order they are
   * added to the context. If one constraint rejects the request the next are
   * not called at all and the resource consumption request is rejected for
   * that consumer. Even if all constraints allow the request it still can be
   * rejected if all resource units are already consumed by this and other 
   * consumers of that type. If there are no constraints registered for the 
   * context the decision to allow or reject the consumption request are based
   * only on the currently available units of the resource for that consumer 
   * type.
   * 
   * @param constraint    the rule implementation to add to the resource context
   */
  public void addConstraint(Constraint constraint);
  
  /**
   * Same as adding a single Constraint but add several constraints at once
   * - the order in the array is preserved
   * @param constraints     array of constraints to add to the resource 
   *                        context
   */
  public void addConstraints(Constraint[] constraints);
  
  /**
   * Inserts a constrait to be the first that is evaluated
   * @param constraint      constraint to insert
   */
  public void insertConstraint(Constraint constraint);

  /**
   * Inserts a constraint at specific index. Not really usefull as you have to
   * know the constraints array size and order and there is no API for that and
   * no actual use case. This method should be removed. No checks are made for
   * the validity of the number so ArrayIndexOutOfBoundsException may be thrown
   * if incorrect index is passed.
   * 
   * @param constraint        constraint to insert
   * @param index             index at which to insert the constraint
   */
  @Deprecated
  public void insertConstraint(Constraint constraint, int index);

  /**
   * Removes a constraint from the context. The constraint will no longer be
   * evaluated when requests to consume resource units are made
   * 
   * @param constraint        constraint to remove
   */
  public void removeConstraint(Constraint constraint);

  /**
   * Adds a notification that will be triggered when resource units are consumed
   * or released. Notifications are not triggered if the request to consume 
   * resource units is rejected.
   * 
   * @param notification        notification to add for the context
   */
  public void addNotification(Notification notification);

  /**
   * Removes a registered notification from the context. It will no longer be 
   * called when resource units are consumed or released
   * 
   * @param notification        notification to remove
   */
  public void removeNotification(Notification notification);

  /**
   * Get the current number of resource units that are accounted for that
   * consumer
   * 
   * @param consumerId      the id of the consumer
   * @return                number of resource units currently consumed by
   *                        the consumer
   */
  public long getCurrentUsage(String consumerId);

  /**
   * Get the total usage of the resource within this context
   * @return              number of resource units currently consumed by all
   *                      consumers of that type
   */
  public long getTotalUsage();
  
  /**
   * ResourceContexts rescribe a configuration specific for a consumer type and
   * a resource. It can be created and registered with the resource manager 
   * independently from the resource. If the resource for the ResourceContext 
   * is not registered with the resource manager the ResourceContext is in
   * "inactive" state - consumption requests for the context are granted by the
   * resource manager in that case and no constraints are evaluated, no usage 
   * accounted, no notifications triggered. As soon as the resource is
   * registered with the resource manager all resource context for that resource
   * move to "active" state and all constraints are evaluated, etc.
   * 
   * @return      true if the context is active; false if the context is not 
   *              active.
   */
  public boolean isActive();
  
}
