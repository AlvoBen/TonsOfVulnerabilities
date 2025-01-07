package com.sap.engine.services.dc.cm.undeploy.impl;

import java.util.Collection;
import java.util.Iterator;

import com.sap.engine.services.dc.cm.undeploy.GenericUndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployItemStatus;
import com.sap.engine.services.dc.cm.undeploy.UndeployResult;
import com.sap.engine.services.dc.cm.undeploy.UndeployResultStatus;
import com.sap.engine.services.dc.cm.utils.measurement.DMeasurement;
import com.sap.engine.services.dc.util.Constants;
/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-1
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class UndeployResultImpl implements UndeployResult {

	private static final long serialVersionUID = -8010924912534115504L;

	private final Collection undeploymentItems;// $JL-SER$
	private final Collection orderedUndeployItems;// $JL-SER$
	private final UndeployResultStatus undeployResultStatus;
	private final String description;
	private final String toString;
	private final DMeasurement measurement;

	UndeployResultImpl(Collection undeploymentItems,
			Collection orderedUndeployItems, DMeasurement measurement){
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
		this.undeployResultStatus = buildUndeployStatus();
		this.description = buildDescription();
		this.toString = buildToString();
		this.measurement = measurement;		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.undeploy.UndeployResult#getUndeployStatus()
	 */
	public UndeployResultStatus getUndeployStatus() {
		return this.undeployResultStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.undeploy.UndeployResult#getUndeployItems()
	 */
	public Collection getUndeployItems() {
		return this.undeploymentItems;
	}
	
	public DMeasurement getMeasurement(){
		return this.measurement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.undeploy.UndeployResult#getOrderedUndeployItems
	 * ()
	 */
	public Collection getOrderedUndeployItems() {
		return this.orderedUndeployItems;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.undeploy.UndeployResult#getDescription()
	 */
	public String getDescription() {
		return this.description;
	}

	public String toString() {
		return this.toString;
	}

	private UndeployResultStatus buildUndeployStatus() {

		boolean oneFinishedInital = false;
		boolean oneFinishedPV = false;
		boolean oneFinishedSkipped = false;
		boolean oneFinishedAborted = false;
		boolean oneFinishedAdmitted = false;
		boolean oneFinishedWarning = false;
		boolean oneFinishedSuccess = false;
		boolean oneFinishedNotDeployed = false;
		boolean oneFinishedNotSupported = false;
		boolean oneFinishedOfflineAdmitted = false;
		boolean oneFinishedOfflineAborted = false;
		boolean oneFinishedOfflineSuccess = false;
		boolean oneFinishedOfflineWarning = false;

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
			} else if (UndeployItemStatus.WARNING.equals(undeployItem
					.getUndeployItemStatus())) {
				if (!oneFinishedWarning) {
					oneFinishedWarning = true;
				}
			} else if (UndeployItemStatus.SUCCESS.equals(undeployItem
					.getUndeployItemStatus())) {
				if (!oneFinishedSuccess) {
					oneFinishedSuccess = true;
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
			} else if (UndeployItemStatus.OFFLINE_SUCCESS.equals(undeployItem
					.getUndeployItemStatus())) {
				if (!oneFinishedOfflineSuccess) {
					oneFinishedOfflineSuccess = true;
				}
			} else if (UndeployItemStatus.OFFLINE_WARNING.equals(undeployItem
					.getUndeployItemStatus())) {
				if (!oneFinishedOfflineWarning) {
					oneFinishedOfflineWarning = true;
				}
			}
		}

		if (oneFinishedInital || oneFinishedPV || oneFinishedSkipped
				|| oneFinishedAborted || oneFinishedAdmitted
				|| oneFinishedNotSupported || oneFinishedOfflineAdmitted
				|| oneFinishedOfflineAborted) {
			return UndeployResultStatus.ERROR;
		} else if (oneFinishedWarning || oneFinishedNotDeployed
				|| oneFinishedOfflineWarning) {
			return UndeployResultStatus.WARNING;
		} else if (oneFinishedSuccess || oneFinishedOfflineSuccess) {
			return UndeployResultStatus.SUCCESS;
		} else {
			// TODO: ERROR log that the result could not be get
			return UndeployResultStatus.UNKNOWN;
		}
	}

	private String buildDescription() {
		return "";
	}

	private String buildToString() {
		final UndeployResultStatus status = getUndeployStatus();
		final String description = getDescription();

		return "The status of the undeployed SDUs is " + status + ". "
				+ Constants.EOL + "Additional information: " + Constants.EOL
				+ description;
	}

}
