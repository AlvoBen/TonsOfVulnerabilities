/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http:////www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.refgraph.impl;

import java.util.HashSet;
import java.util.Iterator;

import com.sap.engine.lib.refgraph.CyclicReferencesException;
import com.sap.engine.lib.refgraph.Edge;
import com.sap.engine.lib.refgraph.GraphBase;
import com.sap.engine.lib.refgraph.NodeHandler;
import com.sap.engine.lib.refgraph.Traverser;


/**
 *@author Luchesar Cekov
 */
public class DFSTraverser<N> implements Traverser<N>{
  private HashSet<Object> visited;


  @SuppressWarnings("unchecked")
  public void traverseForward(GraphBase<N> graph, NodeHandler<N> handler) throws CyclicReferencesException {
    N[] nodes = (N[]) graph.getNodesWithNoReferencesFrom().toArray();
    traverseForwardRootNode(graph, nodes, handler);
  }

  public void traverseForwardRootNode(GraphBase<N> graph, N[] node, NodeHandler<N> handler) throws CyclicReferencesException {
    if (!handler.startRoot()) {
      return;
    }
    for (int i = 0; i < node.length; i++) {
      traverseForward(graph, node[i], handler, !(i < node.length - 1));
    }
    handler.endRoot();
  }

  public void traverseForward(GraphBase<N> graph, N node, NodeHandler<N> handler) throws CyclicReferencesException {
    traverseForward(graph, node, handler, true);
  }

  public void traverseForward(GraphBase<N> graph, N node, NodeHandler<N> handler, boolean isLast) throws CyclicReferencesException {
    visited = new HashSet<Object>(graph.size());
    visited.add(node);
    if (!handler.startNode(node, null, isLast)) {
      return;
    }
    traverseForwardImpl(graph, node, handler);
    handler.endNode(node);
  }

  private void traverseForwardImpl(GraphBase<N> graph, N node, NodeHandler<N> handler) throws CyclicReferencesException {
    Edge<N> ref = null;
    N second = null;
    for (Iterator<Edge<N>> iter = graph.getReferencesToOthersFrom(node).iterator(); iter.hasNext();) {
      ref = iter.next();
      second = ref.getSecond();
      if (node.equals(second)) {
        handler.selfCycle(node, ref,!iter.hasNext());
        continue;
      }
      if (visited.contains(second)) {
        handler.cycle(second, ref,!iter.hasNext());
        continue;
      }
      visited.add(second);
      try {
        if (handler.startNode(second, ref, !iter.hasNext())) {
        	traverseForwardImpl(graph, second, handler);
        }
        handler.endNode(second);
      } finally {
        visited.remove(second);
      }
    }
  }

  @SuppressWarnings("unchecked")
  public void traverseBackward(GraphBase<N> graph, NodeHandler<N> handler) throws CyclicReferencesException {
    for (Iterator<N> iter = graph.getNodesWithNoReferencesTo().iterator(); iter.hasNext();) {
      traverseBackwardRootNode(graph, (N[]) new Object[] { iter.next() }, handler);
    }
  }

  public void traverseBackwardRootNode(GraphBase<N> graph, N[] node, NodeHandler<N> handler) throws CyclicReferencesException {
    if (!handler.startRoot()) {
      return;
    }
    for (int i = 0; i < node.length; i++) {
      traverseBackward(graph, node[i], handler, i >= node.length);
    }
    handler.endRoot();
  }

  public void traverseBackward(GraphBase<N> graph, N node, NodeHandler<N> handler) throws CyclicReferencesException {
    traverseBackward(graph, node, handler, true);
  }

  public void traverseBackward(GraphBase<N> graph, N node, NodeHandler<N> handler, boolean isLast) throws CyclicReferencesException {
    visited = new HashSet<Object>(graph.size());
    visited.add(node);
    if (!handler.startNode(node, null, isLast)) {
      return;
    }
    traverseBackwardImpl(graph, node, handler);
    handler.endNode(node);
  }

  private void traverseBackwardImpl(GraphBase<N> graph, N node, NodeHandler<N> handler) throws CyclicReferencesException {
    Edge<N> ref = null;
    N first = null;
    for (Iterator<Edge<N>> iter = graph.getReferencesFromOthersTo(node).iterator(); iter.hasNext();) {
      ref = iter.next();
      first = ref.getFirst();
      if (node.equals(first)) {
        handler.selfCycle(node, ref,!iter.hasNext());
        continue;
      }

      if (visited.contains(first)) {
        handler.cycle(first, ref,!iter.hasNext());
        continue;
      }
      visited.add(first);
      try {
        if (handler.startNode(first, ref, !iter.hasNext())) {
            traverseBackwardImpl(graph, ref.getFirst(), handler);
        }
        handler.endNode(ref.getFirst());
      } finally {
        visited.remove(first);
      }
    }
  }
  
  public void traverseBackwardHard(GraphBase<N> graph, N node, NodeHandler<N> handler) throws CyclicReferencesException {
	  traverseBackwardHard(graph, node, handler, true);
  }
  
  public void traverseBackwardHard(GraphBase<N> graph, N node, NodeHandler<N> handler, boolean isLast) throws CyclicReferencesException {
	    visited = new HashSet<Object>(graph.size());
	    visited.add(node);
	    if (!handler.startNode(node, null, isLast)) {
	      return;
	    }
	    traverseBackwardHardImpl(graph, node, handler);
	    handler.endNode(node);
  }
  
  private void traverseBackwardHardImpl(GraphBase<N> graph, N node, NodeHandler<N> handler) throws CyclicReferencesException {
	    Edge<N> ref = null;
	    N first = null;
	    for (Iterator<Edge<N>> iter = graph.getReferencesFromOthersTo(node).iterator(); iter.hasNext();) {
	      ref = iter.next();
	      if(ref.getType().equals(Edge.Type.WEAK)){
	    	  continue;
	      }
	      first = ref.getFirst();
	      if (node.equals(first)) {
	        handler.selfCycle(node, ref,!iter.hasNext());
	        continue;
	      }

	      if (visited.contains(first)) {
	        handler.cycle(first, ref,!iter.hasNext());
	        continue;
	      }
	      visited.add(first);
	      try {
	        if (!handler.startNode(first, ref, !iter.hasNext())) {
	          continue;
	        }
	        traverseBackwardHardImpl(graph, ref.getFirst(), handler);
	        handler.endNode(ref.getFirst());
	      } finally {
	        visited.remove(first);
	      }
	    }
  }
}
