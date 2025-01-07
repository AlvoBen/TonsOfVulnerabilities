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
 * Standard implementation of the LinearItem interface.<p>
 *
 * @author Miroslav Petrov
 * @version 1.0.3
 */
public class LinearItemAdapter extends ItemAdapter implements LinearItem { //$JL-CLONE$
  
  static final long serialVersionUID = 394445051499449921L;
  
  /**
   * Successor of the item.<p>
   */
  protected NextItem next = null;
  /**
   * Predecessor of the item.<p>
   */
  protected PrevItem prev = null;

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
   * Sets the predecessor of the item.<p>
   *
   * @param   item the predecessor of this item.
   */
  public void setPrev(PrevItem item) {
    prev = item;
  }

  /**
   * Retrieves the predecessor of the item.<p>
   *
   * @return  the predecessor of the item.
   */
  public PrevItem getPrev() {
    return prev;
  }

  /**
   * Prepare item to be pooled.<p>
   */
  public void clearItem() {
    prev = null;
    next = null;
  }

}

