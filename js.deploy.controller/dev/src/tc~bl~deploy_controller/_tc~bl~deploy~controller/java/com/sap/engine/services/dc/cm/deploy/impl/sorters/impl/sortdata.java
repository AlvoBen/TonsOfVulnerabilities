package com.sap.engine.services.dc.cm.deploy.impl.sorters.impl;

import java.util.Map;

import com.sap.engine.services.dc.util.graph.DiGraph;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-28
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class SortData {

	private final DiGraph acyclicDeployItemGraph;
	private final DiGraph acyclicCompositeDeployItemGraph;
	private final OnlineOfflineItemsMap onlineOfflineItemsMap;
	private final Map itemToCompositeItemMap;

	SortData(DiGraph acyclicDeployItemGraph,
			DiGraph acyclicCompositeDeployItemGraph,
			OnlineOfflineItemsMap onlineOfflineItemsMap,
			Map itemToCompositeItemMap) {
		this.acyclicDeployItemGraph = acyclicDeployItemGraph;
		this.acyclicCompositeDeployItemGraph = acyclicCompositeDeployItemGraph;
		this.onlineOfflineItemsMap = onlineOfflineItemsMap;
		this.itemToCompositeItemMap = itemToCompositeItemMap;
	}

	public DiGraph getAcyclicDeployItemGraph() {
		return this.acyclicDeployItemGraph;
	}

	public DiGraph getAcyclicCompositeDeployItemGraph() {
		return this.acyclicCompositeDeployItemGraph;
	}

	public OnlineOfflineItemsMap getOnlineOfflineItemsMap() {
		return this.onlineOfflineItemsMap;
	}

	public Map getItemToCompositeItemMap() {
		return this.itemToCompositeItemMap;
	}

}
