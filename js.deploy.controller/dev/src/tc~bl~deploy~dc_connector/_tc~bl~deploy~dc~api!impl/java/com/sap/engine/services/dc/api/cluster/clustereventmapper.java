/*
 * Created on May 5, 2005
 */
package com.sap.engine.services.dc.api.cluster;

import java.util.HashMap;

import com.sap.engine.services.dc.api.event.ClusterEventAction;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-4-27
 * 
 * @author Boris Savov(i030791)
 * @author Todor Stoitsev
 * @version 1.0
 * @since 7.1
 * 
 */
public final class ClusterEventMapper {
	private static final HashMap clusterEventActions = new HashMap();
	static {
		clusterEventActions
				.put(
						com.sap.engine.services.dc.event.ClusterEventAction.CLUSTER_RESTART_TRIGGERED,
						com.sap.engine.services.dc.api.event.ClusterEventAction.CLUSTER_RESTART_TRIGGERED);
	}

	public static com.sap.engine.services.dc.api.event.ClusterEvent mapClusterEvent(
			com.sap.engine.services.dc.event.ClusterEvent dcClusterEvent) {

		if (dcClusterEvent == null) {
			throw new IllegalArgumentException(
					"[ERROR CODE DPL.DCAPI.1010] Remote Cluster Event cannot be null.");
		}

		com.sap.engine.services.dc.event.ClusterEventAction dcClusterEventAction = dcClusterEvent
				.getClusterEventAction();
		com.sap.engine.services.dc.api.event.ClusterEventAction daClusterEventAction = mapClusterEventAction(dcClusterEventAction);

		com.sap.engine.services.dc.api.event.ClusterEvent daClusterEvent = new com.sap.engine.services.dc.api.event.ClusterEvent(
				daClusterEventAction);
		return daClusterEvent;
	}

	public static com.sap.engine.services.dc.api.event.ClusterEventAction mapClusterEventAction(
			com.sap.engine.services.dc.event.ClusterEventAction dcClusterEventAction) {
		com.sap.engine.services.dc.api.event.ClusterEventAction ret = (ClusterEventAction) clusterEventActions
				.get(dcClusterEventAction);
		if (ret == null) {
			throw new RuntimeException(
					"[ERROR CODE DPL.DCAPI.1011] Unknown Cluster event "
							+ dcClusterEventAction + " detected");
		}
		return ret;
	}

}
