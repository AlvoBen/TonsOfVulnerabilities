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
public interface NodeHandler<N> {
    public boolean startRoot();
    public void endRoot();

    public boolean startNode(N node, Edge<N> formEdge, boolean isLastCybling);
    public void endNode(N node);

    public void cycle(N node, Edge<N> formEdge,boolean isLastCybling) throws CyclicReferencesException;

    public void selfCycle(N node, Edge<N> fromEdge,boolean isLastCybling);
}
