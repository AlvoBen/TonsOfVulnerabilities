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
 * RandomAccessIterator offers two additional methods to the functionality
 * of BidirectionalIterator.<p>
 *
 * @author Nikola Arnaudov, Andrei Gatev
 * @version 1.0
 */
public interface RandomAccessIterator
  extends BidirectionalIterator {
  static final long serialVersionUID = -1400623986710112925L;    

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
   * @param position the p0osition where the iterator has to be shifted.
   * @return the object at the position where the iterator has to be shifted.
   * @exception IteratorException if the specified position is outside the range of iterator
   *                              or the iterator is empty.
   */
  public Object jumpTo(int position);

}

