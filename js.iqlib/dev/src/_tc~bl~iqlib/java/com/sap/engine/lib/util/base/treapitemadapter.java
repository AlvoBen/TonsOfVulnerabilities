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
 * Standard implementation of the TreapItem interface.<p>
 *
 * @author Nikola Arnaudov
 * @version 1.0.3
 */
public class TreapItemAdapter extends BinTreeItemAdapter implements TreapItem {

  static final long serialVersionUID = 4663370069536504335L;

  /**
   * Priority.<p>
   */
  protected int priority;

  /**
   * Gets priority of node.<p>
   *
   * @return   the priority of node.
   */
  public int getPriority() {
    return priority;
  }

  /**
   * Sets node priority.<p>
   *
   * @param   priority the node priority.
   */
  public void setPriority(int priority) {
    this.priority = priority;
  }

}

