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
package com.sap.engine.services.dc.cm.validate.impl;

import static com.sap.engine.services.dc.cm.utils.ResultUtils.logSummary4Validate;
import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.sap.engine.services.accounting.Accounting;
import com.sap.engine.services.accounting.measurement.AMeasurement;
import com.sap.engine.services.dc.cm.ErrorStrategy;
import com.sap.engine.services.dc.cm.ErrorStrategyAction;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentStatus;
import com.sap.engine.services.dc.cm.deploy.ValidationException;
import com.sap.engine.services.dc.cm.deploy.ValidationResult;
import com.sap.engine.services.dc.cm.deploy.ValidationStatus;
import com.sap.engine.services.dc.cm.deploy.impl.DeployerBase;
import com.sap.engine.services.dc.cm.deploy.impl.DeploymentArchivesCleaner;
import com.sap.engine.services.dc.cm.deploy.impl.DeploymentBatchFilterProcessor;
import com.sap.engine.services.dc.cm.deploy.impl.DeploymentBatchValidator;
import com.sap.engine.services.dc.cm.server.spi.SoftwareTypeService;
import com.sap.engine.services.dc.cm.undeploy.GenericUndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployItemStatus;
import com.sap.engine.services.dc.cm.undeploy.UndeployListenersList;
import com.sap.engine.services.dc.cm.undeploy.UndeployParallelismStrategy;
import com.sap.engine.services.dc.cm.undeploy.UndeploymentBatch;
import com.sap.engine.services.dc.cm.undeploy.UndeploymentData;
import com.sap.engine.services.dc.cm.undeploy.UndeploymentException;
import com.sap.engine.services.dc.cm.undeploy.UndeploymentHelperFactory;
import com.sap.engine.services.dc.cm.undeploy.UndeploymentObserver;
import com.sap.engine.services.dc.cm.undeploy.UndeploymentProcessor;
import com.sap.engine.services.dc.cm.undeploy.UndeploymentProcessorFactory;
import com.sap.engine.services.dc.cm.undeploy.impl.ErrorStrategies;
import com.sap.engine.services.dc.cm.undeploy.impl.UndeployItemInitializer;
import com.sap.engine.services.dc.cm.undeploy.impl.UndeploymentBatchImpl;
import com.sap.engine.services.dc.cm.undeploy.impl.sorters.UndeployItemsSorter;
import com.sap.engine.services.dc.cm.undeploy.impl.sorters.UndeployItemsSorterFactory;
import com.sap.engine.services.dc.cm.utils.filters.BatchFilter;
import com.sap.engine.services.dc.cm.utils.measurement.DataMeasurements;
import com.sap.engine.services.dc.cm.utils.measurement.MeasurementUtils;
import com.sap.engine.services.dc.cm.validate.DeployValidationBatch;
import com.sap.engine.services.dc.cm.validate.DeployValidationBatchResult;
import com.sap.engine.services.dc.cm.validate.UndeployValidationBatch;
import com.sap.engine.services.dc.cm.validate.UndeployValidationBatchResult;
import com.sap.engine.services.dc.cm.validate.ValidateResult;
import com.sap.engine.services.dc.cm.validate.ValidationBatch;
import com.sap.engine.services.dc.cm.validate.ValidationBatchResult;
import com.sap.engine.services.dc.cm.validate.Validator;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.repo.DeploymentsContainer;
import com.sap.engine.services.dc.repo.RepositoryContainer;
import com.sap.engine.services.dc.repo.RepositoryException;
import com.sap.engine.services.dc.util.CallerInfo;
import com.sap.engine.services.dc.util.StringUtils;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.tc.logging.Location;

/**
 * Date: Dec 13, 2007
 * 
 * @author Todor Atanasov(i043963)
 */
public class ValidatorImpl implements Validator {
	
	private Location location = DCLog.getLocation(this.getClass());

	private SoftwareTypeService softwareTypeService = null;
	private final String performerUserUniqueId;

	public ValidatorImpl(final String performerUserUniqueId) {
		this.performerUserUniqueId = performerUserUniqueId;
	}

