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
 * Standard implementation of the NextItem interface.<p>
 *
 * @author Miroslav Petrov
 * @version 1.0.3
 */
public class NextItemAdapter extends ItemAdapter implements NextItem {  //$JL-CLONE$

  static final long serialVersionUID = 8951763471775022372L;
  /**
   * Successor of the item.
   */
  protected NextItem next = null;

  /**
   * Sets the successor of this item.<p>
   *
   * @param   item the successor of this item.
   */
  public void setNext(NextItem item) {
    next = item;
  }

  /**
   * Retrieves the successor of this item.<p>
   *
   * @return  the successor of this item.
   */
  public NextItem getNext() {
    return next;
  }

  /**
   * Prepare item to be pooled.<p>
   *
   */
  public void clearItem() {
    next = null;
  }

}

