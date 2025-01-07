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
 * Class contains algorithms for finding specific objects.<p>
 *
 * @author Meglena Atanasova
 * @version 1.0
 */
public class FindAlgorithms extends ImmutableAlgorithm {

  /**
   * Find the first element in a range that matches a particular object using equals().<p>
   *
   * @param rootIterator an iterator that points to a range of objects.
   * @param object the object to find.
   * @return an iterator positioned at the first element that matches. If no match is
   *		 found, return an iterator positioned immediately after the last element of
   * 		 the range.
   */
  public static RootIterator find(RootIterator rootIterator, Object object) {
    while (!rootIterator.isAtEnd()) {
      if (rootIterator.get().equals(object)) {
        return rootIterator;
      }

      rootIterator.next();
    }

    return rootIterator;
  }

  /**
   * Find the first element in a range that satisfys a particular unary predicate.<p>
   *
   * @param rootIterator an iterator that points to a range of objects.
   * @param predicate the predicate to satisfy.
   * @return an iterator positioned at the first element that matches. If no match is
   *		 found, return an iterator positioned immediately after the last element of
   *		 the range.
   */
  public static RootIterator findIf(RootIterator rootIterator, UnaryPredicate predicate) {
    while (!rootIterator.isAtEnd()) {
      if (predicate.predicate(rootIterator.get())) {
        return rootIterator;
      }

      rootIterator.next();
    }

    return rootIterator;
  }

  /**
   * Find the first two adjacent elements in a range that matches a particular object.<p>
   *
   * @param rootIterator an iterator that points to a range of objects.
   * @param object1  the first object to find.
   * @param object2  The second object to find.
   * @return an iterator positioned at the second element of pair that matches.
   *		 If no match is found, return an iterator positioned immediately after the
   *		 last element of the range.
   */
  public static RootIterator adjacentFind(RootIterator rootIterator, Object object1, Object object2) {
    while (!rootIterator.isAtEnd()) {
      Object prev = rootIterator.next();

      if ((prev.equals(object1) && (rootIterator.next().equals(object2)))) {
        rootIterator.next(-2);
        return rootIterator;
      }
    }

    return rootIterator;
  }

  /**
   * Find the first two adjacent elements in a range that satisfys a particular binary.<p>
   * predicate.
   *
   * @param rootIterator an iterator that points to a range of objects.
   * @param predicate the predicate to satisfy.
   * @return an iterator positioned at the second element of pair that matches.
   *		 If no match is found, return an iterator positioned immediately after the
   *		 last element of the range.
   */
  public static RootIterator adjacentFindIf(RootIterator rootIterator, BinaryPredicate predicate) {
    while (!rootIterator.isAtEnd()) {
      Object prev = rootIterator.next();

      if (predicate.predicate(prev, rootIterator.next())) {
        rootIterator.next(-2);
        return rootIterator;
      }
    }

    return rootIterator;
  }

}

