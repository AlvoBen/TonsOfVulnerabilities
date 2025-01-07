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
 * ListPool new instance creator interface.<p>
 *
 * @author Nikola Arnaudov
 * @version 1.00
 */
public interface ListPoolInstanceCreator {

  /**
   * Creates a new instance.<p>
   *
   * @return   this new instance.
   */
  public NextItem newInstance();

}

