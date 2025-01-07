package com.sap.engine.services.dc.util.graph.impl;

import com.sap.engine.services.dc.util.graph.Node;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
class NodeImpl implements Node {

	private String name;
	private Object userObject;

	NodeImpl(String name, Object userObject) {
		this.name = name;
		this.userObject = userObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.gui.graph.Node#getName()
	 */
	public String getName() {
		return this.name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.gui.graph.Node#getUserObject()
	 */
	public Object getUserObject() {
		return this.userObject;
	}

	public String toString() {
		return this.name;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof Node)) {
			return false;
		}
		Node otherNode = (Node) obj;
		Object otherUserObject = otherNode.getUserObject();
		if ((this.userObject == null) || (otherUserObject == null)) {
			return false;
		}
		if (!this.userObject.getClass().equals(otherUserObject.getClass())) {
			return false;
		}
		return this.userObject.equals(otherUserObject);
	}

	public int hashCode() {
		return this.userObject.hashCode();
	}

}
