package com.sap.engine.services.dc.cm.deploy;

import java.util.ArrayList;
import java.util.Collection;

import com.sap.engine.services.dc.cm.CMException;

/**
 * 
 * Title: J2EE Deployment Team Description: The exception could be thrown by the
 * time of deployment process excution. The deployment process is generally
 * sperated onto following processe:
 * <ol>
 * <li>Loading;
 * <li>Checking (validating);
 * <li>Delivering;
 * <li>Starting;
 * </ol>
 * 
 * @see com.sap.engine.services.dc.cm.deploy.Deployer Copyright: Copyright (c)
 *      2003 Company: SAP AG Date: 2004-4-1
 * 
 * @author dimitar
 * @version 1.0
 * @since 6.40
 * 
 */
public class DeploymentException extends CMException {

	private static final long serialVersionUID = -8120140008174545182L;

	private final Collection deploymentBatchItems;// $JL-SER$
	private final Collection orderedBatchItems;// $JL-SER$

	public DeploymentException(String errMessage) {
		super(errMessage);

		this.deploymentBatchItems = new ArrayList();
		this.orderedBatchItems = new ArrayList();
	}

	public DeploymentException(String errMessage, Throwable throwable) {
		super(errMessage, throwable);

		this.deploymentBatchItems = new ArrayList();
		this.orderedBatchItems = new ArrayList();
	}

	public void addDeploymentBatchItems(Collection _orderedBatchItems,
			Collection _deploymentBatchItems) {
		if (_deploymentBatchItems != null) {
			this.deploymentBatchItems.addAll(_deploymentBatchItems);
		}
		if (_orderedBatchItems != null) {
			this.orderedBatchItems.addAll(_orderedBatchItems);
		}
	}

	public void removeDeploymentBatchItems(Collection _deploymentBatchItems) {
		this.deploymentBatchItems.removeAll(_deploymentBatchItems);
	}

	public Collection getDeploymentBatchItems() {
		return this.deploymentBatchItems;
	}

	public Collection getOrderedBatchItems() {
		return this.orderedBatchItems;
	}
}
