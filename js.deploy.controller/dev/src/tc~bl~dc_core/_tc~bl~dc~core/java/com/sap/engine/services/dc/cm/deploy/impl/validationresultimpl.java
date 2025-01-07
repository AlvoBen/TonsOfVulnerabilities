package com.sap.engine.services.dc.cm.deploy.impl;

import java.util.Collection;

import com.sap.engine.services.dc.cm.deploy.ValidationResult;
import com.sap.engine.services.dc.cm.deploy.ValidationStatus;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-14
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public class ValidationResultImpl implements ValidationResult {

	private static final long serialVersionUID = 9172454580534619478L;

	private final ValidationStatus validationStatus;
	private final boolean offlinePhaseScheduled;

	private final Collection sortedDeploymentBatchItems;// $JL-SER$
	private final Collection deploymentBatchItems;// $JL-SER$

	public ValidationResultImpl(ValidationStatus validationStatus,
			boolean offlinePhaseScheduled,
			Collection sortedDeploymentBatchItems,
			Collection deploymentBatchItems) {
		this.validationStatus = validationStatus;
		this.offlinePhaseScheduled = offlinePhaseScheduled;
		this.sortedDeploymentBatchItems = sortedDeploymentBatchItems;
		this.deploymentBatchItems = deploymentBatchItems;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.ValidationResult#getValidationStatus
	 * ()
	 */
	public ValidationStatus getValidationStatus() {
		return this.validationStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.ValidationResult#isOfflinePhaseScheduled
	 * ()
	 */
	public boolean isOfflinePhaseScheduled() {
		return this.offlinePhaseScheduled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.deploy.ValidationResult#
	 * getSortedDeploymentBatchItems()
	 */
	public Collection getSortedDeploymentBatchItems() {
		return this.sortedDeploymentBatchItems;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.ValidationResult#getDeploymentBatchItems
	 * ()
	 */
	public Collection getDeploymentBatchItems() {
		return this.deploymentBatchItems;
	}

}
