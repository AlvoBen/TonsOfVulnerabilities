/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http:////www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.server;

import java.rmi.RemoteException;
import java.util.Properties;

import com.sap.engine.services.deploy.container.FileUpdateInfo;
import com.sap.engine.services.deploy.timestat.TransactionTimeStat;

/**
 * Wrapper for DeployServiceImpl, which adds functionality, to allow time
 * statistics logging. This class is intended only for internal use by deploy
 * service.
 * 
 * @author Luchesar Cekov
 */
public class DeployServiceImplTimeStatWrapper extends DeployServiceImpl {

	public DeployServiceImplTimeStatWrapper() throws RemoteException {
		super();
	}

	@Override
	public String[] deploy(String earFile, String[] remoteSupport,
		Properties props) throws RemoteException {
		TransactionTimeStat timeStat = TransactionTimeStat
				.createIfNotAvailable("Deploy");
		try {
			return super.deploy(earFile, remoteSupport, props);
		} finally {
			timeStat.finish();
		}
	}

	public String[] deploy(String archiveFile, String containerName,
			String[] remoteSupport, Properties props) throws RemoteException {
		TransactionTimeStat timeStat = TransactionTimeStat
				.createIfNotAvailable("Deploy");
		try {
			return super.deploy(archiveFile, containerName, remoteSupport,
					props);
		} finally {
			timeStat.finish();
		}
	}

	public String[] update(String archiveFile, String containerName,
			String[] remoteSupport, Properties props) throws RemoteException {
		TransactionTimeStat timeStat = TransactionTimeStat
				.createIfNotAvailable("Update");
		try {
			return super.update(archiveFile, containerName, remoteSupport,
					props);
		} finally {
			timeStat.finish();
		}
	}

	public String[] update(String archiveName, Properties props)
			throws RemoteException {
		TransactionTimeStat timeStat = TransactionTimeStat
				.createIfNotAvailable("Update");
		try {
			return super.update(archiveName, props);
		} finally {
			timeStat.finish();
		}
	}

	public void singleFileUpdate(FileUpdateInfo[] files, String appName,
			Properties props) throws RemoteException {
		TransactionTimeStat timeStat = TransactionTimeStat
				.createIfNotAvailable("Single File Update");
		try {
			super.singleFileUpdate(files, appName, props);
		} finally {
			timeStat.finish();
		}
	}

	public void remove(String applicationName) throws RemoteException {
		TransactionTimeStat timeStat = TransactionTimeStat
				.createIfNotAvailable("Remove", applicationName);
		try {
			super.remove(applicationName);
		} finally {
			timeStat.finish();
		}
	}

	public void stopApplication(String applicationName) throws RemoteException {
		TransactionTimeStat timeStat = TransactionTimeStat
				.createIfNotAvailable("Stop Application", applicationName);
		try {
			super.stopApplication(applicationName);
		} finally {
			timeStat.finish();
		}
	}

	public void stopApplicationAndWait(String applicationName)
			throws RemoteException {
		TransactionTimeStat timeStat = TransactionTimeStat
				.createIfNotAvailable("Stop Application Synchron",
						applicationName);
		try {
			super.stopApplicationAndWait(applicationName);
		} finally {
			timeStat.finish();
		}
	}

	public void stopApplicationAndWait(String appName, String[] serverNames)
			throws RemoteException {
		TransactionTimeStat timeStat = TransactionTimeStat
				.createIfNotAvailable("Stop Application Synchron", appName);
		try {
			super.stopApplicationAndWait(appName, serverNames);
		} finally {
			timeStat.finish();
		}
	}

	public void stopApplication(String applicationName, String[] serverNames)
			throws RemoteException {
		TransactionTimeStat timeStat = TransactionTimeStat
				.createIfNotAvailable("Stop Application", applicationName);
		try {
			super.stopApplication(applicationName, serverNames);
		} finally {
			timeStat.finish();
		}
	}

	public void startApplication(String applicationName) throws RemoteException {
		TransactionTimeStat timeStat = TransactionTimeStat
				.createIfNotAvailable("Start Application", applicationName);
		try {
			super.startApplication(applicationName);
		} finally {
			timeStat.finish();
		}
	}

	public void startApplicationAndWait(String applicationName)
			throws RemoteException {
		TransactionTimeStat timeStat = TransactionTimeStat
				.createIfNotAvailable("Start Application Synchron",
						applicationName);
		try {
			super.startApplicationAndWait(applicationName);
		} finally {
			timeStat.finish();
		}
	}

	public void startApplicationAndWait(String appName, String[] serverNames)
			throws RemoteException {
		TransactionTimeStat timeStat = TransactionTimeStat
				.createIfNotAvailable("Start Application Synchron", appName);
		try {
			super.startApplicationAndWait(appName, serverNames);
		} finally {
			timeStat.finish();
		}
	}

	public void startApplication(String applicationName, String[] serverNames)
			throws RemoteException {
		TransactionTimeStat timeStat = TransactionTimeStat
				.createIfNotAvailable("Start Application", applicationName);
		try {
			super.startApplication(applicationName, serverNames);
		} finally {
			timeStat.finish();
		}
	}

	public void remove(String providerName, String applicationName)
			throws RemoteException {
		TransactionTimeStat timeStat = TransactionTimeStat
				.createIfNotAvailable("Remove", applicationName);
		try {
			super.remove(providerName, applicationName);
		} finally {
			timeStat.finish();
		}
	}

	public void singleFileUpdate(FileUpdateInfo[] files, String providerName,
			String appName, Properties props) throws RemoteException {
		TransactionTimeStat timeStat = TransactionTimeStat
				.createIfNotAvailable("Single File Update", appName);
		try {
			super.singleFileUpdate(files, providerName, appName, props);
		} finally {
			timeStat.finish();
		}
	}
}
