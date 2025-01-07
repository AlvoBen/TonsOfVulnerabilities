/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * Created on Oct 18, 2005
 */
package com.sap.engine.services.dc.api.cmd.undeploy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.ErrorStrategy;
import com.sap.engine.services.dc.api.ErrorStrategyAction;
import com.sap.engine.services.dc.api.cmd.AbstractCommand;
import com.sap.engine.services.dc.api.cmd.Command;
import com.sap.engine.services.dc.api.cmd.util.CmdLogger;
import com.sap.engine.services.dc.api.event.EventMode;
import com.sap.engine.services.dc.api.event.ListenerMode;
import com.sap.engine.services.dc.api.event.UndeploymentEvent;
import com.sap.engine.services.dc.api.event.UndeploymentListener;
import com.sap.engine.services.dc.api.lock_mng.AlreadyLockedException;
import com.sap.engine.services.dc.api.undeploy.EngineTimeoutException;
import com.sap.engine.services.dc.api.undeploy.UndeployException;
import com.sap.engine.services.dc.api.undeploy.UndeployItem;
import com.sap.engine.services.dc.api.undeploy.UndeployProcessor;
import com.sap.engine.services.dc.api.undeploy.UndeployResult;
import com.sap.engine.services.dc.api.undeploy.UndeployResultNotFoundException;
import com.sap.engine.services.dc.api.undeploy.UndeployResultStatus;
import com.sap.engine.services.dc.api.undeploy.UndeployWorkflowStrategy;
import com.sap.engine.services.dc.api.undeploy.UndeploymentStrategy;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright (c) 2005, SAP-AG Date: Oct 18, 2005
 * 
 * @author Boris Savov(i030791)
 * @version 1.0
 * @since 7.1
 * 
 */
