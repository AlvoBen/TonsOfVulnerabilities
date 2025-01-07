package com.sap.engine.lib.util;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;


/**
 * This is a class representing a priorirty queue. It supports fast remove of an element. All the
 * algorithms have complexity O(log2(N)), where N is the number of the elements of the priority queue.
 * This data structure uses wrappers of class FastPQElem and during its lifecycle it doesn't produce
 * garbage collecting of wrapper objects, because the already created wrapper objects are being pooled
 * for later reuse after removing the actual elements of the priority queue. The queue is NOT thread
 * safe and in case synchronization is needed - it has to be implemented at a higher level.
 *
 * @author Stefan Dimov
 * @version 1.0
 */
abstract class FastPriorityQueue {

  protected final static int DEFAULT_MAX_SIZE = 1000000;

  /**
   * This variable is used to set the max size of the priority queue
   */
  protected int maxSize;

  /**
   * It's a comparator used to compare two element wrapper objects (FastPQElem) by their priorities
   */
  protected Comparator wrappersComparator;

  /**
   * The actual holder of the priorirty queue elements. The elements are being wrapped in the FastPQElem
   * objects and stored in this array
   */
  protected ArrayObject queue = null;

  /**
   * This is the wrapper objects pool. After removing some the actual element of the priority queue, its
   * wrapper doesn't become an object of garbage collecting, but is being pooled here for later reuse
   */
  protected Stack wrappersPool = null;

  /**
   * This hashtable maps the actual queue elements to (ObjectArray objects containg lists of) their wrappers.
   * It's used while removing some element
   */
  protected HashMapObjectObject actualToWrappersSet = null;

  /**
   * It's a mapping between ids and the wrappers of the objects stored in the priority queue
   */
  protected HashMapLongObject idToWrapper = null;

  /**
   * This is the Id of the object that was last stored in the priority queue. The next one that has to be
   * stored in the priority queue will be given this value +1
   */
  protected long lastGivenId = -1;

  /**
   * Flag that storing a new element in the queue was unsuccessful for some reason. Should be returned
   * by the enqueue method(s) if the queue is full and it's not possible a new element to be stored
   */
  protected final static long ENQUEUE_FAILURE = -1;

  protected FastPriorityQueue() {
    this(DEFAULT_MAX_SIZE);
  }

  protected FastPriorityQueue(int maxSize) {
    if (maxSize <= 0) throw new IllegalArgumentException("The max size of the priorirty queue must be bigger than zero");
    this.maxSize = maxSize;
    queue = new ArrayObject();
    queue.add(null);
    wrappersPool = new Stack();
    actualToWrappersSet = new HashMapObjectObject();
    idToWrapper = new HashMapLongObject();
  }

  // public section

  /**
   * Determines if the priority queue is full
   *
   * @return true if the priority queue elements number reached the max size.
   * Otherwise - false
   *
   */
  public final boolean isFull() {
    return queue.size() - 1 >=  maxSize;
  }

  /**
   * Determines if the priority queue is empty
   *
   * @return true if the number of elements is zero
   * Otherwise - false
   */
  public final boolean isEmpty() {
    return queue.size() == 1;
  }

  /**
   * Removes all the occurences of the given element if it's presented in the queue.
   * Otherwise - does nothing.
   *
   * @param element A reference to the element that has to be removed from queue
   * @return true, if the removal is successful. Otherwise false
   */
  public final boolean removeAllOccurences(Object element) {
    if (isEmpty()) return false;
    HashSet wrappers = (HashSet)actualToWrappersSet.remove(element);
    if (wrappers == null) return false;
    Iterator wrappersIter = wrappers.iterator();
    while (wrappersIter.hasNext()) {
      FastPQElem wrapper = (FastPQElem)wrappersIter.next();
      int index = wrapper.getIndex();
      idToWrapper.remove(wrapper.getId());
      wrapper.clear();
      wrappersPool.push(wrapper);
      remove(index);
    }
    return true;
  }

