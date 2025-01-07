package com.sap.engine.lib.util;

/**
 * This class is an implementation of FastPQElem and is used as a wrapper with int priorirty of the elements
 * of FastIntPriorityQueue. It has a package access, because it has to be hidden for the user. It's intended
 * for FastIntPriorityQueue internal use only
 *
 * 
 * @author Stefan Dimov
 * @version 1.0
 */
final class FastIntPQElem extends FastPQElem {

  /**
   * The priority of the element
   */
  private int priority;

  /**
   * Sets the priority of the queue element
   *
   * @param prio
   * @return The old value of the queue element priority
   */
  final int setPriority(int prio) {
    int res  = this.priority;
    priority = prio;
    return res;
  }

  /**
   * Returns the priority of this queue element
   *
   * @return The priority of this queue element
   */
  final int getPriority() {
    return priority;
  }

}
