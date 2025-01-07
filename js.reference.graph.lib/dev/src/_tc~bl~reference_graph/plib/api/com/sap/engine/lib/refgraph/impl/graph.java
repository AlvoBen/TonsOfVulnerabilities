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

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.sap.engine.lib.refgraph.CyclicReferencesException;
import com.sap.engine.lib.refgraph.Edge;
import com.sap.engine.lib.refgraph.GraphBase;
import com.sap.engine.lib.refgraph.NodeHandler;
import com.sap.engine.lib.refgraph.NodeRemoveException;
import com.sap.engine.lib.refgraph.ReferenceGraph;
import com.sap.engine.lib.refgraph.impl.util.PathFinderHandler;

/**
 * @author Luchesar Cekov
 */
public final class Graph<N> implements ReferenceGraph<N> {
	GraphBase<N> graphBase;

	// Traverser traverser; //commented for multi threading

	public Graph() {
		graphBase = new MapGraphBase<N>();
	}

	public Graph(GraphBase<N> aGraphBase) {
		super();
		graphBase = aGraphBase;
	}

	public List<N> sort() throws CyclicReferencesException {
		TopoSortHandler<N> handler = new TopoSortHandler<N>();
		List<N> nodesWithNoReferencesFrom = getNodesWithNoReferencesFrom();
		if (nodesWithNoReferencesFrom.size() == 0) {
			if (size() != 0) {
				ReferenceCycleCheckerHandler<N> checker = new ReferenceCycleCheckerHandler<N>();
				throw new CyclicReferencesException(checker.getCycles()
						.toArray(new String[0]));
			}
		} else {
			for (Iterator<N> iterator = nodesWithNoReferencesFrom.iterator(); iterator
					.hasNext();) {
				N node = iterator.next();
				traverseForward(node, handler);
			}
		}
		return handler.getSortedComponentsNames();

	}

	public List<N> sortBackward() throws CyclicReferencesException {
		TopoSortHandler<N> handler = new TopoSortHandler<N>();
		List<N> nodesWithNoReferencesTo = getNodesWithNoReferencesTo();
		for (Iterator<N> iterator = nodesWithNoReferencesTo.iterator(); iterator
				.hasNext();) {
			N node = iterator.next();
			traverseBackward(node, handler);
		}
		return handler.getSortedComponentsNames();
	}

	public List<N> sort(N node) throws CyclicReferencesException {
		TopoSortHandler<N> handler = new TopoSortHandler<N>();
		traverseForward(node, handler);
		return handler.getSortedComponentsNames();
	}

	public List<N> sortBackward(N node) throws CyclicReferencesException {
		TopoSortHandler<N> handler = new TopoSortHandler<N>();
		traverseBackward(node, handler);
		return handler.getSortedComponentsNames();
	}

	public List<N> sortBackwardHard(N node) throws CyclicReferencesException {
		TopoSortHandler<N> handler = new TopoSortHandler<N>();
		traverseBackwardHard(node, handler);
		return handler.getSortedComponentsNames();
	}

	public void cycleCheck() throws CyclicReferencesException {
		List<N> nodesWithNoReferencesFrom = getNodesWithNoReferencesFrom();
		if (nodesWithNoReferencesFrom.size() == 0) {
			if (size() != 0) {
				ReferenceCycleCheckerHandler<N> checker = new ReferenceCycleCheckerHandler<N>();
				throw new CyclicReferencesException(checker.getCycles()
						.toArray(new String[0]));
			}
		} else {

			for (Iterator<N> iterator = nodesWithNoReferencesFrom.iterator(); iterator
					.hasNext();) {
				cycleCheck(iterator.next());
			}
		}
	}

