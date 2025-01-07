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

import com.sap.engine.lib.util.iterators.RemoveableIterator;

/**
 * Remove a particular object from a range.<p>
 *
 * @author Meglena Atanasova
 * @version 1.0
 */
public class RemoveAlgorithms extends MutableAlgorithm {

  /**
   * Remove all occurrences of an object from a range.<p>
   *
   * @param removeIterator an iterator that points to objects from the range.
   * @param object the object to remove.
   */
  public static void remove(RemoveableIterator removeIterator, Object object) {
    if (removeIterator.isAtBegin()) {
      if (object.equals(removeIterator.get())) {
        removeIterator.remove();
      }
    }

    while (!removeIterator.isAtEnd()) {
      if (object.equals(removeIterator.next())) {
        removeIterator.next(-2);
        removeIterator.remove();
      }
    }
  }

  /**
   * Remove all occurrences of an objects that satisfy particular unary predicate.<p>
   *
   * @param removeIterator an iterator that points to objects from the range.
   * @param predicate the predicate to satisfy.
   */
  public static void removeIf(RemoveableIterator removeIterator, UnaryPredicate predicate) {
    while (!removeIterator.isAtEnd()) {
      if (predicate.predicate(removeIterator.next())) {
        removeIterator.remove();
      }
    }
  }

}