   /**
   * Removes the element in the queue with the given id. If this element is present in the priority
   * queue more than once the other occurences will not be removed
   *
   * @param id The id of the given element
   * @return A reference to the removed object. Null - if there is no element of the queue with such id
   */
  public final Object removeElement(long id) {
    FastPQElem wrapper = (FastPQElem)idToWrapper.remove(id);
    if (wrapper == null) return null;
    int index = wrapper.getIndex();
    Object actual = wrapper.clear();
    HashSet wrappers = (HashSet)actualToWrappersSet.get(actual);
    wrappers.remove(wrapper);
    if (wrappers.isEmpty()) { actualToWrappersSet.remove(actual); }
    wrappersPool.push(wrapper);
    remove(index);
    return actual;
  }

  /**
   * Returns the first element in the queue - the one with the highest priority,
   * without removing it.
   *
   * @return The element in the queue with the highest priority if there is one.
   * null - if the queue is empty
   */
  public final Object getTopPrioElement() {
    if (isEmpty()) return null;
    return ((FastPQElem)queue.get(1)).getActualElement();
  }

  /**
   * Returns the id of the top priority element
   *
   * @return the id of the top priority element
   */
  public final long getTopPrioElementId() {
    if (isEmpty()) return -1;
    return ((FastPQElem)queue.get(1)).getId();
  }
  /**
   * Removes the highest priority element in the queue and returns it
   *
   * @return The highest prioriry element if there is one. null - if the queue is empty
   */
  public final Object removeTopPrioElement() {
    if (isEmpty()) return null;
    FastPQElem wrapper = (FastPQElem)queue.get(1);
    idToWrapper.remove(wrapper.getId());
    Object actualElem = wrapper.clear();
    HashSet wrappers = (HashSet)actualToWrappersSet.get(actualElem);
    wrappers.remove(wrapper);
    if (wrappers.size() == 0) actualToWrappersSet.remove(actualElem);
    wrappersPool.push(wrapper);
    remove(1);
    return actualElem;
  }

  /**
   * Returns the element with the given id
   *
   * @param id The id of the element
   * @return The corresponding element. Null - if there is no such element in the queue
   */
  public final Object getElement(long id) {
    FastPQElem wrapper = (FastPQElem)idToWrapper.get(id);
    if (wrapper == null) return null;
    return wrapper.getActualElement();
  }
  /**
   * Determines if the queue contains the given object
   *
   * @param element The object that has to be determined if is presented in the queue
   * @return True if the queue contains the given object at least once. False - otherwise
   */
  public final boolean contains(Object element) {
    return actualToWrappersSet.containsKey(element);
  }

  /**
   * Returns info if the element with the given id is in the priority queue
   *
   * @param id The id of the element
   * @return true - if the element with the given id is in the queue. Otherwise - false
   */
  public final boolean containsElemWithId(long id) {
    return idToWrapper.containsKey(id);
  }

  /**
   * Determines how many times the queue contains the given object
   *
   * @param element The object that has to be determined if is presented in the queue
   * @return The number of occurences of this object in the priority queue contains this object
   */
  public final int numberOfOccurences(Object element) {
    HashSet wrappers = (HashSet)actualToWrappersSet.get(element);
    return (wrappers == null)?0:wrappers.size();
  }

  /**
   * Clears the content of the priority queue
   */
  public final void clear() {
    for (int i = 1; i < queue.size(); i++) {
      FastPQElem element = (FastPQElem) queue.get(i);
      element.clear();
      this.wrappersPool.push(element);
    }
    queue.clear();
    queue.add(null);
    actualToWrappersSet.clear();
  }

  /**
   * Returns the number of the elements of the queue
   *
   * @return The size of the priority queue
   */
  public final int size() {
    return queue.size() - 1;
  }

  /**
   * Changes the max size of the priority queue
   *
   * @param maxSize The new max size value
   */
  public final void setMaxSize(int maxSize) {
    if (maxSize <= 0) throw new IllegalArgumentException("The max size of the priorirty queue must be greater than zero");
    if (maxSize < queue.size() - 1) throw new IllegalArgumentException("The newly given value of the max size of the priorirty queue is lesser than its cuurent size");
    this.maxSize = maxSize;
  }

  // protected section