	public void cycleCheck(N node) throws CyclicReferencesException {
		FastReferenceCycleCheckerHandler<N> fastCecker = new FastReferenceCycleCheckerHandler<N>();
		try {
			traverseForward(node, fastCecker);
		} catch (CyclicReferencesException e) {
			ReferenceCycleCheckerHandler<N> checker = new ReferenceCycleCheckerHandler<N>();
			traverseForward(node, checker);
			if (checker.getCycles().size() > 0) {
				throw new CyclicReferencesException(checker.getCycles()
						.toArray(new String[0]));
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void print(PrintWriter out) throws CyclicReferencesException {
		print(out, false, (N[]) getNodesWithNoReferencesFrom().toArray());
	}

	public void print(N[] nodes, PrintWriter out)
			throws CyclicReferencesException {
		print(out, false, nodes);
	}

	@SuppressWarnings("unchecked")
	public void printBackward(PrintWriter out) throws CyclicReferencesException {
		print(out, true, (N[]) getNodesWithNoReferencesFrom().toArray());
	}

	public void printBackward(N[] nodes, PrintWriter out)
			throws CyclicReferencesException {
		print(out, true, nodes);
	}

	private void print(PrintWriter out, boolean referencesFrom, N[] node)
			throws CyclicReferencesException {
		ReferencePrinterHandler<N> handler = new ReferencePrinterHandler<N>(
				out, referencesFrom);
		for (int i = 0; i < node.length; i++) {
			if (referencesFrom) {
				new DFSTraverser<N>().traverseBackward(graphBase, node[i],
						handler);
			} else {
				new DFSTraverser<N>().traverseForward(graphBase, node[i],
						handler);
			}
		}
		handler.endRoot();
	}

	public void add(N aNode) {
		graphBase.add(aNode);
	}

	public void add(Edge<N> aEdge) {
		graphBase.add(aEdge);
	}

	public void clear() {
		graphBase.clear();
	}

	public boolean containsNode(N aNode) {
		return graphBase.containsNode(aNode);
	}

	public List<N> getNodesWithNoReferencesFrom() {
		return graphBase.getNodesWithNoReferencesFrom();
	}

	public List<N> getNodesWithNoReferencesTo() {
		return graphBase.getNodesWithNoReferencesTo();
	}

	public Set<Edge<N>> getReferencesFromOthersTo(N aNode) {
		return graphBase.getReferencesFromOthersTo(aNode);
	}

	public Set<Edge<N>> getReferencesToOthersFrom(N aNode) {
		return graphBase.getReferencesToOthersFrom(aNode);
	}

	public void remove(N aNode) throws NodeRemoveException {
		graphBase.remove(aNode);
	}

	public void remove(Edge<N> aEdge) {
		graphBase.remove(aEdge);
	}

	public int size() {
		return graphBase.size();
	}

	public void traverseBackward(NodeHandler<N> aHandler)
			throws CyclicReferencesException {
		new DFSTraverser<N>().traverseBackward(graphBase, aHandler);
	}

	public void traverseBackward(N aNode, NodeHandler<N> aHandler)
			throws CyclicReferencesException {
		new DFSTraverser<N>().traverseBackward(graphBase, aNode, aHandler);
	}

	public void traverseForward(NodeHandler<N> aHandler)
			throws CyclicReferencesException {
		new DFSTraverser<N>().traverseForward(graphBase, aHandler);
	}

	public void traverseForward(N aNode, NodeHandler<N> aHandler)
			throws CyclicReferencesException {
		new DFSTraverser<N>().traverseForward(graphBase, aNode, aHandler);
	}

	public void traverseBackwardHard(N aNode, NodeHandler<N> aHandler)
			throws CyclicReferencesException {
		new DFSTraverser<N>().traverseBackwardHard(graphBase, aNode, aHandler);
	}

	public void traverseFindPath(N aNodeFrom, N aNodeTo, NodeHandler<N> aHandler)
			throws CyclicReferencesException {
		new DFSTraverser<N>().traverseForward(findPath(aNodeFrom, aNodeTo),
				aNodeFrom, aHandler);
	}

	public void traverseFindReversePath(N aNodeFrom, N aNodeTo,
			NodeHandler<N> aHandler) throws CyclicReferencesException {
		new DFSTraverser<N>().traverseBackward(findReversePath(aNodeFrom,
				aNodeTo), aNodeFrom, aHandler);
	}

	public Graph<N> findPath(N from, N to) throws CyclicReferencesException {
		return this.findPath(from, to, true);
	}

	public Graph<N> findReversePath(N from, N to)
			throws CyclicReferencesException {
		return this.findPath(from, to, false);
	}

	private Graph<N> findPath(N from, N to, boolean forward)
			throws CyclicReferencesException {

		if (!this.containsNode(from)) {
			throw new IllegalArgumentException("Node " + from
					+ " is not present in the graph");
		}

		if (!this.containsNode(to)) {
			throw new IllegalArgumentException("Node " + to
					+ " is not present in the graph");
		}

		PathFinderHandler<N> pathHandler = new PathFinderHandler<N>(from, to,
			forward);

		if (forward) {
			this.traverseForward(from, pathHandler);
		} else {
			this.traverseBackward(from, pathHandler);
		}

		return pathHandler.getPath();
	}
}
