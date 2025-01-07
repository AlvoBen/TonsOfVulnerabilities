package com.sap.engine.services.dc.cm.deploy.impl;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentStatus;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-7-10
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class AdmittedDeploymentItemsEnumeration implements Enumeration {

	private final Collection deploymentItems;
	private final Iterator deploymentItemsIter;

	private DeploymentItem deploymentItem;
	private boolean hasMoreChecked = false;

	AdmittedDeploymentItemsEnumeration(Collection deploymentItems) {
		this.deploymentItems = deploymentItems;
		this.deploymentItemsIter = this.deploymentItems.iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Enumeration#hasMoreElements()
	 */
	public boolean hasMoreElements() {
		if (this.hasMoreChecked) {
			return this.deploymentItem != null;
		} else {
			this.hasMoreChecked = true;
		}

		boolean hasMore = false;
		while (this.deploymentItemsIter.hasNext() && !hasMore) {
			final DeploymentItem deplItem = (DeploymentItem) this.deploymentItemsIter
					.next();
			if (deplItem.getDeploymentStatus()
					.equals(DeploymentStatus.ADMITTED)) {
				this.deploymentItem = deplItem;
				hasMore = true;
			}
		}

		if (!hasMore) {
			this.deploymentItem = null;
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
			if (this.deploymentItem != null) {
				return this.deploymentItem;
			} else {
				throw new NoSuchElementException();
			}
		} else {
			if (hasMoreElements() && this.deploymentItem != null) {
				this.hasMoreChecked = false;
				return this.deploymentItem;
			}

			throw new NoSuchElementException();
		}
	}

}
