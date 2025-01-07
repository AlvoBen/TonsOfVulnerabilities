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
 * Standard implementation of the AVLItem interface.<p>
 *
 * @author Nikola Arnaudov
 * @version 1.0.3
 */
public class AVLItemAdapter extends BinTreeItemAdapter implements AVLItem {
  
  static final long serialVersionUID = -2659782076964521607L;
  /**
   * Balance.<p>
   */
  protected int balance;

  /**
   * Gets balance of the node.<p>
   *
   * @return   the balance of node.
   */
  public int getBalance() {
    return balance;
  }

  /**
   * Sets node balance.<p>
   *
   * @param   balance node balance.
   */
  public void setBalance(int balance) {
    this.balance = balance;
  }

}

