/*
 * Created on May 5, 2005
 */
package com.sap.engine.services.dc.api.deploy.impl;

import java.util.HashMap;

import com.sap.engine.services.dc.api.deploy.DeployItem;
import com.sap.engine.services.dc.api.event.DeploymentEventAction;
import com.sap.engine.services.dc.api.event.MappingException;
import com.sap.engine.services.dc.api.model.impl.SduMapperVisitor;
import com.sap.engine.services.dc.api.util.DAUtils;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-4-27
 * 
 * @author Boris Savov(i030791)
 * @version 1.0
 * @since 7.1
 * 
 */
public final class DeploymentEventMapper {
	private static final HashMap deploymentEventActions = new HashMap();
	static {
		deploymentEventActions
				.put(
						com.sap.engine.services.dc.event.DeploymentEventAction.DEPLOYMENT_PERFORMED,
						com.sap.engine.services.dc.api.event.DeploymentEventAction.DEPLOYMENT_PERFORMED);
		deploymentEventActions
				.put(
						com.sap.engine.services.dc.event.DeploymentEventAction.DEPLOYMENT_TRIGGERED,
						com.sap.engine.services.dc.api.event.DeploymentEventAction.DEPLOYMENT_TRIGGERED);
	}

	/**
	 * Map the remote deployment event
	 * 
	 * @param deployItems
	 *            the deploy items that the user passed in for deployment
	 * @param dcDeploymentEvent
	 *            the remote event
	 * @return the mapped dc api event
	 * @throws MappingException
	 *             if the event cannot be mapped
	 */
	public static com.sap.engine.services.dc.api.event.DeploymentEvent mapDeploymentEvent(
			DeployItem[] deployItems,
			com.sap.engine.services.dc.event.DeploymentEvent dcDeploymentEvent)
			throws MappingException {

		if (dcDeploymentEvent == null) {
			throw new MappingException(
					"[ERROR CODE DPL.DCAPI.1021] Remote Deployment Event cannot be null.");
		}

		com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem deploymentBatchItem = dcDeploymentEvent
				.getDeploymentBatchItem();
		if (deploymentBatchItem == null) {
			throw new MappingException(
					"[ERROR CODE DPL.DCAPI.1021] Remote Deployment Batch Items is null.");
		}

		// map the deployment item
		String sduFilePath = deploymentBatchItem.getSduFilePath();
		String sduFileName = DAUtils.getFileName(sduFilePath);

		DeployItem deployItem = null;
		if (deployItems != null) {
			// try to match the deploy items to the ones that the user passed in
			for (int i = 0; i < deployItems.length; i++) {
				if (sduFileName.equals(deployItems[i].getArchive().getName())) {
					deployItem = deployItems[i];
					break;
				}
			}
		}
		deployItem = DeployMapper.mapOrCreateDeployItem(deployItem,
				deploymentBatchItem);

		// map the event action
		com.sap.engine.services.dc.event.DeploymentEventAction dcDeploymentEventAction = dcDeploymentEvent
				.getDeploymentEventAction();
		if (dcDeploymentEventAction == null) {
			throw new MappingException(
					"[ERROR CODE DPL.DCAPI.1021] Remote Deployment Event Action is null.");
		}

		com.sap.engine.services.dc.api.event.DeploymentEventAction daDeploymentEventAction = mapDeploymentEventAction(dcDeploymentEventAction);

		com.sap.engine.services.dc.api.event.DeploymentEvent daDeploymentEvent = new com.sap.engine.services.dc.api.event.DeploymentEvent(
				deployItem, daDeploymentEventAction);

		return daDeploymentEvent;

	}

	public static com.sap.engine.services.dc.api.event.DeploymentEventAction mapDeploymentEventAction(
			com.sap.engine.services.dc.event.DeploymentEventAction dcDeploymentEventAction) {
		com.sap.engine.services.dc.api.event.DeploymentEventAction ret = (DeploymentEventAction) deploymentEventActions
				.get(dcDeploymentEventAction);
		if (ret == null) {
			throw new RuntimeException(
					"[ERROR CODE DPL.DCAPI.1022] Unknown Deployment event "
							+ dcDeploymentEventAction + " detected");
		}
		return ret;
	}

}
