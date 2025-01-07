package com.sap.engine.services.dc.cm.deploy.impl;

import java.util.Enumeration;
import java.util.Set;

import com.sap.engine.services.dc.cm.ErrorStrategy;
import com.sap.engine.services.dc.cm.deploy.BatchItemId;
import com.sap.engine.services.dc.cm.deploy.DeplEnumOnErrorSkipDepending;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentStatus;
import com.sap.engine.services.dc.cm.deploy.storage.DeplDataStorageException;
import com.sap.engine.services.dc.cm.deploy.storage.DeploymentDataStorageManager;
import com.sap.engine.services.dc.cm.server.spi.OnlineOfflineSoftwareType;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-4-5
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
final class PostDplOnErrSkipDepEnum extends DeplEnumOnErrorSkipDepending {

	private  final Location location = DCLog.getLocation(this.getClass());
	
	private final DeploymentDataStorageManager storageManager;
	private final String sessionId;
	private final OnlineOfflineSoftwareType onOffSoftwareType;

	public PostDplOnErrSkipDepEnum(Enumeration deploymentItemsEnum,
			Set acceptedStatuses, DeploymentDataStorageManager storageManager,
			String sessionId, OnlineOfflineSoftwareType onOffSoftwareType) {
		super(deploymentItemsEnum, acceptedStatuses);

		this.storageManager = storageManager;
		this.sessionId = sessionId;
		this.onOffSoftwareType = onOffSoftwareType;
	}

	protected void setSkipped(DeploymentBatchItem deplBatchItem,
			BatchItemId undeliveredBatchItemId) throws EnumRuntimeException {
		super.setSkipped(deplBatchItem, undeliveredBatchItemId);

		this.persistItem(deplBatchItem);
	}

	protected boolean isNext(DeploymentBatchItem deplBatchItem) {
		return PostEnumHelper.isNext(deplBatchItem, this.onOffSoftwareType);
	}

	private void persistItem(DeploymentBatchItem item)
			throws EnumRuntimeException {
		try {
			this.storageManager.persist(this.sessionId, item);
		} catch (DeplDataStorageException ddse) {
			final String errMessage = DCLog
					.buildExceptionMessage(
							"ASJ.dpl_dc.001089",
							"An error occurred while persisting the item [{0}]. As there is at least one item which was not deployed and because of the error handling strategy [{1}] the status of this item was set to [{2}].",
							new Object[] { item,
									ErrorStrategy.ON_ERROR_SKIP_DEPENDING,
									DeploymentStatus.SKIPPED });

			DCLog.logErrorThrowable(location, null, errMessage, ddse);

			throw new EnumRuntimeException(errMessage, ddse);
		}
	}

}
