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

import java.util.List;
import java.util.Set;

/**
 * @author Luchesar Cekov
 */
public interface GraphBase<N> {
	void add(N node);

	void add(Edge<N> edge);

	void remove(N node) throws NodeRemoveException;

	void remove(Edge<N> edge);

	void clear();

	int size();

	Set<Edge<N>> getReferencesToOthersFrom(N node);

	Set<Edge<N>> getReferencesFromOthersTo(N node);

	boolean containsNode(N node);

	List<N> getNodesWithNoReferencesFrom();

	List<N> getNodesWithNoReferencesTo();
}
