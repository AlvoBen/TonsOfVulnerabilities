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
 * RemoveableIterator is the interface of all iterators that can remove objects
 * from the underlying data structure.<p>
 *
 * @author Nikola Arnaudov, Andrei Gatev
 * @version 1.0
 */
public interface RemoveableIterator
  extends RootIterator {
  static final long serialVersionUID = -1362668618343367055L;

  /**
   * Removes an object at the current iterator position and returns it.<p>
   *
   * @return the removed object.
   * @exception IteratorException if the pointer is outside the range of iterator
   *                              or the iterator is empty.
   */
  public Object remove();

}

