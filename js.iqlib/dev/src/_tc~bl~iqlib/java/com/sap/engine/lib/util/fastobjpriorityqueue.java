package com.sap.engine.lib.util;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;

/**
 * This class is an implementation of the abstract class FastPriorityQueue and supports generic
 * priorities defined by the user. The queue is not thread safe and in case synchronization is needed -
 * it has to be implemented at a higher level.
 *
 *
 * @author Stefan Dimov
 * @version 1.0
 */
public class FastObjPriorityQueue extends FastPriorityQueue {

  /**
   * The constructor that requires the user to provide a comparator,
   * which will be used to compare the priority objects of the queue elements
   *
   * @param prioObjComparator The comparator, which should be able to compare the
   * priority objects given to the enqueue(...) methos in a consistent manner
   */
  public FastObjPriorityQueue(Comparator prioObjComparator) {
    this(DEFAULT_MAX_SIZE, prioObjComparator);
  }

  /**
   * The constructor that allows the user to determine the max size of the queue
   * and requires a comparator,which will be used to compare the priority objects
   * of the queue elements
   *
   * @param maxSize The max size of the priority queue
   * @param prioObjComparator The comparator, which should be able to compare the
   * priority objects given to the enqueue(...) methos in a consistent manner
   */

  public FastObjPriorityQueue(int maxSize, Comparator prioObjComparator) {
    super(maxSize);
    wrappersComparator = new FastObjPQElemComp(prioObjComparator);
  }

  /**
   * Returns the top priority amongst the priorities of all the elements
   *
   * @return the top priority amongst the priorities of all the elements
   * @throws IllegalStateException If the priority queue is empty
   */
  public final Object getTopPriority() {
    if (isEmpty()) throw new IllegalStateException("The priority queue is empty");
    return ((FastObjPQElem)queue.get(1)).getPriority();
  }

  /**
   * Returns the priority of the element with the given id
   *
   * @param id The id of the element
   * @return The priority of the element
   * @throws IllegalArgumentException If there is no element in the queue with such id
   */
  public final Object getElementPriority(long id) {
    FastObjPQElem wrapper = (FastObjPQElem)idToWrapper.get(id);
    if (wrapper == null) throw new IllegalArgumentException("The element with such id is not presented in the queue");
    return wrapper.getPriority();
  }

  /**
   * Allows a new element to be added to the queue. It will succeed only if the queue didn't reach
   * it's max size
   *
   * @param priority A generic object representing the priority of the new element
   * @param value The new element, which has to be enqueued
   * @return The id of the element, which should be >= 0. ENQUEUE_FAILURE(-1) is a flag that the
   * operation was unsuccessful
   */
  public final long enqueue(Object priority, Object value) {
    if (isFull()) return ENQUEUE_FAILURE;
    HashSet wrappers = (HashSet)actualToWrappersSet.get(value);
    FastObjPQElem wrapper = (FastObjPQElem)this.getEmptyWrapper();
    if (wrappers == null) {
      wrappers = new HashSet();
      actualToWrappersSet.put(value, wrappers);
    }
    wrappers.add(wrapper);
    wrapper.setIndex(queue.size());
    wrapper.setPriority(priority);
    wrapper.setActualElement(value);
    wrapper.setId(++lastGivenId);
    queue.add(wrapper);
    adjustTree(queue.size() - 1);
    idToWrapper.put(lastGivenId, wrapper);
    return lastGivenId;
  }

  /**
   * Sets a new priority of all the occurences of the given element in the priority queue
   *
   * @param priority The value of the new priority
   * @param value The element, which priority has to be changed
   * @throws IllegalArgumentException If the given element is not presented in the priority queue
   */
  public final void setElementOccurencesPriority(Object priority, Object value) {
    HashSet wrappers = (HashSet)actualToWrappersSet.get(value);
    if (wrappers == null) throw new IllegalArgumentException("No such element in the priority queue");
    Iterator iter = wrappers.iterator();
    while (iter.hasNext()) {
      FastObjPQElem wrapper = (FastObjPQElem)iter.next();
      wrapper.setPriority(priority);
      adjustTree(wrapper.getIndex());
    }
  }

  /**
   * Changes the priority of the element with the given id
   *
   * @param priority The new priority of the element
   * @param id The id of the element
   * @return The old priority of the element
   * @throws IllegalArgumentException if there is no element in the queue with such id
   */
  public final Object setElementPriority(Object priority, long id) {
    FastObjPQElem wrapper = (FastObjPQElem)idToWrapper.get(id);
    if (wrapper == null) throw new IllegalArgumentException("No element in the queue with such id");
    Object res = wrapper.setPriority(priority);
    adjustTree(wrapper.getIndex());
    return res;
  }

  /**
   * Creates a new element wrapper
   *
   * @return The newly created wrapper
   */
  protected final FastPQElem newWrapper() {
    return new FastObjPQElem();
  }
}
