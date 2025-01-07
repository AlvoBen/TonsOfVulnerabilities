/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.api.undeploy.impl;

import java.util.Hashtable;

/**
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-16
 * 
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
public final class UndeployMapper {
	private static Hashtable undeployItemResult = new Hashtable();
	private static Hashtable undeployResultStatuses = new Hashtable();
	private static Hashtable undeploymentStrategies = new Hashtable();
	private static Hashtable undeployWorkflowStrategies = new Hashtable();

	static {
		// init undeployment strategies
		undeploymentStrategies
				.put(
						com.sap.engine.services.dc.api.undeploy.UndeploymentStrategy.IF_DEPENDING_STOP,
						com.sap.engine.services.dc.cm.undeploy.UndeploymentStrategy.IF_DEPENDING_STOP);
		/*
		 * undeploymentStrategies.put(
		 * com.sap.engine.services.dc.api.undeploy.UndeploymentStrategy.
		 * SKIP_DEPENDING,
		 * com.sap.engine.services.dc.cm.undeploy.UndeploymentStrategy.
		 * SKIP_DEPENDING);
		 */
		undeploymentStrategies
				.put(
						com.sap.engine.services.dc.api.undeploy.UndeploymentStrategy.UNDEPLOY_DEPENDING,
						com.sap.engine.services.dc.cm.undeploy.UndeploymentStrategy.UNDEPLOY_DEPENDING);
		// init undeploy Item Result
		undeployItemResult
				.put(
						com.sap.engine.services.dc.cm.undeploy.UndeployItemStatus.ABORTED,
						com.sap.engine.services.dc.api.undeploy.UndeployItemStatus.ABORTED);
		undeployItemResult
				.put(
						com.sap.engine.services.dc.cm.undeploy.UndeployItemStatus.OFFLINE_ABORTED,
						// com.sap.engine.services.dc.api.undeploy.
						// UndeployItemStatus.
						// OFFLINE_ABORTED
						com.sap.engine.services.dc.api.undeploy.UndeployItemStatus.ABORTED);
		undeployItemResult
				.put(
						com.sap.engine.services.dc.cm.undeploy.UndeployItemStatus.ADMITTED,
						com.sap.engine.services.dc.api.undeploy.UndeployItemStatus.ADMITTED);
		undeployItemResult
				.put(
						com.sap.engine.services.dc.cm.undeploy.UndeployItemStatus.OFFLINE_ADMITTED,
						// com.sap.engine.services.dc.api.undeploy.
						// UndeployItemStatus
						// .OFFLINE_ADMITTED
						com.sap.engine.services.dc.api.undeploy.UndeployItemStatus.ADMITTED);
		undeployItemResult
				.put(
						com.sap.engine.services.dc.cm.undeploy.UndeployItemStatus.INITIAL,
						com.sap.engine.services.dc.api.undeploy.UndeployItemStatus.INITIAL);
		undeployItemResult
				.put(
						com.sap.engine.services.dc.cm.undeploy.UndeployItemStatus.NOT_DEPLOYED,
						com.sap.engine.services.dc.api.undeploy.UndeployItemStatus.NOT_DEPLOYED);
		undeployItemResult
				.put(
						com.sap.engine.services.dc.cm.undeploy.UndeployItemStatus.NOT_SUPPORTED,
						com.sap.engine.services.dc.api.undeploy.UndeployItemStatus.NOT_SUPPORTED);
		undeployItemResult
				.put(
						com.sap.engine.services.dc.cm.undeploy.UndeployItemStatus.SKIPPED,
						com.sap.engine.services.dc.api.undeploy.UndeployItemStatus.SKIPPED);
		undeployItemResult
				.put(
						com.sap.engine.services.dc.cm.undeploy.UndeployItemStatus.SUCCESS,
						com.sap.engine.services.dc.api.undeploy.UndeployItemStatus.SUCCESS);
		undeployItemResult
				.put(
						com.sap.engine.services.dc.cm.undeploy.UndeployItemStatus.OFFLINE_SUCCESS,
						// com.sap.engine.services.dc.api.undeploy.
						// UndeployItemStatus.
						// OFFLINE_SUCCESS
						com.sap.engine.services.dc.api.undeploy.UndeployItemStatus.SUCCESS);
		undeployItemResult
				.put(
						com.sap.engine.services.dc.cm.undeploy.UndeployItemStatus.WARNING,
						com.sap.engine.services.dc.api.undeploy.UndeployItemStatus.WARNING);
		undeployItemResult
				.put(
						com.sap.engine.services.dc.cm.undeploy.UndeployItemStatus.OFFLINE_WARNING,
						// com.sap.engine.services.dc.api.undeploy.
						// UndeployItemStatus.
						// OFFLINE_WARNING
						com.sap.engine.services.dc.api.undeploy.UndeployItemStatus.WARNING);
		undeployItemResult
				.put(
						com.sap.engine.services.dc.cm.undeploy.UndeployItemStatus.PREREQUISITE_VIOLATED,
						com.sap.engine.services.dc.api.undeploy.UndeployItemStatus.PREREQUISITE_VIOLATED);
		// init undeploy Results
		undeployResultStatuses
				.put(
						com.sap.engine.services.dc.cm.undeploy.UndeployResultStatus.ERROR,
						com.sap.engine.services.dc.api.undeploy.UndeployResultStatus.ERROR);
		undeployResultStatuses
				.put(
						com.sap.engine.services.dc.cm.undeploy.UndeployResultStatus.SUCCESS,
						com.sap.engine.services.dc.api.undeploy.UndeployResultStatus.SUCCESS);
		undeployResultStatuses
				.put(
						com.sap.engine.services.dc.cm.undeploy.UndeployResultStatus.UNKNOWN,
						com.sap.engine.services.dc.api.undeploy.UndeployResultStatus.ERROR
				// com.sap.engine.services.dc.api.undeploy.UndeployResultStatus.
				// UNKNOWN
				);
		undeployResultStatuses
				.put(
						com.sap.engine.services.dc.cm.undeploy.UndeployResultStatus.WARNING,
						com.sap.engine.services.dc.api.undeploy.UndeployResultStatus.WARNING);

		undeployWorkflowStrategies
				.put(
						com.sap.engine.services.dc.api.undeploy.UndeployWorkflowStrategy.NORMAL,
						com.sap.engine.services.dc.cm.undeploy.UndeployWorkflowStrategy.NORMAL);
		undeployWorkflowStrategies
				.put(
						com.sap.engine.services.dc.api.undeploy.UndeployWorkflowStrategy.SAFETY,
						com.sap.engine.services.dc.cm.undeploy.UndeployWorkflowStrategy.SAFETY);
	}

	public static com.sap.engine.services.dc.cm.undeploy.UndeploymentStrategy mapUndeploymenStrategy(
			com.sap.engine.services.dc.api.undeploy.UndeploymentStrategy undeploymentStrategy) {
		if (undeploymentStrategy == null) {
			return null;
		}
		com.sap.engine.services.dc.cm.undeploy.UndeploymentStrategy ret = (com.sap.engine.services.dc.cm.undeploy.UndeploymentStrategy) undeploymentStrategies
				.get(undeploymentStrategy);
		if (ret == null) {
			throw new RuntimeException(
					"[ERROR CODE DPL.DCAPI.1148] Unknown undeployment strategy "
							+ undeploymentStrategy + " detected");
		}
		return ret;
	}

	public static com.sap.engine.services.dc.api.undeploy.UndeployItemStatus mapUndeployItemStatus(
			com.sap.engine.services.dc.cm.undeploy.UndeployItemStatus undeployItemStatus) {
		com.sap.engine.services.dc.api.undeploy.UndeployItemStatus ret = (com.sap.engine.services.dc.api.undeploy.UndeployItemStatus) undeployItemResult
				.get(undeployItemStatus);
		if (ret == null) {
			throw new RuntimeException(
					"[ERROR CODE DPL.DCAPI.1149] Unknown undeploy item status "
							+ undeployItemStatus + " detected");
		}
		return ret;
	}

	static com.sap.engine.services.dc.api.undeploy.UndeployResultStatus mapUndeployResultStatus(
			com.sap.engine.services.dc.cm.undeploy.UndeployResultStatus undeployResultStatus) {
		com.sap.engine.services.dc.api.undeploy.UndeployResultStatus ret = (com.sap.engine.services.dc.api.undeploy.UndeployResultStatus) undeployResultStatuses
				.get(undeployResultStatus);
		if (ret == null) {
			throw new RuntimeException(
					"[ERROR CODE DPL.DCAPI.1150] Unknown undeploy result status "
							+ undeployResultStatus + " detected");
		}
		return ret;
	}

	public static com.sap.engine.services.dc.cm.undeploy.UndeployWorkflowStrategy mapUndeployWorkflowStrategy(
			com.sap.engine.services.dc.api.undeploy.UndeployWorkflowStrategy workflowStrategy) {
		final com.sap.engine.services.dc.cm.undeploy.UndeployWorkflowStrategy ret = (com.sap.engine.services.dc.cm.undeploy.UndeployWorkflowStrategy) undeployWorkflowStrategies
				.get(workflowStrategy);

		if (ret == null) {
			throw new RuntimeException(
					"[ERROR CODE DPL.DCAPI.1151] Unknown undeploy workflow strategy "
							+ workflowStrategy + " detected");
		}

		return ret;
	}

}