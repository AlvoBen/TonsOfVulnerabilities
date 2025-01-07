/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * Created on Feb 27, 2006
 */
package com.sap.engine.services.dc.api.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import com.sap.engine.services.dc.api.APIException;
import com.sap.engine.services.dc.api.AuthenticationException;
import com.sap.engine.services.dc.api.Client;
import com.sap.engine.services.dc.api.ClientFactory;
import com.sap.engine.services.dc.api.ComponentManager;
import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.ErrorStrategy;
import com.sap.engine.services.dc.api.ErrorStrategyAction;
import com.sap.engine.services.dc.api.ServiceNotAvailableException;
import com.sap.engine.services.dc.api.archive_mng.ArchiveManager;
import com.sap.engine.services.dc.api.archive_mng.ArchiveNotFoundException;
import com.sap.engine.services.dc.api.archive_mng.DownloadingException;
import com.sap.engine.services.dc.api.deploy.ComponentVersionHandlingRule;
import com.sap.engine.services.dc.api.deploy.DeployException;
import com.sap.engine.services.dc.api.deploy.DeployItem;
import com.sap.engine.services.dc.api.deploy.DeployProcessor;
import com.sap.engine.services.dc.api.deploy.DeployResult;
import com.sap.engine.services.dc.api.deploy.DeployResultNotFoundException;
import com.sap.engine.services.dc.api.deploy.TransportException;
import com.sap.engine.services.dc.api.deploy.ValidationResult;
import com.sap.engine.services.dc.api.event.ClusterEvent;
import com.sap.engine.services.dc.api.event.ClusterListener;
import com.sap.engine.services.dc.api.event.DeploymentEvent;
import com.sap.engine.services.dc.api.event.DeploymentListener;
import com.sap.engine.services.dc.api.event.EventMode;
import com.sap.engine.services.dc.api.event.ListenerMode;
import com.sap.engine.services.dc.api.event.UndeploymentEvent;
import com.sap.engine.services.dc.api.event.UndeploymentListener;
import com.sap.engine.services.dc.api.explorer.RepositoryExplorer;
import com.sap.engine.services.dc.api.explorer.RepositoryExplorerException;
import com.sap.engine.services.dc.api.explorer.RepositoryExplorerFactory;
import com.sap.engine.services.dc.api.lcm.LCMStatus;
import com.sap.engine.services.dc.api.lcm.LifeCycleManager;
import com.sap.engine.services.dc.api.model.ModelFactory;
import com.sap.engine.services.dc.api.model.Sca;
import com.sap.engine.services.dc.api.model.Sda;
import com.sap.engine.services.dc.api.model.SdaId;
import com.sap.engine.services.dc.api.model.Sdu;
import com.sap.engine.services.dc.api.params.Param;
import com.sap.engine.services.dc.api.params.ParamsProcessor;
import com.sap.engine.services.dc.api.selfcheck.SelfChecker;
import com.sap.engine.services.dc.api.selfcheck.SelfCheckerException;
import com.sap.engine.services.dc.api.selfcheck.SelfCheckerResult;
import com.sap.engine.services.dc.api.undeploy.UndeployException;
import com.sap.engine.services.dc.api.undeploy.UndeployItem;
import com.sap.engine.services.dc.api.undeploy.UndeployProcessor;
import com.sap.engine.services.dc.api.undeploy.UndeployResult;
import com.sap.engine.services.dc.api.undeploy.UndeployResultNotFoundException;
import com.sap.engine.services.dc.api.undeploy.UndeploymentStrategy;
import com.sap.engine.services.dc.api.util.exception.APIExceptionConstants;

public class DARunner {
	// session timeout 1800000 ms
	private Client client;
	private ComponentManager componentManager = null;

	private String host, user;
	// private String password;
	private int port;
	private final DALog daLog;

	public DARunner(DALog daLog) {
		this.daLog = daLog;
		// nothing fancy
	}

	public void connect(final String aHost, final int aPort,
			final String aUser, final String aPassword)
			throws AuthenticationException, ConnectionException {
		this.client = ClientFactory.getInstance().createClient(this.daLog,
				aHost, aPort, aUser, aPassword);
		this.componentManager = this.client.getComponentManager();

		this.host = aHost;
		this.port = aPort;
		this.user = aUser;
		// this.password = aPassword;
		System.out.println("connected to " + this.host + ":" + this.port
				+ " with user " + this.user);
	}

