package com.sap.engine.objectprofiler.controller.impl;

import com.sap.engine.objectprofiler.controller.PathFinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.sap.engine.objectprofiler.graph.Node;
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
 * Time: 13:12:34
 */
public class ShortestPathFinder implements PathFinder {
  private Graph graph = null;

  public ShortestPathFinder() {
  }

  public ShortestPathFinder(Graph graph) {
   setGraph(graph);
  }

  public void setGraph(Graph graph) {
    this.graph = graph;
  }

  public String toString() {
    return "Find The Shortest Path";
  }

  public ArrayList findPath(Node start, Node end) {
    if (graph == null) {
      return null;
    }

    ArrayList result = new ArrayList();

    ArrayList visited = new ArrayList();
    ArrayList shortestPath = new ArrayList();
    ArrayList starts = new ArrayList();
    HashMap parents = new HashMap();

    starts.add(start);
    visited.add(start);

    if ((start == end) || (findPath(starts, end, shortestPath, visited, parents))) {
      result.add(shortestPath);
    }

    //printPath(shortestPath, start, end);

    return result;
  }

  public void printPath(ArrayList path, Node start, Node end) {
    System.out.print("SHORTEST PATH BETWEEN "+start.getId()+ " AND "+end.getId()+ " IS: ");
    if (path == null || path.size() == 0) {
      System.out.println("NO PATH!");

      return;
    }

    for (int i=0;i<path.size();i++) {
      Node node = (Node)path.get(i);

      System.out.print(" "+node.getId());
    }
    System.out.println();
  }

  private boolean findPath(ArrayList starts, Node end, ArrayList path, ArrayList visited, HashMap parents) {
    if (starts.size() == 0) {
      return false;
    }

    ArrayList allKids = new ArrayList();

    for (int j = 0;j < starts.size(); j++) {
      Node start = (Node)starts.get(j);

      Node[] kids = graph.getChildren(start);
      if (kids == null) {
        continue;
      }

      for (int i=0;i<kids.length;i++) {
        if (!visited.contains(kids[i])) {
          visited.add(kids[i]);

          parents.put(kids[i], start);

          if (kids[i] == end) {
            constructPath(kids[i], path, parents);
            return true;
          }
          allKids.add(kids[i]);
        }
      }
    }

    return findPath(allKids, end, path, visited, parents);
  }

  private void constructPath(Node node, ArrayList path, HashMap parents) {
    while (node != null) {
      path.add(0, node);

      node = (Node)parents.get(node);
    }
  }
}
