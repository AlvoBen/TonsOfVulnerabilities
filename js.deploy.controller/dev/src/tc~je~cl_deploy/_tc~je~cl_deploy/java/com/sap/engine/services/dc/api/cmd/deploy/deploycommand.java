/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * Created on Sep 7, 2005
 */
package com.sap.engine.services.dc.api.cmd.deploy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import com.sap.engine.services.dc.api.APIException;
import com.sap.engine.services.dc.api.ComponentManager;
import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.ErrorStrategy;
import com.sap.engine.services.dc.api.ErrorStrategyAction;
import com.sap.engine.services.dc.api.cmd.AbstractCommand;
import com.sap.engine.services.dc.api.cmd.Command;
import com.sap.engine.services.dc.api.cmd.report.DeployReporter;
import com.sap.engine.services.dc.api.cmd.report.ReporterFactory;
import com.sap.engine.services.dc.api.cmd.util.CmdLogger;
import com.sap.engine.services.dc.api.deploy.AllItemsAlreadyDeployedValidaionException;
import com.sap.engine.services.dc.api.deploy.ComponentVersionHandlingRule;
import com.sap.engine.services.dc.api.deploy.DeployException;
import com.sap.engine.services.dc.api.deploy.DeployItem;
import com.sap.engine.services.dc.api.deploy.DeployItemStatus;
import com.sap.engine.services.dc.api.deploy.DeployProcessor;
import com.sap.engine.services.dc.api.deploy.DeployResult;
import com.sap.engine.services.dc.api.deploy.DeployResultNotFoundException;
import com.sap.engine.services.dc.api.deploy.DeployResultStatus;
import com.sap.engine.services.dc.api.deploy.DeployWorkflowStrategy;
import com.sap.engine.services.dc.api.deploy.EngineTimeoutException;
import com.sap.engine.services.dc.api.deploy.LifeCycleDeployStrategy;
import com.sap.engine.services.dc.api.deploy.TransportException;
import com.sap.engine.services.dc.api.deploy.ValidationException;
import com.sap.engine.services.dc.api.event.DeploymentEvent;
import com.sap.engine.services.dc.api.event.DeploymentListener;
import com.sap.engine.services.dc.api.event.EventMode;
import com.sap.engine.services.dc.api.event.ListenerMode;
import com.sap.engine.services.dc.api.filters.BatchFilterFactory;
import com.sap.engine.services.dc.api.lock_mng.AlreadyLockedException;
import com.sap.engine.services.dc.api.model.Dependency;
import com.sap.engine.services.dc.api.model.ModelFactory;
import com.sap.engine.services.dc.api.model.Sca;
import com.sap.engine.services.dc.api.model.ScaId;
import com.sap.engine.services.dc.api.model.Sda;
import com.sap.engine.services.dc.api.model.Sdu;
import com.sap.engine.services.dc.api.model.SoftwareType;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright (c) 2005, SAP-AG Date: Sep 7, 2005
 * 
 * @author Boris Savov(i030791)
 * @version 1.0
 * @since 7.1
 * 
 */

public class DeployCommand extends AbstractCommand {
	protected final ArrayList fileList = new ArrayList();

	protected long timeout = -1;

	protected ErrorStrategy errorStrategy = ErrorStrategy.ON_ERROR_STOP;

	protected ComponentVersionHandlingRule updateStrategy = ComponentVersionHandlingRule.UPDATE_ALL_VERSIONS;

	protected DeployWorkflowStrategy deployWorkflowStrategy = DeployWorkflowStrategy.NORMAL;

	protected LifeCycleDeployStrategy lifeCycleDeployStrategy = LifeCycleDeployStrategy.BULK;

	protected ArrayList softwareFilters;

	private int addFileToList(String filePath) {
		File file = new File(filePath);
		String canonicalPath = getCanonicalFilePath(file);
		if (!file.exists()) {
			addDescription("File '" + canonicalPath + " does not exist.", true);
			return Command.CODE_ERROR_OCCURRED;
		}
		if (!file.canRead()) {
			addDescription("File '" + canonicalPath + " cannot read.", true);
			return Command.CODE_ERROR_OCCURRED;
		}
		int ret = Command.CODE_SUCCESS;
		if (this.fileList.contains(canonicalPath)) {
			super
					.daLog()
					.logWarning(
							"ASJ.dpl_api.001262",
							"File [{0}] already exists in the list and will be skipped.",
							new Object[] { canonicalPath });
			ret = Command.CODE_SUCCESS_WITH_WARNINGS;
		} else {
			this.fileList.add(canonicalPath);
		}
		return ret;
	}

