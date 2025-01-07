package com.sap.engine.lib.rcm;

/**
 * This interface defines abstract resource. Implementations of this interface
 * are not the physical resources that are managed but only a "view" of the 
 * resource properties that are evalutaed when resource management decision is
 * taken.
 * 
 * @author Asen Petrov.
 */
public interface Resource {

  /**
   * Human-readable name of the resource. The name must be unique in the 
   * resources registered with the ResourceManager and is case sensitive.
   * 
   * @return      String representing the name of the resource (like 
   *              "threads", etc.).
   */
  String getName();

  /**
   * Total units of the resource available for consumption.
   * 
   * @return        current total number of available units for that resource.
   *                The value is dynamic - units of the resource can be added
   *                or removed at runtime. Negative value means that the 
   *                resource is unlimited (isUnbounded() must return true in 
   *                that case).
   */
	 long getTotalQuantity();    

   /**
    * Human-readable name of a single unit of the resource (like "thread").
    * Could be used for monitirng/logging purposes.
    * 
    * @return       the name of a single unit of the resource.
    */
	 String getUnitName();
   
   /**
    * Specifies if units of this resource can be released/returned in pool 
    * after they are consumed
    * @return    true if the used units can be released; false if used units
    *            cannot be released after they are consumed.
    */
	 boolean isDisposable();

   /**
    * Specifies if there is a finitie number of units of that resource or if
    * it is of unlimited quantity (like traffic or cpu time).
    * 
    * @return     true if the resource is of unlimited quantity; false if there
    *             are only finite number of units available
    */
   boolean isUnbounded();
}
