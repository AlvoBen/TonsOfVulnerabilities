/*
 * Copyright (C) 2000 - 2005 by SAP AG, Walldorf,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.api.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import com.sap.engine.services.dc.api.APIException;
import com.sap.engine.services.dc.api.Batch;
import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.ServiceNotAvailableException;
import com.sap.engine.services.dc.api.deploy.DeployBatch;
import com.sap.engine.services.dc.api.deploy.DeployItem;
import com.sap.engine.services.dc.api.deploy.DeploySettings;
import com.sap.engine.services.dc.api.deploy.TransportException;
import com.sap.engine.services.dc.api.deploy.ValidationException;
import com.sap.engine.services.dc.api.deploy.impl.DeployItemImpl;
import com.sap.engine.services.dc.api.deploy.impl.DeployItemMapperVisitor;
import com.sap.engine.services.dc.api.deploy.impl.DeployMapper;
import com.sap.engine.services.dc.api.model.Sca;
import com.sap.engine.services.dc.api.model.Sda;
import com.sap.engine.services.dc.api.model.Sdu;
import com.sap.engine.services.dc.api.session.Session;
import com.sap.engine.services.dc.api.undeploy.ScaUndeployItem;
import com.sap.engine.services.dc.api.undeploy.SdaUndeployItem;
import com.sap.engine.services.dc.api.undeploy.UndeployBatch;
import com.sap.engine.services.dc.api.undeploy.UndeployItem;
import com.sap.engine.services.dc.api.undeploy.UndeploySettings;
import com.sap.engine.services.dc.api.undeploy.impl.UndeployItemImpl;
import com.sap.engine.services.dc.api.undeploy.impl.UndeployItemTypeMatchVisitor;
import com.sap.engine.services.dc.api.undeploy.impl.UndeployMapper;
import com.sap.engine.services.dc.api.util.exception.APIExceptionConstants;
import com.sap.engine.services.dc.api.validate.DeployValidationResult;
import com.sap.engine.services.dc.api.validate.UndeployValidationResult;
import com.sap.engine.services.dc.api.validate.ValidationResult;
import com.sap.engine.services.dc.api.validate.ValidationResultSet;
import com.sap.engine.services.dc.cm.CM;
import com.sap.engine.services.dc.cm.CMException;
import com.sap.engine.services.dc.cm.DCNotAvailableException;
import com.sap.engine.services.dc.cm.archive_mng.ArchiveManager;
import com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployFactory;
import com.sap.engine.services.dc.cm.undeploy.UndeploymentException;
import com.sap.engine.services.dc.repo.SduId;
import com.sap.engine.services.dc.util.ComponentPropsCorrector;
import com.sap.engine.services.file.FileTransfer;
import com.sap.engine.services.file.RemoteFile;
import com.sap.engine.services.rmi_p4.P4ObjectBroker;

/**
 * Date: Dec 13, 2007
 * 
 * @author Todor Atanasov(i043963)
 */
public class ValidateUtils {
	private static final String SCA = "SCA";
	private static final String SDA = "SDA";
	private Session session;
	private DALog daLog;
	private final String deployerIdInfo;

	public ValidateUtils(Session session, String deployerIdInfo) {
		this.session = session;
		this.daLog = session.getLog();
		this.deployerIdInfo = deployerIdInfo;
	}

	public Map uploadDeployItems(CM cm, String transactionId,
			DeployItem[] deployItems, String[] remoteFilePaths)
			throws ConnectionException, TransportException {
		return uploadDeployItems(cm, transactionId, deployItems,
				remoteFilePaths, null);
	}

	public Map uploadDeployItems(CM cm, String transactionId,
			DeployItem[] deployItems, String[] remoteFilePaths,
			Integer batchNumber) throws ConnectionException, TransportException {
		boolean isLocal = doCheckForSameMachine(cm, deployItems);
		if (isLocal) {
			return getItemsPath(deployItems, remoteFilePaths);
		} else {
			return uploadItemsToServer(cm, transactionId, deployItems,
					remoteFilePaths, batchNumber);
		}
	}