	public ValidateResult validate(final ValidationBatch[] batchList,
			final String sessionId) throws ValidationException {

		final long begin = System.currentTimeMillis();
		if (location.bePath()) {
			tracePath(location, "Starting batch validation ...");
		}

		DeployerBase.checkForAvailablility();

		DeployerBase.doCheckSessionId(sessionId);

		try {
			// make a copy of DeploymentsContainer and set it as the one to be
			// used from now on.
			DeploymentsContainer deploymentsContainer = RepositoryContainer
					.cloneDeploymentsContainer(RepositoryContainer
							.getDeploymentsContainer());
			RepositoryContainer
					.setThreadDeploymentsContainer(deploymentsContainer);

			ValidationBatchResult[] batchResults = new ValidationBatchResult[batchList.length];
			int i = 0;
			softwareTypeService = null;
			for (ValidationBatch batchItem : batchList) {
				if (batchItem instanceof DeployValidationBatch) {
					batchResults[i] = validateDeploymentBatch(
							(DeployValidationBatch) batchItem, sessionId);
					// apply admitted deploy items to container
					applyDeploymentAdmitted(deploymentsContainer,
							((DeployValidationBatchResult) batchResults[i])
									.getSortedDeploymentBatchItems());
				} else if (batchItem instanceof UndeployValidationBatch) {
					batchResults[i] = validateUndeploymentBatch(
							(UndeployValidationBatch) batchItem, sessionId);
					// apply admitted undeploy items to container
					applyUndeploymentAdmitted(deploymentsContainer,
							((UndeployValidationBatchResult) batchResults[i])
									.getOrderedUndeployItems());
				}
				++i;
			}

			// trace result
			DCLog.logInfo(location, "ASJ.dpl_dc.006404", "{0}",
					new Object[] { logSummary4Validate(batchResults) });

			return new ValidateResultImpl(batchResults);
		} catch (RepositoryException re) {
			throw new ValidationException(re.getMessage(), re);
		} finally {
			RepositoryContainer.setThreadDeploymentsContainer(null);
			DeploymentArchivesCleaner.getInstance().clean(sessionId);

			if (location.bePath()) {
				tracePath(location, "Batch validation took [{0}] ms",
						new Object[] { new Long(System.currentTimeMillis() - begin) });
			}
		}
	}

	public DeployValidationBatch createDeployBatch(String[] deployItemPaths,
			BatchFilter[] batchFilters) {
		return new DeployValidationBatchImpl(deployItemPaths, batchFilters);
	}

	public UndeployValidationBatch createUndeployBatch(
			GenericUndeployItem[] undeployItems) {
		UndeployValidationBatchImpl undeployValidationBatch = new UndeployValidationBatchImpl(
				undeployItems);
		if (ServiceConfigurer.getInstance().getUndeployWorkflowStrategy() != null) {
			undeployValidationBatch
					.setUndeployWorkflowStrategy(ServiceConfigurer
							.getInstance().getUndeployWorkflowStrategy());
		}
		return undeployValidationBatch;
	}

	private void applyDeploymentAdmitted(
			DeploymentsContainer deploymentsContainer, Collection items) {
		for (Object item : items) {
			DeploymentBatchItem deploymentBatchItem = (DeploymentBatchItem) item;
			if (deploymentBatchItem.getDeploymentStatus().equals(
					DeploymentStatus.ADMITTED)) {
				deploymentsContainer
						.addDeployment(deploymentBatchItem.getSdu());
			}
		}
	}

	private void applyUndeploymentAdmitted(
			DeploymentsContainer deploymentsContainer, Collection items) {
		for (Object item : items) {
			GenericUndeployItem undeploymentItem = (GenericUndeployItem) item;
			if (undeploymentItem.getUndeployItemStatus().equals(
					UndeployItemStatus.ADMITTED)) {
				deploymentsContainer
						.removeDeployment(undeploymentItem.getSdu());
			}
		}
	}

