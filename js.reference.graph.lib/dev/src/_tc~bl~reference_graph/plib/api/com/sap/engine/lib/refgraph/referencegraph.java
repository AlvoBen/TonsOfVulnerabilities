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
import java.util.List;

public interface ReferenceGraph<N> extends GraphBase<N> {  

  public List<N> sort() throws CyclicReferencesException;

  public List<N> sort(N node) throws CyclicReferencesException;
  
  public List<N> sortBackward() throws CyclicReferencesException;
  
  public List<N> sortBackward(N node) throws CyclicReferencesException;
  
  public List<N> sortBackwardHard(N node) throws CyclicReferencesException;

  public void cycleCheck() throws CyclicReferencesException;

  public void cycleCheck(N node) throws CyclicReferencesException;
  
  
  
  
  public void traverseForward(NodeHandler<N> handler) throws CyclicReferencesException;

  public void traverseForward(N node, NodeHandler<N> handler) throws CyclicReferencesException;

  public void traverseBackward(NodeHandler<N> handler) throws CyclicReferencesException;

  public void traverseBackward(N node, NodeHandler<N> handler) throws CyclicReferencesException;
  
  public void traverseBackwardHard(N node, NodeHandler<N> handler) throws CyclicReferencesException;
}
