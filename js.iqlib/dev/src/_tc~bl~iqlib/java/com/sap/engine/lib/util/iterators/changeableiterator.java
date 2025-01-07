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
 * ChangeableIterator is the interface of all iterators which can change the object
 * on their current position.<p>
 *
 * @author Nikola Arnaudov, Andrei Gatev
 * @version 1.0
 */
public interface ChangeableIterator
  extends RootIterator {

  static final long serialVersionUID = 2375482430349316865L;    

  /**
   * Changes the object at the current iterator position with the specified one
   * and returns the old object.<p>
   *
   * @param obj the new object.
   * @return the old object.
   * @exception IteratorException if the pointer is outside the range of iterator
   *                              or the iterator is empty.
   */
  public Object change(Object obj);

}

