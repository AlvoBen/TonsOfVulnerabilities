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

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.sap.engine.lib.refgraph.Edge;
import com.sap.engine.lib.refgraph.GraphBase;
import com.sap.engine.lib.refgraph.NodeRemoveException;
import com.sap.engine.lib.refgraph.impl.util.ConcurrentHashSet;

/**
 * @author Luchesar Cekov
 */
public class MapGraphBase<N> implements GraphBase<N> {

	private static final int DEFAULT_MAPS_INITIAL_SIZE = 200;
	private static final float DEFAULT_MAPS_RESIZE_FACTOR = 1f;
	private static final int DEFAULT_MAPS_CONCURRENCY_LEVEL = 1;

	private static final int DEFAULT_SETS_INITIAL_SIZE = 3;
	private static final float DEFAULT_SETS_RESIZE_FACTOR = 0.5f;
	private static final int DEFAULT_SETS_CONCURRENCY_LEVEL = 1;

	Map<N, Set<Edge<N>>> referencesTo = new ConcurrentHashMap<N, Set<Edge<N>>>(
			DEFAULT_MAPS_INITIAL_SIZE, DEFAULT_MAPS_RESIZE_FACTOR,
			DEFAULT_MAPS_CONCURRENCY_LEVEL);
	Map<N, Set<Edge<N>>> referencesFrom = new ConcurrentHashMap<N, Set<Edge<N>>>(
			DEFAULT_MAPS_INITIAL_SIZE, DEFAULT_MAPS_RESIZE_FACTOR,
			DEFAULT_MAPS_CONCURRENCY_LEVEL);

	public synchronized void add(N node) {
		initValue(referencesTo, node);
		initValue(referencesFrom, node);
	}

	public synchronized void remove(N node) throws NodeRemoveException {
		checkPresent(node);
		if (referencesFrom.get(node).size() == 0) {
			removeEdgesFrom(node);
			referencesTo.remove(node);
			referencesFrom.remove(node);
		} else {
			throw new NodeRemoveException(node, referencesFrom.get(node));
		}
	}

	public boolean containsNode(N node) {
		return referencesTo.containsKey(node);
	}

	public synchronized void add(Edge<N> edge) {
		checkPresent(edge.getFirst());
		checkPresent(edge.getSecond());

		initValue(referencesTo, edge.getFirst()).add(edge);
		initValue(referencesFrom, edge.getSecond()).add(edge);
	}

	private void checkPresent(Object node) {
		if (!referencesTo.containsKey(node)) {
			throw new RuntimeException("Node " + node
					+ " not present in the graph.");
		}
	}

	public synchronized void remove(Edge<N> edge) {
		initValue(referencesTo, edge.getFirst()).remove(edge);
		initValue(referencesFrom, edge.getSecond()).remove(edge);
	}

	private void removeEdgesFrom(N node) {
		for (Iterator<Edge<N>> iterator = referencesTo.get(node).iterator(); iterator
				.hasNext();) {
			remove(iterator.next());
		}
	}

	private Set<Edge<N>> initValue(Map<N, Set<Edge<N>>> map, N node) {
		Set<Edge<N>> o = map.get(node);
		if (o == null) {
			Set<Edge<N>> set = new ConcurrentHashSet<Edge<N>>(
					DEFAULT_SETS_INITIAL_SIZE, DEFAULT_SETS_RESIZE_FACTOR,
					DEFAULT_SETS_CONCURRENCY_LEVEL);
			map.put(node, set);
			return set;
		}
		return o;
	}

	public List<N> getNodesWithNoReferencesFrom() {
		return getNodesWithNoReferences(referencesFrom);
	}

	public List<N> getNodesWithNoReferencesTo() {
		return getNodesWithNoReferences(referencesTo);
	}

	private List<N> getNodesWithNoReferences(Map<N, Set<Edge<N>>> map) {
		LinkedList<N> result = new LinkedList<N>();

		for (final Map.Entry<N, Set<Edge<N>>> references : map.entrySet()) {
			if (references.getValue().size() == 0) {
				result.add(references.getKey());
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.lib.refgraph.GraphBase#getReferencesToOthersFrom(java.
	 * lang.Object)
	 */
	public Set<Edge<N>> getReferencesToOthersFrom(N node) {
		Set<Edge<N>> result = referencesTo.get(node);
		if(result == null) {
			return Collections.emptySet();
		} 
		return Collections.unmodifiableSet(result);
	}

	public Set<Edge<N>> getReferencesFromOthersTo(N node) {
		Set<Edge<N>> result = referencesFrom.get(node);
		if(result == null) {
			return Collections.emptySet();
		} 
		return Collections.unmodifiableSet(result);
	}

	public void clear() {
		referencesTo.clear();
		referencesFrom.clear();
	}

	public int size() {
		return referencesTo.size();
	}
}
