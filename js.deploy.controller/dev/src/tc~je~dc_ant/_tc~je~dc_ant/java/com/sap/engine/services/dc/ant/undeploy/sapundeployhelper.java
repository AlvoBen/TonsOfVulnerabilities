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
package com.sap.engine.services.dc.ant.undeploy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.sap.engine.services.dc.ant.SAPErrorHandling;
import com.sap.engine.services.dc.ant.SAPErrorStrategy;
import com.sap.engine.services.dc.ant.SAPErrorStrategyAction;
import com.sap.engine.services.dc.ant.SAPJ2EEEngine;
import com.sap.engine.services.dc.ant.undeploy.SAPUndeploymentData;
import com.sap.engine.services.dc.ant.undeploy.SAPUndeploymentStrategy;
import com.sap.engine.services.dc.ant.undeploy.SAPUndeploymentException;
import com.sap.engine.services.dc.api.APIException;
import com.sap.engine.services.dc.api.AuthenticationException;
import com.sap.engine.services.dc.api.Client;
import com.sap.engine.services.dc.api.ClientFactory;
import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.ErrorStrategy;
import com.sap.engine.services.dc.api.ErrorStrategyAction;
import com.sap.engine.services.dc.api.undeploy.UndeployException;
import com.sap.engine.services.dc.api.undeploy.UndeployItem;
import com.sap.engine.services.dc.api.undeploy.UndeployProcessor;
import com.sap.engine.services.dc.api.undeploy.UndeployResult;
import com.sap.engine.services.dc.api.undeploy.UndeployResultStatus;
import com.sap.engine.services.dc.api.undeploy.UndeploymentStrategy;

/**
 * 
 * This class contains the basic functionality for the undeploy call to the
 * engine.
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov( i030791 )
 * @author Todor Stoitsev
 * @version 1.0
 * @since 7.1
 * 
 */
public final class SAPUndeployHelper {

	private static final Map ERROR_ACTIONS_MAP = new HashMap();

	private static final Map ERROR_STRATEGIES_MAP = new HashMap();

	private static final Map UNDEPLOY_RESULTS_MAP = new HashMap();

	private static final Map UNDEPLOY_STRATEGY = new HashMap();

	private static SAPUndeployHelper INSTANCE;

	static {
		ERROR_ACTIONS_MAP.put(SAPErrorStrategyAction.DEPLOYMENT_ACTION,
				ErrorStrategyAction.DEPLOYMENT_ACTION);
		ERROR_ACTIONS_MAP.put(SAPErrorStrategyAction.UNDEPLOYMENT_ACTION,
				ErrorStrategyAction.UNDEPLOYMENT_ACTION);
		ERROR_ACTIONS_MAP.put(
				SAPErrorStrategyAction.PREREQUISITES_CHECK_ACTION,
				ErrorStrategyAction.PREREQUISITES_CHECK_ACTION);

		ERROR_STRATEGIES_MAP.put(SAPErrorStrategy.ON_ERROR_SKIP_DEPENDING,
				ErrorStrategy.ON_ERROR_SKIP_DEPENDING);
		ERROR_STRATEGIES_MAP.put(SAPErrorStrategy.ON_ERROR_STOP,
				ErrorStrategy.ON_ERROR_STOP);

		UNDEPLOY_RESULTS_MAP.put(UndeployResultStatus.ERROR,
				SAPUndeployResult.ERROR);
		UNDEPLOY_RESULTS_MAP.put(UndeployResultStatus.WARNING,
				SAPUndeployResult.WARNING);
		UNDEPLOY_RESULTS_MAP.put(UndeployResultStatus.SUCCESS,
				SAPUndeployResult.SUCCESS);

		UNDEPLOY_STRATEGY.put(SAPUndeploymentStrategy.IF_DEPENDING_STOP,
				UndeploymentStrategy.IF_DEPENDING_STOP);
		UNDEPLOY_STRATEGY.put(SAPUndeploymentStrategy.UNDEPLOY_DEPENDING,
				UndeploymentStrategy.UNDEPLOY_DEPENDING);
	}

