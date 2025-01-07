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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sap.engine.services.dc.util.Constants;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class TreeNodeUtils {

	/**
	 * Returns the paths to each leaf as <code>String<code>.
	 * 
	 * @param treeRoots
	 *            <code>TreeNode</code>[]
	 * @return <code>Set</code> with <code>String</code>s
	 */
	public static Set getLeafPaths(TreeNode treeRoots[]) {
		final Set rootsSet = joinTreeRoots(treeRoots);

		final Set leafPaths = new HashSet();
		getLeafPaths(rootsSet.iterator(), leafPaths);

		return leafPaths;
	}

	private static void getLeafPaths(Iterator roots, Set leafPaths) {
		TreeNode root;
		Map childs;
		while (roots.hasNext()) {
			root = (TreeNode) roots.next();
			childs = root.getLeaves();
			if (childs.size() == 0) {
				leafPaths.add(root.getParentPath()
						+ Constants.CFG_PATH_SEPARATOR + root.getName());
			} else {
				getLeafPaths(childs.values().iterator(), leafPaths);
			}
		}
	}

	/**
	 * Joins the trees, which are with same root.
	 * 
	 * @param treeRoots
	 *            <code>TreeNode</code>[]
	 * @return <code>Set</code> with <code>TreeNode</code>s
	 */
	public static Set joinTreeRoots(TreeNode treeRoots[]) {
		final Set joinedTreeRoots = new HashSet();
		for (int i = 0; i < treeRoots.length; i++) {
			checkJoinTreeRoots(treeRoots[i], joinedTreeRoots);
		}
		return joinedTreeRoots;
	}

	private static void checkJoinTreeRoots(TreeNode addRoot, Set joinedTreeRoots) {
		final Iterator joinedIter = joinedTreeRoots.iterator();
		TreeNode joinedRoot;
		boolean isForAdd = true;
		while (joinedIter.hasNext()) {
			joinedRoot = (TreeNode) joinedIter.next();
			if (compareName(addRoot, joinedRoot)) {
				joinedRoot.addLeaves(addRoot.getLeaves());
				isForAdd = false;
				break;
			}
		}
		if (isForAdd) {
			joinedTreeRoots.add(addRoot);
		}
	}

	// Compares only the name.
	private static boolean compareName(TreeNode a, TreeNode b) {
		if (a.getName().equals(b.getName())) {
			return true;
		}
		return false;
	}

}
