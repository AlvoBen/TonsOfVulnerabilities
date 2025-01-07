﻿/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.util;

/**
 * Object pool new instance creator interface.<p>
 *
 * @author Nikola Arnaudov
 * @version 1.00
 */
public interface PoolInstanceCreator {

  /**
   * Creates a new instance of the pool.<p>
   *
   * @return  the created new instance.<p>
   */
  public Object newInstance();

}

