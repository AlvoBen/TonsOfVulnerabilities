package com.sap.engine.lib.rcm;

/**
 * This interface must be implemented by components that provide/manage some
 * resource and want to regirster it in the resource management framework. Such
 * resources could be of any type the only requirement is that explicit 
 * execution points can be defined where the resource is consumed/released by
 * the calling code.
 * 
 * @author Asen Petrov
 */
public interface ResourceProvider {
  
  /**
   * This method is called when the Resource provider is registered with the
   * ResourceManager to get the provided resource implementation
   * 
   * @return          implementation of the Resource interface that defines the
   *                  provided resource
   */
  public Resource getResource();
  
  /**
   * This method is called when the Resource provider is registered with the
   * ResourceManager to get the provided resource implementation
   * 
   * @return           default Constraint implementation to be applied to all
   *                   ResourceContexts for that Resource - can be null if the
   *                   ResourceProvider does not define default constraint
   */
  public Constraint getDefaultConstrait();
  
  /**
   * This method is called when the Resource provider is registered with the
   * ResourceManager to get the provided resource implementation.
   * 
   * @return            default Notification implementation to be applied to all
   *                    ResourceContexts for that Resource - can be null if the 
   */
  public Notification getDefaultNotification();
}
