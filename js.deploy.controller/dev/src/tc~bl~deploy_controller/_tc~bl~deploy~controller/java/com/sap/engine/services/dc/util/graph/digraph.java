package com.sap.engine.services.dc.util.graph;

import java.util.Set;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
public interface DiGraph {

	public Node[] getNodes();

	public DiEdge[] getEdges();

	public DiEdge[] getEdgesOut(Node node);

	public DiEdge[] getEdgesIn(Node node);

	public Node[] getSourceNodes();

	public Node[] getDrainNodes();

	public Node[] getAdjacentNodesIn(Node node);

	public Node[] getAdjacentNodesOut(Node node);

	public Node getNodeForUserObject(Object obj);

	public boolean areNodesAdjacent(Node startNode, Node endNode);

	public boolean isAcyclic();

	public Set[] getCycles();

	public int getNodeNumberOrig(Node node);

	public DiGraph constructSubgraph(Node nodeToBeRemoved);
}
