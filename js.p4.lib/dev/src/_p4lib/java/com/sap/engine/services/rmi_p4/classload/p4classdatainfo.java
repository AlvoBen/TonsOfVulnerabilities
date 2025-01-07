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
package com.sap.engine.services.rmi_p4.classload;

/**
 * @author Mladen Droshev
 * @version 7.0
 */

public class P4ClassDataInfo {

  public P4ClassDataInfo(long classPosition, int classLength) {
    this.classPosition = classPosition;
    this.classLength = classLength;
  }

  public long classPosition = 0;

  public int classLength = 0;

}
