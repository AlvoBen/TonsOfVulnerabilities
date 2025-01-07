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

package com.sap.engine.services.deploy.server.remote;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.frame.cluster.monitor.ClusterMonitor;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.util.CAConvertor;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.server.DUtils;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.utils.DSRemoteException;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * Utility class for cluster monitoring operations, used to discover servers in 
 * the cluster by different criteria.
 * @author Anton Georgiev
 * 
 * @version 7.00
 */
public final class ClusterMonitorHelper {
	private static final Location location = 
		Location.getLocation(ClusterMonitorHelper.class);

	private final ClusterMonitor cm;
	
	/**
	 * @param cm the cluster monitor. Not null.
	 */
	public ClusterMonitorHelper(final ClusterMonitor cm) {
		assert cm != null;
		this.cm = cm;
	}
	
	/**
	 * @return the ID of the current server node.
	 */
	public int getCurrentServerId() {
		// There is always a current participant (not null).
		return cm.getCurrentParticipant().getClusterId();
	}

	/**
	 * @return the ID of the current instance.
	 */
	public int getCurrentInstanceId() {
		// There is always a current participant (not null).		
		return cm.getCurrentParticipant().getGroupId();
	}
	
	/**
	 * Return the name of the server with the given ID.
	 * @param serverId the server ID.
	 * @return the name of the server with the given ID.
	 */
	public String getServerName(final int serverId) {
		final ClusterElement node = cm.getParticipant(serverId);
		return node == null ? null : node.getName();
	}

