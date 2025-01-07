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
package com.sap.engine.lib.util;

/**
 * The parent class for all data structures, available in
 * com.sap.engine.lib.util library.<p>
 *
 * @author Miroslav Petrov
 * @version 1.0.3
 */
public abstract class AbstractDataStructure implements RootDataStructure {

  static final long serialVersionUID = -4019700619739286916L;

  /**
   * Number of elements in data structure
   */
  protected int count;

  /**
   * Retrieves the count of the elements in the structure.<p>
   *
   * @return   the count of the elements in the structure
   */
  public int size() {
    return count;
  }

  /**
   * Checks if the structure is empty.<p>
   *
   * @return true if the structure has no elements
   * and false otherwise
   */
  public boolean isEmpty() {
    return count == 0;
  }

  /**
   * Creates and returns a copy of this structure object.<p>
   *
   * @return  a clone of this instance.
   */
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException _) { //$JL-EXC$
      //never happens
      throw new InternalError();
    }
  }

}

