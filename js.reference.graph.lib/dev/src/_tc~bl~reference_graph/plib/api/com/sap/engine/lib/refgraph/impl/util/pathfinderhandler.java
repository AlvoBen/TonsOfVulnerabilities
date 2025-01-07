package com.sap.engine.lib.refgraph.impl.util;

import java.util.HashSet;
import java.util.Set;

import com.sap.engine.lib.refgraph.CyclicReferencesException;
import com.sap.engine.lib.refgraph.Edge;
import com.sap.engine.lib.refgraph.NodeHandler;
import com.sap.engine.lib.refgraph.NodeRemoveException;
import com.sap.engine.lib.refgraph.impl.Graph;

public class PathFinderHandler<N> implements NodeHandler<N> {

	final Graph<N> path = new Graph<N>();
	final N from;
	final N to;
	final boolean forward;

	private boolean shouldRemove = true;

	HashSet<Object> visited;

	public PathFinderHandler(N from, N to, boolean forward) {

		this.from = from;
		this.to = to;
		this.forward = forward;
		path.add(from);

	}

	public Graph<N> getPath() {
		return this.path;
	}

	public void cycle(N node, Edge<N> edge, boolean arg2)
		throws CyclicReferencesException {
		// Empty method.
	}

	public boolean startNode(N node, Edge<N> edge, boolean arg2) {

		shouldRemove = true;
		path.add(node);
		if (edge != null) {
			path.add(edge);
		}

		if (node.equals(to)) {
			shouldRemove = false;
			return false;
		}

		if (visited.contains(node)) {
			return false;
		}
		else{
			visited.add(node);
		}
		return true;

	}

	public void endNode(N node) {

		// TODO take into account this.forward

		if (shouldRemove) {

			Set references = null;
			if (forward) {
				references = path.getReferencesToOthersFrom(node);
			} else {
				references = path.getReferencesFromOthersTo(node);
			}
			if (references.size() == 0) {

				Set<Edge<N>> edges = null;
				if (forward) {
					edges = path.getReferencesFromOthersTo(node);
				} else {
					edges = path.getReferencesToOthersFrom(node);
				}
				try {
					if (edges != null) {
						for (Edge edge : edges) {
							path.remove(edge);
						}
					}
					path.remove(node);
				} catch (NodeRemoveException e) {
					throw new RuntimeException(e); // TODO convert to logging
				}
			} else {
				shouldRemove = false;
			}
		}

	}

	public void endRoot() {

	}

	public void selfCycle(N arg0, Edge<N> arg1, boolean arg2) {
		// TODO Auto-generated method stub

	}

	public boolean startRoot() {

		return false;
	}

}
