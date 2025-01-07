package com.sap.engine.objectprofiler.controller;

import com.sap.engine.objectprofiler.graph.Graph;

/**
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 *
 * User: Pavel Bonev
 * Date: 2005-3-17
 * Time: 10:27:47
 */
public interface MemoryCalculator {
  public void setGraph(Graph graph);
  public void calculateWeight();
  public WeightInfo getWeight();
}