	private boolean doCheckForSameMachine(CM cm, DeployItem[] deployItems) {
		String daDoNotCheckForLocalhost = System.getProperty(
				DAConstants.SYSTEM_DO_NOT_CHECK_FOR_LOCAL_HOST, "false");
		if (daDoNotCheckForLocalhost.equalsIgnoreCase("true")) {
			if (daLog.isDebugTraceable()) {
				this.daLog
						.traceDebug("doNotCheckForLocalhost is set to false so the files are going to upload to the server even in case the server and client runs on the same machine.");
			}
			return false;
		}
		if (!NetUtils.isLocalhost(this.session.getHost())) {
			return false;
		}
		ArchiveManager archiveManager = null;
		try {
			archiveManager = cm.getArchiveManager();

			// register the reference to the obtained remote object
			// registerRemoteReference(archiveManager);
		} catch (CMException e) {
			this.daLog.logThrowable(e);
		}
		if (archiveManager == null) {
			this.daLog
					.logError(
							"ASJ.dpl_api.001222",
							"Can not retrieve DC Archive Manager in order to check if the files are visible for the engine.");
		}
		boolean ret;
		String canonicalPath;
		try {
			for (int i = 0; i < deployItems.length; i++) {
				try {
					canonicalPath = deployItems[i].getArchive()
							.getCanonicalPath();
					ret = archiveManager.canRead(canonicalPath);
					if (daLog.isDebugTraceable()) {
						this.daLog
								.traceDebug(
										"Can server read File [{0}] : [{1}]",
										new Object[] { canonicalPath,
												new Boolean(ret) });
					}
					if (!ret) {
						return false;
					}
				} catch (IOException e1) {
					this.daLog.logThrowable(e1);
				}
			}
		} finally {
			final P4ObjectBroker broker = P4ObjectBroker.getBroker();
			if (broker != null) {
				broker.release(archiveManager);
			}
		}
		return true;
	}

	private Map getItemsPath(DeployItem[] deployItems, String[] remoteFilePaths)
			throws TransportException {
		Map fileMap = new Hashtable(deployItems.length);
		File archive;
		for (int i = 0; i < deployItems.length; i++) {
			archive = deployItems[i].getArchive();
			try {
				remoteFilePaths[i] = archive.getCanonicalPath();
				if (daLog.isDebugLoggable()) {
					this.daLog.logDebug("Got full sdu path [{0}]",
							new Object[] { remoteFilePaths[i] });
				}
			} catch (IOException e) {
				String msg = "Cannot get canonical path for " + archive;
				this.daLog.logError("ASJ.dpl_api.001223", "{0}",
						new Object[] { msg });
				throw new TransportException(this.daLog.getLocation(),
						APIExceptionConstants.TRANSPORT_IO_EXCEPTION,
						new String[] { msg, e.getLocalizedMessage() }, e);
			}
			fileMap.put(remoteFilePaths[i], deployItems[i]);
		}
		return fileMap;
	}

	private Map uploadItemsToServer(CM cm, String transactionId,
			DeployItem[] deployItems, String[] remoteFilePaths,
			Integer batchNumber) throws ConnectionException, TransportException {
		try {
			String uploadDirPath = cm.getUploadDirName(transactionId);
			String prefix = transactionId
					+ File.separator
					+ ((batchNumber == null) ? ""
							: (batchNumber.toString() + File.separator));
			Map uploadedDeployItemsMap = new HashMap(deployItems.length);
			File archive;
			FileTransfer transferer = this.session.getFileTransfer();
			RemoteFile remoteFile;
			ServiceTimeWatcher serviceTimeWatcher = new ServiceTimeWatcher();
			if (daLog.isPathTraceable()) {
				this.daLog
						.tracePath(
								"Start to upload items to server: timerId=[{0}]. [{1}], transactionId=[{2}]. Upload archives to the server.Remote upload path is [{3}]",
								new Object[] {
										new Long(serviceTimeWatcher.getId()),
										this.deployerIdInfo, transactionId,
										uploadDirPath });
			}
			for (int i = 0; i < deployItems.length; i++) {
				archive = deployItems[i].getArchive();
				if (!archive.exists()) {
					throw new TransportException(this.daLog.getLocation(),
							APIExceptionConstants.TRANSPORT_NOT_EXISTS,
							new String[] { String.valueOf(i),
									archive.getAbsolutePath() });
				} else if (!archive.canRead()) {
					throw new TransportException(this.daLog.getLocation(),
							APIExceptionConstants.TRANSPORT_CANNOT_READ,
							new String[] { String.valueOf(i),
									archive.getAbsolutePath() });
				} else {
					String filePattern = (batchNumber == null) ? archive
							.getName() : concatDirs(batchNumber.toString(),
							archive.getName());
					remoteFilePaths[i] = concatDirs(uploadDirPath, filePattern);
					// upload the file to the upload dir
					if (daLog.isDebugTraceable()) {
						this.daLog.traceDebug(
								"Going to upload [{0}] on the server. [{1}]",
								new Object[] { archive.getAbsolutePath(),
										this.deployerIdInfo });
					}
					remoteFile = transferer.createRemoteFile(archive
							.getAbsolutePath(), remoteFilePaths[i]);
					remoteFile.upload();
					uploadedDeployItemsMap.put(prefix + archive.getName(),
							deployItems[i]);
					if (daLog.isDebugTraceable()) {
						this.daLog
								.traceDebug(
										"[{0}], transactionId=[{1}]. File [{2}] uploaded successfully as [{3}]. Time:[{4}]",
										new Object[] {
												this.deployerIdInfo,
												transactionId,
												archive.getAbsolutePath(),
												remoteFilePaths[i],
												serviceTimeWatcher
														.getElapsedTimeAsString() });
					}
				}
			}
			if (daLog.isPathTraceable()) {
				this.daLog.tracePath("[{0}]. Archives uploaded. Time: [{1}]",
						new Object[] {
								this.deployerIdInfo,
								serviceTimeWatcher
										.getTotalElapsedTimeAsString() });
			}
			return uploadedDeployItemsMap;
		} catch (IOException ioe) {
			throw new TransportException(this.daLog.getLocation(),
					APIExceptionConstants.TRANSPORT_IO_EXCEPTION, new String[] {
							DAUtils.getThrowableClassName(ioe),
							ioe.getMessage() }, ioe);
		}
	}

