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
 * An extension of the Item interface used by AVL structures.<p>
 *
 * @author Nikola Arnaudov
 * @version 1.0.3
 */
public interface AVLItem
  extends BinTreeItem {
    
  static final long serialVersionUID = -1349785327298897305L;

  /**
   * Gets balance of the node.<p>
   *
   * @return   the balance of node.
   */
  public int getBalance();


  /**
   * Sets node balance.<p>
   *
   * @param   balance node balance.
   */
  public void setBalance(int balance);

}

