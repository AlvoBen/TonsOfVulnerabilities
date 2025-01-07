package com.sap.engine.services.dc.util.graph.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.sap.engine.services.dc.util.exception.DCBaseException;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;
import com.sap.engine.services.dc.util.graph.DiEdge;
import com.sap.engine.services.dc.util.graph.DiGraph;
import com.sap.engine.services.dc.util.graph.Node;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
class DiGraphImpl implements DiGraph {

	protected Node[] nodes;
	protected DiEdge[] edges;
	private Map nodeNumbersOrig;
	private boolean[][] adjacenceMatrix;
	private boolean isAcyclic;
	private boolean isAcyclicDetermined;

	private int currentTopSortNumber;
	private boolean[] topSortProcessed;
	private int[] topSortNumbers;

	private boolean[] nodeVisited;
	private boolean[] nodeExited;
	private int cycleCount;
	private Set[] cycles;
	private boolean cyclesDetermined;
	private Stack cycleStack;
	private List cycleList;

	DiGraphImpl(Node[] nodes, DiEdge[] edges) {
		this.nodes = nodes;
		this.edges = edges;
		this.isAcyclicDetermined = false;
		this.cyclesDetermined = false;
		nodeNumbersOrig = new HashMap();
		for (int i = 0; i < nodes.length; i++) {
			nodeNumbersOrig.put(nodes[i], new Integer(i));
		}
		adjacenceMatrix = new boolean[nodes.length][nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			for (int j = 0; j < nodes.length; j++) {
				adjacenceMatrix[i][j] = false;
			}
		}
		for (int i = 0; i < edges.length; i++) {
			int numberStartNode = ((Integer) nodeNumbersOrig.get(edges[i]
					.getStartNode())).intValue();
			int numberEndNode = ((Integer) nodeNumbersOrig.get(edges[i]
					.getEndNode())).intValue();
			adjacenceMatrix[numberStartNode][numberEndNode] = true;
		}

	}

	public int getNodeNumberOrig(Node node) {
		return ((Integer) nodeNumbersOrig.get(node)).intValue();
	}

	public Node getNodeForUserObject(Object obj) {
		for (int i = 0; i < nodes.length; i++) {
			if (obj.equals(nodes[i].getUserObject())) {
				return nodes[i];
			}
		}
		return null;
	}

	public Node[] getNodes() {
		return this.nodes;
	}

	public Node[] getSourceNodes() {

		boolean drainFound;

		List nodeList = new ArrayList();
		for (int i = 0; i < nodes.length; i++) {
			// check nodes[i]
			drainFound = false;
			for (int j = 0; j < nodes.length; j++) {
				if (adjacenceMatrix[j][i]) {
					drainFound = true;
					break;
				}
			}
			if (!drainFound) {
				nodeList.add(nodes[i]);
			}
		}

		Node[] sourceNodes = new Node[nodeList.size()];
		nodeList.toArray(sourceNodes);
		return sourceNodes;
	}

