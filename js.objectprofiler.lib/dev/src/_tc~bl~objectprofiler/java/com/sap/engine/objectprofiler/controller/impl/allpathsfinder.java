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

package com.sap.engine.objectprofiler.controller.impl;

import com.sap.engine.objectprofiler.graph.Graph;
import com.sap.engine.objectprofiler.graph.Reference;
import com.sap.engine.objectprofiler.graph.Node;
import com.sap.engine.objectprofiler.controller.PathFinder;

import java.util.ArrayList;
import java.util.Vector;
import java.util.Set;
import java.util.HashSet;

/**
 * @author Georgi Stanev, Mladen Droshev
 * @version 7.10C
 */
public class AllPathsFinder implements PathFinder {
  Graph graph = null;

  public AllPathsFinder() {
  }

  public AllPathsFinder(Graph gr) {
    setGraph(gr);
  }

  public ArrayList findPath(Node start, Node end) {
    ArrayList res = new ArrayList();
    ArrayList currentPath = new ArrayList();
    ArrayList visited = new ArrayList();
    HashSet pathNodes = new HashSet();

    findAllPathsRec(start, end, res, currentPath, visited, pathNodes);

    return res;
  }


  private void findAllPathsRec(Node start, Node end, ArrayList res, ArrayList currentPath, ArrayList visited, HashSet pathNodes) {
    if (currentPath.contains(start)) {
      return;
    }

    if (visited.contains(start) && !pathNodes.contains(start)) {
      return;
    }

    currentPath.add(start);
    visited.add(start);

    if (start == end) {
      res.add(currentPath.clone());
      pathNodes.addAll(currentPath);
    } else {
      Node[] kids = graph.getChildren(start);
      if (kids != null) {
        for (int i=0;i<kids.length;i++) {
          findAllPathsRec(kids[i], end, res, currentPath, visited, pathNodes);
        }
      }
    }

    currentPath.remove(currentPath.size()-1);
  }

  private void printPath(ArrayList list) {
    for (int i=0;i<list.size();i++) {
      Node node = (Node)list.get(i);
      System.out.print(node.getId()+ " ");
    }
    System.out.println();
  }

  public String toString() {
    return "Find All Paths";
  }

  public void setGraph(Graph graph) {
    this.graph = graph;
  }


}
