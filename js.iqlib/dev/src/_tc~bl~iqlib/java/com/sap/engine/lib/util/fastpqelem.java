package com.sap.engine.lib.util;

/**
 * This is an abstract class used by the FastPriorityQueue implementations as a superclass of all the clases
 * used as element wrappers. It has a package access, because it has to be hidden for the users of the
 * priority queue. It's intended for internal use by the priority queue
 *
 * @author Stefan Dimov
 * @version 1.0
 */

abstract class FastPQElem {

  /**
   * The actual queue element. If the wrapper is not in the queue internal array, but is pooled for
   * later reuse this member must have value null. Otherwise, it would be a memory leak
   */
  protected Object actualElement = null;

  /**
   * The index of the wrapper in the internal array of the priority queue Used for fast finding
   * of an element in the internal array. The FastPriorirtyQueue and its implementations has to
   * keep the value of this member consistent with actual place of the wrapper object in the
   * internal queue array. If the wrapper is not in the array, the value of this member has to be
   * -1
   */
  protected int index = -1;

  /**
   * The id of the element. Should be -1 if the wrapper is empty and pooled for reuse
   */
  protected long id = -1;

  /**
   * Returns the wrapped object, which is supposed to be the actual queue element.
   *
   * @return The actual wrapped queue element
   */
  final Object getActualElement() {
    return this.actualElement;
  }

  /**
   * Sets a new value of the wrapped actual queue element
   *
   * @param element The new object that has to be wrapped
   * @return The old wrapped object (could be null)
   */
  final Object setActualElement(Object element) {
    Object res = this.actualElement;
    this.actualElement = element;
    return res;
  }

  /**
   * Sets the index of the element in the queue internal array. It has to be consistent with the actual
   * place of the element in the internal array. If the wrapper is not in the
   *
   * @param index
   */
  final int setIndex(int index) {
    int res = this.index;
    this.index = index;
    return res;
  }

  /**
   * Returns the value of index property, which is supposed to be equal to the actual place of the element
   * in the queue internal array
   *
   * @return If the element is in the queue internal array - the index of the element in this array.
   * Otherwise -  -1
   */
  final int getIndex() {
    return index;
  }

  /**
   * Sets a new value to id
   *
   * @param id The new value pf the id
   * @return The old id value
   */
  final long setId(long id) {
    long oldId = this.id;
    this.id = id;
    return oldId;
  }

  /**
   * Returns the id of the enqueued element
   *
   * @return The id of the element. Should return -1 if the wrapper is empty and pooled for reuse
   */
  final long getId() {
    return id;
  }

  /**
   * This method prepares this wrapper object for pooling by emptying it. It's not declared as final, because
   * the inheritors of this wrapper class might hold another objects, which has to be released too. For example -
   * FastObjPQElem. The inheritors of this class should override this method in a manner to release all the
   * holded additional objects and after (or before) this to call the super.clear() to release the actual element
   * and set the index to -1
   *
   * @return The object that was wrapped until calling of this method
   */
  Object clear() {
    Object result = actualElement;
    actualElement = null;
    index = -1;
    id = -1;
    return result;
  }
}
