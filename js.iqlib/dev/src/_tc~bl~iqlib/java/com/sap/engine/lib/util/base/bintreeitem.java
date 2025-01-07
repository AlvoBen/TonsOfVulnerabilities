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
 * An extension of the Item interface used binary tree structures.<p>
 *
 * @author Miroslav Petrov
 * @version 1.0.3
 */
public interface BinTreeItem
  extends Item {
    
  static final long serialVersionUID = 5258906039937066932L;

  /**
   * Gets search key.<p>
   *
   * @return  the search key.
   */
  public Comparable getKey();


  /**
   * Set search key.<p>
   *
   * @param   key the search key.
   */
  public void setKey(Comparable key);


  /**
   * Retrieves the left successor of the item.<p>
   *
   * @return  the left successor of the item.
   */
  public BinTreeItem getLeft();


  /**
   * Sets the left successor of the item.<p>
   *
   * @param   bti the left successor of the item.
   */
  public void setLeft(BinTreeItem bti);


  /**
   * Retrieves the right successor of the item.<p>
   *
   * @return  the right successor of the item.
   */
  public BinTreeItem getRight();


  /**
   * Sets the right successor of the item.<p>
   *
   * @param   bti the left successor of the item.
   */
  public void setRight(BinTreeItem bti);

}

