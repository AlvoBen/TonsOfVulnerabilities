/*
 * Created on 2005-9-10 by radoslav-i
 */
package com.sap.engine.services.dc.cm.deploy.impl;

import java.util.Collection;
import java.util.Iterator;

import com.sap.engine.services.dc.cm.ErrorStrategy;
import com.sap.engine.services.dc.cm.deploy.BatchItemId;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentData;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.utils.statistics.TimeStatisticsEntry;
import com.sap.engine.services.dc.repo.RepositoryException;
import com.sap.engine.services.dc.repo.RepositoryFactory;
import com.sap.engine.services.dc.repo.Sca;
import com.sap.engine.services.dc.repo.SdaId;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;

/**
 * Postprocess deployment item after delivery error.
 * 
 * @author radoslav-i
 */
final class OnDeliveryErrorProcessor {

	private final DeploymentData deploymentData;

	OnDeliveryErrorProcessor(final DeploymentData deploymentData) {
		this.deploymentData = deploymentData;
	}

	/**
	 * Postprocess deployment item after delivery error. If it is a part of sca
	 * and any item from sca was deployed before the error item, then sca will
	 * be registered in repository only with deployed items as sdaIds.
	 * 
	 * @param errorDeploymentItem
	 *            error deployment item
	 * @throws RepositoryException
	 *             if there are problems while persisting the sca that consist
	 *             error deployment item
	 */
	void doProcess(DeploymentItem errorDeploymentItem)
			throws RepositoryException {

		if (!needScaProcession(errorDeploymentItem)) {
			return;
		}

		errorDeploymentItem.startTimeStatEntry("On delivery error",
				TimeStatisticsEntry.ENTRY_TYPE_OTHER);
		try {

			boolean hasDeployedSdasFromSameSca = false;
			boolean isPassedCurrentItem = false;// pointer for passed current
			// item
			Sca sca = getSca(errorDeploymentItem.getParentId());
			String scaAbsoluteFilePath = null;

			Collection sortedDeploymentBatchItem = this.deploymentData
					.getSortedDeploymentBatchItem();
			BatchItemId parentScaId = errorDeploymentItem.getParentId();
			Iterator iSortedDeploymentBatchItem = sortedDeploymentBatchItem
					.iterator();

			while (iSortedDeploymentBatchItem.hasNext()) {
				DeploymentBatchItem sortedDeploymetItem = (DeploymentBatchItem) iSortedDeploymentBatchItem
						.next();
				if (isPassedCurrentItem && !hasDeployedSdasFromSameSca) {
					// then do not need procession because it is first item from
					// sca
					return;
				} else if (errorDeploymentItem.equals(sortedDeploymetItem)) { // current
					// item
					// is
					// pointed
					sca.removeSdaId((SdaId) sortedDeploymetItem.getSdu()
							.getId());
					isPassedCurrentItem = true;
				} else if (// item from same sca
				sortedDeploymetItem instanceof DeploymentItem
						&& parentScaId
								.equals(((DeploymentItem) sortedDeploymetItem)
										.getParentId())) {
					if (isPassedCurrentItem) {// is not deployed then remove
						// sdaId from sca
						sca.removeSdaId((SdaId) sortedDeploymetItem.getSdu()
								.getId());
					} else {// otherwise
						hasDeployedSdasFromSameSca = true;
					}
				} else if (sortedDeploymetItem.getBatchItemId().equals(
						parentScaId)) {
					scaAbsoluteFilePath = sortedDeploymetItem.getSduFilePath();
					break;// there is no other item from sca
				}
			}

			// persist
			persistSca(sca, scaAbsoluteFilePath);
		} finally {
			errorDeploymentItem.finishTimeStatEntry();
		}
	}

	private void persistSca(Sca sca, String scaAbsoluteFilePath)
			throws RepositoryException {
		try {
			RepositoryFactory.getInstance().createRepository().persistSdu(sca,
					scaAbsoluteFilePath);
		} catch (RepositoryException re) {
			throw new RepositoryException(
					DCExceptionConstants.REPO_ERROR_WHILE_PERSISTING_SCA, re);
		}
	}

	private Sca getSca(final BatchItemId parentScaId) {

		Collection sortedDeploymentBatchItem = this.deploymentData
				.getSortedDeploymentBatchItem();
		Iterator iSortedDeploymentBatchItem = sortedDeploymentBatchItem
				.iterator();

		while (iSortedDeploymentBatchItem.hasNext()) {
			DeploymentBatchItem sortedDeploymetItem = (DeploymentBatchItem) iSortedDeploymentBatchItem
					.next();
			if (sortedDeploymetItem.getBatchItemId().equals(parentScaId)) {
				return (Sca) sortedDeploymetItem.getSdu();
			}
		}

		throw new IllegalStateException(
				"ASJ.dpl_dc.003073 An error occurred with sorted items - The composite  item cannot be looked up in sorted items.");
	}

	private boolean needScaProcession(DeploymentItem errorDeploymentItem) {
		// 1.check strategy for ON_ERROR_STOP
		if (!ErrorStrategy.ON_ERROR_STOP.equals(this.deploymentData
				.getDeploymentErrorStrategy())) {
			return false;
		}

		// 2.sda has to be part of sca
		if (errorDeploymentItem.getParentId() == null) {
			return false;
		}

		return true;
	}
}