	public void validate(String[] archiveLocations) throws TransportException,
			DeployException, APIException, ConnectionException {
		DeployProcessor deployProcessor = this.componentManager
				.getDeployProcessor();
		DeployItem deployItem;
		ArrayList deployItemsArr = new ArrayList();
		File tmpFile;
		for (int i = 0; i < archiveLocations.length; i++) {
			deployItem = deployProcessor.createDeployItem(archiveLocations[i]);
			tmpFile = deployItem.getArchive();
			if (tmpFile == null || !tmpFile.exists()) {
				throw new DeployException(null, (DeployItem[]) deployItemsArr
						.toArray(new DeployItem[0]), this.client.getLog()
						.getLocation(), APIExceptionConstants.UNFORMATED,
						new String[] { "file does not exists" });
			}
			deployItemsArr.add(deployItem);
		}
		if (deployItemsArr.isEmpty()) {
			throw new DeployException(null, new DeployItem[0], this.client
					.getLog().getLocation(), APIExceptionConstants.UNFORMATED,
					new String[] { "no files available" });
		}
		deployProcessor.setErrorStrategy(ErrorStrategyAction.DEPLOYMENT_ACTION,
				ErrorStrategy.ON_ERROR_SKIP_DEPENDING);
		deployProcessor.setErrorStrategy(
				ErrorStrategyAction.PREREQUISITES_CHECK_ACTION,
				ErrorStrategy.ON_ERROR_SKIP_DEPENDING);
		deployProcessor.setErrorStrategy(
				ErrorStrategyAction.UNDEPLOYMENT_ACTION,
				ErrorStrategy.ON_ERROR_SKIP_DEPENDING);
		deployProcessor
				.setComponentVersionHandlingRule(ComponentVersionHandlingRule.UPDATE_ALL_VERSIONS);

		// BatchFilterFactory batchFilterFactory =
		// m_componentManager.getBatchFilterFactory();
		// BatchFilter batchFilter =
		// batchFilterFactory.createSoftwareTypeBatchFilter("FS");

		// deployProcessor.addBatchFilter( batchFilter );

		DeployItem[] deployItems = new DeployItem[deployItemsArr.size()];
		deployItems = (DeployItem[]) deployItemsArr.toArray(deployItems);
		ValidationResult validationResult = deployProcessor
				.validate(deployItems);

		System.out.println("\n\n\n");
		System.out
				.println("=====================================================");
		System.out.println("\n\n\n");
		for (int i = 0; i < deployItems.length; i++) {
			System.out.println("item #" + i);
			System.out.println("" + deployItems[i]);
			System.out.println("####################################");
			// System.out.println("result:"+deployItems[i].getArchive()+",
			//version="+deployItems[i].getDeployItemResult().getVersionStatus()+"
			// ,
			// status="+deployItems[i].getDeployItemResult().getDeployStatus()
			// );
		}

		DeployItem[] sortedItems = validationResult
				.getSortedDeploymentBatchItems();
		System.out.println("\n\nSORTED\n\n");
		Sdu nextSdu;
		for (int i = 0; i < sortedItems.length; i++) {
			System.out.println("item #" + i);
			System.out.println("" + sortedItems[i].getSdu().getName());
			nextSdu = sortedItems[i].getSdu();
			if (nextSdu instanceof Sda) {
				System.out.println("\t\t" + ((Sda) nextSdu).getDependencies());
			} else if (nextSdu instanceof Sca) {
				System.out.println("\t\t" + ((Sca) nextSdu).getSdaIds());
			}
			System.out.println("####################################");
		}

		System.out.println("Total validationResult=" + validationResult);
	}