	private int collectFilesFromList(File listFile) {
		BufferedReader in = null;
		int totalRet = Command.CODE_SUCCESS;
		try {
			in = new BufferedReader(new FileReader(listFile));
			String nextLine;
			int ret;
			while ((nextLine = in.readLine()) != null) {
				ret = addFileToList(nextLine.trim());
				if (!isSuccess(ret)) {
					return ret;
				}
				if (ret == Command.CODE_SUCCESS_WITH_WARNINGS) {
					totalRet = Command.CODE_SUCCESS_WITH_WARNINGS;
				}
			}
		} catch (FileNotFoundException e) {
			addDescription("List file '" + getCanonicalFilePath(listFile)
					+ "' not found.", true);
			return Command.CODE_CRITICAL_ERROR;
		} catch (IOException e) {
			addDescription(
					"Error occurred while reading the content of List file '"
							+ getCanonicalFilePath(listFile) + "'. Reason: "
							+ e.getLocalizedMessage(), true);
			return Command.CODE_CRITICAL_ERROR;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e1) {
					// $JL-EXC$
				}
			}
		}
		return totalRet;
	}

	protected int performOperation() {
		DeployReporter reporter = ReporterFactory.getInstance()
				.createDeployReporter();
		try {
			ComponentManager componentManager = getClient()
					.getComponentManager();
			DeployProcessor deployer = componentManager.getDeployProcessor();

			this.fileList.trimToSize();
			DeployItem[] deployItems = new DeployItem[this.fileList.size()];
			this.daLog().logInfo("ASJ.dpl_api.001275", "Items to deploy:");
			int i = 0;
			for (Iterator iter = this.fileList.iterator(); iter.hasNext(); i++) {
				String element = (String) iter.next();
				deployItems[i] = deployer.createDeployItem(element);
				this.daLog().logInfo("ASJ.dpl_api.001276", "{0}",
						new Object[] { deployItems[i].toString() });
			}

			if (this.softwareFilters != null && this.softwareFilters.size() > 0) {
				BatchFilterFactory filterFactory = componentManager
						.getBatchFilterFactory();
				ModelFactory modelFactory = componentManager.getModelFactory();
				for (Iterator iter = this.softwareFilters.iterator(); iter
						.hasNext();) {
					String filter = (String) iter.next();
					SoftwareType softwareType = modelFactory
							.createSoftwareType(filter, filter);
					deployer.addBatchFilter(filterFactory
							.createSoftwareTypeBatchFilter(softwareType));
				}
			}

			deployer.setComponentVersionHandlingRule(this.updateStrategy);
			deployer.setDeployWorkflowStrategy(this.deployWorkflowStrategy);
			deployer.setErrorStrategy(
					ErrorStrategyAction.PREREQUISITES_CHECK_ACTION,
					this.errorStrategy);
			deployer.setErrorStrategy(ErrorStrategyAction.DEPLOYMENT_ACTION,
					this.errorStrategy);
			deployer.setLifeCycleDeployStrategy(this.lifeCycleDeployStrategy);

			deployer
					.addDeploymentListener(new CLToolDeploymentListener(super
							.getCmdLogger()), ListenerMode.LOCAL,
							EventMode.SYNCHRONOUS);

			deployer.addDeploymentListener(reporter.getDeploymentListener(),
					ListenerMode.LOCAL, EventMode.SYNCHRONOUS);

			if (this.timeout > 0) {
				deployer.setCustomServerTimeout(this.timeout);
			}

			DeployResult deployResult = null;

			deployResult = deployer.deploy(deployItems);

			/*
			 * pass results to the reporter; any exception thrown by the
			 * reporter should not affect the behaviour of the tool => log and
			 * forget
			 */
			try {
				reporter.processDeployResult(deployResult);
			} catch (Exception e) {
				daLog().logThrowable(e);
			}

			if (deployResult == null) {
				addDescription("Deploy result could not be received.", true);
				return Command.CODE_ERROR_OCCURRED;
			} else {
				DeployResultStatus deployResultStatus = deployResult
						.getDeployResultStatus();
				if (DeployResultStatus.SUCCESS.equals(deployResultStatus)) {
					return Command.CODE_SUCCESS;
				} else if (DeployResultStatus.WARNING
						.equals(deployResultStatus)
						|| DeployResultStatus.ERROR.equals(deployResultStatus)) {
					boolean isError = DeployResultStatus.ERROR
							.equals(deployResultStatus);
					if (deployResult != null) {
						dumpStatuses(isError, deployResult.getDeploymentItems());
					}
					String globalDesc = deployResult.getDescription();
					if (globalDesc != null && globalDesc.length() > 0) {
						addDescription(globalDesc, isError);
					}
					return isError ? Command.CODE_ERROR_OCCURRED
							: Command.CODE_SUCCESS_WITH_WARNINGS;
				} else {
					addDescription("Unknown result status '"
							+ deployResultStatus + "'", true);
					return Command.CODE_CRITICAL_ERROR;
				}
			}
		} catch (ConnectionException e) {
			addDescription("ConnectionException:" + e.getMessage(), true);
			super.daLog().logThrowable(e);
			return Command.CODE_ERROR_OCCURRED;
		} catch (AlreadyLockedException e) {
			addDescription(
					"DC is already locked. Probably other deployment is performing at the moment. Reason:"
							+ e.getLocalizedMessage(), true);
			super.daLog().logThrowable(e);
			return Command.CODE_ERROR_OCCURRED;
		} catch (DeployResultNotFoundException drnf) {
			addDescription("Deploy Result Not Found. Reason:"
					+ drnf.getLocalizedMessage(), true);
			super.daLog().logThrowable(drnf);
			return Command.CODE_ERROR_OCCURRED;
		} catch (TransportException e) {
			addDescription(
					"Some exception occurred while uploading the files to the server. Reason:"
							+ e.getLocalizedMessage(), true);
			super.daLog().logThrowable(e);
			return Command.CODE_ERROR_OCCURRED;
		} catch (AllItemsAlreadyDeployedValidaionException alreadyDeployedExc) {
			DeployItem[] items = alreadyDeployedExc.getDeploymentItems();
			dumpStatuses(true, items);
			String description = "AllItemsAlreadyDeployedValidaionException exception occurred. Reason:"
					+ alreadyDeployedExc.getLocalizedMessage();
			addDescription(description, true);
			super.daLog().logThrowable(alreadyDeployedExc);

			try {
				reporter.processDeployItems(alreadyDeployedExc
						.getOrderedDeploymentItems(), DeployResultStatus.ERROR,
						description);
			} catch (Exception e) {
				daLog().logThrowable(e);
			}

			return Command.CODE_ERROR_ALL_ITEMS_ALREADY_DEPLOYED;
		} catch (ValidationException ve) {
			DeployItem[] items = ve.getDeploymentItems();
			dumpStatuses(true, items);
			String description = "Validation exception occurred. Reason:"
					+ ve.getLocalizedMessage();
			addDescription(description, true);
			super.daLog().logThrowable(ve);

			try {
				reporter.processDeployItems(ve.getOrderedDeploymentItems(),
						DeployResultStatus.ERROR, description);
			} catch (Exception e) {
				daLog().logThrowable(e);
			}

			return Command.CODE_ERROR_OCCURRED;
		} catch (EngineTimeoutException ete) {
			addDescription("Engine timeout. Reason:"
					+ ete.getLocalizedMessage(), true);
			super.daLog().logThrowable(ete);
			return Command.CODE_ERROR_ENGINE_TIMEOUT;
		} catch (DeployException e) {
			DeployItem[] items = e.getDeploymentItems();
			dumpStatuses(true, e.getDeploymentItems());
			String description = "DeployException:" + e.getMessage();
			addDescription(description, true);
			super.daLog().logThrowable(e);

			try {
				reporter.processDeployItems(e.getOrderedDeploymentItems(),
						DeployResultStatus.ERROR, description);
			} catch (Exception ex) {
				daLog().logThrowable(ex);
			}

			return Command.CODE_ERROR_OCCURRED;
		} catch (APIException e) {
			addDescription(
					"General deployment exception. For more info please refer to the trace file. Reason:"
							+ e.getLocalizedMessage(), true);
			super.daLog().logThrowable(e);
			return Command.CODE_ERROR_OCCURRED;
		}
	}

	private void dumpStatuses(boolean isError, DeployItem[] retDeployItems) {
		DeployItem nextItem;
		Sdu nextSdu;
		DeployItemStatus nextItemStatus;
		for (int j = 0; j < retDeployItems.length; j++) {
			nextItem = retDeployItems[j];
			nextSdu = nextItem.getSdu();
			nextItemStatus = nextItem.getDeployItemStatus();
			if (!DeployItemStatus.SUCCESS.equals(nextItemStatus)
					&& !DeployItemStatus.FILTERED.equals(nextItemStatus)) {
				String sduInfo = nextSdu != null ? (nextSdu.getName() + " ( "
						+ nextSdu.getVendor() + ")") : "n/a";
				addDescription("Component:" + sduInfo + Command.EOL + "Status:"
						+ nextItemStatus + Command.EOL + "Description:"
						+ nextItem.getDescription(), isError);
			}
		}
	}

	private class CLToolDeploymentListener implements DeploymentListener {
		private final CmdLogger logger;

		CLToolDeploymentListener(CmdLogger logger) {
			this.logger = logger;
		}

		public void deploymentPerformed(DeploymentEvent event) {
			dumpAction(event);
		}

		private void dumpAction(DeploymentEvent event) {
			DeployItem deployItem = event.getDeployItem();
			Sdu sdu = deployItem.getSdu();
			if (sdu instanceof Sca) {
				// Do nothing in the case
				return;
			}
			StringBuffer itemInfo = generateDeployItemInfo((Sda) sdu,
					deployItem);
			this.logger.msgToTrace(CmdLogger.PATTERN_EVENT_BEGIN + Command.EOL
					+ "Action: " + event.getDeploymentEventAction().getName()
					+ Command.EOL + "Item: " + Command.EOL + itemInfo
					+ Command.EOL + CmdLogger.PATTERN_EVENT_END);
		}
	}

	public static StringBuffer generateDeployItemInfo(Sda sda,
			DeployItem deployItem) {
		StringBuffer itemInfo = new StringBuffer();
		if (deployItem != null) {
			itemInfo.append(Command.TAB).append("File: ").append(
					deployItem.getArchive()).append(Command.EOL_TAB).append(
					"Status: ").append(deployItem.getDeployItemStatus())
					.append(Command.EOL_TAB).append("Version: ").append(
							deployItem.getVersionStatus());
			if (deployItem.getDescription() != null
					&& deployItem.getDescription().trim().length() > 0) {
				itemInfo.append(Command.EOL_TAB).append("Description: ")
						.append(deployItem.getDescription().trim());
			}
			if (sda != null) {
				itemInfo.append(Command.EOL_TAB).append("Component info:")
						.append(Command.EOL_2TABS).append("Name: ").append(
								sda.getName()).append(Command.EOL_2TABS)
						.append("Vendor: ").append(sda.getVendor()).append(
								Command.EOL_2TABS).append("Version: ").append(
								sda.getVersion().getVersionAsString()).append(
								Command.EOL_2TABS).append("Location: ").append(
								sda.getLocation());
				ScaId scaId = sda.getScaId();
				if (scaId != null) {
					itemInfo.append(Command.EOL_2TABS).append("Sca:").append(
							Command.EOL_3TABS).append("Name: ").append(
							scaId.getName()).append(Command.EOL_3TABS).append(
							"Vendor: ").append(scaId.getVendor());
				}
				Set deps = sda.getDependencies();
				if (deps != null && !deps.isEmpty()) {
					itemInfo.append(Command.EOL_2TABS).append("Dependencies:");
					for (Iterator iter = deps.iterator(); iter.hasNext();) {
						Dependency element = (Dependency) iter.next();
						itemInfo.append(Command.EOL_3TABS).append("Name:")
								.append(element.getName()).append(", ").append(
										"Vendor:").append(element.getVendor());
					}
				}
			}
			itemInfo.append(Command.EOL);
		} else {
			itemInfo.append("Deploy item is null.").append(Command.EOL);
		}
		return itemInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.api.cmd.AbstractCommand#processOption(java
	 * .lang.String, java.lang.String)
	 */
	protected int processOption(String key, String value) {
		if ("-l".equals(key) || "--list".equals(key)) {
			File listFile = new File(value);
			if (!listFile.exists()) {
				addDescription("File '" + getCanonicalFilePath(listFile)
						+ "' does not exist.", true);
				return Command.CODE_ERROR_OCCURRED;
			}
			if (!listFile.canRead()) {
				addDescription("File '" + getCanonicalFilePath(listFile)
						+ "' cannot read.", true);
				return Command.CODE_ERROR_OCCURRED;
			}
			return collectFilesFromList(listFile);
		} else if ("-f".equals(key) || "--file".equals(key)) {
			return addFileToList(value);
		} else if ("-e".equals(key) || "--onError".equals(key)) {
			if ("skipdepending".equals(value)) {
				this.errorStrategy = ErrorStrategy.ON_ERROR_SKIP_DEPENDING;
				return Command.CODE_SUCCESS;
			} else if ("stop".equals(value)) {
				this.errorStrategy = ErrorStrategy.ON_ERROR_STOP;
				return Command.CODE_SUCCESS;
			} else {
				addDescription("Unknown error strategy  '" + value + "'", true);
				return Command.CODE_CRITICAL_ERROR;
			}
		} else if ("-s".equals(key) || "--softwareFilter".equals(key)) {
			if (this.softwareFilters == null) {
				this.softwareFilters = new ArrayList();
			}
			if (this.softwareFilters.contains(value)) {
				addDescription("Software filter '" + value
						+ "' already exists.", false);
				return Command.CODE_SUCCESS_WITH_WARNINGS;
			} else {
				this.softwareFilters.add(value);
				return Command.CODE_SUCCESS;
			}
		} else if ("-u".equals(key) || "--updateVersions".equals(key)) {
			if ("lower".equals(value)) {
				this.updateStrategy = ComponentVersionHandlingRule.UPDATE_LOWER_VERSIONS_ONLY;
				return Command.CODE_SUCCESS;
			} else if ("samelower".equals(value)) {
				this.updateStrategy = ComponentVersionHandlingRule.UPDATE_SAME_AND_LOWER_VERSIONS_ONLY;
				return Command.CODE_SUCCESS;
			} else if ("all".equals(value)) {
				this.updateStrategy = ComponentVersionHandlingRule.UPDATE_ALL_VERSIONS;
				return Command.CODE_SUCCESS;
			} else {
				addDescription("Unknown update versions strategy  '" + value
						+ "'", true);
				return Command.CODE_CRITICAL_ERROR;
			}
		} else if ("-w".equals(key) || "--workflow".equals(key)) {
			if ("safety".equalsIgnoreCase(value)) {
				this.deployWorkflowStrategy = DeployWorkflowStrategy.SAFETY;
				return Command.CODE_SUCCESS;
			} else if ("normal".equalsIgnoreCase(value)) {
				this.deployWorkflowStrategy = DeployWorkflowStrategy.NORMAL;
				return Command.CODE_SUCCESS;
			} else {
				addDescription("Unknown deploy worflow versions strategy  '"
						+ value + "'", true);
				return Command.CODE_CRITICAL_ERROR;
			}
		} else if ("--lcm".equals(key)) {
			if ("sequential".equalsIgnoreCase(value)) {
				this.lifeCycleDeployStrategy = LifeCycleDeployStrategy.SEQUENTIAL;
				return Command.CODE_SUCCESS;
			} else if ("bulk".equalsIgnoreCase(value)) {
				this.lifeCycleDeployStrategy = LifeCycleDeployStrategy.BULK;
				return Command.CODE_SUCCESS;
			} else if ("disable".equalsIgnoreCase(value)) {
				this.lifeCycleDeployStrategy = LifeCycleDeployStrategy.DISABLE_LCM;
				return Command.CODE_SUCCESS;
			} else {
				addDescription("Unknown life cycle deploy strategy '" + value
						+ "'", true);
				return Command.CODE_CRITICAL_ERROR;
			}
		} else if ("-t".equals(key) || "--timeout".equals(key)) {
			try {
				this.timeout = Long.parseLong(value, 10);
				return Command.CODE_SUCCESS;
			} catch (NumberFormatException nfe) {
				addDescription("Invalid timeout value :"
						+ nfe.getLocalizedMessage(), true);
				return Command.CODE_CRITICAL_ERROR;
			}
		} else {
			addDescription("Unknown option '" + key + "', value '" + value
					+ "'", false);
			return Command.CODE_SUCCESS_WITH_WARNINGS;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.api.cmd.AbstractCommand#internalExec()
	 */
	protected int executeCommand() {
		if (this.fileList.isEmpty()) {
			usage();
			return Command.CODE_ERROR_OCCURRED;
		}
		return performOperation();
	}

}
