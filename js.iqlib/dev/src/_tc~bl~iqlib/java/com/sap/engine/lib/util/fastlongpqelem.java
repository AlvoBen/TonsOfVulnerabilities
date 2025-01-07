package com.sap.engine.lib.util;

/**
 * This class inherits the abstract class FastPQElem and is used as a wrapper of the elements of
 * FastLongPriorityQueue. It supports the priority of type long. It has a package access, because
 * it has to be hidden for the user. It's intended for internal usage by the FastLongPriorityQueue
 * only
 *
 * @author Stefan Dimov
 * @version 1.0
 */
class FastLongPQElem extends FastPQElem {

  /**
   * Holds the value of this element priority
   */
  private long priority;

  /**
   * Sets the priority of this element
   *
   * @param prio The new value of the priority
   * @return The old priority value
   */
  final long setPriority(long prio) {
    long res = this.priority;
    this.priority = prio;
    return res;
  }

  /**
   * Gets the element priority
   *
   * @return This element priority value
   */
  final long getPriority() {
    return priority;
  }
  
}
