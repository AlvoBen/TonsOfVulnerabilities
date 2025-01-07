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

import com.sap.engine.lib.util.iterators.ChangeableIterator;

/**
 * Replace a particular object from range.<p>
 *
 * @author Meglena Atanasova
 * @version 1.0
 */
public class ReplaceAlgorithms extends MutableAlgorithm {

  /**
   * Replace all occurrences of an definite object from a range with another given.<p>
   *
   * @param changeIterator  an iterator that points to objects from the range.
   * @param object1 the object to replace.
   * @param object2 the new object to replace object1 with.
   */
  public static void replace(ChangeableIterator changeIterator, Object object1, Object object2) {
    if (changeIterator.isAtBegin()) {
      if (changeIterator.get().equals(object1)) {
        changeIterator.change(object2);
      }
    }

    while (!changeIterator.isAtEnd()) {
      if (changeIterator.next().equals(object1)) {
        changeIterator.next(-2);
        changeIterator.change(object2);
      }
    }
  }

  /**
   * Replace all occurrences of an definite object that satisfys particular unary
   * predicate from a range with another given.<p>
   *
   * @param changeIterator  an iterator that points to objects from the range.
   * @param predicate the predicate to satisfy.
   * @param object the new object to replace with.
   */
  public void replaceIf(ChangeableIterator changeIterator, UnaryPredicate predicate, Object object) {
    while (!changeIterator.isAtEnd()) {
      if (predicate.predicate(changeIterator.next())) {
        changeIterator.change(object);
      }
    }
  }

}

