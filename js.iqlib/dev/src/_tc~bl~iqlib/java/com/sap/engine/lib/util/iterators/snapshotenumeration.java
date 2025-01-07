/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.util.iterators;

/**
 * Static non changeable iterator.<p>
 *
 * @author Nikola Arnaudov
 * @version 4.0
 */
public interface SnapShotEnumeration {

  /**
   * Returns an object at the current iterator position.<p>
   *
   * @return an object pointed by current iterator position.
   */
  public Object get();


  /**
   * Returns object at the current position and shifts the pointer to the next element
   * from the data structure.<p>
   *
   * @return the object at the current position of iterator.
   */
  public Object next();


  /**
   * Shifts the pointer with n positions, returns the pointed object and
   * shifts the pointer one more time.<p>
   *
   * @param n the number of positions that have to be skipped.
   * @return the object at the current position + n.
   */
  public Object next(int n);


  /**
   * Shifts the pointer to the previous element from the data structure and
   * returns object at that position.
   *
   * @return the object at the previous position of iterator.
   */
  public Object prev();


  /**
   * Shifts the pointer with n + 1 positions and returns the pointed object
   *
   * @param n the number of positions that have to be skipped.
   * @return tThe object at the previous position - n.
   */
  public Object prev(int n);


  /**
   * Returns true if this iterator has more elements when traversing
   * the iterator in the forward direction.<p>
   *
   * @return true if the iterator has more elements when traversing
   *         the iterator in the forward direction.
   */
  public boolean hasNext();


  /**
   * Returns true if this iterator has more elements when traversing
   * the iterator in the reverse direction.<p>
   *
   * @return true if the list iterator has more elements when traversing
   *         the iterator in the reverse direction.
   */
  public boolean hasPrev();


  /**
   * Returns the size of given iterator as int.<p>
   *
   * @return the size of iterator.
   */
  public int size();


  /**
   * Returns the current iterator position as int.<p>
   *
   * @return the current iterator position.
   */
  public int currentPosition();


  /**
   * Shifts iterator to a particular position and returns object pointed to it. This position
   * is NOT an offset from the current one, as it is in next() and prev() methods.
   * It is an offset from the beginning of the underlying data structure.<p>
   *
   * @param position the position where iterator has to be shifted.
   * @return the object at the position where iterator has to be shifted.
   */
  public Object jumpTo(int position);

}