public class UndeployCommand extends AbstractCommand {
	private final ArrayList compList = new ArrayList();
	private ErrorStrategy errorStrategy = ErrorStrategy.ON_ERROR_STOP;
	private UndeploymentStrategy undeploymentStrategy = UndeploymentStrategy.IF_DEPENDING_STOP;
	private long timeout = -1;

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
			return collectComponentsFromList(listFile);
		} else if ("-c".equals(key) || "--component".equals(key)) {
			return addComponentToList(value);
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
		} else if ("-u".equals(key) || "--updateVersions".equals(key)) {
			if ("ifDependingStop".equals(value)) {
				this.undeploymentStrategy = UndeploymentStrategy.IF_DEPENDING_STOP;
				return Command.CODE_SUCCESS;
			} else if ("undeployDepending".equals(value)) {
				this.undeploymentStrategy = UndeploymentStrategy.UNDEPLOY_DEPENDING;
				return Command.CODE_SUCCESS;
			} else {
				addDescription("Unknown update versions strategy  '" + value
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

	private int collectComponentsFromList(File listFile) {
		BufferedReader in = null;
		int totalRet = Command.CODE_SUCCESS;
		try {
			in = new BufferedReader(new FileReader(listFile));
			String nextLine;
			int ret;
			while ((nextLine = in.readLine()) != null) {
				ret = addComponentToList(nextLine);
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

	private int addComponentToList(String comp) {
		if (comp == null || (comp = comp.trim()).length() == 0) {
			addDescription("Component cannot be null or empty", true);
			return Command.CODE_ERROR_OCCURRED;
		}
		String[] terms = comp.split(":");
		if (terms.length != 2) {
			addDescription("Not valid component description '" + comp
					+ "'. Pattern componentName:vendorName", true);
			return Command.CODE_ERROR_OCCURRED;
		}
		ComponentId compId = new ComponentId(terms[0], terms[1]);
		if (this.compList.contains(compId)) {
			super.daLog().logWarning("ASJ.dpl_api.001282",
					"[{0}] already exists in the list and will be skipped.",
					new Object[] { compId });
			return Command.CODE_SUCCESS_WITH_WARNINGS;
		} else {
			this.compList.add(compId);
			return Command.CODE_SUCCESS;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.api.cmd.AbstractCommand#internalExec()
	 */
	protected int executeCommand() {
		if (this.compList.isEmpty()) {
			usage();
			return Command.CODE_ERROR_OCCURRED;
		}
		try {
			UndeployProcessor undeployProcessor = getClient()
					.getComponentManager().getUndeployProcessor();
			undeployProcessor
					.setErrorStrategy(ErrorStrategyAction.UNDEPLOYMENT_ACTION,
							this.errorStrategy);
			undeployProcessor
					.setUndeploymentStrategy(this.undeploymentStrategy);
			undeployProcessor
					.setUndeployWorkflowStrategy(UndeployWorkflowStrategy.NORMAL);
			if (this.timeout > 0) {
				undeployProcessor.setCustomServerTimeout(this.timeout);
			}
			UndeployItem[] undeployItems = new UndeployItem[this.compList
					.size()];
			int i = 0;
			for (Iterator iter = this.compList.iterator(); iter.hasNext(); i++) {
				ComponentId element = (ComponentId) iter.next();
				undeployItems[i] = undeployProcessor.createUndeployItem(element
						.getName(), element.getVendor());
			}

			undeployProcessor.addUndeploymentListener(
					new InternalUndeploymentListener(super.getCmdLogger()),
					ListenerMode.LOCAL, EventMode.SYNCHRONOUS);

			UndeployResult undeployResult = undeployProcessor
					.undeploy(undeployItems);
			if (undeployResult == null) {
				addDescription("Undeploy result could not be received.", true);
				return Command.CODE_ERROR_OCCURRED;
			} else {
				UndeployResultStatus undeployResultStatus = undeployResult
						.getUndeployStatus();
				if (UndeployResultStatus.SUCCESS.equals(undeployResultStatus)) {
					return Command.CODE_SUCCESS;
				} else if (UndeployResultStatus.WARNING
						.equals(undeployResultStatus)) {
					addDescription(undeployResult.getDescription(), false);
					return Command.CODE_SUCCESS_WITH_WARNINGS;
				} else if (UndeployResultStatus.ERROR
						.equals(undeployResultStatus)) {
					addDescription(undeployResult.getDescription(), true);
					return Command.CODE_ERROR_OCCURRED;
				} else {
					addDescription("Unknown result status '"
							+ undeployResultStatus + "'", true);
					return Command.CODE_CRITICAL_ERROR;
				}
			}
		} catch (ConnectionException e) {
			addDescription("ConnectionException:" + e.getMessage(), true);
			super.daLog().logThrowable(e);
			return Command.CODE_ERROR_OCCURRED;
		} catch (UndeployResultNotFoundException e) {
			addDescription("Deploy Result Not Found. Reason:"
					+ e.getLocalizedMessage(), true);
			super.daLog().logThrowable(e);
			return Command.CODE_ERROR_OCCURRED;
		} catch (EngineTimeoutException e) {
			addDescription("Engine timeout. Reason:" + e.getLocalizedMessage(),
					true);
			super.daLog().logThrowable(e);
			return Command.CODE_ERROR_ENGINE_TIMEOUT;
		} catch (UndeployException e) {
			addDescription("UndeployException:" + e.getMessage(), true);
			super.daLog().logThrowable(e);
			return Command.CODE_ERROR_OCCURRED;
		} catch (AlreadyLockedException e) {
			addDescription("AlreadyLockedException:" + e.getMessage(), true);
			super.daLog().logThrowable(e);
			return Command.CODE_ERROR_OCCURRED;
		}
	}

	private static class ComponentId {
		private final String name, vendor;
		private final int hashCode;

		ComponentId(String name, String vendor) {
			if (name == null) {
				throw new NullPointerException("Name cannot be null.");
			}
			if (vendor == null) {
				throw new NullPointerException("Vendor cannot be null.");
			}
			this.name = name;
			this.vendor = vendor;
			this.hashCode = (17 + this.name.hashCode()) * 59
					+ this.vendor.hashCode();
		}

		public String getName() {
			return this.name;
		}

		public String getVendor() {
			return this.vendor;
		}

		public String toString() {
			return "Component name '" + this.name + "', vendor '" + this.vendor
					+ "'";
		}

		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}

			if (obj == this) {
				return true;
			}

			if (!(obj instanceof ComponentId)) {
				return false;
			}

			ComponentId other = (ComponentId) obj;
			return this.getName().equals(other.getName())
					&& this.getVendor().equals(other.getVendor());
		}

		public int hashCode() {
			return this.hashCode;
		}
	}

	private class InternalUndeploymentListener implements UndeploymentListener {
		private final CmdLogger logger;

		InternalUndeploymentListener(CmdLogger logger) {
			this.logger = logger;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @seecom.sap.engine.services.dc.api.event.UndeploymentListener#
		 * undeploymentPerformed
		 * (com.sap.engine.services.dc.api.event.UndeploymentEvent)
		 */
		public void undeploymentPerformed(UndeploymentEvent event) {
			dumpAction(event);
		}

		private void dumpAction(UndeploymentEvent event) {
			UndeployItem undeployItem = event.getUndeployItem();
			StringBuffer itemInfo = generateUndeployItemInfo(undeployItem);
			this.logger.msgToTrace(CmdLogger.PATTERN_EVENT_BEGIN + Command.EOL
					+ "Action: " + event.getUndeploymentEventAction().getName()
					+ Command.EOL + "Item: " + Command.EOL + itemInfo
					+ Command.EOL + CmdLogger.PATTERN_EVENT_END);
		}

		public StringBuffer generateUndeployItemInfo(UndeployItem undeployItem) {
			StringBuffer itemInfo = new StringBuffer();
			if (undeployItem != null) {
				itemInfo.append(Command.TAB).append("Name: ").append(
						undeployItem.getName()).append(Command.EOL_TAB).append(
						"Vendor: ").append(undeployItem.getVendor()).append(
						Command.EOL_TAB).append("Version: ").append(
						undeployItem.getVersion()).append(Command.EOL_TAB)
						.append("Location: ")
						.append(undeployItem.getLocation()).append(
								Command.EOL_TAB).append("Status: ").append(
								undeployItem.getUndeployItemStatus());
				if (undeployItem.getDescription() != null
						&& undeployItem.getDescription().trim().length() > 0) {
					itemInfo.append(Command.EOL_TAB).append("Description: ")
							.append(undeployItem.getDescription().trim());
				}
				itemInfo.append(Command.EOL);
			} else {
				itemInfo.append("Undeploy item is null.").append(Command.EOL);
			}
			return itemInfo;
		}

	}

}
