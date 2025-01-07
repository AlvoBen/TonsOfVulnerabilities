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

import java.io.Serializable;

/**
 * An interface that should be implemented by all objects that could be stored
 * in the data structures provided in com.niqmy.lib.util.base library.
 *
 * @author Miroslav Petrov
 * @version 1.0.3
 */
public interface Item
  extends Cloneable, Serializable {
  static final long serialVersionUID = -3457323594966371870L;
  
  /**
   * Clones the item.<p>
   *
   * @return the cloning of the object.
   */
  public Object clone();


  /**
   * Prepare item to be pooled.<p>
   *
   */
  public void clearItem();

}

