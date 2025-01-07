package com.sap.sdm.apiimpl.remote.client.p4;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.sap.engine.services.dc.api.APIException;
import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.deploy.DeployException;
import com.sap.engine.services.dc.api.deploy.DeployResultNotFoundException;
import com.sap.engine.services.dc.api.deploy.TransportException;
import com.sap.engine.services.dc.api.deploy.ValidationException;
import com.sap.engine.services.dc.api.deploy.ValidationResult;
import com.sap.sdm.api.remote.ComponentVersionHandlingRule;
import com.sap.sdm.api.remote.ComponentVersionHandlingRules;
import com.sap.sdm.api.remote.DeployItem;
import com.sap.sdm.api.remote.DeployProcessor;
import com.sap.sdm.api.remote.ErrorHandlingRule;
import com.sap.sdm.api.remote.ErrorHandlingRules;
import com.sap.sdm.api.remote.PrerequisiteErrorHandlingRule;
import com.sap.sdm.api.remote.PrerequisiteErrorHandlingRules;
import com.sap.sdm.api.remote.RemoteException;
import com.sap.sdm.api.remote.ValidateResult;
import com.sap.sdm.api.remote.deployresults.PreconditionViolated;
import com.sap.sdm.apiimpl.remote.client.APIRemoteExceptionImpl;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-7-8
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
class P4DeployProcessorImpl implements DeployProcessor {

	private final com.sap.engine.services.dc.api.Client dcClient;

	static DeployProcessor createInstance(
			com.sap.engine.services.dc.api.Client dcClient) {
		return new P4DeployProcessorImpl(dcClient);
	}

	private P4DeployProcessorImpl(com.sap.engine.services.dc.api.Client dcClient) {
		this.dcClient = dcClient;
	}

	private ComponentVersionHandlingRule componentVersionHandlingRule = P4ComponentVersionHandlingRuleImpl.UPDATE_ALL_VERSIONS;

	private ErrorHandlingRule errorHandlingRule = P4ErrorHandlingRuleImpl.ON_ERROR_STOP;