	/**
	 * Returns the server IDs from server names. Not null.
	 * @param serverNames can be null, which means that the all server IDs will
	 * be returned including the current server.
	 * @return the server IDs from server names. Not null.
	 * @throws DeploymentException if some server name cannot be translated. 
	 */
	public int[] getServerIDs(String[] serverNames)	throws DeploymentException {
		if(serverNames == null || serverNames.length == 0) {
			return findServers();
		}
		// Get all elements in the cluster, where the DS is running,
		// except the current server.
		final ClusterElement[] serviceNodes = cm.getServiceNodes();
		assert serviceNodes != null;
		final int[] serverIDs = new int[serverNames.length];
		final String currName = cm.getCurrentParticipant().getName();
		int pos = 0;
		for(final String serverName : serverNames) {
			if(currName.equals(serverName)) {
				serverIDs[pos++] = getCurrentServerId();
				continue;
			}
			for(final ClusterElement node : serviceNodes) {
				if(node.getName().equals(serverName)) {
					serverIDs[pos++] = node.getClusterId();
					// Breaks the inner cycle.
					break;
				}
			}
		}

		if(pos < serverNames.length) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.CANNOT_FIND_SERVER,
				CAConvertor.toString(serverNames, ""));
			sde.setMessageID("ASJ.dpl_ds.005067");
			throw sde;
		}
		return serverIDs;
	}
    
	/**
	 * @return <tt>true</tt> if the communication is disabled.
	 */
	@SuppressWarnings("boxing")
	public boolean isCommunicationDisabled() {
		final boolean disabled = cm.getCurrentParticipant().getState() == 
			ClusterElement.DEBUGGING;
		if (disabled && location.beDebug()) {
			SimpleLogger.trace(Severity.DEBUG, location, null,
				"The current server node [{0}] is in DEBUGGING state " +
				"and is not allowed to trigger cluster wide operation.",
				getCurrentServerId());
		}
		return disabled;
	}
	
	/**
	 * @return all servers in the cluster, where the deploy service is active
	 * including the current server node. Not null and always contains at least 
	 * the ID of the current server node.
	 */
	public int[] findServers() {
		// Get all elements in the cluster, where the DS is running,
		// except the current server.
		final ClusterElement[] serviceNodes = cm.getServiceNodes();
		assert serviceNodes != null;
		final int[] serverIDs = new int[serviceNodes.length + 1];
		for(int i = 0; i < serviceNodes.length; i++) {
			assert serviceNodes[i].getType() == ClusterElement.SERVER;
			serverIDs[i] = serviceNodes[i].getClusterId();
		}
		serverIDs[serverIDs.length - 1] = getCurrentServerId();
		return serverIDs;
	}

	/**
	 * Return IDs of all server nodes which are part of the given instances and
	 * the deploy service is running on them. If the current instance is in the 
	 * set, the current node will be also included.
	 * @param instances Set of instance IDs. Not null but can be empty. If the
	 * set is empty, we have to return all servers in the cluster.
	 * @return IDs of all server nodes which are part of the given instances
	 * including the current node.
	 */
	@SuppressWarnings("boxing")
	public int[] findServers(final Set<Integer> instances) {
		assert instances != null;
		if(instances.size() == 0) {
			return findServers();
		}
		// Get all elements in the cluster, where the DS is running,
		// except the current server.
		final ClusterElement[] serviceNodes = cm.getServiceNodes();
		assert serviceNodes != null;
		// Allocate array size including a place for the current node.
		final int[] serverIDs = new int[serviceNodes.length + 1];
		int pos = 0;
		for (final ClusterElement node : serviceNodes) {
			if(instances.contains(node.getGroupId())) {
				serverIDs[pos++] = node.getClusterId();
			}
		}
		if(instances.contains(getCurrentInstanceId())) {
			serverIDs[pos++] = getCurrentServerId();
		}
		return Arrays.copyOf(serverIDs, pos);
	}
	
	/**
	 * Find the eligible receivers checking the state of every server node found
	 * in the cluster. The current node is always filtered out.
	 * @return the IDs of the eligible receivers. Not null, but can be empty.
	 */
	public int[] findEligibleReceivers() {
		return filterEligibleReceivers(findServers());
	}

	/**
	 * Filter the eligible receivers checking the state of every of the passed 
	 * server nodes. The current node is always filtered out.
	 * @param serverIDs server IDs. Not null.
	 * @return the IDs of the eligible receivers. Not null, but can be empty.
	 */
	@SuppressWarnings("boxing")
	public int[] filterEligibleReceivers(int[] serverIDs) {
		assert serverIDs != null;
		if(isCommunicationDisabled()) {
			return new int[0];
		}
		final int available[] = new int[serverIDs.length];
		final int current = getCurrentServerId();
		int index = 0;
		for (final int serverId : serverIDs) {
			if(serverId == current) {
				// Filter out the current node.
				continue;
			}
			final ClusterElement node = cm.getParticipant(serverId);
			if (node != null) {
				if (node.getState() == ClusterElement.RUNNING) {
					available[index++] = serverId;
					continue;
				}
				if (location.beDebug()) {
					SimpleLogger.trace(Severity.DEBUG, location, null,
						"The server node [{0}] is in [{1}] state " +
						"and won't recieve message.",
						serverId, node.getStateString());
				}
			} else {
				if (location.beDebug()) {
					SimpleLogger.trace(Severity.PATH, location, null,
						"The server node [{0}] is not available " +
						"and won't recieve message.",
						serverId);
				}
			}
		}
		return Arrays.copyOf(available, index);
	}

	/**
	 * @return all server nodes except the current one, belonging to the current
	 * instance. Not null but can be empty.
	 */
	public int[] findOtherServersInCurrentInstance() {
		// Get all elements in the cluster, where the DS is running,
		// except the current server.
		final ClusterElement[] serviceNodes = cm.getServiceNodes();
		assert serviceNodes != null;
		
		final int curInstance = getCurrentInstanceId();
		
		final int[] serverIDs = new int[serviceNodes.length];
		int pos = 0;
		for(final ClusterElement node : serviceNodes) {
			if (node.getGroupId() == curInstance) {
				serverIDs[pos++] = node.getClusterId();
			}
		}
		return Arrays.copyOf(serverIDs, pos);
	}

	/**
	 * @return the ID of another running server in the current instance or -1 if
	 * no such server node exists.
	 */
	public int findAnotherRunningServerInCurrentInstance() {
    	// Get all elements in the cluster, where the DS is running,
		// except the current server.
		final ClusterElement[] serviceNodes = cm.getServiceNodes();
		assert serviceNodes != null;
		
		final int curInstance = getCurrentInstanceId();
		
		for(final ClusterElement node : serviceNodes) {
			assert node.getType() == ClusterElement.SERVER;
			if (node.getState() == ClusterElement.RUNNING &&
				node.getGroupId() == curInstance) {
				return node.getClusterId();
			}
		}
		return -1;
	}

	/**
	 * @return one server node per instance, excluding the current instance. Not
	 * null, but can be empty.
	 */
	@SuppressWarnings("boxing")
	public int[] findOneServerPerInstanceExceptCurrent() {
		// Get all elements in the cluster, where the DS is running,
		// except the current server.
		final ClusterElement[] serviceNodes = cm.getServiceNodes();
		assert serviceNodes != null;

		final Set<Integer> instances = new HashSet<Integer>();
		// The current instance has to be excluded.
		instances.add(getCurrentInstanceId());
		final int[] serverIDs = new int[serviceNodes.length];
		int pos = 0;
		for(final ClusterElement node : serviceNodes) {
			assert node.getType() == ClusterElement.SERVER;
			if (!instances.contains(node.getGroupId())) {
				serverIDs[pos++] = node.getClusterId();
				instances.add(node.getGroupId());
			}
		}
		return Arrays.copyOf(serverIDs, pos);
	}
	
	/**
	 * Returns the index of local server ID in the specified set of server IDs.
	 * @param serverIDs set of server IDs. Can be null or empty array.
	 * @return the index of local server ID in the specified set of server IDs 
	 * or -1 if the local server ID is not between them.
	 */
	public int findIndexOfCurrentServerId(int[] serverIDs) {
		return DUtils.findElement(getCurrentServerId(), serverIDs);
	}

	/**
	 * Expand the current set of servers to contain all servers that are part of
	 * the instances to which belongs any of the given server names, including 
	 * the current server node.
	 * @param serverNames can be null or empty, which means that all servers 
	 * will be included.
	 * @return all servers that are part of the instances to which belongs any 
	 * of the given server names, including the current server node. The result
	 * can be empty array, but not null.
	 * @throws RemoteException 
	 */
	@SuppressWarnings("boxing")
	public int[] expandToWholeInstances(String[] serverNames) 
		throws DSRemoteException {
		if (serverNames == null || serverNames.length == 0) {
			return findServers();
		}
		int[] serverIDs;
		try {
			serverIDs = getServerIDs(serverNames);
		} catch(DeploymentException dex) {
			throw new DSRemoteException("ASJ.dpl_ds.006193 " +
				"Error while getting serverIDs " + 
				CAConvertor.toString(serverNames, ""),
				dex);
		}
		assert serverIDs.length == serverNames.length;
		final Set<Integer> instances = new HashSet<Integer>();
		for (int serverID : serverIDs) {
			instances.add(cm.getParticipant(serverID).getGroupId());
		}
		return findServers(instances);
	}
}