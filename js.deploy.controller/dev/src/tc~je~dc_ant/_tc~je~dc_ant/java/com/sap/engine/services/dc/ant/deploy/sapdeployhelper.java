package com.sap.engine.services.dc.ant.deploy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.sap.engine.services.dc.ant.SAPErrorHandling;
import com.sap.engine.services.dc.ant.SAPErrorStrategy;
import com.sap.engine.services.dc.ant.SAPErrorStrategyAction;
import com.sap.engine.services.dc.ant.SAPJ2EEEngine;
import com.sap.engine.services.dc.api.APIException;
import com.sap.engine.services.dc.api.AuthenticationException;
import com.sap.engine.services.dc.api.Client;
import com.sap.engine.services.dc.api.ClientFactory;
import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.ErrorStrategy;
import com.sap.engine.services.dc.api.ErrorStrategyAction;
import com.sap.engine.services.dc.api.deploy.ComponentVersionHandlingRule;
import com.sap.engine.services.dc.api.deploy.DeployException;
import com.sap.engine.services.dc.api.deploy.DeployItem;
import com.sap.engine.services.dc.api.deploy.DeployProcessor;
import com.sap.engine.services.dc.api.deploy.DeployResult;
import com.sap.engine.services.dc.api.deploy.DeployResultStatus;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-2-9
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov( i030791 )
 * @version 1.0
 * @since 7.1
 * 
 */
final class SAPDeployHelper {

	private static final Map VERSION_RULES_MAP = new HashMap();
	private static final Map ERROR_ACTIONS_MAP = new HashMap();
	private static final Map ERROR_STRATEGIES_MAP = new HashMap();
	private static final Map DEPLOY_RESULTS_MAP = new HashMap();

	private static SAPDeployHelper INSTANCE;

	static {
		VERSION_RULES_MAP.put(SAPVersionHandlingRule.UPDATE_ALL_VERSIONS,
				ComponentVersionHandlingRule.UPDATE_ALL_VERSIONS);
		VERSION_RULES_MAP.put(
				SAPVersionHandlingRule.UPDATE_LOWER_VERSIONS_ONLY,
				ComponentVersionHandlingRule.UPDATE_LOWER_VERSIONS_ONLY);
		VERSION_RULES_MAP
				.put(
						SAPVersionHandlingRule.UPDATE_SAME_AND_LOWER_VERSIONS_ONLY,
						ComponentVersionHandlingRule.UPDATE_SAME_AND_LOWER_VERSIONS_ONLY);

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

		DEPLOY_RESULTS_MAP.put(DeployResultStatus.UNKNOWN,
				SAPDeployResult.UNKNOWN);
		DEPLOY_RESULTS_MAP.put(DeployResultStatus.ERROR, SAPDeployResult.ERROR);
		DEPLOY_RESULTS_MAP.put(DeployResultStatus.WARNING,
				SAPDeployResult.WARNING);
		DEPLOY_RESULTS_MAP.put(DeployResultStatus.SUCCESS,
				SAPDeployResult.SUCCESS);
	}