	private PrerequisiteErrorHandlingRule prerequisiteErrorHandlingRule = P4PrerequisiteErrorHandlingRuleImpl.ON_PREREQUISITE_ERROR_STOP;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.api.remote.DeployProcessor#deploy(com.sap.sdm.api.remote.
	 * DeployItem[])
	 */
	public void deploy(DeployItem[] deployItems) throws RemoteException {
		final com.sap.engine.services.dc.api.deploy.DeployProcessor deployProcessor;
		try {
			deployProcessor = this.dcClient.getComponentManager()
					.getDeployProcessor();
		} catch (ConnectionException apice) {
			throw new APIRemoteExceptionImpl(
					"Connection error occurred while getting a DeployProcessor "
							+ "from the Deploy Controller API.", apice);
		} catch (DeployException apie) {
			throw new APIRemoteExceptionImpl(
					"An error occurred while getting a DeployProcessor "
							+ "from the Deploy Controller API.", apie);
		}

		final Map dcSdmDeployItemsMap = buildDcSdmDeployItemsMap(
				deployProcessor, deployItems);

		com.sap.engine.services.dc.api.deploy.ComponentVersionHandlingRule versionRule = mapComponentVersionHandlingRule(this.componentVersionHandlingRule);
		com.sap.engine.services.dc.api.ErrorStrategy errorStrategy = mapErrorHandlingRule(this.errorHandlingRule);
		com.sap.engine.services.dc.api.ErrorStrategy prerequisiteErrorStrategy = mapPrerequisiteErrorHandlingRule(this.prerequisiteErrorHandlingRule);

		deployProcessor.setComponentVersionHandlingRule(versionRule);
		deployProcessor
				.setErrorStrategy(
						com.sap.engine.services.dc.api.ErrorStrategyAction.DEPLOYMENT_ACTION,
						errorStrategy);
		deployProcessor
				.setErrorStrategy(
						com.sap.engine.services.dc.api.ErrorStrategyAction.PREREQUISITES_CHECK_ACTION,
						prerequisiteErrorStrategy);

		com.sap.engine.services.dc.api.deploy.DeployItem[] dcDeployItems = (com.sap.engine.services.dc.api.deploy.DeployItem[]) dcSdmDeployItemsMap
				.keySet()
				.toArray(
						new com.sap.engine.services.dc.api.deploy.DeployItem[dcSdmDeployItemsMap
								.keySet().size()]);

		// com.sap.engine.services.dc.api.deploy.DeployResult dcDeployResult =
		// null;
		try {
			// dcDeployResult =
			deployProcessor.deploy(dcDeployItems);
		} catch (ConnectionException apice) {
			throw new APIRemoteExceptionImpl(
					"Connection error occurred while performing deployment "
							+ "via the Deploy Controller API.", apice);
		} catch (DeployResultNotFoundException apie) {
			throw new APIRemoteExceptionImpl(
					"The result of the deployment could not be "
							+ "found on the server.", apie);
		} catch (DeployException apie) {
			buildDeployResult(dcSdmDeployItemsMap);
			return;
			// throw new APIRemoteExceptionImpl(
			// "An error occurred while performing deployment " +
			// "via the Deploy Controller API.", apie);
		} catch (TransportException te) {
			throw new APIRemoteExceptionImpl(
					"An error occurred while transporting "
							+ "the specified archives via the Deploy "
							+ "Controller API to the SAP Application Server Java.",
					te);
		} catch (APIException ae) {
			throw new APIRemoteExceptionImpl(
					"An error occurred via the Deploy "
							+ "Controller API to the SAP Application Server Java.",
					ae);
		}

		buildDeployResult(dcSdmDeployItemsMap);
	}

	// private void buildResult(Map dcSdmDeployItemsMap) {
	// for (Iterator iter = dcSdmDeployItemsMap.entrySet().iterator();
	// iter.hasNext();) {
	// final Map.Entry mapEntry = (Map.Entry) iter.next();
	// com.sap.engine.services.dc.api.deploy.DeployItem dcDeployItem =
	// (com.sap.engine.services.dc.api.deploy.DeployItem) mapEntry.getKey();
	// final P4DeployItemImpl sdmDeployItem = (P4DeployItemImpl)
	// mapEntry.getValue();
	//      
	// sdmDeployItem.setDeployResult( P4DeployResultMapper.map(dcDeployItem) );
	// }
	// }

	private void buildDeployResult(Map dcSdmDeployItemsMap) {
		for (Iterator iter = dcSdmDeployItemsMap.entrySet().iterator(); iter
				.hasNext();) {
			final Map.Entry mapEntry = (Map.Entry) iter.next();
			com.sap.engine.services.dc.api.deploy.DeployItem dcDeployItem = (com.sap.engine.services.dc.api.deploy.DeployItem) mapEntry
					.getKey();
			final P4DeployItemImpl sdmDeployItem = (P4DeployItemImpl) mapEntry
					.getValue();

			sdmDeployItem.setDeployResult(P4DeployResultMapper
					.map(dcDeployItem));
		}
	}

	private Map buildDcSdmDeployItemsMap(
			com.sap.engine.services.dc.api.deploy.DeployProcessor deployProcessor,
			DeployItem[] deployItems) {
		final Map dcSdmDeployItemsMap = new HashMap();

		for (int i = 0; i < deployItems.length; i++) {
			com.sap.engine.services.dc.api.deploy.DeployItem dcDeployItem = deployProcessor
					.createDeployItem(((P4DeployItemImpl) deployItems[i])
							.getFile().getAbsolutePath());

			dcSdmDeployItemsMap.put(dcDeployItem, deployItems[i]);
		}

		return dcSdmDeployItemsMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.api.remote.DeployProcessor#getComponentVersionHandlingRule()
	 */
	public ComponentVersionHandlingRule getComponentVersionHandlingRule()
			throws RemoteException {
		return this.componentVersionHandlingRule;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.api.remote.DeployProcessor#setComponentVersionHandlingRule
	 * (com.sap.sdm.api.remote.ComponentVersionHandlingRule)
	 */
	public void setComponentVersionHandlingRule(
			ComponentVersionHandlingRule rule) throws RemoteException {
		this.componentVersionHandlingRule = rule;
	}

	private com.sap.engine.services.dc.api.deploy.ComponentVersionHandlingRule mapComponentVersionHandlingRule(
			ComponentVersionHandlingRule rule) throws RemoteException {
		if (rule.getRuleAsInt() == ComponentVersionHandlingRules.UPDATE_ALL_VERSIONS) {
			return com.sap.engine.services.dc.api.deploy.ComponentVersionHandlingRule.UPDATE_ALL_VERSIONS;
		} else if (rule.getRuleAsInt() == ComponentVersionHandlingRules.UPDATE_SAME_AND_LOWER_VERSIONS_ONLY) {
			return com.sap.engine.services.dc.api.deploy.ComponentVersionHandlingRule.UPDATE_SAME_AND_LOWER_VERSIONS_ONLY;
		} else if (rule.getRuleAsInt() == ComponentVersionHandlingRules.UPDATE_LOWER_VERSIONS_ONLY) {
			return com.sap.engine.services.dc.api.deploy.ComponentVersionHandlingRule.UPDATE_LOWER_VERSIONS_ONLY;
		}

		final String fatalErrMsg = "Unknown component version handling rule '"
				+ rule + "' detected.";
		throw new IllegalStateException(fatalErrMsg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.DeployProcessor#getErrorHandlingRule()
	 */
	public ErrorHandlingRule getErrorHandlingRule() throws RemoteException {
		return this.errorHandlingRule;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.api.remote.DeployProcessor#setErrorHandlingRule(com.sap.sdm
	 * .api.remote.ErrorHandlingRule)
	 */
	public void setErrorHandlingRule(ErrorHandlingRule rule)
			throws RemoteException {
		this.errorHandlingRule = rule;
	}

	private com.sap.engine.services.dc.api.ErrorStrategy mapErrorHandlingRule(
			ErrorHandlingRule rule) throws RemoteException {
		if (rule.getRuleAsInt() == ErrorHandlingRules.ON_ERROR_STOP) {
			return com.sap.engine.services.dc.api.ErrorStrategy.ON_ERROR_STOP;
		} else if (rule.getRuleAsInt() == ErrorHandlingRules.ON_ERROR_SKIP_DEPENDING) {
			return com.sap.engine.services.dc.api.ErrorStrategy.ON_ERROR_SKIP_DEPENDING;
		} else if (rule.getRuleAsInt() == ErrorHandlingRules.ON_ERROR_IGNORE) {
			throw new UnsupportedOperationException(
					"The error handling rule is not supported: " + rule);
		}

		final String fatalErrMsg = "Unknown error handling rule '" + rule
				+ "' detected.";
		throw new IllegalStateException(fatalErrMsg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.api.remote.DeployProcessor#getPrerequisiteErrorHandlingRule()
	 */
	public PrerequisiteErrorHandlingRule getPrerequisiteErrorHandlingRule()
			throws RemoteException {
		return this.prerequisiteErrorHandlingRule;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.api.remote.DeployProcessor#setPrerequisiteErrorHandlingRule
	 * (com.sap.sdm.api.remote.PrerequisiteErrorHandlingRule)
	 */
	public void setPrerequisiteErrorHandlingRule(
			PrerequisiteErrorHandlingRule rule) throws RemoteException {
		this.prerequisiteErrorHandlingRule = rule;
	}

	private com.sap.engine.services.dc.api.ErrorStrategy mapPrerequisiteErrorHandlingRule(
			PrerequisiteErrorHandlingRule rule) throws RemoteException {
		if (rule.getRuleAsInt() == PrerequisiteErrorHandlingRules.ON_PREREQUISITE_ERROR_STOP) {
			return com.sap.engine.services.dc.api.ErrorStrategy.ON_ERROR_STOP;
		} else if (rule.getRuleAsInt() == PrerequisiteErrorHandlingRules.ON_PREREQUISITE_ERROR_SKIP_DEPENDING) {
			return com.sap.engine.services.dc.api.ErrorStrategy.ON_ERROR_SKIP_DEPENDING;
		}

		final String fatalErrMsg = "Unknown prerequisite error handling rule '"
				+ rule + "' detected.";
		throw new IllegalStateException(fatalErrMsg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.api.remote.DeployProcessor#validate(com.sap.sdm.api.remote
	 * .DeployItem[])
	 */
	public ValidateResult validate(DeployItem[] deployItems)
			throws RemoteException {
		final com.sap.engine.services.dc.api.deploy.DeployProcessor deployProcessor;
		try {
			deployProcessor = this.dcClient.getComponentManager()
					.getDeployProcessor();
		} catch (ConnectionException apice) {
			throw new APIRemoteExceptionImpl(
					"Connection error occurred while getting a DeployProcessor "
							+ "from the Deploy Controller API.", apice);
		} catch (DeployException apie) {
			throw new APIRemoteExceptionImpl(
					"An error occurred while getting a DeployProcessor "
							+ "from the Deploy Controller API.", apie);
		}

		final com.sap.engine.services.dc.api.deploy.DeployItem[] dcDeployItems = buildDcDeployItems(
				deployProcessor, deployItems);
		com.sap.engine.services.dc.api.deploy.ComponentVersionHandlingRule versionRule = mapComponentVersionHandlingRule(this.componentVersionHandlingRule);
		com.sap.engine.services.dc.api.ErrorStrategy errorStrategy = mapErrorHandlingRule(this.errorHandlingRule);
		com.sap.engine.services.dc.api.ErrorStrategy prerequisiteErrorStrategy = mapPrerequisiteErrorHandlingRule(this.prerequisiteErrorHandlingRule);

		deployProcessor.setComponentVersionHandlingRule(versionRule);
		deployProcessor
				.setErrorStrategy(
						com.sap.engine.services.dc.api.ErrorStrategyAction.DEPLOYMENT_ACTION,
						errorStrategy);
		// for the SDM the default value for validate() on prerequisite check is
		// on error skip depending
		deployProcessor
				.setErrorStrategy(
						com.sap.engine.services.dc.api.ErrorStrategyAction.PREREQUISITES_CHECK_ACTION,
						com.sap.engine.services.dc.api.ErrorStrategy.ON_ERROR_SKIP_DEPENDING);

		ValidationResult dcValidationResult = null;
		// com.sap.engine.services.dc.api.deploy.DeployItem[] excItems = null;
		try {
			dcValidationResult = deployProcessor.validate(dcDeployItems);
		} catch (ConnectionException apice) {
			throw new APIRemoteExceptionImpl(
					"Connection error occurred while performing validation "
							+ "via the Deploy Controller API.", apice);
		} catch (ValidationException ve) {
			return buildDamagedItemsValidateResult(deployItems
			// , dcValidationResult
			);
		} catch (TransportException te) {
			throw new APIRemoteExceptionImpl(
					"An error occurred while transporting "
							+ "the specified archives via the Deploy "
							+ "Controller API to the SAP Application Server Java.",
					te);
		} catch (APIException ae) {
			throw new APIRemoteExceptionImpl(
					"An error occurred via the Deploy "
							+ "Controller API to the SAP Application Server Java.",
					ae);
		}

		return buildP4ValidateResultImpl(dcValidationResult);
	}

	/**
	 * @param aDcValidationResult
	 * @return
	 * @throws RemoteException
	 */
	private ValidateResult buildP4ValidateResultImpl(
			ValidationResult dcValidationResult) throws RemoteException {
		P4ValidateResultImpl p4ValidateResultImpl = new P4ValidateResultImpl(
				dcValidationResult);
		p4ValidateResultImpl.setDeploymentBatchItems(dcValidationResult
				.getDeploymentBatchItems());
		p4ValidateResultImpl.setSortedDeploymentBatchItems(dcValidationResult
				.getSortedDeploymentBatchItems());
		return p4ValidateResultImpl;
	}

	private com.sap.engine.services.dc.api.deploy.DeployItem[] buildDcDeployItems(
			com.sap.engine.services.dc.api.deploy.DeployProcessor deployProcessor,
			DeployItem[] deployItems) {

		if (deployItems == null) {
			return null;
		}

		final com.sap.engine.services.dc.api.deploy.DeployItem[] dcDeployItems = new com.sap.engine.services.dc.api.deploy.DeployItem[deployItems.length];

		for (int i = 0; i < deployItems.length; i++) {
			com.sap.engine.services.dc.api.deploy.DeployItem dcDeployItem = deployProcessor
					.createDeployItem(((P4DeployItemImpl) deployItems[i])
							.getFile().getAbsolutePath());

			dcDeployItems[i] = dcDeployItem;
		}
		return dcDeployItems;
	}

	private ValidateResult buildDamagedItemsValidateResult(
			DeployItem[] deployItems
	// ,ValidationResult dcValidationResult
	) throws RemoteException {

		P4ValidateResultImpl p4ValidateResultImpl = new P4ValidateResultImpl();

		DeployItem[] rezDeployItem = new DeployItem[deployItems.length];

		for (int i = 0; i < deployItems.length; i++) {
			// DeployItem deployItem = deployItems[i];
			P4DeployItemImpl p4DeployItemImpl = new P4DeployItemImpl();
			p4DeployItemImpl.setDeployResult(new P4DeployResultImpl(
					new PreconditionViolated() {
					}, ""));

			rezDeployItem[i] = p4DeployItemImpl;
		}
		p4ValidateResultImpl.setDeploymentBatchItems(rezDeployItem);
		return p4ValidateResultImpl;
	}
}