	public void deploy(String[] archiveLocations)
			throws DeployResultNotFoundException, DeployException,
			APIException, ConnectionException {
		DeployProcessor deployProcessor = this.componentManager
				.getDeployProcessor();

		DeploymentListener deploymentListener1 = new DeploymentListener() {
			public void deploymentPerformed(DeploymentEvent event) {
				System.out.println(event.getDeploymentEventAction() + "\titem="
						+ event.getDeployItem() + "]");
			}
		};
		/*
		 * DeploymentListener deploymentListener2 = new DeploymentListener(){
		 * public void deploymentPerformed(DeploymentEvent event){
		 * System.out.println
		 * ("2,[action="+event.getDeploymentEventAction()+",item="
		 * +event.getDeployItem()+"]"); } };
		 */
		ClusterListener clusterListener = new ClusterListener() {

			public void clusterRestartTriggered(ClusterEvent event) {
				System.out.println("Cluster event:" + event);
			}
		};
		deployProcessor.addDeploymentListener(deploymentListener1,
				ListenerMode.LOCAL, EventMode.SYNCHRONOUS);

		// deployProcessor.addDeploymentListener(deploymentListener2,
		// ListenerMode.GLOBAL, EventMode.SYNCHRONOUS );
		deployProcessor.addClusterListener(clusterListener, ListenerMode.LOCAL,
				EventMode.SYNCHRONOUS);

		DeployItem deployItem;
		ArrayList deployItemsArr = new ArrayList();
		File tmpFile;
		ErrorStrategy deploymentAction = null;
		ErrorStrategy prerequisitesCheckAction = null;
		ComponentVersionHandlingRule updateVersions = null;
		for (int i = 0; i < archiveLocations.length; i++) {
			if (archiveLocations[i].equalsIgnoreCase("help")) {
				System.out
						.println(getClass().getName()
								+ " user:pass@host:port deploy"
								+ " [help] [\"deploymentAction=OnErrorSkipDepending|OnErrorStop\"]"
								+ " [\"prerequisitesCheckAction=OnErrorSkipDepending|OnErrorStop\"]"
								+ " [\"updateVersions=UpdateAllVersions|UpdateSameAndLowerVersionsOnly|UpdateLowerVersionsOnly\"]"
								+ " [\"sda1\"] ... [\"sdaN\"]");
				return;
			} else if (archiveLocations[i].startsWith("deploymentAction=")) {
				String error = archiveLocations[i]
						.substring("deploymentAction=".length());
				if (ErrorStrategy.ON_ERROR_SKIP_DEPENDING.getName()
						.equalsIgnoreCase(error)) {
					deploymentAction = ErrorStrategy.ON_ERROR_SKIP_DEPENDING;
				} else if (ErrorStrategy.ON_ERROR_STOP.getName()
						.equalsIgnoreCase(error)) {
					deploymentAction = ErrorStrategy.ON_ERROR_STOP;
				}
				continue;
			} else if (archiveLocations[i]
					.startsWith("prerequisitesCheckAction=")) {
				String error = archiveLocations[i]
						.substring("prerequisitesCheckAction=".length());
				if (ErrorStrategy.ON_ERROR_SKIP_DEPENDING.getName()
						.equalsIgnoreCase(error)) {
					prerequisitesCheckAction = ErrorStrategy.ON_ERROR_SKIP_DEPENDING;
				} else if (ErrorStrategy.ON_ERROR_STOP.getName()
						.equalsIgnoreCase(error)) {
					prerequisitesCheckAction = ErrorStrategy.ON_ERROR_STOP;
				}
				continue;
			} else if (archiveLocations[i].startsWith("updateVersions=")) {
				String version = archiveLocations[i]
						.substring("updateVersions=".length());
				if (ComponentVersionHandlingRule.UPDATE_ALL_VERSIONS.getName()
						.equalsIgnoreCase(version)) {
					updateVersions = ComponentVersionHandlingRule.UPDATE_ALL_VERSIONS;
					continue;
				} else if (ComponentVersionHandlingRule.UPDATE_LOWER_VERSIONS_ONLY
						.getName().equalsIgnoreCase(version)) {
					updateVersions = ComponentVersionHandlingRule.UPDATE_LOWER_VERSIONS_ONLY;
					continue;
				} else if (ComponentVersionHandlingRule.UPDATE_SAME_AND_LOWER_VERSIONS_ONLY
						.getName().equalsIgnoreCase(version)) {
					updateVersions = ComponentVersionHandlingRule.UPDATE_SAME_AND_LOWER_VERSIONS_ONLY;
					continue;
				} else if (ComponentVersionHandlingRule.UPDATE_LOWER_OR_CHANGED_VERSIONS_ONLY
						.getName().equalsIgnoreCase(version)) {
					updateVersions = ComponentVersionHandlingRule.UPDATE_LOWER_OR_CHANGED_VERSIONS_ONLY;
					continue;
				}
			}
			deployItem = deployProcessor.createDeployItem(archiveLocations[i]);
			tmpFile = deployItem.getArchive();
			if (tmpFile == null || !tmpFile.exists()) {
				throw new DeployException(null, (DeployItem[]) deployItemsArr
						.toArray(new DeployItem[0]), this.client.getLog()
						.getLocation(), APIExceptionConstants.UNFORMATED,
						new String[] { "File does not exist:file " + tmpFile });
			}
			deployItemsArr.add(deployItem);
		}
		if (deployItemsArr.isEmpty()) {
			throw new DeployException(null, new DeployItem[0], this.client
					.getLog().getLocation(), APIExceptionConstants.UNFORMATED,
					new String[] { "no files available" });
		}
		if (deploymentAction == null) {
			deploymentAction = ErrorStrategy.ON_ERROR_SKIP_DEPENDING;
		}
		if (prerequisitesCheckAction == null) {
			prerequisitesCheckAction = ErrorStrategy.ON_ERROR_SKIP_DEPENDING;
		}
		if (updateVersions == null) {
			updateVersions = ComponentVersionHandlingRule.UPDATE_ALL_VERSIONS;
		}
		System.out.println("'" + ErrorStrategyAction.DEPLOYMENT_ACTION
				+ "' is '" + deploymentAction + "'");
		System.out.println("'" + ErrorStrategyAction.PREREQUISITES_CHECK_ACTION
				+ "' is '" + prerequisitesCheckAction + "'");
		System.out.println("updateVersions are '" + updateVersions + "'");
		System.out.println("list ot Items to deploy:" + deployItemsArr);
		deployProcessor.setErrorStrategy(ErrorStrategyAction.DEPLOYMENT_ACTION,
				deploymentAction);
		deployProcessor.setErrorStrategy(
				ErrorStrategyAction.PREREQUISITES_CHECK_ACTION,
				prerequisitesCheckAction);
		deployProcessor.setComponentVersionHandlingRule(updateVersions);

		// BatchFilterFactory batchFilterFactory =
		// m_componentManager.getBatchFilterFactory();
		// BatchFilter batchFilter =
		// batchFilterFactory.createSoftwareTypeBatchFilter("FS");

		// deployProcessor.addBatchFilter( batchFilter );

		DeployItem[] deployItems = new DeployItem[deployItemsArr.size()];
		deployItems = (DeployItem[]) deployItemsArr.toArray(deployItems);
		DeployResult deployResult = null;
		try {
			deployResult = deployProcessor.deploy(deployItems);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int i = 0; i < deployItems.length; i++) {
			System.out.println("item #" + (i + 1));
			System.out.println("" + deployItems[i]);
			DeployItem[] contained = deployItems[i].getContainedDeployItems();
			if (contained != null) {
				for (int j = 0; j < contained.length; j++) {
					System.out.println("\t" + (i + 1) + "." + (j + 1) + ":"
							+ contained[j]);
					System.out.println("\t\t------------");
				}
			}
			System.out.println("####################################");
			// System.out.println("result:"+deployItems[i].getArchive()+",
			//version="+deployItems[i].getDeployItemResult().getVersionStatus()+"
			// ,
			// status="+deployItems[i].getDeployItemResult().getDeployStatus()
			// );
		}
		System.out.println("Total deployResult=" + deployResult);

		// deploy again
		/*
		 * deployProcessor.removeDeploymentListener( deploymentListener1 );
		 * 
		 * deployItems = new DeployItem[deployItemsArr.size()]; deployItems =
		 * (DeployItem[]) deployItemsArr.toArray(deployItems); deployResult =
		 * null; try{ deployResult = deployProcessor.deploy(deployItems);
		 * }catch(Exception e){ e.printStackTrace(); }
		 * 
		 * for (int i = 0; i < deployItems.length; i++) {
		 * System.out.println("item #" + i); System.out.println("" +
		 * deployItems[i]);
		 * System.out.println("####################################"); }
		 * System.out.println("Total deployResult=" + deployResult); //
		 */
	}

