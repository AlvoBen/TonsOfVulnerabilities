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
 * BidirectionalIterator is the interface of all iterators which can
 * add, insert, change, or remove objects and can browse the
 * underlying data structure forwards and backwards.<p>
 *
 * @author Nikola Arnaudov, Andrei Gatev
 * @version 1.0
 */
public interface BidirectionalIterator
  extends ForwardIterator {

  static final long serialVersionUID = -7305215341874660880L;
  
  /**
   * Shifts the pointer to the previous element from the data structure and
   * returns object at that position.<p>
   * @return the object at the previous position of iterator.
   * @exception IteratorException if the pointer is outside the range of iterator
   *                              or the iterator is empty.
   */
  public Object prev();


  /**
   * Shifts the pointer with n + 1 positions and returns the pointed object.<p>
   *
   * @param n the number of positions that have to be skipped.
   * @return the object at the previous position - n.
   * @exception IteratorException if the pointer is outside the range of iterator
   *                              or the iterator is empty.
   */
  public Object prev(int n);

}

