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
package com.sap.engine.lib.util.algorithms;

import com.sap.engine.lib.util.iterators.AddableIterator;
import com.sap.engine.lib.util.iterators.ChangeableIterator;
import com.sap.engine.lib.util.iterators.InsertableIterator;
import com.sap.engine.lib.util.iterators.RootIterator;

/**
 * Punlic class CopyAlgorithms class contains algorithms for copying, inserting and swaping.<p>
 *
 * @author Meglena Atanasova
 * @version 1.0
 */
public class CopyAlgorithms extends MutableAlgorithm {

  /**
   * A method to copy the elements from one range at the end of another.<p>
   *
   * @param rootIterator the root iterator for the range that will be copied at the end
   * 							  of the other.
   * @param addIterator  addable iterator at the end of which the first iterator
   * 							  will be copied.
   */
  public static void copy(RootIterator rootIterator, AddableIterator addIterator) {
    while (!rootIterator.isAtEnd()) {
      addIterator.add(rootIterator.next());
    }
  }

  /**
   * A method to insert the elements from one range to another at specific position.<p>
   *
   * @param rootIterator the root iterator for the range that will be inserted
   * 							  in the other.
   * @param insertIterator  addable iterator in which the first iterator
   * 							     will be inserted.
   */
  public static void insert(RootIterator rootIterator, InsertableIterator insertIterator) {
    while (!rootIterator.isAtEnd()) {
      insertIterator.insert(rootIterator.next());
    }
  }

  /**
   * Swap the objects pointed by two iterators.
   *
   * @param changeIterator1 a changeable iterator for the first range.
   * @param changeIterator2  a changeable  iterator for the second range.
   */
  public static void swap(ChangeableIterator changeIterator1, ChangeableIterator changeIterator2) {
    while (!changeIterator1.isAtEnd() || !changeIterator2.isAtEnd()) {
      Object temp = changeIterator1.get();
      changeIterator1.change(changeIterator2.get());
      changeIterator2.change(temp);
      changeIterator1.next();
      changeIterator2.next();
    }
  }

}

