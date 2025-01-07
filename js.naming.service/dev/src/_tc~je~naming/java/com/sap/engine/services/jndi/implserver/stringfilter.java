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
package com.sap.engine.services.jndi.implserver;

import javax.naming.directory.*;

/**
 * Filter Interface used when searching
 *
 * @author Petio Petev
 * @version 4.00
 */
public interface StringFilter extends AttrFilter {

  /**
   * Starts parsing
   *
   * @throws InvalidSearchFilterException Thrown if a problem occures
   */
  public abstract void parse() throws InvalidSearchFilterException;

}

