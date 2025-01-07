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
 * Transform objects pointed by given iterator.<p>
 *
 * @author Meglena Atanasova
 * @version 1.0
 */
public final class TransaformAlgorithms extends ImmutableAlgorithm {

  /**
   * Apply a particular unary function on objects pointed by iterator to change them.<p>
   *
   * @param changeIterator an iterator for the range.
   * @param function the function to apply.
   */
  public static void apply(ChangeableIterator changeIterator, UnaryFunction function) {
    while (!changeIterator.isAtEnd()) {
      changeIterator.change(function.function(changeIterator.next()));
    }
  }

}

