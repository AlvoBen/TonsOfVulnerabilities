package com.sap.engine.services.dc.cm.deploy.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sap.engine.services.dc.cm.ErrorStrategy;
import com.sap.engine.services.dc.cm.deploy.BatchItemId;
import com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeployResult;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatch;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentStatus;
import com.sap.engine.services.dc.repo.ScaId;
import com.sap.engine.services.dc.repo.SdaId;
import com.sap.engine.services.dc.repo.SduId;
import com.sap.engine.services.dc.util.Constants;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-13
 * 
 * @author Ivan Mihalev
 * @version 1.0
 * @since 7.0
 * 
 */
final class DeploymentBatchAnalyzer {

	private static DeploymentBatchAnalyzer INSTANCE;

	static synchronized DeploymentBatchAnalyzer getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DeploymentBatchAnalyzer();
		}

		return INSTANCE;
	}

	private DeploymentBatchAnalyzer() {
	}

	/**
	 * @param deploymentBatch
	 */
	void analyseDeploymentBatch(ErrorStrategy errorStrategy,
			DeploymentBatch deploymentBatch) {
		if (errorStrategy.equals(ErrorStrategy.ON_ERROR_STOP)) {
			String parentDesc = "";
			Iterator iter = deploymentBatch.getDeploymentBatchItems()
					.iterator();
			boolean notFoundFailed = true;
			DeploymentBatchItem failedDeploymentItem = null;
			while (iter.hasNext() && notFoundFailed) {
				final DeploymentBatchItem deplBatchItem = (DeploymentBatchItem) iter
						.next();
				if (deplBatchItem.getDeploymentStatus().equals(
						DeploymentStatus.ABORTED)) {
					failedDeploymentItem = deplBatchItem;
					notFoundFailed = false;
				} else if (deplBatchItem instanceof CompositeDeploymentItem) {
					CompositeDeploymentItem compositeItem = (CompositeDeploymentItem) deplBatchItem;
					Iterator compositeItems = compositeItem
							.getDeploymentItems().iterator();
					while (compositeItems.hasNext() && notFoundFailed) {
						final DeploymentBatchItem deplItem = (DeploymentBatchItem) compositeItems
								.next();
						if (deplItem.getDeploymentStatus().equals(
								DeploymentStatus.ABORTED)) {
							failedDeploymentItem = deplItem;
							compositeItem
									.setDeploymentStatus(DeploymentStatus.ABORTED);
							compositeItem
									.addDescription("Contains Aborted deployment component: "
											+ Constants.EOL
											+ failedDeploymentItem
													.getBatchItemId());
							// failedDeploymentItem);
							StringBuffer parentDescBuf = new StringBuffer();
							parentDescBuf.append(
									"contained in software archive:").append(
									Constants.EOL).append("sdu id: ").append(
									compositeItem.getBatchItemId()).append(
									Constants.EOL);
							parentDesc = parentDescBuf.toString();
							notFoundFailed = false;
						}
					}
				}
			}

			if (failedDeploymentItem != null) {
				StringBuffer batchItemDescBuf = new StringBuffer();
				if (failedDeploymentItem instanceof DeploymentItem) {
					batchItemDescBuf.append("Aborted deployment archive:");
				} else {
					batchItemDescBuf.append("Aborted software archive:");
				}
				batchItemDescBuf.append(Constants.EOL).append("sdu id: ")
						.append(failedDeploymentItem.getBatchItemId()).append(
								Constants.EOL).append(parentDesc);
				String batchItemDesc = batchItemDescBuf.toString();
				iter = deploymentBatch.getAllAdmittedDeplItems().iterator();
				while (iter.hasNext()) {
					final DeploymentBatchItem batchItem = (DeploymentBatchItem) iter
							.next();
					batchItem.setDeploymentStatus(DeploymentStatus.SKIPPED);
					batchItem.addDescription(batchItemDesc);
				}
			}
		}
		final DeploymentBatchItemStatusGetter statusGetter = new DeploymentBatchItemStatusGetter();

		for (Iterator iter = deploymentBatch.getDeploymentBatchItems()
				.iterator(); iter.hasNext();) {
			final DeploymentBatchItem deplBatchItem = (DeploymentBatchItem) iter
					.next();
			deplBatchItem.accept(statusGetter);
		}

	}

}
