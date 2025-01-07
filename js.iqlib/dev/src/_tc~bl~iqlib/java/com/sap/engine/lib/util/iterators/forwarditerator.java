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
package com.sap.engine.lib.util.iterators;

/**
 * ForwardIterator is the interface of all iterators which can
 * add, insert, change, or remove objects.<p>
 *
 * @author Nikola Arnaudov, Andrei Gatev
 * @version 1.0
 */
public interface ForwardIterator
  extends InsertableIterator, RemoveableIterator, ChangeableIterator, AddableIterator {
  static final long serialVersionUID = 4489513614431613630L;
  
  /**
   * Returns the size of the given iterator as an int.<p>
   *
   * @return the size of iterator.
   */
  public int size();


  /**
   * Checks whether the iterator has an implementation of insert() method
   * and returns the result as a boolean.<p>
   *
   * @return true if there is one.
   */
  public boolean isInsertable();


  /**
   * Checks whether the iterator has an implementation of remove() method
   * and returns the result as a boolean.<p>
   *
   * @return true if there is one.
   */
  public boolean isRemoveable();


  /**
   * Checks whether the iterator has an implementation of change() method
   * and returns the result as a boolean.<p>
   *
   * @return true if there is one.
   */
  public boolean isChangeable();


  /**
   * Checks whether the iterator has an implementation of add() method
   * and returns the result as a boolean.<p>
   *
   * @return true if there is one.
   */
  public boolean isAddable();

}

