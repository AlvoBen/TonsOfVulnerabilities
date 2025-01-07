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
package com.sap.engine.lib.refgraph;



/**
 *@author Luchesar Cekov
 */
public interface Traverser<N> {
  public void traverseForward(GraphBase<N> graph, NodeHandler<N> handler) throws CyclicReferencesException;

  public void traverseForward(GraphBase<N> graph, N node, NodeHandler<N> handler) throws CyclicReferencesException;

  public void traverseBackward(GraphBase<N> graph, NodeHandler<N> handler) throws CyclicReferencesException;

  public void traverseBackward(GraphBase<N> graph, N node, NodeHandler<N> handler) throws CyclicReferencesException;
  
  public void traverseBackwardHard(GraphBase<N> graph, N node, NodeHandler<N> handler) throws CyclicReferencesException;
}
