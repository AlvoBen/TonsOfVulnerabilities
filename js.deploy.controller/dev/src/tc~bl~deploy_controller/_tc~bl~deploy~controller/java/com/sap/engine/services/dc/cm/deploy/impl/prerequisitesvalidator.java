package com.sap.engine.services.dc.cm.deploy.impl;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.sap.engine.services.accounting.Accounting;
import com.sap.engine.services.dc.cm.ErrorStrategy;
import com.sap.engine.services.dc.cm.deploy.AllItemsAlreadyDeployedValidationException;
import com.sap.engine.services.dc.cm.deploy.AllItemsFilteredValidaionException;
import com.sap.engine.services.dc.cm.deploy.ComponentVersionHandlingRule;
import com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem;
import com.sap.engine.services.dc.cm.deploy.CrcValidationException;
import com.sap.engine.services.dc.cm.deploy.DeployWorkflowStrategy;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatch;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentStatus;
import com.sap.engine.services.dc.cm.deploy.ValidationException;
import com.sap.engine.services.dc.cm.server.spi.SoftwareTypeService;
import com.sap.engine.services.dc.cm.utils.statistics.TimeStatisticsEntry;
import com.sap.engine.services.dc.util.Constants;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-8-17
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class PrerequisitesValidator {
	private  final Location location = DCLog.getLocation(this.getClass());

	private final DeployWorkflowStrategy workflowStrategy;
	private final ErrorStrategy errorStrategy;
	private SoftwareTypeService softwareTypeService;
	private final VersionVisitor versionVisitor;

	PrerequisitesValidator(DeployWorkflowStrategy workflowStrategy,
			ComponentVersionHandlingRule versionRule,
			ErrorStrategy errorStrategy, SoftwareTypeService softwareTypeService) {

		this.workflowStrategy = workflowStrategy;
		this.errorStrategy = errorStrategy;
		this.softwareTypeService = softwareTypeService;
		versionVisitor = new VersionVisitor(versionRule);
	}

	void doValidate(DeploymentBatch deploymentBatch) throws ValidationException {
		if (location.bePath()) {
			tracePath(location, "Apply prerequisites checks ...");
		}

		doDCUpdateCheck(deploymentBatch);
		checkDeploymentBatch(deploymentBatch);
		applyTheErrorStrategyAfterCheck(deploymentBatch);

		if (location.bePath()) {
			tracePath(location, "Prerequisites checks ended.");
		}
	}

	private void checkDeploymentBatch(DeploymentBatch deploymentBatch)
			throws ValidationException {

		final SoftwareTypeCheckVisitor softwareTypeCheckVisitor = new SoftwareTypeCheckVisitor(
				this.softwareTypeService);
		final StructureCheckVisitor structureCheckVisitor = new StructureCheckVisitor();
		final WorkflowStrategy2SWTypeCheckVisitor workflow2swtypeVisitor = new WorkflowStrategy2SWTypeCheckVisitor(
				this.workflowStrategy);
		final EngineMode2SWTypeVisitor modeVisitor = new EngineMode2SWTypeVisitor();
		final MultipleEqSdusCheckVisitor multipleEqSdusCheckVisitor = new MultipleEqSdusCheckVisitor();

		final Collection<DeploymentBatchItem> deploymentBatchItems = deploymentBatch
				.getDeploymentBatchItems();

		for (final DeploymentBatchItem deploymentBatchItem: deploymentBatchItems) {
			
			if (DeploymentStatus.ALREADY_DEPLOYED.equals(deploymentBatchItem
					.getDeploymentStatus()) ||
				DeploymentStatus.ABORTED.equals(deploymentBatchItem
							.getDeploymentStatus()) ||
				DeploymentStatus.FILTERED.equals(deploymentBatchItem
							.getDeploymentStatus())
			) {
				continue;
			}

			deploymentBatchItem.startTimeStatEntry("validate",
					TimeStatisticsEntry.ENTRY_TYPE_VALIDATE);
			final String tagName = "validate:"+deploymentBatchItem.getSdu().getId();
			Accounting.beginMeasure(tagName, PrerequisitesValidator.class);
			try {

				// init the version and apply the version handling rule
				// if the version resolving is done successfully, the deployment
				// item status
				// is set to ADMITTED
				deploymentBatchItem.accept(versionVisitor);

				// see if the sw type is supported
				if (DeploymentStatus.ADMITTED.equals(deploymentBatchItem
						.getDeploymentStatus())) {
					deploymentBatchItem.accept(softwareTypeCheckVisitor);
				}

				// check for incompatible structure changes
				if (DeploymentStatus.ADMITTED.equals(deploymentBatchItem
						.getDeploymentStatus())) {
					deploymentBatchItem.accept(structureCheckVisitor);
				}

				// check if the item can be deployed with the current workflow
				if (DeploymentStatus.ADMITTED.equals(deploymentBatchItem
						.getDeploymentStatus())) {
					deploymentBatchItem.accept(workflow2swtypeVisitor);
				}

				// check if the item could be deployed in the current engine
				// mode
				if (DeploymentStatus.ADMITTED.equals(deploymentBatchItem
						.getDeploymentStatus())) {
					deploymentBatchItem.accept(modeVisitor);
				}

				// check for repeated
				deploymentBatchItem.accept(multipleEqSdusCheckVisitor);

			} finally {
				Accounting.endMeasure(tagName);
				deploymentBatchItem.finishTimeStatEntry();
			}
		}

		if (!deploymentBatchItems.isEmpty()) {
			if (deploymentBatchItems.size() == versionVisitor
					.getAlreadyDeployedBatchItemsSize()) {
				throw new AllItemsAlreadyDeployedValidationException(
						"ASJ.dpl_dc.003456 All batch items are marked as AlreadyDeployed "
								+ "because of Version check.");
			} else if (deploymentBatchItems.size() == versionVisitor
					.getCRCAlreadyDeployedBatchItemsSize()) {
				CrcValidationException cve = new CrcValidationException(
						"All batch items are marked as AlreadyDeployed "
								+ "because of CRC check.");
				cve.setMessageID("ASJ.dpl_dc.003083");
				throw cve;
			} else if (deploymentBatchItems.size() == (versionVisitor
					.getAlreadyDeployedBatchItemsSize() + versionVisitor
					.getCRCAlreadyDeployedBatchItemsSize())) {
				AllItemsAlreadyDeployedValidationException aiad = new AllItemsAlreadyDeployedValidationException(
						"All batch items are marked as AlreadyDeployed because of CRC and version check");
				aiad.setMessageID("ASJ.dpl_dc.003082");
				throw aiad;
			}
		}

		// removeRepeatedItemsFromBatch(deploymentBatch,
		// multipleEqSdusCheckVisitor);
	}

	void applyTheErrorStrategyAfterCheck(DeploymentBatch deploymentBatch)
			throws ValidationException {
		final Collection deploymentBatchItems = deploymentBatch
				.getDeploymentBatchItems();
		boolean isThereAdmittedDeploymentItem = false;
		final Collection erroneousDeploymentBatchItems = new ArrayList();
		isThereAdmittedDeploymentItem = isThereAdmittedInBatch(
				deploymentBatchItems, erroneousDeploymentBatchItems);

		if (!isThereAdmittedDeploymentItem) {
			final StringBuffer sbErrText = new StringBuffer(256).append(
					"There are no deployment items admitted for deployment.")
					.append(Constants.EOL);
			int idx = 1;
			for (Iterator iter = deploymentBatchItems.iterator(); iter
					.hasNext();) {
				final DeploymentBatchItem depltBatchItem = (DeploymentBatchItem) iter
						.next();
				sbErrText.append(idx++).append(depltBatchItem).append(
						Constants.EOL).append(Constants.EOL);
				if (depltBatchItem instanceof CompositeDeploymentItem) {
					final CompositeDeploymentItem compositeDeploymentItem = (CompositeDeploymentItem) depltBatchItem;
					final Collection deplItems = compositeDeploymentItem
							.getDeploymentItems();
					for (Iterator iterator = deplItems.iterator(); iterator
							.hasNext();) {
						final DeploymentItem deploymentItem = (DeploymentItem) iterator
								.next();
						sbErrText.append(Constants.TAB).append(idx++).append(
								deploymentItem).append(Constants.EOL);
					}
				}
			}

			throw new ValidationException("[ERROR CODE DPL.DC.3084] "
					+ sbErrText.toString());
		}

		if (this.errorStrategy.equals(ErrorStrategy.ON_ERROR_STOP)
				&& !erroneousDeploymentBatchItems.isEmpty()) {
			final StringBuffer sbErrText = new StringBuffer(512)
					.append(
							"An error occurred while checking the deployment items "
									+ "selected for deployment.")
					.append(Constants.EOL)
					.append("The following ")
					.append(erroneousDeploymentBatchItems.size())
					.append(
							" deployment items are not admitted for deployment: ")
					.append(Constants.EOL);
			int idx = 1;
			for (Iterator iter = erroneousDeploymentBatchItems.iterator(); iter
					.hasNext();) {
				final DeploymentBatchItem errDeploymentBatchItem = (DeploymentBatchItem) iter
						.next();
				sbErrText.append(idx++).append(errDeploymentBatchItem).append(
						Constants.EOL).append(Constants.EOL);
			}

			throw new ValidationException("[ERROR CODE DPL.DC.3085] "
					+ sbErrText.toString());
		}
	}

	// TODO: replace with inner class visitor
	private boolean isThereAdmittedInBatch(Collection deploymentBatchItems,
			Collection erroneousDeploymentBatchItems)
			throws AllItemsFilteredValidaionException {
		boolean isThereAdmittedDeploymentItem = false;
		boolean areAllFilteredDeploymentItems = !deploymentBatchItems.isEmpty();
		for (Iterator iter = deploymentBatchItems.iterator(); iter.hasNext();) {
			final DeploymentBatchItem deploymentBatchItem = (DeploymentBatchItem) iter
					.next();

			if (isDeploymentBatchItemAdmitted(deploymentBatchItem,
					erroneousDeploymentBatchItems)) {
				if (!isThereAdmittedDeploymentItem) {
					isThereAdmittedDeploymentItem = true;
					areAllFilteredDeploymentItems = false;
				}
			} else if (areAllFilteredDeploymentItems
					&& !DeploymentStatus.FILTERED.equals(deploymentBatchItem
							.getDeploymentStatus())) {
				areAllFilteredDeploymentItems = false;
			}

			// check the inner items regardless of the composite version
			if (deploymentBatchItem instanceof CompositeDeploymentItem) {
				final CompositeDeploymentItem compositeDeploymentItem = (CompositeDeploymentItem) deploymentBatchItem;
				final Collection deploymentItems = compositeDeploymentItem
						.getDeploymentItems();
				for (Iterator iterator = deploymentItems.iterator(); iterator
						.hasNext();) {
					final DeploymentItem deploymentItem = (DeploymentItem) iterator
							.next();

					if (isDeploymentBatchItemAdmitted(deploymentItem,
							erroneousDeploymentBatchItems)) {
						if (!isThereAdmittedDeploymentItem) {
							isThereAdmittedDeploymentItem = true;
							areAllFilteredDeploymentItems = false;
						}
					} else if (areAllFilteredDeploymentItems
							&& !DeploymentStatus.FILTERED.equals(deploymentItem
									.getDeploymentStatus())) {
						areAllFilteredDeploymentItems = false;
					}
				}
			}
		}

		if (areAllFilteredDeploymentItems) {
			AllItemsFilteredValidaionException aif = new AllItemsFilteredValidaionException(
					"All batch items are marked as Filtered "
							+ "because of applied batch filters.");
			aif.setMessageID("ASJ.dpl_dc.003086");
			throw aif;
		}

		return isThereAdmittedDeploymentItem;
	}

	private boolean isDeploymentBatchItemAdmitted(
			DeploymentBatchItem deploymentBatchItem,
			Collection erroneousDeploymentBatchItems) {
		DeploymentStatus deploymentStatus = deploymentBatchItem
				.getDeploymentStatus();
		if (DeploymentStatus.ADMITTED.equals(deploymentStatus)) {
			return true;
		} else if (DeploymentStatus.ALREADY_DEPLOYED.equals(deploymentStatus)) {
			// do nothing
		} else if (DeploymentStatus.FILTERED.equals(deploymentStatus)) {
			// do nothing
		} else if (DeploymentStatus.REPEATED.equals(deploymentStatus)) {
			// do nothing
		} else {
			// on this step of the process every other state
			// (different than ADMITTED, ALREADY_DEPLOYED, FILTERED & REPEATED)
			// is treated as an erroneous one
			erroneousDeploymentBatchItems.add(deploymentBatchItem);
		}

		return false;
	}

	/**
	 * Deletes from the deployment batch the items which refer to same SDA,
	 * therefore the deployment batch will contain only one item for a concrete
	 * SDA. It does not make sense to deploy same SDA more than one time per
	 * batch
	 */
	private void removeRepeatedItemsFromBatch(DeploymentBatch deploymentBatch,
			final MultipleEqSdusCheckVisitor multipleEqSdusCheckVisitor) {

		final Collection repeatedDeploymentItems = multipleEqSdusCheckVisitor
				.getRepeatedDeploymentItems();

		if (!repeatedDeploymentItems.isEmpty()) {
			final StringBuffer sbErrMsg = new StringBuffer();
			sbErrMsg.append(Constants.EOL);
			for (Iterator iter = repeatedDeploymentItems.iterator(); iter
					.hasNext();) {
				sbErrMsg.append(iter.next()).append(Constants.EOL);
			}

			DCLog
					.logWarning(location, 
							"ASJ.dpl_dc.001100",
							"The following batch items are going to be removed from the deployment batch, because SDUs to which they refer have already been added with another batch items to the deployment batch: [{0}]",
							new Object[] { sbErrMsg.toString() });

			deploymentBatch.removeDeploymentBatchItems(repeatedDeploymentItems);
		}
	}
	
	void doDCUpdateCheck(final DeploymentBatch deploymentBatch)
		throws ValidationException {
		final StructureCheckVisitor structureCheckVisitor = new StructureCheckVisitor();
		final NewDeployControllerVisitor newDeployControllerVisitor = new NewDeployControllerVisitor();
		
		final Collection deploymentBatchItems = deploymentBatch
				.getDeploymentBatchItems();
		
		for (Iterator iter = deploymentBatchItems.iterator(); iter.hasNext();) {
			final DeploymentBatchItem deploymentBatchItem = (DeploymentBatchItem) iter
					.next();
		
			if (DeploymentStatus.FILTERED.equals(deploymentBatchItem
					.getDeploymentStatus())
					|| DeploymentStatus.ABORTED.equals(deploymentBatchItem
							.getDeploymentStatus())) {
				continue;
			}
		
			deploymentBatchItem.accept(versionVisitor);
		
			// check for incompatible structure changes
			if (DeploymentStatus.ADMITTED.equals(deploymentBatchItem
					.getDeploymentStatus())) {
				deploymentBatchItem.accept(structureCheckVisitor);
			}
		
			// check if the item is new deploy controller
			if (DeploymentStatus.ADMITTED.equals(deploymentBatchItem
					.getDeploymentStatus())) {
				deploymentBatchItem.accept(newDeployControllerVisitor);
				try {
					SoftwareTypeService softwareTypeService = newDeployControllerVisitor
					.getNewDeployControllerSoftwareTypeService();
					if(softwareTypeService != null){
						this.softwareTypeService = softwareTypeService;
					}
					if (newDeployControllerVisitor
							.isDeployControllerItemVisited()) {
						return;
					}
				} catch (Exception exc) {
					throw new ValidationException(
							"Exception occured while extracting new meta info from new deploy controller: ",
							exc);
				}
			}
		}
		// removeRepeatedItemsFromBatch(deploymentBatch,
		// multipleEqSdusCheckVisitor);
	
	}
	
	SoftwareTypeService getNewSoftwareTypeService(){
		return softwareTypeService;
	}
	
}
