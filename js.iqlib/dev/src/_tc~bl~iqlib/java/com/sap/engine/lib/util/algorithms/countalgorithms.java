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

import com.sap.engine.lib.util.iterators.RootIterator;

/**
 * A class for obtainig number of definite objects represented by a given iterator.<p>
 *
 * @author Meglena Atanasova
 * @version 1.0
 */
public class CountAlgorithms extends ImmutableAlgorithm {

  /**
   * Returns the number of elements in a range that match a particular object using
   * the equals() method.<p>
   *
   * @param rootIterator an iterator for the range.
   * @param object the object to be counted.
   * @return the number of objects that matched.
   */
  public static int count(RootIterator rootIterator, Object object) {
    int n = 0;

    while (!rootIterator.isAtEnd()) {
      if (object.equals(rootIterator.next())) {
        ++n;
      }
    }

    return n;
  }

  /**
   * Return the number of elements in a range that satisfy a particular unary
   * predicate.<p>
   *
   * @param rootIterator an iterator for the range.
   * @param predicate the predicate to satisfy.
   * @return the number of objects.
   */
  public static int countIf(RootIterator rootIterator, UnaryPredicate predicate) {
    int n = 0;

    while (!rootIterator.isAtEnd()) {
      if (predicate.predicate(rootIterator.next())) {
        ++n;
      }
    }

    return n;
  }

}