  /**
   * This method has to be implemented from every implementation of this class and has to create
   * an empty wrapper from the corresponding class, which implements FastPQElem.
   *
   * @return The newly created wrapper, which is an object of the corresponding FastPQElem class
   * implementation
   */
  protected abstract FastPQElem newWrapper();

  /**
   * Tries to get an empty wrapper from the pool. If it's not successful creates a new one
   *
   * @return An empty element wrapper
   */
  protected final FastPQElem getEmptyWrapper() {
    if (wrappersPool.isEmpty()) return newWrapper();
    return (FastPQElem)wrappersPool.pop();
  }

  /**
   * Removes the element with the given index by replacing it with the last element of the array and
   * adjusting the tree to find its right place in the queue
   *
   * @param index
   */
  private void remove(int index) {
    FastPQElem eLast = (FastPQElem)queue.removeLastElement();
    if (index == queue.size()) return;
    queue.set(index, eLast);
    eLast.setIndex(index);
    adjustTree(index);
  }

  /**
   * Changes the places of two elements of the queue
   *
   * @param index1 The index of the first element
   * @param index2 The index of the second element
   */

  private void exchangeElements(int index1, int index2) {
    if (index1 == index2) return;
    FastPQElem e1 = (FastPQElem)queue.get(index1);
    FastPQElem e2 = (FastPQElem)queue.get(index2);
    e1.setIndex(index2);
    e2.setIndex(index1);
    queue.set(index1, e2);
    queue.set(index2, e1);
  }

  /**
   * Tries to move the element with the given index up or down in the tree according to its priority.
   * Used when this element is changed in such a way that its priorirty is changed and it might be
   * possible that this element is no more in its righr place
   *
   * @param index The index of the changed element
   */
  protected final void adjustTree(int index) {
    // this excludes the redundant checks in the trivial case with one element
    if (queue.size() == 2) return;

    // this flag is used to avoid the redundant checks in the second half of the method, where the element should be
    // moved down after the first part where the element has been already moved up in the tree at least once
    boolean bMovedUp = false;
    if (index > 1) {
      // calculates the index of the parent node
      int parentIndex;
      while (((parentIndex = index >> 1) >= 1) && (wrappersComparator.compare(queue.get(parentIndex), queue.get(index)) < 0)) {
        exchangeElements(index, parentIndex);
        bMovedUp = true;
        index = parentIndex;
      }
    }
    // if bMovedUp is true it means that in the part above the element has been moved up in the tree at least once,
    // which means that it already has found its place and all the operations below are redundant
    if (bMovedUp) return;
    int leftChildIndex;
    // This cycle goes on, while the current element has at least left child
    while ((leftChildIndex = index << 1) < queue.size()) {
      // checks if the left child of the current element has a bigger priority
      int rightChildIndex = leftChildIndex | 1;
      if (rightChildIndex == queue.size()) {
        // if the element has only left child it should exchange with it if it has a bigger prio
        if (wrappersComparator.compare(queue.get(index), queue.get(leftChildIndex)) >= 0) return;
        exchangeElements(index, leftChildIndex);
        // The current index changes accordingly
        index = leftChildIndex;
      } else {
        // if the element has two children it has to excahnge with the bigger one
        if (wrappersComparator.compare(queue.get(leftChildIndex), queue.get(rightChildIndex)) >= 0) {
          // The left child has a bigger prio than the right, but has no bigger prio than the current element so our job is done
          if (wrappersComparator.compare(queue.get(index), queue.get(leftChildIndex)) >= 0) return;
          // The left child has a bigger prio than the right and has bigger prio than the current element so we must exchange them
          exchangeElements(index, leftChildIndex);
          // The current index changes accordingly
          index = leftChildIndex;
        } else {
          // The right child has a bigger prio than the left, but has no bigger prio than the current element so our job is done
          if (wrappersComparator.compare(queue.get(index), queue.get(rightChildIndex)) >= 0) return;
          // The right child has a bigger prio than the left and has bigger prio than the current element so we must exchange them
          exchangeElements(index, rightChildIndex);
          // The current index changes accordingly
          index = rightChildIndex;
        }
      }
    }
  }
}
