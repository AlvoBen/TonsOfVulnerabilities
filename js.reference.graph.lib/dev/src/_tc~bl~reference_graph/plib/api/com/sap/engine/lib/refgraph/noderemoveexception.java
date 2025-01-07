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

import java.util.Set;

/**
 *@author Luchesar Cekov
 */
public class NodeRemoveException extends Exception {
  private static final long serialVersionUID = 1L;
  
  private final String message;
  
  public NodeRemoveException(Object nodeToBeRemoved, Set aReferencesFromEdges) {
    message = "Node \""+ nodeToBeRemoved +"\" can not be removed. Nodes :" + getReferencesFromNodes(aReferencesFromEdges) + " have references to it";
  }
  
  public String getMessage() {
    return message;
  }
  
  private static String getReferencesFromNodes(Set<Edge<?>> edges) {
    StringBuilder sb = new StringBuilder();
    for (Edge edge: edges) {
      sb.append("\"").append(edge.getFirst()).append("\",").append(" ");
    }
    
    return sb.toString();
  }
}
