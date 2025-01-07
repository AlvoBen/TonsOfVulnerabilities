package com.sap.engine.services.dc.util.graph.impl;

import com.sap.engine.services.dc.util.graph.DiEdge;
import com.sap.engine.services.dc.util.graph.DiGraph;
import com.sap.engine.services.dc.util.graph.GraphFactory;
import com.sap.engine.services.dc.util.graph.Node;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
public class GraphFactoryImpl extends GraphFactory {

	public GraphFactoryImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.gui.graph.GraphFactory#createNode(java.lang.String,
	 * java.lang.Object)
	 */
	public Node createNode(String name, Object userObject) {
		return new NodeImpl(name, userObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.gui.graph.GraphFactory#createEdge(com.sap.sdm.gui.graph.Node,
	 * com.sap.sdm.gui.graph.Node)
	 */
	public DiEdge createEdge(Node startNode, Node endNode) {
		return new DiEdgeImpl(startNode, endNode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.gui.graph.GraphFactory#createGraph(com.sap.sdm.gui.graph.
	 * Node[], com.sap.sdm.gui.graph.DirectedEdge[])
	 */
	public DiGraph createGraph(Node[] nodes, DiEdge[] edges) {
		return new DiGraphImpl(nodes, edges);
	}

}