	public void getDeployResult(String transactionId)
			throws ConnectionException, DeployException,
			ServiceNotAvailableException, IOException {
		if (transactionId == null || transactionId.trim().length() == 0) {
			System.out.print("Enter transactionId:");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					System.in));
			transactionId = in.readLine();
		}
		DeployProcessor deployProcessor = this.componentManager
				.getDeployProcessor();
		DeployResult deployResult = deployProcessor
				.getDeployResultById(transactionId);
		System.out.println("DeployResult : " + deployResult);
	}

	public void getOfflineDeployTransactionIDs() throws ConnectionException,
			DeployException, ServiceNotAvailableException {
		DeployProcessor deployProcessor = this.componentManager
				.getDeployProcessor();
		String[] availableTransactionIDS = deployProcessor
				.getOfflineDeployTransactionIDs();
		System.out.println(Arrays.asList(availableTransactionIDS));
	}

	public void getOfflineUneployTransactionIDs() throws ConnectionException,
			ServiceNotAvailableException, UndeployException {
		UndeployProcessor undeployProcessor = this.componentManager
				.getUndeployProcessor();
		String[] availableTransactionIDS = undeployProcessor
				.getOfflineUndeployTransactionIDs();
		System.out.println(Arrays.asList(availableTransactionIDS));
	}

	public void undeploy(String[] ids) throws ConnectionException,
			UndeployResultNotFoundException, UndeployException {
		if (ids == null || ids.length == 0) {
			System.out.println("Nothing to undeploy");
			return;
		}
		UndeployProcessor undeployProcessor = this.componentManager
				.getUndeployProcessor();

		UndeploymentListener listener1 = new UndeploymentListener() {

			public void undeploymentPerformed(UndeploymentEvent event) {
				System.out.println("[1]undeploymentPerformed:" + event);
			}
		};

		UndeploymentListener listener2 = new UndeploymentListener() {

			public void undeploymentPerformed(UndeploymentEvent event) {
				System.out.println("[2]undeploymentPerformed:" + event);
			}
		};

		undeployProcessor.addUndeploymentListener(listener1,
				ListenerMode.LOCAL, EventMode.SYNCHRONOUS);
		undeployProcessor.addUndeploymentListener(listener2,
				ListenerMode.LOCAL, EventMode.SYNCHRONOUS);

		// undeployProcessor.removeUndeploymentListener( listener2 );

		ErrorStrategy undeploymentAction = null;
		ErrorStrategy prerequisitesCheckAction = null;
		UndeploymentStrategy undeploymentStrategy = null;
		String[] tmp;
		ArrayList arr = new ArrayList();
		for (int i = 0; i < ids.length; i++) {

			if (ids[i].equalsIgnoreCase("help")) {
				System.out
						.println(getClass().getName()
								+ " user:pass@host:port undeploy"
								+ " [help] [\"undeploymentAction=OnErrorSkipDepending|OnErrorStop\"]"
								+ " [\"prerequisitesCheckAction=OnErrorSkipDepending|OnErrorStop\"]"
								+ " [\"undeploymentStrategy=IfDependingStop|UndeployDepending\"]"
								+ " [\"name1:vendor1\"] ... [\"nameN:vendorN\"]");
				return;
			} else if (ids[i].startsWith("undeploymentAction=")) {
				String error = ids[i].substring("undeploymentAction=".length());
				if (ErrorStrategy.ON_ERROR_SKIP_DEPENDING.getName()
						.equalsIgnoreCase(error)) {
					undeploymentAction = ErrorStrategy.ON_ERROR_SKIP_DEPENDING;
				} else if (ErrorStrategy.ON_ERROR_STOP.getName()
						.equalsIgnoreCase(error)) {
					undeploymentAction = ErrorStrategy.ON_ERROR_STOP;
				}
				continue;
			} else if (ids[i].startsWith("prerequisitesCheckAction=")) {
				String error = ids[i].substring("prerequisitesCheckAction="
						.length());
				if (ErrorStrategy.ON_ERROR_SKIP_DEPENDING.getName()
						.equalsIgnoreCase(error)) {
					prerequisitesCheckAction = ErrorStrategy.ON_ERROR_SKIP_DEPENDING;
				} else if (ErrorStrategy.ON_ERROR_STOP.getName()
						.equalsIgnoreCase(error)) {
					prerequisitesCheckAction = ErrorStrategy.ON_ERROR_STOP;
				}
				continue;
			} else if (ids[i].startsWith("undeploymentStrategy=")) {
				String strategy = ids[i].substring("undeploymentStrategy="
						.length());
				if (UndeploymentStrategy.IF_DEPENDING_STOP.getName()
						.equalsIgnoreCase(strategy)) {
					undeploymentStrategy = UndeploymentStrategy.IF_DEPENDING_STOP;
				} else if (UndeploymentStrategy.UNDEPLOY_DEPENDING.getName()
						.equalsIgnoreCase(strategy)) {
					undeploymentStrategy = UndeploymentStrategy.UNDEPLOY_DEPENDING;
				}
				continue;
			}
			tmp = ids[i].split(":");
			if (tmp.length != 2) {
				System.out
						.println("cant add '" + ids[i] + "' to undeploy list");
				continue;
			}
			arr.add(undeployProcessor.createUndeployItem(tmp[0], tmp[1]));
		}

		if (arr != null && arr.size() > 0) {
			if (undeploymentAction == null) {
				undeploymentAction = ErrorStrategy.ON_ERROR_SKIP_DEPENDING;
			}
			if (prerequisitesCheckAction == null) {
				prerequisitesCheckAction = ErrorStrategy.ON_ERROR_SKIP_DEPENDING;
			}
			if (undeploymentStrategy == null) {
				undeploymentStrategy = UndeploymentStrategy.IF_DEPENDING_STOP;
			}

			System.out.println("'" + ErrorStrategyAction.UNDEPLOYMENT_ACTION
					+ "' is '" + undeploymentAction + "'");
			System.out.println("'"
					+ ErrorStrategyAction.PREREQUISITES_CHECK_ACTION + "' is '"
					+ prerequisitesCheckAction + "'");
			System.out.println("undeploymentStrategy is '"
					+ undeploymentStrategy + "'");
			System.out.println("undeployment list=" + arr);
			undeployProcessor
					.setErrorStrategy(ErrorStrategyAction.UNDEPLOYMENT_ACTION,
							undeploymentAction);
			undeployProcessor.setErrorStrategy(
					ErrorStrategyAction.PREREQUISITES_CHECK_ACTION,
					prerequisitesCheckAction);
			undeployProcessor.setUndeploymentStrategy(undeploymentStrategy);

			UndeployItem[] undeployItems = new UndeployItem[arr.size()];
			undeployItems = (UndeployItem[]) arr.toArray(undeployItems);
			UndeployResult undeployResult = null;
			try {
				undeployResult = undeployProcessor.undeploy(undeployItems);
			} catch (Exception e) {
				e.printStackTrace();
			}
			for (int i = 0; i < undeployItems.length; i++) {
				System.out.println("item #" + i);
				System.out.println("" + undeployItems[i]);
				System.out.println("####################################");
			}
			System.out.println("\nundeployResult=" + undeployResult);
		}
	}

	public void getAllParams() {
		try {
			ParamsProcessor paramsProcessor = this.componentManager
					.getParamsProcessor();
			Param[] allParams = paramsProcessor.getAllParams();
			System.out.println("allParams:");
			if (allParams != null) {
				for (int i = 0; i < allParams.length; i++) {
					System.out.println(i + " -> " + allParams[i]);
				}
			} else {
				System.out.println("null");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getParam(String name) {
		try {
			ParamsProcessor paramsProcessor = this.componentManager
					.getParamsProcessor();
			Param param = paramsProcessor.getParamByName(name);
			if (param != null) {
				System.out.println(param);
			} else {
				System.out.println("null");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addParam(String paramDesc, boolean isAdd) {
		try {
			String[] tmp = paramDesc.split(":");
			ParamsProcessor paramsProcessor = this.componentManager
					.getParamsProcessor();
			Param param = paramsProcessor.createParam(tmp[0], tmp[1]);
			if (isAdd) {
				paramsProcessor.addParam(param);
			} else {
				paramsProcessor.updateParam(param);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addParams(String[] paramDescs, boolean isAdd) {
		try {
			ParamsProcessor paramsProcessor = this.componentManager
					.getParamsProcessor();
			Param[] params = new Param[paramDescs.length];
			for (int i = 0; i < paramDescs.length; i++) {
				String[] tmp = paramDescs[i].split(":");
				params[i] = paramsProcessor.createParam(tmp[0], tmp[1]);
			}
			if (isAdd) {
				paramsProcessor.addParams(params);
			} else {
				paramsProcessor.updateParams(params);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeParam(String paramName) {
		try {
			ParamsProcessor paramsProcessor = this.componentManager
					.getParamsProcessor();
			Param param = paramsProcessor.createParam(paramName, "empty");
			paramsProcessor.removeParam(param);
		} catch (Exception e) {
			this.daLog.traceThrowable(e);
			e.printStackTrace();
		}
	}

	public void removeParams(String[] paramDescs) {
		try {
			ParamsProcessor paramsProcessor = this.componentManager
					.getParamsProcessor();
			Param[] params = new Param[paramDescs.length];
			for (int i = 0; i < paramDescs.length; i++) {
				params[i] = paramsProcessor.createParam(paramDescs[i], "empty");
			}
			paramsProcessor.removeParams(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void findSda(String name, String vendor)
			throws RepositoryExplorerException, ConnectionException {
		RepositoryExplorerFactory repositoryExplorerFactory = this.componentManager
				.getRepositoryExplorerFactory();
		RepositoryExplorer repositoryExplorer = repositoryExplorerFactory
				.createRepositoryExplorer();
		Sda sda = repositoryExplorer.findSda(name, vendor);
		System.out.println("SDA:" + sda);
		System.out.println("ScaId:" + (sda != null ? sda.getScaId() : null));
	}

	public void findSca(String name, String vendor)
			throws RepositoryExplorerException, ConnectionException {
		RepositoryExplorerFactory repositoryExplorerFactory = this.componentManager
				.getRepositoryExplorerFactory();
		RepositoryExplorer repositoryExplorer = repositoryExplorerFactory
				.createRepositoryExplorer();
		Sca sca = repositoryExplorer.findSca(name, vendor);
		System.out.println("SCA:" + sca);
	}

	public void findAll() throws ConnectionException, APIException {
		RepositoryExplorerFactory repositoryExplorerFactory = this.componentManager
				.getRepositoryExplorerFactory();
		RepositoryExplorer repositoryExplorer = repositoryExplorerFactory
				.createRepositoryExplorer();
		Sdu[] allSdus = repositoryExplorer.findAll();
		ArrayList list = new ArrayList();
		if (allSdus != null) {
			ModelFactory modelFactory = this.componentManager.getModelFactory();
			for (int i = 0; i < allSdus.length; i++) {
				System.out.println("#" + i + " -> " + allSdus[i]);
				if (allSdus[i] instanceof Sda) {
					list.add(modelFactory.createSdaId(allSdus[i].getName(),
							allSdus[i].getVendor()));
				}
			}
			if (!list.isEmpty()) {
				System.out.println("Statuses:");
				SdaId[] sdaIds = new SdaId[list.size()];
				list.toArray(sdaIds);
				LifeCycleManager lifeCycleManager = this.client
						.getLifeCycleManager();
				LCMStatus[] lcmStatuses = lifeCycleManager
						.getLCMStatuses(sdaIds);
				for (int i = 0; i < lcmStatuses.length; i++) {
					if (LCMStatus.NOT_SUPPORTED.equals(lcmStatuses[i])) {
						continue;
					}

					if (LCMStatus.STARTED.equals(lcmStatuses[i])) {
						System.out.println(lcmStatuses[i] + " -> " + sdaIds[i]);
					} else {
						System.err.println(lcmStatuses[i] + " -> " + sdaIds[i]);
					}
				}
			}
		} else {
			System.out.println("kofti");
		}
	}

	public void downloadSduSources(String name, String vendor, String path)
			throws ArchiveNotFoundException, DownloadingException,
			ConnectionException, APIException {
		ArchiveManager archiveManager = this.componentManager
				.getArchiveManager();
		String realName = archiveManager.downloadSduSources(name, vendor, path);
		System.out.println("Downloaded to path:" + path);
		System.out.println("RealName:" + realName);
	}

	public void downloadSdaArchive(String name, String vendor, String path)
			throws ArchiveNotFoundException, DownloadingException,
			ConnectionException, APIException {
		ArchiveManager archiveManager = this.componentManager
				.getArchiveManager();
		String realName = archiveManager.downloadSdaArchive(name, vendor, path);
		System.out.println("Downloaded to path:" + path);
		System.out.println("RealName:" + realName);
	}

	public void performCheck() throws SelfCheckerException,
			ConnectionException, AuthenticationException {
		SelfChecker selfChecker = this.componentManager.getSelfChecker();
		long start = System.currentTimeMillis();
		SelfCheckerResult checkResult = selfChecker.doCheck();
		long end = System.currentTimeMillis();
		System.out.println("toString:" + checkResult.toString());

		System.out.println("status:" + checkResult.getStatus());
		System.out.println("result:" + checkResult.getDescription());
		System.out.println("time:" + (end - start) + "ms.");
	}

	public void close() throws ConnectionException {
		if (this.client != null) {
			this.client.close();
		}
	}

	private static void usage() {
		System.out.println("usage: user:password@hots:port deploy | undeploy "
				+ "| getDeployResult | getOfflineDeployTransactionIDs "
				+ "| getOfflineUndeployTransactionIDs");
		System.exit(42);
	}

	public static DALog.Logger getLogger() {
		return new DALog.Logger() {
			public void trace(int severity, String message) {
				System.out.println("Trace: " + message);
			}

			public void traceThrowable(String message, Throwable th) {
				System.out.println("traceThrowable: " + message);
				th.printStackTrace(System.out);
			}

			public void log(int severity, String message) {
				System.out.println("log: " + message);
			}

			public void logThrowable(String message, Throwable th) {
				System.out.println("logThrowable: " + message);
				th.printStackTrace(System.out);
			}

			public void flush() {
				System.out.println(" flush ");
				System.out.flush();
			}

			public void close() {
				System.out.println(" close ");
			}
		};
	}

	public static void execute(String[] args) {

		if (args.length >= 2) {
			String[] tmp1 = args[0].split("@");
			if (tmp1 == null || tmp1.length != 2) {
				usage();
			} else {
				String[] tmp2 = tmp1[0].split(":");
				int lastColumn = tmp1[1].lastIndexOf(':');
				if (lastColumn == -1) {
					usage();
				} else {
					String host = tmp1[1].substring(0, lastColumn);
					int port = -1;
					try {
						port = Integer.parseInt(tmp1[1]
								.substring(lastColumn + 1), 10);
					} catch (NumberFormatException nfe) {
						nfe.printStackTrace();
					}

					DALog daLog = DALog.getInstance();
					DARunner dasTest = new DARunner(daLog);
					try {
						dasTest.connect(host, port, tmp2[0], tmp2[1]);

						if ("validate".equalsIgnoreCase(args[1])) {
							String[] files = new String[args.length - 2];
							System.arraycopy(args, 2, files, 0, files.length);
							dasTest.validate(files);
						} else if ("deploy".equalsIgnoreCase(args[1])) {
							String[] files = new String[args.length - 2];
							System.arraycopy(args, 2, files, 0, files.length);
							dasTest.deploy(files);
						} else if ("undeploy".equalsIgnoreCase(args[1])) {
							String[] sdus = new String[args.length - 2];
							System.arraycopy(args, 2, sdus, 0, sdus.length);
							dasTest.undeploy(sdus);
						} else if ("allParams".equalsIgnoreCase(args[1])) {
							dasTest.getAllParams();
						} else if ("getParam".equalsIgnoreCase(args[1])) {
							dasTest.getParam(args[2]);
						} else if ("addParam".equalsIgnoreCase(args[1])) {
							dasTest.addParam(args[2], true);
						} else if ("addParams".equalsIgnoreCase(args[1])) {
							String[] params = new String[args.length - 2];
							System.arraycopy(args, 2, params, 0, params.length);
							dasTest.addParams(params, true);
						} else if ("updateParam".equalsIgnoreCase(args[1])) {
							dasTest.addParam(args[2], false);
						} else if ("updateParams".equalsIgnoreCase(args[1])) {
							String[] params = new String[args.length - 2];
							System.arraycopy(args, 2, params, 0, params.length);
							dasTest.addParams(params, false);
						} else if ("removeParam".equalsIgnoreCase(args[1])) {
							dasTest.removeParam(args[2]);
						} else if ("removeParams".equalsIgnoreCase(args[1])) {
							String[] params = new String[args.length - 2];
							System.arraycopy(args, 2, params, 0, params.length);
							dasTest.removeParams(params);
						} else if ("explorerFindAll".equalsIgnoreCase(args[1])) {
							dasTest.findAll();
						} else if ("findSda".equals(args[1])) {
							dasTest.findSda(args[2], args[3]);
						} else if ("findSca".equals(args[1])) {
							dasTest.findSca(args[2], args[3]);
						} else if ("selfCheck".equals(args[1])) {
							dasTest.performCheck();
						} else if ("downloadSduSources".equals(args[1])) {
							dasTest.downloadSduSources(args[2], args[3],
									args[4]);
						} else if ("downloadSdaArchive".equals(args[1])) {
							dasTest.downloadSdaArchive(args[2], args[3],
									args[4]);
						} else if ("getDeployResult".equals(args[1])) {
							dasTest.getDeployResult(args.length > 2 ? args[2]
									: null);
						} else if ("getOfflineDeployTransactionIDs"
								.equals(args[1])) {
							dasTest.getOfflineDeployTransactionIDs();
						} else if ("getOfflineUndeployTransactionIDs"
								.equals(args[1])) {
							dasTest.getOfflineUneployTransactionIDs();
						}
						dasTest.close();
					} catch (Exception e) {
						//daLog.logThrowable("Exception ,cause="+e.getMessage(),
						// e);
						e.printStackTrace();
					} finally {
						daLog.close();
					}
				}
			}
		} else {
			usage();
		}
	}

}
