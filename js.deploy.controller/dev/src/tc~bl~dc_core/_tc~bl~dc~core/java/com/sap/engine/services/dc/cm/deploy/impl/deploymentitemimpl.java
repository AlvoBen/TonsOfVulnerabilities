package com.sap.engine.services.dc.cm.deploy.impl;

import java.util.HashSet;
import java.util.Set;

import com.sap.engine.services.dc.cm.deploy.BatchItemId;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.repo.Sda;

/**
 * 
 * Title: Software Deployment Manager Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-3-11
 * 
 * @author dimitar
 * @version 1.0
 * @since 6.40
 * 
 */
final class DeploymentItemImpl extends DeploymentBatchItemImpl implements
		DeploymentItem {

	private static final long serialVersionUID = -3481452444856846699L;

	private final Set depending;// $JL-SER$
	private final BatchItemId parentId;

	DeploymentItemImpl(BatchItemId batchItemId, String sdaFilePath,
			boolean enableTimeStats) {
		this(batchItemId, sdaFilePath, null, enableTimeStats);
	}

	DeploymentItemImpl(BatchItemId batchItemId, String sdaFilePath,
			BatchItemId parentId, boolean enableTimeStats) {
		super(batchItemId, sdaFilePath, enableTimeStats);

		this.depending = new HashSet();
		this.parentId = parentId;
	}

	DeploymentItemImpl(Sda sda, String sdaFilePath, boolean enableTimeStats) {
		this(sda, sdaFilePath, null, enableTimeStats);
	}

	DeploymentItemImpl(Sda sda, String sdaFilePath, BatchItemId parentId,
			boolean enableTimeStats) {
		super(sda, sdaFilePath, enableTimeStats);

		this.depending = new HashSet();
		this.parentId = parentId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.deploy.DeploymentItem#getParentId()
	 */
	public BatchItemId getParentId() {
		return this.parentId;
	}

	public Set getDepending() {
		return this.depending;
	}

	public void addDepending(DeploymentItem deploymentItem) {
		this.depending.add(deploymentItem);
	}

	public void removeDepending(DeploymentItem deploymentItem) {
		this.depending.remove(deploymentItem);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.DeploymentItem#accept(com.sap.engine
	 * .services.dc.cm.deploy.DeploymentBatchItemVisitor)
	 */
	public void accept(DeploymentBatchItemVisitor visitor) {
		visitor.visit(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.deploy.DeploymentItem#getSda()
	 */
	public Sda getSda() {
		return (Sda) this.getSdu();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.deploy.DeploymentItem#getOldSda()
	 */
	public Sda getOldSda() {
		return (Sda) this.getOldSdu();
	}

}
