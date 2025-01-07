package com.sap.engine.lib.util;

/**
 * This class inherits the abstract clas FastPQElem and is used as a wrapper for the elements of
 * FastObjPriorityQueue. It supports the generic priorities of type Objects
 *
 * @author Stefan Dimov
 * @version 1.0
 */
class FastObjPQElem  extends FastPQElem {

  /**
   * The priority of this element
   */
  private Object priority = null;

  /**
   * Sets the priority of this element
   *
   * @param prio The new priority value
   * @return The old priority values
   */
  final Object setPriority(Object prio) {
    Object res = this.priority;
    this.priority = prio;
    return res;
  }

  /**
   * Gets the element priority
   *
   * @return The value of the element priority
   */
  final Object getPriority() {
    return priority;
  }

  /**
   * Empties this wrapper by calling the overridden method in the super class to release the wrapped object and set
   * the index to -1 and after that releases the priority object
   *
   * @return The wrapped object
   */
  final Object clear() {
    Object result = super.clear();
    priority = null;
    return result;
  }

}
