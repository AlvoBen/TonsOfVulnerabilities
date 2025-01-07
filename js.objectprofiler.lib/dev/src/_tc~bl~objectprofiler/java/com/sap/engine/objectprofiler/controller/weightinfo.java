package com.sap.engine.objectprofiler.controller;

import com.sap.engine.objectprofiler.graph.Node;

import java.util.HashMap;

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
 * Time: 10:28:07
 */
public class WeightInfo {
  private HashMap nodes = new HashMap();
  private double totalWeight = 0;

  public void addNode(Node node, double weight) {
    nodes.put(node, new Double(weight));

    totalWeight += weight;
  }

  public double getWeight(Node node) throws Exception {
    Double weight = (Double)nodes.get(node);

    if (weight == null) {
      throw new Exception("Node not found!");
    }

    return weight.doubleValue();
  }

  public double getTotalWeight() {
    return totalWeight;
  }

  public boolean containsWeight(Node node) {
    boolean res = true;
    
    Double weight = (Double)nodes.get(node);

    if (weight == null) {
      res = false;
    }

    return res;
  }

}
