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
package com.sap.engine.services.jndi.persistent;

/**
 * Enumeration for JNDI's Handle
 *
 * @author Panayot Dobrikov
 * @version 4.00
 */
public interface JNDIHandleEnumeration {

  /**
   *  Returns next object in the enumeration
   *
   * @return     Next object in the enumeration
   */
  public JNDIHandle nextObject();


  /**
   *  Returns the condition wether there are more elements in the enumeration
   *
   * @return     True if there are more elements in the enumeration
   */
  public boolean hasMoreElements();


  /**
   *  Closes the enumeration
   *
   */
  public void closeEnumeration();

}

