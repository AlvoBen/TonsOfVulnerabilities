package com.sap.engine.services.dc.cm.deploy.impl;

import java.util.Collection;
import java.util.Enumeration;

import com.sap.engine.services.dc.cm.deploy.BatchItemId;
import com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.repo.Sca;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-8-17
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class CompositeDeploymentItemImpl extends DeploymentBatchItemImpl
		implements CompositeDeploymentItem {

	private static final long serialVersionUID = 481851092262917920L;

	private final Collection deploymentItems;// $JL-SER$

	CompositeDeploymentItemImpl(BatchItemId batchItemId, String scaFilePath,
			Collection deploymentItems, boolean enableTimeStats) {
		super(batchItemId, scaFilePath, enableTimeStats);
		this.deploymentItems = deploymentItems;
	}

	CompositeDeploymentItemImpl(Sca sca, String scaFilePath,
			Collection deploymentItems, boolean enableTimeStats) {
		super(sca, scaFilePath, enableTimeStats);
		this.deploymentItems = deploymentItems;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem#
	 * getDeploymentItems()
	 */
	public Collection getDeploymentItems() {
		return this.deploymentItems;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem#
	 * removeDeploymentItem(com.sap.engine.services.dc.cm.deploy.DeploymentItem)
	 */
	public void removeDeploymentItem(DeploymentItem deploymentItem) {
		this.deploymentItems.remove(deploymentItem);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem#
	 * removeDeploymentItems(java.util.Collection)
	 */
	public void removeDeploymentItems(Collection aDeploymentItems) {
		this.deploymentItems.removeAll(aDeploymentItems);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem#
	 * getAdmittedDeploymentItems()
	 */
	public Enumeration getAdmittedDeploymentItems() {
		return new AdmittedDeploymentItemsEnumeration(getDeploymentItems());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.impl.DeploymentBatchItemImpl#accept
	 * (com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor)
	 */
	public void accept(DeploymentBatchItemVisitor visitor) {
		visitor.visit(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem#getSca()
	 */
	public Sca getSca() {
		return (Sca) this.getSdu();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem#getOldSca()
	 */
	public Sca getOldSca() {
		return (Sca) this.getOldSdu();
	}

}