	static synchronized SAPDeployHelper getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SAPDeployHelper();
		}

		return INSTANCE;
	}

	private SAPDeployHelper() {
	}

	SAPDeployResult doDeploy(SAPDeploymentData deploymentData)
			throws SAPDeploymentException {
		final DeployProcessor deployProcessor = getDeployProcessor(deploymentData
				.getEngine());

		setupDeployProcessor(deployProcessor, deploymentData);

		final DeployItem[] deployItems = getDeployItems(deploymentData
				.getArchives(), deployProcessor);

		final DeployResult deployResult;
		try {
			deployResult = deployProcessor.deploy(deployItems);
		} catch (ConnectionException ce) {
			throw new SAPDeploymentException(ce);
		} catch (APIException apie) {
			throw new SAPDeploymentException(apie);
		}

		final DeployResultStatus deployResultStatus = deployResult
				.getDeployResultStatus();
		final SAPDeployResult taskDeployResult = (SAPDeployResult) DEPLOY_RESULTS_MAP
				.get(deployResultStatus);

		return taskDeployResult != null ? taskDeployResult
				: SAPDeployResult.UNKNOWN;
	}

	private void setupDeployProcessor(DeployProcessor deployProcessor,
			SAPDeploymentData deploymentData) throws SAPDeploymentException {
		setVersionRule(deployProcessor, deploymentData);
		setServerTimeout(deployProcessor, deploymentData);
		setErrorHandlings(deployProcessor, deploymentData);
		
	}
	
	/**
	 * @param deployProcessor
	 * @param deploymentData
	 * @throws SAPDeploymentException
	 */
	private void setServerTimeout(DeployProcessor deployProcessor,
			SAPDeploymentData deploymentData) throws SAPDeploymentException {
		if (deploymentData.getDeployTimeout() != null) {
			if (deploymentData.getDeployTimeout() > 0) {
				deployProcessor.setCustomServerTimeout(deploymentData
						.getDeployTimeout());
			} else {
				throw new SAPDeploymentException(
						"The given deploy timeout " +deploymentData.getDeployTimeout()+" is not correct. It must be valid integer greater than 0. Correct its value in used ant xml.");
			}
		}
	}

	/**
	 * @param deploymentData
	 */
	private void setErrorHandlings(DeployProcessor deployProcessor,
			SAPDeploymentData deploymentData) throws SAPDeploymentException {
		final Collection errHandlings = deploymentData.getErrorHandlings();
		for (Iterator iter = errHandlings.iterator(); iter.hasNext();) {
			final SAPErrorHandling errHandling = (SAPErrorHandling) iter.next();

			final ErrorStrategyAction errAction = getErrorAction(errHandling);
			final ErrorStrategy errStrategy = getErrorStrategy(errHandling);
			if (errAction != null && errStrategy != null) {
				deployProcessor.setErrorStrategy(errAction, errStrategy);
			}
		}
	}

	private ErrorStrategyAction getErrorAction(
			final SAPErrorHandling errHandling) throws SAPDeploymentException {
		if (errHandling.getErrorAction() != null
				&& !errHandling.getErrorAction().trim().equals("")) {
			final SAPErrorStrategyAction errAction = SAPErrorStrategyAction
					.getErrorStrategyActionByName(errHandling.getErrorAction());
			final ErrorStrategyAction errStrategyAction = (ErrorStrategyAction) ERROR_ACTIONS_MAP
					.get(errAction);

			if (errStrategyAction == null) {
				throw new SAPDeploymentException(
						"The mapped error action is null. "
								+ "The specified error action is '"
								+ errHandling.getErrorAction() + "'.");
			}

			return errStrategyAction;
		}

		return null;
	}

	private ErrorStrategy getErrorStrategy(final SAPErrorHandling errHandling)
			throws SAPDeploymentException {
		if (errHandling.getErrorStrategy() != null
				&& !errHandling.getErrorStrategy().trim().equals("")) {
			final SAPErrorStrategy errStrategy = SAPErrorStrategy
					.getErrorStrategyByName(errHandling.getErrorStrategy());
			final ErrorStrategy errorStrategy = (ErrorStrategy) ERROR_STRATEGIES_MAP
					.get(errStrategy);

			if (errorStrategy == null) {
				throw new SAPDeploymentException(
						"The mapped error strategy is null. "
								+ "The specified error strategy is '"
								+ errHandling.getErrorStrategy() + "'.");
			}

			return errorStrategy;
		}

		return null;
	}

	private void setVersionRule(DeployProcessor deployProcessor,
			SAPDeploymentData deploymentData) throws SAPDeploymentException {
		if (deploymentData.getVersionRule() != null
				&& !deploymentData.getVersionRule().trim().equals("")) {
			final SAPVersionHandlingRule versionRule = SAPVersionHandlingRule
					.getComponentVersionHandlingRuleByName(deploymentData
							.getVersionRule());
			final ComponentVersionHandlingRule compVersionRule = (ComponentVersionHandlingRule) VERSION_RULES_MAP
					.get(versionRule);

			if (compVersionRule == null) {
				throw new SAPDeploymentException(
						"The mapped component version handling rule is null. "
								+ "The specified version handling rule is '"
								+ deploymentData.getVersionRule() + "'.");
			}

			deployProcessor.setComponentVersionHandlingRule(compVersionRule);
		}
	}

	private DeployProcessor getDeployProcessor(SAPJ2EEEngine engine)
			throws SAPDeploymentException {
		Thread.currentThread().setContextClassLoader(
				this.getClass().getClassLoader());

		final Client dcClient;
		try {
			dcClient = ClientFactory.getInstance().createClient(
					engine.getServerHost(), engine.getServerPort(),
					engine.getUserName(), engine.getUserPassword());
		} catch (AuthenticationException ae) {
			throw new SAPDeploymentException(ae);
		} catch (ConnectionException ce) {
			throw new SAPDeploymentException(ce);
		}

		final DeployProcessor deployProcessor;
		try {
			deployProcessor = dcClient.getComponentManager()
					.getDeployProcessor();
		} catch (DeployException de) {
			throw new SAPDeploymentException(de);
		} catch (ConnectionException ce) {
			throw new SAPDeploymentException(ce);
		}

		return deployProcessor;
	}

	private DeployItem[] getDeployItems(Collection archives,
			final DeployProcessor deployProcessor) {
		final DeployItem[] deployItems = new DeployItem[archives.size()];
		int idx = 0;
		for (Iterator iter = archives.iterator(); iter.hasNext();) {
			final String archivePath = (String) iter.next();
			deployItems[idx++] = deployProcessor.createDeployItem(archivePath);
		}

		return deployItems;
	}

}
