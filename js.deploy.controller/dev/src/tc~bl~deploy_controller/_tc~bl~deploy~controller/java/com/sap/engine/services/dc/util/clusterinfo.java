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

package com.sap.engine.services.dc.util;

import java.util.Hashtable;
import java.util.Enumeration;

/**
 * Provides info about the cluster configuration.
 * 
 * @author Anton Georgiev
 * @version 7.00
 */
public class ClusterInfo {

	private Hashtable groupIDs_serverIDs = null;
	private int serverIDs[] = new int[0];
	private int groupIDs[] = new int[0];

	public ClusterInfo(Hashtable groupIDs_serverIDs) {
		setGroupIDs_serverIDs(groupIDs_serverIDs);
	}

	/**
	 * Sets the cluster info
	 * 
	 * @param groupIDs_serverIDs
	 *            where the groupIDs, which is hashtablekey is
	 *            java.lang.Integer, but the serverIDs are int[].
	 */
	private void setGroupIDs_serverIDs(Hashtable groupIDs_serverIDs) {
		this.groupIDs_serverIDs = groupIDs_serverIDs;
		if (groupIDs_serverIDs != null) {
			Enumeration enum1 = groupIDs_serverIDs.keys();
			Integer key = null;
			while (enum1.hasMoreElements()) {
				key = (Integer) enum1.nextElement();
				groupIDs = DUtils.concatArrays(groupIDs, new int[] { key
						.intValue() });
				serverIDs = DUtils.concatArrays(serverIDs,
						(int[]) groupIDs_serverIDs.get(key));
			}
		}
	}

	/**
	 * Gets all the cluster IDs of all server node in the cluster.
	 * 
	 * @return all the cluster IDs of all server node in the cluster.
	 */
	public int[] getServerIDs() {
		return serverIDs;
	}

	/**
	 * Gets all the cluster IDs of all server node in the cluster from the given
	 * group.
	 * 
	 * @param groupID
	 * @return all the cluster IDs of all server node in the cluster from the
	 *         given group.
	 */
	public int[] getServerIDs(int groupID) {
		return (int[]) groupIDs_serverIDs.get(new Integer(groupID));
	}

	/**
	 * Gets all the cluster IDs of all server node in the cluster from the given
	 * group without the removeServerID.
	 * 
	 * @param groupID
	 * @param removeServerID
	 * @return all the cluster IDs of all server node in the cluster from the
	 *         given group without the removeServerID.
	 */
	public int[] getServerIDs(int groupID, int removeServerID) {
		return DUtils.removeElements(getServerIDs(groupID),
				new int[] { removeServerID });
	}

	/**
	 * Gets an array, which contains only one server node per group. The int
	 * value from the returned array represents a server ID from the groupIDs[i]
	 * group.
	 * 
	 * @return an array, which contains only one server node per group.
	 */
	public int[] getOneServerFromGroupIDs() {
		int result[] = null;
		int temp[] = null;
		if (groupIDs != null) {
			for (int i = 0; i < groupIDs.length; i++) {
				temp = getServerIDs(groupIDs[i]);
				if (temp != null) {
					result = DUtils.concatArrays(result, new int[] { temp[0] });
				}
			}
		}
		return result;
	}

	/**
	 * Gets an array, which contains only one server node per group excluding
	 * the removeGroupID.
	 * 
	 * @param removeGroupID
	 * @return an array, which contains only one server node per group excluding
	 *         the removeGroupID.
	 */
	public int[] getOneServerFromGroupIDs(int removeGroupID) {
		int result[] = null;
		int temp[] = null;
		if (groupIDs != null) {
			for (int i = 0; i < groupIDs.length; i++) {
				if (removeGroupID == groupIDs[i]) {
					continue;
				}
				temp = getServerIDs(groupIDs[i]);
				if (temp != null) {
					result = DUtils.concatArrays(result, new int[] { temp[0] });
				}
			}
		}
		return result;
	}

	/**
	 * Gets all group IDs in the cluster.
	 * 
	 * @return all group IDs in the cluster.
	 */
	public int[] getGroupIDs() {
		return groupIDs;
	}

}
