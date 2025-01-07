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

import java.util.Collection;
import java.util.Iterator;

import com.sap.engine.services.dc.cm.deploy.ValidationStatus;
import com.sap.engine.services.dc.cm.undeploy.GenericUndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployItemStatus;
import com.sap.engine.services.dc.cm.validate.UndeployValidationBatchResult;

/**
 * Date: Dec 13, 2007
 * 
 * @author Todor Atanasov(i043963)
 */
public class UndeployValidationBatchResultImpl implements
		UndeployValidationBatchResult {
	private static final long serialVersionUID = 1L;
	private final Collection undeploymentItems;
	private final Collection orderedUndeployItems;
	private final ValidationStatus undeployValidationStatus;
	private final String description;
	private boolean offlinePhaseScheduled = false;

	public UndeployValidationBatchResultImpl(Collection undeploymentItems,
			Collection orderedUndeployItems, String description) {
		if (undeploymentItems == null) {
			throw new NullPointerException(
					"[ERROR CODE DPL.DC.3413] The collection with the undeployment items could not be null.");
		}

		if (orderedUndeployItems == null) {
			throw new NullPointerException(
					"[ERROR CODE DPL.DC.3414] The collection with the ordered undeployment items could not be null.");
		}

		this.undeploymentItems = undeploymentItems;
		this.orderedUndeployItems = orderedUndeployItems;
		this.undeployValidationStatus = buildStatus();
        this.description = description == null ? "" : description;
	}

	public String getDescription() {
		return this.description;
	}

	public Collection getOrderedUndeployItems() {
		return this.orderedUndeployItems;
	}

	public Collection getUndeployItems() {
		return this.undeploymentItems;
	}

	public ValidationStatus getValidationStatus() {
		return this.undeployValidationStatus;
	}

	private ValidationStatus buildStatus() {
		boolean oneFinishedInital = false;
		boolean oneFinishedPV = false;
		boolean oneFinishedSkipped = false;
		boolean oneFinishedAborted = false;
		boolean oneFinishedAdmitted = false;
		boolean oneFinishedNotDeployed = false;
		boolean oneFinishedNotSupported = false;
		boolean oneFinishedOfflineAdmitted = false;
		boolean oneFinishedOfflineAborted = false;

		for (Iterator iter = this.getUndeployItems().iterator(); iter.hasNext();) {
			final GenericUndeployItem undeployItem = (GenericUndeployItem) iter.next();

			if (UndeployItemStatus.INITIAL.equals(undeployItem
					.getUndeployItemStatus())) {
				if (!oneFinishedInital) {
					oneFinishedInital = true;
				}
			} else if (UndeployItemStatus.PREREQUISITE_VIOLATED
					.equals(undeployItem.getUndeployItemStatus())) {
				if (!oneFinishedPV) {
					oneFinishedPV = true;
				}
			} else if (UndeployItemStatus.SKIPPED.equals(undeployItem
					.getUndeployItemStatus())) {
				if (!oneFinishedSkipped) {
					oneFinishedSkipped = true;
				}
			} else if (UndeployItemStatus.ABORTED.equals(undeployItem
					.getUndeployItemStatus())) {
				if (!oneFinishedAborted) {
					oneFinishedAborted = true;
				}
			} else if (UndeployItemStatus.ADMITTED.equals(undeployItem
					.getUndeployItemStatus())) {
				if (!oneFinishedAdmitted) {
					oneFinishedAdmitted = true;
				}
			} else if (UndeployItemStatus.NOT_DEPLOYED.equals(undeployItem
					.getUndeployItemStatus())) {
				if (!oneFinishedNotDeployed) {
					oneFinishedNotDeployed = true;
				}
			} else if (UndeployItemStatus.NOT_SUPPORTED.equals(undeployItem
					.getUndeployItemStatus())) {
				if (!oneFinishedNotSupported) {
					oneFinishedNotSupported = true;
				}
			} else if (UndeployItemStatus.OFFLINE_ADMITTED.equals(undeployItem
					.getUndeployItemStatus())) {
				if (!oneFinishedOfflineAdmitted) {
					oneFinishedOfflineAdmitted = true;
				}
			} else if (UndeployItemStatus.OFFLINE_ABORTED.equals(undeployItem
					.getUndeployItemStatus())) {
				if (!oneFinishedOfflineAborted) {
					oneFinishedOfflineAborted = true;
				}
			}
		}

		this.offlinePhaseScheduled = oneFinishedOfflineAdmitted;

		if (oneFinishedInital || oneFinishedPV || oneFinishedSkipped
				|| oneFinishedAborted || oneFinishedNotSupported
				|| oneFinishedOfflineAborted) {
			return ValidationStatus.ERROR;
		} else if (oneFinishedAdmitted || oneFinishedOfflineAdmitted) {
			return ValidationStatus.SUCCESS;
		} else {
			// TODO: ERROR log that the result could not be get
			return ValidationStatus.ERROR;
		}
	}

	public boolean isOfflinePhaseScheduled() {
		return this.offlinePhaseScheduled;
	}
}
