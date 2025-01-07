package com.sap.engine.objectprofiler.controller;

import java.io.Serializable;

/**
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 * <p/>
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * <p/>
 * User: Pavel Bonev
 * Date: 2005-7-7
 * Time: 16:53:20
 */
public interface GraphFilter extends Serializable {

  public boolean filter(Object obj);
  public boolean filterByDescription(String obj);
}
