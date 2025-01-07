/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * <<http://www.sap.com>>
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.deploy.server;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.rmi.PortableRemoteObject;

import com.sap.engine.frame.state.ManagementListener;
import com.sap.engine.lib.io.HashOutputStream;
import com.sap.engine.services.deploy.DeployRuntimeControlInterface;
import com.sap.engine.services.deploy.DeployService;
import com.sap.engine.services.deploy.container.AdditionalAppInfo;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.cache.dpl_info.Applications;
import com.sap.tc.logging.Location;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

public class DeployRuntimeControlImpl extends PortableRemoteObject implements
		DeployRuntimeControlInterface {
	private static final Location location = 
		Location.getLocation(DeployRuntimeControlImpl.class);

	private static final int CONTAINER_PROPERTIES_COUNT = 7;
	private final DeployServiceImpl deployService;

	public DeployRuntimeControlImpl(DeployServiceImpl ds)
			throws RemoteException {
		deployService = ds;
	}

	public String getApplicationStatus(String applicationName, String serverName)
			throws RemoteException {
		return deployService.getApplicationStatus(applicationName, serverName);
	}

	public boolean isApplicationStarted(String applicationName)
			throws RemoteException {
		if (DeployService.STARTED_APP_STATUS.equals(getApplicationStatus(
				applicationName, null))) {
			return true;
		} else {
			return false;
		}
	}

	public String[][] getApplicationsInfo() throws RemoteException {
		final int dimantion = 4;

		String[] applications = deployService.listApplications();
		Arrays.sort(applications);
		final SortedSet<String> failed2Start = deployService
				.getManagementListenerUtils().getFailed2Start();

		final String[][] appsInfo = new String[applications.length][dimantion];
		try {
			// first - failed to start ones
			final Iterator<String> failed2StartIter = failed2Start.iterator();
			int curr = 0;
			while (failed2StartIter.hasNext()) {
				setApplicationsInfo(appsInfo, curr, failed2StartIter.next());
				curr++;
			}
			// second - the rest
			for (int i = 0; i < applications.length; i++) {
				if (failed2Start.contains(applications[i])) {
					failed2Start.remove(applications[i]);
				} else {
					setApplicationsInfo(appsInfo, i + failed2Start.size(),
							applications[i]);
				}
			}
			// check
			if (failed2Start.size() != 0) {
				throw new IllegalStateException(
						"Cannot build requested applications info, because "
								+ failed2Start + " are not deployed.");
			}
		} catch (RuntimeException re) {
			DSLog.logErrorThrowable(location, "ASJ.dpl_ds.006357",
					"Exception while trying to get applications info", re);
			throw re;
		} catch (RemoteException re) {
			DSLog
					.logErrorThrowable(
							location, 
							"ASJ.dpl_ds.006358",
							"Exception on remote node while trying to get applications info",
							re);
			throw re;
		}

		return appsInfo;
	}

	private void setApplicationsInfo(String[][] appsInfo, int i, String appName)
			throws RemoteException {
		appsInfo[i][0] = appName;
		appsInfo[i][1] = deployService.getApplicationStatus(appName);
		appsInfo[i][2] = AdditionalAppInfo.getStartUpString(deployService
				.getApplicationInfo(appName).getStartUp());
		if (Applications.get(appName).getExceptionInfo() != null) {
			appsInfo[i][3] = Applications.get(appName).getExceptionInfo()
					.getStackTrace();
		} else {
			appsInfo[i][3] = "";
		}
	}

	public void registerManagementListener(ManagementListener managementListener) {
		deployService.getManagementListenerUtils().setManagementListener(
				managementListener);
	}

}