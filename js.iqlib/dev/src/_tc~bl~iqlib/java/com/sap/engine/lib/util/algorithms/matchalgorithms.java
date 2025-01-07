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
 * Contains algorithms for matching objects pointed by two iterators.<p>
 *
 * @author Meglena Atanasova
 * @version 1.0
 */
public class MatchAlgorithms extends ImmutableAlgorithm {

  /**
   * Find the first pair of elements in a range that does not matches each other.<p>
   *
   * @param rootIterator1 the first iterator that points to a range of objects.
   * @param rootIterator2 the second iterator that points to a range of objects.
   * @return an array that contains iterators positioned at the first elements that
   *		 does not matches. If no missmatch is found, return null??.
   */
  public static Object[] missMatch(RootIterator rootIterator1, RootIterator rootIterator2) {
    Object[] objects = new Object[2];

    while ((!rootIterator1.isAtEnd() && !rootIterator2.isAtEnd())) {
      if (!((rootIterator1.get().equals(rootIterator2.get())))) {
        objects[0] = rootIterator1.get();
        objects[1] = rootIterator2.get();
      }

      rootIterator1.next();
      rootIterator2.next();
    }

    return objects;
  }

  /**
   * Find the first pair of elements in a range that does not satisfy particular
   * binary predicate.<p>
   * @param rootIterator1 the first iterator that points to a range of objects.
   * @param rootIterator2 the second iterator that points to a range of objects.
   * @param predicate the predicate to satisfy.
   * @return an array that contains iterators positioned at the first elements that
   *		 does not matches. If no missmatch is found, return null??.
   */
  public static Object[] missMatchIf(RootIterator rootIterator1, RootIterator rootIterator2, BinaryPredicate predicate) {
    Object[] objects = new Object[2];

    while (!rootIterator1.isAtEnd() && !rootIterator2.isAtEnd()) {
      if (predicate.predicate(rootIterator1.get(), rootIterator2.get())) {
        objects[0] = rootIterator1.get();
        objects[1] = rootIterator2.get();
      }

      rootIterator1.next();
      rootIterator2.next();
    }

    return objects;
  }

  /**
   * Scan two ranges of the same size and return true if every element in one
   * range matches its counterpart.<p>
   *
   * @param rootIterator1 the first iterator that points to a range of objects.
   * @param rootIterator2 the second iterator that points to a range of objects.
   * @return true if matches and false if does not.
   */
  public static boolean equals(RootIterator rootIterator1, RootIterator rootIterator2) {
    int sizeR1 = rootIterator1.getDataStructure().size();
    int sizeR2 = rootIterator2.getDataStructure().size();

    if (sizeR1 - sizeR2 == 0) {
      while (!(rootIterator1.isAtEnd()) || !(rootIterator2.isAtEnd())) {
        if (!(rootIterator1.next().equals(rootIterator2.next()))) {
          return false;
        }
      }

      return true;
    }

    return false;
  }

  /**
   * Scan two ranges of the same size and return true if every element in one
   * range and its counterpart satisfy definite binary predicate.<p>
   *
   * @param rootIterator1 the first iterator that points to a range of objects.
   * @param rootIterator2 the second iterator that points to a range of objects.
   * @param predicate the predicate to satisfy.
   * @return true if matches and false if does not.
   */
  public static boolean equalsIf(RootIterator rootIterator1, RootIterator rootIterator2, BinaryPredicate predicate) {
    int sizeR1 = rootIterator1.getDataStructure().size();
    int sizeR2 = rootIterator2.getDataStructure().size();

    if (sizeR1 - sizeR2 == 0) {
      while (!rootIterator1.isAtEnd() && !rootIterator2.isAtEnd()) {
        if (!predicate.predicate(rootIterator1.next(), rootIterator2.next())) {
          return false;
        }
      }

      return true;
    }

    return false;
  }

}