	private static String concatDirs(String baseDir, String dir) {
		if (baseDir == null) {
			throw new NullPointerException("[ERROR CODE DPL.DCAPI.1044] "
					+ "The argument 'baseDir' could not be null!");
		}
		if (dir == null) {
			throw new NullPointerException(
					"[ERROR CODE DPL.DCAPI.1045] The argument 'dir' could not be null!");
		}

		if (!baseDir.endsWith("\\") || !baseDir.endsWith("/")) {
			baseDir += "/";
		}

		if (dir.startsWith("\\") || dir.startsWith("/")) {
			dir = dir.substring(1);
		}
		String intermediate = baseDir + dir;
		String ret = intermediate.replace('\\', '/');
		return ret;
	}

	public String getTransactionID(DeployItem[] deployItems)
			throws APIException, ConnectionException {

		String transactionId = null;
		CM localCm = this.session.createCM();
		ServiceTimeWatcher serviceTimeWatcher = new ServiceTimeWatcher();
		try {
			if (daLog.isDebugTraceable()) {
				this.daLog.traceDebug("going to generate Session id");
			}
			transactionId = localCm.generateSessionId();
			if (daLog.isDebugTraceable()) {
				this.daLog.traceDebug("got Session id=[{0}],time:[{1}].[{2}]",
						new Object[] { transactionId,
								serviceTimeWatcher.getElapsedTimeAsString(),
								this.deployerIdInfo });
			}
		} catch (DCNotAvailableException dcNotAvailableExc) {
			throw new ServiceNotAvailableException(this.daLog.getLocation(),
					APIExceptionConstants.DC_NOT_OPERATIONAL_YET, new Object[0]);
		} catch (CMException cme) {
			this.daLog.logError("ASJ.dpl_api.001228",
					"Exception on creating transaction session.cause=[{0}]",
					new Object[] { cme.getMessage() });
			throw new ValidationException(null, deployItems, this.daLog
					.getLocation(), APIExceptionConstants.DC_CM_EXCEPTION,
					new String[] { DAUtils.getThrowableClassName(cme),
							cme.getMessage() }, cme);
		}
		return transactionId;
	}

	public com.sap.engine.services.dc.cm.undeploy.GenericUndeployItem[] createRemoteUndeployItems(
			UndeployFactory undeployFactory, UndeployItem[] undeployItems)
			throws UndeploymentException {
		ServiceTimeWatcher serviceTimeWatcher = new ServiceTimeWatcher();
		if (daLog.isDebugTraceable()) {
			this.daLog.traceDebug(
					"*  createRemoteUndeployItems: begin,timerId=[{0}]",
					new Object[] { new Long(serviceTimeWatcher.getId()) });
		}
		try {
			UndeployItem nextItem;
			
			Collection sdaUndeployItems = new ArrayList();
			Collection scaUndeployItems = new ArrayList();

			for (int i = 0; i < undeployItems.length; i++) {
				nextItem = undeployItems[i];
				if (nextItem instanceof SdaUndeployItem) {
					sdaUndeployItems.add(undeployFactory
							.createUndeployItem(nextItem.getName(), nextItem
									.getVendor(), nextItem.getLocation(),
									nextItem.getVersion()));
				} else if (nextItem instanceof ScaUndeployItem) {
					scaUndeployItems.add(undeployFactory
							.createScaUndeployItem(nextItem.getName(), nextItem
									.getVendor(), nextItem.getLocation(),
									nextItem.getVersion()));
				} else {
					throw new IllegalArgumentException("Unknown type: "
							+ nextItem.getClass() + " for item : " + nextItem);
				}
			}
			
			
			com.sap.engine.services.dc.cm.undeploy.GenericUndeployItem[] remoteUndeployItems;
			if (scaUndeployItems.isEmpty()) {
				remoteUndeployItems = new com.sap.engine.services.dc.cm.undeploy.UndeployItem[sdaUndeployItems
						.size()];
				sdaUndeployItems.toArray(remoteUndeployItems);
			} else {
				sdaUndeployItems.addAll(scaUndeployItems);
				remoteUndeployItems = new com.sap.engine.services.dc.cm.undeploy.GenericUndeployItem[sdaUndeployItems
						.size()];
				sdaUndeployItems.toArray(remoteUndeployItems);
			}
			
			return remoteUndeployItems;
		} finally {
			if (daLog.isDebugTraceable()) {
				this.daLog.traceDebug(
						"*  createRemoteUndeployItems:end,total time=[{0}]",
						new Object[] { serviceTimeWatcher
								.getTotalElapsedTimeAsString() });
			}
		}
	}

