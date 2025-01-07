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
 * Pointer to a wrapped value.<p>
 *
 * @author Nikola Araudov
 * @version 4.0.3
 */
public interface Pointer {

  /**
   *  Gets the wrapped value.<p>
   *
   * @return     the wrapped value.
   */
  public Object getElement();


  /**
   *  Sets the wrapped element.<p>
   *
   * @param   object the new wrapped value.
   * @return  the old wrapped value.
   */
  public Object setElement(Object object);

}

