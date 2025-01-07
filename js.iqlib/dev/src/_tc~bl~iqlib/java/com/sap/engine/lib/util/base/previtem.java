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
 * An extension of the Item interface used for linear structures.<p>
 *
 * @author Miroslav Petrov
 * @version 1.0.3
 */
public interface PrevItem
  extends Item {
    
  static final long serialVersionUID = 5516289702762491358L;

  /**
   * Retrieves the successor of this item.<p>
   *
   * @return  the successor of this item.
   */
  public PrevItem getPrev();


  /**
   * Sets the successor of this item.<p>
   *
   * @param   pi the successor of this item.
   */
  public void setPrev(PrevItem pi);

}

