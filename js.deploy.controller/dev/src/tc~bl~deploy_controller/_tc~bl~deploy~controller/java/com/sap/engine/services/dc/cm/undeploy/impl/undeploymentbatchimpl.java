package com.sap.engine.services.dc.cm.undeploy.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sap.engine.services.dc.cm.undeploy.GenericUndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeploymentBatch;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * The class is not thread safe.
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-1
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public final class UndeploymentBatchImpl implements UndeploymentBatch {

	private final Set undeployItems;
	private final List orderedUndeployItems;

	public static UndeploymentBatchImpl createUndeploymentBatch(
			GenericUndeployItem[] undeployItemsArr) {
		return new UndeploymentBatchImpl(undeployItemsArr);
	}

	static UndeploymentBatchImpl createUndeploymentBatch(Set undeployItems) {
		return new UndeploymentBatchImpl(undeployItems);
	}

	private UndeploymentBatchImpl(GenericUndeployItem[] undeployItemsArr) {
		this.undeployItems = new HashSet();
		this.orderedUndeployItems = new ArrayList();

		init(undeployItemsArr);
	}

	private UndeploymentBatchImpl(Set undeployItems) {
		this.undeployItems = undeployItems;
		this.orderedUndeployItems = new ArrayList();
	}

	public List getOrderedUndeployItems() {
		return this.orderedUndeployItems;
	}

	public void addOrderedUndeployItems(List ordered) {
		this.orderedUndeployItems.clear();

		this.orderedUndeployItems.addAll(ordered);
	}

	public Enumeration getAdmittedUndeployItems() {
		return new AdmittedUndeployItemsEnumeration(this.undeployItems);
	}

	public Set getUndeployItems() {
		return this.undeployItems;
	}

	public void addUndeployItem(GenericUndeployItem undeployItem) {
		this.undeployItems.add(undeployItem);
	}

	public void addUndeployItems(Collection undeployItems) {
		this.undeployItems.addAll(undeployItems);
	}

	public void removeUndeployItem(GenericUndeployItem undeployItem) {
		this.undeployItems.remove(undeployItem);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.undeploy.UndeploymentBatch#removeUndeployItems
	 * (java.util.Collection)
	 */
	public void removeUndeployItems(Collection undeployItems) {
		this.undeployItems.removeAll(undeployItems);
	}

	private void init(GenericUndeployItem[] undeployItemsArr) {
		for (int i = 0; i < undeployItemsArr.length; i++) {
			this.undeployItems.add(undeployItemsArr[i]);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.undeploy.UndeploymentBatch#clear()
	 */
	public void clear() {
		this.undeployItems.clear();
		this.orderedUndeployItems.clear();
	}

}
