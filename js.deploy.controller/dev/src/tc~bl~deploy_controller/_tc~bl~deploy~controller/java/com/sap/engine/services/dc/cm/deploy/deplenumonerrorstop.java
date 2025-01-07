package com.sap.engine.services.dc.cm.deploy;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import com.sap.engine.services.dc.cm.ErrorStrategy;

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
public class DeplEnumOnErrorStop implements Enumeration {

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

	public DeplEnumOnErrorStop(Enumeration deploymentItemsEnum) {
		this(deploymentItemsEnum, DEFAULT_ACCEPTED_STATUSES);
	}

	public DeplEnumOnErrorStop(Enumeration deploymentItemsEnum,
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

		if (this.hasMore && !isCorrect(this.nextDeplBatchItem)) {
			setDeplItemsSkipped();

			this.hasMore = false;

			return this.hasMore;
		}

		this.hasMoreChecked = true;
		this.hasMore = false;

		while (this.deploymentBatchItemsEnum.hasMoreElements() && !this.hasMore) {
			final DeploymentBatchItem deplBatchItem = (DeploymentBatchItem) this.deploymentBatchItemsEnum
					.nextElement();
			if (isNext(deplBatchItem)) {
				this.nextDeplBatchItem = deplBatchItem;
				this.hasMore = true;
			}
		}

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
				// this.hasMore = false;
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

	protected boolean isNext(DeploymentBatchItem deplBatchItem) {
		return true;
	}

	protected boolean isCorrect(DeploymentBatchItem deplBatchItem) {
		return (deplBatchItem == null || this.acceptedDeploymentStatuses
				.contains(deplBatchItem.getDeploymentStatus()));
	}

	protected void setSkipped(DeploymentBatchItem deplBatchItem) {
		deplBatchItem.setDeploymentStatus(DeploymentStatus.SKIPPED);
		deplBatchItem
				.addDescription("Item is skipped because of failed deployment of item '"
						+ this.nextDeplBatchItem.getBatchItemId()
						+ "' and because the applied error strategy is "
						+ ErrorStrategy.ON_ERROR_STOP);
	}

	private void setDeplItemsSkipped() {
		while (this.deploymentBatchItemsEnum.hasMoreElements()) {
			final DeploymentBatchItem deplBatchItem = (DeploymentBatchItem) this.deploymentBatchItemsEnum
					.nextElement();

			if (!DeploymentStatus.SKIPPED.equals(deplBatchItem
					.getDeploymentStatus())) {
				this.setSkipped(deplBatchItem);
			}
		}
	}
}
