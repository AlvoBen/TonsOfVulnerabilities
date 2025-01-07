/* 
* Copyright (c) 2005 by SAP AG, Walldorf.,
* http://www.sap.com
* All rights reserved.
*
* This software is the confidential and proprietary information
* of SAP AG, Walldorf. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms
* of the license agreement you entered into with SAP.
*/


package com.sap.engine.lib.refgraph.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import com.sap.engine.lib.refgraph.CyclicReferencesException;
import com.sap.engine.lib.refgraph.Edge;
import com.sap.engine.lib.refgraph.NodeHandler;

/**
 *@author Luchesar Cekov
 */
class TopoSortHandler<N> implements NodeHandler<N> {
  private LinkedHashSet<N>  sortedComponents = new LinkedHashSet<N>();
  private HashSet<N> visited = new HashSet<N>();

  public boolean startRoot() {
    return true;
  }

  public void endRoot() {//
  }

  public boolean startNode(N node, Edge<N> formEdge, boolean hasNextCybling) {
    if (visited.contains(node)) {
      return false;
    }
    visited.add(node);
    return true;
  }

  public void endNode(N aNode) {
    if (!sortedComponents.contains(aNode)) {
      sortedComponents.add(aNode);
    }
  }

  public void cycle(N node, Edge<N> formEdge,boolean isLastCybling) throws CyclicReferencesException {
    throw new CyclicReferencesException(new String[] {});
  }

  public void selfCycle(N node, Edge<N> formEdge,boolean isLastCybling) {//
  }


  @SuppressWarnings("unchecked")
  public List<N> getSortedComponentsNames() {
    return (List<N>) Arrays.asList(sortedComponents.toArray());
  }
}
