package com.sap.engine.services.dc.cm.undeploy.impl.sorters.impl;

import com.sap.engine.services.dc.repo.DependencyUndeploymentGraph;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-30
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class SortData {

	private final DependencyUndeploymentGraph acyclicItemsGraph;
	private final OnlineOfflineItemsMap onlineOfflineItemsMap;

	SortData(DependencyUndeploymentGraph acyclicItemsGraph,
			OnlineOfflineItemsMap onlineOfflineItemsMap) {
		this.acyclicItemsGraph = acyclicItemsGraph;
		this.onlineOfflineItemsMap = onlineOfflineItemsMap;
	}

	DependencyUndeploymentGraph getAcyclicItemsGraph() {
		return this.acyclicItemsGraph;
	}

	OnlineOfflineItemsMap getOnlineOfflineItemsMap() {
		return this.onlineOfflineItemsMap;
	}

}
