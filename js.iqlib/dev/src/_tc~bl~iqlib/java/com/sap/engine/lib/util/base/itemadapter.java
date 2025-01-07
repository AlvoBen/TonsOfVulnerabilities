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
package com.sap.engine.lib.util.base;

/**
 * Standard implementation of the Item interface.<p>
 *
 * @author Miroslav Petrov
 * @version 1.0.3
 */
public abstract class ItemAdapter implements Item {

  static final long serialVersionUID = 4913663995089581660L;
  /**
   * Clones the item.<p>
   *
   * @return the cloning of the object.
   */
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException exc) {
      // never coming here because ItemAdapter implements Cloneable via Item
      return null;
    }
  }

}

