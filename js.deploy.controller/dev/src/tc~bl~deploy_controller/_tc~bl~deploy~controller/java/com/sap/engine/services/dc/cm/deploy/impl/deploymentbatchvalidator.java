package com.sap.engine.services.dc.cm.deploy.impl;

import java.util.Collection;
import java.util.Iterator;

import com.sap.engine.services.dc.cm.ErrorStrategy;
import com.sap.engine.services.dc.cm.deploy.ComponentVersionHandlingRule;
import com.sap.engine.services.dc.cm.deploy.DependenciesResolvingException;
import com.sap.engine.services.dc.cm.deploy.DeployFactory;
import com.sap.engine.services.dc.cm.deploy.DeployWorkflowStrategy;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatch;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentException;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.deploy.SduLoadingException;
import com.sap.engine.services.dc.cm.deploy.ValidationException;
import com.sap.engine.services.dc.cm.deploy.ValidationResult;
import com.sap.engine.services.dc.cm.deploy.ValidationStatus;
import com.sap.engine.services.dc.cm.deploy.impl.sorters.DeploymentBatchItemsSorterFactory;
import com.sap.engine.services.dc.cm.deploy.impl.sorters.DeploymentBatchSorter;
import com.sap.engine.services.dc.cm.deploy.impl.sorters.SortException;
import com.sap.engine.services.dc.cm.server.ServerFactory;
import com.sap.engine.services.dc.cm.server.spi.SoftwareTypeService;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-13
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public final class DeploymentBatchValidator {

	private static DeploymentBatchValidator INSTANCE;

	public static synchronized DeploymentBatchValidator getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DeploymentBatchValidator();
		}

		return INSTANCE;
	}

	private DeploymentBatchValidator() {
	}

	public ValidationResult doValidate(ValidatorData validatorData)
			throws ValidationException {
		final DeploymentBatch deploymentBatch;
		try {
			deploymentBatch = loadArchives(validatorData);
		} catch (SduLoadingException sle) {
			ValidationException ve = new ValidationException(
					"SDU loading exception occurred while "
							+ "validating the archives.", sle);
			ve.setMessageID("ASJ.dpl_dc.003049");
			throw ve;
		}

		Collection sortedItems = null;
		try {

			doCheckDeploymentBatch(deploymentBatch);

			validatorData.getBatchFilterProcessor().applyBatchFilters(
					deploymentBatch);
			

			final PrerequisitesValidator prerequisitesValidator = getPrerequisitesValidator(
					validatorData, getSoftwareTypeService(validatorData));
			prerequisitesValidator.doValidate(deploymentBatch);
			
			final SoftwareTypeService softwareTypeService = prerequisitesValidator.getNewSoftwareTypeService();
			validatorData.setSoftwareTypeService(softwareTypeService);


			try {
				Collection<DeploymentItem> sortedByDependency = resolveDeploymentBatch(
						deploymentBatch, validatorData.getErrorStrategy(),
						softwareTypeService);

				// all the sorted items are also admitted, therefore if the
				// following collection is not empty there should a physical
				// deployments performed
				sortedItems = sortDeploymentBatch(deploymentBatch,
						sortedByDependency, softwareTypeService);
			} catch (DependenciesResolvingException dre) {
				ValidationException ve = new ValidationException(
						"SDU dependencies resolving exception occurred while "
								+ "validating the archives.", dre);
				ve.setMessageID("ASJ.dpl_dc.003050");
				throw ve;			
			} catch (SortException se) {
				ValidationException ve = new ValidationException(
						"SDU sorting exception occurred while "
								+ "validating the archives.", se);
				ve.setMessageID("ASJ.dpl_dc.003051");
				throw ve;
			}

			final boolean offlinePhaseScheduled;
			try {
				offlinePhaseScheduled = isOfflinePhaseScheduled(sortedItems,
						softwareTypeService);
			} catch (DeploymentException de) {
				ValidationException ve = new ValidationException(
						"Exception occurred while checking whether "
								+ "an offline phase will be scheduled during the deployment.",
						de);
				ve.setMessageID("ASJ.dpl_dc.003052");
				throw ve;
			}

			final ValidationStatus validationStatus = ValidationStatusBuilder
					.getInstance().buildValidationStatus(deploymentBatch);

			return DeployFactory.getInstance().createValidationResult(
					validationStatus, offlinePhaseScheduled, sortedItems,
					deploymentBatch.getDeploymentBatchItems());

		} catch (ValidationException ve) {
			if (ve.getDeploymentBatchItems().isEmpty()) {
				DeploymentBatchAnalyzer.getInstance().analyseDeploymentBatch(
						validatorData.getErrorStrategy(), deploymentBatch);
				ve.addDeploymentBatchItems(sortedItems, deploymentBatch
						.getDeploymentBatchItems());
			}

			throw ve;
		}
	}

	private SoftwareTypeService getSoftwareTypeService(
			ValidatorData validatorData) {

		// return default
		return (validatorData.getSoftwareTypeService() != null) ? validatorData
				.getSoftwareTypeService()
				: (SoftwareTypeService) ServerFactory.getInstance()
						.createServer().getServerService(
								ServerFactory.getInstance()
										.createSoftwareTypeRequest());

	}

	boolean isOfflinePhaseScheduled(final Collection sortedItems)
			throws DeploymentException {
		return isOfflinePhaseScheduled(sortedItems, null);
	}

	boolean isOfflinePhaseScheduled(final Collection sortedItems,
			final SoftwareTypeService softwareTypeService)
			throws DeploymentException {
		if (!sortedItems.isEmpty()) {
			final DeployPhaseGetter deployPhaseGetter;
			if (softwareTypeService != null) {
				deployPhaseGetter = DeployPhaseGetter
						.createInstance(softwareTypeService);
			} else {
				deployPhaseGetter = DeployPhaseGetter.createInstance();
			}
			for (Iterator iter = sortedItems.iterator(); iter.hasNext();) {
				final DeploymentBatchItem item = (DeploymentBatchItem) iter
						.next();
				final DeployPhase deployPhase = deployPhaseGetter
						.getPhase(item);

				if (DeployPhase.OFFLINE.equals(deployPhase)) {
					return true;
				}
			}
		}

		return false;
	}

	private DeploymentBatch loadArchives(ValidatorData validatorData)
			throws SduLoadingException {
		final SduLoader sduLoader = new SduLoader(validatorData
				.getErrorStrategy());

		return sduLoader.load(validatorData.getSessionId(), validatorData
				.getArchives(), false);
	}

	private PrerequisitesValidator getPrerequisitesValidator(
			final ValidatorData validatorData,
			final SoftwareTypeService softwareTypeService) {
		return new PrerequisitesValidator(validatorData.getWorkflowStrategy(),
				validatorData.getVersionRule(), validatorData
						.getErrorStrategy(), softwareTypeService);

	}

	private void doCheckDeploymentBatch(DeploymentBatch deploymentBatch) {
		if (deploymentBatch == null) {
			throw new NullPointerException(
					"ASJ.dpl_dc.003053 The deployment batch could not be null.");
		}

		if (deploymentBatch.getDeploymentBatchItems() == null) {
			throw new NullPointerException(
					"ASJ.dpl_dc.003054 The deployment batch items could not be null.");
		}
	}

	private Collection<DeploymentItem> resolveDeploymentBatch(
			final DeploymentBatch deploymentBatch,
			final ErrorStrategy errorStrategy,
			final SoftwareTypeService softwareTypeService)
			throws DependenciesResolvingException {
		final DependencyResolver.ResolverData resolverData = new DependencyResolver.ResolverData(
				errorStrategy, deploymentBatch, softwareTypeService);

		return DependencyResolver.getInstance().resolve(resolverData);
	}

	private Collection sortDeploymentBatch(
			final DeploymentBatch deploymentBatch,
			final Collection<DeploymentItem> sortedByDependency,
			final SoftwareTypeService softwareTypeService) throws SortException {
		final DeploymentBatchSorter sorter = DeploymentBatchItemsSorterFactory
				.getInstance().createDependencyAndSoftwareTypeSorter(
						softwareTypeService);

		return sorter.sort(deploymentBatch, sortedByDependency);
	}

	public static final class ValidatorData {
		private final String sessionId;
		private final String[] archives;
		private final ErrorStrategy errorStrategy;
		private final DeploymentBatchFilterProcessor batchFilterProcessor;
		private final ComponentVersionHandlingRule versionRule;
		private final DeployWorkflowStrategy workflowStrategy;
		private SoftwareTypeService softwareTypeService = null;

		public ValidatorData(String sessionId, String[] archivePaths,
				ErrorStrategy errStrategy,
				DeploymentBatchFilterProcessor filterProcessor,
				ComponentVersionHandlingRule verRule,
				DeployWorkflowStrategy workflowStrategy) {
			this.sessionId = sessionId;
			this.archives = archivePaths;
			this.errorStrategy = errStrategy;
			this.batchFilterProcessor = filterProcessor;
			this.versionRule = verRule;
			this.workflowStrategy = workflowStrategy;
		}

		String getSessionId() {
			return this.sessionId;
		}

		String[] getArchives() {
			return this.archives;
		}

		ErrorStrategy getErrorStrategy() {
			return this.errorStrategy;
		}

		DeploymentBatchFilterProcessor getBatchFilterProcessor() {
			return this.batchFilterProcessor;
		}

		ComponentVersionHandlingRule getVersionRule() {
			return this.versionRule;
		}

		DeployWorkflowStrategy getWorkflowStrategy() {
			return this.workflowStrategy;
		}

		public SoftwareTypeService getSoftwareTypeService() {
			return this.softwareTypeService;
		}

		public void setSoftwareTypeService(
				SoftwareTypeService softwareTypeService) {
			this.softwareTypeService = softwareTypeService;
		}

	}

}
