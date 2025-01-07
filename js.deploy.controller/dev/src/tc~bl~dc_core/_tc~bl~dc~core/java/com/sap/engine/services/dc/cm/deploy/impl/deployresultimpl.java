package com.sap.engine.services.dc.cm.deploy.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.sap.engine.services.dc.cm.deploy.DeployResult;
import com.sap.engine.services.dc.cm.deploy.DeployResultStatus;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem; //import com.sap.engine.services.dc.cm.deploy.DeploymentData;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentStatus;
import com.sap.engine.services.dc.cm.utils.measurement.DMeasurement;

/**
 * 
 * Title: Software Deployment Manager Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-4-8
 * 
 * @author dimitar
 * @version 1.0
 * @since 6.40
 * 
 */
class DeployResultImpl implements DeployResult {

	private static final long serialVersionUID = -682162367738873764L;
	private static final String EOL = System.getProperty("line.separator");

	private final Collection deploymentBatchItems;// $JL-SER$
	private final Collection sortedDeploymentBatchItems;// $JL-SER$
	private final DeployResultStatus deployResultStatus;
	private final String description;
	private final String toString;
	private Map repeatedMap;// $JL-SER$
	private final DMeasurement measurement;

	DeployResultImpl(String dplDataDescription,
				Collection dplDataSortedDeploymentBatchItems,
				Collection dplDataDeploymentBatchItems,
				DMeasurement measurement) {
			String ddDescription = dplDataDescription;
		Collection ddSortedDeploymentBatchItems = dplDataSortedDeploymentBatchItems;
		Collection ddDeploymentBatchItems = dplDataDeploymentBatchItems;
		if (ddDeploymentBatchItems == null) {
			throw new NullPointerException(
					"[ERROR CODE DPL.DC.3405] The collection with the deployment batch items is null!");
		}

		if (ddSortedDeploymentBatchItems == null) {
			throw new NullPointerException(
					"[ERROR CODE DPL.DC.3406] The collection with the sorted batch items is null!");
		}

		this.sortedDeploymentBatchItems = ddSortedDeploymentBatchItems;

		this.deploymentBatchItems = ddDeploymentBatchItems;

		this.deployResultStatus = buildDeployStatus();
		this.description = buildDescription(ddDescription);
		this.toString = buildToString();
		this.measurement = measurement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.deploy.DeployResult#getDeployStatus()
	 */
	public DeployResultStatus getDeployStatus() {
		return this.deployResultStatus;
	}
	
	public DMeasurement getMeasurement(){
		return this.measurement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.deploy.DeployResult#
	 * getSortedDeploymentBatchItems()
	 */
	public Collection getSortedDeploymentBatchItems() {
		return this.sortedDeploymentBatchItems;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.deploy.DeployResult#getDeployItems()
	 */
	public Collection getDeploymentItems() {
		return this.deploymentBatchItems;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.deploy.DeployResult#getDescription()
	 */
	public String getDescription() {
		return this.description;
	}

	public String toString() {
		return this.toString;
	}

	private String buildToString() {
		final DeployResultStatus deplStatus = getDeployStatus();
		final String descr = getDescription();

		return "The status of the deployed SDUs is " + deplStatus + ". " + EOL
				+ "Additional information: " + EOL + descr;
	}

	private String buildDescription(String ddDescription) {
		return ddDescription != null ? ddDescription : "";
	}

	private DeployResultStatus buildDeployStatus() {
		boolean oneFinishedInital = false;
		boolean oneFinishedSkipped = false;
		boolean oneFinishedAborted = false;
		boolean oneFinishedAdmitted = false;
		boolean oneFinishedPrerequisiteViolated = false;
		boolean oneFinishedDelivered = false;
		boolean oneFinishedWarning = false;
		boolean oneFinishedSuccess = false;
		boolean oneFinishedAlreadyDeployed = false;
		boolean oneFinishedFiltered = false;
		boolean oneFinishedRepeated = false;
		boolean oneFinishedOfflineAdmitted = false;
		boolean oneFinishedOfflineAborted = false;
		boolean oneFinishedOfflineSuccess = false;

		for (Iterator iter = this.deploymentBatchItems.iterator(); iter
				.hasNext();) {
			final DeploymentBatchItem deplBatchItem = (DeploymentBatchItem) iter
					.next();
			final DeploymentStatus deplItemStatus = deplBatchItem
					.getDeploymentStatus();

			if (deplItemStatus.equals(DeploymentStatus.INITIAL)) {
				if (!oneFinishedInital) {
					oneFinishedInital = true;
				}
			} else if (deplItemStatus.equals(DeploymentStatus.SKIPPED)) {
				if (!oneFinishedSkipped) {
					oneFinishedSkipped = true;
				}
			} else if (deplItemStatus.equals(DeploymentStatus.ABORTED)) {
				if (!oneFinishedAborted) {
					oneFinishedAborted = true;
				}
			} else if (deplItemStatus.equals(DeploymentStatus.ADMITTED)) {
				if (!oneFinishedAdmitted) {
					oneFinishedAdmitted = true;
				}
			} else if (deplItemStatus
					.equals(DeploymentStatus.PREREQUISITE_VIOLATED)) {
				if (!oneFinishedPrerequisiteViolated) {
					oneFinishedPrerequisiteViolated = true;
				}
			} else if (deplItemStatus.equals(DeploymentStatus.DELIVERED)) {
				if (!oneFinishedDelivered) {
					oneFinishedDelivered = true;
				}
			} else if (deplItemStatus.equals(DeploymentStatus.WARNING)) {
				if (!oneFinishedWarning) {
					oneFinishedWarning = true;
				}
			} else if (deplItemStatus.equals(DeploymentStatus.SUCCESS)) {
				if (!oneFinishedSuccess) {
					oneFinishedSuccess = true;
				}
			} else if (deplItemStatus.equals(DeploymentStatus.ALREADY_DEPLOYED)) {
				if (!oneFinishedAlreadyDeployed) {
					oneFinishedAlreadyDeployed = true;
				}
			} else if (deplItemStatus.equals(DeploymentStatus.FILTERED)) {
				if (!oneFinishedFiltered) {
					oneFinishedFiltered = true;
				}
			} else if (deplItemStatus.equals(DeploymentStatus.OFFLINE_ADMITTED)) {
				if (!oneFinishedOfflineAdmitted) {
					oneFinishedOfflineAdmitted = true;
				}
			} else if (deplItemStatus.equals(DeploymentStatus.OFFLINE_ABORTED)) {
				if (!oneFinishedOfflineAborted) {
					oneFinishedOfflineAborted = true;
				}
			} else if (deplItemStatus.equals(DeploymentStatus.OFFLINE_SUCCESS)) {
				if (!oneFinishedOfflineSuccess) {
					oneFinishedOfflineSuccess = true;
				}
			} else if (deplItemStatus.equals(DeploymentStatus.REPEATED)) {
				setCorrectStatus(deplBatchItem);

				if (!oneFinishedRepeated) {
					oneFinishedRepeated = true;
				}
			}
			// else {
			// DCLog.logError(
			// DCLogConstants.DEPLOY_UKNOWN_DEPLOYMENT_BATCH_ITEM_STATUS,
			// new Object[] {deplItemStatus, deplBatchItem}
			// );
			// }
		}

		if (oneFinishedInital || oneFinishedSkipped || oneFinishedAborted
				|| oneFinishedAdmitted || oneFinishedPrerequisiteViolated
				|| oneFinishedOfflineAdmitted || oneFinishedOfflineAborted) {
			return DeployResultStatus.ERROR;
		} else if (oneFinishedDelivered || oneFinishedWarning) {
			return DeployResultStatus.WARNING;
		} else if (oneFinishedSuccess || oneFinishedOfflineSuccess) {
			return DeployResultStatus.SUCCESS;
		} else if (oneFinishedAlreadyDeployed || oneFinishedFiltered
				|| oneFinishedRepeated) {
			return DeployResultStatus.ERROR;
		} else {
			// DCLog.logError(
			// DCLogConstants.DEPLOY_ENTERED_ILLEGAL_STATE);
			return DeployResultStatus.UNKNOWN;
		}
	}

	private synchronized void setCorrectStatus(DeploymentBatchItem deplBatchItem) {
		if (this.repeatedMap == null) {
			initRepeatedMap();
		}

		setCorrectStatusForSingleItem(deplBatchItem);

		if (deplBatchItem instanceof CompositeDeploymentItemImpl) {
			CompositeDeploymentItemImpl compositeDeploymentItemImpl = (CompositeDeploymentItemImpl) deplBatchItem;
			Collection innerItems = compositeDeploymentItemImpl
					.getDeploymentItems();
			for (Iterator iterator = innerItems.iterator(); iterator.hasNext();) {
				DeploymentItem innerItem = (DeploymentItem) iterator.next();
				setCorrectStatusForSingleItem(innerItem);
			}
		}
	}

	private void setCorrectStatusForSingleItem(DeploymentBatchItem deplBatchItem) {

		final Collection repeatedItems = (Collection) this.repeatedMap
				.get(deplBatchItem.getBatchItemId());
		DeploymentStatus deplStatus = null;
		for (Iterator iter = repeatedItems.iterator(); iter.hasNext();) {
			final DeploymentBatchItem item = (DeploymentBatchItem) iter.next();
			if (!DeploymentStatus.REPEATED.equals(item.getDeploymentStatus())) {
				deplStatus = item.getDeploymentStatus();

				break;
			}
		}

		for (Iterator iter = repeatedItems.iterator(); iter.hasNext();) {
			final DeploymentBatchItem item = (DeploymentBatchItem) iter.next();
			if (DeploymentStatus.REPEATED.equals(item.getDeploymentStatus())
					&& deplStatus != null) {
				item.setDeploymentStatus(deplStatus);
			}
		}
	}

	private void processSingleItemWhenInitMap(DeploymentBatchItem item) {
		Collection repeatedItems = (Collection) this.repeatedMap.get(item
				.getBatchItemId());
		if (repeatedItems == null) {
			repeatedItems = new ArrayList();
			this.repeatedMap.put(item.getBatchItemId(), repeatedItems);
		}
		repeatedItems.add(item);
	}

	private void initRepeatedMap() {
		this.repeatedMap = new HashMap();

		for (Iterator iter = this.deploymentBatchItems.iterator(); iter
				.hasNext();) {
			final DeploymentBatchItem item = (DeploymentBatchItem) iter.next();
			if (item instanceof CompositeDeploymentItemImpl) {
				CompositeDeploymentItemImpl compositeDeploymentItemImpl = (CompositeDeploymentItemImpl) item;
				Collection innerItems = compositeDeploymentItemImpl
						.getDeploymentItems();
				for (Iterator iterator = innerItems.iterator(); iterator
						.hasNext();) {
					DeploymentItem innerItem = (DeploymentItem) iterator.next();
					processSingleItemWhenInitMap(innerItem);
				}
			}

			processSingleItemWhenInitMap(item);
			/*
			 * Collection repeatedItems = (Collection) this.repeatedMap.get(
			 * item.getBatchItemId() ); if (repeatedItems == null) {
			 * repeatedItems = new ArrayList();
			 * this.repeatedMap.put(item.getBatchItemId(), repeatedItems); }
			 * repeatedItems.add(item);
			 */
		}
	}

}
