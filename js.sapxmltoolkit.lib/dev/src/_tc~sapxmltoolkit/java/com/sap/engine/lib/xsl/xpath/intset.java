/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.xsl.xpath;

/**
 * @author              Nick Nickolov, e-mail nick_nickolov@abv.bg
 * @version             0.0.1
 *
 * @deprecated
 */
/**
 *   Classes that optimize operations a set of integers implement this interface.
 */
public interface IntSet {

  /**
   *   Add an int to the set.
   */
  void add(int x);


  /**
   *   Remove an int from the set.
   */
  void remove(int x);


  /**
   *   Add all integers between x and y to the set.
   */
  void addInterval(int x, int y);


  /**
   *   Remove all integers between x and y from the set.
   */
  void removeInterval(int x, int y);


  /**
   *   Checks if a certain int is present in the set.
   */
  boolean contains(int x);


  /**
   *
   */
  //void uniteWith(IntSet set);
  /**
   *   Makes the set contain no elements.
   */
  void clear();


  /**
   *   Checks if the set has no elements.
   */
  boolean isEmpty();


  /**
   *   Returns the number of elements in the set.
   */
  int count();


  /**
   *   Returns an iterator over the elements of the set
   * in forward direction.
   */
  IntSetIterator iterator();


  /**
   *   Returns an iterator over the elements of the set
   * in reverse direction.
   */
  IntSetIterator backIterator();


  /**
   *   Returns the k-th element in the set in ascending order, or -1 if no such element exists.
   *   Counting starts from 1.
   */
  int getKth(int k);


  /**
   *
   */
  int getFirst();


  int getLast();


  /**
   *
   */
  String toString();

}

