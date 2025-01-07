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

import java.io.Serializable;

/**
 * Parent class for all data structures, that work with primitive types like int, float, and so on.<p>
 *
 * @author Andrei Gatev
 * @version 1.0
 */
public abstract class PrimitiveTypeDataStructure implements Cloneable, Serializable {

  static final long serialVersionUID = -3234468849799419068L;

  protected int count;

  /**
   * Retrieves the count of the elements in the structure.<p>
   *
   * @return   the count of the elements in the structure.
   */
  public int size() {
    return count;
  }

  public boolean isEmpty() {
    return count == 0;
  }

  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException _) { //$JL-EXC$
      //never happens
      throw new InternalError();
    }
  }

  public abstract void clear();

}

