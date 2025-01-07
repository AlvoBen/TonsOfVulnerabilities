package com.sap.engine.services.dc.cm.deploy.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

import com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatch;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentStatus;

/**
 * 
 * Title: Software Deployment Manager Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-3-18
 * 
 * @author dimitar
 * @version 1.0
 * @since 6.40
 * 
 */
class DeploymentBatchImpl implements DeploymentBatch {

	private static final long serialVersionUID = -4312939559357992703L;

	private final Collection<DeploymentBatchItem> deploymentBatchItems;//$JL-SER$

	DeploymentBatchImpl(Collection<DeploymentBatchItem> deploymentBatchItems) {
		if (deploymentBatchItems == null) {
			throw new NullPointerException(
					"ASJ.dpl_dc.003403 The specified argument \"deploymentBatchItems\" is null.");
		}

		this.deploymentBatchItems = deploymentBatchItems;
	}

	public DeploymentBatchItem getDeploymentBatchItem(String name, String vendor) {
		for (Iterator iterator = deploymentBatchItems.iterator(); iterator
				.hasNext();) {
			final DeploymentItem batchItem = (DeploymentItem) iterator.next();
			if ((batchItem.getSdu().getName().equals(name))
					&& (batchItem.getSdu().getVendor().equals(vendor))) {
				return batchItem;
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.deploy.DeploymentBatch#
	 * removeDeploymentBatchItem
	 * (com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem)
	 */
	public void removeDeploymentBatchItem(
			DeploymentBatchItem deploymentBatchItem) {
		this.deploymentBatchItems.remove(deploymentBatchItem);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.DeploymentBatch#removeDeploymentItems
	 * (java.util.Collection)
	 */
	public void removeDeploymentBatchItems(Collection deploymentBatchItems) {
		this.deploymentBatchItems.removeAll(deploymentBatchItems);
	}

	/**
	 * hmm, should we have getComponents and getDeployOrder or we need only one?
	 * could we have resolved and unordered batch?
	 **/
	public Collection<DeploymentBatchItem> getDeploymentBatchItems() {
		return deploymentBatchItems;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		final StringBuffer sbText = new StringBuffer();
		for (Iterator iterator = deploymentBatchItems.iterator(); iterator
				.hasNext();) {
			final DeploymentItem item = (DeploymentItem) iterator.next();
			sbText.append(item.toString()).append(
					"\n/---/----/----/----/----/----/----/----/----\n");
		}
		return sbText.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.deploy.DeploymentBatch#
	 * getAdmittedDeploymentItems()
	 */
	public Enumeration getAdmittedDeploymentBatchItems() {
		return new AdmittedDeploymentItemsEnumeration(deploymentBatchItems);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.DeploymentBatch#getAllAdmittedDeplItems
	 * ()
	 */
	public Collection<DeploymentBatchItem> getAllAdmittedDeplItems() {

		// TODO: the logic could be replaced by a visitor - no instanceof
		final Collection<DeploymentBatchItem> deploymentBatchItems = getDeploymentBatchItems();
		final Collection<DeploymentBatchItem> admittedSdas = new ArrayList<DeploymentBatchItem>();

		Iterator<DeploymentBatchItem> iter = deploymentBatchItems.iterator();
		while (iter.hasNext()) {

			final DeploymentBatchItem item = iter.next();
			if (item instanceof CompositeDeploymentItem) {
				Collection<DeploymentBatchItem> containedItems = getAdmittedDeplItems((CompositeDeploymentItem) item);
				admittedSdas.addAll(containedItems);

			} else if (item instanceof DeploymentItem) {
				if (item.getDeploymentStatus()
						.equals(DeploymentStatus.ADMITTED)) {
					admittedSdas.add(item);
				}
			} else {
				throw new IllegalStateException(
						"[ERROR CODE DPL.DC.3404] The system found "
								+ "unknown deployment batch item type: " + item
								+ "(" + item.getClass().getName() + ")!");
			}
		}

		return admittedSdas;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.DeploymentBatch#getAllCompositeDeplItems
	 * ()
	 */
	public Collection<CompositeDeploymentItem> getAllCompositeDeplItems() {
		// TODO: the logic could be replaced by a visitor - no instanceof
		final Collection deplBatchItems = getDeploymentBatchItems();
		final Collection<CompositeDeploymentItem> compositeDeplItems = new ArrayList<CompositeDeploymentItem>();

		for (Iterator iter = deplBatchItems.iterator(); iter.hasNext();) {
			DeploymentBatchItem deplBatchItem = (DeploymentBatchItem) iter
					.next();
			if (deplBatchItem instanceof CompositeDeploymentItem) {
				compositeDeplItems.add((CompositeDeploymentItem) deplBatchItem);
			}
		}

		return compositeDeplItems;
	}

	private Collection<DeploymentBatchItem> getAdmittedDeplItems(
			CompositeDeploymentItem compositeItem) {
		final Collection<DeploymentBatchItem> deplItems = new ArrayList<DeploymentBatchItem>();
		final Enumeration admittedDeplItemsEnum = compositeItem
				.getAdmittedDeploymentItems();
		while (admittedDeplItemsEnum.hasMoreElements()) {
			final DeploymentItem deplItem = (DeploymentItem) admittedDeplItemsEnum
					.nextElement();
			deplItems.add(deplItem);
		}

		return deplItems;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.deploy.DeploymentBatch#clear()
	 */
	public void clear() {
		this.deploymentBatchItems.clear();
	}

}