	public Node[] getDrainNodes() {

		boolean sourceFound;

		List nodeList = new ArrayList();
		for (int i = 0; i < nodes.length; i++) {
			// check nodes[i]
			sourceFound = false;
			for (int j = 0; j < nodes.length; j++) {
				if (adjacenceMatrix[i][j]) {
					sourceFound = true;
					break;
				}
			}
			if (!sourceFound) {
				nodeList.add(nodes[i]);
			}
		}
		Node[] drainNodes = new Node[nodeList.size()];
		nodeList.toArray(drainNodes);
		return drainNodes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.gui.graph.DirectedGraoh#getEdges()
	 */
	public DiEdge[] getEdges() {
		return this.edges;
	}

	public Node[] getAdjacentNodesIn(Node node) {
		int nodeNumber = ((Integer) nodeNumbersOrig.get(node)).intValue();
		List resultList = new ArrayList();
		for (int i = 0; i < nodes.length; i++) {
			if (adjacenceMatrix[i][nodeNumber]) {
				resultList.add(nodes[i]);
			}
		}
		Node[] result = new Node[resultList.size()];
		resultList.toArray(result);
		return result;
	}

	public Node[] getAdjacentNodesOut(Node node) {
		int nodeNumber = ((Integer) nodeNumbersOrig.get(node)).intValue();
		List resultList = new ArrayList();
		for (int i = 0; i < nodes.length; i++) {
			if (adjacenceMatrix[nodeNumber][i]) {
				resultList.add(nodes[i]);
			}
		}
		Node[] result = new Node[resultList.size()];
		resultList.toArray(result);
		return result;
	}

	public DiEdge[] getEdgesIn(Node node) {
		List edgeList = new ArrayList();
		for (int i = 0; i < edges.length; i++) {
			if (edges[i].getEndNode().equals(node)) {
				edgeList.add(edges[i]);
			}
		}
		DiEdge[] edgesIn = new DiEdge[edgeList.size()];
		edgeList.toArray(edgesIn);
		return edgesIn;
	}

	public DiEdge[] getEdgesOut(Node node) {
		List edgeList = new ArrayList();
		for (int i = 0; i < edges.length; i++) {
			if (edges[i].getStartNode().equals(node)) {
				edgeList.add(edges[i]);
			}
		}
		DiEdge[] edgesOut = new DiEdge[edgeList.size()];
		edgeList.toArray(edgesOut);
		return edgesOut;
	}

	public boolean areNodesAdjacent(Node startNode, Node endNode) {
		int startNodeNumber = ((Integer) nodeNumbersOrig.get(startNode))
				.intValue();
		int endNodeNumber = ((Integer) nodeNumbersOrig.get(endNode)).intValue();
		return adjacenceMatrix[startNodeNumber][endNodeNumber];
	}

	public boolean isAcyclic() {

		if (this.isAcyclicDetermined) {
			return this.isAcyclic;
		}

		boolean containsCycle = this.topSortCyclicTest(this);
		this.isAcyclicDetermined = true;
		this.isAcyclic = (containsCycle) ? false : true;
		if (this.isAcyclic) {
			this.cyclesDetermined = true;
			this.cycles = new Set[0];
		}
		return this.isAcyclic;

	}

	private boolean topSortCyclicTest(DiGraph graph) {

		Node[] topSortNodes = graph.getNodes();
		this.topSortProcessed = new boolean[topSortNodes.length];
		this.topSortNumbers = new int[topSortNodes.length];
		currentTopSortNumber = topSortNodes.length - 1;
		boolean cycleFound = false;

		for (int i = 0; i < topSortProcessed.length; i++) {
			topSortProcessed[i] = false;
			topSortNumbers[i] = -1;
		}
		try {

			for (int i = 0; i < topSortNodes.length; i++) {
				if (!topSortProcessed[i]) {
					topSortProc(i, graph);
				}
			}
		} catch (CycleFoundException cfe) {
			cycleFound = true;
		}

		return cycleFound;
	}

	private void topSortProc(int i, DiGraph graph) throws CycleFoundException {
		topSortProcessed[i] = true;
		Node[] nodesAdjacentOut = graph
				.getAdjacentNodesOut(graph.getNodes()[i]);
		for (int j = 0; j < nodesAdjacentOut.length; j++) {
			int nodeNo = graph.getNodeNumberOrig(nodesAdjacentOut[j]);
			if (!topSortProcessed[nodeNo]) {
				topSortProc(nodeNo, graph);
			} else if (topSortNumbers[nodeNo] == -1) {
				throw new CycleFoundException(
						DCExceptionConstants.GRAPH_CYCLE_FOUND);
			}
		}
		topSortNumbers[i] = currentTopSortNumber;
		currentTopSortNumber--;
	}

	public Set[] getCycles() {
		if (this.cyclesDetermined) {
			return this.cycles;
		}
		this.cycleList = new ArrayList();
		this.cycleStack = new Stack();
		nodeVisited = new boolean[nodes.length];
		nodeExited = new boolean[nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			nodeVisited[i] = false;
			nodeExited[i] = false;
		}
		cycleCount = 0;
		for (int i = 0; i < nodes.length; i++) {
			if (!nodeVisited[i]) {
				cycleSearch(i, this);
			}
		}
		this.cyclesDetermined = true;
		this.cycles = new Set[this.cycleList.size()];
		this.cycleList.toArray(this.cycles);
		return cycles;
	}

	private void cycleSearch(int i, DiGraph graph) {
		this.nodeVisited[i] = true;
		Node currentNode = graph.getNodes()[i];
		this.cycleStack.push(currentNode);
		Node[] nodesAdjacentOut = graph.getAdjacentNodesOut(currentNode);
		for (int j = 0; j < nodesAdjacentOut.length; j++) {
			int nodeNo = graph.getNodeNumberOrig(nodesAdjacentOut[j]);
			if (!nodeVisited[nodeNo]) {
				cycleSearch(nodeNo, graph);
			} else if (!nodeExited[nodeNo]) {
				// cycle found
				cycleCount++;
				Set newNodeSet = new HashSet();
				Stack dummyStack = new Stack();
				Node cycleNode = (Node) this.cycleStack.pop();
				newNodeSet.add(cycleNode);
				dummyStack.push(cycleNode);
				while (!cycleNode.equals(nodesAdjacentOut[j])) {
					cycleNode = (Node) this.cycleStack.pop();
					newNodeSet.add(cycleNode);
					dummyStack.push(cycleNode);
				}
				while (!dummyStack.isEmpty()) {
					this.cycleStack.push(dummyStack.pop());
				}
				this.cycleList.add(newNodeSet);
			}
		}
		nodeExited[i] = true;
		this.cycleStack.pop();

	}

	public DiGraph constructSubgraph(Node nodeToBeRemoved) {
		Set allNodes = new HashSet();
		for (int i = 0; i < nodes.length; i++) {
			allNodes.add(nodes[i]);
		}
		allNodes.remove(nodeToBeRemoved);
		Node[] subgraphNodes = new Node[allNodes.size()];
		allNodes.toArray(subgraphNodes);

		List edgesList = new ArrayList();
		for (int i = 0; i < edges.length; i++) {
			if ((!edges[i].getStartNode().equals(nodeToBeRemoved))
					&& (!edges[i].getEndNode().equals(nodeToBeRemoved))) {
				edgesList.add(edges[i]);
			}
		}
		DiEdge[] subgraphEdges = new DiEdge[edgesList.size()];
		edgesList.toArray(subgraphEdges);

		return new DiGraphImpl(subgraphNodes, subgraphEdges);
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Nodes: \n");
		for (int i = 0; i < nodes.length; i++) {
			buf.append(nodes[i].getName() + "\n");
		}

		buf.append("\nEdges: \n");
		for (int i = 0; i < edges.length; i++) {
			buf.append(edges[i].toString());
			buf.append("\n");
		}
		return buf.toString();
	}

	private class CycleFoundException extends DCBaseException {

		public CycleFoundException(String patternKey) {
			super(patternKey);
		}

		public CycleFoundException(String patternKey, Object[] parameters) {
			super(patternKey, parameters);
		}

		public CycleFoundException(String patternKey, Throwable cause) {
			super(patternKey, cause);
		}

		public CycleFoundException(String patternKey, Object[] parameters,
				Throwable cause) {
			super(patternKey, parameters, cause);
		}

	}

}
