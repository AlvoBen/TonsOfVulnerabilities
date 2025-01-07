package com.sap.engine.services.dc.cm.deploy;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

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
public class DeplEnumOnErrorSkipDepending implements Enumeration {

	private static final Set DEFAULT_ACCEPTED_STATUSES = new HashSet();

	private DeploymentBatchItem nextDeplBatchItem;
	private boolean hasMoreChecked;
	private boolean hasMore;

	private final Enumeration deploymentBatchItemsEnum;
	private final Set acceptedDeploymentStatuses;

	static {
		DEFAULT_ACCEPTED_STATUSES.add(DeploymentStatus.DELIVERED);
		DEFAULT_ACCEPTED_STATUSES.add(DeploymentStatus.WARNING);
		DEFAULT_ACCEPTED_STATUSES.add(DeploymentStatus.OFFLINE_WARNING);
		DEFAULT_ACCEPTED_STATUSES.add(DeploymentStatus.OFFLINE_SUCCESS);
		DEFAULT_ACCEPTED_STATUSES.add(DeploymentStatus.SUCCESS);
	}

	public DeplEnumOnErrorSkipDepending(Enumeration deploymentItemsEnum) {
		this(deploymentItemsEnum, DEFAULT_ACCEPTED_STATUSES);
	}

	public DeplEnumOnErrorSkipDepending(Enumeration deploymentItemsEnum,
			Set acceptedDeploymentStatuses) {
		this.deploymentBatchItemsEnum = deploymentItemsEnum;

		if (acceptedDeploymentStatuses == null
				|| acceptedDeploymentStatuses.isEmpty()) {
			this.acceptedDeploymentStatuses = DEFAULT_ACCEPTED_STATUSES;
		} else {
			this.acceptedDeploymentStatuses = new HashSet();
			this.acceptedDeploymentStatuses.addAll(acceptedDeploymentStatuses);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Enumeration#hasMoreElements()
	 */
	public boolean hasMoreElements() {
		if (this.hasMoreChecked) {
			return this.hasMore;
		}

		this.hasMoreChecked = true;
		this.hasMore = false;

		while (this.deploymentBatchItemsEnum.hasMoreElements() && !this.hasMore) {
			final DeploymentBatchItem deplBatchItem = (DeploymentBatchItem) this.deploymentBatchItemsEnum
					.nextElement();
			if (isNext(deplBatchItem)) {
				DeploymentBatchItem undeliveredDeplBatchItem = hasUndeliveredDependingDeplItem(deplBatchItem);
				if (undeliveredDeplBatchItem == null) {
					this.nextDeplBatchItem = deplBatchItem;
					this.hasMore = true;
				} else {
					setSkipped(deplBatchItem, undeliveredDeplBatchItem
							.getBatchItemId());
				}
			}
		}

		// if ( !this.hasMore ) {
		// this.nextDeplBatchItem = null;
		// }

		return hasMore;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Enumeration#nextElement()
	 */
	public Object nextElement() {
		if (this.hasMoreChecked) {
			this.hasMoreChecked = false;
			if (this.hasMore) {
				this.hasMore = false;
				return this.nextDeplBatchItem;
			} else {
				throw new NoSuchElementException();
			}
		} else {
			if (hasMoreElements() /* && this.hasMore */) {
				this.hasMoreChecked = false;
				// this.hasMore = false;
				return this.nextDeplBatchItem;
			}

			throw new NoSuchElementException();
		}
	}

	protected void setSkipped(DeploymentBatchItem deplBatchItem,
			BatchItemId undeliveredBatchItemId) {
		deplBatchItem.setDeploymentStatus(DeploymentStatus.SKIPPED);
		deplBatchItem
				.addDescription("Item is skipped because at least one item on "
						+ "which depends is not delivered - '"
						+ undeliveredBatchItemId + "'.");
	}

	protected boolean isNext(DeploymentBatchItem deplBatchItem) {
		return true;
	}

	protected DeploymentBatchItem hasUndeliveredDependingDeplItem(
			DeploymentBatchItem deplBatchItem) {
		final DependingDeliveredChecker deliveredChecker = new DependingDeliveredChecker(
				this.acceptedDeploymentStatuses);
		deplBatchItem.accept(deliveredChecker);

		return deliveredChecker.hasUndeliveredDependingDeplItem();
	}

	private static class DependingDeliveredChecker implements
			DeploymentBatchItemVisitor {

		private final Set acceptedDeploymentStatuses;
		private DeploymentBatchItem undeliveredDependingDeplItem = null;

		private DependingDeliveredChecker(Set acceptedDeplStatuses) {
			acceptedDeploymentStatuses = acceptedDeplStatuses;
		}

		DeploymentBatchItem hasUndeliveredDependingDeplItem() {
			return undeliveredDependingDeplItem;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor#visit
		 * (com.sap.engine.services.dc.cm.deploy.DeploymentItem)
		 */
		public void visit(DeploymentItem deplItem) {
			final Collection dependingDeploymentItems = deplItem.getDepending();
			if (dependingDeploymentItems != null
					&& !dependingDeploymentItems.isEmpty()) {
				for (Iterator iter = dependingDeploymentItems.iterator(); iter
						.hasNext();) {
					final DeploymentBatchItem deploymentItem = (DeploymentBatchItem) iter
							.next();
					if (!acceptedDeploymentStatuses.contains(deploymentItem
							.getDeploymentStatus())) {
						undeliveredDependingDeplItem = deploymentItem;
						return;
					}
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor#visit
		 * (com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem)
		 */
		public void visit(CompositeDeploymentItem deploymentItem) {
		}

	}

}
