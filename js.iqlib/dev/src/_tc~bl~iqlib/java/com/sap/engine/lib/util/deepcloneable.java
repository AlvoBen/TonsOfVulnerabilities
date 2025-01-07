package com.sap.engine.lib.util;

/**
 * Interface for deep clonning
 *
 * @author Vasil Popovski
 * @version 4.0
 */
public interface DeepCloneable {

  /**
   * Creates and returns a deep copy of this object.<p>
   *
   * @return  a clone of this instance.
   */
  public Object deepClone();

}