	private DeployItemImpl mapDistinctItem(
			String prefix,
			Map uploadedDeployItemsMap,
			com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem nextDeploymentBatchItem) {
		String sduFilePath = nextDeploymentBatchItem.getSduFilePath();
		DeployItemImpl origin = (DeployItemImpl) uploadedDeployItemsMap
				.get(sduFilePath);
		if (origin == null) {
			sduFilePath = DAUtils.getFileName(sduFilePath);
			origin = (DeployItemImpl) uploadedDeployItemsMap.get(prefix
					+ sduFilePath);
		}
		return origin;
	}

	private Map internalMapSdus(String prefix,
			Collection remoteDeploymentBatchItems, Map uploadedDeployItemsMap) {
		Map containedDeployItemsMap = new HashMap();

		if ((remoteDeploymentBatchItems == null)
				|| (remoteDeploymentBatchItems.size() == 0)) {
			return containedDeployItemsMap;
		}

		DeployItemMapperVisitor deployItemMapperVisitor = new DeployItemMapperVisitor();

		for (Iterator iter = remoteDeploymentBatchItems.iterator(); iter
				.hasNext();) {
			com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem nextDeploymentBatchItem = (com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem) iter
					.next();

			// find the corresponding deploy item that was passed from the user
			DeployItemImpl origin = mapDistinctItem(prefix,
					uploadedDeployItemsMap, nextDeploymentBatchItem);

			if (origin != null) {
				DeployMapper.setCommonItemProps(this.daLog, origin,
						nextDeploymentBatchItem);

				// set SDU info
				com.sap.engine.services.dc.repo.Sdu remoteSdu = nextDeploymentBatchItem
						.getSdu();

				if (remoteSdu != null) {
					// check for SCA
					deployItemMapperVisitor.setCompositeDeploymentItem(
							nextDeploymentBatchItem, origin);
					remoteSdu.accept(deployItemMapperVisitor);

					Sdu apiSdu = deployItemMapperVisitor.getGeneratedSdu();
					if (nextDeploymentBatchItem instanceof CompositeDeploymentItem) {
						CompositeDeploymentItem composite = (CompositeDeploymentItem) nextDeploymentBatchItem;
						composite.getDeploymentItems();
						DeployItem[] contained = origin
								.getContainedDeployItems();
						for (int i = 0; i < contained.length; i++) {
							String key = contained[i].getSdu().getName() + "_"
									+ contained[i].getSdu().getVendor();
							containedDeployItemsMap.put(key, contained[i]);
						}
					}
					origin.setSdu(apiSdu);
					if (daLog.isDebugTraceable()) {
						this.daLog.traceDebug(
								"internalMapSdus:[{0}] sdu=[{1}]",
								new Object[] { this.deployerIdInfo, apiSdu });
					}
				} else {
					this.daLog.traceError("ASJ.dpl_api.001230",
							"The item: [{0}] does not have remote SDU.",
							new Object[] { nextDeploymentBatchItem });
				}
			} else {
				this.daLog
						.traceError(
								"ASJ.dpl_api.001231",
								"The item: [{0}] cannot be mapped to the list of deploy items passed on the client side.",
								new Object[] { nextDeploymentBatchItem });
			}
		}

		return containedDeployItemsMap;
	}

	private DeployItem[] mapSortedItems(String prefix, Collection sorted,
			Map uploadedDeployItemsMap, Map containedDeployItemsMap) {
		if (sorted == null) {
			return new DeployItem[0];
		}
		ArrayList arrDeployItems = new ArrayList();
		Object obj;
		DeployItemImpl origin;
		com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem nextDeploymentBatchItem;

		for (Iterator iterator = sorted.iterator(); iterator.hasNext();) {
			obj = iterator.next();
			if (obj instanceof com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem) {
				nextDeploymentBatchItem = (com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem) obj;

				// try to find the item in the uploaded items (i.e. either
				// standalone SDA or SCA )
				origin = mapDistinctItem(prefix, uploadedDeployItemsMap,
						nextDeploymentBatchItem);

				if (origin != null) {
					arrDeployItems.add(origin);
				} else {
					// if not found in the uploaded items map it must be an SDA
					// within SCA
					SduId sduId = nextDeploymentBatchItem.getSdu().getId();
					String key = sduId.getName() + "_" + sduId.getVendor();
					origin = (DeployItemImpl) containedDeployItemsMap.get(key);
					if (origin == null) {
						this.daLog
								.traceError(
										"ASJ.dpl_api.001232",
										"The item is contained in the sorted items, but is not contained in the deployment items map: [{0}]",
										new Object[] { nextDeploymentBatchItem });
					} else {
						arrDeployItems.add(origin);
					}
				}
			} else {
				this.daLog.traceError("ASJ.dpl_api.001233",
						"[{0}] is not a deployItem. [{1}]", new Object[] { obj,
								this.deployerIdInfo });
			}
		}

		DeployItem[] deployItems = new DeployItem[arrDeployItems.size()];
		arrDeployItems.toArray(deployItems);
		return deployItems;
	}

