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
 * Apply some function over all objects pointed by iterator.<p>
 *
 * @author Meglena Atanasova
 * @version 1.0
 */
public class ForEachAlgorithms extends ImmutableAlgorithm {

  /**
   * Apply a particular unary function on objects pointed by iterator.<p>
   *
   * @param rootIterator an iterator that points to a range of objects.
   * @param function the function to apply.
   */
  public static void forEach(RootIterator rootIterator, UnaryFunction function) {
    while (!rootIterator.isAtEnd()) {
      function.function(rootIterator.next());
    }
  }

  /**
   * Applied a binary function that takes for the first time as parameter an initial
   * object and each time takes the produced object from previous iteration,
   * for the second parameter takes objects pointed by iterator.<p>
   *
   * @param rootIterator an iterator that points to a range of objects.
   * @param object the initial object.
   * @param function the binary function to apply.
   * @return the accumulated object.
   */
  public static Object accumulate(RootIterator rootIterator, Object object, BinaryFunction function) {
    while (!rootIterator.isAtEnd()) {
      object = function.function(object, rootIterator.next());
    }

    return object;
  }

}

