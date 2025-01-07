/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.util.structure.tree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.sap.engine.services.dc.util.Constants;

/**
 * Reperecents tree structure.
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class TreeNode {

	private final String name;
	private final int hashCode;
	private final Map leaves = new HashMap();
	private Map entires;

	private String parentPath = "";

	public TreeNode(String _name) {
		this.name = _name;
		this.hashCode = getName().hashCode();
	}

	/**
	 * Gets all leaves of current node.
	 * 
	 * @return
	 */
	public Map getLeaves() {
		return this.leaves;
	}

	/**
	 * Gets the name of the current node.
	 * 
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Adds new leaf to the current node.
	 * 
	 * @param leaf
	 *            <code>TreeNode</code>
	 */
	public void addLeaf(TreeNode leaf) {
		String newPerantPath = "";
		if (getParentPath() != null && !getParentPath().equals("")) {
			newPerantPath = getParentPath() + Constants.CFG_PATH_SEPARATOR
					+ getName();
		} else {
			newPerantPath = getName();
		}
		leaf.setParentPath(newPerantPath);
		TreeNode child = (TreeNode) this.leaves.get(leaf.getName());
		if (child != null) {
			child.addLeaves(leaf.getLeaves());
		} else {
			this.leaves.put(leaf.getName(), leaf);
		}
	}

	/**
	 * Addes new leaves to the current node.
	 * 
	 * @param childs
	 *            <code>ArrayList</code>
	 */
	public void addLeaves(Map childs) {
		final Iterator leavesIter = childs.values().iterator();
		TreeNode leaf = null;
		while (leavesIter.hasNext()) {
			leaf = (TreeNode) leavesIter.next();
			addLeaf(leaf);
		}
	}

	/**
	 * Gets the parent path to the current node.
	 * 
	 * @return
	 */
	public String getParentPath() {
		return this.parentPath;
	}

	public void addEntry(String key, Object value) {
		if (this.entires == null) {
			synchronized (this) {
				if (this.entires == null) {
					this.entires = new HashMap();
				}
			}
		}
		this.entires.put(key, value);
	}

	public Map getEntries() {
		return this.entires;
	}

	// Sets new parent path to the current node
	private void setParentPath(String string) {
		this.parentPath = string;
		final Iterator leavesIter = getLeaves().values().iterator();
		TreeNode leaf = null;
		while (leavesIter.hasNext()) {
			leaf = (TreeNode) leavesIter.next();
			leaf.setParentPath(this.parentPath + Constants.CFG_PATH_SEPARATOR
					+ leaf.getParentPath());
		}
	}

	// Adds new cell at the begining of the parent path.
	/*
	 * private void addParentPath(String add) { if (getParentPath() != null &&
	 * !getParentPath().equals("")) { setParentPath(add +
	 * Constants.CFG_PATH_SEPARATOR + getParentPath()); } else {
	 * setParentPath(add); } }
	 */
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (this.getClass() != obj.getClass()) {
			return false;
		}

		final TreeNode tNode = (TreeNode) obj;

		if (!this.getName().equals(tNode.getName())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.hashCode;
	}

	public String toString() {
		return getName();
	}

}