	public DeployItem[] mapItems(String transactionId,
			Collection deploymentBatchItems, Collection orderedBatchItems,
			Map uploadedDeployItemsMap) {
		return mapItems(transactionId, deploymentBatchItems, orderedBatchItems,
				uploadedDeployItemsMap, null);
	}

	public DeployItem[] mapItems(String transactionId,
			Collection deploymentBatchItems, Collection orderedBatchItems,
			Map uploadedDeployItemsMap, Integer batchNumber) {
		String prefix = transactionId
				+ File.separator
				+ ((batchNumber == null) ? ""
						: (batchNumber.toString() + File.separator));
		Map containedItems = internalMapSdus(prefix, deploymentBatchItems,
				uploadedDeployItemsMap);

		DeployItem[] sortedDeployItems = mapSortedItems(prefix,
				orderedBatchItems, uploadedDeployItemsMap, containedItems);

		return sortedDeployItems;
	}

	public UndeployItem[] buildOrderedList(UndeployItem[] undeployItems,
			Collection remoteOrderedList, StringBuffer buffer) {
		if (remoteOrderedList == null) {
			return new UndeployItem[0];
		}
		ArrayList tmpOrderedList = new ArrayList(remoteOrderedList.size());
		ArrayList tmpItemsList = new ArrayList(Arrays.asList(undeployItems));
		com.sap.engine.services.dc.cm.undeploy.GenericUndeployItem nextRemoteUndeployItem;
		UndeployItem nextUndeployItem;
		String checkedName;
		String checkedVendor;
		String location;
		String version;
		int index = 0;
		UndeployItemTypeMatchVisitor typeMatchVisitor = new UndeployItemTypeMatchVisitor();
		for (Iterator iter = remoteOrderedList.iterator(); iter.hasNext();) {
			nextRemoteUndeployItem = (com.sap.engine.services.dc.cm.undeploy.GenericUndeployItem) iter
					.next();
			for (Iterator internalIterator = tmpItemsList.iterator(); internalIterator
					.hasNext();) {
				nextUndeployItem = (UndeployItem) internalIterator.next();
				typeMatchVisitor.setClientUndeployItem(nextUndeployItem);
				nextRemoteUndeployItem.accept(typeMatchVisitor);
				if (typeMatchVisitor.isMatch()) {
					checkedName = ComponentPropsCorrector
							.getCorrected(nextUndeployItem.getName());
					checkedVendor = ComponentPropsCorrector
							.getCorrected(nextUndeployItem.getVendor());
					location = nextUndeployItem.getLocation();
					version = nextUndeployItem.getVersion();
					if (checkedVendor
							.equals(nextRemoteUndeployItem.getVendor())
							&& checkedName.equals(nextRemoteUndeployItem
									.getName())) {
						boolean matched = true;
						if ((location != null)
								&& (nextRemoteUndeployItem.getLocation() != null)) {
							if (!location.equals(nextRemoteUndeployItem
									.getLocation())) {
								matched = false;
							}
						}
						if (matched
								&& (version != null)
								&& (nextRemoteUndeployItem.getVersion() != null)) {
							if (!version.equals(nextRemoteUndeployItem
									.getVersion().getVersionAsString())) {
								matched = false;
							}
						}
						if (matched) {
							buildUndeployItemInfo(++index, buffer,
									nextUndeployItem);
							tmpOrderedList.add(nextUndeployItem);
							tmpItemsList.remove(nextUndeployItem);
							break;
						}
					}
				}
			}
		}
		UndeployItem[] orderedItems = new UndeployItem[tmpOrderedList.size()];
		return (UndeployItem[]) tmpOrderedList.toArray(orderedItems);
	}

