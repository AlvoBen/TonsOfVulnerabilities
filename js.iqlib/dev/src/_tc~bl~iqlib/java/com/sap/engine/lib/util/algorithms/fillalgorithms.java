﻿/*
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

/**
 * This class contains algorithms for filling structures, represented by iterators with
 * specific objects.<p>
 *
 * @author Meglena Atanasova
 * @version 1.0
 */
public class FillAlgorithms extends MutableAlgorithm {

  /**
   * A method to add n objects generated by GenerateFunction
   * after the last position of iterator.<p>
   *
   * @param addIterator an AddableIterator.
   * @param function a function that generates object to add.
   * @param n the number of objects to add.
   */
  public static void generate(AddableIterator addIterator, GenerateFunction function, int n) {
    while (n-- > 0) {
      addIterator.add(function);
    }
  }

  /**
   * Add n particular objects after the last position of iterator.<p>
   *
   * @param addIterator an AddableIterator.
   * @param object the object to add.
   * @param n the number of objects to add.
   */
  public static void generateO(AddableIterator addIterator, Object object, int n) {
    while (n-- > 0) {
      addIterator.add(object);
    }
  }

  /**
   * Insert n objects generated by GenerateFunction at specific position of iterator.<p>
   *
   * @param insertIterator an InsertableIterator.
   * @param function a function that generates the object to add.
   * @param n the number of objects to add.
   */
  public static void igenerate(InsertableIterator insertIterator, GenerateFunction function, int n) {
    while (n-- > 0) {
      insertIterator.insert(function);
    }
  }

  /**
   * Insert n particular objects at specific position of iterator.<p>
   *
   * @param insertIterator an InsertableIterator.
   * @param object the object to add.
   * @param n the number of objects to add.
   */
  public static void igenerateO(InsertableIterator insertIterator, Object object, int n) {
    while (n-- > 0) {
      insertIterator.insert(object);
    }
  }

  /**
   * Change objects pointed by iterator with a particular object.<p>
   *
   * @param changeIterator a ChangeableIterator.
   * @param object the object to be added.
   */
  public static void fillO(ChangeableIterator changeIterator, Object object) {
    while (!changeIterator.isAtEnd()) {
      changeIterator.change(object);
    }
  }

  /**
   * Change objects pointed by iterator with objects generated by fuction.<p>
   *
   * @param changeIterator a ChangeableIterator.
   * @param function a function that generates the object to be added.
   */
  public static void fill(ChangeableIterator changeIterator, GenerateFunction function) {
    while (!changeIterator.isAtEnd()) {
      changeIterator.change(function);
    }
  }

}

