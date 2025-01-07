package com.sap.engine.services.dc.cm.undeploy;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import com.sap.engine.services.dc.cm.ErrorStrategy;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-23
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public class UndeplEnumOnErrorStop implements Enumeration {

	private static final Set DEFAULT_ACCEPTED_STATUSES = new HashSet();

	private GenericUndeployItem nextUndeployItem;
	private final Enumeration undeployItemsEnum;
	private final Set acceptedDeploymentStatuses;

	private boolean hasMoreChecked;
	private boolean hasMore;

	static {
		DEFAULT_ACCEPTED_STATUSES.add(UndeployItemStatus.WARNING);
		DEFAULT_ACCEPTED_STATUSES.add(UndeployItemStatus.OFFLINE_WARNING);
		DEFAULT_ACCEPTED_STATUSES.add(UndeployItemStatus.OFFLINE_SUCCESS);
		DEFAULT_ACCEPTED_STATUSES.add(UndeployItemStatus.SUCCESS);
	}

	public UndeplEnumOnErrorStop(Enumeration undeployItemsEnum) {
		this(undeployItemsEnum, DEFAULT_ACCEPTED_STATUSES);
	}

	public UndeplEnumOnErrorStop(Enumeration undeployItemsEnum,
			Set acceptedDeploymentStatuses) {
		this.undeployItemsEnum = undeployItemsEnum;

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

		if (this.hasMore && !isCorrect(this.nextUndeployItem)) {
			setDeplItemsSkipped(undeployItemsEnum);

			this.hasMore = false;

			return this.hasMore;
		}

		this.hasMoreChecked = true;
		this.hasMore = false;

		while (this.undeployItemsEnum.hasMoreElements() && !hasMore) {
			final GenericUndeployItem undeployItem = (GenericUndeployItem) this.undeployItemsEnum
					.nextElement();

			if (isNext(undeployItem)) {
				this.nextUndeployItem = undeployItem;
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
				return this.nextUndeployItem;
			} else {
				throw new NoSuchElementException();
			}
		} else {
			if (hasMoreElements()) {
				this.hasMoreChecked = false;
				return this.nextUndeployItem;
			}

			throw new NoSuchElementException();
		}
	}

	protected boolean isNext(GenericUndeployItem undeployItem) {
		return true;
	}

	protected boolean isCorrect(GenericUndeployItem undeployItem) {
		return undeployItem == null
				|| this.acceptedDeploymentStatuses.contains(undeployItem
						.getUndeployItemStatus());
	}

	protected void setSkipped(GenericUndeployItem undeployItem) {
		undeployItem.setUndeployItemStatus(UndeployItemStatus.SKIPPED);
		undeployItem
				.setDescription("Item is skipped because of failed undeployment of item '"
						+ this.nextUndeployItem.getId()
						+ "' and because the applied error strategy is "
						+ ErrorStrategy.ON_ERROR_STOP);
	}

	private void setDeplItemsSkipped(Enumeration undeployItemsEnum) {
		while (undeployItemsEnum.hasMoreElements()) {
			final GenericUndeployItem undeplItem = (GenericUndeployItem) undeployItemsEnum
					.nextElement();

			if (!UndeployItemStatus.SKIPPED.equals(undeplItem
					.getUndeployItemStatus())) {
				setSkipped(undeplItem);
			}
		}
	}

}
