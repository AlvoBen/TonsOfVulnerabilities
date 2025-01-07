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

import com.sap.engine.lib.util.RootDataStructure;

import java.io.Serializable;

/**
 * RootIterator is an interface which offers basic functionality for all iterators.<p>
 *
 * @author Nikola Arnaudov, Andrei Gatev
 * @version 1.0
 */
public interface RootIterator
  extends Cloneable, Serializable {
    
  static final long serialVersionUID = -3983017530047930853L;

  /**
   * Returns an object at the current iterator position.<p>
   *
   * @return an object pointed by current iterator position.
   * @exception IteratorException if the pointer is outside the range of iterator
   *                              or the iterator is empty.
   */
  public Object get();


  /**
   * Checks whether iterator is at starting position and returns boolean result.<p>
   *
   * @return true if pointer is over the first element of the iterator.
   */
  public boolean isAtBegin();


  /**
   * Checks whether iterator is at the end and returns boolean result.<p>
   *
   * @return true if there are no more elements.
   */
  public boolean isAtEnd();


  /**
   * Returns the data structure browsed by this iterator.<p>
   *
   * @return the data structure.
   */
  public RootDataStructure getDataStructure();


  /**
   * Returns object at the current position and shifts the pointer to the next element
   * from the data structure.<p>
   *
   * @return the object at the current position of iterator.
   * @exception IteratorException if the pointer is outside the range of iterator
   *                              or the iterator is empty.
   */
  public Object next();


  /**
   * Shifts the pointer with n positions, returns the pointed object and
   * shifts the pointer one more time.<p>
   *
   * @param n the number of positions that have to be skipped.
   * @return the object at the current position + n.
   * @exception IteratorException if the pointer is outside the range of iterator
   *                              or the iterator is empty.
   */
  public Object next(int n);


  /**
   * Sets the start of this iterator at the position of the pointer from the specified
   * iterator. The pointer of this iterator is set at its new beginning.<p>
   *
   * @exception IteratorException if the pointer is outside the range of iterator
   *                              or the iterator is empty.
   */
  public void setStartFromIterator(RootIterator iterator);


  /**
   * Sets the end of this iterator at the position of the pointer from the specified
   * iterator. The pointer of this iterator remains unchanged.<p>
   *
   * @exception IteratorException if the pointer is outside the range of iterator
   *                              or the iterator is empty.
   */
  public void setEndFromIterator(RootIterator iterator);

}

