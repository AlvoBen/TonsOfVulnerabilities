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

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;

import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.frame.cluster.monitor.ClusterMonitor;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.services.dc.cm.server.Server;
import com.sap.engine.services.dc.cm.server.ServerFactory;
import com.sap.engine.services.dc.cm.server.ServerService;
import com.sap.engine.services.dc.cm.server.spi.ServerMode;
import com.sap.engine.services.dc.cm.server.spi.ServerModeService;
import com.sap.engine.services.dc.cm.server.spi.ServerModeService.ServerModeServiceException;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.engine.services.dc.util.logging.DCLogConstants;
import com.sap.tc.logging.Location;

/**
 * Wrappes the ClusterMonitor functionality.
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public final class ClusterUtils {
	private static final ClusterUtils instance = new ClusterUtils();
	private static Location location = DCLog.getLocation(ClusterUtils.class);

	private ClusterUtils() {
	}

	public static ClusterUtils getInstance() {
		return instance;
	}

	/**
	 * Checks for other running cluster node, on which is running the same
	 * service.
	 * 
	 * @param cm
	 * @return true if threr are, otherwise false.
	 */
	public static boolean areThereOthers(ClusterMonitor cm) {
		if (cm == null) {
			return false;
		}
		ClusterElement cElements[] = cm.getServiceNodes();
		if (cElements != null && cElements.length != 0) {
			ClusterElement ce = null;
			for (int i = 0; i < cElements.length; i++) {
				ce = cElements[i];
				if (ce.getState() == ClusterElement.RUNNING) {
					return true;
				}
			}
		}
		return false;
	}

	public static String getClusterPerformerId(ConfigurationHandler cfgHandler)
			throws ConfigurationException, NullPointerException {
		try {
			final Configuration clusterConfigCfg = CfgUtils.openCfgRead(
					cfgHandler, "cluster_config");
			return (String) clusterConfigCfg.getConfigEntry("performerID");
		} finally {
			cfgHandler.closeAllConfigurations();
		}
	}

	public static void buildLockOnFS(String operation) throws IOException {

		File file = getLockFSFile();
		String absolutePath = file.getAbsolutePath();

		if (location.bePath()) {
			DCLog
				.tracePath(location,
						"Locking the instance for offline operation with file [{0}] ...",
						new Object[] { absolutePath });
		}

		if (file.exists()) {
			DCLog.logWarning(location, "File already exists: [{0}]",
					"ASJ.dpl_dc.004704", new Object[] { absolutePath });
			if (!file.delete()) {
				// reusing the old file is OK because the offline part just
				// checks for its existence
				DCLog.logWarning(location, "ASJ.dpl_dc.004705",
						"Cannot remove file: [{0}]",
						new Object[] { absolutePath });
				return; // the lock file exists and cannot be removed which in
				// this case is equivalent to the creation of the file
			}
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(operation.getBytes());
			fos.flush();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// $JL-EXC$
				}
			}
		}
	}

	public static String getLockOperationFromFS() {
		File file = getLockFSFile();
		if (file.exists()) {
			return "n/a";
		}
		return null;
	}

	public static void cleanLockOnFS() {

		File file = getLockFSFile();
		String absolutePath = file.getAbsolutePath();
		
		if (location.bePath()) {
			DCLog
				.tracePath(location, "Unlocking the {0}.",
				new Object[] { "instance offline operation by deleting file ["
						+ absolutePath + "]" });
		}
				
		if (file != null && file.exists()) {
			if (!file.delete()) {
				DCLog.logError(location, "ASJ.dpl_dc.004706",
						"Cannot remove file: [{0}]",
						new Object[] { absolutePath });
			} else {
				if (location.bePath()) {
					tracePath(location, "File removed successfully: [{0}]",
							new Object[] { "offline operation lock"
									+ absolutePath });
				}
			}
		} else {
			if (location.bePath()) {
				tracePath(location, "File does not exist: [{0}]",
						new Object[] { " offline operation lock "
								+ absolutePath });
			}
		}
	}

	private static File getLockFSFile() {
		String clusterFolder = SystemProfileManager
				.getSysParamValue(SystemProfileManager.DIR_CLUSTER);
		File file = new File(clusterFolder, "offlineDCinfo.dat");
		return file;
	}

	public ServerMode getServerMode(
			ConfigurationHandlerFactory configurationHandlerFactory)
			throws ServerModeServiceException {
		final ServerModeService serverModeService = getServerModeService(configurationHandlerFactory);
		ServerMode serverMode = serverModeService.getServerMode();
		return serverMode;
	}

	public ServerModeService getServerModeService(
			ConfigurationHandlerFactory configurationHandlerFactory)
			throws ServerModeServiceException {
		final Server server = ServerFactory.getInstance().createServer();
		final ServerService serverService = server
				.getServerService(ServerFactory.getInstance()
						.createOfflineServerModeRequest(
								configurationHandlerFactory));

		if (serverService == null
				|| !(serverService instanceof ServerModeService)) {
			final String errMsg = "ASJ.dpl_dc.003391 Received ServerService for get/set the server mode "
					+ "which is not of type ServerModeService.";
			throw new ServerModeServiceException(errMsg);
		}

		return (ServerModeService) serverService;
	}

	public static ClusterInfo getClusterInfoWithoutThis(
			ClusterMonitor clusterMonitor) {
		final ClusterElement[] elements = clusterMonitor.getServiceNodes();
		return getClusterInfo(elements);
	}

	public static ClusterInfo getClusterInfoWithThis(
			ClusterMonitor clusterMonitor) {
		final ClusterElement[] elements = clusterMonitor.getServiceNodes();
		final ClusterElement[] all = new ClusterElement[elements.length + 1];
		System.arraycopy(elements, 0, all, 0, elements.length);
		all[all.length - 1] = clusterMonitor.getCurrentParticipant();
		return getClusterInfo(all);
	}

	private static ClusterInfo getClusterInfo(ClusterElement[] elements) {
		Hashtable<Integer, int[]> groupIDs_serverIDs = null;
		if (elements != null) {
			groupIDs_serverIDs = new Hashtable<Integer, int[]>();
			Integer key = null;
			int serverIDs[] = null;
			for (int i = 0; i < elements.length; i++) {
				if (elements[i].getType() == ClusterElement.SERVER) {
					key = new Integer(elements[i].getGroupId());
					serverIDs = (int[]) groupIDs_serverIDs.get(key);
					serverIDs = DUtils.concatArrays(serverIDs,
							new int[] { elements[i].getClusterId() });
					groupIDs_serverIDs.put(key, serverIDs);
				}
			}
		}

		return new ClusterInfo(groupIDs_serverIDs);
	}

}