	public static synchronized SAPUndeployHelper getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SAPUndeployHelper();
		}
		return INSTANCE;
	}

	private SAPUndeployHelper() {
	}

	public SAPUndeployResult doUndeploy(SAPUndeploy task,
			SAPUndeploymentData undeploymentData)
			throws SAPUndeploymentException {
		final UndeployProcessor undeployProcessor = getUndeployProcessor(undeploymentData
				.getEngine());

		setupUndeployProcessor(undeployProcessor, undeploymentData);
		task.log("processor undeploy strategy is "
				+ undeployProcessor.getUndeploymentStrategy());
		final UndeployItem[] undeployItems = getUndeployItems(undeploymentData
				.getItems(), undeployProcessor);

		final UndeployResult undeployResult;
		try {
			undeployResult = undeployProcessor.undeploy(undeployItems);
		} catch (ConnectionException ce) {
			throw new SAPUndeploymentException(ce);
		} catch (APIException apie) {
			throw new SAPUndeploymentException(apie);
		}

		final UndeployResultStatus undeployResultStatus = undeployResult
				.getUndeployStatus();
		final SAPUndeployResult taskUndeployResult = (SAPUndeployResult) UNDEPLOY_RESULTS_MAP
				.get(undeployResultStatus);

		if (taskUndeployResult == null) {
			task.log("Error! Undeploy result '" + undeployResultStatus
					+ "' is unknown.");
			return SAPUndeployResult.ERROR;
		}
		return taskUndeployResult;
	}

	private void setupUndeployProcessor(UndeployProcessor undeployProcessor,
			SAPUndeploymentData deploymentData) throws SAPUndeploymentException {
		setUndeployStrategy(undeployProcessor, deploymentData);
		setErrorHandlings(undeployProcessor, deploymentData);
	}

	/**
	 * @param undeploymentData
	 */
	private void setErrorHandlings(UndeployProcessor undeployProcessor,
			SAPUndeploymentData undeploymentData)
			throws SAPUndeploymentException {
		final Collection errHandlings = undeploymentData.getErrorHandlings();
		for (Iterator iter = errHandlings.iterator(); iter.hasNext();) {
			final SAPErrorHandling errHandling = (SAPErrorHandling) iter.next();
			final ErrorStrategyAction errAction = getErrorAction(errHandling);
			final ErrorStrategy errStrategy = getErrorStrategy(errHandling);
			if (errAction != null && errStrategy != null) {
				undeployProcessor.setErrorStrategy(errAction, errStrategy);
			}
		}
	}

	private ErrorStrategyAction getErrorAction(
			final SAPErrorHandling errHandling) throws SAPUndeploymentException {
		if (errHandling.getErrorAction() != null
				&& !errHandling.getErrorAction().trim().equals("")) {
			final SAPErrorStrategyAction errAction = SAPErrorStrategyAction
					.getErrorStrategyActionByName(errHandling.getErrorAction());
			final ErrorStrategyAction errStrategyAction = (ErrorStrategyAction) ERROR_ACTIONS_MAP
					.get(errAction);

			if (errStrategyAction == null) {
				throw new SAPUndeploymentException(
						"The mapped error action is null. "
								+ "The specified error action is '"
								+ errHandling.getErrorAction() + "'.");
			}

			return errStrategyAction;
		}

		return null;
	}

	private ErrorStrategy getErrorStrategy(final SAPErrorHandling errHandling)
			throws SAPUndeploymentException {
		if (errHandling.getErrorStrategy() != null
				&& !errHandling.getErrorStrategy().trim().equals("")) {
			final SAPErrorStrategy errStrategy = SAPErrorStrategy
					.getErrorStrategyByName(errHandling.getErrorStrategy());
			final ErrorStrategy errorStrategy = (ErrorStrategy) ERROR_STRATEGIES_MAP
					.get(errStrategy);

			if (errorStrategy == null) {
				throw new SAPUndeploymentException(
						"The mapped error strategy is null. "
								+ "The specified error strategy is '"
								+ errHandling.getErrorStrategy() + "'.");
			}

			return errorStrategy;
		}

		return null;
	}

	private void setUndeployStrategy(UndeployProcessor undeployProcessor,
			SAPUndeploymentData undeploymentData)
			throws SAPUndeploymentException {
		if (undeploymentData.getUndeployStrategy() != null
				&& !undeploymentData.getUndeployStrategy().trim().equals("")) {

			final SAPUndeploymentStrategy undeployStrategy = SAPUndeploymentStrategy
					.getComponentUndeploymentStrategyByName(undeploymentData
							.getUndeployStrategy());
			final UndeploymentStrategy componentUndeploymentStrategy = (UndeploymentStrategy) UNDEPLOY_STRATEGY
					.get(undeployStrategy);

			if (componentUndeploymentStrategy == null) {
				throw new SAPUndeploymentException(
						"The mapped component undeploy strategy is null. "
								+ "The specified undeploy strategy is '"
								+ undeploymentData.getUndeployStrategy() + "'.");
			}

			undeployProcessor
					.setUndeploymentStrategy(componentUndeploymentStrategy);
		}
	}

	private UndeployProcessor getUndeployProcessor(SAPJ2EEEngine engine)
			throws SAPUndeploymentException {
		Thread.currentThread().setContextClassLoader(
				this.getClass().getClassLoader());

		final Client dcClient;
		try {
			dcClient = ClientFactory.getInstance().createClient(
					engine.getServerHost(), engine.getServerPort(),
					engine.getUserName(), engine.getUserPassword());
		} catch (AuthenticationException ae) {
			throw new SAPUndeploymentException(ae);
		} catch (ConnectionException ce) {
			throw new SAPUndeploymentException(ce);
		}

		final UndeployProcessor undeployProcessor;
		try {
			undeployProcessor = dcClient.getComponentManager()
					.getUndeployProcessor();
		} catch (UndeployException de) {
			throw new SAPUndeploymentException(de);
		} catch (ConnectionException ce) {
			throw new SAPUndeploymentException(ce);
		}

		return undeployProcessor;
	}

	private UndeployItem[] getUndeployItems(Collection items,
			final UndeployProcessor undeployProcessor) {
		final UndeployItem[] undeployItems = new UndeployItem[items.size()];
		int idx = 0;
		for (Iterator iter = items.iterator(); iter.hasNext();) {
			final String sFullItemName = (String) iter.next();
			String sVendor = "sap.com";
			String sName = sFullItemName;
			int niIdx = sFullItemName.indexOf("/");
			if (niIdx != -1) {
				sVendor = sFullItemName.substring(0, niIdx);
				sName = sFullItemName.substring(niIdx + 1);
			}
			undeployItems[idx++] = undeployProcessor.createUndeployItem(sName,
					sVendor);
		}

		return undeployItems;
	}
}
