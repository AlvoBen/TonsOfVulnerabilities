package com.sap.engine.services.dc.cm.deploy.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.sap.engine.services.accounting.Accounting;
import com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentStatus;
import com.sap.engine.services.dc.cm.deploy.VersionStatus;
import com.sap.engine.services.dc.cm.utils.statistics.TimeStatisticsEntry;
import com.sap.engine.services.dc.repo.RepositoryContainer;
import com.sap.engine.services.dc.repo.Sda;
import com.sap.engine.services.dc.repo.Sdu;
import com.sap.engine.services.dc.util.Constants;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-7-27
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class StructureCheckVisitor extends ValidationVisitor {

	StructureCheckVisitor() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor#visit
	 * (com.sap.engine.services.dc.cm.deploy.DeploymentItem)
	 */
	public void visit(DeploymentItem deploymentItem) {
		if (VersionStatus.NOT_RESOLVED
				.equals(deploymentItem.getVersionStatus())
				|| VersionStatus.NEW.equals(deploymentItem.getVersionStatus())) {
			return;
		}

		doStructureCheck(deploymentItem);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor#visit
	 * (com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem)
	 */
	public void visit(CompositeDeploymentItem compositeDeploymentItem) {
		if (VersionStatus.NOT_RESOLVED.equals(compositeDeploymentItem
				.getVersionStatus())
				|| VersionStatus.NEW.equals(compositeDeploymentItem
						.getVersionStatus())) {
			return;
		}

		doStructureCheck(compositeDeploymentItem);

		checkCompositeInternalItemsStructure(compositeDeploymentItem);

		if (!isThereAvailabaleItems(compositeDeploymentItem)) {
			compositeDeploymentItem
					.setDeploymentStatus(DeploymentStatus.PREREQUISITE_VIOLATED);
		}

	}

	private void checkCompositeInternalItemsStructure(
			CompositeDeploymentItem compositeDeploymentItem) {

		compositeDeploymentItem.startTimeStatEntry(
				"Check contained Items Structure",
				TimeStatisticsEntry.ENTRY_TYPE_OTHER);
		final String tagName = "Check contained Items Structure" + compositeDeploymentItem.getSdu().getId();
		Accounting.beginMeasure(tagName, StructureCheckVisitor.class);
		try {
			final Set oldScaSdaIds = compositeDeploymentItem.getOldSca()
					.getOrigSdaIds();
			final Collection deploymentItems = compositeDeploymentItem
					.getDeploymentItems();
			for (Iterator iter = deploymentItems.iterator(); iter.hasNext();) {
				final DeploymentItem deploymentItem = (DeploymentItem) iter
						.next();

				if (deploymentItem.getVersionStatus()
						.equals(VersionStatus.SAME)
						&& deploymentItem.getDeploymentStatus().equals(
								DeploymentStatus.ADMITTED)) {

					if (oldScaSdaIds.contains(deploymentItem.getSda().getId())) {
						final Sda comparableSda = (Sda) RepositoryContainer
								.getDeploymentsContainer().getDeployment(
										deploymentItem.getSda().getId());

						if (comparableSda == null) {
							final String description = "The system did not find the Sda '"
									+ deploymentItem.getSda().getId()
									+ "' in its runtime repository.";
							compositeDeploymentItem
									.setDeploymentStatus(DeploymentStatus.PREREQUISITE_VIOLATED);
							compositeDeploymentItem.addDescription(description);
							deploymentItem
									.setDeploymentStatus(DeploymentStatus.PREREQUISITE_VIOLATED);
							deploymentItem.addDescription(description);
						} else {
							doStructureCheck(deploymentItem, comparableSda);
						}
					} else if (compositeDeploymentItem.getVersionStatus()
							.equals(VersionStatus.SAME)) {
						final String description = "The system has found an identical archive within its "
								+ "repository but with different structure. "
								+ Constants.EOL
								+ "The archive found within the repository is: "
								+ Constants.EOL
								+ "'"
								+ compositeDeploymentItem.getOldSca()
								+ "'."
								+ Constants.EOL
								+ "The archive which is selected for deployment is: "
								+ Constants.EOL
								+ "'"
								+ compositeDeploymentItem.getSca()
								+ "'. "
								+ Constants.EOL
								+ "The first one does not contain the SDA '"
								+ deploymentItem.getSda().getId() + "'.";
						compositeDeploymentItem
								.setDeploymentStatus(DeploymentStatus.PREREQUISITE_VIOLATED);
						compositeDeploymentItem.addDescription(description);
						deploymentItem
								.setDeploymentStatus(DeploymentStatus.PREREQUISITE_VIOLATED);
						deploymentItem.addDescription(description);
					}
				}
			}
		} finally {
			Accounting.endMeasure(tagName);
			compositeDeploymentItem.finishTimeStatEntry();
		}
	}

	// private Set getScaInternalSdaIds(Sca sca) {
	// final Set result = new HashSet();
	// final Set sdas = sca.getSdas();
	// for (Iterator iter = sdas.iterator(); iter.hasNext();) {
	// final Sda sda = (Sda) iter.next();
	// result.put(sda.getId(), sda);
	// }
	//    
	// return result;
	// }

	private void doStructureCheck(DeploymentBatchItem deploymentBatchItem) {
		doStructureCheck(deploymentBatchItem, deploymentBatchItem.getOldSdu());
	}

	private void doStructureCheck(DeploymentBatchItem deploymentBatchItem,
			Sdu compareSdu) {
		if (!deploymentBatchItem.getVersionStatus().equals(VersionStatus.SAME)) {
			return;
		}
		deploymentBatchItem.startTimeStatEntry("Structure Check:"
				+ deploymentBatchItem.getSdu().getId(),
				TimeStatisticsEntry.ENTRY_TYPE_OTHER);
		final String tagName = "Structure Check:" + deploymentBatchItem.getSdu().getId();
		Accounting.beginMeasure(tagName, StructureCheckVisitor.class);
		try {
			if (compareSdu == null) {
				deploymentBatchItem
						.setDeploymentStatus(DeploymentStatus.PREREQUISITE_VIOLATED);
				deploymentBatchItem
						.addDescription("The structure check could not be performed. "
								+ "The component which has been read from the repository "
								+ "is null!");
			} else {
				final Sdu batchSdu = deploymentBatchItem.getSdu();
				if (deploymentBatchItem.getSdu().getVersion().equals(
						compareSdu.getVersion())
						&& !batchSdu.equals(compareSdu)) {
					deploymentBatchItem
							.setDeploymentStatus(DeploymentStatus.PREREQUISITE_VIOLATED);
					deploymentBatchItem
							.addDescription("The system has found an identical archive within its "
									+ "repository but with different structure. "
									+ Constants.EOL
									+ "The archive found within the repository is: "
									+ Constants.EOL
									+ "'"
									+ compareSdu
									+ "'."
									+ "The archive which is selected for deployment is: "
									+ Constants.EOL + "'" + batchSdu + "'.");
				}
			}
		} finally {
			Accounting.endMeasure(tagName);
			deploymentBatchItem.finishTimeStatEntry();
		}
	}
}
