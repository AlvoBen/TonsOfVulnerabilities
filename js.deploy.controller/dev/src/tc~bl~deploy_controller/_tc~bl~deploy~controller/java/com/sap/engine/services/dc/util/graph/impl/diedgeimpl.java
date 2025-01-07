package com.sap.engine.services.dc.util.graph.impl;

import com.sap.engine.services.dc.util.graph.DiEdge;
import com.sap.engine.services.dc.util.graph.Node;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
class DiEdgeImpl implements DiEdge {

	private Node startNode;
	private Node endNode;

	DiEdgeImpl(Node startNode, Node endNode) {
		this.startNode = startNode;
		this.endNode = endNode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.gui.graph.DirectedEdge#getStartNode()
	 */
	public Node getStartNode() {
		return this.startNode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.gui.graph.DirectedEdge#getEndNode()
	 */
	public Node getEndNode() {
		return this.endNode;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(startNode.getName());
		buf.append(" ---> ");
		buf.append(endNode.getName());
		return buf.toString();
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof DiEdge)) {
			return false;
		}
		DiEdge otherEdge = (DiEdge) obj;
		if ((this.startNode.equals(otherEdge.getStartNode()))
				&& (this.endNode.equals(otherEdge.getEndNode()))) {
			return true;
		}
		return false;
	}

	public int hashCode() {
		return (this.startNode.hashCode() + this.endNode.hashCode());
	}

}