	private static void buildUndeployItemInfo(int prefix, StringBuffer buffer,
			UndeployItem undeployItem) {
		buffer.append(DAConstants.EOL_SINGLE_INDENT).append(prefix).append(
				". name '").append(undeployItem.getName())
				.append("', vendor '").append(undeployItem.getVendor()).append(
						"', location '").append(undeployItem.getLocation())
				.append("' version '").append(undeployItem.getVersion())
				.append("', description '").append(
						undeployItem.getDescription()).append("'").append(
						DAConstants.EOL_INDENT).append("Undeploy status is '")
				.append(undeployItem.getUndeployItemStatus()).append("'");
	}

	public StringBuffer internalMapUndeployItems(UndeployItem[] undeployItems,
			Collection remoteResultUndeployItems) {
		Object obj;
		com.sap.engine.services.dc.cm.undeploy.GenericUndeployItem nextRemoteUndeployItem;
		StringBuffer buffer = new StringBuffer();
		String checkedName, checkedVendor;
		UndeployItemTypeMatchVisitor typeMatchVisitor = new UndeployItemTypeMatchVisitor();
		int count = 0;
		for (Iterator iter = remoteResultUndeployItems.iterator(); iter
				.hasNext();) {
			obj = iter.next();
			if (obj instanceof com.sap.engine.services.dc.cm.undeploy.GenericUndeployItem) {
				nextRemoteUndeployItem = (com.sap.engine.services.dc.cm.undeploy.GenericUndeployItem) obj;
				for (int i = 0; i < undeployItems.length; i++) {
					typeMatchVisitor.setClientUndeployItem(undeployItems[i]);
					nextRemoteUndeployItem.accept(typeMatchVisitor);
					if (typeMatchVisitor.isMatch()) {
						checkedName = ComponentPropsCorrector
								.getCorrected(undeployItems[i].getName());
						checkedVendor = ComponentPropsCorrector
								.getCorrected(undeployItems[i].getVendor());
						if (checkedVendor.equals(nextRemoteUndeployItem
								.getVendor())
								&& checkedName.equals(nextRemoteUndeployItem
										.getName())) {
							boolean matched = true;
							String location = undeployItems[i].getLocation();
							String version = undeployItems[i].getVersion();
							if (location != null
									&& location.length() > 0
									&& (!location.equals(nextRemoteUndeployItem
											.getLocation()) && nextRemoteUndeployItem
											.getLocation() != null)) {
								matched = false;
							}
							if (version != null && version.length() > 0) {
								com.sap.engine.services.dc.repo.Version remoteVersion = nextRemoteUndeployItem
										.getVersion();
								if (remoteVersion != null
										&& !version.equals(remoteVersion
												.getVersionAsString())) {
									matched = false;
								}
							}
							if (matched) {
								if (undeployItems[i] instanceof UndeployItemImpl) {
									final UndeployItemImpl tmp = (UndeployItemImpl) undeployItems[i];
									com.sap.engine.services.dc.repo.Version ver = nextRemoteUndeployItem
											.getVersion();
									tmp.setVersion((ver != null) ? ver
											.getVersionAsString() : null);
									tmp.setLocation(nextRemoteUndeployItem
											.getLocation());
									tmp.setDescription(nextRemoteUndeployItem
											.getDescription());
								}
								undeployItems[i]
										.setUndeployItemStatus(UndeployMapper
												.mapUndeployItemStatus(nextRemoteUndeployItem
														.getUndeployItemStatus()));
								buildUndeployItemInfo(++count, buffer,
										undeployItems[i]);
							}
						}
					}
				}
			} else {
				buffer.append("'").append(obj)
						.append("' is not a undeployItem");
			}
		}
		return buffer;
	}