	private DeployValidationBatchResult validateDeploymentBatch(
			DeployValidationBatch deployValidationBatch, String sessionId) {
		DeployerBase
				.doCheckArchivePaths(deployValidationBatch.getDeployItems());
		DeploymentBatchFilterProcessor batchFilterProcessor = new DeploymentBatchFilterProcessor();
		for (BatchFilter batchFilter : deployValidationBatch.getBatchFilters()) {
			batchFilterProcessor.addBatchFilter(batchFilter);
		}
		final DeploymentBatchValidator.ValidatorData validatorData = new DeploymentBatchValidator.ValidatorData(
				sessionId, deployValidationBatch.getDeployItems(),
				ErrorStrategy.ON_ERROR_SKIP_DEPENDING, batchFilterProcessor,
				deployValidationBatch.getComponentVersionHandlingRule(),
				deployValidationBatch.getDeployWorkflowStrategy());

		try {
			validatorData.setSoftwareTypeService(softwareTypeService);
			ValidationResult validationResult = DeploymentBatchValidator
					.getInstance().doValidate(validatorData);
			softwareTypeService = validatorData.getSoftwareTypeService();

			// store ValidationResult in new object
			return new DeployValidationBatchResultImpl(validationResult, null);
		} catch (ValidationException e) {
			DCLog.logErrorThrowable(location, e);
			return new DeployValidationBatchResultImpl(ValidationStatus.ERROR,
					false, e.getOrderedBatchItems(), e
							.getDeploymentBatchItems(), StringUtils
							.getCauseMessage(e));
		}
	}

	private UndeployValidationBatchResult validateUndeploymentBatch(
			UndeployValidationBatch undeployValidationBatch, String sessionId) {
		final String tagName = "PreProcess";
		Accounting.beginMeasure(tagName, ValidatorImpl.class);

		final UndeploymentBatch undeploymentBatch = UndeploymentBatchImpl
				.createUndeploymentBatch(undeployValidationBatch
						.getUndeployItems());

		ErrorStrategies errorStrategies = ErrorStrategies.createInstance();
		final ErrorStrategy prerequisitesErrorStrategy = errorStrategies
				.getErrorStrategy(ErrorStrategyAction.PREREQUISITES_CHECK_ACTION);

		try {
			if (location.bePath()) {
				tracePath(location, "Start undeployment validation checks ...");
			}
			UndeployItemInitializer.getInstance().init(undeploymentBatch,
					prerequisitesErrorStrategy);

			final UndeployItemsSorter sorter = UndeployItemsSorterFactory
					.getInstance().createUndeploymentsSorter();
			final List<GenericUndeployItem> sortedUndeployItems = sorter
					.sort(undeploymentBatch);
			undeploymentBatch.addOrderedUndeployItems(sortedUndeployItems);

			final ErrorStrategy undeployErrorStrategy = errorStrategies
					.getErrorStrategy(ErrorStrategyAction.UNDEPLOYMENT_ACTION);
			UndeployParallelismStrategy undeployParallelismStrategy = ServiceConfigurer
					.getInstance().getUndeployParallelismStrategy();
			if (undeployParallelismStrategy == null) {
				undeployParallelismStrategy = UndeployParallelismStrategy.NORMAL;
			}

			final AMeasurement measurement = Accounting.endMeasure(tagName);
			final DataMeasurements dataMeasurements = new DataMeasurements();
			dataMeasurements.setPrePhaseMeasurement(MeasurementUtils
					.map(measurement));

			// TODO - why is UndeploymentData used here?
			final UndeploymentData undeploymentData = UndeploymentHelperFactory
					.getInstance().createUndeploymentData(
							sortedUndeployItems,
							undeploymentBatch,
							sessionId,
							new ArrayList<UndeploymentObserver>(),
							undeployErrorStrategy,
							undeployValidationBatch.getUndeploymentStrategy(),
							undeployValidationBatch
									.getUndeployWorkflowStrategy(),
							undeployParallelismStrategy,
							UndeployListenersList.createInstance(),
							dataMeasurements, performerUserUniqueId,
							CallerInfo.getHost());

			final UndeploymentProcessor undeploymentProcessor = UndeploymentProcessorFactory
					.getInstance()
					.createUndeploymentProcessor(undeploymentData);

			return undeploymentProcessor.validate(undeploymentBatch);
		} catch (UndeploymentException ue) {
			DCLog.logErrorThrowable(location, ue);
			return new UndeployValidationBatchResultImpl(ue
					.getOrderedUndeployItems(), ue.getUndeployItems(),
					StringUtils.getCauseMessage(ue));
		}
	}

	public UndeployValidationBatch createUndeployBatch(
			UndeployItem[] undeployItems) {
		return createUndeployBatch((GenericUndeployItem[]) undeployItems);
	}
}
