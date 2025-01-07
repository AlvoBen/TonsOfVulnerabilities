package com.sap.engine.objectprofiler.controller;

import com.sap.engine.objectprofiler.graph.Node;
import com.sap.engine.objectprofiler.graph.Graph;

import java.util.ArrayList;

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
 * Time: 10:12:36
 */
public interface PathFinder {
  public void setGraph(Graph graph);
  public ArrayList findPath(Node start, Node end);
}
