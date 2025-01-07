package com.sap.engine.objectprofiler.controller.impl;

import com.sap.engine.objectprofiler.controller.MemoryCalculator;
import com.sap.engine.objectprofiler.controller.WeightInfo;
import com.sap.engine.objectprofiler.graph.Graph;
import com.sap.engine.objectprofiler.graph.Node;

import java.util.Collection;
import java.util.Iterator;

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
 * Date: 2005-3-22
 * Time: 16:51:40
 */
public class NativeSizeCalculator implements MemoryCalculator {
  private WeightInfo info = null;
  private Graph graph = null;

  public void setGraph(Graph graph) {
    this.graph = graph;
  }

  public void calculateWeight() {
    if (graph == null) {
      return;
    }

    info = new WeightInfo();

    Collection nodes = graph.getNodes();
    Iterator iterat = nodes.iterator();
    while (iterat.hasNext()) {
      Node node = (Node)iterat.next();
      double size = node.getWeight();
      info.addNode(node, size);
    }

  }

  public WeightInfo getWeight() {
    return info;
  }

  public String toString() {
    return "Native Size Calculator";
  }
}
