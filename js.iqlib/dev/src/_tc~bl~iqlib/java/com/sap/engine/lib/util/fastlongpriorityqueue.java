package com.sap.engine.lib.util;

import java.util.HashSet;
import java.util.Iterator;

/**
 * This class is an implementation of the abstract class FastPriorityQueue. It supports
 * priorities of type long. The queue is not thread safe and in case synchronization is needed -
 * it has to be implemented at a higher level.
 *
 *
 * @author Stefan Dimov
 * @version 1.0
 */
public final class FastLongPriorityQueue extends FastPriorityQueue {

  /**
   * The default constructor
   */
  public FastLongPriorityQueue() {
    this(DEFAULT_MAX_SIZE);
  }

  /**
   * The constructor allowing the user to determine the max size of the queue
   *
   * @param maxSize The max size of the priority queue
   */
  public FastLongPriorityQueue(int maxSize) {
    super(maxSize);
    this.wrappersComparator = new FastLongPQElemComp();
  }

  /**
   * Returns the top priority amongst the priorities of all the elements
   *
   * @return the top priority amongst the priorities of all the elements
   * @throws IllegalStateException If the priority queue is empty
   */
  public final long getTopPriority() {
    if (isEmpty()) throw new IllegalStateException("The priority queue is empty");
    return ((FastLongPQElem)queue.get(1)).getPriority();
  }

  /**
   * Returns the priority of the element with the given id
   *
   * @param id The id of the element
   * @return The priority of the element
   * @throws IllegalArgumentException If there is no element in the queue with such id
   */
  public final long getElementPriority(long id) {
    FastLongPQElem wrapper = (FastLongPQElem)idToWrapper.get(id);
    if (wrapper == null) throw new IllegalArgumentException("The element with such id is not presented in the queue");
    return wrapper.getPriority();
  }
  /**
   * Allows the user to add a new element into the priority queue. It will succeed only
   * if the max size of the queue is not reached
   *
   * @param priority The priority of the new element
   * @param value The new element
   * @return The id of the element, which should be >= 0. ENQUEUE_FAILURE(-1) is a flag that the
   * operation was unsuccessful
   */
  public final long enqueue(long priority, Object value) {
    if (isFull()) return ENQUEUE_FAILURE;
    HashSet wrappers = (HashSet)actualToWrappersSet.get(value);
    FastLongPQElem wrapper = (FastLongPQElem)this.getEmptyWrapper();
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
  public final void setElementOccurencesPriority(long priority, Object value) {
    HashSet wrappers = (HashSet)actualToWrappersSet.get(value);
    if (wrappers == null) throw new IllegalArgumentException("No such element in the priority queue");
    Iterator iter = wrappers.iterator();
    while (iter.hasNext()) {
      FastLongPQElem wrapper = (FastLongPQElem)iter.next();
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
  public final long setElementPriority(long priority, long id) {
    FastLongPQElem wrapper = (FastLongPQElem)idToWrapper.get(id);
    if (wrapper == null) throw new IllegalArgumentException("No element in the queue with such id");
    long res = wrapper.setPriority(priority);
    adjustTree(wrapper.getIndex());
    return res;
  }

  /**
   * Creates a new wrapper of type FastLongPQElem
   *
   * @return The newly created wrapper
   */
  protected final FastPQElem newWrapper() {
    return new FastLongPQElem();
  }

}
