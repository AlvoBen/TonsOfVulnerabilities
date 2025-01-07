package com.sap.engine.objectprofiler.controller.impl;

import com.sap.engine.objectprofiler.controller.MemoryCalculator;
import com.sap.engine.objectprofiler.controller.WeightInfo;
import com.sap.engine.objectprofiler.graph.Graph;
import com.sap.engine.objectprofiler.graph.Node;

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
 * Date: 2005-4-13
 * Time: 14:42:49
 */
public class DeltaMemoryCalculator implements MemoryCalculator {
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
    ArrayList visited = new ArrayList();
    Node root = graph.getRoot();

//    Node[] nodes = graph.getNodes();
//    for (int i=0;i<nodes.length;i++) {
//      visited.clear();
//
//      if (nodes[i] != root) {
//        visited.add(root);
//      }
//      double size = calcSubgraphWeight(nodes[i], visited);
//      info.addNode(nodes[i], size);
//    }
    calcSubgraphWeight(root, visited);
  }

  private int calcSubgraphWeight(Node a, ArrayList visited) {
    if (visited.contains(a)) {
      return 0;
    }

//    try {
//      int w = (int)info.getWeight(a);
//      return w;
//    } catch (Exception e) {
//    }

    int weight = a.getWeight();
    visited.add(a);
    Node[] kids = graph.getChildren(a);
    if (kids != null) {
      for (int i = 0; i < kids.length; i++) {
        weight += calcSubgraphWeight(kids[i], visited);
      }
    }

    info.addNode(a, weight);
    return weight;
  }

  public WeightInfo getWeight() {
    return info;
  }

  public String toString() {
    return "Delta Memory Calculator";
  }
}