	public StringBuffer buildItemsInfo(StringBuffer orderedItemsInfo,
			StringBuffer itemsInfo) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(DAConstants.EOL).append("----- Sorted Items -----")
				.append(orderedItemsInfo.toString()).append(DAConstants.EOL)
				.append(DAConstants.EOL).append("----- Undeploy Items -----")
				.append(itemsInfo.toString()).append(DAConstants.EOL_INDENT);
		return buffer;
	}

	public StringBuffer buildItemsInfo(DeployItem[] orderedDeployItems,
			DeployItem[] deployItems, boolean isTimeStatEnabled) {
		StringBuffer buffer = new StringBuffer();
		{// orderedDeployItems
			buffer.append(DAConstants.EOL).append("----- Sorted Items -----");
			StringBuffer sortedItemsBuffer = generateDeployItemsInfo(
					orderedDeployItems, true, isTimeStatEnabled);
			if (sortedItemsBuffer.length() > 0) {
				buffer.append(sortedItemsBuffer).append(DAConstants.EOL);
			}
		}
		{// deployItems
			buffer.append(DAConstants.EOL).append(
					"----- Deployment Items -----");
			StringBuffer deployItemsBuffer = generateDeployItemsInfo(
					deployItems, false, isTimeStatEnabled);
			if (deployItemsBuffer != null && deployItemsBuffer.length() > 0) {
				buffer.append(deployItemsBuffer).append(DAConstants.EOL);
			}
		}
		return buffer;
	}

	public StringBuffer generateDeployItemsInfo(DeployItem[] deployItems,
			boolean hideDetails, boolean isTimeStatEnabled) {

		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < deployItems.length; i++) {

			buffer.append(DAConstants.EOL_SINGLE_INDENT).append(i + 1)

			.append(". Client path '").append(deployItems[i].getArchive())
					.append("'")

					.append(DAConstants.EOL_INDENT)
					.append("Deploy status is '").append(
							deployItems[i].getDeployItemStatus()).append("'");

			// Description
			if (!hideDetails) {
				String description = deployItems[i].getDescription();
				if (description != null
						&& (description = description.trim()).length() != 0) {
					buffer.append(DAConstants.EOL_INDENT).append(
							"Description:'").append(description).append("'.");
				}
			}

			// Sdu info
			Sdu sdu = deployItems[i].getSdu();

			if (sdu == null) {

				if (daLog.isWarningTraceable()) {
					this.daLog.traceWarning("ASJ.dpl_api.001234",
							"The sdu of item [{0}] is null.",
							new Object[] { deployItems[i].getArchive() });
				}
				buffer.append(DAConstants.EOL_INDENT).append("Sdu : null");

			} else if (sdu instanceof Sca) {

				Sca sca = (Sca) sdu;
				buffer.append(DAConstants.EOL_INDENT).append("SCA : ").append(
						LogUtil.createScaInfo(sca));

			} else if (sdu instanceof Sda) {

				Sda sda = (Sda) sdu;
				buffer.append(DAConstants.EOL_INDENT).append("SDA : ").append(
						LogUtil.createSdaInfo(sda));
			}

			if (!hideDetails) {

				DeployItem[] containedDeployItems = deployItems[i]
						.getContainedDeployItems();

				String timestatsPrefix = "";
				if (containedDeployItems != null
						&& containedDeployItems.length > 0) {
					timestatsPrefix = "Composite ";
				}

				// time stats
				if (isTimeStatEnabled) {
					buffer.append(DAConstants.EOL);
					buffer.append(LogUtil
							.dumpTimeStatistics(DAConstants.INDENT,
									timestatsPrefix, deployItems[i]));
				}

				// contained DCs if any

				if (containedDeployItems != null
						&& containedDeployItems.length > 0) {

					buffer.append(DAConstants.EOL_INDENT);
					buffer.append("Contained DCs:");

					for (int j = 0; j < containedDeployItems.length; j++) {
						buffer.append(DAConstants.EOL_INDENT_INDENT).append(
								i + 1).append(".").append(j + 1).append(" : ")

						.append("Relative path '").append(
								containedDeployItems[j].getArchive()).append(
								"'").append(
								DAConstants.EOL_INDENT_INDENT_INDENT)

						.append("Deploy status is '").append(
								containedDeployItems[j].getDeployItemStatus())
								.append("'").append(
										DAConstants.EOL_INDENT_INDENT_INDENT);

						// Description
						String description = containedDeployItems[j]
								.getDescription();
						if (description != null
								&& (description = description.trim()).length() != 0) {
							buffer
									.append("Description:'")
									.append(description)
									.append("'.")
									.append(
											DAConstants.EOL_INDENT_INDENT_INDENT);
						}

						Sda sda = (Sda) containedDeployItems[j].getSdu();
						if (sda != null) {
							buffer.append("SDA : ");
							buffer.append(LogUtil.createSdaInfo(sda));
						} else {
							this.daLog.logError("ASJ.dpl_api.001235",
									"The sdu of item [{0}] is null.",
									new Object[] { containedDeployItems[j]
											.getArchive() });
							buffer.append("SDA : " + "null");
						}

						buffer.append(DAConstants.EOL);
						buffer.append(LogUtil.dumpTimeStatistics(
								DAConstants.INDENT + DAConstants.INDENT, "",
								deployItems[i]));

					}

				}

			}
		}
		return buffer;
	}

	public void logDeployInfo(DeployItem[] deployItems,
			DeploySettings deploySettings, boolean isDeploy, int batchID,
			int batches) {
		String actionWord = (isDeploy ? "deploy" : "deploy validation")
				+ (batchID == 0 ? "" : " batch (" + batchID + "/" + batches
						+ ")");

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < deployItems.length; i++) {
			if (deployItems[i] == null) {
				throw new NullPointerException(
						"[ERROR CODE DPL.DCAPI.1024] Deploy item #" + i
								+ " is null. The " + actionWord
								+ " could not be performed.");
			}
			sb.append(DAConstants.EOL);
			sb.append(deployItems[i].getArchive().getAbsolutePath());
		}

		if (daLog.isInfoTraceable()) {
			this.daLog.traceInfo("ASJ.dpl_api.001237",
					"[{0}] settings. [{1}][{2}]", new Object[] { actionWord,
							this.deployerIdInfo, deploySettings.toString() });
		}
	}

	public void logUndeployInfo(UndeployItem[] undeployItems,
			UndeploySettings undeploySettings, boolean isUndeploy, int batchID,
			int batches) {
		String actionWord = (isUndeploy ? "undeploy" : "undeploy validation")
				+ (batchID == 0 ? "" : " batch (" + batchID + "/" + batches
						+ ")");

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < undeployItems.length; i++) {
			if (undeployItems[i] == null) {
				throw new NullPointerException(
						"[ERROR CODE DPL.DCAPI.1156] Undeploy item #"
								+ i
								+ " is null. The undeployment could not be performed.");
			}
			sb.append(DAConstants.EOL);
			sb.append(undeployItems[i].toString());
		}

		if (daLog.isInfoTraceable()) {
			this.daLog.traceInfo("ASJ.dpl_api.001240",
					"[{0}] settings. [{1}][{2}]", new Object[] { actionWord,
							this.deployerIdInfo, undeploySettings.toString() });
		}
	}

	public void logValidateInfo(Batch[] batchList) {
		for (int i = 0; i < batchList.length; ++i) {
			if (batchList[i] == null) {
				throw new NullPointerException(
						"[ERROR CODE DPL.DCAPI.1171] Validation batch #"
								+ i
								+ " is null. The validation could not be performed.");
			}
			String batchType = (batchList[i] instanceof DeployBatch) ? "Deployment"
					: "Undeployment";
			if (batchList[i] instanceof DeployBatch) {
				DeployBatch deployBatch = ((DeployBatch) batchList[i]);
				if (deployBatch.getDeploySettings() == null) {
					throw new NullPointerException(
							"[ERROR CODE DPL.DCAPI.1172] Deploy settings in validation batch #"
									+ i
									+ " is null. The validation could not be performed.");
				}
				logDeployInfo(deployBatch.getDeployItems(), deployBatch
						.getDeploySettings(), false, i + 1, batchList.length);
			} else if (batchList[i] instanceof UndeployBatch) {
				UndeployBatch undeployBatch = ((UndeployBatch) batchList[i]);
				if (undeployBatch.getUndeploySettings() == null) {
					throw new NullPointerException(
							"[ERROR CODE DPL.DCAPI.1173] Undeploy settings in validation batch #"
									+ i
									+ " is null. The validation could not be performed.");
				}
				logUndeployInfo(undeployBatch.getUndeployItems(), undeployBatch
						.getUndeploySettings(), false, i + 1, batchList.length);
			}
		}
	}

	public StringBuffer buildValidationInfo(ValidationResultSet validationResult) {
		final StringBuffer sb = new StringBuffer();
		final ValidationResult[] validationResults = validationResult
				.getBatchResults();
		for (int i = 0; i < validationResults.length; i++) {
			sb.append(DAConstants.EOL);
			if (validationResults[i] instanceof DeployValidationResult) {
				sb.append("----- Deploy Validation Result (" + (i + 1) + "/"
						+ validationResults.length + ") -----");
				final DeployValidationResult dvr = (DeployValidationResult) validationResults[i];
				{
					sb.append(DAConstants.EOL_SINGLE_INDENT);
					sb.append("--- Sorted Items ---");
					buildValidationInfo(sb, dvr.getSortedDeploymentBatchItems());
				}
				{
					sb.append(DAConstants.EOL_SINGLE_INDENT);
					sb.append("--- Deploy Items ---");
					buildValidationInfo(sb, dvr.getDeploymentBatchItems());
				}
			} else if (validationResults[i] instanceof UndeployValidationResult) {
				sb.append("----- Undeploy Validation Result (" + (i + 1) + "/"
						+ validationResults.length + ") -----");
				final UndeployValidationResult uvr = (UndeployValidationResult) validationResults[i];
				{
					sb.append(DAConstants.EOL_SINGLE_INDENT);
					sb.append("--- Sorted Items ---");
					buildValidationInfo(sb, uvr.getSortedUndeploymentItems());
				}
				{
					sb.append(DAConstants.EOL_SINGLE_INDENT);
					sb.append("--- Undeploy Items ---");
					buildValidationInfo(sb, uvr.getUndeploymentItems());
				}
			} else {
				throw new IllegalStateException(
						"Report a message in BC-JAS-DPL to fix this exception.");
			}
		}
		return sb;
	}

	private void buildValidationInfo(StringBuffer sb, Object[] objs) {
		for (int j = 0; j < objs.length; j++) {
			sb.append(DAConstants.EOL_INDENT_INDENT);
			sb.append(j + 1);
			sb.append(". ");
			sb.append(objs[j]);
		}
	}

}
