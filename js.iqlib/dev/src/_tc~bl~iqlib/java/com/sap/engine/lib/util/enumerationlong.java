/*
 * Copyright (c) 2001 by In-Q-My Technologies GmbH,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of In-Q-My Technologies GmbH. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with In-Q-My.
 */
package com.sap.engine.lib.util;

/**
 * An object that implements the EnumerationLong interface
 * generates a series of elements of type long, one at a time.
 * Successive calls to the nextElement method
 * return successive elements of the series.<p>
 *
 * @author Nikola Arnaudov, George Manev
 * @version 1.0
 */
public interface EnumerationLong {

  /**
   * Tests if this enumeration contains more elements.<p>
   *
   * @return  true if and only if this enumeration object
   *          contains at least one more element to provide,
   *          false otherwise.<p>
   */
  public boolean hasMoreElements();


  /**
   * Returns the next element of this enumeration if this enumeration
   * object has at least one more element to provide.<p>
   *
   * @return  the next element of this enumeration.<p>
   */
  public long nextElement();

}

