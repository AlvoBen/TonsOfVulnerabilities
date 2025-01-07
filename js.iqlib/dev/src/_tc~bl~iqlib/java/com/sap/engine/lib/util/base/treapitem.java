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
 * An extension of the Item interface used by treap structures.<p>
 *
 * @author Nikola Arnaudov
 * @version 1.0.3
 */
public interface TreapItem
  extends BinTreeItem {

  static final long serialVersionUID = -662286136872308395L;

  /**
   * Gets priority of node.<p>
   *
   * @return   the priority of node.
   */
  public int getPriority();


  /**
   * Sets node priority.<p>
   *
   * @param   priority the node priority.
   */
  public void setPriority(int priority);

}

